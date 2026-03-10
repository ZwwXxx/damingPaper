<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="科目" prop="subjectId">
        <el-select v-model="queryParams.subjectId" placeholder="请选择科目" clearable style="width: 200px">
          <el-option
            v-for="item in subjectOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="动画名称" prop="animationName">
        <el-input
          v-model="queryParams.animationName"
          placeholder="请输入动画名称"
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
          icon="el-icon-upload2"
          size="mini"
          @click="openUpload"
          v-hasPermi="['quiz:animation:upload']"
        >上传</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['quiz:animation:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['quiz:animation:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="animationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="动画ID" align="center" prop="animationId" width="90" />
      <el-table-column label="科目" align="center" prop="subjectId" width="140">
        <template slot-scope="scope">
          <span>{{ subjectNameMap[scope.row.subjectId] || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="动画名称" align="center" prop="animationName" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="动画URL" align="center" prop="animationUrl" min-width="260">
        <template slot-scope="scope">
          <el-link v-if="scope.row.animationUrl" :href="scope.row.animationUrl" target="_blank" :underline="false">预览</el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createUser" width="120" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="140">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handlePreview(scope.row)"
          >预览</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['quiz:animation:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog title="上传动画HTML" :visible.sync="uploadVisible" width="520px" append-to-body>
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="科目" required>
          <el-select v-model="uploadForm.subjectId" placeholder="请选择科目" clearable style="width: 100%">
            <el-option
              v-for="item in subjectOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="文件" required>
          <el-upload
            :action="uploadAction"
            :headers="uploadHeaders"
            :data="{ subjectId: uploadForm.subjectId }"
            :limit="1"
            :file-list="uploadFileList"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-remove="handleUploadRemove"
            accept=".html,.htm"
          >
            <el-button size="mini" type="primary">选择HTML文件</el-button>
            <div slot="tip" class="el-upload__tip">
              仅支持 <b>.html/.htm</b>，大小不超过 <b>5MB</b>，会直接上传到OSS并入库
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadVisible=false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAnimation, delAnimation } from "@/api/quiz/animation";
import { optionSubject } from "@/api/quiz/subject";
import { getToken } from "@/utils/auth";

export default {
  name: "QuizAnimation",
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      animationList: [],
      subjectOptions: [],
      subjectNameMap: {},
      uploadVisible: false,
      uploadForm: {
        subjectId: undefined
      },
      uploadFileList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        subjectId: undefined,
        animationName: undefined
      }
    };
  },
  computed: {
    uploadAction() {
      return process.env.VUE_APP_BASE_API + "/quiz/animation/upload";
    },
    uploadHeaders() {
      return { Authorization: "Bearer " + getToken() };
    }
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
      const map = {};
      this.subjectOptions.forEach(x => { map[x.value] = x.label; });
      this.subjectNameMap = map;
    },
    getList() {
      this.loading = true;
      listAnimation(this.queryParams).then(response => {
        this.animationList = response.rows || [];
        this.total = response.total || 0;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.animationId);
      this.multiple = !selection.length;
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handlePreview(row) {
      if (row && row.animationUrl) {
        window.open(row.animationUrl, "_blank");
      }
    },
    handleDelete(row) {
      const animationIds = row && row.animationId ? [row.animationId] : this.ids;
      if (!animationIds || !animationIds.length) return;
      this.$modal.confirm('确认删除选中的动画吗？').then(() => {
        return delAnimation(animationIds.join(','));
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('quiz/animation/export', {
        ...this.queryParams
      }, `animation_${new Date().getTime()}.xlsx`)
    },
    openUpload() {
      this.uploadForm.subjectId = this.queryParams.subjectId;
      this.uploadFileList = [];
      this.uploadVisible = true;
    },
    beforeUpload(file) {
      if (!this.uploadForm.subjectId) {
        this.$message.error('请先选择科目');
        return false;
      }
      const ext = (file.name || '').split('.').pop();
      const ok = ['html', 'htm'].includes(String(ext || '').toLowerCase());
      if (!ok) {
        this.$message.error('只允许上传 .html/.htm 文件');
        return false;
      }
      const maxSize = 5; // MB
      const sizeOk = file.size / 1024 / 1024 <= maxSize;
      if (!sizeOk) {
        this.$message.error('文件大小不能超过5MB');
        return false;
      }
      return true;
    },
    handleUploadSuccess(res, file) {
      if (res && res.code === 200) {
        const data = res.data || {};
        this.$message.success('上传成功');
        this.uploadFileList = [{ name: data.animationName || file.name, url: data.url }];
        this.getList();
      } else {
        this.$message.error((res && res.msg) ? res.msg : '上传失败');
      }
    },
    handleUploadRemove() {
      this.uploadFileList = [];
    }
  }
};
</script>

