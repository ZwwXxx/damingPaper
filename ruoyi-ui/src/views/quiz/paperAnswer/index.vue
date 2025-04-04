<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="试卷名" prop="paperName">
        <el-input
          v-model="queryParams.paperName"
          placeholder="请输入试卷名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="试卷ID" prop="paperId">
        <el-input
          v-model="queryParams.paperId"
          placeholder="请输入试卷ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="回答人" prop="createUser">
        <el-input
          v-model="queryParams.createUser"
          placeholder="请输入回答人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="试卷总分" prop="paperScore">
        <el-input
          v-model="queryParams.paperScore"
          placeholder="请输入试卷总分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户得分" prop="finalScore">
        <el-input
          v-model="queryParams.finalScore"
          placeholder="请输入用户得分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="题目数量" prop="questionCount">
        <el-input
          v-model="queryParams.questionCount"
          placeholder="请输入题目数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="答对数量" prop="correctCount">
        <el-input
          v-model="queryParams.correctCount"
          placeholder="请输入答对数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="科目ID" prop="subjectId">
        <el-select v-model="queryParams.subjectId" placeholder="请输入科目id" clearable>
          <el-option
            v-for="dict in dict.type.subject_id"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['quiz:paperAnswer:add']"
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
          v-hasPermi="['quiz:paperAnswer:edit']"
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
          v-hasPermi="['quiz:paperAnswer:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['quiz:paperAnswer:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="paperAnswerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="答卷记录ID" align="center" prop="paperAnswerId" />
      <el-table-column label="试卷名" align="center" prop="paperName" />
      <el-table-column label="试卷ID" align="center" prop="paperId" />
      <el-table-column label="回答人" align="center" prop="createUser" />
      <el-table-column label="试卷总分" align="center" prop="paperScore" />
      <el-table-column label="用户得分" align="center" prop="finalScore" />
      <el-table-column label="题目数量" align="center" prop="questionCount" />
      <el-table-column label="答对数量" align="center" prop="correctCount" />
      <el-table-column label="所属科目" align="center" prop="subjectId">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.subject_id" :value="scope.row.subjectId"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['quiz:paperAnswer:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['quiz:paperAnswer:remove']"
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

    <!-- 添加或修改试卷作答情况对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="试卷名" prop="paperName">
          <el-input v-model="form.paperName" placeholder="请输入试卷名" />
        </el-form-item>
        <el-form-item label="试卷ID" prop="paperId">
          <el-input v-model="form.paperId" placeholder="请输入试卷ID" />
        </el-form-item>
        <el-form-item label="回答人" prop="createUser">
          <el-input v-model="form.createUser" placeholder="请输入回答人" />
        </el-form-item>
        <el-form-item label="试卷总分" prop="paperScore">
          <el-input v-model="form.paperScore" placeholder="请输入试卷总分" />
        </el-form-item>
        <el-form-item label="用户得分" prop="finalScore">
          <el-input v-model="form.finalScore" placeholder="请输入用户得分" />
        </el-form-item>
        <el-form-item label="题目数量" prop="questionCount">
          <el-input v-model="form.questionCount" placeholder="请输入题目数量" />
        </el-form-item>
        <el-form-item label="答对数量" prop="correctCount">
          <el-input v-model="form.correctCount" placeholder="请输入答对数量" />
        </el-form-item>
        <el-form-item label="所属科目" prop="subjectId">
          <el-input v-model="form.subjectId" placeholder="请输入所属科目" />
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
import { listPaperAnswer, getPaperAnswer, delPaperAnswer, addPaperAnswer, updatePaperAnswer } from "@/api/quiz/paperAnswer";
import {listQuestion} from "@/api/quiz/question";

export default {
  dicts: ['subject_id'],
  name: "PaperAnswer",
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
      // 试卷作答情况表格数据
      paperAnswerList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        paperName: null,
        paperId: null,
        createUser: null,
        paperScore: null,
        finalScore: null,
        questionCount: null,
        correctCount: null,
        subjectId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        paperName: [
          { required: true, message: "试卷名不能为空", trigger: "blur" }
        ],
        paperId: [
          { required: true, message: "试卷ID不能为空", trigger: "blur" }
        ],
        createUser: [
          { required: true, message: "回答人不能为空", trigger: "blur" }
        ],
        paperScore: [
          { required: true, message: "试卷总分不能为空", trigger: "blur" }
        ],
        questionCount: [
          { required: true, message: "题目数量不能为空", trigger: "blur" }
        ],
        subjectId: [
          { required: true, message: "所属科目不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询试卷作答情况列表 */
    getList() {
      this.loading = true;
      listPaperAnswer(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.paperAnswerList = response.rows;
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
        paperAnswerId: null,
        paperName: null,
        paperId: null,
        createUser: null,
        paperScore: null,
        finalScore: null,
        questionCount: null,
        correctCount: null,
        createTime: null,
        subjectId: null
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
      this.ids = selection.map(item => item.paperAnswerId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加试卷作答情况";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const paperAnswerId = row.paperAnswerId || this.ids
      getPaperAnswer(paperAnswerId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改试卷作答情况";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.paperAnswerId != null) {
            updatePaperAnswer(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPaperAnswer(this.form).then(response => {
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
      const paperAnswerIds = row.paperAnswerId || this.ids;
      this.$modal.confirm('是否确认删除试卷作答情况编号为"' + paperAnswerIds + '"的数据项？').then(function() {
        return delPaperAnswer(paperAnswerIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('quiz/paperAnswer/export', {
        ...this.queryParams
      }, `paperAnswer_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
