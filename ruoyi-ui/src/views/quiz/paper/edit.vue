<template>
  <div style="padding: 20px">
    <el-row :gutter="15">
      <el-form ref="elForm1" :model="formData" :rules="rules" :show-message="false" size="medium" label-width="100px">
        <el-col :span="12">
          <el-form-item label="科目" prop="subjectId">
            <el-select v-model="formData.subjectId" placeholder="请选择科目" clearable :style="{width: '100%'}">
              <el-option v-for="(item, index) in subjectIdOptions" :key="index" :label="item.label"
                         :value="item.value" :disabled="item.disabled"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="试卷名" prop="paperName">
            <el-input v-model="formData.paperName" placeholder="请输入试卷名" clearable :style="{width: '100%'}">
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="试卷类型" prop="paperType">
            <el-select v-model="formData.paperType" placeholder="请选择试卷类型" clearable :style="{width: '100%'}">
              <el-option v-for="(item, index) in paperTypeOptions" :key="index" :label="item.label"
                         :value="item.value" :disabled="item.disabled"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="考试时长" prop="suggestTime">
            <el-input-number v-model="formData.suggestTime" :min="0"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="开始时间" prop="startTime">
            <el-date-picker
              v-model="formData.startTime"
              type="datetime"
              placeholder="请选择开始时间"
              value-format="yyyy-MM-dd HH:mm:ss"
              format="yyyy-MM-dd HH:mm:ss"
              :style="{width: '100%'}">
            </el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="截止时间" prop="endTime">
            <el-date-picker
              v-model="formData.endTime"
              type="datetime"
              placeholder="请选择截止时间"
              value-format="yyyy-MM-dd HH:mm:ss"
              format="yyyy-MM-dd HH:mm:ss"
              :style="{width: '100%'}">
            </el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="防作弊">
            <el-switch
              v-model="formData.enableAntiCheat"
              active-text="开启"
              inactive-text="关闭">
            </el-switch>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="题号规则">
            <el-radio-group v-model="formData.numberMode">
              <el-radio :label="1">按题型分组</el-radio>
              <el-radio :label="2">按加入顺序</el-radio>
              <el-radio :label="3">按原卷题序</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <!--分割线-->
        <el-col :span="24" class="mb-2">
          <el-alert title="可按照题型规则或按日期范围快速自动组卷，仍可在生成后调整题型及题目列表" type="info" :closable="false"/>
          <el-button type="primary" icon="el-icon-magic-stick" size="mini" class="mt-2" @click="openAutoComposeDialog">
            规则自动组卷
          </el-button>
          <el-button type="success" icon="el-icon-date" size="mini" class="mt-2" style="margin-left: 8px"
                     @click="openAutoComposeByDateDialog">
            按日期自动组卷（例如：今天录入的所有题）
          </el-button>
        </el-col>
        <el-col :span="24">
          <el-form-item
            :label="formData.numberMode === 1 ? ('题型'+(qtypeIndex+1)+':') : (questionType.name || '题目')"
            v-for="(questionType,qtypeIndex) in formData.paperQuestionTypeDto"
            :key="qtypeIndex"
          >
            <!-- 按题型分组时才显示题型名称编辑与删除类型按钮 -->
            <template v-if="formData.numberMode === 1">
              <el-autocomplete
                v-model="questionType.name"
                :fetch-suggestions="queryQuestionTypeSearch"
                placeholder="输入或选择题型名称"
                style="width: 80%;margin-right: 10px;"
                clearable
              ></el-autocomplete>
              <el-button type="text" @click="handleAddQuestion(questionType)">添加题目</el-button>
              <el-button type="text" @click="formData.paperQuestionTypeDto.splice(qtypeIndex,1)">删除题型</el-button>
            </template>
            <!-- 按加入顺序时：在列表上方只保留“添加题目”按钮，不再编辑题型名称 -->
            <template v-else>
              <el-button type="text" @click="handleAddQuestion(questionType)">添加题目</el-button>
            </template>
            <el-card style="margin-top: 10px;" v-if="questionType.questionDtos.length>0">
              <draggable
                :list="getDisplayQuestions(questionType)"
                tag="div"
                class="question-drag-list"
                :animation="200"
                handle=".question-drag-handle"
                @end="handleQuestionDragEnd($event, questionType)"
              >
                <div
                  v-for="(q, questionIndex) in getDisplayQuestions(questionType)"
                  :key="q.id || questionIndex"
                  class="question-drag-item"
                >
                  <el-form-item :label="getQuestionDisplayLabel(questionType, q, questionIndex)" class="question-item-form">
                    <el-row>
                      <el-col :span="1" class="drag-handle-col">
                        <i class="el-icon-rank question-drag-handle" title="拖拽调整题序"/>
                      </el-col>
                      <el-col :span="20">
                        <div>{{ q.questionTitle }}</div>
                        <span v-for="(option,index) in q.items" :key="index" style="margin-right: 20px;">
                          <span>{{ option.prefix }}</span>
                          <span style="margin: 0 2px"> {{ option.content }}</span>
                        </span>
                      </el-col>
                      <el-col :span="3">
                        <div style="display: flex; align-items: center; justify-content: flex-end; gap: 8px; margin-bottom: 2px;">
                          <span style="font-size: 12px; color: #909399;">
                            {{ hasChildren(questionType, q) ? '原卷(首子题)' : '原卷' }}
                          </span>
                          <el-input-number
                            v-model="originOrderMap[q.id]"
                            size="mini"
                            :disabled="true"
                            :min="1"
                            :max="9999"
                            :controls="true"
                            controls-position="right"
                            style="width: 120px;"
                            placeholder="题号"
                          />
                        </div>
                        <el-popover placement="left" width="420" trigger="click">
                          <div style="max-height: 360px; overflow: auto; line-height: 1.6;">
                            <div style="font-weight: 600; margin-bottom: 6px;">
                              {{ getQuestionTypeLabel(q.questionType) }}（ID: {{ q.id }}）
                            </div>
                          <div
                            style="white-space: pre-wrap; word-break: break-word;"
                            v-html="renderContent(q.questionTitle, q.questionTitleFormat)"
                          ></div>
                            <div v-if="q.items && q.items.length" style="margin-top: 10px;">
                              <div style="font-weight: 600; margin-bottom: 6px;">选项</div>
                              <div v-for="(op, opIndex) in q.items" :key="opIndex" style="margin-bottom: 4px;">
                                <span style="display: inline-block; width: 22px; font-weight: 600;">{{ op.prefix }}</span>
                                <span
                                  style="white-space: pre-wrap; word-break: break-word;"
                                  v-html="renderContent(op.content, q.optionFormat)"
                                ></span>
                              </div>
                            </div>
                            <div v-if="q.questionType === 6" style="margin-top: 10px;">
                              <div style="font-weight: 600; margin-bottom: 6px;">完形填空</div>
                              <div>子题数量：{{ getClozeChildCount(questionType, q) }}</div>
                            </div>
                          </div>
                          <el-button slot="reference" type="text">题目概览</el-button>
                        </el-popover>
                        <el-button type="text" @click="handleAdjustQuestionOrder(questionType, q)">调整题序</el-button>
                        <el-button type="text" @click="removeQuestionWithChildren(questionType, q)">删除题目</el-button>
                      </el-col>
                    </el-row>
                  </el-form-item>
                </div>
              </draggable>
            </el-card>
          </el-form-item>
        </el-col>
        <!--分割线-->
        <el-col :span="24">
          <el-form-item size="large">
            <el-button type="primary" @click="submitForm">提交</el-button>
            <el-button @click="resetForm">重置</el-button>
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              @click="formData.numberMode === 1 ? addQuestionTypeInput() : handleAddQuestionNormal()"
            >{{ formData.numberMode === 1 ? '添加题型' : '添加题目' }}</el-button>

          </el-form-item>
        </el-col>
      </el-form>
    </el-row>

    <!--弹框-->
    <el-dialog v-bind="$attrs" v-on="$listeners" @open="onOpen" @close="onClose" :visible.sync="open"
               title="新增题目">
      <el-row :gutter="15">
        <el-form :model="formData" size="medium" ref="elForm2" label-width="50px">
          <!--<el-col :span="12">-->
          <!--  <el-form-item label="题干" prop="questionTitle">-->
          <!--    <el-input v-model="formData.questionTitle" placeholder="请输入题干" clearable-->
          <!--              :style="{width: '100%'}"></el-input>-->
          <!--  </el-form-item>-->
          <!--</el-col>-->
          <el-col :span="12">
            <el-form-item label="题型" prop="questionType">
              <el-select v-model="questionList.queryParams.questionType" placeholder="请选择题型" clearable
                         :style="{width: '100%'}">
                <el-option v-for="(item, index) in questionTypeOptions" :key="index" :label="item.label"
                           :value="item.value" :disabled="item.disabled"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="科目" prop="id">
              <el-select v-model="questionList.queryParams.subjectId" placeholder="请选择科目" clearable
                         :style="{width: '100%'}">
                <el-option v-for="(item, index) in subjectOptions" :key="index" :label="item.label"
                           :value="item.value" :disabled="item.disabled"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="题干" prop="questionTitle">
              <el-input
                v-model="questionList.queryParams.questionTitle"
                placeholder="请输入题干关键字"
                clearable
                :style="{width: '100%'}"
                @keyup.enter.native="queryQuestionList"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="ID" prop="id">
              <el-input v-model="questionList.queryParams.id" placeholder="请输入ID" clearable
                        :style="{width: '100%'}" @keyup.enter.native="queryQuestionList"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年份">
              <el-input-number
                v-model="questionList.queryParams.examYear"
                :min="2000"
                :max="2100"
                :controls="true"
                controls-position="right"
                placeholder="例如 2024"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批次">
              <el-select
                v-model="questionList.queryParams.examHalf"
                placeholder="上/下半年（可不选）"
                clearable
                :style="{width: '100%'}"
              >
                <el-option :value="1" label="上半年" />
                <el-option :value="2" label="下半年" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="日期">
              <el-date-picker
                v-model="questionList.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="yyyy-MM-dd"
                :style="{width: '100%'}"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-select v-model="questionList.dateOrder" placeholder="按创建时间排序" clearable :style="{width: '100%'}">
                <el-option label="最新在前" value="desc" />
                <el-option label="最旧在前" value="asc" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="插入">
              <el-input-number
                v-model="questionInsertPosition"
                :min="1"
                :max="9999"
                :controls="true"
                controls-position="right"
                style="width: 100%;"
              />
              <div style="margin-top: 4px; color: #909399; font-size: 12px;">
                不填则追加到末尾；填 1 表示插到最前面（以“题目序号/可展示题目”为准，完形子题会跟随父题一起插入）
              </div>
            </el-form-item>
          </el-col>
          <el-col style="margin-bottom: 10px;">
            <el-button type="primary" icon="el-icon-search" size="medium" @click="queryQuestionList">查询</el-button>
            <el-button
              type="success"
              plain
              icon="el-icon-check"
              size="medium"
              :loading="questionList.bulkSelecting"
              @click="selectAllMatchingQuestionsCrossPage"
            >全选跨页结果</el-button>
            <span style="margin-left: 12px; color: #606266; font-size: 13px;">
              已选 <b>{{ (questionList.selectionList || []).length }}</b> 题
            </span>
          </el-col>
        </el-form>
      </el-row>
      <!--表格区域，展示题目，和题型-->
      <el-table
        ref="questionTable"
        v-loading="questionList.loading"
        :data="questionList.tableData"
                @selection-change="handleSelectionChange" border fit highlight-current-row style="width: 100%">
        <el-table-column type="selection" width="40"></el-table-column>
        <el-table-column label="原卷题号" width="120">
          <template v-slot="scope">
            <el-input-number
              v-model="originOrderDraft[scope.row.id]"
              size="mini"
              :disabled="true"
              :min="1"
              :max="9999"
              :controls="true"
              controls-position="right"
              style="width: 100%;"
            />
            <div v-if="scope.row.questionType === 6" style="font-size: 12px; color: #909399; margin-top: 2px;">
              填首个子题题号
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="Id" width="60px"/>
        <el-table-column label="科目" width="70px">
          <template v-slot="scope">
            {{ getSubjectLabel(scope.row.subjectId) }}
          </template>
        </el-table-column>
        <el-table-column label="题型" width="70px">
          <template v-slot="scope">
            {{ getQuestionTypeLabel(scope.row.questionType) }}
          </template>
        </el-table-column>
        <el-table-column label="年份" width="72" align="center">
          <template v-slot="scope">
            <span v-if="scope.row.examYear">{{ scope.row.examYear }}</span>
            <span v-else style="color:#909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="批次" width="88" align="center">
          <template v-slot="scope">
            {{ getExamHalfLabel(scope.row.examHalf) }}
          </template>
        </el-table-column>
        <el-table-column prop="questionTitle" label="题干" show-overflow-tooltip/>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template v-slot="scope">
            {{ scope.row.createTime }}
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; text-align: right;">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :current-page="questionList.queryParams.pageNum"
          :page-size="questionList.queryParams.pageSize"
          :page-sizes="[5,10,20,50]"
          :total="questionList.total"
          @size-change="handleQuestionPageSizeChange"
          @current-change="handleQuestionPageChange"
        />
      </div>
      <div slot="footer">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" @click="addQuestionConfirm">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="自动组卷" :visible.sync="autoCompose.visible" width="600px" @close="closeAutoComposeDialog">
      <el-form label-width="90px" size="small">
        <el-form-item label="科目">
          <el-select v-model="autoCompose.form.subjectId" placeholder="请选择科目" :style="{width: '100%'}">
            <el-option v-for="(item,index) in subjectIdOptions" :key="index" :label="item.label"
                       :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="规则配置">
          <div class="auto-rule-row" v-for="(rule,idx) in autoCompose.form.rules" :key="idx">
            <el-input class="rule-name" v-model="rule.name" placeholder="分区名称(可选)"></el-input>
            <el-select class="rule-type" v-model="rule.questionType" placeholder="题型">
              <el-option v-for="(item,index) in questionTypeOptions" :key="index" :label="item.label"
                         :value="item.value"></el-option>
            </el-select>
            <el-input-number class="rule-count" v-model="rule.questionCount" :min="1" :max="50"
                             controls-position="right"></el-input-number>
            <el-button type="text" @click="removeAutoRule(idx)" v-if="autoCompose.form.rules.length > 1">删除</el-button>
          </div>
          <el-button type="text" icon="el-icon-plus" @click="addAutoRule">新增规则</el-button>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeAutoComposeDialog">取 消</el-button>
        <el-button type="primary" :loading="autoCompose.loading" @click="handleAutoComposeConfirm">生成题目</el-button>
      </div>
    </el-dialog>

    <el-dialog title="按日期自动组卷" :visible.sync="autoComposeByDate.visible" width="500px"
               @close="closeAutoComposeByDateDialog">
      <el-form label-width="90px" size="small">
        <el-form-item label="科目">
          <el-select v-model="autoComposeByDate.form.subjectId" placeholder="请选择科目" :style="{width: '100%'}">
            <el-option v-for="(item,index) in subjectIdOptions" :key="index" :label="item.label"
                       :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="autoComposeByDate.form.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            :style="{width: '100%'}">
          </el-date-picker>
        </el-form-item>
        <el-alert
          title="系统会按题型（单选、多选等）自动分组，把该日期范围内的所有题目加入试卷，可在生成后继续微调。"
          type="info"
          :closable="false"
        />
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeAutoComposeByDateDialog">取 消</el-button>
        <el-button type="primary" :loading="autoComposeByDate.loading" @click="handleAutoComposeByDateConfirm">
          生成题目
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {getClozeQuestion, listQuestion} from "@/api/quiz/question";
import {addPaper, getPaper, updatePaper, autoComposePaper, autoComposePaperByDate} from "@/api/quiz/paper";
import {optionSubject} from "@/api/quiz/subject";
import draggable from 'vuedraggable';
import DOMPurify from 'dompurify';
import { marked } from 'marked';

export default {
  components: { draggable },
  props: [],
  data() {
    return {
      subjectMap: {},
      questionTypeMap: {
        1: '单选题',
        2: '多选题',
        3: '主观题',
        4: '判断题',
        5: '填空题',
        6: '完形填空题'
      },
      questionTypeSuggestions: [
        { value: '单选题' },
        { value: '多选题' },
        { value: '主观题' },
        { value: '判断题' },
        { value: '填空题' },
        { value: '完形填空题（父题+子题）' }
      ],
      // 控制新增题目的弹框显示
      open: false,
      // 当前题型，用于指定给哪个题型push题目
      currQuestionType: {},
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
      subjectOptions: [],
      questionList: {
        selectionList: [],
        queryParams: {
          id: undefined,
          questionTitle: undefined,
          questionType: undefined,
          subjectId: undefined,
          examYear: undefined,
          examHalf: undefined,
          pageNum: 1,
          pageSize: 5
        },
        dateRange: [],
        dateOrder: 'desc',
        tableData: [],
        total: 0,
        loading: undefined,
        /** 程序化同步表格勾选时忽略 selection-change，避免冲掉跨页已选 */
        selectionSyncing: false,
        /** 跨页全选请求中 */
        bulkSelecting: false,
      },
      // 新增题目时：插入到第几个（从1开始），为空则追加到末尾
      questionInsertPosition: null,
      // 原卷题号（仅前端展示/排序使用，不提交后端）
      originOrderMap: {},
      // 新增题目弹框里临时编辑用的“原卷题号”
      originOrderDraft: {},

      formData: {
        paperId: undefined,
        paperType: undefined,
        subjectId: undefined,
        paperName: undefined,
        suggestTime: undefined,
        // 未选择时间时用 null，避免后端解析空字符串失败
        startTime: null,
        endTime: null,
        enableAntiCheat: false,
        // 题号规则：1=按题型分组编号，2=按加入顺序全局编号（默认2）
        numberMode: 2,
        score: 0,
        questionCount: 0,
        paperQuestionTypeDto: [
          // {
          //   name: '单选题',
          //   //该题型下的所有题目
          //   questionDtos: [
          //     {
          //       id:1,
          //       questionTitle: '测试题目标题',
          //       items: [
          //         {prefix: "A", content: "苹果"},
          //         {prefix: "B", content: "香蕉"},
          //         {prefix: "C", content: "葡萄"},
          //       ],
          //       score:4
          //     },
          //   ]
          //
          // }
        ]
      },
      rules: {
        subjectId: [{
          required: true,
          message: '请输入科目',
          trigger: 'change'
        }],
        paperName: [
          {
            required: true,
            message: '请输入试卷名',
            trigger: 'blur',

          },
        ],
        suggestTime: [{
          required: true,
          message: '请输入考试时长',
          trigger: 'blur'
        }, {type: 'number', message: '必须是数字', trigger: 'blur'},],
        paperType: [{
          required: true,
          message: '请输入试卷类型',
          trigger: 'blur'
        }],
        questionTypeName: [
          {
            required: true, message: '请输入试卷类型', trigger: 'blur'
          }
        ]


      },
      paperTypeOptions: [{
        "label": "真题",
        "value": 1
      }, {
        "label": "模拟题",
        "value": 2
      }
      ],
      subjectIdOptions: [],
      autoCompose: {
        visible: false,
        loading: false,
        form: {
          subjectId: undefined,
          rules: [{
            name: '',
            questionType: 1,
            questionCount: 5
          }]
        }
      },
      autoComposeByDate: {
        visible: false,
        loading: false,
        form: {
          subjectId: undefined,
          dateRange: []
        }
      }
    }
  },
  computed: {},
  watch: {
    'formData.subjectId'(val) {
      this.autoCompose.form.subjectId = val
      this.autoComposeByDate.form.subjectId = val
    },
    'formData.numberMode'(val, oldVal) {
      // 统一为数字，避免接口/控件返回字符串导致分支判断失效
      let mode = Number(val)
      if (Number.isNaN(mode)) mode = 2
      if (this.formData.numberMode !== mode) {
        this.formData.numberMode = mode
      }
      // 仅在「进入」非按题型分组（2/3）时合并一次，避免重复触发或反复合并
      if ((mode === 2 || mode === 3) && !(Number(oldVal) === 2 || Number(oldVal) === 3)) {
        this.mergeQuestionTypesAsFlatList()
      }
    }
  },
  async created() {
    await this.loadSubjectOptions();
    let paperId = this.$route.query.paperId
    if (paperId) {
      this.getPaperById(paperId)
    } else {
      // 新建试卷进入页先创建占位块（numberMode=2 下只有一个占位块，名称仅用于页面展示）
      this.ensureDefaultQuestionBlock()
    }
  },
  mounted() {
  },
  methods: {
    getQuestionDisplayLabel(questionType, q, questionIndex) {
      const no = this.getOriginDisplayNo(questionType, q, questionIndex)
      return `题目${no}：`
    },
    // 严格按原卷题序显示：有 originOrder 就用它（允许跳号）；复合题显示范围
    // fallback：没有 originOrder 时显示“无”
    /** 题目对象上的原卷题号（后端字段 originOrder；兼容 originalOrder） */
    getStrictOriginOrder(q) {
      if (!q) return null
      const raw =
        q.originOrder !== undefined && q.originOrder !== null && q.originOrder !== ''
          ? q.originOrder
          : (q.originalOrder !== undefined && q.originalOrder !== null && q.originalOrder !== ''
            ? q.originalOrder
            : null)
      if (raw === '' || raw === null || raw === undefined) return null
      const n = Number(raw)
      if (!Number.isFinite(n) || n <= 0) return null
      return Math.floor(n)
    },
    getOriginDisplayNo(questionType, q, questionIndex) {
      if (!q) return '无'
      // 子题不展示（getDisplayQuestions 已过滤），这里只处理父题/普通题
      const n = this.getStrictOriginOrder(q)
      if (n != null) {
        const start = n
        const span = Math.max(1, Number(this.estimateOriginSpan(questionType, q)) || 1)
        if (span > 1) return `${start}-${start + span - 1}`
        return String(start)
      }
      return '无'
    },
    hasChildren(questionType, parentQuestion) {
      if (!questionType || !parentQuestion || !parentQuestion.id) return false
      const list = questionType.questionDtos || []
      return list.some(x => x && x.parentId === parentQuestion.id)
    },
    // 估算“这道题在原卷里占用了多少个题号”
    // - 普通题固定占 1
    // - 复合题父题（有子题）按子题数量占号（原卷一般从首子题开始编号）
    estimateOriginSpan(questionType, q) {
      if (!q) return 1
      // 子题本身不单独计号（跟随父题）
      if (q.parentId) return 0

      // 只要存在子题（不局限完形题型），按子题数占号
      if (this.hasChildren(questionType, q)) {
        const list = questionType.questionDtos || []
        const n = list.filter(x => x && x.parentId === q.id).length
        return Math.max(1, Number(n) || 1)
      }
      return 1
    },
    // 智能续号：按“当前展示顺序”或“按原卷顺序”后的顺序，给后续未填写的原卷题号自动顺延
    // 规则：只对“可展示题目”（非子题）生效；完形父题按子题数量占号
    autoFillOriginOrders(questionType) {
      if (!questionType) return
      const displayList = this.getDisplayQuestions(questionType)
      if (!Array.isArray(displayList) || !displayList.length) return

      // 从左到右扫描：遇到已填写题号就作为锚点；后续空缺按“占号数”顺延
      let lastNumber = null
      let lastSpan = 1
      let filled = 0
      for (const q of displayList) {
        if (!q || q.id === undefined || q.id === null) continue
        const raw = this.originOrderMap[q.id]
        const n = raw === '' || raw === null || raw === undefined ? null : Number(raw)
        const has = Number.isFinite(n) && n > 0

        if (has) {
          lastNumber = Math.floor(n)
          lastSpan = this.estimateOriginSpan(questionType, q) || 1
          continue
        }
        if (lastNumber === null) {
          // 还没有锚点，跳过（避免乱填）
          continue
        }
        const next = lastNumber + Math.max(1, Number(lastSpan) || 1)
        this.$set(this.originOrderMap, q.id, next)
        filled++
        lastNumber = next
        lastSpan = this.estimateOriginSpan(questionType, q) || 1
      }

      if (!filled) {
        this.$message.warning('未能自动续号：请先给第一道（或任意一道）题填一个原卷题号作为锚点。')
      } else {
        this.$message.success(`已智能续号 ${filled} 道题（小题数来自题干文本；图片内小题无法识别）`)
      }
    },
    sortQuestionsByOriginOrder(questionType) {
      if (!questionType) return
      const displayList = this.getDisplayQuestions(questionType)
      if (!Array.isArray(displayList) || !displayList.length) return

      const withIndex = displayList.map((q, idx) => {
        // 优先用 originOrderMap（新增/展示过程只保证 map 被正确设置），否则再回退到题目对象字段
        const nFromObj = this.getStrictOriginOrder(q)
        const rawFromMap = (q && q.id != null) ? this.originOrderMap[q.id] : null
        const nFromMap = rawFromMap === '' || rawFromMap === null || rawFromMap === undefined ? null : Number(rawFromMap)

        const n = nFromObj != null ? nFromObj
          : (Number.isFinite(nFromMap) && nFromMap > 0 ? Math.floor(nFromMap) : null)

        const key = n != null ? n : Number.POSITIVE_INFINITY
        return { q, idx, key }
      })
      withIndex.sort((a, b) => {
        if (a.key !== b.key) return a.key - b.key
        return a.idx - b.idx
      })
      const newOrder = withIndex.map(x => x.q)
      this.applyDisplayOrder(questionType, newOrder)
    },
    getClozeChildCount(questionType, parentQuestion) {
      if (!questionType || !parentQuestion || !parentQuestion.id) return 0
      const list = questionType.questionDtos || []
      return list.filter(x => x && x.parentId === parentQuestion.id).length
    },
    /** 渲染内容（支持HTML和Markdown），格式取决于存储时的 format 字段 */
    renderContent(content, format) {
      if (!content) return '';
      const contentFormat = format || 'html';
      if (contentFormat === 'markdown') {
        try {
          let html = marked(content);
          // 处理表格，添加边框样式
          html = html.replace(/<table([^>]*)>/gi, (match, attrs) => {
            return `<table${attrs} style="border-collapse: collapse; width: 100%; margin: 10px 0; border: 1px solid #dcdfe6;">`;
          });
          html = html.replace(/<th([^>]*)>/gi, (match, attrs) => {
            return `<th${attrs} style="border: 1px solid #dcdfe6; padding: 8px 12px; text-align: left; background-color: #f5f7fa; font-weight: 600;">`;
          });
          html = html.replace(/<td([^>]*)>/gi, (match, attrs) => {
            return `<td${attrs} style="border: 1px solid #dcdfe6; padding: 8px 12px; text-align: left;">`;
          });
          return DOMPurify.sanitize(html, {
            ALLOWED_TAGS: ['p', 'br', 'strong', 'em', 'u', 'ol', 'ul', 'li', 'img', 'a', 'span', 'div', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'blockquote', 'code', 'pre', 'table', 'thead', 'tbody', 'tr', 'th', 'td'],
            ALLOWED_ATTR: ['href', 'src', 'alt', 'title', 'class', 'style', 'target'],
            ALLOW_DATA_ATTR: false
          });
        } catch (error) {
          console.error('Markdown渲染失败:', error);
          return this.sanitizeHtml(String(content).replace(/\n/g, '<br>'));
        }
      } else {
        return this.sanitizeHtml(content);
      }
    },
    /** HTML安全处理 */
    sanitizeHtml(html) {
      if (!html) return '';
      const str = String(html);
      if (!/<[^>]+>/.test(str)) {
        return str;
      }
      return DOMPurify.sanitize(str, {
        ALLOWED_TAGS: ['p', 'br', 'strong', 'em', 'u', 'ol', 'ul', 'li', 'img', 'a', 'span', 'div', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'blockquote', 'code', 'pre', 'table', 'thead', 'tbody', 'tr', 'th', 'td'],
        ALLOWED_ATTR: ['href', 'src', 'alt', 'title', 'class', 'style', 'target'],
        ALLOW_DATA_ATTR: false
      });
    },
    showValidatePopup(fields) {
      const fieldMap = fields || {};
      const keys = Object.keys(fieldMap);
      if (!keys.length) {
        return this.$alert('请完善表单必填项后再提交。', '校验未通过', { type: 'warning' });
      }

      const messages = [];
      keys.forEach((k) => {
        const arr = fieldMap[k];
        if (Array.isArray(arr) && arr.length) {
          const msg = arr[0] && arr[0].message ? String(arr[0].message) : '';
          if (msg) messages.push(msg);
        }
      });
      const uniq = [...new Set(messages)].slice(0, 8);
      const html = uniq.length ? uniq.map(m => `- ${m}`).join('<br/>') : '请完善表单必填项后再提交。';

      return this.$alert(html, '校验未通过', {
        type: 'warning',
        dangerouslyUseHTMLString: true,
        confirmButtonText: '知道了'
      });
    },
    normalizeInsertPosition(pos) {
      if (pos === null || pos === undefined || pos === '') return null
      const n = Number(pos)
      if (!Number.isFinite(n)) return null
      const i = Math.floor(n)
      return i >= 1 ? i : 1
    },
    // 根据“可展示题目”的第 N 个（从1开始），计算在 questionDtos 里的真实插入下标
    getRawInsertIndexByDisplayPosition(questionType, displayPos) {
      if (!questionType || !Array.isArray(questionType.questionDtos)) return 0
      const normalized = this.normalizeInsertPosition(displayPos)
      if (!normalized) return questionType.questionDtos.length
      if (normalized === 1) return 0
      let displayCount = 0
      const dtos = questionType.questionDtos
      for (let rawIndex = 0; rawIndex < dtos.length; rawIndex++) {
        const q = dtos[rawIndex]
        if (!q || q.parentId) continue
        displayCount++
        // displayCount 已经是当前 rawIndex 对应的“第 displayCount 个可展示题目”
        // 要插入到第 normalized 个 => 插入点应在第 normalized-1 个之后
        if (displayCount === normalized - 1) {
          return rawIndex + 1
        }
      }
      // 超过当前长度 => 追加
      return dtos.length
    },
    insertQuestionsAtPosition(questionType, rawIndex, questionsToInsert) {
      if (!questionType) return
      if (!Array.isArray(questionType.questionDtos)) {
        this.$set(questionType, 'questionDtos', [])
      }
      const list = questionType.questionDtos
      const idx = Math.max(0, Math.min(Number(rawIndex) || 0, list.length))
      list.splice(idx, 0, ...(questionsToInsert || []))
    },
    async handleAdjustQuestionOrder(questionType, question) {
      if (!questionType || !question || question.parentId) return
      const displayList = this.getDisplayQuestions(questionType)
      const currIndex = displayList.findIndex(x => x && x.id === question.id)
      if (currIndex < 0) return
      const max = displayList.length
      try {
        const { value } = await this.$prompt(`请输入要调整到第几题（1-${max}）`, '调整题序', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputValue: String(currIndex + 1),
          inputPattern: /^[1-9]\d*$/,
          inputErrorMessage: '请输入正整数'
        })
        const target = Math.max(1, Math.min(max, Math.floor(Number(value) || 1)))
        if (target === currIndex + 1) return
        const newOrder = [...displayList]
        const [moved] = newOrder.splice(currIndex, 1)
        newOrder.splice(target - 1, 0, moved)
        this.applyDisplayOrder(questionType, newOrder)
      } catch (e) {
        // 用户取消
      }
    },
    queryQuestionTypeSearch(queryString, cb) {
      const suggestions = this.questionTypeSuggestions;
      const results = queryString ? suggestions.filter(item => {
        return item.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0;
      }) : suggestions;
      cb(results);
    },
    async loadSubjectOptions() {
      const res = await optionSubject();
      const list = res.data || [];
      const options = list.map(item => ({
        label: item.subjectName,
        value: item.subjectId
      }));
      this.subjectOptions = options;
      this.subjectIdOptions = options;
      this.subjectMap = list.reduce((acc, cur) => {
        acc[cur.subjectId] = cur.subjectName;
        return acc;
      }, {});
    },
    async getPaperById(paperId) {
      this.formData = (await getPaper(paperId)).data
      console.log(this.formData)
      let nm = Number(this.formData.numberMode)
      if (Number.isNaN(nm)) nm = 2
      this.formData.numberMode = nm
      // 非按题型分组：加载后合并为一块，避免多块题型残留
      if (this.formData.numberMode === 2 || this.formData.numberMode === 3) {
        this.mergeQuestionTypesAsFlatList()
      }
      this.initOriginOrderMapFromPaper()
      // 进入编辑页后按 originOrderMap 重新确保：序号最小的在最上面
      if (this.formData.numberMode === 2 || this.formData.numberMode === 3) {
        const firstType = (this.formData.paperQuestionTypeDto || [])[0]
        if (firstType) this.sortQuestionsByOriginOrder(firstType)
      }
      // 兼容后端可能返回空字符串的情况
      if (this.formData.startTime === '') this.formData.startTime = null
      if (this.formData.endTime === '') this.formData.endTime = null
      this.autoCompose.form.subjectId = this.formData.subjectId
    },
    initOriginOrderMapFromPaper() {
      const map = { ...(this.originOrderMap || {}) }
      const types = this.formData.paperQuestionTypeDto || []
      types.forEach(t => {
        const dtos = (t && t.questionDtos) || []
        dtos.forEach(q => {
          if (!q || q.id === undefined || q.id === null) return
          // 只给“可展示题目”（非完形子题）初始化默认值，避免父题/子题冲突
          if (q.parentId) return
          const fromDb = this.getStrictOriginOrder(q)
          if (fromDb != null) {
            map[q.id] = fromDb
            return
          }
          if (map[q.id] !== undefined && map[q.id] !== null && map[q.id] !== '') return
          // 无题库原卷题号时：用入卷顺序 itemOrder 作为占位，便于微调
          if (q.itemOrder !== undefined && q.itemOrder !== null && q.itemOrder !== '') {
            const n = Number(q.itemOrder)
            map[q.id] = Number.isFinite(n) ? n + 1 : undefined
          }
        })
      })
      this.originOrderMap = map
    },
    getExistingQuestionIds() {
      const ids = new Set();
      (this.formData.paperQuestionTypeDto || []).forEach(section => {
        (section.questionDtos || []).forEach(item => {
          if (item && item.id !== undefined && item.id !== null) {
            ids.add(item.id);
          }
        });
      });
      return ids;
    },
    filterExistingQuestions(rows) {
      if (!Array.isArray(rows) || !rows.length) {
        return [];
      }
      const existingIds = this.getExistingQuestionIds();
      const keyword = (this.questionList.queryParams.questionTitle || '').trim().toLowerCase();
      const filtered = rows.filter(row => {
        if (existingIds.has(row.id)) {
          return false;
        }
        if (keyword) {
          const title = (row.questionTitle || '').toLowerCase();
          return title.includes(keyword);
        }
        // 按日期范围筛选（仅在选择日期时生效，前端过滤）
        const [begin, end] = this.questionList.dateRange || [];
        if (begin && end && row.createTime) {
          const dateStr = String(row.createTime).slice(0, 10);
          if (dateStr < begin || dateStr > end) {
            return false;
          }
        }
        return true;
      });
      // 按创建时间排序：最新在前/最旧在前
      const order = this.questionList.dateOrder || 'desc';
      filtered.sort((a, b) => {
        const ta = new Date(a.createTime || 0).getTime();
        const tb = new Date(b.createTime || 0).getTime();
        return order === 'asc' ? ta - tb : tb - ta;
      });
      return filtered;
    },
    async getQuestList() {
      this.questionList.loading = true;
      try {
        const params = {
          ...this.questionList.queryParams
        };
        const response = await listQuestion(params);
        const filteredRows = this.filterExistingQuestions(response.rows || []);
        this.questionList.tableData = filteredRows;
        // 弹框“原卷题号”仅展示：优先从题库题目 originOrder 回填到 draft，避免出现 idx+1 或空值
        this.syncOriginOrderDraftFromRows(filteredRows)
        // 使用后端分页总数，保证分页组件正常工作
        this.questionList.total = Number(response.total) || filteredRows.length;
        this.$nextTick(() => {
          this.restoreQuestionTableSelection();
        });
      } finally {
        this.questionList.loading = false;
      }
    },
    syncOriginOrderDraftFromRows(rows) {
      if (!Array.isArray(rows) || !rows.length) return
      if (!this.originOrderDraft) this.originOrderDraft = {}
      for (const r of rows) {
        if (!r || r.id === undefined || r.id === null) continue
        const existed = this.originOrderDraft[r.id]
        if (!(existed === undefined || existed === null || existed === '')) {
          continue
        }
        const dbRaw = r.originOrder !== undefined ? r.originOrder : r.originalOrder
        const n = dbRaw === '' || dbRaw === null || dbRaw === undefined ? null : Number(dbRaw)
        if (Number.isFinite(n) && n > 0) {
          this.$set(this.originOrderDraft, r.id, Math.floor(n))
        }
      }
    },
    /** 按当前查询条件分页拉取全部题目，合并去重后写入已选（跨页全选） */
    async selectAllMatchingQuestionsCrossPage() {
      const base = { ...this.questionList.queryParams };
      const fetchSize = 100;
      this.questionList.bulkSelecting = true;
      this.questionList.loading = true;
      try {
        let pageNum = 1;
        const allRows = [];
        let total = null;
        for (;;) {
          const response = await listQuestion({ ...base, pageNum, pageSize: fetchSize });
          if (total === null) {
            total = Number(response.total) || 0;
          }
          const rows = response.rows || [];
          if (!rows.length) {
            break;
          }
          allRows.push(...rows);
          if (allRows.length >= total || rows.length < fetchSize) {
            break;
          }
          pageNum += 1;
          if (pageNum > 500) {
            break;
          }
        }
        const filtered = this.filterExistingQuestions(allRows);
        const seen = new Set();
        const unique = [];
        for (const r of filtered) {
          if (!r || r.id == null) {
            continue;
          }
          if (seen.has(r.id)) {
            continue;
          }
          seen.add(r.id);
          unique.push(r);
        }
        this.questionList.selectionList = unique;
        this.$message.success(`已选中 ${unique.length} 道题目（当前查询条件下跨页全选）`);
        await this.$nextTick();
        this.restoreQuestionTableSelection();
      } finally {
        this.questionList.bulkSelecting = false;
        this.questionList.loading = false;
      }
    },
    /** 根据 selectionList 恢复当前页的勾选状态 */
    restoreQuestionTableSelection() {
      const table = this.$refs.questionTable;
      if (!table) {
        return;
      }
      this.questionList.selectionSyncing = true;
      this.$nextTick(() => {
        table.clearSelection();
        const selectedIds = new Set(
          (this.questionList.selectionList || [])
            .map(r => r && r.id)
            .filter(id => id != null)
        );
        (this.questionList.tableData || []).forEach(row => {
          if (row && selectedIds.has(row.id)) {
            table.toggleRowSelection(row, true);
          }
        });
        this.$nextTick(() => {
          this.questionList.selectionSyncing = false;
        });
      });
    },
    queryQuestionList() {
      this.questionList.queryParams.pageNum = 1;
      this.questionList.selectionList = [];
      this.getQuestList();
    },
    handleQuestionPageSizeChange(size) {
      this.questionList.queryParams.pageSize = size;
      this.questionList.queryParams.pageNum = 1;
      this.getQuestList();
    },
    handleQuestionPageChange(page) {
      this.questionList.queryParams.pageNum = page;
      this.getQuestList();
    },
    getSubjectLabel(id) {
      return this.subjectMap[id] || '未知科目'; // 默认值
    },
    getQuestionTypeLabel(type) {
      return this.questionTypeMap[type] || '未知题型'; // 默认值
    },
    getExamHalfLabel(h) {
      if (h === 1) return '上半年';
      if (h === 2) return '下半年';
      return '-';
    },
    // 组卷页面展示用：隐藏完形子题，只展示父题或普通题
    getDisplayQuestions(questionType) {
      if (!questionType || !Array.isArray(questionType.questionDtos)) {
        return [];
      }
      return questionType.questionDtos.filter(q => !q || !q.parentId);
    },
    // 拖拽题目结束：根据新顺序重写 questionDtos（保持完形子题紧跟父题）
    handleQuestionDragEnd(evt, questionType) {
      if (evt.oldIndex === evt.newIndex || !questionType || !Array.isArray(questionType.questionDtos)) {
        return;
      }
      const displayList = this.getDisplayQuestions(questionType);
      const newOrder = [...displayList];
      const [removed] = newOrder.splice(evt.oldIndex, 1);
      newOrder.splice(evt.newIndex, 0, removed);
      this.applyDisplayOrder(questionType, newOrder);
    },
    // 按新的“可展示题目”顺序重排 questionDtos，完形子题仍紧跟其父题
    applyDisplayOrder(questionType, newDisplayOrder) {
      if (!questionType || !Array.isArray(newDisplayOrder)) {
        return;
      }
      const newDtos = [];
      for (const q of newDisplayOrder) {
        if (!q) continue;
        newDtos.push(q);
        const children = (questionType.questionDtos || []).filter(x => x && x.parentId === q.id);
        newDtos.push(...children);
      }
      questionType.questionDtos = newDtos;
    },
    // 删除题目时：如果是完形父题，则连同其子题一并删除
    removeQuestionWithChildren(questionType, question) {
      if (!questionType || !Array.isArray(questionType.questionDtos)) {
        return;
      }
      const targetId = question && question.id;
      if (!targetId) {
        // 无ID的题目，退回到原来的按引用删除
        const idx = questionType.questionDtos.indexOf(question);
        if (idx >= 0) {
          questionType.questionDtos.splice(idx, 1);
        }
        // 若“题库选择弹窗”正打开，需要刷新列表（让被移除的题重新可选）
        if (this.open) {
          this.questionList.queryParams.pageNum = 1;
          this.getQuestList();
        }
        return;
      }
      questionType.questionDtos = questionType.questionDtos.filter(q => {
        if (!q) return false;
        if (q.id === targetId) return false;
        if (q.parentId === targetId) return false;
        return true;
      });
      // 若“题库选择弹窗”正打开，需要刷新列表（让被移除的题重新可选）
      if (this.open) {
        this.questionList.queryParams.pageNum = 1;
        this.getQuestList();
      }
    },
    // 将多个题型下的题目扁平为单一“题目列表”（用于按加入顺序展示）
    mergeQuestionTypesAsFlatList() {
      const list = this.formData.paperQuestionTypeDto || []
      const firstName = (list || []).find(t => t && t.name && String(t.name).trim())?.name
      const allQuestions = []
      const seenId = new Set()
      list.forEach(t => {
        (t.questionDtos || []).forEach(q => {
          if (!q) return
          if (q.id != null) {
            if (seenId.has(q.id)) return
            seenId.add(q.id)
          }
          allQuestions.push(q)
        })
      })
      this.formData.paperQuestionTypeDto = [{
        // numberMode=2 下只有一个“占位块”，名称用于页面展示，不影响真实排序逻辑
        name: firstName || '单选题',
        questionDtos: allQuestions
      }]
    },
    // 合并当前页勾选与其它页已选，支持跨页保留勾选
    handleSelectionChange(val) {
      if (this.questionList.selectionSyncing) {
        return;
      }
      const pageRows = this.questionList.tableData || [];
      const currentPageIds = new Set(
        pageRows.map(r => r && r.id).filter(id => id != null)
      );
      const prev = this.questionList.selectionList || [];
      const kept = prev.filter(r => r && !currentPageIds.has(r.id));
      const picked = (val || []).filter(r => r);
      this.questionList.selectionList = [...kept, ...picked];
    },
    onOpen() {
    },
    onClose() {
      this.$refs['elForm2'].resetFields()
      this.questionInsertPosition = null
      this.originOrderDraft = {}
      this.questionList.selectionList = []
    },
    close() {
      this.open = false
    },
    async addQuestionConfirm() {
      if (!this.questionList.selectionList || !this.questionList.selectionList.length) {
        this.$message.warning('请先勾选题目，或使用「全选跨页结果」');
        return;
      }
      const existingIds = this.getExistingQuestionIds()
      // 插入位置：以“可展示题目”的序号为准（忽略完形子题），为空则追加到末尾
      const displayPos = this.normalizeInsertPosition(this.questionInsertPosition)
      let rawInsertIndex = this.getRawInsertIndexByDisplayPosition(this.currQuestionType, displayPos)
      for (const q of this.questionList.selectionList) {
        if (!q || existingIds.has(q.id)) {
          continue
        }
        const isClozeParent = Number(q.questionType) === 6
        // 题库列表已含题干、原卷题号等，无需再逐题 getQuestion；完形仅请求 cloze 接口拿父题详情与子题
        if (isClozeParent) {
          const clozeRes = await getClozeQuestion(q.id)
          if (clozeRes.code !== 200 || !clozeRes.data) {
            continue
          }
          const data = clozeRes.data.parent
          if (!data || existingIds.has(data.id)) {
            continue
          }
          const toInsert = [data]
          const children = clozeRes.data.children || []
          children.forEach(child => {
            if (!child || existingIds.has(child.id)) return
            toInsert.push(child)
          })
          this.insertQuestionsAtPosition(this.currQuestionType, rawInsertIndex, toInsert)
          toInsert.forEach(item => item && item.id && existingIds.add(item.id))
          // 原卷题号：优先读取题库题目 originOrder（只做展示，避免覆盖你提前定义的顺序）
          if (data && data.id) {
            const draft = this.originOrderDraft ? this.originOrderDraft[data.id] : undefined
            const n = draft === '' || draft === null || draft === undefined ? null : Number(draft)
            const dbRaw = data.originOrder !== undefined ? data.originOrder : data.originalOrder
            const dbN = dbRaw === '' || dbRaw === null || dbRaw === undefined ? null : Number(dbRaw)

            // 试卷编辑页只做展示：优先读取题库题目的 originOrder，避免用 idx+1 兜底覆盖你提前定义的顺序
            if (Number.isFinite(dbN) && dbN > 0) {
              this.$set(this.originOrderMap, data.id, Math.floor(dbN))
            } else if (Number.isFinite(n) && n > 0) {
              this.$set(this.originOrderMap, data.id, Math.floor(n))
            } else if (this.originOrderMap[data.id] === undefined || this.originOrderMap[data.id] === null || this.originOrderMap[data.id] === '') {
              const displayListAfter = this.getDisplayQuestions(this.currQuestionType)
              const idx = displayListAfter.findIndex(x => x && x.id === data.id)
              if (idx >= 0) this.$set(this.originOrderMap, data.id, idx + 1)
            }
          }
          rawInsertIndex += toInsert.length
        } else {
          const data = { ...q }
          if (!data.id || existingIds.has(data.id)) {
            continue
          }
          this.insertQuestionsAtPosition(this.currQuestionType, rawInsertIndex, [data])
          existingIds.add(data.id)
          if (data && data.id) {
            const draft = this.originOrderDraft ? this.originOrderDraft[data.id] : undefined
            const n = draft === '' || draft === null || draft === undefined ? null : Number(draft)
            const dbRaw = data.originOrder !== undefined ? data.originOrder : data.originalOrder
            const dbN = dbRaw === '' || dbRaw === null || dbRaw === undefined ? null : Number(dbRaw)

            // 试卷编辑页只做展示：优先读取题库题目的 originOrder，避免用 idx+1 兜底覆盖你提前定义的顺序
            if (Number.isFinite(dbN) && dbN > 0) {
              this.$set(this.originOrderMap, data.id, Math.floor(dbN))
            } else if (Number.isFinite(n) && n > 0) {
              this.$set(this.originOrderMap, data.id, Math.floor(n))
            } else if (this.originOrderMap[data.id] === undefined || this.originOrderMap[data.id] === null || this.originOrderMap[data.id] === '') {
              const displayListAfter = this.getDisplayQuestions(this.currQuestionType)
              const idx = displayListAfter.findIndex(x => x && x.id === data.id)
              if (idx >= 0) this.$set(this.originOrderMap, data.id, idx + 1)
            }
          }
          rawInsertIndex += 1
        }
      }
      // 按原卷题号展示需求：保证序号最小的在最上面（只重排 questionDtos 顺序，不覆盖题号值）
      if (this.formData && (Number(this.formData.numberMode) === 2 || Number(this.formData.numberMode) === 3) && this.currQuestionType) {
        this.sortQuestionsByOriginOrder(this.currQuestionType)
      }
      this.close()
    },
    // 点击新增时显示弹窗和加载题目表，也就是题目列表那些
    handleAddQuestion(questionType) {
      this.open = true
      this.questionInsertPosition = null
      this.questionList.selectionList = []
      // 拷贝一份当前的题型，标识给哪个题型加题目
      this.currQuestionType = questionType
      // 如果当前题型块还是空的（还没“第一题”），默认插到最前面
      const displayLen = this.getDisplayQuestions(questionType).length
      if (displayLen === 0) {
        this.questionInsertPosition = 1
      }
      this.questionList.loading = true
      // 初始化弹框内的“原卷题号草稿”为当前已记录的值
      this.originOrderDraft = { ...(this.originOrderMap || {}) }
      if (!this.questionList.queryParams.subjectId && this.formData.subjectId) {
        this.questionList.queryParams.subjectId = this.formData.subjectId
      }
      if (questionType && questionType.questionType && !this.questionList.queryParams.questionType) {
        this.questionList.queryParams.questionType = questionType.questionType
      }
      this.getQuestList()
    },
    /** 保证至少有一个题型块，便于从底部「添加题目」打开题库弹窗 */
    ensureDefaultQuestionBlock() {
      const list = this.formData.paperQuestionTypeDto
      if (list && list.length) {
        return
      }
      this.$set(this.formData, 'paperQuestionTypeDto', [{
        name: this.formData.numberMode === 1 ? '题型1' : '单选题',
        questionDtos: []
      }])
    },
    /** 底部主按钮：不新增题型块，只向当前（或首个）题目列表加题（题号规则为「按加入顺序」时使用） */
    handleAddQuestionNormal() {
      this.ensureDefaultQuestionBlock()
      const first = this.formData.paperQuestionTypeDto[0]
      this.handleAddQuestion(first)
    },
    /** 题号规则为「按题型分组」时：底部按钮新增一个空题型块 */
    addQuestionTypeInput() {
      this.formData.paperQuestionTypeDto.push({
        name: `题型${(this.formData.paperQuestionTypeDto || []).length + 1}`,
        questionDtos: []
      })
    },
    async submitForm() {
      let noSubmit;
      // 自定义前置空值判断
      this.formData.paperQuestionTypeDto.forEach((questionType, qtypeIndex) => {
        if (!questionType.name) {
          this.$message.error(`题型 ${qtypeIndex + 1} 的题型名不能为空`);
          noSubmit = true;
          return
        }
        if (questionType.questionDtos.length === 0) {
          this.$message.error(`题型 ${qtypeIndex + 1} 里至少得有一道题`);
          noSubmit = true;
        }
      })
      if (noSubmit) return
      // 统一将“未设置时间”归一化为 null，避免传空字符串导致后端解析失败
      if (!this.formData.startTime) this.formData.startTime = null
      if (!this.formData.endTime) this.formData.endTime = null
      if (this.formData.startTime && this.formData.endTime && new Date(this.formData.startTime) >= new Date(this.formData.endTime)) {
        return this.$message.error('开始时间必须早于截止时间')
      }
      const valid = await new Promise((resolve) => {
        this.$refs['elForm1'].validate((ok, fields) => {
          if (!ok) {
            this.showValidatePopup(fields)
          }
          resolve(ok)
        })
      })
      if (!valid) return
      // 判断题型是否为空
      if (this.formData.paperQuestionTypeDto.length < 1 || this.formData.paperName.trim() === '') {
        return this.$message.error("信息未填写完毕")
      }
      let res = null
      try {
        if (this.formData.paperId) {
          console.log("是修改")
          res = await updatePaper(this.formData)
        } else {
          res = await addPaper(this.formData)
        }
      } catch (e) {
        const msg = e?.response?.data?.msg || e?.message || '提交失败，请检查开始/截止时间格式或后端日志'
        this.$message.error(msg)
        return
      }

      const message = res.code === 200 ? "操作成功!" : "操作失败,请联系管理员!"
      const type = res.code === 200 ? "success" : "error"
      this.$message({
        message,
        type
      });

      this.$router.push({
        path: '/paper/index'
      })
    },
    resetForm() {
      this.$refs['elForm1'].resetFields()
    },
    openAutoComposeDialog() {
      if (!this.autoCompose.form.subjectId && this.formData.subjectId) {
        this.autoCompose.form.subjectId = this.formData.subjectId
      }
      if (!this.autoCompose.form.rules.length) {
        this.addAutoRule()
      }
      this.autoCompose.visible = true
    },
    closeAutoComposeDialog() {
      this.autoCompose.visible = false
      this.autoCompose.loading = false
    },
    openAutoComposeByDateDialog() {
      if (!this.autoComposeByDate.form.subjectId && this.formData.subjectId) {
        this.autoComposeByDate.form.subjectId = this.formData.subjectId
      }
      if (!this.autoComposeByDate.form.dateRange || !this.autoComposeByDate.form.dateRange.length) {
        const today = this.formatToday()
        this.autoComposeByDate.form.dateRange = [today, today]
      }
      this.autoComposeByDate.visible = true
    },
    closeAutoComposeByDateDialog() {
      this.autoComposeByDate.visible = false
      this.autoComposeByDate.loading = false
    },
    addAutoRule() {
      const defaultType = this.questionTypeOptions.length ? this.questionTypeOptions[0].value : 1
      this.autoCompose.form.rules.push({
        name: '',
        questionType: defaultType,
        questionCount: 5
      })
    },
    removeAutoRule(index) {
      this.autoCompose.form.rules.splice(index, 1)
      if (!this.autoCompose.form.rules.length) {
        this.addAutoRule()
      }
    },
    async handleAutoComposeConfirm() {
      if (!this.autoCompose.form.subjectId) {
        return this.$message.error('请选择科目')
      }
      const payload = {
        subjectId: this.autoCompose.form.subjectId,
        paperName: this.formData.paperName,
        paperType: this.formData.paperType,
        suggestTime: this.formData.suggestTime,
        enableAntiCheat: this.formData.enableAntiCheat,
        rules: this.autoCompose.form.rules.map(rule => ({
          sectionName: rule.name,
          questionType: rule.questionType,
          questionCount: rule.questionCount
        }))
      }
      if (!payload.rules.length) {
        return this.$message.error('请至少配置一条规则')
      }
      const invalidRule = payload.rules.find(rule => !rule.questionType || !rule.questionCount || rule.questionCount <= 0)
      if (invalidRule) {
        return this.$message.error('请完善每条规则的题型与数量')
      }
      this.autoCompose.loading = true
      try {
        const res = await autoComposePaper(payload)
        if (res.code === 200 && res.data) {
          this.formData.paperQuestionTypeDto = res.data.paperQuestionTypeDto || []
          this.formData.score = res.data.score
          this.formData.questionCount = res.data.questionCount
          if (res.data.subjectId) {
            this.formData.subjectId = res.data.subjectId
          }
          this.$message.success('已根据规则自动组卷')
          this.autoCompose.visible = false
        } else {
          this.$message.error(res.msg || '自动组卷失败')
        }
      } finally {
        this.autoCompose.loading = false
      }
    },
    formatToday() {
      const d = new Date()
      const pad = (n) => (n < 10 ? '0' + n : '' + n)
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
    },
    async handleAutoComposeByDateConfirm() {
      if (!this.autoComposeByDate.form.subjectId) {
        return this.$message.error('请选择科目')
      }
      if (!this.autoComposeByDate.form.dateRange || this.autoComposeByDate.form.dateRange.length !== 2) {
        return this.$message.error('请选择日期范围')
      }
      const [beginTime, endTime] = this.autoComposeByDate.form.dateRange
      const payload = {
        subjectId: this.autoComposeByDate.form.subjectId,
        paperName: this.formData.paperName,
        paperType: this.formData.paperType,
        suggestTime: this.formData.suggestTime,
        enableAntiCheat: this.formData.enableAntiCheat,
        beginTime,
        endTime
      }
      this.autoComposeByDate.loading = true
      try {
        const res = await autoComposePaperByDate(payload)
        if (res.code === 200 && res.data) {
          this.formData.paperQuestionTypeDto = res.data.paperQuestionTypeDto || []
          this.formData.score = res.data.score
          this.formData.questionCount = res.data.questionCount
          if (res.data.subjectId) {
            this.formData.subjectId = res.data.subjectId
          }
          // 按日期组卷后端按题型分组返回多块，若当前为“按加入顺序”则合并为一块，避免页面出现多块“题目列表”
          if (this.formData.numberMode === 2) {
            this.mergeQuestionTypesAsFlatList()
          }
          this.$message.success('已按日期范围自动组卷')
          this.autoComposeByDate.visible = false
        } else {
          this.$message.error(res.msg || '按日期自动组卷失败')
        }
      } finally {
        this.autoComposeByDate.loading = false
      }
    }
  }
}

</script>
<style scoped>
.auto-rule-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.auto-rule-row .rule-name {
  flex: 1;
  margin-right: 10px;
}

.auto-rule-row .rule-type {
  width: 120px;
  margin-right: 10px;
}

.auto-rule-row .rule-count {
  width: 120px;
  margin-right: 10px;
}

.mt-2 {
  margin-top: 10px;
}

.mb-2 {
  margin-bottom: 10px;
}

.question-drag-list {
  min-height: 8px;
}

.question-drag-item {
  margin-bottom: 4px;
}

.question-drag-handle {
  cursor: move;
  color: #909399;
  font-size: 18px;
  vertical-align: middle;
}

.question-drag-handle:hover {
  color: #409EFF;
}

.drag-handle-col {
  line-height: 32px;
}

.question-item-form {
  margin-bottom: 8px;
}
</style>
