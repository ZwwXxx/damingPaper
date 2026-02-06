<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="题目类型" prop="questionType">
        <el-select v-model="queryParams.questionType" placeholder="请选择题目类型" clearable>
          <el-option
            v-for="option in questionTypeOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
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
          icon="el-icon-plus"
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
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['quiz:question:import']"
        >导入
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-document"
          size="mini"
          :disabled="multiple"
          @click="handleExportSelected"
          v-hasPermi="['quiz:question:export']"
        >导出选中
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
        >导出全部
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="questionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="题目id" align="center" prop="id"/>
      <el-table-column label="题目类型" align="center" prop="questionType">
        <template slot-scope="scope">
          <div class="type-chip"
               :style="{borderColor: getTypeMeta(scope.row.questionType).color, color: getTypeMeta(scope.row.questionType).color}">
            <i :class="getTypeMeta(scope.row.questionType).icon"></i>
            <span class="ml-1">{{ getTypeMeta(scope.row.questionType).label }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="题目题干内容" align="left" min-width="260">
        <template slot-scope="scope">
          <div class="question-title-cell">
            <span class="question-title-text">
              {{ getQuestionTitlePreview(scope.row.questionTitle) }}
            </span>
            <el-button
              v-if="shouldShowQuestionPreview(scope.row.questionTitle)"
              type="text"
              size="mini"
              @click="openQuestionPreview(scope.row)"
            >
              查看详情
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="题目分数" align="center" prop="score"/>
      <el-table-column label="科目" align="center" prop="subjectId">
        <template slot-scope="scope">
          <span>{{ getSubjectLabel(scope.row.subjectId) }}</span>
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
          <el-button
            size="mini"
            type="text"
            icon="el-icon-download"
            @click="handleExportSingle(scope.row)"
            v-hasPermi="['quiz:question:export']"
          >导出
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

    <el-dialog :title="upload.title" :visible.sync="upload.open" width="480px" append-to-body>
      <el-upload
        ref="upload"
        drag
        :limit="1"
        accept=".xls,.xlsx"
        :auto-upload="false"
        :headers="upload.headers"
        :action="upload.url"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :on-error="handleFileError">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或 <em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">
          <p>⭐ 模板已提供5种题型的完整示例：</p>
          <ul style="font-size: 12px; margin: 5px 0; padding-left: 20px;">
            <li><strong>单选题/多选题：</strong>需要填写完整选项 JSON 数组</li>
            <li><strong>判断题：</strong>选项会自动生成，答案填A(正确)或B(错误)</li>
            <li><strong>主观题/填空题：</strong>选项字段留空或填"[]"</li>
          </ul>
          <p>📎 仅支持 .xls/.xlsx 格式</p>
        </div>
      </el-upload>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" :loading="upload.isUploading" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
        <el-link type="primary" :underline="false" @click="importTemplate">下载模板</el-link>
      </span>
    </el-dialog>

    <el-dialog
      title="题干预览"
      :visible.sync="previewDialog.visible"
      width="60%"
      append-to-body>
      <div v-if="previewDialog.content" class="question-preview-body" v-html="previewDialog.content"></div>
      <el-empty v-else description="暂无题干内容"/>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="previewDialog.visible=false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {listQuestion, getQuestion, delQuestion, addQuestion, updateQuestion} from "@/api/quiz/question";
import {optionSubject} from "@/api/quiz/subject";
import { getToken } from "@/utils/auth";

export default {
  name: "Question",
  dicts: ['question_type'],
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
      },
      subjectOptions: [],
      subjectMap: {},
      questionTypeOptions: [
        {label: '单选题', value: '1'},
        {label: '多选题', value: '2'},
        {label: '主观题', value: '3'},
        {label: '判断题', value: '4'},
        {label: '填空题', value: '5'}
      ],
      questionTypeMeta: {
        '1': {label: '单选题', icon: 'el-icon-circle-check', color: '#409EFF'},
        '2': {label: '多选题', icon: 'el-icon-s-operation', color: '#67C23A'},
        '3': {label: '主观题', icon: 'el-icon-edit', color: '#E6A23C'},
        '4': {label: '判断题', icon: 'el-icon-s-check', color: '#F56C6C'},
        '5': {label: '填空题', icon: 'el-icon-document', color: '#9C27B0'}
      },
      upload: {
        open: false,
        title: "题目导入",
        isUploading: false,
        headers: { Authorization: "Bearer " + getToken() },
        url: process.env.VUE_APP_BASE_API + "/quiz/question/importData"
      },
      previewDialog: {
        visible: false,
        title: '',
        content: ''
      }
    };
  },
  created() {
    this.ensureDictTypes();
    this.loadSubjectOptions();
    this.getList();
    console.log(this.dict)
  },
  watch: {
    'dict.type.question_type': {
      handler() {
        this.ensureDictTypes();
      },
      deep: true
    }
  },
  methods: {
    ensureDictTypes() {
      const fallbackTypes = [
        {label: '单选题', value: '1', listClass: 'primary'},
        {label: '多选题', value: '2', listClass: 'success'},
        {label: '主观题', value: '3', listClass: 'warning'},
        {label: '判断题', value: '4', listClass: 'info'},
        {label: '填空题', value: '5', listClass: 'default'}
      ];
      const dictList = this.dict?.type?.question_type;
      if (!Array.isArray(dictList) || !dictList.length) {
        this.questionTypeOptions = fallbackTypes;
        return;
      }
      fallbackTypes.forEach(item => {
        const exists = dictList.some(dict => String(dict.value) === item.value);
        if (!exists) {
          dictList.push({...item});
        }
      });
      this.questionTypeOptions = dictList.map(item => ({
        label: item.label,
        value: String(item.value)
      }));
    },
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
    getTypeMeta(value) {
      const key = String(value);
      if (this.questionTypeMeta[key]) {
        return this.questionTypeMeta[key];
      }
      const dictItem = (this.dict?.type?.question_type || []).find(item => String(item.value) === key);
      if (dictItem) {
        return {label: dictItem.label, icon: 'el-icon-help', color: '#909399'};
      }
      return {label: key || '-', icon: 'el-icon-help', color: '#909399'};
    },
    addQuestionType() {

    },
    stripHtml(text) {
      if (!text) return '';
      return String(text).replace(/<[^>]+>/g, '').replace(/\s+/g, ' ').trim();
    },
    getQuestionTitlePreview(title) {
      const plain = this.stripHtml(title);
      if (plain.length <= 40) {
        return plain || '-';
      }
      return plain.slice(0, 40) + '...';
    },
    shouldShowQuestionPreview(title) {
      const plain = this.stripHtml(title);
      return plain.length > 40;
    },
    openQuestionPreview(row) {
      this.previewDialog.title = `题目ID：${row.id}`;
      this.previewDialog.content = row.questionTitle || '';
      this.previewDialog.visible = true;
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
    },
    /** 导出选中 */
    handleExportSelected() {
      if (!this.ids.length) {
        this.$modal.msgWarning("请选择要导出的题目");
        return;
      }
      this.download('quiz/question/exportByIds', {
        ids: this.ids.join(',')
      }, `question_selected_${new Date().getTime()}.xlsx`)
    },
    /** 单个导出 */
    handleExportSingle(row) {
      if (!row || !row.id) {
        this.$modal.msgWarning("未找到题目信息");
        return;
      }
      this.download('quiz/question/exportByIds', {
        ids: row.id
      }, `question_${row.id}_${new Date().getTime()}.xlsx`)
    },
    /** 导入按钮 */
    handleImport() {
      this.upload.title = "题目导入";
      this.upload.open = true;
    },
    /** 下载模板 */
    importTemplate() {
      this.download('quiz/question/importTemplate', {}, `question_template_${new Date().getTime()}.xlsx`)
    },
    handleFileUploadProgress() {
      this.upload.isUploading = true;
    },
    handleFileSuccess(response) {
      this.upload.isUploading = false;
      this.upload.open = false;
      if (this.$refs.upload) {
        this.$refs.upload.clearFiles();
      }
      this.$alert("<div style='overflow:auto;max-height:60vh;padding:10px 20px;'>" + response.msg + "</div>", "导入结果", {dangerouslyUseHTMLString: true});
      this.getList();
    },
    handleFileError() {
      this.upload.isUploading = false;
    },
    submitFileForm() {
      this.$refs.upload.submit();
    }
  }
};
</script>

<style scoped>
.question-title-cell {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 8px;
}
.question-title-text {
  max-width: 360px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.question-preview-body {
  max-height: 60vh;
  overflow-y: auto;
  padding: 8px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.type-chip {
  display: inline-flex;
  align-items: center;
  border: 1px solid;
  border-radius: 16px;
  padding: 2px 10px;
  font-size: 12px;
}
</style>
