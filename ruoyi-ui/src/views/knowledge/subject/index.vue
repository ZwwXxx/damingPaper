<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['knowledge:subject:add']">新增科目</el-button>
      </el-col>
    </el-row>
    <el-table v-loading="loading" :data="subjectList">
      <el-table-column label="ID" prop="subjectId" width="70" align="center"/>
      <el-table-column label="科目名称" prop="subjectName" min-width="160"/>
      <el-table-column label="状态" prop="status" width="90" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="165" align="center">
        <template slot-scope="scope">{{ formatCreateTime(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)" v-hasPermi="['knowledge:subject:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDel(scope.row)" v-hasPermi="['knowledge:subject:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="form.subjectId ? '修改科目' : '新增科目'" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="科目名称" prop="subjectName">
          <el-input v-model="form.subjectName" placeholder="请输入科目名称" maxlength="100" show-word-limit clearable/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listKnowledgeSubjects, addSubject, updateSubject, delSubject } from '@/api/quiz/knowledge'

export default {
  name: 'KnowledgeSubject',
  data() {
    return {
      loading: false,
      subjectList: [],
      open: false,
      form: { status: 1 },
      rules: {
        subjectName: [{ required: true, message: '请输入科目名称', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadList()
  },
  methods: {
    formatCreateTime(row) {
      const t = row.createTime != null ? row.createTime : row.create_time
      if (t == null || t === '') return '-'
      if (typeof t === 'string' && /^\d{4}-\d{2}-\d{2}/.test(t)) return t
      const formatted = this.$parseTime && this.$parseTime(t, '{y}-{m}-{d} {h}:{i}')
      return formatted || (t && String(t)) || '-'
    },
    loadList() {
      this.loading = true
      listKnowledgeSubjects().then(res => {
        this.subjectList = res.data || []
      }).finally(() => { this.loading = false })
    },
    handleAdd() {
      this.form = { subjectName: '', status: 1 }
      this.open = true
      this.$nextTick(() => this.$refs.formRef && this.$refs.formRef.clearValidate())
    },
    handleEdit(row) {
      this.form = { subjectId: row.subjectId, subjectName: row.subjectName, status: row.status }
      this.open = true
      this.$nextTick(() => this.$refs.formRef && this.$refs.formRef.clearValidate())
    },
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (!valid) return
        if (this.form.subjectId) {
          updateSubject(this.form).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.loadList()
          })
        } else {
          addSubject(this.form).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.loadList()
          })
        }
      })
    },
    handleDel(row) {
      this.$modal.confirm('是否确认删除该科目？').then(() => {
        return delSubject(row.subjectId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.loadList()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.mb8 { margin-bottom: 8px; }
</style>
