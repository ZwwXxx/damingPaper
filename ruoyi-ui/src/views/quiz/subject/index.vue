<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="科目名称" prop="subjectName">
        <el-input
          v-model="queryParams.subjectName"
          placeholder="请输入科目名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['quiz:subject:add']"
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
          v-hasPermi="['quiz:subject:edit']"
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
          v-hasPermi="['quiz:subject:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['quiz:subject:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="subjectList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="科目ID" prop="subjectId" align="center"/>
      <el-table-column label="科目名称" prop="subjectName" align="center"/>
      <el-table-column label="创建时间" prop="createTime" align="center"/>
      <el-table-column label="更新时间" prop="updateTime" align="center"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['quiz:subject:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['quiz:subject:remove']"
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

    <el-dialog :title="title" :visible.sync="open" width="400px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="科目名称" prop="subjectName">
          <el-input v-model="form.subjectName" placeholder="请输入科目名称"/>
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
import {addSubject, delSubject, getSubject, listSubject, updateSubject} from "@/api/quiz/subject";

export default {
  name: "Subject",
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      subjectList: [],
      ids: [],
      single: true,
      multiple: true,
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        subjectName: null
      },
      form: {},
      rules: {
        subjectName: [
          {required: true, message: "科目名称不能为空", trigger: "blur"},
          {min: 1, max: 50, message: "长度在 1 到 50 个字符", trigger: "blur"}
        ]
      }
    }
  },
  created() {
    this.getList();
  },
  activated() {
    this.getList();
  },
  methods: {
    async getList() {
      this.loading = true;
      try {
        const res = await listSubject(this.queryParams);
        this.subjectList = res.rows || [];
        this.total = res.total || 0;
      } catch (error) {
        this.$message.error(error.msg || "加载科目数据失败");
      } finally {
        this.loading = false;
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.subjectId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "新增科目";
    },
    handleUpdate(row) {
      this.reset();
      const subjectId = row.subjectId || this.ids[0];
      getSubject(subjectId).then(res => {
        this.form = res.data;
        this.open = true;
        this.title = "修改科目";
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) return;
        if (this.form.subjectId) {
          updateSubject(this.form).then(() => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
        } else {
          addSubject(this.form).then(() => {
            this.$modal.msgSuccess("新增成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    handleDelete(row) {
      const subjectIds = row.subjectId || this.ids;
      this.$modal.confirm('是否确认删除科目编号为"' + subjectIds + '"的数据项？').then(() => {
        return delSubject(subjectIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('quiz/subject/export', { ...this.queryParams }, `subject_${new Date().getTime()}.xlsx`)
    },
    reset() {
      this.form = {
        subjectId: null,
        subjectName: null
      };
      this.resetForm("form");
    },
    cancel() {
      this.open = false;
      this.reset();
    }
  }
}
</script>

