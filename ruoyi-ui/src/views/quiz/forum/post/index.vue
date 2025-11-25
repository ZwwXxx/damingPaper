<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="帖子标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入帖子标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发帖人" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入发帖人用户名或昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="正常" value="1" />
          <el-option label="审核中" value="2" />
          <el-option label="已删除" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="置顶" prop="isTop">
        <el-select v-model="queryParams.isTop" placeholder="请选择置顶状态" clearable>
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="热门" prop="isHot">
        <el-select v-model="queryParams.isHot" placeholder="请选择热门状态" clearable>
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
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

    <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="帖子ID" align="center" prop="postId" width="80" />
      <el-table-column label="标题" align="center" prop="title" :show-overflow-tooltip="true" min-width="200" />
      <el-table-column label="发帖人" align="center" width="120">
        <template slot-scope="scope">
          <div style="display: flex; align-items: center; justify-content: center;">
            <el-avatar :size="30" :src="scope.row.avatar" style="margin-right: 8px" v-if="scope.row.avatar"></el-avatar>
            <span>{{ scope.row.nickName || scope.row.userName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="浏览" align="center" prop="viewCount" width="80" />
      <el-table-column label="点赞" align="center" prop="likeCount" width="80" />
      <el-table-column label="评论" align="center" prop="commentCount" width="80" />
      <el-table-column label="置顶" align="center" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isTop === 1 ? 'danger' : 'info'" size="small">
            {{ scope.row.isTop === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="热门" align="center" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isHot === 1 ? 'warning' : 'info'" size="small">
            {{ scope.row.isHot === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="statusTypeMap[scope.row.status]" size="small">
            {{ statusMap[scope.row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
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
            icon="el-icon-top"
            @click="handleToggleTop(scope.row)"
            v-hasPermi="['quiz:forum:edit']"
          >{{ scope.row.isTop === 1 ? '取消置顶' : '置顶' }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-star-off"
            @click="handleToggleHot(scope.row)"
            v-hasPermi="['quiz:forum:edit']"
          >{{ scope.row.isHot === 1 ? '取消热门' : '热门' }}</el-button>
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

    <!-- 查看帖子详情对话框 -->
    <el-dialog :title="'帖子详情 - ' + postDetail.title" :visible.sync="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border v-if="postDetail.postId">
        <el-descriptions-item label="帖子ID">{{ postDetail.postId }}</el-descriptions-item>
        <el-descriptions-item label="发帖人">
          <el-avatar :size="30" :src="postDetail.avatar" style="margin-right: 8px" v-if="postDetail.avatar"></el-avatar>
          {{ postDetail.nickName || postDetail.userName }}
        </el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ postDetail.title }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">
          <div v-html="postDetail.content" style="max-height: 300px; overflow-y: auto;"></div>
        </el-descriptions-item>
        <el-descriptions-item label="图片" :span="2" v-if="postDetail.images && postDetail.images.length > 0">
          <el-image
            v-for="(img, index) in postDetail.images"
            :key="index"
            :src="img"
            :preview-src-list="postDetail.images"
            style="width: 100px; height: 100px; margin-right: 10px;"
          ></el-image>
        </el-descriptions-item>
        <el-descriptions-item label="浏览次数">{{ postDetail.viewCount }}</el-descriptions-item>
        <el-descriptions-item label="点赞数">{{ postDetail.likeCount }}</el-descriptions-item>
        <el-descriptions-item label="评论数">{{ postDetail.commentCount }}</el-descriptions-item>
        <el-descriptions-item label="置顶状态">
          <el-tag :type="postDetail.isTop === 1 ? 'danger' : 'info'" size="small">
            {{ postDetail.isTop === 1 ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="热门状态">
          <el-tag :type="postDetail.isHot === 1 ? 'warning' : 'info'" size="small">
            {{ postDetail.isHot === 1 ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTypeMap[postDetail.status]" size="small">
            {{ statusMap[postDetail.status] }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ parseTime(postDetail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ parseTime(postDetail.updateTime) }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPost, getPost, delPost, delPosts, toggleTop, toggleHot } from "@/api/quiz/forum";

export default {
  name: "ForumPost",
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
      // 帖子表格数据
      postList: [],
      // 查看对话框
      viewOpen: false,
      // 帖子详情
      postDetail: {},
      // 状态字典
      statusMap: {
        0: '已删除',
        1: '正常',
        2: '审核中'
      },
      statusTypeMap: {
        0: 'danger',
        1: 'success',
        2: 'warning'
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        userName: null,
        status: null,
        isTop: null,
        isHot: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询帖子列表 */
    getList() {
      this.loading = true;
      listPost(this.queryParams).then(response => {
        this.postList = response.rows;
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
      this.ids = selection.map(item => item.postId)
      this.multiple = !selection.length
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.viewOpen = true;
      const postId = row.postId;
      getPost(postId).then(response => {
        this.postDetail = response.data;
        // 解析图片JSON
        if (this.postDetail.imagesJson) {
          try {
            this.postDetail.images = JSON.parse(this.postDetail.imagesJson);
          } catch (e) {
            this.postDetail.images = [];
          }
        }
      });
    },
    /** 置顶/取消置顶按钮操作 */
    handleToggleTop(row) {
      const newIsTop = row.isTop === 1 ? 0 : 1;
      const text = newIsTop === 1 ? "置顶" : "取消置顶";
      this.$modal.confirm('确认要' + text + '该帖子吗？').then(() => {
        return toggleTop(row.postId, newIsTop);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(text + "成功");
      }).catch(() => {});
    },
    /** 热门/取消热门按钮操作 */
    handleToggleHot(row) {
      const newIsHot = row.isHot === 1 ? 0 : 1;
      const text = newIsHot === 1 ? "设为热门" : "取消热门";
      this.$modal.confirm('确认要' + text + '该帖子吗？').then(() => {
        return toggleHot(row.postId, newIsHot);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(text + "成功");
      }).catch(() => {});
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const postIds = row.postId || this.ids;
      this.$modal.confirm('是否确认删除帖子编号为"' + postIds + '"的数据项？').then(() => {
        return delPost(postIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('quiz/admin/forum/post/export', {
        ...this.queryParams
      }, `forum_post_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
