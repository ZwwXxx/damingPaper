<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="试卷名称" prop="paperName">
        <el-input
          v-model="queryParams.paperName"
          placeholder="请输入试卷名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="科目ID" prop="subjectId">
        <el-select v-model="queryParams.subjectId" placeholder="请选择科目" clearable>
          <el-option
            v-for="option in subjectOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="试卷总分" prop="score">
        <el-input
          v-model="queryParams.score"
          placeholder="请输入试卷总分"
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
      <el-form-item label="试卷类型" prop="paperType">
        <el-select v-model="queryParams.paperType" placeholder="请选择试卷类型" clearable>
          <el-option
            v-for="dict in dict.type.paper_type"
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
          v-hasPermi="['quiz:paper:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['quiz:paper:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['quiz:paper:remove']"
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
          v-hasPermi="['quiz:paper:export']"
        >导出
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-document"
          size="mini"
          :disabled="single"
          @click="handleExportSync"
          v-hasPermi="['quiz:paper:query']"
        >导出同步包
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="openImportSyncDialog"
          v-hasPermi="['quiz:paper:add']"
        >导入同步包
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="paperList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="试卷ID" align="center" prop="paperId"/>
      <el-table-column label="试卷名称" align="center" prop="paperName"/>
      <el-table-column label="科目ID" align="center" prop="subjectId">
        <template slot-scope="scope">
          <span>{{ getSubjectLabel(scope.row.subjectId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="试卷总分" align="center" prop="score"/>
      <el-table-column label="题目数量" align="center" prop="questionCount"/>
      <el-table-column label="考试时长" align="center" prop="suggestTime"/>
      <el-table-column label="试卷类型" align="center" prop="paperType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.paper_type" :value="scope.row.paperType"/>
        </template>
      </el-table-column>
      <el-table-column label="防作弊" align="center" prop="enableAntiCheat">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enableAntiCheat ? 'success' : 'info'">
            {{ scope.row.enableAntiCheat ? '开启' : '关闭' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <!--<span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>-->
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['quiz:paper:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['quiz:paper:remove']"
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

    <!-- 添加或修改试卷对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="试卷名称" prop="paperName">
          <el-input v-model="form.paperName" placeholder="请输入试卷名称"/>
        </el-form-item>
        <el-form-item label="科目ID" prop="subjectId">
          <el-select v-model="form.subjectId" placeholder="请选择科目">
            <el-option
              v-for="option in subjectOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="试卷总分" prop="score">
          <el-input v-model="form.score" placeholder="请输入试卷总分"/>
        </el-form-item>
        <el-form-item label="试卷类型" prop="paperType">
          <el-input v-model="form.paperType" placeholder="请输入试卷类型"/>
        </el-form-item>
        <el-form-item label="题目数量" prop="questionCount">
          <el-input v-model="form.questionCount" placeholder="请输入题目数量"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="导入试卷同步包" :visible.sync="importSyncOpen" width="520px" append-to-body>
      <div style="margin-bottom: 10px; color: #909399;">
        请选择由“导出同步包”得到的 JSON 文件。
      </div>
      <el-upload
        ref="syncUpload"
        drag
        action="#"
        :auto-upload="false"
        :show-file-list="true"
        :limit="1"
        :on-change="handleSyncFileChange"
        :on-remove="handleSyncFileRemove"
        :before-upload="() => false"
        accept=".json,application/json"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将 JSON 文件拖到此处，或<em>点击上传</em></div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="importSyncOpen = false">取 消</el-button>
        <el-button type="primary" :loading="importingSync" @click="submitImportSync">确 定 导 入</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listPaper,
  delPaper,
  addPaper,
  updatePaper,
  exportPaperSyncPackage,
  importPaperSyncPackage
} from "@/api/quiz/paper";
import {optionSubject} from "@/api/quiz/subject";

export default {
  dicts: ['paper_type'],
  name: "Paper",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      // 日期范围
      dateRange: [],
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 试卷表格数据
      paperList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 同步包导入弹窗
      importSyncOpen: false,
      // 上传的同步包文件
      syncImportFile: null,
      // 是否正在导入
      importingSync: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        paperName: null,
        subjectId: null,
        score: null,
        questionCount: null,
        paperType:null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        paperName: [
          {required: true, message: "试卷名称不能为空", trigger: "blur"}
        ],
        subjectId: [
          {required: true, message: "科目ID不能为空", trigger: "change"}
        ],
        score: [
          {required: true, message: "试卷总分不能为空", trigger: "blur"}
        ],
        questionCount: [
          {required: true, message: "题目数量不能为空", trigger: "blur"}
        ],
        paperType: [
          {required: true, message: "试卷类型不能为空", trigger: "blur"}
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
      const data = res.data || [];
      this.subjectOptions = data.map(item => ({
        label: item.subjectName,
        value: item.subjectId
      }));
      this.subjectMap = data.reduce((acc, cur) => {
        acc[cur.subjectId] = cur.subjectName;
        return acc;
      }, {});
    },
    getSubjectLabel(subjectId) {
      return this.subjectMap[subjectId] || '-';
    },
    /** 查询试卷列表 */
    getList() {
      this.loading = true;
      listPaper(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.paperList = response.rows;
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
        paperId: null,
        paperName: null,
        subjectId: null,
        score: null,
        questionCount: null,
        delFlag: [],
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
      this.ids = selection.map(item => item.paperId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
     this.$router.push({path:'/paper/edit'})
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      // this.reset();
      // const paperId = row.paperId || this.ids
      // getPaper(paperId).then(response => {
      //   this.form = response.data;
      //   this.form.delFlag = this.form.delFlag.split(",");
      //   this.open = true;
      //   this.title = "修改试卷";
      // });
      console.log(row.paperId)
      this.$router.push({
        path: '/paper/edit',
        query: {paperId:row.paperId}
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.delFlag = this.form.delFlag.join(",");
          if (this.form.paperId != null) {
            updatePaper(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPaper(this.form).then(response => {
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
      const paperIds = row.paperId || this.ids;
      this.$modal.confirm('是否确认删除试卷编号为"' + paperIds + '"的数据项？').then(function () {
        return delPaper(paperIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('quiz/paper/export', {
        ...this.queryParams
      }, `paper_${new Date().getTime()}.xlsx`)
    },
    /** 导出同步包 */
    handleExportSync(row) {
      const paperId = (row && row.paperId) || this.ids[0];
      if (!paperId) {
        this.$modal.msgWarning("请先选择一条试卷");
        return;
      }
      exportPaperSyncPackage(paperId).then(res => {
        const payload = res && res.data ? res.data : {};
        const paperName = (payload.paper && payload.paper.paperName) || `paper_${paperId}`;
        const safeName = String(paperName).replace(/[\\/:*?"<>|]/g, "_");
        const content = JSON.stringify(payload, null, 2);
        const blob = new Blob([content], {type: "application/json;charset=utf-8"});
        const fileName = `${safeName}_sync_${new Date().getTime()}.json`;
        if (window.navigator.msSaveOrOpenBlob) {
          window.navigator.msSaveBlob(blob, fileName);
          return;
        }
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", fileName);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
        this.$modal.msgSuccess("同步包导出成功");
      });
    },
    openImportSyncDialog() {
      this.syncImportFile = null;
      this.importSyncOpen = true;
      this.$nextTick(() => {
        if (this.$refs.syncUpload) {
          this.$refs.syncUpload.clearFiles();
        }
      });
    },
    handleSyncFileChange(file) {
      this.syncImportFile = file ? file.raw : null;
    },
    handleSyncFileRemove() {
      this.syncImportFile = null;
    },
    submitImportSync() {
      if (!this.syncImportFile) {
        this.$modal.msgWarning("请先选择 JSON 文件");
        return;
      }
      const reader = new FileReader();
      reader.onload = async e => {
        try {
          const text = e.target && e.target.result ? e.target.result : "";
          const payload = JSON.parse(text);
          this.importingSync = true;
          const res = await importPaperSyncPackage(payload);
          const data = res && res.data ? res.data : {};
          this.$modal.msgSuccess(
            `导入成功，新试卷ID：${data.newPaperId || "-"}，新增题目：${data.insertedQuestionCount || 0}`
          );
          this.importSyncOpen = false;
          this.getList();
        } catch (error) {
          this.$modal.msgError("导入失败，请检查 JSON 文件格式和内容");
        } finally {
          this.importingSync = false;
        }
      };
      reader.onerror = () => {
        this.$modal.msgError("文件读取失败，请重试");
      };
      reader.readAsText(this.syncImportFile, "utf-8");
    }
  }
};
</script>
