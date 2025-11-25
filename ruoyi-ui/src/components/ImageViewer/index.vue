<template>
  <transition name="viewer-fade">
    <div
      ref="el-image-viewer__wrapper"
      tabindex="-1"
      class="el-image-viewer__wrapper"
      :style="{ 'z-index': zIndex }"
    >
      <div class="el-image-viewer__mask" @click.self="handleMaskClick"></div>
      <!-- CLOSE -->
      <span class="el-image-viewer__btn el-image-viewer__close" @click="hide">
        <i class="el-icon-close"></i>
      </span>
      <!-- ARROW -->
      <template v-if="!isSingle">
        <span
          class="el-image-viewer__btn el-image-viewer__prev"
          :class="{ 'is-disabled': !infinite && isFirst }"
          @click="prev"
        >
          <i class="el-icon-arrow-left" />
        </span>
        <span
          class="el-image-viewer__btn el-image-viewer__next"
          :class="{ 'is-disabled': !infinite && isLast }"
          @click="next"
        >
          <i class="el-icon-arrow-right" />
        </span>
      </template>
      <!-- ACTIONS -->
      <div class="el-image-viewer__btn el-image-viewer__actions">
        <div class="el-image-viewer__actions__inner">
          <i class="el-icon-zoom-out" @click="handleActions('zoomOut')"></i>
          <i class="el-icon-zoom-in" @click="handleActions('zoomIn')"></i>
          <i class="el-image-viewer__actions__divider"></i>
          <i :class="mode.icon" @click="toggleMode"></i>
          <i class="el-image-viewer__actions__divider"></i>
          <i class="el-icon-refresh-left" @click="handleActions('anticlocelise')"></i>
          <i class="el-icon-refresh-right" @click="handleActions('clocelise')"></i>
        </div>
      </div>
      <!-- CANVAS -->
      <div class="el-image-viewer__canvas">
        <img
          v-for="(url, i) in urlList"
          v-if="i === index"
          ref="img"
          :key="url"
          :src="currentImg"
          :style="imgStyle"
          class="el-image-viewer__img"
          @load="handleImgLoad"
          @error="handleImgError"
          @mousedown="handleMouseDown"
        />
      </div>
    </div>
  </transition>
</template>

<script>
const Mode = {
  CONTAIN: {
    name: 'contain',
    icon: 'el-icon-full-screen'
  },
  ORIGINAL: {
    name: 'original',
    icon: 'el-icon-c-scale-to-original'
  }
}

const mousewheelEventName = 'onwheel' in document.createElement('div') ? 'wheel' : 'mousewheel'

export default {
  name: 'ElImageViewer',
  props: {
    urlList: {
      type: Array,
      default: () => []
    },
    zIndex: {
      type: Number,
      default: 2000
    },
    onClose: {
      type: Function,
      default: () => {}
    },
    initialIndex: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      index: this.initialIndex,
      isShow: true,
      infinite: true,
      loading: false,
      mode: Mode.CONTAIN,
      transform: {
        scale: 1,
        deg: 0,
        offsetX: 0,
        offsetY: 0,
        enableTransition: false
      }
    }
  },
  computed: {
    isSingle() {
      return this.urlList.length <= 1
    },
    isFirst() {
      return this.index === 0
    },
    isLast() {
      return this.index === this.urlList.length - 1
    },
    currentImg() {
      return this.urlList[this.index]
    },
    imgStyle() {
      const { scale, deg, offsetX, offsetY, enableTransition } = this.transform
      const style = {
        transform: `scale(${scale}) rotate(${deg}deg)`,
        transition: enableTransition ? 'transform .3s' : '',
        'margin-left': `${offsetX}px`,
        'margin-top': `${offsetY}px`
      }
      if (this.mode === Mode.CONTAIN) {
        style.maxWidth = style.maxHeight = '100%'
      }
      return style
    }
  },
  watch: {
    index: {
      handler: function(val) {
        this.reset()
      }
    },
    currentImg() {
      this.$nextTick(() => {
        const $img = this.$refs.img[0]
        if (!$img || !$img.complete) {
          this.loading = true
        }
      })
    }
  },
  mounted() {
    this.deviceSupportInstall()
    this.$refs['el-image-viewer__wrapper'].focus()
  },
  methods: {
    hide() {
      this.deviceSupportUninstall()
      this.onClose()
    },
    deviceSupportInstall() {
      this._keyDownHandler = e => {
        const keyCode = e.keyCode
        switch (keyCode) {
          case 27:
            this.hide()
            break
          case 32:
            this.toggleMode()
            break
          case 37:
            this.prev()
            break
          case 38:
            this.handleActions('zoomIn')
            break
          case 39:
            this.next()
            break
          case 40:
            this.handleActions('zoomOut')
            break
        }
      }
      this._mouseWheelHandler = e => {
        const delta = e.wheelDelta ? e.wheelDelta : -e.detail
        if (delta > 0) {
          this.handleActions('zoomIn', {
            zoomRate: 0.015,
            enableTransition: false
          })
        } else {
          this.handleActions('zoomOut', {
            zoomRate: 0.015,
            enableTransition: false
          })
        }
      }
      document.addEventListener('keydown', this._keyDownHandler)
      document.addEventListener(mousewheelEventName, this._mouseWheelHandler)
    },
    deviceSupportUninstall() {
      document.removeEventListener('keydown', this._keyDownHandler)
      document.removeEventListener(mousewheelEventName, this._mouseWheelHandler)
      this._keyDownHandler = null
      this._mouseWheelHandler = null
    },
    handleImgLoad() {
      this.loading = false
    },
    handleImgError(e) {
      this.loading = false
      e.target.alt = '加载失败'
    },
    handleMouseDown(e) {
      if (this.loading || e.button !== 0) return

      const { offsetX, offsetY } = this.transform
      const startX = e.pageX
      const startY = e.pageY
      this._dragHandler = ev => {
        this.transform.offsetX = offsetX + ev.pageX - startX
        this.transform.offsetY = offsetY + ev.pageY - startY
      }
      document.addEventListener('mousemove', this._dragHandler)
      document.addEventListener('mouseup', () => {
        document.removeEventListener('mousemove', this._dragHandler)
      })

      e.preventDefault()
    },
    handleMaskClick() {
      this.hide()
    },
    reset() {
      this.transform = {
        scale: 1,
        deg: 0,
        offsetX: 0,
        offsetY: 0,
        enableTransition: false
      }
    },
    toggleMode() {
      if (this.loading) return

      const modeNames = Object.keys(Mode)
      const modeValues = Object.values(Mode)
      const index = modeValues.indexOf(this.mode)
      const nextIndex = (index + 1) % modeNames.length
      this.mode = Mode[modeNames[nextIndex]]
      this.reset()
    },
    prev() {
      if (this.isFirst && !this.infinite) return
      const len = this.urlList.length
      this.index = (this.index - 1 + len) % len
    },
    next() {
      if (this.isLast && !this.infinite) return
      const len = this.urlList.length
      this.index = (this.index + 1) % len
    },
    handleActions(action, options = {}) {
      if (this.loading) return
      const { zoomRate, rotateDeg, enableTransition } = {
        zoomRate: 0.2,
        rotateDeg: 90,
        enableTransition: true,
        ...options
      }
      const { transform } = this
      switch (action) {
        case 'zoomOut':
          if (transform.scale > 0.2) {
            transform.scale = parseFloat((transform.scale - zoomRate).toFixed(3))
          }
          break
        case 'zoomIn':
          transform.scale = parseFloat((transform.scale + zoomRate).toFixed(3))
          break
        case 'clocelise':
          transform.deg += rotateDeg
          break
        case 'anticlocelise':
          transform.deg -= rotateDeg
          break
      }
      transform.enableTransition = enableTransition
    }
  }
}
</script>

<style scoped>
.el-image-viewer__wrapper {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}

.el-image-viewer__mask {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  opacity: 0.5;
  background: #000;
}

.el-image-viewer__btn {
  position: absolute;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  opacity: 0.8;
  cursor: pointer;
  box-sizing: border-box;
  user-select: none;
}

.el-image-viewer__btn.is-disabled {
  cursor: not-allowed;
  opacity: 0.3;
}

.el-image-viewer__close {
  top: 40px;
  right: 40px;
  width: 40px;
  height: 40px;
  font-size: 24px;
  color: #fff;
  background-color: #606266;
}

.el-image-viewer__canvas {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.el-image-viewer__img {
  display: block;
  cursor: grab;
  user-select: none;
}

.el-image-viewer__actions {
  left: 50%;
  bottom: 30px;
  transform: translateX(-50%);
  width: 282px;
  height: 44px;
  padding: 0 23px;
  background-color: #606266;
  border-color: #fff;
  border-radius: 22px;
}

.el-image-viewer__actions__inner {
  width: 100%;
  height: 100%;
  text-align: justify;
  cursor: default;
  font-size: 23px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-around;
}

.el-image-viewer__actions__inner i {
  cursor: pointer;
}

.el-image-viewer__prev {
  top: 50%;
  transform: translateY(-50%);
  left: 40px;
  width: 44px;
  height: 44px;
  font-size: 24px;
  color: #fff;
  background-color: #606266;
}

.el-image-viewer__next {
  top: 50%;
  transform: translateY(-50%);
  right: 40px;
  text-indent: 2px;
  width: 44px;
  height: 44px;
  font-size: 24px;
  color: #fff;
  background-color: #606266;
}

.el-image-viewer__actions__divider {
  width: 1px;
  height: 100%;
  background-color: #fff;
}

.viewer-fade-enter-active {
  animation: viewer-fade-in 0.3s;
}

.viewer-fade-leave-active {
  animation: viewer-fade-out 0.3s;
}

@keyframes viewer-fade-in {
  0% {
    transform: translate3d(0, -20px, 0);
    opacity: 0;
  }
  100% {
    transform: translate3d(0, 0, 0);
    opacity: 1;
  }
}

@keyframes viewer-fade-out {
  0% {
    transform: translate3d(0, 0, 0);
    opacity: 1;
  }
  100% {
    transform: translate3d(0, -20px, 0);
    opacity: 0;
  }
}
</style>
