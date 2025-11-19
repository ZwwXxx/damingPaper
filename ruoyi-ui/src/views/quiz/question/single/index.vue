<template>
  <div style="padding: 20px">
    <el-row :gutter="15">
      <el-form ref="elForm" :model="formData" :rules="rules" size="medium" label-width="100px">
        <el-col :span="12">
          <el-form-item label="科目" prop="subjectId">
            <el-select v-model="formData.subjectId" placeholder="请输入科目" clearable :style="{width: '100%'}">
              <el-option v-for="(item, index) in subjectIdOptions" :key="index" :label="item.label"
                         :value="item.value" :disabled="item.disabled"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="题目类型" prop="questionType">
            <el-select v-model="formData.questionType" placeholder="请选择题目类型" clearable
                       :style="{width: '100%'}">
              <el-option v-for="(item, index) in questionTypeOptions" :key="index" :label="item.label"
                         :value="item.value" :disabled="item.disabled"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="题干" prop="questionTitle">
            <el-input v-model="formData.questionTitle" placeholder="请输入题干" clearable :style="{width: '100%'}">
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="选项：" required>
            <el-form-item :label="item.prefix" :key="item.prefix" v-for="(item,index) in formData.items" required
                          label-width="50px" style="margin: 10px 0 !important; ">
              <el-input v-model="item.prefix" style="width:50px;"/>
              <el-input v-model="item.content" style="width: 60%; margin-left: 10px"/>
              <el-button type="danger" size="mini" style="margin-left: 20px" icon="el-icon-delete"
                         @click="optionItemRemove(index)"></el-button>
            </el-form-item>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="标准答案"  :prop="formData.questionType === 1 ? 'correct' : 'correctArray'">
            <el-radio-group v-model="formData.correct" size="medium" @change="changeHandler"
                            v-if="formData.questionType===1">
              <el-radio v-for="item in formData.items" :key="item.prefix" :label="item.prefix"
                        :disabled="item.disabled">{{ item.prefix }}
              </el-radio>
            </el-radio-group>
            <el-checkbox-group v-model="formData.correctArray" v-if="formData.questionType===2">
              <el-checkbox v-for="item in formData.items" :label="item.prefix" :key="item.prefix">{{ item.prefix }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="解析" prop="analysis">
            <editor v-model="formData.analysis" :min-height="220" placeholder="请输入解析"/>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="分数" prop="score">
            <el-input-number v-model="formData.score" placeholder="分数"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item size="large">
            <el-button type="primary" @click="submitForm">提交</el-button>
            <el-button @click="resetForm">重置</el-button>
            <el-button type="warning" @click="addNewOption">添加选项</el-button>
          </el-form-item>
        </el-col>
      </el-form>
    </el-row>
  </div>
</template>
<script>
import question from "@/views/quiz/question/index.vue";
import {addQuestion, getQuestion, updateQuestion} from "@/api/quiz/question";
import {optionSubject} from "@/api/quiz/subject";
import Editor from "@/components/Editor";

export default {
  components: { Editor },
  props: [],
  data() {
    return {

      formData: {
        id: undefined,
        subjectId: undefined,
        questionType: 1,
        questionTitle: undefined,
        correct: undefined,
        correctArray: [],
        analysis: '无',
        score: 1,
        items: [
          {prefix: 'A', content: ''},
          {prefix: 'B', content: ''},
          {prefix: 'C', content: ''},
          {prefix: 'D', content: ''}
        ],
      },
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

      questionTypeOptions: [{
        "label": "单选题",
        "value": 1
      }, {
        "label": "多选题",
        "value": 2
      },
      ],
      // correctAnswerOptions: [{
      //   "label": "A",
      //   "value": 1
      // }, {
      //   "label": "B",
      //   "value": 2
      // }, {
      //   "label": "C",
      //   "value": 3
      // }, {
      //   "label": "D",
      //   "value": 4
      // }],
    }
  },
  computed: {},
  watch: {},
  async created() {
    await this.loadSubjectOptions();
    // 如果有传参，说明是修改我们请求数据进行回显
    let id = this.$route.query.id
    if (id) {
      this.getData(id)
    }
  },
  mounted() {
  },
  methods: {
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
    async getData(id) {
      this.loading = true;
      await getQuestion(id).then(response => {
        console.log(response.data)
        this.formData = response.data;
        this.loading = false;
      })
    },
    async submitForm() {
      const valid = await this.$refs['elForm'].validate(); // 使用 await 等待验证结果
      if (!valid) return;
      // TODO 提交表单
      if (this.formData.questionType===1){
        this.formData.correctArray=[]
      }else {
        this.formData.correct=''
      }
      let res = null
      if (this.formData.id !== undefined) {
        res = await updateQuestion(this.formData)
      } else {
        res = await addQuestion(this.formData)
      }
      console.log(res)
      const message = res.code === 200 ? "操作成功!" : "操作失败,请联系管理员!"
      const type = res.code === 200 ? "success" : "error"
      this.$message({
        message,
        type
      });

      if (res.code === 200) {
        const keepSubjectId = this.formData.subjectId;
        this.$refs['elForm'].resetFields();
        this.formData.subjectId = keepSubjectId;
        this.formData.items.forEach(item=>{
          item.content=''
        })
        // this.$router.push({
        //   path: '/question/index'
        // })
      }

    },
    resetForm() {
      const keepSubjectId = this.formData.subjectId;
      this.$refs['elForm'].resetFields();
      this.formData.subjectId = keepSubjectId;
    },
    optionItemRemove(index) {
      this.formData.items.splice(index, 1)
    },
    addNewOption() {
      let oldOption = this.formData.items
      // 根据长度来判断新增的选项的名字，是A还是A的下一个(prefix)
      let newPrefix
      if (oldOption.length > 0) {
        console.log(111)
        // 那就是下一个了,获取最后一个，
        // 根据他的字符编码加1得到A的下一个字母B的编码值转为string
        let lastOption = oldOption[oldOption.length - 1]
        newPrefix = String.fromCharCode(lastOption.prefix.charCodeAt() + 1)
      } else {
        // 没有选项，那默认从A开始
        newPrefix = "A"
      }
      // push到item数组里
      oldOption.push({id: null, prefix: newPrefix, content: ''})
    },

  }
}

</script>
<style>
</style>
