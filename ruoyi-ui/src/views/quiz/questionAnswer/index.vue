<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="提交人" prop="createUser">
        <el-input
          v-model="queryParams.createUser"
          placeholder="请输入提交人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="试卷id" prop="paperId">
        <el-input
          v-model="queryParams.paperId"
          placeholder="请输入试卷id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="试卷记录id" prop="paperAnswerId">
        <el-input
          v-model="queryParams.paperAnswerId"
          placeholder="请输入试卷记录id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="题目id" prop="questionId">
        <el-input
          v-model="queryParams.questionId"
          placeholder="请输入题目id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="科目" prop="subjectId">
        <el-select v-model="queryParams.subjectId" placeholder="请选择科目" clearable>
          <el-option
            v-for="option in subjectOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="题目分数" prop="questionScore">
        <el-input
          v-model="queryParams.questionScore"
          placeholder="请输入题目分数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否正确" prop="isCorrect">
        <el-input
          v-model="queryParams.isCorrect"
          placeholder="请输入是否正确"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['quiz:questionAnswer:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['quiz:questionAnswer:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['quiz:questionAnswer:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['quiz:questionAnswer:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="questionAnswerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="达卷id" align="center" prop="answerId" />
      <el-table-column label="提交人" align="center" prop="createUser" />
      <el-table-column label="试卷id" align="center" prop="paperId" />
      <el-table-column label="试卷记录id" align="center" prop="paperAnswerId" />
      <el-table-column label="用户答案" align="center" prop="userAnswer" />
      <el-table-column label="题目类型" align="center" prop="questionType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.question_type" :value="scope.row.questionType"/>
        </template>
      </el-table-column>
      <el-table-column label="题目id" align="center" prop="questionId" />
      <el-table-column label="科目" align="center" prop="subjectId">
        <template slot-scope="scope">
          <span>{{ getSubjectLabel(scope.row.subjectId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户得分" align="center" prop="finalScore" />
      <el-table-column label="题目分数" align="center" prop="questionScore" />
      <el-table-column label="是否正确" align="center" prop="isCorrect">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.is_correct" :value="scope.row.isCorrect"/>
        </template>
      </el-table-column>
      <el-table-column label="题目顺序" align="center" prop="itemOrder" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['quiz:questionAnswer:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['quiz:questionAnswer:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改题目回答情况表对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="提交人" prop="createUser">
          <el-input v-model="form.createUser" placeholder="请输入提交人" />
        </el-form-item>
        <el-form-item label="试卷id" prop="paperId">
          <el-input v-model="form.paperId" placeholder="请输入试卷id" />
        </el-form-item>
        <el-form-item label="试卷记录id" prop="paperAnswerId">
          <el-input v-model="form.paperAnswerId" placeholder="请输入试卷记录id" />
        </el-form-item>
        <el-form-item label="用户答案" prop="userAnswer">
          <el-input v-model="form.userAnswer" placeholder="请输入用户答案" />
        </el-form-item>
        <el-form-item label="题目id" prop="questionId">
          <el-input v-model="form.questionId" placeholder="请输入题目id" />
        </el-form-item>
        <el-form-item label="科目" prop="subjectId">
          <el-select v-model="form.subjectId" placeholder="请选择科目">
            <el-option
              v-for="option in subjectOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="用户得分" prop="finalScore">
          <el-input v-model="form.finalScore" placeholder="请输入用户得分" />
        </el-form-item>
        <el-form-item label="题目分数" prop="questionScore">
          <el-input v-model="form.questionScore" placeholder="请输入题目分数" />
        </el-form-item>
        <el-form-item label="是否正确" prop="isCorrect">
          <el-input v-model="form.isCorrect" placeholder="请输入是否正确" />
        </el-form-item>
        <el-form-item label="题目顺序" prop="itemOrder">
          <el-input v-model="form.itemOrder" placeholder="请输入题目顺序" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listQuestionAnswer, getQuestionAnswer, delQuestionAnswer, addQuestionAnswer, updateQuestionAnswer } from "@/api/quiz/questionAnswer";
import {optionSubject} from "@/api/quiz/subject";

export default {
  dicts: ['question_type','is_correct'],
  name: "QuestionAnswer",
  data() {
    return {
      dateRange:[],
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 题目回答情况表表格数据
      questionAnswerList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        createUser: null,
        paperId: null,
        paperAnswerId: null,
        userAnswer: null,
        questionId: null,
        subjectId: null,
        finalScore: null,
        questionScore: null,
        isCorrect: null,
        itemOrder: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        createUser: [
          { required: true, message: "提交人不能为空", trigger: "blur" }
        ],
        paperId: [
          { required: true, message: "试卷id不能为空", trigger: "blur" }
        ],
        questionId: [
          { required: true, message: "题目id不能为空", trigger: "blur" }
        ],
        subjectId: [
          { required: true, message: "科目不能为空", trigger: "change" }
        ],
      },
      subjectOptions: [],
      subjectMap: {}
    };
  },
  created() {
    this.loadSubjectOptions();
    this.getList();
  },
  methods: {
    async loadSubjectOptions() {
      const res = await optionSubject();
      const list = res.data || [];
      this.subjectOptions = list.map(item => ({
        label: item.subjectName,
        value: item.subjectId
      }));
      this.subjectMap = list.reduce((acc, cur) => {
        acc[cur.subjectId] = cur.subjectName;
        return acc;
      }, {});
    },
    getSubjectLabel(id) {
      return this.subjectMap[id] || '-';
    },
    /** 查询题目回答情况表列表 */
    getList() {
      this.loading = true;
      listQuestionAnswer(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.questionAnswerList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        answerId: null,
        createUser: null,
        paperId: null,
        paperAnswerId: null,
        userAnswer: null,
        createTime: null,
        questionId: null,
        subjectId: null,
        finalScore: null,
        questionScore: null,
        isCorrect: null,
        itemOrder: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.answerId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加题目回答情况表";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const answerId = row.answerId || this.ids
      getQuestionAnswer(answerId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改题目回答情况表";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.answerId != null) {
            updateQuestionAnswer(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addQuestionAnswer(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const answerIds = row.answerId || this.ids;
      this.$modal.confirm('是否确认删除题目回答情况表编号为"' + answerIds + '"的数据项？').then(function() {
        return delQuestionAnswer(answerIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('quiz/questionAnswer/export', {
        ...this.queryParams
      }, `questionAnswer_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
