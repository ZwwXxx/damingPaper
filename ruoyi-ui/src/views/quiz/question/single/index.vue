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
            <editor v-model="formData.questionTitle" :min-height="200" placeholder="请输入题干（支持图片、富文本）"/>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="formData.questionType !== 3 && formData.questionType !== 5">
          <el-form-item label="选项：" required>
            <el-form-item :label="item.prefix" :key="item.prefix" v-for="(item,index) in formData.items" required
                          label-width="50px" style="margin: 10px 0 !important; ">
              <div style="display: flex; align-items: flex-start; width: 100%;">
                <el-input v-model="item.prefix" style="width:50px; margin-right: 10px;"/>
                <div style="flex: 1; margin-right: 10px;">
                  <editor 
                    v-model="item.content" 
                    :min-height="120" 
                    :placeholder="'请输入选项内容（支持图片、富文本）'"
                    style="width: 100%;"
                  />
                </div>
                <el-button type="danger" size="mini" icon="el-icon-delete"
                           @click="optionItemRemove(index)"></el-button>
              </div>
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
              v-if="[3,5].includes(formData.questionType)"
              v-model="formData.correct"
              type="textarea"
              :placeholder="formData.questionType === 5 ? '请输入填空题标准答案（多个答案用英文逗号分隔）' : '请输入主观题标准答案'"
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
import { addQuestion, getQuestion, updateQuestion, bindKnowledgePoints, getQuestionKnowledgePoints, listKnowledgePoints } from "@/api/quiz/question";
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
        knowledgePointIds: [],
        items: initialType === 3
            ? []
            : initialType === 4
                ? defaultJudgeOptions()
                : defaultChoiceOptions()
      },
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
      if (questionType === 3 || questionType === 5) {
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
          items: response.data.items || [],
          knowledgePointIds: []
        };
        this.applyQuestionTypeDefaults(this.formData.questionType, { preserveAnswer: true });
        
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
    async submitForm() {
      const valid = await this.$refs['elForm'].validate();
      if (!valid) return;
      const type = this.formData.questionType;
      if (type === 2) {
        this.formData.correct = '';
      } else {
        this.formData.correctArray = [];
      }
      if (type === 3 || type === 5) {
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
          } else if (keepQuestionType === 3 || keepQuestionType === 5) {
            // 主观题和填空题：清空答案
            this.formData.items = [];
            this.formData.correct = '';
            this.formData.correctArray = [];
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
