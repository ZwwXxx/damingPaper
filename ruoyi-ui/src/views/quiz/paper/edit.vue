<template>
  <div style="padding: 20px">
    <el-row :gutter="15">
      <el-form ref="elForm1" :model="formData" :rules="rules" size="medium" label-width="100px">
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
            :label="formData.numberMode === 1 ? ('题型'+(qtypeIndex+1)+':') : '题目列表'"
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
                  <el-form-item :label="'题目'+(questionIndex+1)+'：'" class="question-item-form">
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
            <el-button type="warning" @click="addQuestionTypeInput">添加题型</el-button>

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
          <el-col style="margin-bottom: 10px;">
            <el-button type="primary" icon="el-icon-search" size="medium" @click="queryQuestionList">查询</el-button>
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
import {getQuestion, getClozeQuestion, listQuestion} from "@/api/quiz/question";
import {addPaper, getPaper, updatePaper, autoComposePaper, autoComposePaperByDate} from "@/api/quiz/paper";
import {optionSubject} from "@/api/quiz/subject";
import draggable from 'vuedraggable';

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
          pageNum: 1,
          pageSize: 5
        },
        dateRange: [],
        dateOrder: 'desc',
        tableData: [],
        total: 0,
        loading: undefined,
      },

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
      // 切换为“按加入顺序”时，将题型扁平为单一题目列表，避免残留空题型块
      if (val === 2) {
        this.mergeQuestionTypesAsFlatList()
      }
    }
  },
  async created() {
    await this.loadSubjectOptions();
    let paperId = this.$route.query.paperId
    if (paperId) {
      this.getPaperById(paperId)
    }
  },
  mounted() {
  },
  methods: {
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
      // 兼容后端可能返回空字符串的情况
      if (this.formData.startTime === '') this.formData.startTime = null
      if (this.formData.endTime === '') this.formData.endTime = null
      this.autoCompose.form.subjectId = this.formData.subjectId
      // 如果当前试卷配置为按加入顺序编号，则将题型扁平化为一个题目列表
      if (this.formData.numberMode === 2) {
        this.mergeQuestionTypesAsFlatList()
      }
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
        // 使用后端分页总数，保证分页组件正常工作
        this.questionList.total = Number(response.total) || filteredRows.length;
        this.$nextTick(() => {
          this.$refs.questionTable && this.$refs.questionTable.clearSelection();
        });
      } finally {
        this.questionList.loading = false;
      }
    },
    queryQuestionList() {
      this.questionList.queryParams.pageNum = 1;
      this.getQuestList()
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
        return;
      }
      questionType.questionDtos = questionType.questionDtos.filter(q => {
        if (!q) return false;
        if (q.id === targetId) return false;
        if (q.parentId === targetId) return false;
        return true;
      });
    },
    // 将多个题型下的题目扁平为单一“题目列表”（用于按加入顺序展示）
    mergeQuestionTypesAsFlatList() {
      const list = this.formData.paperQuestionTypeDto || []
      const allQuestions = []
      list.forEach(t => {
        (t.questionDtos || []).forEach(q => {
          if (q) {
            allQuestions.push(q)
          }
        })
      })
      this.formData.paperQuestionTypeDto = [{
        name: '题目列表',
        questionDtos: allQuestions
      }]
    },
    // 获取当前页复选框勾选的选项,后续新增直接传，全选就全部，选一个[]里就一个item
    handleSelectionChange(val) {
      this.questionList.selectionList = val
    },
    onOpen() {
    },
    onClose() {
      this.$refs['elForm2'].resetFields()
    },
    close() {
      this.open = false
    },
    async addQuestionConfirm() {
      const existingIds = this.getExistingQuestionIds()
      for (const q of this.questionList.selectionList) {
        if (existingIds.has(q.id)) {
          continue
        }
        const res = await getQuestion(q.id)
        const data = res.data
        if (!data || existingIds.has(data.id)) {
          continue
        }
        // 如果是完形填空父题，额外把其子题一并加入当前题型
        if (data.questionType === 6) {
          // 先加入父题
          this.currQuestionType.questionDtos.push(data)
          existingIds.add(data.id)
          const clozeRes = await getClozeQuestion(data.id)
          if (clozeRes.code === 200 && clozeRes.data) {
            const children = clozeRes.data.children || []
            children.forEach(child => {
              if (!child || existingIds.has(child.id)) return
              this.currQuestionType.questionDtos.push(child)
              existingIds.add(child.id)
            })
          }
        } else {
          this.currQuestionType.questionDtos.push(data)
          existingIds.add(data.id)
        }
      }
      this.close()
    },
    // 点击新增时显示弹窗和加载题目表，也就是题目列表那些
    handleAddQuestion(questionType) {
      this.open = true
      // 拷贝一份当前的题型，标识给哪个题型加题目
      this.currQuestionType = questionType
      this.questionList.loading = true
      if (!this.questionList.queryParams.subjectId && this.formData.subjectId) {
        this.questionList.queryParams.subjectId = this.formData.subjectId
      }
      if (questionType && questionType.questionType && !this.questionList.queryParams.questionType) {
        this.questionList.queryParams.questionType = questionType.questionType
      }
      this.getQuestList()
    },
    addQuestionTypeInput() {
      this.formData.paperQuestionTypeDto.push({
        name: '',
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
      this.$refs['elForm1'].validate(valid => {
        if (!valid) return
        // TODO 提交表单
      })
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
