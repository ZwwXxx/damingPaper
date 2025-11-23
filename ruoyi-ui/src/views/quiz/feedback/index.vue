<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="反馈标题" prop="feedbackTitle">
        <el-input
          v-model="queryParams.feedbackTitle"
          placeholder="请输入反馈标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="反馈类型" prop="feedbackType">
        <el-select v-model="queryParams.feedbackType" placeholder="请选择反馈类型" clearable>
          <el-option label="功能建议" :value="1"/>
          <el-option label="Bug反馈" :value="2"/>
          <el-option label="使用问题" :value="3"/>
          <el-option label="其他" :value="4"/>
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择处理状态" clearable>
          <el-option label="待处理" :value="0"/>
          <el-option label="处理中" :value="1"/>
          <el-option label="已处理" :value="2"/>
          <el-option label="已关闭" :value="3"/>
        </el-select>
      </el-form-item>
      <el-form-item label="优先级" prop="priority">
        <el-select v-model="queryParams.priority" placeholder="请选择优先级" clearable>
          <el-option label="低" :value="1"/>
          <el-option label="中" :value="2"/>
          <el-option label="高" :value="3"/>
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
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['quiz:feedback:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['quiz:feedback:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="feedbackList" @selection-change="handleSelectionChange" style="width: 100%">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="反馈ID" align="center" prop="feedbackId" width="80"/>
      <el-table-column label="用户名称" align="center" prop="userName" width="120"/>
      <el-table-column label="反馈类型" align="center" prop="feedbackType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.feedbackType === 1" type="success">功能建议</el-tag>
          <el-tag v-else-if="scope.row.feedbackType === 2" type="danger">Bug反馈</el-tag>
          <el-tag v-else-if="scope.row.feedbackType === 3" type="warning">使用问题</el-tag>
          <el-tag v-else type="info">其他</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="反馈标题" align="center" prop="feedbackTitle" :show-overflow-tooltip="true" min-width="200"/>
      <el-table-column label="优先级" align="center" prop="priority" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.priority === 3" type="danger">高</el-tag>
          <el-tag v-else-if="scope.row.priority === 2" type="warning">中</el-tag>
          <el-tag v-else type="info">低</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="处理状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" type="warning">待处理</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="primary">处理中</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="success">已处理</el-tag>
          <el-tag v-else type="info">已关闭</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="联系方式" align="center" prop="contactInfo" min-width="130"/>
      <el-table-column label="处理人" align="center" prop="handler" width="100"/>
      <el-table-column label="提交时间" align="center" prop="createTime" min-width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" min-width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >查看</el-button>
          <el-button
            v-if="scope.row.status !== 2 && scope.row.status !== 3"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleProcess(scope.row)"
            v-hasPermi="['quiz:feedback:handle']"
          >处理</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['quiz:feedback:remove']"
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

    <!-- 查看反馈详情对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="反馈ID">{{ viewData.feedbackId }}</el-descriptions-item>
        <el-descriptions-item label="用户名称">{{ viewData.userName }}</el-descriptions-item>
        <el-descriptions-item label="反馈类型">
          <el-tag v-if="viewData.feedbackType === 1" type="success">功能建议</el-tag>
          <el-tag v-else-if="viewData.feedbackType === 2" type="danger">Bug反馈</el-tag>
          <el-tag v-else-if="viewData.feedbackType === 3" type="warning">使用问题</el-tag>
          <el-tag v-else type="info">其他</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag v-if="viewData.priority === 3" type="danger">高</el-tag>
          <el-tag v-else-if="viewData.priority === 2" type="warning">中</el-tag>
          <el-tag v-else type="info">低</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="联系方式" :span="2">{{ viewData.contactInfo }}</el-descriptions-item>
        <el-descriptions-item label="反馈标题" :span="2">{{ viewData.feedbackTitle }}</el-descriptions-item>
        <el-descriptions-item label="反馈内容" :span="2">
          <div style="white-space: pre-wrap;">{{ viewData.feedbackContent }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag v-if="viewData.status === 0" type="warning">待处理</el-tag>
          <el-tag v-else-if="viewData.status === 1" type="primary">处理中</el-tag>
          <el-tag v-else-if="viewData.status === 2" type="success">已处理</el-tag>
          <el-tag v-else type="info">已关闭</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人">{{ viewData.handler }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" :span="2">{{ parseTime(viewData.handleTime) }}</el-descriptions-item>
        <el-descriptions-item label="回复内容" :span="2" v-if="viewData.replyContent">
          <div style="white-space: pre-wrap; background: #f5f7fa; padding: 10px; border-radius: 4px;">
            {{ viewData.replyContent }}
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间" :span="2">{{ parseTime(viewData.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 处理反馈对话框 -->
    <el-dialog title="处理反馈" :visible.sync="processOpen" width="700px" append-to-body>
      <el-form ref="processForm" :model="processForm" :rules="processRules" label-width="100px">
        <el-form-item label="反馈标题">
          <el-input v-model="processForm.feedbackTitle" :disabled="true"/>
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input
            v-model="processForm.feedbackContent"
            type="textarea"
            :rows="4"
            :disabled="true"
          />
        </el-form-item>
        <el-form-item label="处理状态" prop="status">
          <el-radio-group v-model="processForm.status">
            <el-radio :label="1">处理中</el-radio>
            <el-radio :label="2">已处理</el-radio>
            <el-radio :label="3">已关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="processForm.priority">
            <el-radio :label="1">低</el-radio>
            <el-radio :label="2">中</el-radio>
            <el-radio :label="3">高</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="回复内容" prop="replyContent">
          <el-input
            v-model="processForm.replyContent"
            type="textarea"
            :rows="6"
            placeholder="请输入回复内容"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitProcess">确 定</el-button>
        <el-button @click="processOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFeedback, getFeedback, delFeedback, handleFeedback } from "@/api/quiz/feedback";

export default {
  name: "Feedback",
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
      // 反馈表格数据
      feedbackList: [],
      // 弹出层标题
      dialogTitle: "",
      // 是否显示查看弹出层
      viewOpen: false,
      // 是否显示处理弹出层
      processOpen: false,
      // 查看数据
      viewData: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: null,
        feedbackTitle: null,
        feedbackType: null,
        status: null,
        priority: null
      },
      // 处理表单参数
      processForm: {},
      // 处理表单校验
      processRules: {
        status: [
          { required: true, message: "处理状态不能为空", trigger: "change" }
        ],
        replyContent: [
          { required: true, message: "回复内容不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询反馈列表 */
    getList() {
      this.loading = true;
      listFeedback(this.queryParams).then(response => {
        this.feedbackList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
      this.ids = selection.map(item => item.feedbackId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 查看按钮操作 */
    handleView(row) {
      const feedbackId = row.feedbackId;
      getFeedback(feedbackId).then(response => {
        this.viewData = response.data;
        this.viewOpen = true;
        this.dialogTitle = "反馈详情";
      });
    },
    /** 处理按钮操作 */
    handleProcess(row) {
      const feedbackId = row.feedbackId;
      getFeedback(feedbackId).then(response => {
        this.processForm = response.data;
        this.processOpen = true;
      });
    },
    /** 提交处理 */
    submitProcess() {
      this.$refs["processForm"].validate(valid => {
        if (valid) {
          handleFeedback(this.processForm).then(response => {
            this.$modal.msgSuccess("处理成功");
            this.processOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const feedbackIds = row.feedbackId || this.ids;
      this.$modal.confirm('是否确认删除反馈编号为"' + feedbackIds + '"的数据项？').then(function() {
        return delFeedback(feedbackIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('quiz/feedback/export', {
        ...this.queryParams
      }, `feedback_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
