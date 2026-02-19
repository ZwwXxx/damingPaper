<template>
  <div class="app-container">
    <el-tabs v-model="activeTab">
      <!-- 科目管理 -->
      <el-tab-pane label="科目管理" name="subject">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAddSubject" v-hasPermi="['knowledge:subject:add']">新增科目</el-button>
          </el-col>
        </el-row>
        <el-table v-loading="subjectLoading" :data="subjectList">
          <el-table-column label="ID" prop="subjectId" width="70" align="center"/>
          <el-table-column label="科目名称" prop="subjectName" min-width="160"/>
          <el-table-column label="状态" prop="status" width="90" align="center">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" prop="createTime" width="165" align="center">
            <template slot-scope="scope">{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</template>
          </el-table-column>
          <el-table-column label="操作" width="160" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEditSubject(scope.row)" v-hasPermi="['knowledge:subject:edit']">修改</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelSubject(scope.row)" v-hasPermi="['knowledge:subject:remove']">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-dialog :title="subjectForm.subjectId ? '修改科目' : '新增科目'" :visible.sync="subjectOpen" width="500px" append-to-body>
          <el-form ref="subjectFormRef" :model="subjectForm" :rules="subjectRules" label-width="90px">
            <el-form-item label="科目名称" prop="subjectName">
              <el-input v-model="subjectForm.subjectName" placeholder="请输入科目名称" maxlength="100" show-word-limit clearable/>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="subjectForm.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitSubjectForm">确 定</el-button>
            <el-button @click="subjectOpen = false">取 消</el-button>
          </div>
        </el-dialog>
      </el-tab-pane>

      <!-- 知识点管理：复用原知识点列表页 -->
      <el-tab-pane label="知识点管理" name="point">
        <point-index />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { listKnowledgeSubjects, addSubject, updateSubject, delSubject } from '@/api/quiz/knowledge'
import PointIndex from './point/index.vue'

export default {
  name: 'KnowledgeManage',
  components: { PointIndex },
  data() {
    return {
      activeTab: 'subject',
      subjectLoading: false,
      subjectList: [],
      subjectOpen: false,
      subjectForm: { status: 1 },
      subjectRules: {
        subjectName: [{ required: true, message: '请输入科目名称', trigger: 'blur' }]
      }
    }
  },
  watch: {
    activeTab(val) {
      if (val === 'subject') this.loadSubjects()
    }
  },
  created() {
    this.loadSubjects()
  },
  methods: {
    parseTime: function(...args) {
      return this.$parseTime && this.$parseTime.apply(this, args) || ''
    },
    loadSubjects() {
      this.subjectLoading = true
      listKnowledgeSubjects().then(res => {
        this.subjectList = res.data || []
      }).finally(() => { this.subjectLoading = false })
    },
    handleAddSubject() {
      this.subjectForm = { subjectName: '', status: 1 }
      this.subjectOpen = true
      this.$nextTick(() => this.$refs.subjectFormRef && this.$refs.subjectFormRef.clearValidate())
    },
    handleEditSubject(row) {
      this.subjectForm = { subjectId: row.subjectId, subjectName: row.subjectName, status: row.status }
      this.subjectOpen = true
      this.$nextTick(() => this.$refs.subjectFormRef && this.$refs.subjectFormRef.clearValidate())
    },
    submitSubjectForm() {
      this.$refs.subjectFormRef.validate(valid => {
        if (!valid) return
        if (this.subjectForm.subjectId) {
          updateSubject(this.subjectForm).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.subjectOpen = false
            this.loadSubjects()
          })
        } else {
          addSubject(this.subjectForm).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.subjectOpen = false
            this.loadSubjects()
          })
        }
      })
    },
    handleDelSubject(row) {
      this.$modal.confirm('是否确认删除该科目？').then(() => {
        return delSubject(row.subjectId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.loadSubjects()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.mb-2 { margin-bottom: 12px; }
.mb8 { margin-bottom: 8px; }
</style>
