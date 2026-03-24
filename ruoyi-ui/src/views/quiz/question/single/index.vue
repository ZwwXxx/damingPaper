<template>
  <div style="padding: 20px">
    <el-row :gutter="15">
      <el-form ref="elForm" :model="formData" :rules="rules" size="medium" label-width="100px">
        <el-col :span="12" v-if="showOriginOrder">
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
        <el-col :span="12">
          <el-form-item label="是否真题">
            <el-switch
              v-model="formData.isRealQuestion"
              active-text="真题"
              inactive-text="非真题"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="showOriginOrder && formData.isRealQuestion">
          <el-form-item label="原卷题号">
            <el-input-number
              v-model="formData.originOrder"
              :min="1"
              :max="9999"
              controls-position="right"
              style="width: 180px;"
              placeholder="不填则不参与原卷排序"
            />
            <div v-if="formData.questionType === 6" style="margin-top: 5px; color: #909399; font-size: 12px">
              💡 完形/复合题父题不编号时，这里填“首个子题的原卷题号”
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="formData.isRealQuestion">
          <el-form-item label="题目年份">
            <el-input-number
              v-model="formData.examYear"
              :min="2000"
              :max="2100"
              controls-position="right"
              style="width: 180px;"
              placeholder="例如 2020"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="formData.isRealQuestion">
          <el-form-item label="考试批次">
            <el-select v-model="formData.examHalf" placeholder="上/下半年（可不选）" clearable :style="{width: '100%'}">
              <el-option :value="1" label="上半年" />
              <el-option :value="2" label="下半年" />
            </el-select>
            <div style="margin-top: 5px; color: #909399; font-size: 12px">
              💡 不选表示该年份仅考一次
            </div>
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
              @visible-change="handlePracticeGroupDropdown"
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
              @visible-change="handlePracticeColumnDropdown"
              @change="handlePracticeColumnIdsChange"
            >
              <el-option
                v-for="col in practiceColumnOptions"
                :key="col.columnId"
                :label="formatPracticeColumnLabel(col)"
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
      <el-col :span="24">
        <el-form-item label="智能录题">
          <div class="ai-generate-box">
            <div class="ai-generate-row">
              <el-upload
                ref="aiImageUpload"
                :auto-upload="false"
                :show-file-list="true"
                :limit="1"
                accept="image/*"
                :on-change="handleAiImageChange"
                :on-remove="handleAiImageRemove"
                :on-exceed="handleAiImageExceed"
                action="#"
              >
                <el-button size="small" icon="el-icon-picture-outline">选择题目图片</el-button>
              </el-upload>
              <el-switch
                v-model="aiGenerate.autoAssignColumn"
                active-text="自动分配专栏"
                inactive-text="不自动分配"
                @change="handleAutoAssignColumnSwitchChange"
              />
              <el-button
                type="primary"
                size="small"
                :loading="aiGenerate.loading"
                @click="handleAiGenerateFromImage"
              >智能生成题目解析</el-button>
            </div>
            <div class="ai-generate-tip">
              上传截图后自动回填题型、题干、选项、答案、解析、难度；单题/完形均可识别，完形时回填多子题选项与答案；开启自动分配时尝试回填二级栏目。
              若当前为<strong>真题</strong>，将额外识别卷面<strong>原卷题号</strong>并填入「原卷题号」。
            </div>
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
              <el-button type="primary" size="mini" icon="el-icon-plus" @click="addClozeChild(1)">添加子题</el-button>
              <span style="margin-left: 12px; color:#909399; font-size: 12px;">批量添加：</span>
              <el-input-number
                v-model="clozeChildBatchCount"
                size="mini"
                :min="1"
                :max="50"
                style="width: 120px; margin-right: 8px;"
              />
              <el-button type="success" size="mini" icon="el-icon-circle-plus-outline" @click="addClozeChild(clozeChildBatchCount)">
                批量添加
              </el-button>
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
          <el-form-item label="难度" prop="difficulty">
            <el-select
              v-model="formData.difficulty"
              placeholder="请选择难度"
              :style="{ width: '260px' }"
            >
              <el-option :value="1" label="简单" />
              <el-option :value="2" label="中等" />
              <el-option :value="3" label="困难" />
            </el-select>
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
import { addQuestion, addClozeQuestion, updateClozeQuestion, getQuestion, getClozeQuestion, updateQuestion, bindKnowledgePoints, getQuestionKnowledgePoints, listKnowledgePoints, getQuestionPracticeColumns, bindQuestionPracticeColumns, createQuestionAiImageTask, queryQuestionAiImageTask } from "@/api/quiz/question";
import { optionSubject } from "@/api/quiz/subject";
import { listAnimation, getAnimation } from "@/api/quiz/animation";
import { listPracticeGroupOptions, listPracticeColumnOptions, addPracticeColumn } from "@/api/quiz/practiceColumn";
import { uploadFile } from "@/api/common";
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
    // 是否显示“原卷题号”输入框（组卷管理中可关闭）
    showOriginOrder: {
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
        originOrder: null,
        examYear: null,
        examHalf: null,
        isRealQuestion: true,
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
        difficulty: 1,
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
      clozeChildBatchCount: 3,
      aiGenerate: {
        loading: false,
        imageFile: null,
        autoAssignColumn: false,
        moduleCandidates: []
      },
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
  watch: {
    'formData.isRealQuestion'(val) {
      // 非真题：隐藏并清空“原卷题号/年份/批次”，避免误保存
      if (val === false) {
        this.formData.originOrder = null;
        this.formData.examYear = null;
        this.formData.examHalf = null;
      }
    },
    'formData.analysis'(val) {
      // 仅单选题：从解析文本中自动识别“答案：X”并设置标准答案
      this.autoInferSingleChoiceAnswerFromAnalysis(val);
      // 从解析中识别“知识点：XXX”并自动选择对应专栏
      this.autoInferPracticeColumnFromAnalysis(val);
    }
  },
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
      // 新增模式：不再做本地草稿保存/恢复
    }
  },
  methods: {
    getNextOriginOrderAfterCreate(span = 1) {
      if (!this.formData || this.formData.isRealQuestion !== true) return null;
      const raw = this.formData.originOrder;
      if (raw === null || raw === undefined || raw === '') return null;
      const n = Number(raw);
      if (!Number.isFinite(n)) return null;
      const s = Number(span);
      if (!Number.isFinite(s) || s <= 0) {
        return Math.floor(n) + 1;
      }
      return Math.floor(n) + Math.floor(s);
    },
    getRecentPracticeColumnIds() {
      try {
        const raw = localStorage.getItem('dm_recent_practice_columns');
        if (!raw) return [];
        const arr = JSON.parse(raw);
        return Array.isArray(arr) ? arr : [];
      } catch (e) {
        return [];
      }
    },
    saveRecentPracticeColumnIds(ids) {
      try {
        localStorage.setItem('dm_recent_practice_columns', JSON.stringify(ids || []));
      } catch (e) {
      }
    },
    applyRecentOrderToColumns(list) {
      if (!Array.isArray(list) || !list.length) return list;
      const recent = this.getRecentPracticeColumnIds();
      if (!recent.length) return list;
      const orderMap = new Map();
      recent.forEach((id, idx) => {
        if (!orderMap.has(id)) {
          orderMap.set(id, idx);
        }
      });
      return [...list].sort((a, b) => {
        const ia = orderMap.has(a.columnId) ? orderMap.get(a.columnId) : Infinity;
        const ib = orderMap.has(b.columnId) ? orderMap.get(b.columnId) : Infinity;
        if (ia !== ib) return ia - ib;
        return (a.columnId || 0) - (b.columnId || 0);
      });
    },
    getNextOriginOrderAfterCreate(span = 1) {
      if (!this.formData || this.formData.isRealQuestion !== true) return null;
      const raw = this.formData.originOrder;
      if (raw === null || raw === undefined || raw === '') return null;
      const n = Number(raw);
      if (!Number.isFinite(n)) return null;
      const s = Number(span);
      if (!Number.isFinite(s) || s <= 0) {
        return Math.floor(n) + 1;
      }
      return Math.floor(n) + Math.floor(s);
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
      const cols = (cRes && cRes.data) || [];
      this.practiceColumnOptions = this.applyRecentOrderToColumns(cols);
    },
    formatPracticeColumnLabel(col) {
      if (!col) return '';
      const g = (col.groupName || '其他').trim ? (col.groupName || '其他').trim() : (col.groupName || '其他');
      const c = (col.columnName || '').trim ? (col.columnName || '').trim() : (col.columnName || '');
      if (!c) return g;
      return `${g} / ${c}`;
    },
    async handlePracticeGroupChange(val) {
      // 按一级分组过滤二级栏目选项；
      // 规则：当一级分组被清空时，二级已选也要同步清空（避免脏数据）
      if (!this.formData.subjectId) return;
      const params = { subjectId: this.formData.subjectId };
      if (!val) {
        // 清空一级分组 => 清空二级已选，并恢复“当前科目下全量二级栏目”
        this.practiceColumnIds = [];
      } else {
        params.groupName = val;
      }
      const res = await listPracticeColumnOptions(params);
      const cols = (res && res.data) || [];
      this.practiceColumnOptions = this.applyRecentOrderToColumns(cols);
      const allowed = new Set(this.practiceColumnOptions.map(x => x.columnId));
      this.practiceColumnIds = (this.practiceColumnIds || []).filter(id => allowed.has(id));
    },
    async handlePracticeGroupDropdown(visible) {
      if (!visible) return;
      // 下拉展开时强制刷新，避免跨页面新增栏目后这里仍是旧数据
      await this.loadPracticeOptions();
    },
    async handlePracticeColumnDropdown(visible) {
      if (!visible) return;
      if (!this.formData.subjectId) return;
      // 有选择一级分组则按分组刷新，否则刷新全量
      if (this.practiceGroupName) {
        await this.handlePracticeGroupChange(this.practiceGroupName);
      } else {
        await this.loadPracticeOptions();
      }
    },
    handlePracticeColumnIdsChange(val) {
      // 需求：新增题目时，选择了二级栏目后，若一级分组未选则自动补全
      if (!this.formData.subjectId) return;
      const selectedIds = Array.isArray(val) ? val : (this.practiceColumnIds || []);
      if (!selectedIds.length) {
        // 仅清空选择时不更新最近使用列表
        return;
      }
      // 若一级分组已选，则不自动覆盖用户选择
      if (this.practiceGroupName && String(this.practiceGroupName).trim()) {
        // 已经有分组就不自动推断
      } else {
        const picked = (this.practiceColumnOptions || []).filter(c => selectedIds.includes(c.columnId));
        const uniqGroups = Array.from(new Set(picked.map(x => (x.groupName || '').trim()).filter(Boolean)));
        if (uniqGroups.length === 1) {
          this.practiceGroupName = uniqGroups[0];
        }
      }
      // 更新“最近使用栏目”缓存：本次选择的在前，其它保持
      const recent = this.getRecentPracticeColumnIds();
      const set = new Set();
      const merged = [];
      selectedIds.forEach(id => {
        if (!set.has(id)) {
          set.add(id);
          merged.push(id);
        }
      });
      recent.forEach(id => {
        if (!set.has(id)) {
          set.add(id);
          merged.push(id);
        }
      });
      this.saveRecentPracticeColumnIds(merged);
      this.practiceColumnOptions = this.applyRecentOrderToColumns(this.practiceColumnOptions || []);
    },
    handleAiImageChange(file) {
      this.aiGenerate.imageFile = file && file.raw ? file.raw : null;
    },
    /** limit=1 时再次选图会走 exceed：先清空再接纳新文件，无需先点 × */
    handleAiImageExceed(files) {
      if (!files || !files.length) return;
      const ref = this.$refs.aiImageUpload;
      if (!ref || typeof ref.clearFiles !== 'function' || typeof ref.handleStart !== 'function') return;
      ref.clearFiles();
      ref.handleStart(files[0]);
    },
    handleAutoAssignColumnSwitchChange(val) {
      if (val && !this.formData.subjectId) {
        this.$message.warning('请先选择科目');
        this.aiGenerate.autoAssignColumn = false;
      }
    },
    handleAiImageRemove() {
      this.aiGenerate.imageFile = null;
    },
    /** 清空智能录题图片：与点文件列表上的「×」一致，便于新增成功后连续录题 */
    clearAiImageUpload() {
      this.aiGenerate.imageFile = null;
      const u = this.$refs.aiImageUpload;
      if (u && typeof u.clearFiles === 'function') {
        u.clearFiles();
      }
    },
    mapAiQuestionType(questionType) {
      const t = String(questionType || '').toUpperCase();
      if (t === 'SINGLE') return 1;
      if (t === 'MULTIPLE') return 2;
      if (t === 'SUBJECTIVE') return 3;
      if (t === 'JUDGE') return 4;
      // 业务仅支持客观单选/多选/判断与完形；模型勿返回 FILL_BLANK，若仍返回则按单选处理
      if (t === 'FILL_BLANK') return 1;
      if (t === 'CLOZE') return 6;
      return this.formData.questionType || 1;
    },
    /** AI 子题题型 -> 后台 questionType（仅单选/多选/判断） */
    mapAiChildQuestionType(questionType) {
      const t = String(questionType || '').toUpperCase();
      if (t === 'MULTIPLE') return 2;
      if (t === 'JUDGE') return 4;
      return 1;
    },
    async handleAiGenerateFromImage() {
      if (!this.aiGenerate.imageFile) {
        this.$message.warning('请先选择题目图片');
        return;
      }
      if (!this.formData.subjectId) {
        this.$message.warning('请先选择科目');
        return;
      }
      if (this.formData.isRealQuestion === true) {
        if (this.formData.examYear == null || this.formData.examYear === '') {
          this.$message.warning('已勾选真题，请先填写题目年份');
          return;
        }
        if (this.formData.examHalf == null || this.formData.examHalf === '') {
          this.$message.warning('已勾选真题，请先选择考试批次');
          return;
        }
      }
      this.aiGenerate.loading = true;
      try {
        // 自动分配专栏依赖一级分组/二级栏目；若未刷新会导致 pointCandidates 为空、knowledgePointId 无法匹配
        if (this.formData.subjectId) {
          await this.loadPracticeOptions();
        }
        // 先上传到OSS；仅在AI结果完成后再统一回填，避免图片提前回显
        const ossForm = new FormData();
        ossForm.append('file', this.aiGenerate.imageFile);
        const uploadRes = await uploadFile(ossForm);
        const imageUrl = uploadRes && (uploadRes.url || (uploadRes.data && uploadRes.data.url));
        if (!imageUrl) {
          this.$message.error('图片上传OSS失败');
          return;
        }

        const formData = new FormData();
        formData.append('imageFile', this.aiGenerate.imageFile);
        formData.append('autoAssignColumn', String(this.aiGenerate.autoAssignColumn));
        formData.append('isRealQuestion', String(this.formData.isRealQuestion === true));
        if (this.aiGenerate.autoAssignColumn) {
          const candidates = this.buildModuleLevelCandidates();
          this.aiGenerate.moduleCandidates = candidates;
          formData.append('pointCandidates', JSON.stringify(candidates));
        }
        const createRes = await createQuestionAiImageTask(formData);
        const taskId = createRes && createRes.data && createRes.data.taskId;
        if (!taskId) {
          this.$message.error((createRes && createRes.msg) || '创建AI任务失败');
          return;
        }
        const result = await this.pollAiTaskResult(taskId, 30, 2000);
        if (!result || result.status !== 'SUCCESS' || !result.result) {
          this.$message.error((result && result.errorMessage) || 'AI任务失败');
          return;
        }
        await this.applyAiResultToForm(this.normalizeAiImageResult(result.result), imageUrl);
        this.$message.success('AI回填成功，请检查后再提交');
      } finally {
        this.aiGenerate.loading = false;
      }
    },
    async pollAiTaskResult(taskId, maxCount, intervalMs) {
      for (let i = 0; i < maxCount; i++) {
        const res = await queryQuestionAiImageTask(taskId);
        const data = res && res.data;
        if (data && (data.status === 'SUCCESS' || data.status === 'FAILED')) {
          return data;
        }
        await new Promise(resolve => setTimeout(resolve, intervalMs));
      }
      return { status: 'FAILED', errorMessage: 'AI任务超时，请稍后重试' };
    },
    /** 解析模型偶发返回的 JSON 字符串、统一 subAnswers 结构 */
    normalizeAiImageResult(raw) {
      const ai = raw && typeof raw === 'object' ? { ...raw } : {};
      const parseMaybe = (v) => {
        if (typeof v === 'string') {
          const s = v.trim();
          if (s.startsWith('[') || s.startsWith('{')) {
            try {
              return JSON.parse(s);
            } catch (e) {
              return v;
            }
          }
        }
        return v;
      };
      ai.subQuestions = parseMaybe(ai.subQuestions);
      ai.subAnswers = parseMaybe(ai.subAnswers);
      if (ai.subQuestions != null && !Array.isArray(ai.subQuestions)) {
        ai.subQuestions = [];
      }
      if (ai.subAnswers != null && typeof ai.subAnswers !== 'object') {
        ai.subAnswers = null;
      }
      return ai;
    },
    /** 模型常把完形判成 SINGLE：只要有多空结构就按完形回填 */
    shouldTreatAiResultAsCloze(ai) {
      const t = String(ai.questionType || '').toUpperCase();
      if (t === 'CLOZE') return true;
      const subs = ai.subQuestions;
      if (Array.isArray(subs) && subs.length > 1) return true;
      const sa = ai.subAnswers;
      if (sa && typeof sa === 'object') {
        const keys = Object.keys(sa).filter(k => String(k).trim() !== '');
        if (keys.length > 1) return true;
      }
      return false;
    },
    async applyAiResultToForm(ai, imageUrl) {
      const mappedType = this.shouldTreatAiResultAsCloze(ai) ? 6 : this.mapAiQuestionType(ai.questionType);
      this.formData.questionType = mappedType;
      this.applyQuestionTypeDefaults(mappedType);

      this.formData.questionTitle = this.buildStemWithImageFirst(imageUrl, ai.stemText || '');
      if (mappedType === 6) {
        this.applyAiClozeResult(ai);
      } else {
        if (Array.isArray(ai.options) && [1, 2, 4].includes(mappedType)) {
          this.formData.items = ai.options.map(opt => ({
            prefix: opt.key,
            content: opt.text || ''
          }));
        }
        if (ai.answer && ai.answer.option) {
          const optionText = String(ai.answer.option || '');
          if (mappedType === 2) {
            this.formData.correctArray = optionText.split('').filter(Boolean);
            this.formData.correct = '';
          } else {
            this.formData.correct = optionText;
          }
        }
        if (ai.analysis) this.formData.analysis = ai.analysis;
        if (ai.difficultyTag) {
          this.formData.difficulty = ai.difficultyTag === 'HARD_LATER' ? 3 : 1;
        }
      }
      this.applyAiOriginOrder(ai);

      if (this.aiGenerate.autoAssignColumn) {
        // 优先使用 knowledgeColumnId；若模型未命中真实二级ID，则根据一级模块候选回填
        const targetId = Number(ai.knowledgeColumnId || 0);
        const exists = (this.practiceColumnOptions || []).some(c => Number(c.columnId) === targetId);
        if (targetId && exists) {
          this.practiceColumnIds = [targetId];
          this.handlePracticeColumnIdsChange(this.practiceColumnIds);
        } else {
          // 一级模块兜底：根据 AI 返回的 knowledgePointId 映射到一级分组，再落到该分组“其他/零散题”
          const groupName = this.resolveGroupNameFromAi(ai);
          if (!groupName) {
            this.$message.warning('未命中一级专栏：请检查科目下是否有专项分组，或手动选择专栏（模型返回的 knowledgePointId 与候选不一致）');
          } else {
            const picked = await this.pickOrCreateDefaultColumnByGroup(groupName);
            if (picked && picked.columnId) {
              this.practiceColumnIds = [picked.columnId];
              this.handlePracticeColumnIdsChange(this.practiceColumnIds);
              this.$message.success(`已自动归入：${picked.groupName || '未分组'} / ${picked.columnName}`);
            } else {
              this.$message.warning('未命中一级专栏，请先创建一级专栏');
            }
          }
        }
      }
    },
    /** 真题模式下回填「原卷题号」，对应后端字段 originOrder */
    applyAiOriginOrder(ai) {
      if (!this.formData || this.formData.isRealQuestion !== true) return;
      const v = ai.originOrder;
      if (v === null || v === undefined || v === '') return;
      const n = Number(v);
      if (!Number.isFinite(n) || n < 1) return;
      this.formData.originOrder = Math.floor(n);
    },
    /**
     * 完形填空：解析 AI 返回的 subQuestions / subAnswers，回填 clozeChildren。
     * subAnswers 形如 {"1":"B","2":"C"}；子题题干无单独文字时 questionTitle 可为空，仅填选项与答案。
     */
    applyAiClozeResult(ai) {
      const subAnswers = ai.subAnswers && typeof ai.subAnswers === 'object' ? ai.subAnswers : null;
      let list = Array.isArray(ai.subQuestions) ? ai.subQuestions.slice() : [];

      if (list.length === 0 && subAnswers) {
        const keys = Object.keys(subAnswers).sort((a, b) => Number(a) - Number(b));
        for (const k of keys) {
          list.push({
            index: Number(k),
            questionTitle: '',
            questionType: 'SINGLE',
            options: [],
            answer: { option: String(subAnswers[k] != null ? subAnswers[k] : '').trim() }
          });
        }
      }
      // subQuestions 只有 1 条但 subAnswers 有多键时，按 subAnswers 补全各空
      if (subAnswers && list.length > 0) {
        const keys = Object.keys(subAnswers).sort((a, b) => Number(a) - Number(b));
        for (const k of keys) {
          const idxNum = Number(k);
          if (!list.some(x => Number(x.index) === idxNum || String(x.index) === String(k))) {
            list.push({
              index: idxNum,
              questionTitle: '',
              questionType: 'SINGLE',
              options: [],
              answer: { option: String(subAnswers[k] != null ? subAnswers[k] : '').trim() }
            });
          }
        }
      }

      list.sort((a, b) => (Number(a.index) || 0) - (Number(b.index) || 0));

      const children = list.map((sq, idx) => {
        const idxKey = String(sq.index != null ? sq.index : idx + 1);
        let letter = '';
        if (sq.answer && sq.answer.option != null && String(sq.answer.option).trim() !== '') {
          letter = String(sq.answer.option).trim();
        } else if (subAnswers && subAnswers[idxKey] != null) {
          letter = String(subAnswers[idxKey]).trim();
        }
        const qt = this.mapAiChildQuestionType(sq.questionType || 'SINGLE');
        let items = [];
        if (qt === 4) {
          items = defaultJudgeOptions();
        } else if (Array.isArray(sq.options) && sq.options.length > 0) {
          items = sq.options.map(opt => ({
            prefix: String(opt.key || '').toUpperCase(),
            content: opt.text != null ? String(opt.text) : ''
          }));
        } else {
          items = defaultChoiceOptions();
        }
        const child = {
          questionType: qt,
          questionTitle: sq.questionTitle != null ? String(sq.questionTitle) : '',
          score: 1,
          items,
          correct: '',
          correctArray: []
        };
        if (qt === 2 && letter) {
          child.correctArray = letter.replace(/[^A-Za-z]/g, '').toUpperCase().split('').filter(Boolean);
        } else if (qt === 4 && letter) {
          const u = letter.toUpperCase();
          child.correct = u.startsWith('A') || u.indexOf('对') >= 0 || u === 'T' ? 'A' : 'B';
        } else if (letter) {
          child.correct = letter.toUpperCase().charAt(0);
        }
        return child;
      });

      this.formData.clozeChildren = children;
      if (ai.analysis) this.formData.analysis = ai.analysis;
      if (ai.difficultyTag) {
        this.formData.difficulty = ai.difficultyTag === 'HARD_LATER' ? 3 : 1;
      }
    },
    resolveGroupNameFromAi(ai) {
      const raw = ai && ai.knowledgePointId;
      if (raw === null || raw === undefined || raw === '') return '';
      const pointId = Number(raw);
      if (!Number.isFinite(pointId)) return '';
      const candidates = this.aiGenerate.moduleCandidates || [];
      const hit = candidates.find(c =>
        Number(c.pointId) === pointId ||
        String(c.pointId) === String(raw)
      );
      if (hit && hit.pointName) {
        return String(hit.pointName).trim();
      }
      return '';
    },
    async pickOrCreateDefaultColumnByGroup(groupName) {
      const matchedGroup = String(groupName || '').trim();
      if (!matchedGroup) return null;
      const inGroup = (this.practiceColumnOptions || []).filter(c => String(c.groupName || '').trim() === matchedGroup);
      const prefer = inGroup.find(c => this.isDefaultScatterColumn(String(c.columnName || '')));
      if (prefer) {
        this.practiceGroupName = matchedGroup;
        return prefer;
      }
      // 该一级下无默认二级才创建
      try {
        const addRes = await addPracticeColumn({
          subjectId: this.formData.subjectId,
          groupName: matchedGroup,
          columnName: '其它/零散题',
          enablePractice: 1,
          groupSort: 0,
          sortOrder: 9999
        });
        if (!addRes || addRes.code !== 200) return null;
        await this.loadPracticeOptions();
        const refreshed = (this.practiceColumnOptions || []).find(
          c => String(c.groupName || '').trim() === matchedGroup && this.isDefaultScatterColumn(String(c.columnName || ''))
        );
        if (refreshed) {
          this.practiceGroupName = matchedGroup;
          return refreshed;
        }
      } catch (e) {
        // ignore
      }
      return null;
    },
    isDefaultScatterColumn(name) {
      const raw = String(name || '').trim();
      if (!raw) return false;
      const normalized = raw.replace(/\s+/g, '').replace(/｜/g, '|').replace(/／/g, '/').toLowerCase();
      if (['其他', '其它', '默认', '零散题', '其他/零散题', '其它/零散题', '其他/默认', '其它/默认'].includes(normalized)) {
        return true;
      }
      return (normalized.includes('其他') || normalized.includes('其它') || normalized.includes('默认')) && normalized.includes('零散');
    },
    buildModuleLevelCandidates() {
      // 规则：只把所有一级分组发给AI，不提前创建二级默认栏目
      const groups = (this.practiceGroupOptions || []).map(g => String(g.groupName || '').trim()).filter(Boolean);
      if (!groups.length) return [];
      return groups.map((groupName, index) => {
        const inGroup = (this.practiceColumnOptions || []).filter(
          c => String(c.groupName || '').trim() === groupName && Number(c.columnId) > 0
        );
        // 使用该一级分组下真实存在的 columnId 作为 pointId，避免虚拟ID造成混淆
        const sorted = inGroup.slice().sort((a, b) => Number(a.columnId) - Number(b.columnId));
        const realPointId = sorted.length ? Number(sorted[0].columnId) : (900000 + index);
        return {
          pointId: realPointId,
          pointName: groupName,
          columnId: 0,
          columnName: ''
        };
      });
    },
    buildStemWithImageFirst(imageUrl, stemText) {
      const text = String(stemText || '').trim();
      const url = String(imageUrl || '').trim();
      if (!url) {
        return text;
      }
      if (this.formData.questionTitleFormat === 'markdown') {
        return text ? `![题目图片](${url})\n\n${text}` : `![题目图片](${url})`;
      }
      const safeText = this.escapeHtml(text);
      const imageHtml = `<p><img src="${url}" style="max-width: 100%; height: auto;" /></p>`;
      return text ? `${imageHtml}<p>${safeText}</p>` : imageHtml;
    },
    escapeHtml(text) {
      return String(text || '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
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
      if (questionType === 6) {
        this.formData.items = [];
        this.formData.correct = '';
        this.formData.correctArray = [];
        this.formData.fillBlankAnswers = [];
        if (!preserveAnswer) {
          this.formData.clozeChildren = [];
        }
        return;
      }
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
        
        const inferredIsReal = (response.data.originOrder !== null && response.data.originOrder !== undefined)
          || (response.data.examYear !== null && response.data.examYear !== undefined)
          || (response.data.examHalf !== null && response.data.examHalf !== undefined);
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
          isRealQuestion: inferredIsReal,
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
              id: child.id,
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
    addClozeChild(count = 1) {
      if (!Array.isArray(this.formData.clozeChildren)) {
        this.formData.clozeChildren = [];
      }
      const n = Math.max(1, Math.min(50, Number(count) || 1));
      for (let i = 0; i < n; i++) {
        this.formData.clozeChildren.push({
          questionType: 1,
          questionTitle: '',
          score: 1,
          items: defaultChoiceOptions(),
          correct: '',
          correctArray: []
        });
      }
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
        const keepIsReal = this.formData.isRealQuestion === true;
        const keepExamYear = keepIsReal ? this.formData.examYear : null;
        const keepExamHalf = keepIsReal ? this.formData.examHalf : null;
        // 完形父题原卷题号表示“首个子题题号”，新增一组后应按子题数量跳号
        const clozeSpan = (this.formData.clozeChildren || []).filter(Boolean).length || 1;
        const nextOriginOrder = this.getNextOriginOrderAfterCreate(clozeSpan);
        const children = (this.formData.clozeChildren || []).map((child, index) => {
          return {
            id: child.id,
            questionType: child.questionType,
            questionTitle: child.questionTitle,
            score: child.score,
            items: child.items,
            correct: child.correct,
            correctArray: child.correctArray,
            clozeIndex: index + 1
          };
        });
        const payload = {
          parent: {
            id: this.formData.id,
            subjectId: this.formData.subjectId,
            questionType: 6,
            questionTitle: this.formData.questionTitle,
            analysis: this.formData.analysis,
            score: this.formData.score,
            animationId: this.formData.animationId,
            originOrder: keepIsReal ? this.formData.originOrder : null,
            examYear: keepExamYear,
            examHalf: keepExamHalf,
            analysisFormat: this.formData.analysisFormat,
            questionTitleFormat: this.formData.questionTitleFormat
          },
          children: children
        };
        const isEdit = !!this.formData.id;
        const res = isEdit ? await updateClozeQuestion(payload) : await addClozeQuestion(payload);
        const message = res.code === 200 ? "完形填空保存成功!" : "保存失败,请联系管理员!"
        const typeName = res.code === 200 ? "success" : "error"
        this.$message({ message, type: typeName });
        // 新增：重置输入；编辑：刷新一次数据，保证子题id/索引回显一致
        if (res.code === 200 && !isEdit) {
          const keepSubjectId = this.formData.subjectId;
          const keepQuestionType = this.formData.questionType; // 6

          // 先按表单初始值整体重置，再恢复科目/题型
          this.$refs['elForm'] && this.$refs['elForm'].resetFields();

          // resetFields 不会处理这些“表单外状态”，这里一并清空
          this.animationFileList = [];
          this.knowledgePointOptions = [];
          this.practiceGroupName = '';
          this.practiceColumnIds = [];
          this.formData.animationId = null;
          this.formData.animationUrl = '';
          this.formData.knowledgePointIds = [];

          this.formData.subjectId = keepSubjectId;
          this.formData.questionType = keepQuestionType;
          this.formData.isRealQuestion = keepIsReal;
          this.formData.examYear = keepExamYear;
          this.formData.examHalf = keepExamHalf;
          this.formData.originOrder = nextOriginOrder;
          // 仅保留科目/题型：其余全部按默认初始化
          this.formData.score = 1;
          this.formData.analysis = '无';
          this.formData.questionTitle = '';
          this.formData.correct = '';
          this.formData.correctArray = [];
          this.formData.fillBlankAnswers = [{ value: '' }];
          this.formData.requireOrder = false;
          this.applyQuestionTypeDefaults(keepQuestionType);
          this.formData.clozeChildren = [];
          this.clearAiImageUpload();

          this.$nextTick(() => {
            this.$refs['elForm'] && this.$refs['elForm'].clearValidate();
          });
        }
        if (res.code === 200 && isEdit) {
          await this.getData(this.formData.id);
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
          const keepIsReal = this.formData.isRealQuestion === true;
          const keepExamYear = keepIsReal ? this.formData.examYear : null;
          const keepExamHalf = keepIsReal ? this.formData.examHalf : null;
          const nextOriginOrder = this.getNextOriginOrderAfterCreate();
          // 新增成功后：只保留科目+题型，其余全部清空，便于连续录题
          const keepSubjectId = this.formData.subjectId;
          const keepQuestionType = this.formData.questionType;
          // 直接整体重置到初始默认，再回填科目/题型
          this.$refs['elForm'] && this.$refs['elForm'].resetFields();
          // 表单外状态同步清空
          this.practiceGroupName = '';
          this.practiceColumnIds = [];
          this.animationFileList = [];
          this.knowledgePointOptions = [];
          this.animationOptions = [];
          // 回填科目/题型
          this.formData.subjectId = keepSubjectId;
          this.formData.questionType = keepQuestionType;
          this.formData.isRealQuestion = keepIsReal;
          this.formData.examYear = keepExamYear;
          this.formData.examHalf = keepExamHalf;
          this.formData.originOrder = nextOriginOrder;
          // 强制其余默认（resetFields 可能受当前表单结构影响，这里显式兜底）
          this.formData.id = undefined;
          this.formData.questionTitle = '';
          this.formData.analysis = '无';
          this.formData.score = 1;
          this.formData.correct = '';
          this.formData.correctArray = [];
          this.formData.fillBlankAnswers = [{ value: '' }];
          this.formData.requireOrder = false;
          this.formData.animationId = null;
          this.formData.animationUrl = '';
          this.formData.knowledgePointIds = [];
          this.applyQuestionTypeDefaults(keepQuestionType);
          // resetFields 不覆盖 items；applyQuestionTypeDefaults 在单选/多选且 items 非空时也不会重建选项，需显式清空
          if ([1, 2].includes(keepQuestionType)) {
            this.formData.items = defaultChoiceOptions();
          }
          this.clearAiImageUpload();

          // 清除表单验证状态，避免显示红色提示信息
          this.$nextTick(() => {
            this.$refs['elForm'].clearValidate();
          });
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

    // 从解析内容中自动识别单选题标准答案（仅在未手动选择答案时生效）
    autoInferSingleChoiceAnswerFromAnalysis(text) {
      // 只对单选题生效
      if (!this.formData || this.formData.questionType !== 1) return;
      // 已经有标准答案时不覆盖人工选择
      if (this.formData.correct) return;
      if (!text) return;
      const raw = String(text);
      // 富文本模式下简单去掉 HTML 标签，只看纯文本
      const plain = raw.replace(/<[^>]*>/g, ' ');
      // 需求：找到“答案”二字后，若干字符范围内出现的第一个字母作为选项
      const idx = plain.indexOf('答案');
      if (idx === -1) return;
      const tail = plain.slice(idx + 2); // 跳过“答案”两个字
      // 允许中间有“：”“:”“为”“是”等字以及空格和少量其他字符
      const around = tail.slice(0, 20); // 只看“答案”后面最多20个字符
      const match = around.match(/[A-Z]/i);
      if (!match) return;
      const letter = String(match[0] || '').toUpperCase();
      if (!letter) return;
      // 只在当前选项前缀中存在时才设置
      const hasOption = (this.formData.items || []).some(item => item && item.prefix === letter);
      if (!hasOption) return;
      this.formData.correct = letter;
      // 这里不再弹出提示，避免与知识点专栏提示叠加打扰
    },

    // 从解析中识别“知识点：XXX”，并自动选择对应一级/二级专栏（优先选择该一级分组下名称为“其他”或“默认”的二级栏目）
    async autoInferPracticeColumnFromAnalysis(text) {
      if (!this.formData || !this.formData.subjectId) return;
      if (!text) return;
      const raw = String(text);
      // 去掉 HTML 标签
      const plain = raw.replace(/<[^>]*>/g, ' ');
      // 匹配“知识点：软件测试”这类格式，取“知识点：”后面连续的一段非标点/空白字符作为分组名称
      const m = plain.match(/知识点\s*[：:]\s*([^\s，。、；;]+)/);
      if (!m) return;
      const groupName = (m[1] || '').trim();
      if (!groupName) return;

      // 若当前一级分组已与解析里识别的一致且已经选过二级栏目，则不重复覆盖
      if (this.practiceGroupName === groupName && Array.isArray(this.practiceColumnIds) && this.practiceColumnIds.length) {
        return;
      }

      // 确保当前科目的专栏选项已加载
      if (!Array.isArray(this.practiceGroupOptions) || !this.practiceGroupOptions.length
        || !Array.isArray(this.practiceColumnOptions) || !this.practiceColumnOptions.length) {
        await this.loadPracticeOptions();
      }

      // 一级分组名称支持“相似匹配”，只要包含关系即可，例如解析里是“软件测试”，系统里是“软件测试基础”
      const existsGroup = (this.practiceGroupOptions || []).some(g => {
        const name = (g.groupName || '').trim();
        if (!name || !groupName) return false;
        return name.includes(groupName) || groupName.includes(name);
      });
      if (!existsGroup) {
        // 若系统里没有这个一级分组，就不做任何自动选择，避免误判
        return;
      }

      // 二级栏目也按分组名称做“相似匹配”
      const candidates = (this.practiceColumnOptions || []).filter(c => {
        const name = (c.groupName || '').trim();
        if (!name || !groupName) return false;
        return name.includes(groupName) || groupName.includes(name);
      });
      if (!candidates.length) return;

      // 优先选择名称为“其他”或“默认”的二级栏目，否则就取该分组下第一个栏目
      const preferNames = ['其他', '默认'];
      let picked = candidates.find(c => preferNames.includes((c.columnName || '').trim()));
      if (!picked) {
        picked = candidates[0];
      }
      if (!picked || !picked.columnId) return;

      this.practiceGroupName = groupName;
      this.practiceColumnIds = [picked.columnId];

      // 更新最近使用缓存与排序
      this.handlePracticeColumnIdsChange(this.practiceColumnIds);
      // 仅保留一个提示，避免与答案提示叠加
      this.$message.success(`已根据解析中的知识点自动选择专栏：${groupName} / ${picked.columnName || '其他/零散题'}`);
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

.ai-generate-box {
  padding: 10px 12px;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  background: #fafafa;
}

.ai-generate-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.ai-generate-tip {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
}
</style>
