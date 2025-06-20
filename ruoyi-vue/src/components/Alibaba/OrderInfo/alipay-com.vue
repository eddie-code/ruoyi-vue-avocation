<template>
  <el-dialog
    :title="payInfo.desc"
    v-model="open"
    width="400px"
    :before-close="handleCancel"
    :close-on-click-modal="false"
    :show-close="false"
    custom-class="alipay-dialog"
    top="20px"
    @closed="afterClose"
  >
    <div class="pay-info">
      <div style="font-size: 25px; margin: 20px;">
        <img style="width: 35px" src="/image/alipay-icon.jpg"/>&nbsp;支付宝扫码支付
      </div>

      <div class="qrcode-wrapper">
        <iframe
          v-if="showIframe"
          :src="iframeSrc"
          sandbox="allow-scripts allow-forms allow-same-origin allow-popups allow-top-navigation"
          style="height: 240px; width: 200px; border: none;"
          @load="onIframeLoad"
          name="alipay_qrcode_frame"
        ></iframe>

        <div v-if="loading" class="loading-text">
          <el-icon class="is-loading" style="font-size: 24px;">
            <Loading />
          </el-icon>
          <div style="margin-top: 10px;">正在加载支付页面...</div>
        </div>

        <div v-if="error" class="error-text">
          支付页面加载失败，请刷新重试
        </div>
      </div>

      <div style="font-size: 16px; margin-top: 10px;">
        打开手机支付宝，扫码支付<span style="color: red">{{payInfo.amount}}</span>元
      </div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button
          @click="handleCancel"
          size="large"
          :icon="Close"
        >
          取消支付
        </el-button>
        <el-button
          type="primary"
          :loading="modalLoading"
          @click="handleModalOk"
          size="large"
          :icon="Check"
        >
          我已支付
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import { Close, Check, Loading } from '@element-plus/icons-vue'
import { queryOrderStatusApi } from '@/api/audio/voiceRecognition/filetrans-upload'

const iframeSrc = ref('')
const showIframe = ref(false)
const loading = ref(false)
const error = ref(false)
const payInfo = ref({})
const open = ref(false)
const modalLoading = ref(false)
const orderNo = ref('')
let queryPayInterval = null

const emit = defineEmits(['after-pay'])

// 清除定时任务
const clearPaymentInterval = () => {
  if (queryPayInterval) {
    clearInterval(queryPayInterval)
    queryPayInterval = null
  }
}

const handleOpen = async (info) => {
  try {
    loading.value = true
    error.value = false
    payInfo.value = info
    open.value = true
    orderNo.value = info.orderNo
    showIframe.value = false

    // 清除之前的定时任务
    clearPaymentInterval()

    // 创建临时div处理支付宝表单
    const tempDiv = document.createElement('div')
    tempDiv.style.display = 'none'
    tempDiv.innerHTML = info.qrcode
    document.body.appendChild(tempDiv)

    const form = tempDiv.querySelector('form')
    if (!form) {
      throw new Error('无效的二维码格式')
    }

    form.setAttribute('target', 'alipay_qrcode_frame')

    // 准备iframe内容
    iframeSrc.value = `data:text/html;charset=utf-8,${encodeURIComponent(info.qrcode)}`

    showIframe.value = true
    loading.value = false

    // 手动触发表单提交
    setTimeout(() => {
      const iframe = document.querySelector('iframe[name="alipay_qrcode_frame"]');
      if (iframe && iframe.contentDocument) {
        const iframeForm = iframe.contentDocument.querySelector('form');
        if (iframeForm) {
          iframeForm.submit();
        }
      }
    }, 500);

    // 启动支付状态轮询
    queryPayInterval = setInterval(async () => {
      try {
        const res = await queryOrderStatusApi(orderNo.value)
        if (res.code === 200) {
          const status = res.data.status
          if (status === 'S') {
            // 支付成功处理
            ElNotification({
              title: '支付成功',
              message: '订单支付成功',
              type: 'success',
              duration: 3000
            })
            clearPaymentInterval()
            open.value = false
            emit('after-pay', 'S')
          } else if (status === 'F') {
            // 支付失败处理
            ElNotification({
              title: '支付失败',
              message: '请重新尝试支付',
              type: 'error',
              duration: 3000
            })
            clearPaymentInterval()
            emit('after-pay', 'F')
          }
        }
      } catch (err) {
        console.error('查询支付状态失败:', err)
      }
    }, 2000)
  } catch (err) {
    console.error('支付初始化失败:', err)
    loading.value = false
    error.value = true
    ElMessage.error('支付初始化失败')
  } finally {
    // 清理临时元素
    setTimeout(() => {
      const tempDiv = document.querySelector('div[style="display: none;"]')
      if (tempDiv) {
        document.body.removeChild(tempDiv)
      }
    }, 3000)
  }
}

const onIframeLoad = () => {
  console.log('iframe加载完成');
}

const handleModalOk = () => {
  modalLoading.value = true
  queryOrderStatusApi(orderNo.value)
    .then(res => {
      if (res.code === 200) {
        const status = res.data.status
        if (status === 'S') {
          ElNotification({
            title: '支付成功',
            message: '订单支付成功',
            type: 'success',
            duration: 3000
          })
          clearPaymentInterval()
          open.value = false
          emit('after-pay', 'S')
        } else if (status === 'F') {
          ElNotification({
            title: '支付失败',
            message: '请重新尝试支付',
            type: 'error',
            duration: 3000
          })
        } else {
          ElMessage.info('订单状态：' + (status === 'P' ? '处理中' : '未知状态'))
        }
      }
    })
    .catch(err => {
      console.error('查询支付状态失败:', err)
      ElMessage.error('查询支付状态失败')
    })
    .finally(() => {
      modalLoading.value = false
    })
}

// 取消支付处理
const handleCancel = () => {
  clearPaymentInterval()
  open.value = false
}

// 窗口关闭后清理
const afterClose = () => {
  clearPaymentInterval()
  showIframe.value = false
  iframeSrc.value = ''
}

// 组件卸载时清理
onUnmounted(() => {
  clearPaymentInterval()
})

// 暴露方法给父组件
defineExpose({
  handleOpen
})
</script>

<style scoped>
.pay-info {
  text-align: center;
  margin-bottom: 20px;
}

.qrcode-wrapper {
  height: 240px;
  width: 200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 4px;
  position: relative;
}

.loading-text,
.error-text {
  position: absolute;
  width: 100%;
  text-align: center;
  color: #666;
  font-size: 14px;
}

.error-text {
  color: #ff4d4f;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.alipay-dialog .el-dialog__body {
  padding: 10px 20px;
}
</style>
