<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="知识点标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入知识点标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属科目" prop="subjectId">
        <el-select v-model="queryParams.subjectId" placeholder="请选择科目" clearable style="width: 160px">
          <el-option
            v-for="item in subjectOptions"
            :key="item.subjectId"
            :label="item.subjectName"
            :value="item.subjectId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="请选择审核状态" clearable>
          <el-option label="待审核" :value="0"/>
          <el-option label="已通过" :value="1"/>
          <el-option label="已拒绝" :value="2"/>
        </el-select>
      </el-form-item>
      <el-form-item label="发布状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="已发布" :value="1"/>
          <el-option label="草稿" :value="0"/>
          <el-option label="已下架" :value="3"/>
        </el-select>
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
          v-hasPermi="['knowledge:point:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="multiple"
          @click="handleBatchAudit(1)"
          v-hasPermi="['knowledge:point:audit']"
        >批量通过</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-close"
          size="mini"
          :disabled="multiple"
          @click="handleBatchAudit(2)"
          v-hasPermi="['knowledge:point:audit']"
        >批量拒绝</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['knowledge:point:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="knowledgeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="ID" prop="pointId" width="70" align="center"/>
      <el-table-column label="标题" prop="title" min-width="180" :show-overflow-tooltip="true"/>
      <el-table-column label="科目" prop="subjectName" width="120" align="center"/>
      <el-table-column label="作者" prop="authorName" width="100" align="center"/>
      <el-table-column label="难度" prop="difficulty" width="80" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.difficulty === 1" type="success" size="small">简单</el-tag>
          <el-tag v-else-if="scope.row.difficulty === 2" type="warning" size="small">中等</el-tag>
          <el-tag v-else-if="scope.row.difficulty === 3" type="danger" size="small">困难</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" prop="auditStatus" width="90" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.auditStatus === 0" type="warning" size="small">待审核</el-tag>
          <el-tag v-else-if="scope.row.auditStatus === 1" type="success" size="small">已通过</el-tag>
          <el-tag v-else-if="scope.row.auditStatus === 2" type="danger" size="small">已拒绝</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="浏览/点赞" width="100" align="center">
        <template slot-scope="scope">
          {{ scope.row.viewCount || 0 }} / {{ scope.row.likeCount || 0 }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="165" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['knowledge:point:detail']"
          >查看</el-button>
          <el-button
            v-if="scope.row.auditStatus === 0"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleAudit(scope.row, 1)"
            v-hasPermi="['knowledge:point:audit']"
          >通过</el-button>
          <el-button
            v-if="scope.row.auditStatus === 0"
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleAudit(scope.row, 2)"
            v-hasPermi="['knowledge:point:audit']"
          >拒绝</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['knowledge:point:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['knowledge:point:remove']"
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

    <!-- 查看知识点详情对话框 -->
    <el-dialog :title="'知识点详情'" :visible.sync="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border v-if="viewData.pointId">
        <el-descriptions-item label="知识点ID">{{ viewData.pointId }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ viewData.title }}</el-descriptions-item>
        <el-descriptions-item label="所属科目">{{ viewData.subjectName }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ viewData.authorName }}</el-descriptions-item>
        <el-descriptions-item label="难度">
          <el-tag v-if="viewData.difficulty === 1" type="success" size="small">简单</el-tag>
          <el-tag v-else-if="viewData.difficulty === 2" type="warning" size="small">中等</el-tag>
          <el-tag v-else-if="viewData.difficulty === 3" type="danger" size="small">困难</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag v-if="viewData.auditStatus === 0" type="warning" size="small">待审核</el-tag>
          <el-tag v-else-if="viewData.auditStatus === 1" type="success" size="small">已通过</el-tag>
          <el-tag v-else-if="viewData.auditStatus === 2" type="danger" size="small">已拒绝</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="摘要" :span="2">{{ viewData.summary || '-' }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">
          <div class="knowledge-content-preview content-box" v-html="formatContent(viewData.content)"></div>
        </el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2" v-if="viewData.auditRemark">{{ viewData.auditRemark }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(viewData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(viewData.updateTime) }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 新增/修改知识点对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="750px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入知识点标题" maxlength="200" show-word-limit/>
        </el-form-item>
        <el-form-item label="所属科目" prop="subjectId">
          <el-select v-model="form.subjectId" placeholder="请选择科目" style="width: 100%">
            <el-option
              v-for="item in subjectOptions"
              :key="item.subjectId"
              :label="item.subjectName"
              :value="item.subjectId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="摘要" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="请输入摘要（可选）" maxlength="500" show-word-limit/>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="12" placeholder="请输入知识点内容（支持Markdown格式）"/>
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-radio-group v-model="form.difficulty">
            <el-radio :label="1">简单</el-radio>
            <el-radio :label="2">中等</el-radio>
            <el-radio :label="3">困难</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否推荐" prop="isRecommend">
          <el-radio-group v-model="form.isRecommend">
            <el-radio :label="0">否</el-radio>
            <el-radio :label="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否置顶" prop="isTop">
          <el-radio-group v-model="form.isTop">
            <el-radio :label="0">否</el-radio>
            <el-radio :label="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog :title="auditTitle" :visible.sync="auditOpen" width="500px" append-to-body>
      <el-form ref="auditForm" :model="auditForm" label-width="100px">
        <el-form-item label="审核备注" prop="auditRemark">
          <el-input v-model="auditForm.auditRemark" type="textarea" :rows="3" placeholder="拒绝时可填写原因"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAudit">确 定</el-button>
        <el-button @click="auditOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { marked } from 'marked';
import {
  listKnowledge,
  getKnowledge,
  addKnowledge,
  updateKnowledge,
  delKnowledge,
  auditKnowledge,
  batchAuditKnowledge,
  listKnowledgeSubjects
} from "@/api/quiz/knowledge";

export default {
  name: "KnowledgePoint",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      knowledgeList: [],
      subjectOptions: [],
      title: "",
      open: false,
      viewOpen: false,
      auditOpen: false,
      auditTitle: "",
      viewData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        subjectId: null,
        auditStatus: null,
        status: null
      },
      form: {},
      auditForm: {
        pointId: null,
        pointIds: [],
        auditStatus: null,
        auditRemark: null
      },
      rules: {
        title: [{ required: true, message: "知识点标题不能为空", trigger: "blur" }],
        subjectId: [{ required: true, message: "请选择所属科目", trigger: "change" }],
        content: [{ required: true, message: "知识点内容不能为空", trigger: "blur" }]
      }
    };
  },
  created() {
    this.getList();
    this.loadSubjects();
  },
  methods: {
    /** 格式化内容：使用与 daming-front 一致的 marked 渲染 Markdown */
    formatContent(content) {
      if (!content) return '-';
      try {
        return marked(content);
      } catch (e) {
        console.error('Markdown渲染失败:', e);
        return content.replace(/\n/g, '<br>');
      }
    },
    loadSubjects() {
      listKnowledgeSubjects().then(res => {
        this.subjectOptions = res.data || [];
      });
    },
    getList() {
      this.loading = true;
      listKnowledge(this.queryParams).then(response => {
        this.knowledgeList = response.rows || [];
        this.total = response.total || 0;
        this.loading = false;
      });
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
      this.ids = selection.map(item => item.pointId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    handleAdd() {
      this.reset();
      this.form = {
        pointId: null,
        subjectId: null,
        title: "",
        summary: "",
        content: "",
        difficulty: 1,
        isRecommend: 0,
        isTop: 0
      };
      this.open = true;
      this.title = "新增知识点";
    },
    handleUpdate(row) {
      this.reset();
      const pointId = row.pointId;
      getKnowledge(pointId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改知识点";
      });
    },
    handleView(row) {
      const pointId = row.pointId;
      getKnowledge(pointId).then(response => {
        this.viewData = response.data;
        this.viewOpen = true;
      });
    },
    handleAudit(row, auditStatus) {
      this.auditForm = {
        pointId: row.pointId,
        pointIds: [],
        auditStatus: auditStatus,
        auditRemark: null
      };
      this.auditTitle = auditStatus === 1 ? "审核通过" : "审核拒绝";
      this.auditOpen = true;
    },
    submitAudit() {
      const { pointId, pointIds, auditStatus, auditRemark } = this.auditForm;
      if (pointIds && pointIds.length > 0) {
        batchAuditKnowledge({ pointIds, auditStatus, auditRemark }).then(() => {
          this.$modal.msgSuccess("批量审核成功");
          this.auditOpen = false;
          this.getList();
        });
      } else {
        auditKnowledge({ pointId, auditStatus, auditRemark }).then(() => {
          this.$modal.msgSuccess("审核成功");
          this.auditOpen = false;
          this.getList();
        });
      }
    },
    handleBatchAudit(auditStatus) {
      this.auditForm = {
        pointId: null,
        pointIds: this.ids,
        auditStatus: auditStatus,
        auditRemark: null
      };
      this.auditTitle = auditStatus === 1 ? "批量审核通过" : "批量审核拒绝";
      this.auditOpen = true;
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) return;
        if (this.form.pointId) {
          updateKnowledge(this.form).then(() => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
        } else {
          addKnowledge(this.form).then(() => {
            this.$modal.msgSuccess("新增成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    handleDelete(row) {
      const pointIds = row.pointId ? [row.pointId] : this.ids;
      this.$modal.confirm('是否确认删除所选知识点？').then(() => {
        return delKnowledge(pointIds.join(","));
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    reset() {
      this.form = {};
      this.resetForm("form");
    },
    cancel() {
      this.open = false;
      this.reset();
    }
  }
};
</script>

<style scoped>
.knowledge-content-preview {
  max-height: 360px;
  overflow-y: auto;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1.8;
  color: #606266;
}

/* 与 daming-front 知识点详情一致的 Markdown 样式 */
.knowledge-content-preview >>> h1,
.knowledge-content-preview >>> h2,
.knowledge-content-preview >>> h3,
.knowledge-content-preview >>> h4,
.knowledge-content-preview >>> h5,
.knowledge-content-preview >>> h6 {
  font-weight: bold;
  color: #303133;
  margin: 20px 0 10px;
  line-height: 1.4;
}
.knowledge-content-preview >>> h1 { font-size: 22px; border-bottom: 2px solid #ebeef5; padding-bottom: 8px; }
.knowledge-content-preview >>> h2 { font-size: 20px; border-bottom: 1px solid #ebeef5; padding-bottom: 6px; }
.knowledge-content-preview >>> h3 { font-size: 18px; }
.knowledge-content-preview >>> h4 { font-size: 16px; }
.knowledge-content-preview >>> h5,
.knowledge-content-preview >>> h6 { font-size: 14px; }
.knowledge-content-preview >>> p { margin: 10px 0; }
.knowledge-content-preview >>> ul,
.knowledge-content-preview >>> ol {
  padding-left: 30px;
  margin: 10px 0;
  list-style-position: outside;
}
.knowledge-content-preview >>> ul { list-style-type: disc !important; }
.knowledge-content-preview >>> ol { list-style-type: decimal !important; }
.knowledge-content-preview >>> li { margin: 5px 0; display: list-item !important; list-style: inherit !important; }
.knowledge-content-preview >>> code {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', Courier, monospace;
  font-size: 13px;
  color: #e83e8c;
}
.knowledge-content-preview >>> pre {
  background: #e6e8eb;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 12px;
  overflow-x: auto;
  margin: 12px 0;
}
.knowledge-content-preview >>> pre code {
  background: transparent;
  padding: 0;
  color: #303133;
}
.knowledge-content-preview >>> blockquote {
  border-left: 4px solid #409eff;
  padding-left: 15px;
  margin: 12px 0;
  color: #606266;
  background: #f5f7fa;
  padding: 10px 15px;
}
.knowledge-content-preview >>> table {
  border-collapse: collapse;
  width: 100%;
  margin: 12px 0;
}
.knowledge-content-preview >>> table th,
.knowledge-content-preview >>> table td {
  border: 1px solid #dcdfe6;
  padding: 8px 12px;
  text-align: left;
}
.knowledge-content-preview >>> table th {
  background: #f5f7fa;
  font-weight: 600;
  color: #303133;
}
</style>
