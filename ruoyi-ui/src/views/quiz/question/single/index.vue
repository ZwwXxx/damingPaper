<template>
  <div style="padding: 20px">
    <el-row :gutter="15">
      <el-form ref="elForm" :model="formData" :rules="rules" size="medium" label-width="100px">
        <el-col :span="12">
          <el-form-item label="科目" prop="subjectId">
            <el-select v-model="formData.subjectId" placeholder="请输入科目" clearable :style="{width: '100%'}" @change="handleSubjectChange">
              <el-option v-for="(item, index) in subjectIdOptions" :key="index" :label="item.label"
                         :value="item.value" :disabled="item.disabled"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="题目类型" prop="questionType">
            <el-select v-model="formData.questionType"
                       placeholder="请选择题目类型"
                       clearable
                       :disabled="!allowTypeSwitch"
                       :style="{width: '100%'}"
                       @change="handleQuestionTypeChange">
              <el-option v-for="(item, index) in selectableQuestionTypes" :key="index" :label="item.label"
                         :value="item.value" :disabled="item.disabled"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-divider content-position="left">专项刷题归属（可选）</el-divider>
        </el-col>
        <el-col :span="12">
          <el-form-item label="一级分组">
            <el-select
              v-model="practiceGroupName"
              clearable
              filterable
              placeholder="选择章节/大类（可选）"
              :style="{width: '100%'}"
              :disabled="!formData.subjectId"
              @change="handlePracticeGroupChange"
            >
              <el-option
                v-for="(item, index) in practiceGroupOptions"
                :key="index"
                :label="item.groupName"
                :value="item.groupName"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="二级栏目">
            <el-select
              v-model="practiceColumnIds"
              multiple
              clearable
              filterable
              placeholder="选择专项栏目（可多选）"
              :style="{width: '100%'}"
              :disabled="!formData.subjectId"
            >
              <el-option
                v-for="col in practiceColumnOptions"
                :key="col.columnId"
                :label="col.columnName"
                :value="col.columnId"
              >
                <span style="float: left">{{ col.columnName }}</span>
                <span style="float: right; color: #909399; font-size: 12px">{{ col.groupName || '其他' }}</span>
              </el-option>
            </el-select>
            <div style="margin-top: 5px; color: #909399; font-size: 12px">
              💡 保存题目后会自动把题目绑定到所选栏目
            </div>
          </el-form-item>
        </el-col>
      <!-- 父题题干（完形父题和普通题复用） -->
      <el-col :span="24">
          <el-form-item label="题干" prop="questionTitle">
            <div style="margin-bottom: 10px;">
              <el-radio-group v-model="formData.questionTitleFormat" size="small" @change="handleQuestionTitleFormatChange">
                <el-radio-button label="html">富文本</el-radio-button>
                <el-radio-button label="markdown">Markdown</el-radio-button>
              </el-radio-group>
              <span style="margin-left: 10px; color: #909399; font-size: 12px;">
                {{ formData.questionTitleFormat === 'html' ? '💡 富文本编辑器，支持图片、格式等' : '💡 Markdown 格式，更简洁易读，适合代码和公式' }}
              </span>
            </div>
            <editor 
              v-if="formData.questionTitleFormat === 'html'"
              v-model="formData.questionTitle" 
              :min-height="200" 
              placeholder="请输入题干（支持图片、富文本）"
            />
            <markdown-editor
              v-else
              v-model="formData.questionTitle"
              :rows="12"
              placeholder="请输入题干（Markdown 格式）"
            />
          </el-form-item>
        </el-col>

        <!-- 普通题：选项编辑 -->
        <el-col :span="24" v-if="formData.questionType !== 3 && formData.questionType !== 5 && formData.questionType !== 6">
          <el-form-item label="选项：" required>
            <div style="margin-bottom: 10px;">
              <el-radio-group v-model="formData.optionFormat" size="small" @change="handleOptionFormatChange">
                <el-radio-button label="html">富文本</el-radio-button>
                <el-radio-button label="markdown">Markdown</el-radio-button>
              </el-radio-group>
              <span style="margin-left: 10px; color: #909399; font-size: 12px;">
                {{ formData.optionFormat === 'html' ? '💡 富文本编辑器，支持图片、格式等' : '💡 Markdown 格式，更简洁易读，适合代码和公式' }}
              </span>
            </div>
            <el-form-item :label="item.prefix" :key="item.prefix" v-for="(item,index) in formData.items" required
                          label-width="50px" style="margin: 10px 0 !important; ">
              <div style="display: flex; align-items: flex-start; width: 100%;">
                <div style="flex: 1; margin-right: 10px;">
                  <editor 
                    v-if="formData.optionFormat === 'html'"
                    v-model="item.content" 
                    :min-height="120" 
                    :placeholder="'请输入选项内容（支持图片、富文本）'"
                    style="width: 100%;"
                  />
                  <markdown-editor
                    v-else
                    v-model="item.content"
                    :rows="8"
                    :placeholder="'请输入选项内容（Markdown 格式）'"
                  />
                </div>
                <el-button type="danger" size="mini" icon="el-icon-delete"
                           @click="optionItemRemove(index)"></el-button>
              </div>
            </el-form-item>
          </el-form-item>
        </el-col>

        <!-- 完形填空：子题编辑区域 -->
        <el-col :span="24" v-if="formData.questionType === 6">
          <el-form-item label="完形子题列表">
            <div style="margin-bottom: 10px;">
              <el-button type="primary" size="mini" icon="el-icon-plus" @click="addClozeChild">添加子题</el-button>
            </div>
            <el-card
              v-for="(child, cIndex) in formData.clozeChildren"
              :key="cIndex"
              style="margin-bottom: 16px;"
            >
              <div slot="header" class="clearfix">
                <span>第 {{ cIndex + 1 }} 空子题</span>
                <el-button style="float: right; padding: 3px 0" type="text" @click="removeClozeChild(cIndex)">
                  删除
                </el-button>
              </div>
              <el-row :gutter="15">
                <el-col :span="12">
                  <el-form-item :label="'题型'" :prop="`clozeChildren.${cIndex}.questionType`">
                    <el-select v-model="child.questionType" placeholder="请选择题型" style="width: 100%;">
                      <el-option :value="1" label="单选题" />
                      <el-option :value="2" label="多选题" />
                      <el-option :value="4" label="判断题" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item :label="'分数'" :prop="`clozeChildren.${cIndex}.score`">
                    <el-input-number v-model="child.score" :min="1" />
                  </el-form-item>
                </el-col>
                <el-col :span="24">
                  <el-form-item :label="'子题题干'" :prop="`clozeChildren.${cIndex}.questionTitle`">
                    <editor
                      v-model="child.questionTitle"
                      :min-height="120"
                      placeholder="请输入子题题干"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="24">
                  <el-form-item label="选项">
                    <el-form-item
                      :label="item.prefix"
                      :key="index"
                      v-for="(item, index) in child.items"
                      label-width="50px"
                      style="margin: 10px 0 !important;"
                    >
                      <div style="display: flex; align-items: flex-start; width: 100%;">
                        <div style="flex: 1; margin-right: 10px;">
                          <editor
                            v-model="item.content"
                            :min-height="80"
                            placeholder="请输入选项内容"
                            style="width: 100%;"
                          />
                        </div>
                        <el-button type="danger" size="mini" icon="el-icon-delete"
                                   @click="removeClozeChildOption(cIndex, index)"></el-button>
                      </div>
                    </el-form-item>
                    <el-button type="primary" size="mini" icon="el-icon-plus" @click="addClozeChildOption(cIndex)">
                      添加选项
                    </el-button>
                  </el-form-item>
                </el-col>
                <el-col :span="24">
                  <el-form-item label="标准答案">
                    <el-radio-group
                      v-if="child.questionType === 1 || child.questionType === 4"
                      v-model="child.correct"
                    >
                      <el-radio v-for="item in child.items" :key="item.prefix" :label="item.prefix">
                        {{ item.prefix }}
                      </el-radio>
                    </el-radio-group>
                    <el-checkbox-group
                      v-if="child.questionType === 2"
                      v-model="child.correctArray"
                    >
                      <el-checkbox v-for="item in child.items" :key="item.prefix" :label="item.prefix">
                        {{ item.prefix }}
                      </el-checkbox>
                    </el-checkbox-group>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-card>
          </el-form-item>
        </el-col>

        <el-col :span="24" v-if="formData.questionType !== 6">
          <el-form-item label="标准答案"  :prop="formData.questionType === 2 ? 'correctArray' : (formData.questionType === 5 ? 'fillBlankAnswers' : 'correct')">
            <el-radio-group v-model="formData.correct" size="medium" @change="changeHandler"
                            v-if="[1,4].includes(formData.questionType)">
              <el-radio v-for="item in formData.items" :key="item.prefix" :label="item.prefix"
                        :disabled="item.disabled">{{ item.prefix }}
              </el-radio>
            </el-radio-group>
            <el-checkbox-group v-model="formData.correctArray" v-if="formData.questionType===2">
              <el-checkbox v-for="item in formData.items" :label="item.prefix" :key="item.prefix">{{ item.prefix }}
              </el-checkbox>
            </el-checkbox-group>
            <!-- 主观题标准答案：支持富文本和 Markdown -->
            <div v-if="formData.questionType===3">
              <div style="margin-bottom: 10px;">
                <el-radio-group v-model="formData.correctFormat" size="small" @change="handleCorrectFormatChange">
                  <el-radio-button label="html">富文本</el-radio-button>
                  <el-radio-button label="markdown">Markdown</el-radio-button>
                </el-radio-group>
                <span style="margin-left: 10px; color: #909399; font-size: 12px;">
                  {{ formData.correctFormat === 'html' ? '💡 富文本编辑器，支持图片、格式等' : '💡 Markdown 格式，更简洁易读，适合代码和公式' }}
                </span>
              </div>
              <editor 
                v-if="formData.correctFormat === 'html'"
                v-model="formData.correct" 
                :min-height="220" 
                placeholder="请输入主观题标准答案（支持图片、富文本）"
              />
              <markdown-editor
                v-if="formData.correctFormat === 'markdown'"
                v-model="formData.correct"
                :rows="10"
                placeholder="请输入主观题标准答案（Markdown 格式）"
              />
            </div>
            <!-- 填空题答案：动态添加多个输入框 -->
            <div v-if="formData.questionType===5">
              <el-form-item 
                :label="`第${index + 1}空`" 
                :key="index" 
                v-for="(answer, index) in formData.fillBlankAnswers" 
                label-width="80px"
                style="margin: 10px 0 !important;"
                :prop="`fillBlankAnswers.${index}.value`"
                :rules="[{ required: true, message: '答案不能为空', trigger: 'blur' }]">
                <div style="display: flex; align-items: flex-start; width: 100%;">
                  <el-input 
                    v-model="answer.value" 
                    :placeholder="`请输入第${index + 1}空答案`"
                    style="flex: 1; margin-right: 10px;"
                  />
                  <el-button 
                    type="danger" 
                    size="mini" 
                    icon="el-icon-delete"
                    @click="removeFillBlankAnswer(index)"
                    :disabled="formData.fillBlankAnswers.length <= 1"
                  ></el-button>
                </div>
              </el-form-item>
              <el-button 
                type="primary" 
                size="small" 
                icon="el-icon-plus"
                @click="addFillBlankAnswer"
                style="margin-top: 10px;"
              >添加答案</el-button>
              <el-form-item label="答案顺序" style="margin-top: 15px;">
                <el-switch
                  v-model="formData.requireOrder"
                  active-text="要求按顺序"
                  inactive-text="不要求按顺序"
                  :active-value="true"
                  :inactive-value="false"
                />
                <div style="margin-top: 5px; color: #909399; font-size: 12px;">
                  💡 {{ formData.requireOrder ? '答案必须按照标准答案的顺序填写' : '答案可以不按顺序填写，系统会自动匹配' }}
                </div>
              </el-form-item>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="解析" prop="analysis">
            <div style="margin-bottom: 10px;">
              <el-radio-group v-model="formData.analysisFormat" size="small" @change="handleAnalysisFormatChange">
                <el-radio-button label="html">富文本</el-radio-button>
                <el-radio-button label="markdown">Markdown</el-radio-button>
              </el-radio-group>
              <span style="margin-left: 10px; color: #909399; font-size: 12px;">
                {{ formData.analysisFormat === 'html' ? '💡 富文本编辑器，支持图片、格式等' : '💡 Markdown 格式，更简洁易读，适合代码和公式' }}
              </span>
            </div>
            <editor 
              v-if="formData.analysisFormat === 'html'"
              v-model="formData.analysis" 
              :min-height="220" 
              placeholder="请输入解析（支持图片、富文本）"
            />
            <markdown-editor
              v-else
              v-model="formData.analysis"
              :rows="15"
              placeholder="请输入解析（Markdown 格式）"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="动画解析（选填）">
            <div style="display: flex; flex-direction: column; gap: 10px;">
              <div style="display: flex; align-items: center; flex-wrap: wrap; gap: 10px;">
                <el-select
                  v-model="formData.animationId"
                  filterable
                  clearable
                  placeholder="从动画库选择（按当前科目筛选）"
                  style="width: 360px"
                  @visible-change="handleAnimationDropdown"
                  @change="handleAnimationSelect"
                >
                  <el-option
                    v-for="item in animationOptions"
                    :key="item.animationId"
                    :label="`[${item.animationId}] ${item.animationName || '未命名'}`"
                    :value="item.animationId"
                  />
                </el-select>
                <el-link v-if="formData.animationUrl" :href="formData.animationUrl" target="_blank" :underline="false">预览动画</el-link>
                <el-button v-if="formData.animationId" size="mini" type="danger" plain @click="clearAnimation">清除关联</el-button>
              </div>
              <el-upload
                :action="animationUpload.url"
                :headers="animationUpload.headers"
                :data="animationUpload.extraData"
                :limit="1"
                :file-list="animationFileList"
                :on-success="handleAnimationUploadSuccess"
                :before-upload="beforeAnimationUpload"
                :on-remove="handleAnimationRemove"
                accept=".html,.htm"
              >
                <el-button size="mini" type="primary">上传HTML动画</el-button>
                <div slot="tip" class="el-upload__tip">
                  选填：仅支持 <b>.html/.htm</b>，上传成功后会写入动画库，并按当前题目科目自动分类
                </div>
              </el-upload>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="分数" prop="score">
            <el-input-number v-model="formData.score" placeholder="分数"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="关联知识点">
            <el-select
              v-model="formData.knowledgePointIds"
              multiple
              filterable
              remote
              placeholder="请输入关键词搜索知识点"
              :remote-method="searchKnowledgePoints"
              :loading="knowledgeLoading"
              @change="handleKnowledgePointChange"
              style="width: 100%">
              <el-option
                v-for="item in knowledgePointOptions"
                :key="item.pointId"
                :label="item.title"
                :value="item.pointId">
                <span style="float: left">{{ item.title }}</span>
                <span style="float: right; color: #8492a6; font-size: 12px">
                  难度: {{ ['简单', '中等', '困难'][item.difficulty - 1] || '未知' }}
                </span>
              </el-option>
            </el-select>
            <div style="margin-top: 5px; color: #909399; font-size: 12px">
              💡 提示：可关联1-3个知识点，帮助学生错题后精准学习
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item size="large">
            <el-button type="primary" @click="submitForm">提交</el-button>
            <el-button @click="resetForm">重置</el-button>
            <el-button
              v-if="formData.questionType !== 3 && formData.questionType !== 5"
              type="warning"
              @click="addNewOption"
            >添加选项</el-button>
          </el-form-item>
        </el-col>
      </el-form>
    </el-row>
  </div>
</template>
<script>
import { addQuestion, addClozeQuestion, getQuestion, getClozeQuestion, updateQuestion, bindKnowledgePoints, getQuestionKnowledgePoints, listKnowledgePoints, getQuestionPracticeColumns, bindQuestionPracticeColumns } from "@/api/quiz/question";
import { optionSubject } from "@/api/quiz/subject";
import { listAnimation, getAnimation } from "@/api/quiz/animation";
import { listPracticeGroupOptions, listPracticeColumnOptions } from "@/api/quiz/practiceColumn";
import Editor from "@/components/Editor";
import MarkdownEditor from "@/components/MarkdownEditor";
import { getToken } from "@/utils/auth";

const defaultChoiceOptions = () => ([
  { prefix: 'A', content: '' },
  { prefix: 'B', content: '' },
  { prefix: 'C', content: '' },
  { prefix: 'D', content: '' }
]);

const defaultJudgeOptions = () => ([
  { prefix: 'A', content: '正确' },
  { prefix: 'B', content: '错误' }
]);

// 本页面草稿本地存储 key（按路由区分）
const LOCAL_DRAFT_KEY = 'dm_question_single_draft';
const LOCAL_DRAFT_VERSION = 1;

export default {
  name: 'QuestionEditor',
  components: { Editor, MarkdownEditor },
  props: {
    presetQuestionType: {
      type: Number,
      default: 1
    },
    allowTypeSwitch: {
      type: Boolean,
      default: true
    },
    questionTypeOptionsOverride: {
      type: Array,
      default: null
    }
  },
  data() {
    const initialType = this.presetQuestionType;
    return {
      formData: {
        id: undefined,
        subjectId: undefined,
        questionType: initialType,
        questionTitle: undefined,
        animationId: null,
        animationUrl: '',
        correct: undefined,
        correctArray: [],
        fillBlankAnswers: [{ value: '' }], // 填空题答案数组
        requireOrder: false, // 填空题答案是否要求按顺序
        questionTitle: undefined,
        questionTitleFormat: 'html', // 题干内容格式：html或markdown
        optionFormat: 'html', // 选项内容格式：html或markdown
        correctFormat: 'html', // 标准答案内容格式：html或markdown（仅主观题）
        analysis: '无',
        analysisFormat: 'html', // 解析内容格式：html或markdown
        score: 1,
        knowledgePointIds: [],
        items: initialType === 3
            ? []
            : initialType === 4
                ? defaultJudgeOptions()
                : defaultChoiceOptions(),
        // 完形填空子题列表（仅在questionType=6时使用）
        clozeChildren: []
      },
      animationUpload: {
        url: process.env.VUE_APP_BASE_API + "/quiz/animation/upload",
        headers: { Authorization: "Bearer " + getToken() },
        extraData: { subjectId: undefined }
      },
      animationFileList: [],
      animationOptions: [],
      knowledgePointOptions: [],
      knowledgeLoading: false,
      rules: {
        subjectId: [{
          required: true,
          message: '请输入科目',
          trigger: 'change'
        }],
        questionType: [{
          required: true,
          message: '请选择题目类型',
          trigger: 'change'
        }],
        questionTitle: [{
          required: true,
          message: '请输入题干',
          trigger: 'blur'
        }],
        correct: [{
          required: true,
          message: '标准答案不能为空',
          trigger: 'change'
        }],
        analysis: [{
          required: true,
          message: '请输入解析',
          trigger: 'change'
        }],
        score: [{
          required: true,
          message: '分数',
          trigger: 'blur'
        }],
      },
      subjectIdOptions: [],
      practiceGroupOptions: [],
      practiceGroupName: '',
      practiceColumnOptions: [],
      practiceColumnIds: [],
      questionTypeOptions: [{
        "label": "单选题",
        "value": 1
      }, {
        "label": "多选题",
        "value": 2
      }, {
        "label": "主观题",
        "value": 3
      }, {
        "label": "判断题",
        "value": 4
      }, {
        "label": "填空题",
        "value": 5
      }, {
        "label": "完形填空题",
        "value": 6
      }],
      // 完形填空父子关系（单题编辑时使用）
      parentIdVisible: false,
      clozeIndexVisible: false,
      // 本地草稿保存节流定时器
      draftSaveTimer: null,
    }
  },
  computed: {
    selectableQuestionTypes() {
      if (Array.isArray(this.questionTypeOptionsOverride) && this.questionTypeOptionsOverride.length) {
        return this.questionTypeOptionsOverride;
      }
      return this.questionTypeOptions;
    }
  },
  watch: {},
  async created() {
    await this.loadSubjectOptions();
    await this.loadPracticeOptions();
    // 初始化动画上传分类（科目）
    this.animationUpload.extraData.subjectId = this.formData.subjectId;
    const id = this.$route.query.id;
    if (id) {
      // 编辑模式：不使用本地草稿，直接从后端加载
      this.getData(id);
    } else {
      // 新增模式：尝试恢复草稿
      this.loadDraft();
      // 监听表单变化，自动保存草稿（深度监听）
      this.$watch('formData', this.scheduleSaveDraft, { deep: true });
      this.$watch('practiceGroupName', this.scheduleSaveDraft);
      this.$watch('practiceColumnIds', this.scheduleSaveDraft, { deep: true });
    }
  },
  methods: {
    // ========== 本地草稿存取 ==========
    scheduleSaveDraft() {
      if (this.$route.query.id) return; // 编辑模式不保存草稿
      if (this.draftSaveTimer) {
        clearTimeout(this.draftSaveTimer);
      }
      this.draftSaveTimer = setTimeout(() => {
        this.saveDraftNow();
      }, 500);
    },
    saveDraftNow() {
      if (this.$route.query.id) return;
      try {
        const payload = {
          v: LOCAL_DRAFT_VERSION,
          formData: this.formData,
          practiceGroupName: this.practiceGroupName,
          practiceColumnIds: this.practiceColumnIds
        };
        window.localStorage.setItem(LOCAL_DRAFT_KEY, JSON.stringify(payload));
      } catch (e) {
        // 本地存储失败忽略即可
      }
    },
    async loadDraft() {
      try {
        const raw = window.localStorage.getItem(LOCAL_DRAFT_KEY);
        if (!raw) return;
        const data = JSON.parse(raw);
        if (!data || data.v !== LOCAL_DRAFT_VERSION) return;
        if (data.formData && !this.formData.id) {
          this.formData = {
            ...this.formData,
            ...data.formData,
            items: Array.isArray(data.formData.items) && data.formData.items.length
              ? data.formData.items
              : this.formData.items,
            clozeChildren: Array.isArray(data.formData.clozeChildren)
              ? data.formData.clozeChildren
              : this.formData.clozeChildren,
            fillBlankAnswers: Array.isArray(data.formData.fillBlankAnswers) && data.formData.fillBlankAnswers.length
              ? data.formData.fillBlankAnswers
              : this.formData.fillBlankAnswers,
          };
        }
        if (typeof data.practiceGroupName === 'string') {
          this.practiceGroupName = data.practiceGroupName;
        }
        if (Array.isArray(data.practiceColumnIds)) {
          this.practiceColumnIds = data.practiceColumnIds;
        }
        // 草稿里如果带了科目，刷新完后要主动加载一次专栏选项，
        // 否则 el-select 会只显示数值 ID（例如 8），而不是栏目中文名
        if (this.formData.subjectId) {
          await this.loadPracticeOptions();
          // 若已选择了一级分组，则按分组再过滤一次二级栏目列表
          if (this.practiceGroupName) {
            await this.handlePracticeGroupChange(this.practiceGroupName);
          }
        }
      } catch (e) {
        // 解析失败直接忽略
      }
    },
    clearDraft() {
      try {
        window.localStorage.removeItem(LOCAL_DRAFT_KEY);
      } catch (e) {
        // ignore
      }
    },
    async loadPracticeOptions() {
      // subjectId 未选时不加载
      if (!this.formData.subjectId) {
        this.practiceGroupOptions = [];
        this.practiceColumnOptions = [];
        return;
      }
      const [gRes, cRes] = await Promise.all([
        listPracticeGroupOptions({ subjectId: this.formData.subjectId }),
        listPracticeColumnOptions({ subjectId: this.formData.subjectId })
      ]);
      this.practiceGroupOptions = (gRes && gRes.data) || [];
      this.practiceColumnOptions = (cRes && cRes.data) || [];
    },
    async handlePracticeGroupChange(val) {
      // 按一级分组过滤二级栏目选项；清空已选不在范围内的栏目
      if (!this.formData.subjectId) return;
      const params = { subjectId: this.formData.subjectId };
      if (val) params.groupName = val;
      const res = await listPracticeColumnOptions(params);
      this.practiceColumnOptions = (res && res.data) || [];
      const allowed = new Set(this.practiceColumnOptions.map(x => x.columnId));
      this.practiceColumnIds = (this.practiceColumnIds || []).filter(id => allowed.has(id));
    },
    formatValidateKeyToLabel(key) {
      const map = {
        subjectId: '科目',
        questionType: '题目类型',
        questionTitle: '题干',
        correct: '标准答案',
        correctArray: '标准答案',
        analysis: '解析',
        score: '分数',
        knowledgePointIds: '关联知识点'
      };
      if (!key) return '未填写项';
      if (map[key]) return map[key];
      // 填空题：fillBlankAnswers.0.value
      const fb = String(key).match(/^fillBlankAnswers\.(\d+)\.value$/);
      if (fb) return `第${Number(fb[1]) + 1}空答案`;
      // 完形子题（若后续补齐 rules 会用到）
      const clozeTitle = String(key).match(/^clozeChildren\.(\d+)\.questionTitle$/);
      if (clozeTitle) return `完形第${Number(clozeTitle[1]) + 1}空子题题干`;
      const clozeScore = String(key).match(/^clozeChildren\.(\d+)\.score$/);
      if (clozeScore) return `完形第${Number(clozeScore[1]) + 1}空子题分数`;
      const clozeType = String(key).match(/^clozeChildren\.(\d+)\.questionType$/);
      if (clozeType) return `完形第${Number(clozeType[1]) + 1}空子题题型`;
      return key;
    },
    showValidatePopup(fields) {
      const keys = Object.keys(fields || {});
      const labels = keys.map(k => this.formatValidateKeyToLabel(k));
      const uniq = Array.from(new Set(labels));
      const html = `
        <div style="line-height: 1.8;">
          <div style="margin-bottom: 8px;">请先完善以下必填项后再提交：</div>
          <ul style="padding-left: 18px; margin: 0;">
            ${uniq.map(x => `<li>${x}</li>`).join('')}
          </ul>
        </div>
      `;
      this.$alert(html, '表单未填写完整', {
        type: 'warning',
        dangerouslyUseHTMLString: true,
        confirmButtonText: '我知道了'
      });
      // 滚动到第一个错误项（避免不在当前视口察觉不到）
      this.$nextTick(() => {
        const first = this.$el && this.$el.querySelector && this.$el.querySelector('.el-form-item.is-error');
        if (first && first.scrollIntoView) {
          first.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
      });
    },
    async loadSubjectOptions() {
      const res = await optionSubject();
      const list = res.data || [];
      this.subjectIdOptions = list.map(item => ({
        label: item.subjectName,
        value: item.subjectId
      }));
    },
    changeHandler(value) {
      console.log('改变之后的值是:' + value)
    },
    handleQuestionTypeChange(value) {
      this.applyQuestionTypeDefaults(value);
    },
    async handleSubjectChange(value) {
      // 科目变化时：更新动画上传分类，并清空当前动画关联（避免跨科目串数据）
      this.animationUpload.extraData.subjectId = value;
      this.clearAnimation();
      this.animationOptions = [];
      // 专项刷题归属：随科目变化刷新
      this.practiceGroupName = '';
      this.practiceColumnIds = [];
      await this.loadPracticeOptions();
    },
    applyQuestionTypeDefaults(questionType, options = {}) {
      const { preserveAnswer = false } = options;
      if (questionType === 3) {
        this.formData.items = [];
        if (!preserveAnswer) {
          this.formData.correct = '';
        }
        this.formData.correctArray = [];
        this.formData.fillBlankAnswers = [];
        return;
      }
      if (questionType === 5) {
        this.formData.items = [];
        if (!preserveAnswer) {
          this.formData.fillBlankAnswers = [{ value: '' }];
        }
        this.formData.correctArray = [];
        return;
      }
      if (questionType === 4) {
        this.formData.items = defaultJudgeOptions();
        this.formData.correctArray = [];
        if (!preserveAnswer || !['A', 'B'].includes(this.formData.correct)) {
          this.formData.correct = 'A';
        }
        return;
      }
      if (!this.formData.items.length) {
        this.formData.items = defaultChoiceOptions();
      }
      if (questionType === 2) {
        if (!preserveAnswer) {
          this.formData.correct = '';
          this.formData.correctArray = [];
        } else {
          this.formData.correct = this.formData.correct || '';
          this.formData.correctArray = this.formData.correctArray || [];
        }
      } else {
        if (!preserveAnswer) {
          this.formData.correctArray = [];
        } else {
          this.formData.correctArray = this.formData.correctArray || [];
        }
      }
    },
    async getData(id) {
      this.loading = true;
      try {
        const response = await getQuestion(id);
        // 处理填空题答案：如果是填空题，将逗号分隔的字符串转换为数组
        let fillBlankAnswers = [{ value: '' }];
        let requireOrder = false;
        if (response.data.questionType === 5 && response.data.correct) {
          const answers = response.data.correct.split(',').map(a => a.trim()).filter(a => a);
          fillBlankAnswers = answers.length > 0 ? answers.map(a => ({ value: a })) : [{ value: '' }];
          // 读取答案顺序要求
          requireOrder = response.data.requireOrder !== undefined ? response.data.requireOrder : false;
        }
        
        // 读取解析格式（默认html）
        const analysisFormat = response.data.analysisFormat || 'html';
        // 读取题干格式（默认html）
        const questionTitleFormat = response.data.questionTitleFormat || 'html';
        // 读取选项格式（默认html）
        const optionFormat = response.data.optionFormat || 'html';
        // 读取标准答案格式（默认html，仅主观题）
        const correctFormat = response.data.correctFormat || 'html';
        
        this.formData = {
          ...response.data,
          correctArray: response.data.correctArray || [],
          items: response.data.items || [],
          knowledgePointIds: [],
          fillBlankAnswers: fillBlankAnswers,
          requireOrder: requireOrder,
          questionTitleFormat: questionTitleFormat,
          optionFormat: optionFormat,
          correctFormat: correctFormat,
          analysisFormat: analysisFormat,
          // 默认无完形子题，后续如为完形再单独加载
          clozeChildren: []
        };
        // 上传动画时带上科目分类
        this.animationUpload.extraData.subjectId = this.formData.subjectId;
        // 动画解析回显（可选）
        if (this.formData.animationId && this.formData.animationUrl) {
          this.animationFileList = [{ name: this.formData.animationUrl.split('/').pop() || 'animation.html', url: this.formData.animationUrl }];
        } else {
          this.animationFileList = [];
        }
        this.applyQuestionTypeDefaults(this.formData.questionType, { preserveAnswer: true });

        // 编辑时先加载专项选项（一级分组、二级栏目），否则下拉会显示“无数据”
        if (this.formData.subjectId) {
          await this.loadPracticeOptions();
        }
        // 回显：专项栏目绑定
        const pcRes = await getQuestionPracticeColumns(id);
        this.practiceColumnIds = (pcRes && pcRes.data) ? pcRes.data : [];
        // 回显：根据栏目反推一级分组（若混选多个分组，则不强行选中）
        if (Array.isArray(this.practiceColumnIds) && this.practiceColumnIds.length && (this.practiceColumnOptions || []).length) {
          const picked = this.practiceColumnOptions.filter(c => this.practiceColumnIds.includes(c.columnId));
          const uniqGroups = Array.from(new Set(picked.map(x => (x.groupName || '').trim()).filter(Boolean)));
          this.practiceGroupName = uniqGroups.length === 1 ? uniqGroups[0] : '';
        }

        // 如果是完形填空父题，额外加载子题列表
        if (this.formData.questionType === 6) {
          const clozeRes = await getClozeQuestion(id);
          if (clozeRes.code === 200 && clozeRes.data) {
            const children = (clozeRes.data.children || []).map(child => ({
              questionType: child.questionType,
              questionTitle: child.questionTitle,
              score: child.score,
              items: child.items || [],
              correct: child.correct,
              correctArray: child.correctArray || []
            }));
            this.formData.clozeChildren = children;
          }
        }
        
        const kpResponse = await getQuestionKnowledgePoints(id);
        if (kpResponse.code === 200 && kpResponse.data) {
          this.formData.knowledgePointIds = kpResponse.data.map(kp => kp.pointId);
          this.knowledgePointOptions = kpResponse.data;
        }
      } finally {
        this.loading = false;
      }
    },
    async searchKnowledgePoints(query) {
      if (!query) {
        this.knowledgePointOptions = [];
        return;
      }
      this.knowledgeLoading = true;
      try {
        const response = await listKnowledgePoints({ title: query, pageSize: 20 });
        if (response.code === 200) {
          this.knowledgePointOptions = response.rows || [];
        }
      } finally {
        this.knowledgeLoading = false;
      }
    },
    handleKnowledgePointChange() {
      // 选中后只保留已选中的知识点选项，清空搜索结果
      this.$nextTick(() => {
        const selectedIds = this.formData.knowledgePointIds || [];
        this.knowledgePointOptions = this.knowledgePointOptions.filter(
          item => selectedIds.includes(item.pointId)
        );
      });
    },
    addClozeChild() {
      if (!Array.isArray(this.formData.clozeChildren)) {
        this.formData.clozeChildren = [];
      }
      this.formData.clozeChildren.push({
        questionType: 1,
        questionTitle: '',
        score: 1,
        items: defaultChoiceOptions(),
        correct: '',
        correctArray: []
      });
    },
    removeClozeChild(index) {
      if (!Array.isArray(this.formData.clozeChildren)) return;
      this.formData.clozeChildren.splice(index, 1);
    },
    addClozeChildOption(childIndex) {
      const children = this.formData.clozeChildren || [];
      const child = children[childIndex];
      if (!child) return;
      if (!Array.isArray(child.items)) {
        this.$set(child, 'items', []);
      }
      child.items.push({ prefix: String.fromCharCode(65 + child.items.length), content: '' });
    },
    removeClozeChildOption(childIndex, optionIndex) {
      const children = this.formData.clozeChildren || [];
      const child = children[childIndex];
      if (!child || !Array.isArray(child.items)) return;
      child.items.splice(optionIndex, 1);
    },
    async submitForm() {
      // 统一在提交时弹窗提示，不在输入框下方显示红字（页面样式里已隐藏错误文案）
      const valid = await new Promise((resolve) => {
        this.$refs['elForm'].validate((ok, fields) => {
          if (!ok) {
            this.showValidatePopup(fields);
          }
          resolve(ok);
        });
      });
      if (!valid) return;
      const type = this.formData.questionType;
      // 完形填空：走单独的批量保存接口
      if (type === 6) {
        const children = (this.formData.clozeChildren || []).map((child, index) => {
          return {
            questionType: child.questionType,
            questionTitle: child.questionTitle,
            score: child.score,
            items: child.items,
            correct: child.correct,
            correctArray: child.correctArray,
            itemOrder: index
          };
        });
        const payload = {
          parent: {
            subjectId: this.formData.subjectId,
            questionType: 6,
            questionTitle: this.formData.questionTitle,
            analysis: this.formData.analysis,
            score: this.formData.score
          },
          children: children
        };
        const res = await addClozeQuestion(payload);
        const message = res.code === 200 ? "完形填空保存成功!" : "保存失败,请联系管理员!"
        const typeName = res.code === 200 ? "success" : "error"
        this.$message({ message, type: typeName });
        // 新增成功后重置输入：只保留“科目 + 题型”，其余回到默认值（与其他题型保持一致）
        if (res.code === 200) {
          const keepSubjectId = this.formData.subjectId;
          const keepQuestionType = this.formData.questionType; // 6

          // 先按表单初始值整体重置，再恢复科目/题型
          this.$refs['elForm'] && this.$refs['elForm'].resetFields();

          // resetFields 不会处理这些“表单外状态”，这里一并清空
          this.animationFileList = [];
          this.knowledgePointOptions = [];

          this.formData.subjectId = keepSubjectId;
          this.formData.questionType = keepQuestionType;
          // 完形题型下的一些默认结构（目前主要是子题列表）
          this.applyQuestionTypeDefaults(keepQuestionType);
          this.formData.clozeChildren = [];

          this.$nextTick(() => {
            this.$refs['elForm'] && this.$refs['elForm'].clearValidate();
          });
        }
        return;
      }
      if (type === 2) {
        this.formData.correct = '';
      } else {
        this.formData.correctArray = [];
      }
      // 处理填空题答案：将数组转换为逗号分隔的字符串
      if (type === 5) {
        const answers = this.formData.fillBlankAnswers
          .map(a => a.value ? a.value.trim() : '')
          .filter(a => a);
        this.formData.correct = answers.join(',');
        this.formData.items = [];
      } else if (type === 3) {
        this.formData.items = [];
      } else if (type === 4 && this.formData.items.length === 0) {
        this.formData.items = defaultJudgeOptions();
      } else if ([1, 2].includes(type) && this.formData.items.length === 0) {
        this.formData.items = defaultChoiceOptions();
      }
      let res = null;
      if (this.formData.id !== undefined) {
        res = await updateQuestion(this.formData);
      } else {
        res = await addQuestion(this.formData);
      }
      const message = res.code === 200 ? "操作成功!" : "操作失败,请联系管理员!"
      const typeName = res.code === 200 ? "success" : "error"
      this.$message({
        message,
        type: typeName
      });

      if (res.code === 200) {
        const questionId = this.formData.id || res.data;
        // 绑定专项栏目（可选）
        await bindQuestionPracticeColumns(questionId, this.practiceColumnIds || []);
        if (this.formData.knowledgePointIds && this.formData.knowledgePointIds.length > 0) {
          await bindKnowledgePoints({
            questionId: questionId,
            knowledgePointIds: this.formData.knowledgePointIds,
            relationType: 1
          });
        }
        
        if (this.formData.id === undefined) {
          // 新增成功后，保留科目和题型，只清空题干、选项、答案、解析
          const keepSubjectId = this.formData.subjectId;
          const keepQuestionType = this.formData.questionType;
          const keepScore = this.formData.score;
          
          // 清空题干和解析
          this.formData.questionTitle = '';
          this.formData.analysis = '无';
          
          // 根据题型清空答案和选项
          if (keepQuestionType === 1) {
            // 单选题：保留选项结构，清空内容和答案
            this.formData.items = defaultChoiceOptions();
            this.formData.correct = '';
            this.formData.correctArray = [];
          } else if (keepQuestionType === 2) {
            // 多选题：保留选项结构，清空内容和答案
            this.formData.items = defaultChoiceOptions();
            this.formData.correct = '';
            this.formData.correctArray = [];
          } else if (keepQuestionType === 3) {
            // 主观题：清空答案
            this.formData.items = [];
            this.formData.correct = '';
            this.formData.correctArray = [];
          } else if (keepQuestionType === 5) {
            // 填空题：清空答案数组，保留顺序设置
            this.formData.items = [];
            this.formData.fillBlankAnswers = [{ value: '' }];
            this.formData.correctArray = [];
            // 不清空requireOrder，保持用户设置
          } else if (keepQuestionType === 4) {
            // 判断题：保留正确/错误选项，清空答案
            this.formData.items = defaultJudgeOptions();
            this.formData.correct = '';
            this.formData.correctArray = [];
          }
          
          // 保留科目、题型和分数
          this.formData.subjectId = keepSubjectId;
          this.formData.questionType = keepQuestionType;
          this.formData.score = keepScore;
          
          // 清除表单验证状态，避免显示红色提示信息
          this.$nextTick(() => {
            this.$refs['elForm'].clearValidate();
          });
          // 新增成功后，清空本地草稿
          this.clearDraft();
        }
      }

    },

    async handleAnimationDropdown(visible) {
      if (!visible) return;
      // 下拉展开时加载：按当前题目科目筛选
      const subjectId = this.formData.subjectId;
      const res = await listAnimation({ pageNum: 1, pageSize: 200, subjectId: subjectId });
      if (res && res.code === 200) {
        this.animationOptions = res.rows || [];
      }
    },

    async handleAnimationSelect(animationId) {
      if (!animationId) {
        this.formData.animationUrl = '';
        this.animationFileList = [];
        return;
      }
      const res = await getAnimation(animationId);
      if (res && res.code === 200 && res.data) {
        this.formData.animationUrl = res.data.animationUrl || '';
        if (this.formData.animationUrl) {
          this.animationFileList = [{ name: res.data.animationName || 'animation.html', url: this.formData.animationUrl }];
        } else {
          this.animationFileList = [];
        }
      }
    },
    resetForm() {
      const keepSubjectId = this.formData.subjectId;
      const keepType = this.presetQuestionType;
      this.$refs['elForm'].resetFields();
      this.formData.subjectId = keepSubjectId;
      this.formData.questionType = keepType;
      this.applyQuestionTypeDefaults(keepType);
      // 手动重置时，同步更新草稿
      this.scheduleSaveDraft();
    },
    optionItemRemove(index) {
      this.formData.items.splice(index, 1)
    },
    addNewOption() {
      let oldOption = this.formData.items
      let newPrefix
      if (oldOption.length > 0) {
        let lastOption = oldOption[oldOption.length - 1]
        newPrefix = String.fromCharCode(lastOption.prefix.charCodeAt() + 1)
      } else {
        newPrefix = "A"
      }
      oldOption.push({id: null, prefix: newPrefix, content: ''})
    },
    // 添加填空题答案
    addFillBlankAnswer() {
      this.formData.fillBlankAnswers.push({ value: '' });
    },
    // 删除填空题答案
    removeFillBlankAnswer(index) {
      if (this.formData.fillBlankAnswers.length > 1) {
        this.formData.fillBlankAnswers.splice(index, 1);
      }
    },
    // 处理解析格式切换
    handleCorrectFormatChange(format) {
      // 格式切换时，如果从html切换到markdown，可以尝试将HTML转换为Markdown（简单处理）
      // 这里不做自动转换，让用户手动编辑
      if (format === 'markdown' && this.formData.correct) {
        // 可以在这里添加格式转换逻辑，但为了安全，暂时不做自动转换
        console.log('切换到 Markdown 格式，请手动编辑内容');
      }
    },
    handleAnalysisFormatChange(format) {
      // 格式切换时，如果从html切换到markdown，可以尝试将HTML转换为Markdown（简单处理）
      // 这里不做自动转换，让用户手动编辑
      if (format === 'markdown' && this.formData.analysis) {
        // 可以在这里添加HTML到Markdown的转换逻辑（可选）
        // 暂时保留原内容，让用户手动编辑
      }
    },
    // 处理题干格式切换
    handleQuestionTitleFormatChange(format) {
      // 格式切换时，如果从html切换到markdown，可以尝试将HTML转换为Markdown（简单处理）
      // 这里不做自动转换，让用户手动编辑
      if (format === 'markdown' && this.formData.questionTitle) {
        // 可以在这里添加HTML到Markdown的转换逻辑（可选）
        // 暂时保留原内容，让用户手动编辑
      }
    },
    // 处理选项格式切换
    handleOptionFormatChange(format) {
      // 格式切换时，如果从html切换到markdown，可以尝试将HTML转换为Markdown（简单处理）
      // 这里不做自动转换，让用户手动编辑
      if (format === 'markdown' && this.formData.items && this.formData.items.length > 0) {
        // 可以在这里添加HTML到Markdown的转换逻辑（可选）
        // 暂时保留原内容，让用户手动编辑
      }
    },

    beforeAnimationUpload(file) {
      const ext = (file.name || '').split('.').pop();
      const ok = ['html', 'htm'].includes(String(ext || '').toLowerCase());
      if (!ok) {
        this.$message.error('只允许上传 .html/.htm 文件');
        return false;
      }
      const maxSize = 5; // MB
      const sizeOk = file.size / 1024 / 1024 <= maxSize;
      if (!sizeOk) {
        this.$message.error('文件大小不能超过5MB');
        return false;
      }
      return true;
    },

    handleAnimationUploadSuccess(res, file) {
      if (res && res.code === 200) {
        const data = res.data || {};
        this.formData.animationId = data.animationId || null;
        this.formData.animationUrl = data.url || '';
        this.animationFileList = [{ name: data.animationName || file.name, url: this.formData.animationUrl }];
        // 上传成功后把新动画追加到下拉选项，便于立即复用
        if (this.formData.animationId) {
          const exists = (this.animationOptions || []).some(x => x.animationId === this.formData.animationId);
          if (!exists) {
            this.animationOptions = [{ animationId: this.formData.animationId, animationName: data.animationName, animationUrl: data.url }].concat(this.animationOptions || []);
          }
        }
        this.$message.success('动画上传成功，已关联到题目');
      } else {
        this.$message.error((res && res.msg) ? res.msg : '动画上传失败');
      }
    },

    handleAnimationRemove() {
      // 只移除题目与动画的关联，不删除动画库记录
      this.formData.animationId = null;
      this.formData.animationUrl = '';
      this.animationFileList = [];
    },

    clearAnimation() {
      this.handleAnimationRemove();
    },

  }
}

</script>
<style scoped>
/* 该页面采用“提交时统一弹窗提示”，隐藏每个输入框下方的红色校验文案 */
::v-deep .el-form-item__error {
  display: none !important;
}
</style>
