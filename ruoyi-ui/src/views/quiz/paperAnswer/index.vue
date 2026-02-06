<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="试卷名" prop="paperName">
        <el-input
          v-model="queryParams.paperName"
          placeholder="请输入试卷名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="试卷ID" prop="paperId">
        <el-input
          v-model="queryParams.paperId"
          placeholder="请输入试卷ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="回答人" prop="createUser">
        <el-input
          v-model="queryParams.createUser"
          placeholder="请输入回答人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="试卷总分" prop="paperScore">
        <el-input
          v-model="queryParams.paperScore"
          placeholder="请输入试卷总分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户得分" prop="finalScore">
        <el-input
          v-model="queryParams.finalScore"
          placeholder="请输入用户得分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="题目数量" prop="questionCount">
        <el-input
          v-model="queryParams.questionCount"
          placeholder="请输入题目数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="答对数量" prop="correctCount">
        <el-input
          v-model="queryParams.correctCount"
          placeholder="请输入答对数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
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
          v-hasPermi="['quiz:paperAnswer:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['quiz:paperAnswer:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['quiz:paperAnswer:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['quiz:paperAnswer:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="paperAnswerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="答卷记录ID" align="center" prop="paperAnswerId" />
      <el-table-column label="试卷名" align="center" prop="paperName" />
      <el-table-column label="试卷ID" align="center" prop="paperId" />
      <el-table-column label="回答人" align="center" prop="createUser" />
      <el-table-column label="试卷总分" align="center" prop="paperScore" />
      <el-table-column label="用户得分" align="center" prop="finalScore" />
      <el-table-column label="题目数量" align="center" prop="questionCount" />
      <el-table-column label="作答日期" align="center" prop="createTime" />
      <el-table-column label="作答时间" align="center" prop="doTime">
        <template slot-scope="scope">
          {{ formatSeconds(scope.row.doTime) }}
        </template>
      </el-table-column>
      <el-table-column label="答对数量" align="center" prop="correctCount" />
      <el-table-column label="所属科目" align="center" prop="subjectId">
        <template slot-scope="scope">
          <span>{{ getSubjectLabel(scope.row.subjectId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleViewDetail(scope.row)"
            v-hasPermi="['quiz:paperAnswer:query']"
          >查看详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['quiz:paperAnswer:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['quiz:paperAnswer:remove']"
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

    <!-- 查看详情对话框 -->
    <el-dialog title="试卷答题详情"
               :visible.sync="detailVisible"
               width="90%"
               class="paper-answer-dialog"
               :close-on-click-modal="false"
               append-to-body>
      <div v-if="detailLoading" style="text-align: center; padding: 40px 0;">
        <i class="el-icon-loading" style="font-size: 24px;"></i>
        <p style="margin-top: 10px;">加载中...</p>
      </div>
      <div v-else-if="paperReviewData" class="detail-body">
        <div class="detail-layout">
          <!-- 左侧题号导航 -->
          <div class="detail-aside" v-if="paperReviewData.paperDto && paperReviewData.paperDto.paperQuestionTypeDto">
            <div class="question-anchor-card">
              <p class="qa-title">{{ (paperReviewData.paperDto && paperReviewData.paperDto.paperName) || '试卷题目' }}</p>
              <div class="qa-summary" v-if="paperReviewData.paperAnswerDto">
                <p>得分：{{ (paperReviewData.paperAnswerDto.finalScore || 0) }}/{{ (paperReviewData.paperDto && paperReviewData.paperDto.score) || 0 }}</p>
                <p>耗时：{{ formatSeconds((paperReviewData.paperAnswerDto.doTime) || 0) }}</p>
              </div>
              <div v-for="(questionType, tIndex) in paperReviewData.paperDto.paperQuestionTypeDto"
                   :key="tIndex"
                   class="qa-type-block">
                <p class="qa-type-name">{{ questionType.name }}</p>
                <div class="qa-tags">
                  <el-tag
                    v-for="(question, qIndex) in questionType.questionDtos"
                    :key="qIndex"
                    size="mini"
                    class="qa-tag"
                    :class="{'qa-tag-active': activeQuestionOrder === question.itemOrder}"
                    @click="jumpToQuestion(question.itemOrder)"
                  >
                    {{ question.itemOrder + 1 }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧内容区域 -->
          <div class="detail-main" ref="detailMain">
            <!-- 试卷基本信息 -->
            <el-card class="box-card" style="margin-bottom: 20px;">
              <div slot="header" class="clearfix">
                <span style="font-weight: bold;">试卷信息</span>
              </div>
              <el-row :gutter="20">
                <el-col :span="6">
                  <p><strong>试卷名称：</strong>{{ (paperReviewData.paperDto && paperReviewData.paperDto.paperName) || '-' }}</p>
                </el-col>
                <el-col :span="6">
                  <p><strong>考生账号：</strong>{{ (paperReviewData.paperAnswerDto && paperReviewData.paperAnswerDto.createUser) || '-' }}</p>
                </el-col>
                <el-col :span="6">
                  <p><strong>试卷总分：</strong>{{ (paperReviewData.paperDto && paperReviewData.paperDto.score) || 0 }} 分</p>
                </el-col>
                <el-col :span="6">
                  <p><strong>最终得分：</strong>{{ (paperReviewData.paperAnswerDto && paperReviewData.paperAnswerDto.finalScore) || 0 }} 分</p>
                </el-col>
                <el-col :span="6">
                  <p><strong>客观题得分：</strong>{{ (paperReviewData.paperAnswerDto && paperReviewData.paperAnswerDto.objectiveScore) || 0 }} 分</p>
                </el-col>
                <el-col :span="6">
                  <p><strong>主观题得分：</strong>{{ (paperReviewData.paperAnswerDto && paperReviewData.paperAnswerDto.subjectiveScore) || 0 }} 分</p>
                </el-col>
                <el-col :span="6">
                  <p><strong>答题耗时：</strong>{{ formatSeconds((paperReviewData.paperAnswerDto && paperReviewData.paperAnswerDto.doTime) || 0) }}</p>
                </el-col>
                <el-col :span="6">
                  <p><strong>批改状态：</strong>
                    <el-tag :type="(paperReviewData.paperAnswerDto && paperReviewData.paperAnswerDto.reviewStatus) === 1 ? 'warning' : 'success'" size="small">
                      {{ (paperReviewData.paperAnswerDto && paperReviewData.paperAnswerDto.reviewStatus) === 1 ? '待批改' : '已完成' }}
                    </el-tag>
                  </p>
                </el-col>
              </el-row>
              <div v-if="paperReviewData.paperAnswerDto && paperReviewData.paperAnswerDto.reviewRemark" style="margin-top: 10px;">
                <p><strong>批改备注：</strong>{{ paperReviewData.paperAnswerDto.reviewRemark }}</p>
              </div>
            </el-card>

            <!-- 题目列表 -->
            <template v-if="paperReviewData.paperDto && paperReviewData.paperDto.paperQuestionTypeDto">
              <div v-for="(questionType, typeIndex) in paperReviewData.paperDto.paperQuestionTypeDto" :key="typeIndex" style="margin-bottom: 30px;">
                <el-card class="box-card">
                  <div slot="header" class="clearfix">
                    <span style="font-weight: bold; font-size: 16px;">{{ questionType.name }}</span>
                  </div>
                  <template v-if="questionType.questionDtos">
                    <div v-for="(question, qIndex) in questionType.questionDtos" :key="qIndex" class="question-item" :id="'question-' + question.itemOrder" style="margin-bottom: 30px; padding-bottom: 20px; border-bottom: 1px solid #ebeef5;">
              <!-- 题目 -->
              <div class="question-title" style="margin-bottom: 15px;">
                <p style="font-size: 15px; font-weight: 600; margin-bottom: 10px;">
                  <span>{{ question.itemOrder + 1 }}. </span>
                  <span
                    class="question-title-content"
                    :class="{'is-collapsed': shouldCollapseQuestion(question)}"
                    v-html="renderContent(question.questionTitle, question.questionTitleFormat)"></span>
                  <span style="color: #909399;">({{ question.score }}分)</span>
                </p>
                <div v-if="shouldShowQuestionToggle(question)" class="question-toggle">
                  <el-button type="text" size="mini" @click="toggleQuestionContent(question)">
                    {{ isQuestionExpanded(question) ? '收起' : '查看详情' }}
                  </el-button>
                </div>
              </div>

              <!-- 选项（单选题、多选题） -->
              <div v-if="question.questionType === 1 || question.questionType === 2" class="question-options" style="margin-bottom: 15px;">
                <div v-for="(option, oIndex) in question.items" :key="oIndex" style="margin-bottom: 8px; padding: 8px; background: #f5f7fa; border-radius: 4px;">
                  <span style="font-weight: 600; margin-right: 8px;">{{ option.prefix }}.</span>
                  <span v-html="renderContent(option.content, question.optionFormat)"></span>
                </div>
              </div>

              <!-- 答案信息 -->
              <div class="answer-info" style="margin-top: 15px;">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <p><strong>标准答案：</strong>
                      <span v-if="question.questionType === 1 || question.questionType === 4 || question.questionType === 5">
                        {{ question.correct }}
                      </span>
                      <span v-else>
                        {{ Array.isArray(question.correctArray) ? question.correctArray.join(', ') : question.correctArray }}
                      </span>
                    </p>
                  </el-col>
                  <el-col :span="12">
                    <p><strong>学生答案：</strong>
                      <template v-if="getUserAnswer(question.itemOrder)">
                        <span v-if="question.questionType === 3" v-html="getUserAnswer(question.itemOrder)"></span>
                        <span v-else-if="question.questionType === 2 || question.questionType === 5">
                          {{ Array.isArray(getUserAnswer(question.itemOrder)) ? getUserAnswer(question.itemOrder).join(', ') : getUserAnswer(question.itemOrder) }}
                        </span>
                        <span v-else>{{ getUserAnswer(question.itemOrder) }}</span>
                      </template>
                      <span v-else style="color: #909399;">未作答</span>
                    </p>
                  </el-col>
                </el-row>
                <el-row :gutter="20" style="margin-top: 10px;">
                  <el-col :span="12">
                    <p><strong>得分：</strong>
                      <span :style="{ color: getUserScore(question.itemOrder) >= question.score ? '#67c23a' : '#f56c6c', fontWeight: 'bold' }">
                        {{ getUserScore(question.itemOrder) || 0 }} / {{ question.score }}
                      </span>
                    </p>
                  </el-col>
                  <el-col :span="12">
                    <p><strong>是否正确：</strong>
                      <el-tag :type="getIsCorrect(question) ? 'success' : 'danger'" size="small">
                        {{ getIsCorrect(question) ? '正确' : '错误' }}
                      </el-tag>
                    </p>
                  </el-col>
                </el-row>
                <div v-if="getReviewComment(question.itemOrder)" style="margin-top: 10px; padding: 10px; background: #f0f9ff; border-radius: 4px;">
                  <p><strong>批改评语：</strong>{{ getReviewComment(question.itemOrder) }}</p>
                </div>
              </div>

              <!-- 解析 -->
              <div v-if="question.analysis" class="question-analysis" style="margin-top: 15px; padding: 15px; background: #f9fafc; border-radius: 4px; border-left: 4px solid #409eff;">
                <p style="font-weight: 600; margin-bottom: 8px; color: #409eff;">解析：</p>
                <div v-html="renderContent(question.analysis, question.analysisFormat)"></div>
              </div>
                    </div>
                  </template>
                </el-card>
              </div>
            </template>
          </div>
        </div>
      </div>
      <div v-else style="text-align: center; padding: 40px 0;">
        <p style="color: #909399;">暂无数据</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="detailVisible = false">关 闭</el-button>
      </span>
    </el-dialog>

    <!-- 添加或修改试卷作答情况对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="试卷名" prop="paperName">
          <el-input v-model="form.paperName" placeholder="请输入试卷名" />
        </el-form-item>
        <el-form-item label="试卷ID" prop="paperId">
          <el-input v-model="form.paperId" placeholder="请输入试卷ID" />
        </el-form-item>
        <el-form-item label="回答人" prop="createUser">
          <el-input v-model="form.createUser" placeholder="请输入回答人" />
        </el-form-item>
        <el-form-item label="试卷总分" prop="paperScore">
          <el-input v-model="form.paperScore" placeholder="请输入试卷总分" />
        </el-form-item>
        <el-form-item label="用户得分" prop="finalScore">
          <el-input v-model="form.finalScore" placeholder="请输入用户得分" />
        </el-form-item>
        <el-form-item label="题目数量" prop="questionCount">
          <el-input v-model="form.questionCount" placeholder="请输入题目数量" />
        </el-form-item>
        <el-form-item label="答对数量" prop="correctCount">
          <el-input v-model="form.correctCount" placeholder="请输入答对数量" />
        </el-form-item>
        <el-form-item label="所属科目" prop="subjectId">
          <el-select v-model="form.subjectId" placeholder="请选择科目">
            <el-option
              v-for="option in subjectOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
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
import { listPaperAnswer, getPaperAnswer, delPaperAnswer, addPaperAnswer, updatePaperAnswer } from "@/api/quiz/paperAnswer";
import { getPaperReview } from "@/api/quiz/paperReview";
import {optionSubject} from "@/api/quiz/subject";
import DOMPurify from 'dompurify';
import { marked } from 'marked';
import ElImageViewer from "element-ui/packages/image/src/image-viewer";

export default {
  name: "PaperAnswer",
  components: {
    ElImageViewer
  },
  data() {
    return {
      dateRange:[],
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
      // 试卷作答情况表格数据
      paperAnswerList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        paperName: null,
        paperId: null,
        createUser: null,
        paperScore: null,
        finalScore: null,
        questionCount: null,
        correctCount: null,
        subjectId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        paperName: [
          { required: true, message: "试卷名不能为空", trigger: "blur" }
        ],
        paperId: [
          { required: true, message: "试卷ID不能为空", trigger: "blur" }
        ],
        createUser: [
          { required: true, message: "回答人不能为空", trigger: "blur" }
        ],
        paperScore: [
          { required: true, message: "试卷总分不能为空", trigger: "blur" }
        ],
        questionCount: [
          { required: true, message: "题目数量不能为空", trigger: "blur" }
        ],
        subjectId: [
          { required: true, message: "所属科目不能为空", trigger: "change" }
        ]
      },
      subjectOptions: [],
      subjectMap: {},
      // 查看详情相关
      detailVisible: false,
      detailLoading: false,
      paperReviewData: null,
      activeQuestionOrder: null,
      questionTitleExpanded: {},
      imagePreview: {
        visible: false,
        urls: [],
        index: 0
      },
      detailBodyClickHandler: null,
      detailScrollHandler: null
    };
  },
  created() {
    this.loadSubjectOptions();
    this.getList();
  },
  methods: {
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
    /** 查询试卷作答情况列表 */
    getList() {
      this.loading = true;
      listPaperAnswer(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.paperAnswerList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        paperAnswerId: null,
        paperName: null,
        paperId: null,
        createUser: null,
        paperScore: null,
        finalScore: null,
        questionCount: null,
        correctCount: null,
        createTime: null,
        subjectId: null
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
      this.ids = selection.map(item => item.paperAnswerId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加试卷作答情况";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const paperAnswerId = row.paperAnswerId || this.ids
      getPaperAnswer(paperAnswerId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改试卷作答情况";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.paperAnswerId != null) {
            updatePaperAnswer(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPaperAnswer(this.form).then(response => {
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
      const paperAnswerIds = row.paperAnswerId || this.ids;
      this.$modal.confirm('是否确认删除试卷作答情况编号为"' + paperAnswerIds + '"的数据项？').then(function() {
        return delPaperAnswer(paperAnswerIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('quiz/paperAnswer/export', {
        ...this.queryParams
      }, `paperAnswer_${new Date().getTime()}.xlsx`)
    },
    /** 查看详情按钮操作 */
    handleViewDetail(row) {
      this.detailVisible = true;
      this.detailLoading = true;
      this.paperReviewData = null;
      this.activeQuestionOrder = null;
      getPaperReview(row.paperAnswerId).then(response => {
        const data = response.data || {};
        this.paperReviewData = {
          ...data,
          paperDto: data.paperDto || { paperQuestionTypeDto: [] },
          paperAnswerDto: data.paperAnswerDto || { questionAnswerDtos: [] }
        };
        this.detailLoading = false;
        this.$nextTick(() => {
          this.setInitialActiveQuestion();
          this.bindImagePreview();
          this.bindDetailScrollSpy();
        });
      }).catch(() => {
        this.$modal.msgError("加载详情失败");
        this.detailLoading = false;
      });
    },
    /** 格式化时间（秒转时分秒） */
    formatSeconds(seconds) {
      if (!seconds || seconds === 0) return '0秒';
      const hours = Math.floor(seconds / 3600);
      const minutes = Math.floor((seconds % 3600) / 60);
      const secs = seconds % 60;
      let result = '';
      if (hours > 0) result += hours + '时';
      if (minutes > 0) result += minutes + '分';
      if (secs > 0 || result === '') result += secs + '秒';
      return result;
    },
    /** 渲染内容（支持HTML和Markdown） */
    renderContent(content, format) {
      if (!content) return '';
      const contentFormat = format || 'html';
      if (contentFormat === 'markdown') {
        try {
          let html = marked(content);
          // 处理表格，添加边框样式
          html = html.replace(/<table([^>]*)>/gi, (match, attrs) => {
            return `<table${attrs} style="border-collapse: collapse; width: 100%; margin: 10px 0; border: 1px solid #dcdfe6;">`;
          });
          html = html.replace(/<th([^>]*)>/gi, (match, attrs) => {
            return `<th${attrs} style="border: 1px solid #dcdfe6; padding: 8px 12px; text-align: left; background-color: #f5f7fa; font-weight: 600;">`;
          });
          html = html.replace(/<td([^>]*)>/gi, (match, attrs) => {
            return `<td${attrs} style="border: 1px solid #dcdfe6; padding: 8px 12px; text-align: left;">`;
          });
          return DOMPurify.sanitize(html, {
            ALLOWED_TAGS: ['p', 'br', 'strong', 'em', 'u', 'ol', 'ul', 'li', 'img', 'a', 'span', 'div', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'blockquote', 'code', 'pre', 'table', 'thead', 'tbody', 'tr', 'th', 'td'],
            ALLOWED_ATTR: ['href', 'src', 'alt', 'title', 'class', 'style', 'target'],
            ALLOW_DATA_ATTR: false
          });
        } catch (error) {
          console.error('Markdown渲染失败:', error);
          return this.sanitizeHtml(content.replace(/\n/g, '<br>'));
        }
      } else {
        return this.sanitizeHtml(content);
      }
    },
    /** HTML安全处理 */
    sanitizeHtml(html) {
      if (!html) return '';
      if (!/<[^>]+>/.test(html)) {
        return html;
      }
      return DOMPurify.sanitize(html, {
        ALLOWED_TAGS: ['p', 'br', 'strong', 'em', 'u', 'ol', 'ul', 'li', 'img', 'a', 'span', 'div', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'blockquote', 'code', 'pre', 'table', 'thead', 'tbody', 'tr', 'th', 'td'],
        ALLOWED_ATTR: ['href', 'src', 'alt', 'title', 'class', 'style', 'target'],
        ALLOW_DATA_ATTR: false
      });
    },
    /** 获取用户答案 */
    getUserAnswer(itemOrder) {
      if (!this.paperReviewData || !this.paperReviewData.paperAnswerDto || !this.paperReviewData.paperAnswerDto.questionAnswerDtos) {
        return null;
      }
      const answer = this.paperReviewData.paperAnswerDto.questionAnswerDtos.find(qa => qa.itemOrder === itemOrder);
      if (!answer) return null;
      if (answer.contentArray && answer.contentArray.length > 0) {
        return answer.contentArray;
      }
      return answer.content || null;
    },
    /** 获取用户得分 */
    getUserScore(itemOrder) {
      if (!this.paperReviewData || !this.paperReviewData.paperAnswerDto || !this.paperReviewData.paperAnswerDto.questionAnswerDtos) {
        return 0;
      }
      const answer = this.paperReviewData.paperAnswerDto.questionAnswerDtos.find(qa => qa.itemOrder === itemOrder);
      return answer && typeof answer.finalScore === 'number' ? answer.finalScore : 0;
    },
    /** 获取是否正确：优先看得分是否满分，其次看后端标记的 isCorrect */
    getIsCorrect(question) {
      if (!question) return false;
      if (!this.paperReviewData || !this.paperReviewData.paperAnswerDto || !this.paperReviewData.paperAnswerDto.questionAnswerDtos) {
        return false;
      }
      const answer = this.paperReviewData.paperAnswerDto.questionAnswerDtos.find(qa => qa.itemOrder === question.itemOrder);
      if (!answer) return false;
      const totalScore = typeof question.score === 'number' ? question.score : Number(question.score);
      if (typeof answer.finalScore === 'number' && Number.isFinite(totalScore) && totalScore > 0) {
        if (answer.finalScore >= totalScore) {
          return true;
        }
      }
      return answer.isCorrect === true;
    },
    /** 获取批改评语 */
    getReviewComment(itemOrder) {
      if (!this.paperReviewData || !this.paperReviewData.paperAnswerDto || !this.paperReviewData.paperAnswerDto.questionAnswerDtos) {
        return null;
      }
      const answer = this.paperReviewData.paperAnswerDto.questionAnswerDtos.find(qa => qa.itemOrder === itemOrder);
      return (answer && answer.reviewComment) || null;
    },
    /** 左侧题号点击跳转到对应题目（右侧容器内滚动） */
    jumpToQuestion(itemOrder) {
      const container = this.$refs.detailMain;
      if (!container) return;
      const target = container.querySelector(`#question-${itemOrder}`);
      if (!target) return;
      const containerRect = container.getBoundingClientRect();
      const targetRect = target.getBoundingClientRect();
      const offset = targetRect.top - containerRect.top - 20;
      container.scrollTo({
        top: container.scrollTop + offset,
        behavior: 'smooth'
      });
    },
    cleanQuestionText(question) {
      const raw = (question && question.questionTitle) || '';
      return raw.replace(/<[^>]+>/g, '').replace(/\s+/g, ' ').trim();
    },
    shouldShowQuestionToggle(question) {
      const text = this.cleanQuestionText(question);
      return text.length > 120;
    },
    isQuestionExpanded(question) {
      if (!question) return false;
      return !!this.questionTitleExpanded[question.itemOrder];
    },
    toggleQuestionContent(question) {
      if (!question) return;
      const current = this.isQuestionExpanded(question);
      this.$set(this.questionTitleExpanded, question.itemOrder, !current);
    },
    shouldCollapseQuestion(question) {
      return this.shouldShowQuestionToggle(question) && !this.isQuestionExpanded(question);
    },
    /** 滚动高亮当前题序 */
    bindDetailScrollSpy() {
      const container = this.$refs.detailMain;
      if (!container) return;
      this.removeDetailScrollSpy();
      const handler = () => {
        const sections = Array.from(container.querySelectorAll('.question-item'));
        if (!sections.length) return;
        const parentRect = container.getBoundingClientRect();
        const offset = 40;
        let current = sections[0].id;
        for (const sec of sections) {
          const rect = sec.getBoundingClientRect();
          const top = rect.top - parentRect.top;
          if (top - offset <= 0) {
            current = sec.id;
          } else {
            break;
          }
        }
        if (current) {
          const order = Number((current || '').replace('question-', ''));
          this.activeQuestionOrder = Number.isFinite(order) ? order : null;
        }
      };
      this.detailScrollHandler = handler;
      container.addEventListener('scroll', handler, { passive: true });
      handler();
    },
    setInitialActiveQuestion() {
      if (!this.paperReviewData || !this.paperReviewData.paperDto || !this.paperReviewData.paperDto.paperQuestionTypeDto) {
        this.activeQuestionOrder = null;
        return;
      }
      const allQuestions = this.paperReviewData.paperDto.paperQuestionTypeDto
        .flatMap(t => t.questionDtos || []);
      const first = allQuestions[0];
      this.activeQuestionOrder = first && first.itemOrder !== undefined ? first.itemOrder : null;
    },
    removeDetailScrollSpy() {
      const container = this.$refs.detailMain;
      if (container && this.detailScrollHandler) {
        container.removeEventListener('scroll', this.detailScrollHandler);
      }
      this.detailScrollHandler = null;
    },
    /** 绑定图片预览点击 */
    bindImagePreview() {
      const body = this.$refs.detailMain;
      if (!body) return;
      if (this.detailBodyClickHandler) {
        body.removeEventListener('click', this.detailBodyClickHandler);
      }
      this.detailBodyClickHandler = (e) => {
        const target = e.target || e.srcElement;
        if (!target || target.tagName !== 'IMG') return;
        const imgs = Array.from(body.querySelectorAll('img'))
          .map(img => img.getAttribute('src'))
          .filter(Boolean);
        const index = imgs.indexOf(target.getAttribute('src'));
        if (imgs.length) {
          this.imagePreview.urls = imgs;
          this.imagePreview.index = index >= 0 ? index : 0;
          this.imagePreview.visible = true;
        }
      };
      body.addEventListener('click', this.detailBodyClickHandler);
    },
    closeImagePreview() {
      this.imagePreview.visible = false;
    }
  },
  beforeDestroy() {
    const body = this.$refs.detailMain;
    if (body && this.detailBodyClickHandler) {
      body.removeEventListener('click', this.detailBodyClickHandler);
    }
    this.removeDetailScrollSpy();
  }
};
</script>

<style scoped>
.paper-answer-dialog ::v-deep .el-dialog__body {
  display: flex;
  flex-direction: column;
  max-height: 75vh;
  overflow: hidden;
}
.detail-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}
.detail-layout {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  flex: 1;
  min-height: 0;
}

/* 左侧题号导航卡片 */
.detail-aside {
  width: 240px;
  flex-shrink: 0;
  position: sticky;
  top: 12px;
  max-height: 70vh;
  overflow-y: auto;
}
.question-anchor-card {
  background: #fff;
  border-radius: 8px;
  padding: 12px 12px 4px;
  border: 1px solid #ebeef5;
  position: sticky;
  top: 0;
}
.qa-title {
  font-weight: 600;
  margin-bottom: 8px;
}
.qa-summary {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}
.qa-type-block {
  margin-bottom: 8px;
}
.qa-type-name {
  font-size: 13px;
  margin-bottom: 4px;
}
.qa-tags {
  display: flex;
  flex-wrap: wrap;
}
.qa-tag {
  margin: 2px;
  min-width: 26px;
  text-align: center;
}
.qa-tag-active {
  box-shadow: 0 0 0 2px #409eff inset;
  color: #409eff;
  background: #ecf5ff;
  border-color: #409eff;
}

.detail-main {
  flex: 1;
  max-height: 75vh;
  overflow-y: auto;
  padding-right: 8px;
}

/* 限制解析区域中的图片大小，保持与前台一致 */
.question-analysis >>> img {
  max-width: 200px;
  max-height: 200px;
  width: auto;
  height: auto;
  cursor: zoom-in;
  display: inline-block;
}

/* 题干、选项中的图片也限制尺寸 */
.question-title >>> img,
.question-options >>> img {
  max-width: 200px;
  max-height: 200px;
  width: auto;
  height: auto;
  cursor: zoom-in;
  display: inline-block;
}

.question-title-content {
  display: inline-block;
  word-break: break-word;
}
.question-title-content.is-collapsed {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.question-toggle {
  margin-top: 4px;
}
</style>
