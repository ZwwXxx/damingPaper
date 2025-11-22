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
        <!--分割线-->
        <el-col :span="24" class="mb-2">
          <el-alert title="可按照题型快速自动组卷，仍可在生成后调整题型及题目列表" type="info" :closable="false"/>
          <el-button type="primary" icon="el-icon-magic-stick" size="mini" class="mt-2" @click="openAutoComposeDialog">
            自动组卷
          </el-button>
        </el-col>
        <el-col :span="24">
          <el-form-item :label="'题型'+(qtypeIndex+1)+':'"
                        v-for="(questionType,qtypeIndex) in formData.paperQuestionTypeDto"
                        :key="qtypeIndex"
          >
            <el-input style="width: 80%;margin-right: 10px;" v-model="questionType.name"></el-input>
            <el-button type="text" @click="handleAddQuestion(questionType)">添加题目</el-button>
            <el-button type="text" @click="formData.paperQuestionTypeDto.splice(qtypeIndex,1)">删除题型</el-button>
            <el-card style="margin-top: 10px;" v-if="questionType.questionDtos.length>0">
              <!--遍历所有题目-->
              <el-form-item :label="'题目'+(questionIndex+1)+'：'"
                            v-for="(q,questionIndex) in questionType.questionDtos"
                            :key="questionIndex"
              >
                <el-row>
                  <el-col :span="21">
                    <div>{{ q.questionTitle }}</div>
                    <span v-for="(option,index) in q.items" :key="index" style="margin-right: 20px;">
                      <span>{{ option.prefix }}</span>
                      <span style="margin: 0 2px"> {{ option.content }}</span>
                    </span>
                  </el-col>
                  <el-col :span="3">
                    <el-button type="text" @click="questionType.questionDtos.splice(questionIndex,1)">删除题目
                    </el-button>
                  </el-col>
                </el-row>
              </el-form-item>
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
      </el-table>
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
  </div>
</template>
<script>
import {getQuestion, listQuestion} from "@/api/quiz/question";
import {addPaper, getPaper, updatePaper, autoComposePaper} from "@/api/quiz/paper";
import {optionSubject} from "@/api/quiz/subject";

export default {
  components: {},
  props: [],
  data() {
    return {
      subjectMap: {},
      questionTypeMap: {
        1: '单选题',
        2: '多选题',
        3: '主观题',
        4: '判断题'
      },
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
        startTime: '',
        endTime: '',
        enableAntiCheat: false,
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
      }
    }
  },
  computed: {},
  watch: {
    'formData.subjectId'(val) {
      this.autoCompose.form.subjectId = val
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
      this.autoCompose.form.subjectId = this.formData.subjectId
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
      return rows.filter(row => {
        if (existingIds.has(row.id)) {
          return false;
        }
        if (keyword) {
          const title = (row.questionTitle || '').toLowerCase();
          return title.includes(keyword);
        }
        return true;
      });
    },
    async getQuestList() {
      this.questionList.loading = true;
      try {
        const params = {
          ...this.questionList.queryParams,
          questionTitle: undefined
        };
        const response = await listQuestion(params);
        const filteredRows = this.filterExistingQuestions(response.rows || []);
        this.questionList.tableData = filteredRows;
        this.questionList.total = filteredRows.length;
        this.$nextTick(() => {
          this.$refs.questionTable && this.$refs.questionTable.clearSelection();
        });
      } finally {
        this.questionList.loading = false;
      }
    },
    queryQuestionList() {
      this.getQuestList()
    },
    getSubjectLabel(id) {
      return this.subjectMap[id] || '未知科目'; // 默认值
    },
    getQuestionTypeLabel(type) {
      return this.questionTypeMap[type] || '未知题型'; // 默认值
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
    addQuestionConfirm() {
      const existingIds = this.getExistingQuestionIds()
      this.questionList.selectionList.forEach(q => {
        if (existingIds.has(q.id)) {
          return
        }
        getQuestion(q.id).then(res => {
          const data = res.data
          if (!existingIds.has(data.id)) {
            this.currQuestionType.questionDtos.push(data)
            existingIds.add(data.id)
          }
        })
      })
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
      if (this.formData.paperId) {
        console.log("是修改")
        res = await updatePaper(this.formData)
      } else {
        res = await addPaper(this.formData)
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
</style>
