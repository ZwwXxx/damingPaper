<template>
  <div class="dashboard-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <i class="el-icon-data-analysis"></i>
        数据大屏
      </h2>
      <el-button 
        type="primary" 
        icon="el-icon-refresh" 
        size="small" 
        @click="refreshAllData"
        :loading="loading"
      >
        刷新数据
      </el-button>
    </div>

    <!-- 核心数据卡片区 -->
    <el-row :gutter="20" class="overview-row">
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <div class="stat-card">
          <div class="stat-icon user-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-content">
            <div class="stat-title">总用户数</div>
            <div class="stat-value">{{ overviewData.totalUsers || 0 }}</div>
            <div class="stat-growth" :class="getGrowthClass(overviewData.totalUsersGrowth)">
              <i :class="getGrowthIcon(overviewData.totalUsersGrowth)"></i>
              {{ overviewData.totalUsersGrowth || '0' }}
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <div class="stat-card">
          <div class="stat-icon question-icon">
            <i class="el-icon-edit"></i>
          </div>
          <div class="stat-content">
            <div class="stat-title">总题目数</div>
            <div class="stat-value">{{ overviewData.totalQuestions || 0 }}</div>
            <div class="stat-growth" :class="getGrowthClass(overviewData.totalQuestionsGrowth)">
              <i :class="getGrowthIcon(overviewData.totalQuestionsGrowth)"></i>
              {{ overviewData.totalQuestionsGrowth || '0' }}
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <div class="stat-card">
          <div class="stat-icon paper-icon">
            <i class="el-icon-document"></i>
          </div>
          <div class="stat-content">
            <div class="stat-title">总试卷数</div>
            <div class="stat-value">{{ overviewData.totalPapers || 0 }}</div>
            <div class="stat-growth" :class="getGrowthClass(overviewData.totalPapersGrowth)">
              <i :class="getGrowthIcon(overviewData.totalPapersGrowth)"></i>
              {{ overviewData.totalPapersGrowth || '0' }}
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <div class="stat-card">
          <div class="stat-icon exam-icon">
            <i class="el-icon-s-order"></i>
          </div>
          <div class="stat-content">
            <div class="stat-title">总考试次数</div>
            <div class="stat-value">{{ overviewData.totalExams || 0 }}</div>
            <div class="stat-growth" :class="getGrowthClass(overviewData.totalExamsGrowth)">
              <i :class="getGrowthIcon(overviewData.totalExamsGrowth)"></i>
              {{ overviewData.totalExamsGrowth || '0' }}
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 用户登录趋势 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">用户登录趋势</h3>
            <el-tag size="small" type="info">最近30天</el-tag>
          </div>
          <div class="chart-body">
            <div id="loginTrendChart" style="width: 100%; height: 350px;"></div>
          </div>
        </div>
      </el-col>

      <!-- 考试趋势 - 按日期 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">考试趋势（按日期）</h3>
            <el-tag size="small" type="info">最近30天</el-tag>
          </div>
          <div class="chart-body">
            <div id="examTrendChart" style="width: 100%; height: 350px;"></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 按试卷分类的考试统计 -->
      <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">热门试卷考试统计</h3>
            <el-tag size="small" type="warning">最近30天 TOP5</el-tag>
          </div>
          <div class="chart-body">
            <el-empty v-if="!examData.paperExamStats || examData.paperExamStats.length === 0" description="暂无考试数据" />
            <el-table
              v-else
              :data="examData.paperExamStats"
              style="width: 100%"
            >
              <el-table-column type="index" label="排名" width="60" align="center" />
              <el-table-column prop="paperName" label="试卷名称" min-width="200" show-overflow-tooltip />
              <el-table-column prop="examCount" label="考试人次" width="120" align="center" sortable>
                <template slot-scope="scope">
                  <el-tag type="primary" size="small">{{ scope.row.examCount }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="avgScore" label="平均分" width="150" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.avgScore }} / {{ scope.row.totalScore }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="avgScorePercent" label="平均得分率" width="150" align="center" sortable>
                <template slot-scope="scope">
                  <el-progress 
                    :percentage="scope.row.avgScorePercent" 
                    :color="getScorePercentColor(scope.row.avgScorePercent)"
                  ></el-progress>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 科目题目分布 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">科目题目分布</h3>
          </div>
          <div class="chart-body">
            <div id="subjectDistributionChart" style="width: 100%; height: 350px;"></div>
          </div>
        </div>
      </el-col>

      <!-- 题型分布 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">题型分布</h3>
          </div>
          <div class="chart-body">
            <div id="typeDistributionChart" style="width: 100%; height: 350px;"></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
  

      <!-- 批改进度和考试时间分布 -->
      <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">批改进度</h3>
          </div>
          <div class="chart-body">
            <div id="reviewProgressChart" style="width: 100%; height: 160px;"></div>
          </div>
        </div>

        <div class="chart-card" style="margin-top: 20px;">
          <div class="chart-header">
            <h3 class="chart-title">考试时间分布</h3>
          </div>
          <div class="chart-body">
            <div id="timeDistributionChart" style="width: 100%; height: 160px;"></div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import {
  getOverview,
  getUserActivity,
  getExamStatistics,
  getQuestionStatistics,
  getPaperStatistics,
  getTimeDistribution
} from '@/api/dashboard'

export default {
  name: 'Dashboard',
  data() {
    return {
      loading: false,
      overviewData: {},
      userActivityData: {},
      examData: {
        paperExamStats: []
      },
      questionData: {},
      paperData: {
        topPapers: []
      },
      timeData: {},
      charts: {}
    }
  },
  mounted() {
    this.loadAllData()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    // 销毁所有图表
    Object.values(this.charts).forEach(chart => {
      if (chart) {
        chart.dispose()
      }
    })
  },
  methods: {
    // 加载所有数据
    async loadAllData() {
      this.loading = true
      try {
        await Promise.all([
          this.loadOverview(),
          this.loadUserActivity(),
          this.loadExamStatistics(),
          this.loadQuestionStatistics(),
          this.loadPaperStatistics(),
          this.loadTimeDistribution()
        ])
        this.$modal.msgSuccess('数据加载成功')
      } catch (error) {
        console.error('数据加载失败:', error)
        this.$modal.msgError('数据加载失败')
      } finally {
        this.loading = false
      }
    },

    // 刷新所有数据
    refreshAllData() {
      this.loadAllData()
    },

    // 加载概览数据
    async loadOverview() {
      const res = await getOverview()
      this.overviewData = res.data || {}
    },

    // 加载用户活跃度数据
    async loadUserActivity() {
      const res = await getUserActivity()
      this.userActivityData = res.data || {}
      this.$nextTick(() => {
        this.renderLoginTrendChart()
      })
    },

    // 加载考试统计数据
    async loadExamStatistics() {
      const res = await getExamStatistics()
      console.log('考试统计API返回:', res)
      console.log('试卷统计数据:', res.data ? res.data.paperExamStats : '无数据')
      this.examData = res.data || { paperExamStats: [] }
      // 确保paperExamStats存在
      if (!this.examData.paperExamStats) {
        this.examData.paperExamStats = []
      }
      this.$nextTick(() => {
        this.renderExamTrendChart()
        this.renderReviewProgressChart()
      })
    },

    // 加载题目统计数据
    async loadQuestionStatistics() {
      const res = await getQuestionStatistics()
      this.questionData = res.data || {}
      this.$nextTick(() => {
        this.renderSubjectDistributionChart()
        this.renderTypeDistributionChart()
      })
    },

    // 加载试卷统计数据
    async loadPaperStatistics() {
      const res = await getPaperStatistics()
      this.paperData = res.data || { topPapers: [] }
    },

    // 加载时间分布数据
    async loadTimeDistribution() {
      const res = await getTimeDistribution()
      this.timeData = res.data || {}
      this.$nextTick(() => {
        this.renderTimeDistributionChart()
      })
    },

    // 渲染登录趋势图
    renderLoginTrendChart() {
      const chartDom = document.getElementById('loginTrendChart')
      if (!chartDom) return

      if (this.charts.loginTrend) {
        this.charts.loginTrend.dispose()
      }

      const chart = echarts.init(chartDom)
      const loginTrend = this.userActivityData.loginTrend || []

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['登录人次', '唯一用户数']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: loginTrend.map(item => item.date)
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '登录人次',
            type: 'line',
            data: loginTrend.map(item => item.logins),
            smooth: true,
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
              ])
            }
          },
          {
            name: '唯一用户数',
            type: 'line',
            data: loginTrend.map(item => item.uniqueUsers),
            smooth: true,
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      }

      chart.setOption(option)
      this.charts.loginTrend = chart
    },

    // 渲染考试趋势图（按日期）
    renderExamTrendChart() {
      const chartDom = document.getElementById('examTrendChart')
      if (!chartDom) return

      if (this.charts.examTrend) {
        this.charts.examTrend.dispose()
      }

      const chart = echarts.init(chartDom)
      const examTrend = this.examData.examTrend || []

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: examTrend.map(item => item.date),
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          name: '考试人次'
        },
        series: [
          {
            name: '考试人次',
            type: 'bar',
            data: examTrend.map(item => item.count),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 0.5, color: '#188df0' },
                { offset: 1, color: '#188df0' }
              ])
            },
            barWidth: '60%'
          }
        ]
      }

      chart.setOption(option)
      this.charts.examTrend = chart
    },

    // 渲染科目分布图
    renderSubjectDistributionChart() {
      const chartDom = document.getElementById('subjectDistributionChart')
      if (!chartDom) return

      if (this.charts.subjectDistribution) {
        this.charts.subjectDistribution.dispose()
      }

      const chart = echarts.init(chartDom)
      const subjectDistribution = this.questionData.subjectDistribution || []

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          data: subjectDistribution.map(item => item.subjectName)
        },
        series: [
          {
            name: '科目分布',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 20,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: subjectDistribution.map(item => ({
              value: item.count,
              name: item.subjectName
            }))
          }
        ]
      }

      chart.setOption(option)
      this.charts.subjectDistribution = chart
    },

    // 渲染题型分布图
    renderTypeDistributionChart() {
      const chartDom = document.getElementById('typeDistributionChart')
      if (!chartDom) return

      if (this.charts.typeDistribution) {
        this.charts.typeDistribution.dispose()
      }

      const chart = echarts.init(chartDom)
      const typeDistribution = this.questionData.typeDistribution || []

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: typeDistribution.map(item => item.typeName)
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '题目数量',
            type: 'bar',
            data: typeDistribution.map(item => item.count),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 0.5, color: '#188df0' },
                { offset: 1, color: '#188df0' }
              ])
            },
            barWidth: '60%'
          }
        ]
      }

      chart.setOption(option)
      this.charts.typeDistribution = chart
    },

    // 渲染批改进度图
    renderReviewProgressChart() {
      const chartDom = document.getElementById('reviewProgressChart')
      if (!chartDom) return

      if (this.charts.reviewProgress) {
        this.charts.reviewProgress.dispose()
      }

      const chart = echarts.init(chartDom)
      const reviewProgress = this.examData.reviewProgress || {}

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          bottom: 10,
          left: 'center'
        },
        series: [
          {
            name: '批改进度',
            type: 'pie',
            radius: '70%',
            center: ['50%', '45%'],
            data: [
              { value: reviewProgress.reviewed || 0, name: '已批改', itemStyle: { color: '#67C23A' } },
              { value: reviewProgress.pending || 0, name: '待批改', itemStyle: { color: '#E6A23C' } }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            label: {
              show: true,
              formatter: '{b}\n{c}份'
            }
          }
        ]
      }

      chart.setOption(option)
      this.charts.reviewProgress = chart
    },

    // 渲染时间分布图
    renderTimeDistributionChart() {
      const chartDom = document.getElementById('timeDistributionChart')
      if (!chartDom) return

      if (this.charts.timeDistribution) {
        this.charts.timeDistribution.dispose()
      }

      const chart = echarts.init(chartDom)
      const hourlyDistribution = this.timeData.hourlyDistribution || []

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '5%',
          right: '5%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: hourlyDistribution.map(item => item.hour + '时'),
          axisLabel: {
            interval: 2
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '考试人次',
            type: 'bar',
            data: hourlyDistribution.map(item => item.count),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#ffd700' },
                { offset: 1, color: '#ff8c00' }
              ])
            }
          }
        ]
      }

      chart.setOption(option)
      this.charts.timeDistribution = chart
    },

    // 窗口大小改变时重新渲染图表
    handleResize() {
      Object.values(this.charts).forEach(chart => {
        if (chart) {
          chart.resize()
        }
      })
    },

    // 获取增长样式类
    getGrowthClass(growth) {
      if (!growth) return ''
      const value = parseInt(growth)
      return value >= 0 ? 'growth-up' : 'growth-down'
    },

    // 获取增长图标
    getGrowthIcon(growth) {
      if (!growth) return ''
      const value = parseInt(growth)
      return value >= 0 ? 'el-icon-top' : 'el-icon-bottom'
    },

    // 获取排名标签类型
    getRankTagType(index) {
      const types = ['danger', 'warning', 'success']
      return types[index] || ''
    },

    // 获取分数标签类型
    getScoreTagType(score) {
      if (score >= 85) return 'success'
      if (score >= 60) return ''
      return 'danger'
    },

    // 根据得分率返回进度条颜色
    getScorePercentColor(percent) {
      if (percent >= 85) return '#67C23A'  // 绿色-优秀
      if (percent >= 60) return '#409EFF'  // 蓝色-及格
      return '#F56C6C'  // 红色-不及格
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 84px);

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .page-title {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin: 0;

      i {
        margin-right: 10px;
        color: #409EFF;
      }
    }
  }

  .overview-row {
    margin-bottom: 20px;
  }

  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
    }

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28px;
      margin-right: 15px;

      &.user-icon {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: #fff;
      }

      &.question-icon {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        color: #fff;
      }

      &.paper-icon {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        color: #fff;
      }

      &.exam-icon {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        color: #fff;
      }
    }

    .stat-content {
      flex: 1;

      .stat-title {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
      }

      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 5px;
      }

      .stat-growth {
        font-size: 12px;

        &.growth-up {
          color: #67C23A;
        }

        &.growth-down {
          color: #F56C6C;
        }

        i {
          margin-right: 2px;
        }
      }
    }
  }

  .chart-row {
    margin-bottom: 20px;
  }

  .chart-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .chart-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin: 0;
      }
    }

    .chart-body {
      position: relative;
    }
  }
}

// 响应式适配
@media (max-width: 768px) {
  .dashboard-container {
    padding: 10px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;

      .page-title {
        margin-bottom: 10px;
      }
    }

    .stat-card {
      padding: 15px;

      .stat-icon {
        width: 50px;
        height: 50px;
        font-size: 24px;
      }

      .stat-content .stat-value {
        font-size: 24px;
      }
    }
  }
}
</style>
