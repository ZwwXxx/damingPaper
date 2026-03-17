<template>
  <div class="app-container">
    <!-- 查询条件 -->
    <el-form :inline="true" :model="queryParams" size="small" label-width="80px">
      <el-form-item label="科目">
        <el-select
          v-model="queryParams.subjectId"
          clearable
          placeholder="请选择科目"
          style="width: 180px"
        >
          <el-option
            v-for="item in subjectOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="分组名称">
        <el-input
          v-model="queryParams.groupName"
          placeholder="请输入一级分组"
          clearable
          style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="栏目名称">
        <el-input
          v-model="queryParams.columnName"
          placeholder="请输入栏目名称"
          clearable
          style="width: 180px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['quiz:practice:column:add']"
        >新增栏目</el-button>
      </el-col>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="ID" prop="columnId" width="80" align="center" />
      <el-table-column label="科目" prop="subjectId" width="120">
        <template slot-scope="scope">
          {{ renderSubject(scope.row.subjectId) }}
        </template>
      </el-table-column>
      <el-table-column label="一级分组" prop="groupName" min-width="140" />
      <el-table-column label="分组排序" prop="groupSort" width="100" align="center" />
      <el-table-column label="栏目名称" prop="columnName" min-width="160" />
      <el-table-column label="栏目排序" prop="sortOrder" width="100" align="center" />
      <el-table-column label="描述" prop="description" min-width="220" show-overflow-tooltip />
      <el-table-column label="参与专项刷题" prop="enablePractice" width="130" align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.enablePractice"
            :active-value="1"
            :inactive-value="0"
            @change="val => handleEnableChange(scope.row, val)"
            v-hasPermi="['quiz:practice:column:edit']"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="mini"
            @click="handleEdit(scope.row)"
            v-hasPermi="['quiz:practice:column:edit']"
          >编辑</el-button>
          <el-button
            type="text"
            size="mini"
            @click="handleConfigQuestions(scope.row)"
            v-hasPermi="['quiz:practice:column:bind']"
          >配置题目</el-button>
          <el-button
            type="text"
            size="mini"
            style="color:#f56c6c"
            @click="handleDelete(scope.row)"
            v-hasPermi="['quiz:practice:column:remove']"
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

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="open" width="640px">
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="90px"
        class="column-form"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="科目" prop="subjectId">
              <el-select v-model="form.subjectId" placeholder="请选择科目" style="width: 100%">
                <el-option
                  v-for="item in subjectOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="启用刷题" prop="enablePractice">
              <el-switch v-model="form.enablePractice" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="一级分组" prop="groupName">
              <el-autocomplete
                ref="groupAuto"
                v-model="form.groupName"
                placeholder="如：计算机组成与结构"
                :fetch-suggestions="queryGroupSuggestions"
                :trigger-on-focus="true"
                :debounce="150"
                clearable
                style="width: 100%"
                @focus="onGroupFocus"
                @select="handleGroupSelect"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分组排序" prop="groupSort">
              <el-input-number v-model="form.groupSort" :min="0" :max="9999" />
              <span class="form-tip">大在前</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="栏目名称" prop="columnName">
              <el-autocomplete
                ref="columnAuto"
                v-model="form.columnName"
                placeholder="如：存储系统（不填则默认：其它/零散题）"
                :fetch-suggestions="queryColumnSuggestions"
                :trigger-on-focus="true"
                :debounce="150"
                clearable
                style="width: 100%"
                @focus="onColumnFocus"
                @select="handleColumnSelect"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="栏目排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
              <span class="form-tip">大在前</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="3"
                placeholder="栏目用途说明（选填）"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="open = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 配置题目：左侧题库选择，右侧已选题目（拖拽排序） -->
    <el-dialog
      :title="`配置题目 - ${currentConfigColumn ? currentConfigColumn.columnName : ''}`"
      :visible.sync="configDialogVisible"
      width="900px"
      @open="onConfigDialogOpen"
    >
      <div style="margin-bottom: 10px; color:#606266; font-size: 13px;">
        勾选该栏目包含的题目并保存。保存后会清空该栏目的 <code>paperId</code>，下次学生进入会重新生成试卷。
      </div>

      <el-row :gutter="16">
        <!-- 左侧：题库选择列表 -->
        <el-col :span="15" class="config-left">
          <el-form :inline="true" :model="questionQueryParams" size="small" label-width="80px">
            <el-form-item label="科目">
              <el-select
                v-model="questionQueryParams.subjectId"
                clearable
                placeholder="请选择科目"
                style="width: 180px"
                @change="handleQuestionQuery"
              >
                <el-option
                  v-for="item in subjectOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="题干关键字">
              <el-input
                v-model="questionQueryParams.questionTitle"
                clearable
                placeholder="输入关键字"
                style="width: 220px"
                @keyup.enter.native="handleQuestionQuery"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="handleQuestionQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" @click="resetQuestionQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table
            ref="questionTable"
            v-loading="questionLoading"
            :data="questionList"
            border
            :row-key="row => row.id"
            @selection-change="handleQuestionSelectionChange"
            style="width: 100%"
            height="520"
          >
            <el-table-column type="selection" width="55" align="center" :reserve-selection="true" />
            <el-table-column label="题目ID" prop="id" width="90" align="center" />
            <el-table-column label="题型" prop="questionType" width="90" align="center" />
            <el-table-column label="题干" min-width="360" align="left">
              <template slot-scope="scope">
                <span>{{ scope.row.questionTitle }}</span>
              </template>
            </el-table-column>
            <el-table-column label="分数" prop="score" width="70" align="center" />
          </el-table>

          <pagination
            v-show="questionTotal > 0"
            :total="questionTotal"
            :page.sync="questionQueryParams.pageNum"
            :limit.sync="questionQueryParams.pageSize"
            @pagination="getQuestionList"
          />
        </el-col>

        <!-- 右侧：已选题目 = 试卷编辑效果（可拖拽排序） -->
        <el-col :span="9" class="config-right">
          <div class="selected-panel selected-panel--full">
            <div class="selected-panel__header">
              <div class="selected-panel__title">
                已选题目（{{ selectedQuestionCount }}）
              </div>
              <div class="selected-panel__actions">
                <el-button
                  v-if="selectedQuestionCount > 0"
                  size="mini"
                  type="text"
                  style="color:#f56c6c"
                  @click="clearSelectedQuestions"
                >
                  清空
                </el-button>
              </div>
            </div>

            <div v-if="selectedQuestionCount === 0" class="selected-panel__empty">
              左侧勾选题目后，会出现在这里；可拖拽调整顺序。
            </div>

            <draggable
              v-else
              v-model="selectedQuestionIds"
              class="selected-panel__list"
              :animation="200"
              handle=".selected-panel__drag"
              @end="handleSelectedDragEnd"
            >
              <div v-for="(id, idx) in selectedQuestionIds" :key="id" class="selected-panel__item">
                <i class="el-icon-rank selected-panel__drag" title="拖拽调整顺序" />
                <div class="selected-panel__content">
                  <div class="selected-panel__line1">
                    <span class="selected-panel__index">{{ idx + 1 }}.</span>
                    <span class="selected-panel__qid">ID: {{ id }}</span>
                  </div>
                  <div class="selected-panel__line2">
                    {{ renderSelectedQuestionTitle(id) }}
                  </div>
                </div>
                <el-button type="text" class="selected-panel__removeBtn" @click.stop="removeSelectedQuestion(id)">
                  移除
                </el-button>
              </div>
            </draggable>
          </div>
        </el-col>
      </el-row>

      <span slot="footer" class="dialog-footer">
        <el-button @click="configDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="questionSaving" @click="submitConfigQuestions">保 存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import draggable from 'vuedraggable'
import {
  listPracticeGroupOptions,
  listPracticeColumnOptions,
  getPracticeColumnQuestionIds,
  savePracticeColumnQuestions
} from '@/api/quiz/practiceColumn'
import { optionSubject } from '@/api/quiz/subject'
import { listQuestion, getQuestion } from '@/api/quiz/question'
import request from '@/utils/request'

export default {
  name: 'PracticeColumn',
  components: { draggable },
  data() {
    return {
      loading: false,
      total: 0,
      list: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        subjectId: undefined,
        columnName: undefined,
        groupName: undefined
      },
      subjectOptions: [],
      open: false,
      dialogTitle: '',
      form: {
        columnId: undefined,
        subjectId: undefined,
        groupName: '',
        groupSort: 0,
        columnName: '',
        sortOrder: 0,
        enablePractice: 1,
        description: ''
      },
      // 一级分组 / 二级栏目 自动补全缓存（避免每次输入都请求）
      groupOptionNames: [],
      groupOptionSubjectId: undefined,
      columnOptionList: [],
      columnOptionKey: '',
      rules: {
        subjectId: [{ required: true, message: '请选择科目', trigger: 'change' }],
        groupName: [{ required: true, message: '请输入一级分组', trigger: 'blur' }],
        // columnName 可不填：后端会自动补默认值“其它/零散题”
        columnName: []
      },
      configDialogVisible: false,
      currentConfigColumn: null,
      questionLoading: false,
      questionSaving: false,
      questionTotal: 0,
      questionList: [],
      questionQueryParams: {
        pageNum: 1,
        pageSize: 10,
        subjectId: undefined,
        questionTitle: undefined
      },
      selectedQuestionIdSet: new Set(),
      selectedQuestionIds: [],
      selectedQuestionCount: 0,
      selectedQuestionTitleMap: {},
      lastBoundQuestionIds: [],
      // 标记当前是否在代码中批量应用勾选，避免触发 selection-change 时误清空已选集合
      applyingSelection: false
    }
  },
  created() {
    this.loadSubjectOptions()
    this.getList()
  },
  methods: {
    async loadSubjectOptions() {
      const res = await optionSubject()
      const list = (res && res.data) || []
      this.subjectOptions = list.map(it => ({
        label: it.subjectName,
        value: it.subjectId
      }))
    },
    renderSubject(subjectId) {
      const item = this.subjectOptions.find(x => x.value === subjectId)
      return item ? item.label : subjectId
    },
    async getList() {
      this.loading = true
      try {
        const res = await request({
          url: '/quiz/practice/column/list',
          method: 'get',
          params: this.queryParams
        })
        this.list = (res && res.rows) || []
        this.total = res && res.total ? res.total : 0
      } finally {
        this.loading = false
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: this.queryParams.pageSize,
        subjectId: undefined,
        columnName: undefined,
        groupName: undefined
      }
      this.getList()
    },
    resetForm() {
      this.form = {
        columnId: undefined,
        subjectId: undefined,
        groupName: '',
        groupSort: 0,
        columnName: '',
        sortOrder: 0,
        enablePractice: 1,
        description: ''
      }
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleAdd() {
      this.resetForm()
      this.dialogTitle = '新增栏目'
      this.open = true
    },
    handleEdit(row) {
      this.resetForm()
      this.dialogTitle = '编辑栏目'
      this.form = {
        columnId: row.columnId,
        subjectId: row.subjectId,
        groupName: row.groupName,
        groupSort: row.groupSort,
        columnName: row.columnName,
        sortOrder: row.sortOrder,
        enablePractice: row.enablePractice,
        description: row.description
      }
      this.open = true
    },
    onGroupFocus() {
      // 某些场景下仅依赖 trigger-on-focus 不会触发，这里主动拉取并展开候选
      this.$nextTick(() => {
        const c = this.$refs.groupAuto
        if (c && typeof c.getData === 'function') {
          c.getData(this.form.groupName || '')
        }
      })
    },
    onColumnFocus() {
      this.$nextTick(() => {
        const c = this.$refs.columnAuto
        if (c && typeof c.getData === 'function') {
          c.getData(this.form.columnName || '')
        }
      })
    },
    async ensureGroupOptionsLoaded() {
      const subjectId = this.form ? this.form.subjectId : undefined
      if (this.groupOptionNames && this.groupOptionNames.length > 0 && this.groupOptionSubjectId === subjectId) {
        return
      }
      this.groupOptionSubjectId = subjectId
      const res = await listPracticeGroupOptions({ subjectId })
      const list = (res && res.data) || []
      this.groupOptionNames = (Array.isArray(list) ? list : [])
        .map(x => {
          if (x == null) return ''
          if (typeof x === 'string' || typeof x === 'number') return String(x).trim()
          // 兼容后端返回对象的情况：优先取 groupName
          const name = x.groupName || x.label || x.value || x.name
          return name == null ? '' : String(name).trim()
        })
        .filter(Boolean)
    },
    async ensureColumnOptionsLoaded() {
      const subjectId = this.form ? this.form.subjectId : undefined
      const groupName = this.form ? (this.form.groupName || '').trim() : ''
      const key = `${subjectId || ''}__${groupName}`
      if (this.columnOptionKey === key && this.columnOptionList && this.columnOptionList.length > 0) {
        return
      }
      this.columnOptionKey = key
      const res = await listPracticeColumnOptions({ subjectId, groupName })
      const list = (res && res.data) || []
      this.columnOptionList = Array.isArray(list) ? list : []
    },
    async queryGroupSuggestions(queryString, cb) {
      try {
        await this.ensureGroupOptionsLoaded()
        const q = (queryString || '').trim().toLowerCase()
        const result = (this.groupOptionNames || [])
          .filter(name => {
            if (!q) return true
            return String(name).toLowerCase().includes(q)
          })
          .slice(0, 50)
          .map(name => ({ value: name }))
        cb(result)
      } catch (e) {
        cb([])
      }
    },
    async queryColumnSuggestions(queryString, cb) {
      try {
        await this.ensureColumnOptionsLoaded()
        const q = (queryString || '').trim().toLowerCase()
        const uniq = new Set()
        const result = (this.columnOptionList || [])
          .map(it => ({
            value: it && it.columnName != null ? String(it.columnName) : '',
            columnId: it ? it.columnId : undefined,
            groupName: it && it.groupName != null ? String(it.groupName) : ''
          }))
          .filter(it => it.value)
          .filter(it => {
            if (!q) return true
            return it.value.toLowerCase().includes(q)
          })
          .filter(it => {
            const k = `${it.groupName}__${it.value}`
            if (uniq.has(k)) return false
            uniq.add(k)
            return true
          })
          .slice(0, 50)
        cb(result)
      } catch (e) {
        cb([])
      }
    },
    handleGroupSelect(item) {
      // 选择一级分组后，二级栏目候选依赖 groupName：清缓存，确保下一次聚焦即可拉取新候选
      this.columnOptionKey = ''
      this.columnOptionList = []
    },
    handleColumnSelect(item) {
      // 若用户先选了栏目但没填分组，则用返回的 groupName 自动补齐
      if (item && item.groupName && !String(this.form.groupName || '').trim()) {
        this.form.groupName = item.groupName
      }
    },
    submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        const isEdit = !!this.form.columnId
        const method = isEdit ? 'put' : 'post'
        const res = await request({
          url: '/quiz/practice/column',
          method,
          data: this.form
        })
        if (res && res.code === 200) {
          this.$message.success('保存成功')
          this.open = false
          this.getList()
        }
      })
    },
    async handleDelete(row) {
      this.$confirm(`确认删除栏目「${row.columnName}」吗？`, '提示', {
        type: 'warning'
      }).then(async () => {
        const res = await request({
          url: `/quiz/practice/column/${row.columnId}`,
          method: 'delete'
        })
        if (res && res.code === 200) {
          this.$message.success('删除成功')
          this.getList()
        }
      }).catch(() => {})
    },
    async handleEnableChange(row, val) {
      await request({
        url: '/quiz/practice/column',
        method: 'put',
        data: {
          columnId: row.columnId,
          enablePractice: val
        }
      })
      this.$message.success('已更新展示状态')
    },
    handleConfigQuestions(row) {
      this.currentConfigColumn = row
      this.configDialogVisible = true
    },
    async onConfigDialogOpen() {
      // 初始化查询条件：默认用栏目科目
      this.questionQueryParams.pageNum = 1
      this.questionQueryParams.pageSize = 10
      this.questionQueryParams.subjectId = this.currentConfigColumn ? this.currentConfigColumn.subjectId : undefined
      this.questionQueryParams.questionTitle = undefined

      // 拉取已绑定题目ID，用于回显
      this.selectedQuestionIdSet = new Set()
      this.selectedQuestionIds = []
      this.selectedQuestionCount = 0
      this.selectedQuestionTitleMap = {}
      this.lastBoundQuestionIds = []
      if (this.currentConfigColumn && this.currentConfigColumn.columnId) {
        const res = await getPracticeColumnQuestionIds(this.currentConfigColumn.columnId)
        const ids = (res && res.data) || []
        this.lastBoundQuestionIds = ids
        // 初始化已选集合 + 顺序，用于右侧直接回显
        this.selectedQuestionIdSet = new Set(ids)
        this.selectedQuestionIds = Array.isArray(ids) ? ids.slice() : []
        this.selectedQuestionCount = this.selectedQuestionIds.length
      }

      await this.getQuestionList()
      this.applyQuestionSelection()
      this.syncSelectedQuestionView()
      // 预加载右侧题干（按需，失败不影响保存）
      for (const id of this.selectedQuestionIds || []) {
        this.ensureSelectedQuestionTitle(id)
      }
    },
    async getQuestionList() {
      this.questionLoading = true
      try {
        const res = await listQuestion(this.questionQueryParams)
        this.questionList = (res && res.rows) || []
        this.questionTotal = res && res.total ? res.total : 0
      } finally {
        this.questionLoading = false
      }
      this.$nextTick(() => {
        this.applyQuestionSelection()
      })
    },
    applyQuestionSelection() {
      const table = this.$refs.questionTable
      if (!table) return
      // 打标记：下面的 clearSelection / toggleRowSelection 会触发 selection-change，
      // 此时不应该按照“用户手动勾选”的逻辑去改动 selectedQuestionIdSet
      this.applyingSelection = true
      try {
        table.clearSelection()
        const ids = this.selectedQuestionIdSet
        if (!ids || ids.size === 0) {
          return
        }
        for (const row of this.questionList) {
          if (row && ids.has(row.id)) {
            table.toggleRowSelection(row, true)
          }
        }
      } finally {
        this.applyingSelection = false
      }
    },
    handleQuestionSelectionChange(selection) {
      // 若是程序批量应用勾选触发的事件，直接跳过
      if (this.applyingSelection) {
        return
      }
      // 只根据当前页的 selection 进行增删，保持跨页已选集合
      const currentPageIds = new Set(this.questionList.map(x => x.id))
      for (const id of currentPageIds) {
        this.selectedQuestionIdSet.delete(id)
      }
      for (const row of selection || []) {
        if (row && row.id != null) {
          this.selectedQuestionIdSet.add(row.id)
          if (row.questionTitle != null && row.questionTitle !== '') {
            this.$set(this.selectedQuestionTitleMap, String(row.id), row.questionTitle)
          }
        }
      }
      this.syncSelectedQuestionView()
    },
    syncSelectedQuestionView() {
      const set = this.selectedQuestionIdSet || new Set()
      const current = Array.isArray(this.selectedQuestionIds) ? this.selectedQuestionIds : []
      const result = []
      // 先保留当前顺序中仍然存在于集合里的部分
      for (const id of current) {
        if (set.has(id)) {
          result.push(id)
        }
      }
      // 再把新加入但还没在 result 里的补充进去（按数值从小到大）
      const rest = Array.from(set).filter(id => !result.includes(id))
      rest.sort((a, b) => Number(a) - Number(b))
      this.selectedQuestionIds = result.concat(rest)
      this.selectedQuestionCount = this.selectedQuestionIds.length
    },
    renderSelectedQuestionTag(id) {
      const title = this.selectedQuestionTitleMap && this.selectedQuestionTitleMap[String(id)]
      if (title) {
        const t = String(title)
        return `${id} - ${t.length > 18 ? t.slice(0, 18) + '…' : t}`
      }
      return String(id)
    },
    renderSelectedQuestionTitle(id) {
      const title = this.selectedQuestionTitleMap && this.selectedQuestionTitleMap[String(id)]
      return title ? String(title) : '（题干加载中…）'
    },
    async ensureSelectedQuestionTitle(id) {
      if (id == null) return
      const key = String(id)
      if (this.selectedQuestionTitleMap && this.selectedQuestionTitleMap[key]) return
      try {
        const res = await getQuestion(id)
        const t = res && res.data ? res.data.questionTitle : ''
        if (t) {
          this.$set(this.selectedQuestionTitleMap, key, t)
        }
      } catch (e) {
        // ignore: 题干拉取失败不影响保存
      }
    },
    removeSelectedQuestion(id) {
      if (id == null) return
      this.selectedQuestionIdSet.delete(id)
      this.syncSelectedQuestionView()
      // 若当前页存在该行，则同步取消勾选
      const table = this.$refs.questionTable
      if (!table) return
      const row = (this.questionList || []).find(x => x && x.id === id)
      if (row) {
        table.toggleRowSelection(row, false)
      }
    },
    clearSelectedQuestions() {
      this.selectedQuestionIdSet = new Set()
      this.syncSelectedQuestionView()
      const table = this.$refs.questionTable
      table && table.clearSelection()
    },
    handleSelectedDragEnd() {
      // 拖拽结束后，同步集合与数量，保持顺序为当前拖拽后的顺序
      this.selectedQuestionCount = (this.selectedQuestionIds || []).length
      this.selectedQuestionIdSet = new Set(this.selectedQuestionIds || [])
      // 尽量补齐右侧题干展示
      for (const id of this.selectedQuestionIds || []) {
        this.ensureSelectedQuestionTitle(id)
      }
    },
    handleQuestionQuery() {
      this.questionQueryParams.pageNum = 1
      this.getQuestionList()
    },
    resetQuestionQuery() {
      this.questionQueryParams.pageNum = 1
      this.questionQueryParams.pageSize = 10
      this.questionQueryParams.subjectId = this.currentConfigColumn ? this.currentConfigColumn.subjectId : undefined
      this.questionQueryParams.questionTitle = undefined
      this.getQuestionList()
    },
    async submitConfigQuestions() {
      if (!this.currentConfigColumn || !this.currentConfigColumn.columnId) return
      this.questionSaving = true
      try {
        // 以拖拽后的选中顺序为准；若数组为空则退回到集合顺序
        const ordered = (this.selectedQuestionIds && this.selectedQuestionIds.length)
          ? this.selectedQuestionIds.slice()
          : Array.from(this.selectedQuestionIdSet || [])
        await savePracticeColumnQuestions(this.currentConfigColumn.columnId, ordered)
        this.$message.success('保存成功')
        this.configDialogVisible = false
      } finally {
        this.questionSaving = false
      }
    }
  }
}
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
.selected-panel {
  margin: 6px 0 10px;
  padding: 10px 10px 6px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background: #fafcff;
}
.selected-panel__title {
  font-size: 12px;
  color: #606266;
  margin-bottom: 8px;
}
.selected-panel__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.selected-panel__tag {
  max-width: 100%;
}
.column-form {
  padding-top: 8px;
}
.form-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}
</style>

