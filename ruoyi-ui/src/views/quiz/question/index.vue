<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="题目类型" prop="questionType">
        <el-select v-model="queryParams.questionType" placeholder="请选择题目类型" clearable>
          <el-option
            v-for="dict in dict.type.question_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="题目分数" prop="score">
        <el-input
          v-model="queryParams.score"
          placeholder="请输入题目分数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <!--<el-form-item label="题目标准答案" prop="correct">-->
      <!--  <el-input-->
      <!--    v-model="queryParams.correct"-->
      <!--    placeholder="请输入题目标准答案"-->
      <!--    clearable-->
      <!--    @keyup.enter.native="handleQuery"-->
      <!--  />-->
      <!--</el-form-item>-->

      <el-form-item label="科目id" prop="subjectId">
        <el-select v-model="queryParams.subjectId" placeholder="请输入科目id" clearable>
          <el-option
            v-for="dict in dict.type.subject_id"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <!--<el-form-item label="创建时间" prop="createTime">-->
      <!--  <el-date-picker clearable-->
      <!--                  v-model="queryParams.createTime"-->
      <!--                  type="date"-->
      <!--                  value-format="yyyy-MM-dd"-->
      <!--                  placeholder="请选择创建时间">-->
      <!--  </el-date-picker>-->
      <!--</el-form-item>-->
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
      <!--<el-form-item label="更新时间" prop="updateTime">-->
      <!--  <el-date-picker clearable-->
      <!--                  v-model="queryParams.updateTime"-->
      <!--                  type="date"-->
      <!--                  value-format="yyyy-MM-dd"-->
      <!--                  placeholder="请选择更新时间">-->
      <!--  </el-date-picker>-->
      <!--</el-form-item>-->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <!--<el-col :span="1.5">-->
      <!--  <el-button-->
      <!--    type="primary"-->
      <!--    plain-->
      <!--    icon="el-icon-plus"-->
      <!--    size="mini"-->
      <!--    @click="handleAdd"-->
      <!--    v-hasPermi="['quiz:question:add']"-->
      <!--  >新增-->
      <!--  </el-button>-->
      <!--</el-col>-->
      <!--<el-col :span="1.5">-->
      <!--  <el-button-->
      <!--    type="success"-->
      <!--    plain-->
      <!--    icon="el-icon-edit"-->
      <!--    size="mini"-->
      <!--    :disabled="single"-->
      <!--    @click="handleUpdate"-->
      <!--    v-hasPermi="['quiz:question:edit']"-->
      <!--  >修改-->
      <!--  </el-button>-->
      <!--</el-col>-->
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-delete"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['quiz:question:add']"
        >新增
        </el-button>
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['quiz:question:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['quiz:question:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="questionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="题目id" align="center" prop="id"/>
      <el-table-column label="题目类型" align="center" prop="questionType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.question_type" :value="scope.row.questionType"/>
        </template>
      </el-table-column>
      <el-table-column label="题目题干内容" align="center" prop="questionTitle"/>
      <el-table-column label="题目分数" align="center" prop="score"/>
      <el-table-column label="科目iD" align="center" prop="subjectId">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.subject_id" :value="scope.row.subjectId"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <!--<span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>-->
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
        <template slot-scope="scope">
          <!--<span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d}') }}</span>-->
          <span>{{ scope.row.updateTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['quiz:question:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['quiz:question:remove']"
          >删除
          </el-button>
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

    <!--&lt;!&ndash; 添加或修改题目管理对话框 &ndash;&gt;-->
    <!--<el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>-->
    <!--  <el-form ref="form" :model="form" :rules="rules" label-width="80px">-->
    <!--    <el-form-item label="题目类型" prop="questionType">-->
    <!--      <el-select v-model="form.questionType" placeholder="请选择题目类型">-->
    <!--        <el-option-->
    <!--          v-for="dict in dict.type.question_type"-->
    <!--          :key="dict.value"-->
    <!--          :label="dict.label"-->
    <!--          :value="parseInt(dict.value)"-->
    <!--        ></el-option>-->
    <!--      </el-select>-->
    <!--    </el-form-item>-->
    <!--    <el-form-item label="题目题干内容" prop="questionTitle">-->
    <!--      <el-input v-model="form.questionTitle" type="textarea" placeholder="请输入内容"/>-->
    <!--    </el-form-item>-->
    <!--    <el-form-item label="题目分数" prop="score">-->
    <!--      <el-input v-model="form.score" placeholder="请输入题目分数"/>-->
    <!--    </el-form-item>-->
    <!--    <el-form-item label="题目标准答案" prop="correct">-->
    <!--      <el-input v-model="form.correct" placeholder="请输入题目标准答案"/>-->
    <!--    </el-form-item>-->
    <!--    <el-form-item label="题目解析" prop="analysis">-->
    <!--      <el-input v-model="form.analysis" type="textarea" placeholder="请输入内容"/>-->
    <!--    </el-form-item>-->
    <!--    <el-form-item label="逻辑删除" prop="delFlag">-->
    <!--      <el-input v-model="form.delFlag" placeholder="请输入逻辑删除"/>-->
    <!--    </el-form-item>-->
    <!--    <el-form-item label="科目id" prop="subjectId">-->
    <!--      <el-input v-model="form.subjectId" placeholder="请输入科目id"/>-->
    <!--    </el-form-item>-->
    <!--  </el-form>-->
    <!--  <div slot="footer" class="dialog-footer">-->
    <!--    <el-button type="primary" @click="submitForm">确 定</el-button>-->
    <!--    <el-button @click="cancel">取 消</el-button>-->
    <!--  </div>-->
    <!--</el-dialog>-->
  </div>
</template>

<script>
import {listQuestion, getQuestion, delQuestion, addQuestion, updateQuestion} from "@/api/quiz/question";

export default {
  name: "Question",
  dicts: ['question_type', 'subject_id'],
  data() {
    return {
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
      // 题目管理表格数据
      questionList: [],
      // 日期范围
      dateRange: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        questionType: null,
        questionInfoId: null,
        correct: null,
        score: null,
        subjectId: null,
        createTime: null,
        updateTime: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        questionType: [
          {required: true, message: "题目类型不能为空", trigger: "change"}
        ],
        questionTitle: [
          {required: true, message: "题目题干内容不能为空", trigger: "blur"}
        ],
        score: [
          {required: true, message: "题目分数不能为空", trigger: "blur"}
        ],
        correct: [
          {required: true, message: "题目标准答案不能为空", trigger: "blur"}
        ],
        subjectId: [
          {required: true, message: "科目id不能为空", trigger: "blur"}
        ],
      }
    };
  },
  created() {
    this.getList();
    console.log(this.dict)
  },
  methods: {
    addQuestionType() {

    },
    /** 查询题目管理列表 */
    getList() {
      this.loading = true;
      listQuestion(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.questionList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        questionType: null,
        questionTitle: null,
        score: null,
        correct: null,
        analysis: null,
        delFlag: null,
        subjectId: null,
        createTime: null,
        updateTime: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$router.push({path: '/question/single'})
      // this.reset();
      // this.open = true;
      // this.title = "添加题目管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      let url;
      url = '/question/single'
      this.$router.push({path: url, query: {id: row.id}})
      // this.reset();
      // const id = row.id || this.ids
      // getQuestion(id).then(response => {
      //   this.form = response.data;
      //   this.open = true;
      //   this.title = "修改题目管理";
      // });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateQuestion(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addQuestion(this.form).then(response => {
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
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除题目管理编号为"' + ids + '"的数据项？').then(function () {
        return delQuestion(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('quiz/question/export', {
        ...this.queryParams
      }, `question_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
