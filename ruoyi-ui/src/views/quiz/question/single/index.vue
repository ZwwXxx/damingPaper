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
          <el-form-item label="题干" prop="questionTitle">
            <el-input v-model="formData.questionTitle" placeholder="请输入题干" clearable :style="{width: '100%'}">
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="formData.questionType !== 3">
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
          <el-form-item label="标准答案"  :prop="formData.questionType === 2 ? 'correctArray' : 'correct'">
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
            <el-input
              v-if="formData.questionType===3"
              v-model="formData.correct"
              type="textarea"
              placeholder="请输入标准答案"
              :rows="3"
            />
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
            <el-button
              v-if="formData.questionType !== 3"
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
import { addQuestion, getQuestion, updateQuestion } from "@/api/quiz/question";
import { optionSubject } from "@/api/quiz/subject";
import Editor from "@/components/Editor";

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
  components: { Editor },
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
        correct: undefined,
        correctArray: [],
        analysis: '无',
        score: 1,
        items: initialType === 3
            ? []
            : initialType === 4
                ? defaultJudgeOptions()
                : defaultChoiceOptions()
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
      }, {
        "label": "主观题",
        "value": 3
      }, {
        "label": "判断题",
        "value": 4
      }],
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
    const id = this.$route.query.id;
    if (id) {
      this.getData(id);
    }
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
    handleQuestionTypeChange(value) {
      this.applyQuestionTypeDefaults(value);
    },
    applyQuestionTypeDefaults(questionType, options = {}) {
      const { preserveAnswer = false } = options;
      if (questionType === 3) {
        this.formData.items = [];
        if (!preserveAnswer) {
          this.formData.correct = '';
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
        this.formData = {
          ...response.data,
          correctArray: response.data.correctArray || [],
          items: response.data.items || []
        };
        this.applyQuestionTypeDefaults(this.formData.questionType, { preserveAnswer: true });
      } finally {
        this.loading = false;
      }
    },
    async submitForm() {
      const valid = await this.$refs['elForm'].validate();
      if (!valid) return;
      const type = this.formData.questionType;
      if (type === 2) {
        this.formData.correct = '';
      } else {
        this.formData.correctArray = [];
      }
      if (type === 3) {
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
        if (this.formData.id === undefined) {
          const keepSubjectId = this.formData.subjectId;
          const keepType = this.presetQuestionType;
          this.$refs['elForm'].resetFields();
          this.formData.subjectId = keepSubjectId;
          this.formData.questionType = keepType;
          this.applyQuestionTypeDefaults(keepType);
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

  }
}

</script>
<style>
</style>
