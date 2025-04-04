<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户名" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户昵称" prop="nickName">
        <el-input
          v-model="queryParams.nickName"
          placeholder="请输入用户昵称"
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
          v-hasPermi="['quiz:user:add']"
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
          v-hasPermi="['quiz:user:edit']"
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
          v-hasPermi="['quiz:user:remove']"
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
          v-hasPermi="['quiz:user:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="用户id" align="center" prop="userId"/>
      <el-table-column label="头像" align="center">
        <template slot-scope="scope">
          <el-image v-if="apiUrl+scope.row.avatar"
                    :src="apiUrl+scope.row.avatar"
                    style="width: 50px;height: 50px;border-radius: 50%"
                    :preview-src-list="[apiUrl+scope.row.avatar]"
          ></el-image>
        </template>
      </el-table-column>
      <el-table-column label="用户名" align="center" prop="userName"/>
      <el-table-column label="用户昵称" align="center" prop="nickName"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['quiz:user:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['quiz:user:remove']"
          >删除
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleResetPwd(scope.row)"
            v-hasPermi="['quiz:user:resetPwd']"
          >重置密码
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

    <!-- 添加或修改刷题用户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body  >
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="头像">
          <el-upload
            :http-request="requestUpload"
            action="#"
            :before-upload="beforeAvatarUpload"
            :show-file-list="false"

          >
            <img :src="blob!=''? options.img:apiUrl+options.img"
                 class="cursor-pointer"
                 style="width: 120px;height: 120px; border-radius: 100%;box-shadow: 1px 1px 10px black ;border: 4px white solid">
          </el-upload>
        </el-form-item>
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名"/>
        </el-form-item>
        <el-form-item label="用户昵称" prop="nickName">
          <el-input v-model="form.nickName" placeholder="请输入用户昵称"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>


    <el-dialog title="重置密码" :visible.sync="resetDialogOpen" width="500px" :close-on-click-modal="false"
               append-to-body >
      <el-input v-model="newPwd.password"></el-input>
      <el-button @click="submitResetPwd" style="margin-top: 20px; ">确定重置</el-button>
    </el-dialog>
  </div>
</template>

<script>
import {listUser, getUser, delUser, addUser, updateUser, resetPwd} from "@/api/quiz/user";

export default {
  name: "User",
  data() {
    return {
      newPwd: {
        userName: '',
        password: '',
      },
      resetDialogOpen: false,
      blob: '',
      options: {
        img: ''
      },
      apiUrl: process.env.VUE_APP_BASE_API,
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
      // 刷题用户表格数据
      userList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: null,
        password: null,
        nickName: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    handleEditClose(){
      this.blob=''
      this.form=''
    },
    handleResetPwd(row) {
      this.newPwd.password=''
      this.newPwd.userName = row.userName
      this.resetDialogOpen = true
    },
    submitResetPwd() {
      resetPwd(this.newPwd).then(res => {
        if (res.code === 200) {
          this.$message.success("重置密码成功")
        }
        this.resetDialogOpen = false
      })

    },
    beforeAvatarUpload(file) {
      if (file.type.indexOf("image/") == -1) {
        this.$message.error("文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。");
      } else {
        const imageConversion = require("image-conversion");
        console.log('压缩前' + (file.size / 1024) + "K");
        imageConversion.compressAccurately(file, 100).then(res => {
          console.log('压缩后' + (res.size / 1024) + "K");
          const reader = new FileReader();
          reader.readAsDataURL(res);
          reader.onload = () => {
            this.blob = res
            this.options.img = reader.result;
            this.options.filename = file.name;
          };
        })
      }
    },
    // 覆盖默认的上传行为
    requestUpload() {
    },
    /** 查询刷题用户列表 */
    getList() {
      this.loading = true;
      listUser(this.queryParams).then(response => {
        this.userList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.blob=''
      this.options.img=''
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        userId: null,
        userName: null,
        password: null,
        nickName: null
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
      this.ids = selection.map(item => item.userId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.options.img = ''
      this.blob = ''
      this.reset();
      this.open = true;
      this.title = "添加刷题用户";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const userId = row.userId || this.ids
      getUser(userId).then(response => {
        this.form = response.data;
        this.options.img = this.form.avatar
        this.open = true;
        this.title = "修改刷题用户";
      });
    },
    // /** 提交按钮 */
    // if (valid) {
    //   const formData = new FormData;
    //   if (this.blob) {
    //     formData.append("avatarfile", this.blob, this.options.filename);
    //   }
    //   // 将对象转换为 JSON 字符串
    //   formData.append("userForm", JSON.stringify(this.form));
    //   updateInfo(formData).then(res => {
    //     if (res.code === 200) {
    //       this.$message.success('修改成功！')
    //       this.options.img = process.env.VUE_APP_BASE_API + res.imgUrl;
    //       console.log(this.options.img)
    //       store.commit('SET_AVATAR', this.options.img);
    //     } else {
    //       this.$message.error("修改失败！请确认信息是否填写完毕")
    //     }
    //   })
    // } else {
    //   this.$message.error("请确认信息是否填写完毕")
    //   return false;
    // }
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const formData = new FormData;
          if (!this.options.img) {
            this.$message.error("请上传头像")
            return
          }

          if (this.blob) {
            formData.append("avatarfile", this.blob, this.options.filename);
          }
          // 如果用户没有设置头像，默认为this options img
          this.form.avatar = this.options.img
          // 将对象转换为 JSON 字符串
          formData.append("userForm", JSON.stringify(this.form));
          if (this.form.userId != null) {
            updateUser(formData).then(response => {
              this.options.img = process.env.VUE_APP_BASE_API + response.imgUrl;
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addUser(formData).then(response => {
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
      const userIds = row.userId || this.ids;
      this.$modal.confirm('是否确认删除刷题用户编号为"' + userIds + '"的数据项？').then(function () {
        return delUser(userIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('quiz/user/export', {
        ...this.queryParams
      }, `user_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
