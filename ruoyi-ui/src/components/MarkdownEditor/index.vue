<template>
  <div class="markdown-editor-wrapper">
    <div class="editor-toolbar">
      <el-button-group>
        <el-button size="mini" @click="insertMarkdown('**', '**')" title="加粗">
          <strong style="font-weight: bold;">B</strong>
        </el-button>
        <el-button size="mini" @click="insertMarkdown('*', '*')" title="斜体">
          <em style="font-style: italic;">I</em>
        </el-button>
        <el-button size="mini" @click="insertMarkdown('# ', '')" title="标题">
          <i class="el-icon-edit"></i>
        </el-button>
        <el-button size="mini" @click="insertMarkdown('`', '`')" title="行内代码">
          <i class="el-icon-document"></i>
        </el-button>
        <el-button size="mini" @click="insertMarkdown('```\n', '\n```')" title="代码块">
          <i class="el-icon-document-copy"></i>
        </el-button>
        <el-button size="mini" @click="insertMarkdown('- ', '')" title="无序列表">
          <i class="el-icon-menu"></i>
        </el-button>
        <el-button size="mini" @click="insertMarkdown('1. ', '')" title="有序列表">
          <i class="el-icon-s-order"></i>
        </el-button>
        <el-button size="mini" @click="insertMarkdown('> ', '')" title="引用">
          <i class="el-icon-chat-line-round"></i>
        </el-button>
        <el-button size="mini" @click="insertMarkdown('[链接文字](', ')')" title="链接">
          <i class="el-icon-link"></i>
        </el-button>
        <el-button size="mini" @click="insertMarkdown('![图片描述](', ')')" title="图片">
          <i class="el-icon-picture"></i>
        </el-button>
        <el-button size="mini" @click="insertMarkdown('$', '$')" title="行内公式">
          <span style="font-family: 'Times New Roman', serif;">f(x)</span>
        </el-button>
        <el-button size="mini" @click="insertMarkdown('$$\n', '\n$$')" title="块级公式">
          <span style="font-family: 'Times New Roman', serif;">∫</span>
        </el-button>
      </el-button-group>
      <span class="toolbar-tip">💡 实时预览 | 支持数学公式</span>
    </div>
    <!-- 编辑和预览并排显示 -->
    <div class="editor-preview-container">
      <!-- 左侧编辑区域 -->
      <div class="editor-area">
        <div class="area-header">编辑</div>
        <el-input
          v-model="currentValue"
          type="textarea"
          :rows="rows"
          :placeholder="placeholder"
          @input="handleInput"
          class="markdown-textarea"
        />
      </div>
      <!-- 右侧预览区域 -->
      <div class="preview-area">
        <div class="area-header">预览</div>
        <div 
          class="preview-content markdown-body" 
          v-html="renderedContent"
        ></div>
      </div>
    </div>
    <div class="editor-tip" style="margin-top: 5px; color: #909399; font-size: 12px;">
      💡 提示：支持 Markdown 语法，可以使用工具栏快速插入格式，右侧实时预览效果
    </div>
  </div>
</template>

<script>
// 使用 CommonJS 版本避免 webpack 解析 ES 模块问题
const markedModule = require('marked');
// marked 4.3.0 导出的是一个对象，marked.marked 是主函数
let marked = markedModule.marked || markedModule.parse || markedModule;

// 验证 marked 是否为函数
if (typeof marked !== 'function') {
  console.error('marked 不是函数，类型:', typeof marked, '值:', marked);
  // 如果 marked 是对象，尝试获取 marked 属性
  if (marked && typeof marked.marked === 'function') {
    marked = marked.marked;
  } else if (marked && typeof marked.parse === 'function') {
    marked = marked.parse;
  }
}
import DOMPurify from 'dompurify';
// 引入 KaTeX 用于数学公式渲染
const katex = require('katex');
require('katex/dist/katex.min.css');

export default {
  name: "MarkdownEditor",
  props: {
    value: {
      type: String,
      default: ""
    },
    rows: {
      type: Number,
      default: 10
    },
    placeholder: {
      type: String,
      default: "请输入 Markdown 格式内容..."
    }
  },
  data() {
    return {
      currentValue: this.value || ""
    };
  },
  computed: {
    renderedContent() {
      if (!this.currentValue) {
        return '<div style="color: #909399; padding: 20px; text-align: center;">开始输入，实时查看预览效果</div>';
      }
      try {
        let content = this.currentValue;
        
        // 先使用 marked 渲染 Markdown
        let html = marked(content);
        
        // 在渲染后的 HTML 中直接处理数学公式
        // 处理块级公式 $$...$$（可能在 HTML 中被转义）
        html = html.replace(/\$\$([\s\S]*?)\$\$/g, (match, formula) => {
          try {
            // 移除可能的 HTML 标签和实体
            const cleanFormula = formula.replace(/<[^>]*>/g, '').replace(/&[a-z]+;/gi, '').trim();
            const rendered = katex.renderToString(cleanFormula, {
              displayMode: true,
              throwOnError: false
            });
            return `<div class="katex-block">${rendered}</div>`;
          } catch (e) {
            console.error('KaTeX 渲染失败:', e);
            return match;
          }
        });
        
        // 处理行内公式 $...$（需要避免匹配 $$ 和已处理的块级公式）
        html = html.replace(/([^$<]|^)\$([^$\n<]+?)\$([^$>]|$)/g, (match, before, formula, after) => {
          // 跳过已经被 HTML 标签包裹的内容或包含 HTML 实体的内容
          if (formula.includes('<') || formula.includes('>') || formula.includes('&')) {
            return match;
          }
          try {
            const rendered = katex.renderToString(formula.trim(), {
              displayMode: false,
              throwOnError: false
            });
            return `${before || ''}<span class="katex-inline">${rendered}</span>${after || ''}`;
          } catch (e) {
            console.error('KaTeX 渲染失败:', e);
            return match;
          }
        });
        
        // 调试：检查渲染结果
        if (process.env.NODE_ENV === 'development') {
          console.log('Markdown 输入:', this.currentValue);
          console.log('Marked 渲染结果:', html);
        }
        
        // 处理表格，添加边框样式
        html = html.replace(/<table([^>]*)>/gi, (match, attrs) => {
          return `<table${attrs} style="border-collapse: collapse; width: 100%; margin: 10px 0; border: 1px solid #dcdfe6;">`;
        });
        
        // 为表格单元格添加边框
        html = html.replace(/<th([^>]*)>/gi, (match, attrs) => {
          return `<th${attrs} style="border: 1px solid #dcdfe6; padding: 8px 12px; text-align: left; background-color: #f5f7fa; font-weight: 600;">`;
        });
        
        html = html.replace(/<td([^>]*)>/gi, (match, attrs) => {
          return `<td${attrs} style="border: 1px solid #dcdfe6; padding: 8px 12px; text-align: left;">`;
        });
        
        // 处理图片标签，添加样式
        html = html.replace(/<img\s+([^>]*?)src\s*=\s*["']([^"']+)["']([^>]*?)>/gi, (match, beforeSrc, src, afterSrc) => {
          const altMatch = match.match(/alt\s*=\s*["']([^"']*?)["']/i);
          const alt = altMatch ? altMatch[1] : '';
          return `<img src="${src}" alt="${alt}" style="max-width: 100%; border-radius: 4px; margin: 10px 0; display: block;" />`;
        });
        
        // 使用 DOMPurify 清理渲染后的 HTML
        // 允许 KaTeX 生成的 HTML 标签和属性
        const sanitized = DOMPurify.sanitize(html, {
          ALLOWED_TAGS: ['p', 'br', 'strong', 'em', 'u', 's', 'strike', 'del', 'ol', 'ul', 'li', 'img', 'a', 'span', 'div', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'blockquote', 'code', 'pre', 'table', 'thead', 'tbody', 'tr', 'th', 'td', 'hr', 'math', 'annotation', 'semantics', 'mrow', 'mi', 'mo', 'mn', 'mfrac', 'msup', 'msub', 'munderover', 'mover', 'munder', 'mtable', 'mtr', 'mtd', 'mtext', 'mspace', 'menclose', 'merror', 'mfenced', 'mpadded', 'mphantom', 'mroot', 'mstyle', 'mmultiscripts', 'mover', 'munder', 'munderover'],
          ALLOWED_ATTR: ['href', 'src', 'alt', 'title', 'class', 'style', 'target', 'data-*', 'aria-*', 'role', 'id'],
          ALLOW_DATA_ATTR: true,
          KEEP_CONTENT: true
        });
        
        // 调试：检查清理后的结果
        if (process.env.NODE_ENV === 'development') {
          console.log('DOMPurify 清理后:', sanitized);
        }
        
        return sanitized;
      } catch (error) {
        console.error('Markdown渲染失败:', error);
        return '<div style="color: #f56c6c; padding: 10px;">渲染失败，请检查 Markdown 语法</div>';
      }
    }
  },
  watch: {
    value(newVal) {
      if (newVal !== this.currentValue) {
        this.currentValue = newVal || "";
      }
    }
  },
  methods: {
    handleInput(value) {
      this.currentValue = value;
      this.$emit("input", value);
      this.$emit("change", value);
    },
    insertMarkdown(prefix, suffix) {
      const textarea = this.$el.querySelector('textarea');
      if (!textarea) return;
      
      const start = textarea.selectionStart;
      const end = textarea.selectionEnd;
      const selectedText = this.currentValue.substring(start, end);
      const newText = prefix + selectedText + suffix;
      
      this.currentValue = 
        this.currentValue.substring(0, start) + 
        newText + 
        this.currentValue.substring(end);
      
      this.$emit("input", this.currentValue);
      this.$emit("change", this.currentValue);
      
      // 恢复焦点和光标位置
      this.$nextTick(() => {
        textarea.focus();
        const newPosition = start + prefix.length + selectedText.length;
        textarea.setSelectionRange(newPosition, newPosition);
      });
    }
  }
};
</script>

<style scoped>
/* 使用深度选择器确保 v-html 内容也能应用样式 */
.markdown-editor-wrapper {
  width: 100%;
}

.editor-toolbar {
  margin-bottom: 10px;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.editor-toolbar .el-button-group {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.toolbar-tip {
  color: #909399;
  font-size: 12px;
  margin-left: 10px;
}

.editor-preview-container {
  display: flex;
  gap: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.editor-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #dcdfe6;
}

.preview-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fafafa;
}

.area-header {
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
  font-size: 13px;
  font-weight: 500;
  color: #606266;
}

.markdown-textarea {
  flex: 1;
  border: none;
}

.markdown-textarea >>> .el-textarea__inner {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  border: none;
  resize: none;
  padding: 12px;
}

.preview-content {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  min-height: 200px;
  max-height: 600px;
}

/* Markdown 预览样式 */
.preview-content.markdown-body {
  font-size: 14px;
  line-height: 1.8;
  color: #333;
}

.preview-content.markdown-body h1,
.preview-content.markdown-body h2,
.preview-content.markdown-body h3,
.preview-content.markdown-body h4,
.preview-content.markdown-body h5,
.preview-content.markdown-body h6 {
  margin-top: 20px;
  margin-bottom: 10px;
  font-weight: 600;
  line-height: 1.4;
}

.preview-content.markdown-body h1 {
  font-size: 24px;
  border-bottom: 2px solid #eaecef;
  padding-bottom: 8px;
}

.preview-content.markdown-body h2 {
  font-size: 20px;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 6px;
}

.preview-content.markdown-body h3 {
  font-size: 18px;
}

.preview-content.markdown-body h4 {
  font-size: 16px;
}

.preview-content.markdown-body p {
  margin: 10px 0;
}

.preview-content.markdown-body strong {
  font-weight: 600;
  color: #333;
}

.preview-content.markdown-body em {
  font-style: italic;
  color: #333;
}

.preview-content.markdown-body code {
  background-color: #f6f8fa;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  color: #e83e8c;
}

.preview-content.markdown-body pre {
  background-color: #f6f8fa;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 10px 0;
}

.preview-content.markdown-body pre code {
  background-color: transparent;
  padding: 0;
  color: #333;
  font-size: 13px;
}

.preview-content.markdown-body blockquote {
  border-left: 4px solid #dfe2e5;
  padding-left: 16px;
  margin: 10px 0;
  color: #6a737d;
}

.preview-content.markdown-body ul,
.preview-content.markdown-body ol {
  margin: 10px 0;
  padding-left: 30px;
}

.preview-content.markdown-body li {
  margin: 5px 0;
}

/* 表格样式 - 使用深度选择器确保 v-html 内容也能应用 */
.preview-content.markdown-body >>> table,
.preview-content.markdown-body /deep/ table,
.preview-content.markdown-body ::v-deep table {
  border-collapse: collapse !important;
  width: 100%;
  margin: 10px 0;
  border: 1px solid #dcdfe6 !important;
}

.preview-content.markdown-body >>> table th,
.preview-content.markdown-body >>> table td,
.preview-content.markdown-body /deep/ table th,
.preview-content.markdown-body /deep/ table td,
.preview-content.markdown-body ::v-deep table th,
.preview-content.markdown-body ::v-deep table td {
  border: 1px solid #dcdfe6 !important;
  padding: 8px 12px;
  text-align: left;
}

.preview-content.markdown-body >>> table th,
.preview-content.markdown-body /deep/ table th,
.preview-content.markdown-body ::v-deep table th {
  background-color: #f5f7fa;
  font-weight: 600;
  border-bottom: 2px solid #dcdfe6 !important;
}

.preview-content.markdown-body >>> table tr,
.preview-content.markdown-body /deep/ table tr,
.preview-content.markdown-body ::v-deep table tr {
  border-bottom: 1px solid #dcdfe6 !important;
}

.preview-content.markdown-body >>> table tr:last-child,
.preview-content.markdown-body /deep/ table tr:last-child,
.preview-content.markdown-body ::v-deep table tr:last-child {
  border-bottom: none !important;
}

.preview-content.markdown-body >>> table thead,
.preview-content.markdown-body /deep/ table thead,
.preview-content.markdown-body ::v-deep table thead {
  border-bottom: 2px solid #dcdfe6 !important;
}

.preview-content.markdown-body >>> table tbody tr:hover,
.preview-content.markdown-body /deep/ table tbody tr:hover,
.preview-content.markdown-body ::v-deep table tbody tr:hover {
  background-color: #fafafa;
}

.preview-content.markdown-body a {
  color: #0366d6;
  text-decoration: none;
}

.preview-content.markdown-body a:hover {
  text-decoration: underline;
}

.preview-content.markdown-body hr {
  border: none;
  border-top: 1px solid #eaecef;
  margin: 20px 0;
}

/* KaTeX 数学公式样式 */
.preview-content.markdown-body .katex-block {
  margin: 15px 0;
  text-align: center;
  overflow-x: auto;
  overflow-y: hidden;
}

.preview-content.markdown-body .katex-inline {
  display: inline-block;
  margin: 0 2px;
}

/* 确保 KaTeX 渲染的公式正确显示 */
.preview-content.markdown-body .katex {
  font-size: 1.1em;
}

.preview-content.markdown-body .katex-display {
  margin: 1em 0;
  text-align: center;
}
</style>


