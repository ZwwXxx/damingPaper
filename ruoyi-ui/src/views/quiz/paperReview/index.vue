<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="80px">
      <el-form-item label="试卷名称" prop="paperName">
        <el-input v-model="queryParams.paperName" placeholder="请输入试卷名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="考生账号" prop="createUser">
        <el-input v-model="queryParams.createUser" placeholder="请输入考生账号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态" prop="reviewStatus">
        <el-select v-model="queryParams.reviewStatus" placeholder="请选择" clearable>
          <el-option label="待批改" :value="1"/>
          <el-option label="已完成" :value="2"/>
          <el-option label="全部" :value="undefined"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="paperList">
      <el-table-column label="答卷ID" prop="paperAnswerId" width="120"/>
      <el-table-column label="试卷名称" prop="paperName" min-width="180"/>
      <el-table-column label="考生账号" prop="createUser" width="120"/>
      <el-table-column label="状态" width="120">
        <template slot-scope="scope">
          <el-tag :type="scope.row.reviewStatus === 1 ? 'warning' : 'success'">
            {{ scope.row.reviewStatus === 1 ? '待批改' : '已完成' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="客观题得分" prop="objectiveScore" width="130">
        <template slot-scope="scope">
          {{ scope.row.objectiveScore != null ? scope.row.objectiveScore : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="最终得分" prop="finalScore" width="130">
        <template slot-scope="scope">
          {{ scope.row.reviewStatus === 1 ? '-' : scope.row.finalScore }}
        </template>
      </el-table-column>
      <el-table-column label="提交时间" prop="createTime" width="180"/>
      <el-table-column label="操作" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="openReview(scope.row)">
            {{ scope.row.reviewStatus === 1 ? '批改' : '查看' }}
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

    <el-dialog :title="dialogTitle"
               :visible.sync="reviewVisible"
               width="80%"
               class="paper-review-dialog"
               :close-on-click-modal="false"
               append-to-body>
      <div v-if="detailLoading" style="text-align: center;padding: 40px 0;">
        <el-spinner/>
      </div>
      <div v-else-if="currentPaper" class="review-body">
        <div class="paper-meta">
          <p><strong>考生账号：</strong>{{ paperAnswerInfo.createUser }}</p>
          <p><strong>考试状态：</strong>
            <el-tag :type="paperAnswerInfo.reviewStatus === 1 ? 'warning' : 'success'">
              {{ paperAnswerInfo.reviewStatus === 1 ? '待批改' : '已完成' }}
            </el-tag>
          </p>
        </div>
        <div class="review-layout">
          <div class="review-aside" v-if="subjectiveList.length">
            <div class="aside-title">题目目录</div>
            <div class="aside-tags">
              <el-tag
                v-for="item in subjectiveList"
                :key="item.questionId"
                type="info"
                class="aside-tag"
                :class="{'tag-active': activeQuestionId === item.questionId}"
                @click="scrollToQuestion(item.questionId)">
                {{ item.displayOrder }}
              </el-tag>
            </div>
          </div>
          <div class="review-main">
            <el-table
              v-if="subjectiveList.length"
              :data="subjectiveList"
              border
              :row-class-name="questionRowClass"
              ref="subjectiveTable">
              <el-table-column label="题目" min-width="280">
                <template slot-scope="scope">
                  <div class="question-title">{{ scope.row.title }}</div>
                  <div class="student-answer">
                    <strong>学生答案：</strong>
                    <template v-if="scope.row.studentAnswer">
                      <span class="answer-preview">{{ formatAnswerPreview(scope.row.studentAnswer) }}</span>
                      <el-button type="text" size="mini" @click="openAnswerPreview(scope.row)">查看详情</el-button>
                    </template>
                    <span v-else>未作答</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="满分" prop="maxScore" width="80"/>
              <el-table-column label="得分" width="150" class-name="score-column">
                <template slot-scope="scope">
                  <el-input-number v-model="scope.row.score" :min="0" :max="scope.row.maxScore"/>
                </template>
              </el-table-column>
              <el-table-column label="评语">
                <template slot-scope="scope">
                  <el-input type="textarea" v-model="scope.row.comment" :rows="2" placeholder="可选"/>
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-else description="该试卷无主观题"/>
            <el-form label-width="80px" style="margin-top: 20px;">
              <el-form-item label="批注">
                <el-input type="textarea" v-model="reviewRemark" :rows="2" placeholder="可填写整体批注"/>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="reviewVisible=false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitReview" v-if="paperAnswerInfo.reviewStatus === 1">提 交</el-button>
      </span>
    </el-dialog>

    <el-dialog title="学生答案详情"
               :visible.sync="answerPreviewVisible"
               width="60%"
               :close-on-click-modal="false"
               append-to-body>
      <div
        class="answer-preview-body"
        v-if="answerPreviewContent"
        ref="answerPreviewBody"
        v-html="answerPreviewContent"
      ></div>
      <el-empty v-else description="学生未作答"/>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="answerPreviewVisible=false">关 闭</el-button>
      </span>
    </el-dialog>
    <el-image-viewer
      v-if="imagePreview.visible"
      :url-list="imagePreview.urls"
      :initial-index="imagePreview.index"
      :on-close="closeImagePreview"
    />
  </div>
</template>

<script>
import { listPaperReview, getPaperReview, submitPaperReview } from "@/api/quiz/paperReview";
import { getOssSign } from "@/api/common";
import ElImageViewer from "element-ui/packages/image/src/image-viewer";

export default {
  name: "PaperReview",
  components: {
    ElImageViewer
  },
  data() {
    return {
      loading: false,
      paperList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        paperName: undefined,
        createUser: undefined,
        reviewStatus: undefined
      },
      reviewVisible: false,
      detailLoading: false,
      currentPaper: null,
      subjectiveList: [],
      reviewRemark: '',
      submitLoading: false,
      activeQuestionId: null,
      reviewScrollHandler: null,
      answerPreviewVisible: false,
      answerPreviewContent: '',
      answerPreviewTitle: '',
      imagePreview: {
        visible: false,
        urls: [],
        index: 0
      },
      ossUrlCache: {}
    }
  },
  created() {
    this.getList()
  },
  computed: {
    dialogTitle() {
      if (this.currentPaper && this.currentPaper.paperDto && this.currentPaper.paperDto.paperName) {
        return this.currentPaper.paperDto.paperName
      }
      return '批改试卷'
    },
    paperAnswerInfo() {
      if (this.currentPaper && this.currentPaper.paperAnswerDto) {
        return this.currentPaper.paperAnswerDto
      }
      return {}
    }
  },
  methods: {
    getList() {
      this.loading = true
      listPaperReview(this.queryParams).then(res => {
        this.paperList = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    openReview(row) {
      this.reviewVisible = true
      this.detailLoading = true
      this.currentPaper = null
      this.subjectiveList = []
      this.reviewRemark = ''
      this.activeQuestionId = null
      getPaperReview(row.paperAnswerId).then(res => {
        this.currentPaper = res.data
        const answerDto = (res.data && res.data.paperAnswerDto) ? res.data.paperAnswerDto : {}
        this.reviewRemark = answerDto.reviewRemark || ''
        this.buildSubjectiveList()
      }).finally(() => {
        this.detailLoading = false
      })
    },
    buildSubjectiveList() {
      const paperDto = (this.currentPaper && this.currentPaper.paperDto) ? this.currentPaper.paperDto : {}
      const answerDto = (this.currentPaper && this.currentPaper.paperAnswerDto) ? this.currentPaper.paperAnswerDto : {}
      const answerMap = {}
      ;(answerDto.questionAnswerDtos || []).forEach(item => {
        answerMap[item.questionId] = item
      })
      const list = []
      ;(paperDto.paperQuestionTypeDto || []).forEach(type => {
        (type.questionDtos || []).forEach(question => {
          if (question.questionType === 3) {
            const answer = answerMap[question.id]
            if (!answer || !answer.answerId) {
              return
            }
            list.push({
              questionId: question.id,
              displayOrder: question.itemOrder != null ? question.itemOrder + 1 : list.length + 1,
              answerId: answer.answerId,
              title: question.questionTitle,
              maxScore: question.score || 0,
              score: answer.finalScore != null ? answer.finalScore : 0,
              studentAnswer: answer.content || '',
              comment: answer.reviewComment || ''
            })
          }
        })
      })
      this.subjectiveList = list
      this.activeQuestionId = list.length ? list[0].questionId : null
      this.$nextTick(() => {
        this.bindReviewScrollSpy()
      })
    },
    questionRowClass({ row }) {
      return row && row.questionId ? `question-row-${row.questionId}` : ''
    },
    scrollToQuestion(questionId) {
      if (!questionId) return
      this.activeQuestionId = questionId
      this.$nextTick(() => {
        const wrapper = this.$refs.subjectiveTable && this.$refs.subjectiveTable.bodyWrapper
        const target = wrapper ? wrapper.querySelector(`.question-row-${questionId}`) : null
        if (target && typeof target.scrollIntoView === 'function') {
          const top = target.offsetTop - 12
          wrapper.scrollTo({ top, behavior: 'smooth' })
        }
      })
    },
    bindReviewScrollSpy() {
      const wrapper = this.$refs.subjectiveTable && this.$refs.subjectiveTable.bodyWrapper
      if (!wrapper) return
      this.removeReviewScrollSpy()
      const handler = () => {
        const rows = Array.from(wrapper.querySelectorAll('tbody tr'))
        if (!rows.length) return
        const scrollTop = wrapper.scrollTop
        const offset = 20
        let currentId = rows[0].className || ''
        for (const row of rows) {
          if (row.offsetTop - offset <= scrollTop) {
            currentId = row.className || ''
          } else {
            break
          }
        }
        if (currentId) {
          const match = currentId.match(/question-row-(\d+)/)
          if (match && match[1]) {
            const qid = Number(match[1])
            if (Number.isFinite(qid)) {
              this.activeQuestionId = qid
            }
          }
        }
      }
      this.reviewScrollHandler = handler
      wrapper.addEventListener('scroll', handler, { passive: true })
      handler()
    },
    removeReviewScrollSpy() {
      const wrapper = this.$refs.subjectiveTable && this.$refs.subjectiveTable.bodyWrapper
      if (wrapper && this.reviewScrollHandler) {
        wrapper.removeEventListener('scroll', this.reviewScrollHandler)
      }
      this.reviewScrollHandler = null
    },
    submitReview() {
      if (!this.subjectiveList.length) {
        this.$message.warning('没有主观题无需批改')
        return
      }
      const payload = {
        paperAnswerId: this.paperAnswerInfo.paperAnswerId,
        questionScores: this.subjectiveList.map(item => ({
          answerId: item.answerId,
          score: item.score,
          comment: item.comment
        })),
        reviewRemark: this.reviewRemark
      }
      this.submitLoading = true
      submitPaperReview(payload).then(() => {
        this.$message.success('批改完成')
        this.reviewVisible = false
        this.getList()
      }).finally(() => {
        this.submitLoading = false
      })
    },
    formatAnswerPreview(content) {
      if (!content) {
        return ''
      }
      const plain = content.replace(/<[^>]+>/g, '').replace(/\s+/g, ' ').trim()
      return plain.length > 30 ? `${plain.slice(0, 30)}...` : plain
    },
    openAnswerPreview(row) {
      this.answerPreviewContent = row.studentAnswer || ''
      this.answerPreviewVisible = true
      this.$nextTick(() => {
        this.bindAnswerImages()
      })
    },
    async bindAnswerImages() {
      const container = this.$refs.answerPreviewBody
      if (!container) {
        return
      }
      const imgs = container.querySelectorAll('img')
      if (!imgs.length) {
        return
      }
      const imgArray = Array.from(imgs)
      const signedUrls = await Promise.all(imgArray.map(img => this.prepareAnswerImageNode(img)))
      const previewUrls = []
      imgArray.forEach((img, index) => {
        img.style.maxWidth = '160px'
        img.style.maxHeight = '160px'
        img.style.objectFit = 'contain'
        img.style.margin = '8px 8px 8px 0'
        const url = signedUrls[index]
        if (url) {
          img.style.cursor = 'zoom-in'
          const previewIndex = previewUrls.push(url) - 1
          img.onclick = () => this.openImagePreview(previewUrls, previewIndex)
        } else {
          img.style.cursor = 'not-allowed'
        }
      })
    },
    async prepareAnswerImageNode(img) {
      const raw = img.getAttribute('data-src') || img.getAttribute('src') || img.src
      const finalUrl = await this.getDisplayImageUrl(raw)
      if (finalUrl) {
        img.setAttribute('src', finalUrl)
      }
      return finalUrl
    },
    async getDisplayImageUrl(rawSrc) {
      const parsed = this.parseImageSource(rawSrc)
      if (!parsed) {
        return ''
      }
      if (parsed.url) {
        return parsed.url
      }
      if (!parsed.ossKey) {
        return ''
      }
      const cacheEntry = this.ossUrlCache[parsed.ossKey]
      const now = Date.now()
      if (cacheEntry && cacheEntry.expireAt > now) {
        return cacheEntry.url
      }
      try {
        const res = await getOssSign({ objectName: parsed.ossKey })
        const url = res.url || (res.data && res.data.url) || ''
        if (!url) {
          return ''
        }
        const expireSeconds = res.expireSeconds || (res.data && res.data.expireSeconds) || 300
        this.ossUrlCache[parsed.ossKey] = {
          url,
          expireAt: now + expireSeconds * 1000 - 5000
        }
        return url
      } catch (e) {
        return ''
      }
    },
    parseImageSource(src) {
      if (!src) {
        return null
      }
      const value = src.trim()
      if (!value) {
        return null
      }
      if (/^https?:\/\//i.test(value) || value.startsWith('data:')) {
        return { url: value }
      }
      const base = (process.env.VUE_APP_BASE_API || '').replace(/\/$/, '')
      let normalized = value
      if (base && normalized.startsWith(base)) {
        normalized = normalized.slice(base.length)
      }
      normalized = normalized.replace(/^\/+/, '')
      if (!normalized) {
        return null
      }
      const localPrefixes = ['upload/', 'profile/']
      if (localPrefixes.some(prefix => normalized.startsWith(prefix))) {
        return { url: this.joinBaseUrl(`/${normalized}`) }
      }
      return { ossKey: normalized }
    },
    joinBaseUrl(path) {
      const base = process.env.VUE_APP_BASE_API || ''
      if (!base) {
        return path
      }
      if (base.endsWith('/') && path.startsWith('/')) {
        return `${base}${path.slice(1)}`
      }
      if (!base.endsWith('/') && !path.startsWith('/')) {
        return `${base}/${path}`
      }
      return `${base}${path}`
    },
    openImagePreview(urls, index) {
      this.imagePreview.urls = urls
      this.imagePreview.index = index
      this.imagePreview.visible = true
    },
    closeImagePreview() {
      this.imagePreview.visible = false
    }
  },
  beforeDestroy() {
    this.removeReviewScrollSpy()
  }
}
</script>

<style scoped>
.paper-review-dialog ::v-deep .el-dialog__body {
  display: flex;
  flex-direction: column;
  max-height: 75vh;
  overflow: hidden;
}
.review-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
  min-height: 0;
}
.paper-meta {
  margin-bottom: 16px;
}
.paper-meta p {
  margin: 4px 0;
}
.review-layout {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  position: relative;
  flex: 1;
  min-height: 0;
}
.review-aside {
  width: 220px;
  flex-shrink: 0;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 12px;
  background: #f8f9fb;
  position: sticky;
  top: 12px;
  max-height: 70vh;
  overflow-y: auto;
  z-index: 2;
}
.aside-title {
  font-weight: 600;
  margin-bottom: 10px;
  color: #303133;
}
.aside-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.aside-tag {
  cursor: pointer;
}
.aside-tag.tag-active {
  box-shadow: 0 0 0 2px #409EFF inset;
}
.review-main {
  flex: 1;
  max-height: 75vh;
  overflow-y: auto;
  padding-right: 8px;
}
.student-answer {
  margin-top: 8px;
  color: #606266;
  font-size: 13px;
}
.answer-preview {
  margin-right: 8px;
}
.answer-preview-body {
  max-height: 60vh;
  overflow-y: auto;
  padding: 8px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
.answer-preview-body img {
  max-width: 160px;
  max-height: 160px;
  object-fit: contain;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
  background: #f5f7fa;
  margin: 8px 8px 8px 0;
  cursor: zoom-in;
}
::v-deep .score-column .cell {
  overflow: visible;
}
::v-deep .score-column .el-input-number {
  width: 120px;
}
</style>

