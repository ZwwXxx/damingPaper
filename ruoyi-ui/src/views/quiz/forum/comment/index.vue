<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="帖子ID" prop="postId">
        <el-input
          v-model="queryParams.postId"
          placeholder="请输入帖子ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评论人" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入评论人用户名或昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评论内容" prop="content">
        <el-input
          v-model="queryParams.content"
          placeholder="请输入评论内容"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="正常" value="1" />
          <el-option label="已删除" value="0" />
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
          v-hasPermi="['quiz:forum:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['quiz:forum:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="评论ID" align="center" prop="commentId" width="80" />
      <el-table-column label="帖子ID" align="center" prop="postId" width="80" />
      <el-table-column label="评论人" align="center" width="120">
        <template slot-scope="scope">
          <div style="display: flex; align-items: center; justify-content: center;">
            <el-avatar :size="30" :src="scope.row.avatar" style="margin-right: 8px" v-if="scope.row.avatar"></el-avatar>
            <span>{{ scope.row.nickName || scope.row.userName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="评论内容" align="center" prop="content" :show-overflow-tooltip="true" min-width="250" />
      <el-table-column label="回复" align="center" width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.parentId">
            回复 @{{ scope.row.replyToNickName || scope.row.replyToUserName }}
          </span>
          <span v-else style="color: #909399;">无</span>
        </template>
      </el-table-column>
      <el-table-column label="点赞数" align="center" prop="likeCount" width="80" />
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.status === 1 ? '正常' : '已删除' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['quiz:forum:remove']"
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

    <!-- 查看评论详情对话框 -->
    <el-dialog title="评论详情" :visible.sync="viewOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border v-if="commentDetail.commentId">
        <el-descriptions-item label="评论ID">{{ commentDetail.commentId }}</el-descriptions-item>
        <el-descriptions-item label="帖子ID">{{ commentDetail.postId }}</el-descriptions-item>
        <el-descriptions-item label="评论人" :span="2">
          <el-avatar :size="30" :src="commentDetail.avatar" style="margin-right: 8px" v-if="commentDetail.avatar"></el-avatar>
          {{ commentDetail.nickName || commentDetail.userName }}
        </el-descriptions-item>
        <el-descriptions-item label="评论内容" :span="2">
          <div style="max-height: 200px; overflow-y: auto;">{{ commentDetail.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="回复用户" :span="2" v-if="commentDetail.parentId">
          @{{ commentDetail.replyToNickName || commentDetail.replyToUserName }}
        </el-descriptions-item>
        <el-descriptions-item label="父评论ID" v-if="commentDetail.parentId">{{ commentDetail.parentId }}</el-descriptions-item>
        <el-descriptions-item label="点赞数">{{ commentDetail.likeCount }}</el-descriptions-item>
        <el-descriptions-item label="状态" :span="2">
          <el-tag :type="commentDetail.status === 1 ? 'success' : 'danger'" size="small">
            {{ commentDetail.status === 1 ? '正常' : '已删除' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ parseTime(commentDetail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ parseTime(commentDetail.updateTime) }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listComment, getComment, delComment, delComments } from "@/api/quiz/forum";

export default {
  name: "ForumComment",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 评论表格数据
      commentList: [],
      // 查看对话框
      viewOpen: false,
      // 评论详情
      commentDetail: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        postId: null,
        userName: null,
        content: null,
        status: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询评论列表 */
    getList() {
      this.loading = true;
      listComment(this.queryParams).then(response => {
        this.commentList = response.rows;
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
      this.ids = selection.map(item => item.commentId)
      this.multiple = !selection.length
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.viewOpen = true;
      const commentId = row.commentId;
      getComment(commentId).then(response => {
        this.commentDetail = response.data;
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const commentIds = row.commentId || this.ids;
      this.$modal.confirm('是否确认删除评论编号为"' + commentIds + '"的数据项？').then(() => {
        return delComment(commentIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('quiz/admin/forum/comment/export', {
        ...this.queryParams
      }, `forum_comment_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
