<template>
  <div style="padding: 20px">
    <el-row :gutter="15">
      <el-form ref="elForm1" :model="formData" :rules="rules" size="medium" label-width="100px">
        <el-col :span="12">
          <el-form-item label="科目" prop="subjectId">
            <el-select v-model="formData.subjectId" placeholder="请输入科目" clearable :style="{width: '100%'}">
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
            <el-select v-model="formData.paperType" placeholder="请输入试卷类型" clearable :style="{width: '100%'}">
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
        <!--分割线-->
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
            <el-form-item label="id" prop="id">
              <el-input v-model="formData.id" placeholder="请输入id" clearable
                        :style="{width: '100%'}"></el-input>
            </el-form-item>
          </el-col>
          <el-col style="margin-bottom: 10px;">
            <el-button type="primary" icon="el-icon-search" size="medium" @click="queryQuestionList">查询</el-button>
          </el-col>
        </el-form>
      </el-row>
      <!--表格区域，展示题目，和题型-->
      <el-table v-loading="questionList.loading" :data="questionList.tableData"
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
  </div>
</template>
<script>
import {getQuestion, listQuestion} from "@/api/quiz/question";
import {addPaper, getPaper, updatePaper} from "@/api/quiz/paper";

export default {
  components: {},
  props: [],
  data() {
    return {
      subjectMap: {
        1: '高数',
        2: '政治',
        3: '计算机基础与程序设计',
        // 其他科目...
      },
      questionTypeMap: {
        1: '单选题',
        2: '多选题',
        3: '主观题',
        // 其他题型...
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
      }],
      subjectOptions: [{
        "label": "高等数学",
        "value": 1
      }, {
        "label": "政治",
        "value": 2
      }, {
        "label": "计算机基础与程序设计",
        "value": 3
      }],
      questionList: {
        selectionList: [],
        queryParams: {
          id: undefined,
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
      subjectIdOptions: [{
        "label": "高等数学",
        "value": 1
      }, {
        "label": "政治",
        "value": 2
      }, {
        "label": "计算机基础与程序设计",
        "value": 3
      }],
    }
  },
  computed: {},
  watch: {},
  created() {
    let paperId = this.$route.query.paperId
    if (paperId) {
      this.getPaperById(paperId)
    }
  },
  mounted() {
  },
  methods: {
    async getPaperById(paperId) {
      this.formData = (await getPaper(paperId)).data
      console.log(this.formData)
    },
    getQuestList() {
      listQuestion(this.questionList.queryParams).then(response => {
          this.questionList.tableData = response.rows;
          this.total = response.total;
          this.questionList.loading = false;
        }
      );
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
      // 根据勾选的题目的id遍历查询然后push到
      this.questionList.selectionList.forEach(q => {
        getQuestion(q.id).then(res => {
          this.currQuestionType.questionDtos.push(res.data)
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
  }
}

</script>
<style>
</style>
