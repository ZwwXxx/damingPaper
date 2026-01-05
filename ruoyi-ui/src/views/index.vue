<template>
  <div class="app-container home">
    <!-- 联系方式区域 -->
    <div class="contact-section">
      <div class="section-header">
        <h2 class="section-title">
          <i class="el-icon-phone-outline"></i>
          联系方式
        </h2>
        <p class="section-desc">如有问题或建议，欢迎联系作者</p>
      </div>
      
      <el-row :gutter="20" class="contact-row">
        <!-- QQ群 -->
        <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
          <div class="contact-card qq-group" @click="copyText('1041489197', 'QQ群')">
            <div class="card-icon">
              <i class="el-icon-qq"></i>
            </div>
            <div class="card-title">QQ群</div>
            <div class="card-value">1041489197</div>
            <div class="card-copy" @click.stop="copyText('1041489197', 'QQ群')">
              <i class="el-icon-document-copy"></i> 复制
            </div>
          </div>
        </el-col>
        
        <!-- QQ号 -->
        <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
          <div class="contact-card qq-number" @click="copyText('1626016153', 'QQ号')">
            <div class="card-icon">
              <i class="el-icon-qq"></i>
            </div>
            <div class="card-title">QQ号</div>
            <div class="card-value">1626016153</div>
            <div class="card-copy" @click.stop="copyText('1626016153', 'QQ号')">
              <i class="el-icon-document-copy"></i> 复制
            </div>
          </div>
        </el-col>
        
        <!-- 微信号 -->
        <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
          <div class="contact-card wechat-id" @click="copyText('anni0891xx', '微信号')">
            <div class="card-icon">
              <i class="el-icon-message"></i>
            </div>
            <div class="card-title">微信号</div>
            <div class="card-value">anni0891xx</div>
            <div class="card-copy" @click.stop="copyText('anni0891xx', '微信号')">
              <i class="el-icon-document-copy"></i> 复制
            </div>
          </div>
        </el-col>
        
        <!-- 微信二维码 -->
        <el-col :xs="12" :sm="8" :md="6" :lg="6" :xl="6">
          <div class="contact-card qr-card">
            <div class="card-icon">
              <i class="el-icon-picture-outline"></i>
            </div>
            <div class="card-title">微信二维码</div>
            <div class="qr-wrapper">
              <img 
                src="https://cdn.zww0891.fun/image-20260105113204128.png" 
                alt="微信二维码" 
                class="qr-image"
                @error="handleImageError"
              />
            </div>
            <div class="card-tip">扫码添加微信</div>
          </div>
        </el-col>
        
        <!-- 支持作者 -->
        <el-col :xs="12" :sm="8" :md="6" :lg="6" :xl="6">
          <div class="contact-card support-card">
            <div class="card-icon">
              <i class="el-icon-coin"></i>
            </div>
            <div class="card-title">支持作者</div>
            <div class="qr-wrapper">
              <img 
                src="https://cdn.zww0891.fun/image-20260105113105506.png" 
                alt="微信收款码" 
                class="qr-image"
                @error="handleImageError"
              />
            </div>
            <div class="card-tip">☕ 请作者喝杯咖啡</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 可爱的Q版弹窗 -->
    <transition name="bounce">
      <div v-if="showCopyDialog" class="cute-dialog" @click="closeDialog">
        <div class="dialog-content" @click.stop>
          <div class="dialog-emoji">🎉</div>
          <div class="dialog-text">{{ copyMessage }}</div>
          <div class="dialog-subtitle">已复制到剪贴板啦~</div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "3.8.8",
      showCopyDialog: false,
      copyMessage: '',
      copyTimer: null
    };
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    },
    // 处理图片加载错误
    handleImageError(event) {
      event.target.style.display = 'none'
    },
    // 复制文本（使用现代 Clipboard API）
    async copyText(text, label) {
      try {
        // 优先使用现代 Clipboard API
        if (navigator.clipboard && navigator.clipboard.writeText) {
          await navigator.clipboard.writeText(text);
          this.showSuccessDialog(`${label}已复制！`);
        } else {
          // 降级方案：使用传统方法
          const input = document.createElement('input');
          input.value = text;
          input.style.position = 'fixed';
          input.style.opacity = '0';
          input.style.left = '-9999px';
          document.body.appendChild(input);
          input.select();
          const success = document.execCommand('copy');
          document.body.removeChild(input);
          
          if (success) {
            this.showSuccessDialog(`${label}已复制！`);
          } else {
            this.showErrorDialog('复制失败，请手动复制');
          }
        }
      } catch (err) {
        // 如果 Clipboard API 失败，尝试降级方案
        try {
          const input = document.createElement('input');
          input.value = text;
          input.style.position = 'fixed';
          input.style.opacity = '0';
          input.style.left = '-9999px';
          document.body.appendChild(input);
          input.select();
          const success = document.execCommand('copy');
          document.body.removeChild(input);
          
          if (success) {
            this.showSuccessDialog(`${label}已复制！`);
          } else {
            this.showErrorDialog('复制失败，请手动复制');
          }
        } catch (e) {
          this.showErrorDialog('复制失败，请手动复制');
        }
      }
    },
    // 显示成功弹窗
    showSuccessDialog(message) {
      this.copyMessage = message;
      this.showCopyDialog = true;
      
      // 清除之前的定时器
      if (this.copyTimer) {
        clearTimeout(this.copyTimer);
      }
      
      // 3秒后自动关闭
      this.copyTimer = setTimeout(() => {
        this.closeDialog();
      }, 3000);
    },
    // 显示错误弹窗
    showErrorDialog(message) {
      this.copyMessage = message;
      this.showCopyDialog = true;
      
      // 清除之前的定时器
      if (this.copyTimer) {
        clearTimeout(this.copyTimer);
      }
      
      // 2秒后自动关闭
      this.copyTimer = setTimeout(() => {
        this.closeDialog();
      }, 2000);
    },
    // 关闭弹窗
    closeDialog() {
      this.showCopyDialog = false;
      if (this.copyTimer) {
        clearTimeout(this.copyTimer);
        this.copyTimer = null;
      }
    }
  },
  beforeDestroy() {
    // 组件销毁前清除定时器
    if (this.copyTimer) {
      clearTimeout(this.copyTimer);
    }
  }
};
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }

  .contact-section {
    margin-top: 30px;
    padding: 30px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    .section-header {
      text-align: center;
      margin-bottom: 30px;

      .section-title {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
        margin: 0 0 10px 0;
        display: flex;
        align-items: center;
        justify-content: center;

        i {
          margin-right: 10px;
          color: #409EFF;
          font-size: 32px;
        }
      }

      .section-desc {
        font-size: 14px;
        color: #909399;
        margin: 0;
      }
    }

    .contact-row {
      margin-bottom: 0;
    }

    .contact-card {
      background: #fff;
      border: 1px solid #EBEEF5;
      border-radius: 12px;
      padding: 24px;
      text-align: center;
      transition: all 0.3s;
      cursor: pointer;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 200px;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
        border-color: #409EFF;
      }

      .card-icon {
        width: 56px;
        height: 56px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        margin-bottom: 16px;
        color: #fff;
      }

      .card-title {
        font-size: 16px;
        color: #606266;
        margin-bottom: 12px;
        font-weight: 500;
      }

      .card-value {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 12px;
        word-break: break-all;
      }

      .card-copy {
        font-size: 12px;
        color: #409EFF;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 4px;
        transition: color 0.3s;

        &:hover {
          color: #66b1ff;
        }

        i {
          font-size: 14px;
        }
      }

      .qr-wrapper {
        width: 100%;
        display: flex;
        justify-content: center;
        margin: 12px 0;

        .qr-image {
          width: 100%;
          max-width: 180px;
          height: auto;
          border-radius: 8px;
          border: 1px solid #EBEEF5;
          background: #fff;
          padding: 8px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          transition: transform 0.3s;
        }
      }

      .card-tip {
        font-size: 13px;
        color: #909399;
        margin-top: 8px;
      }

      &.qq-group .card-icon {
        background: linear-gradient(135deg, #12B7F5 0%, #0D9AE8 100%);
      }

      &.qq-number .card-icon {
        background: linear-gradient(135deg, #12B7F5 0%, #0D9AE8 100%);
      }

      &.wechat-id .card-icon {
        background: linear-gradient(135deg, #07C160 0%, #06AD56 100%);
      }

      &.qr-card .card-icon {
        background: linear-gradient(135deg, #07C160 0%, #06AD56 100%);
      }

      &.support-card .card-icon {
        background: linear-gradient(135deg, #FF6B6B 0%, #EE5A6F 100%);
      }

      &:hover .qr-image {
        transform: scale(1.05);
      }
    }
  }

  @media (max-width: 768px) {
    .contact-section {
      padding: 20px;

      .section-header .section-title {
        font-size: 22px;
      }

      .contact-card {
        min-height: 160px;
        padding: 20px;

        .card-icon {
          width: 48px;
          height: 48px;
          font-size: 24px;
        }
      }
    }
  }

  /* 可爱的Q版弹窗样式 */
  .cute-dialog {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
    backdrop-filter: blur(4px);
  }

  .dialog-content {
    background: linear-gradient(135deg, #FFE5E5 0%, #FFF0F5 100%);
    border-radius: 24px;
    padding: 40px 50px;
    text-align: center;
    box-shadow: 0 10px 40px rgba(255, 105, 180, 0.3);
    border: 3px solid #FFB6C1;
    position: relative;
    animation: popIn 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
    min-width: 280px;
  }

  .dialog-content::before {
    content: '';
    position: absolute;
    top: -10px;
    left: 50%;
    transform: translateX(-50%);
    width: 0;
    height: 0;
    border-left: 15px solid transparent;
    border-right: 15px solid transparent;
    border-bottom: 15px solid #FFB6C1;
  }

  .dialog-emoji {
    font-size: 64px;
    margin-bottom: 16px;
    animation: bounce 1s infinite;
    display: inline-block;
  }

  .dialog-text {
    font-size: 20px;
    font-weight: 600;
    color: #FF69B4;
    margin-bottom: 8px;
    text-shadow: 1px 1px 2px rgba(255, 182, 193, 0.5);
  }

  .dialog-subtitle {
    font-size: 14px;
    color: #FF91A4;
    font-weight: 500;
  }

  @keyframes popIn {
    0% {
      transform: scale(0.3) rotate(-10deg);
      opacity: 0;
    }
    50% {
      transform: scale(1.1) rotate(5deg);
    }
    100% {
      transform: scale(1) rotate(0deg);
      opacity: 1;
    }
  }

  @keyframes bounce {
    0%, 100% {
      transform: translateY(0) scale(1);
    }
    50% {
      transform: translateY(-10px) scale(1.1);
    }
  }

  /* 弹窗进入/离开动画 */
  .bounce-enter-active {
    animation: fadeIn 0.3s;
  }

  .bounce-leave-active {
    animation: fadeOut 0.3s;
  }

  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  @keyframes fadeOut {
    from {
      opacity: 1;
    }
    to {
      opacity: 0;
    }
  }

  @media (max-width: 768px) {
    .dialog-content {
      padding: 30px 40px;
      min-width: 240px;
    }

    .dialog-emoji {
      font-size: 48px;
    }

    .dialog-text {
      font-size: 18px;
    }
  }
}
</style>

