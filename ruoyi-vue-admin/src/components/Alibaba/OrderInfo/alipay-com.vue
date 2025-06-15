<template>
  <a-modal
    :title="payInfo.desc"
    :visible="open"
    :confirm-loading="modalLoading"
    :afterClose="afterClose"
    :closable="false"
    style="top: 20px"
    width="400px"
    @cancel="handleCancel"
  >
    <template #footer>
      <a-button key="back" @click="handleCancel" size="large">
        <CloseOutlined />取消支付
      </a-button>
      <a-button key="submit" type="primary" :loading="modalLoading" @click="handleModalOk" size="large">
        <CheckOutlined />我已支付
      </a-button>
    </template>

    <div class="pay-info">
      <div style="font-size: 25px; margin: 20px;">
        <img style="width: 35px" src="/image/alipay-icon.jpg"/>&nbsp;支付宝扫码支付
      </div>

      <div class="qrcode-wrapper">
        <!-- 移除调试文本 -->
        <!-- <div v-if="showIframe">showIframe: true</div>
        <div v-else>showIframe: false</div> -->

        <iframe
          v-if="showIframe"
          :src="iframeSrc"
          sandbox="allow-scripts allow-forms allow-same-origin allow-popups allow-top-navigation"
          style="height: 240px; width: 200px; border: none;"
          @load="onIframeLoad"
          name="alipay_qrcode_frame"
        ></iframe>

        <div v-if="loading" class="loading-text">
          <a-spin size="large"/>
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
  </a-modal>
</template>

<script>
import { defineComponent, ref } from 'vue'
import { message, notification, Spin } from 'ant-design-vue'
import { CloseOutlined, CheckOutlined } from '@ant-design/icons-vue'
import { queryOrderStatusApi } from '@/api/audio/voiceRecognition/filetrans-upload'

export default defineComponent({
  components: {
    CloseOutlined,
    CheckOutlined,
    'a-spin': Spin
  },
  emits: ['after-pay'],
  setup(props, { emit }) {
    const iframeSrc = ref('')
    const showIframe = ref(false)
    const loading = ref(false)
    const error = ref(false)
    const payInfo = ref({})
    const open = ref(false)
    const modalLoading = ref(false)
    const orderNo = ref('')
    let queryPayInterval = null

    const handleOpen = async (info) => {
      try {
        loading.value = true
        error.value = false
        payInfo.value = info
        open.value = true
        orderNo.value = info.orderNo
        showIframe.value = false

        console.log('Received QR code data:', info);
        console.log('QR code HTML:', info.qrcode);

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

        // 准备iframe内容 - 特别注意这里的脚本标签处理
        iframeSrc.value = `data:text/html;charset=utf-8,${encodeURIComponent(info.qrcode)}`

        console.log('iframeSrc:', iframeSrc.value);

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

        queryPayResult(info.orderNo)

        // 清理临时元素
        setTimeout(() => document.body.removeChild(tempDiv), 3000)
      } catch (err) {
        console.error('支付初始化失败:', err)
        loading.value = false
        error.value = true
        message.error('支付初始化失败')
      }
    }

    const queryPayResult = (orderNo) => {
      clearInterval(queryPayInterval)
      queryPayInterval = setInterval(async () => {
        try {
          const res = await queryOrderStatusApi(orderNo)
          if (res.code === 200) {
            const status = res.data.status
            if (status === 'S') {
              clearInterval(queryPayInterval)
              notification.success({
                message: '支付成功',
                description: '订单支付成功'
              })
              open.value = false
              emit('after-pay', 'S')
            } else if (status === 'F') {
              clearInterval(queryPayInterval)
              notification.error({
                message: '支付失败',
                description: '请重新尝试支付'
              })
              emit('after-pay', 'F')
            }
          }
        } catch (err) {
          console.error('查询支付状态失败:', err)
        }
      }, 2000)
    }

    const onIframeLoad = () => {
      console.log('iframe加载完成');
      // 进一步调试信息
      const iframe = document.querySelector('iframe[name="alipay_qrcode_frame"]');
      if (iframe && iframe.contentDocument) {
        console.log('iframe content:', iframe.contentDocument.body.innerHTML);
      }
    }

    const handleModalOk = () => {
      modalLoading.value = true
      queryOrderStatusApi(orderNo.value)
        .finally(() => {
          modalLoading.value = false
        })
    }

    const handleCancel = () => {
      open.value = false
      clearInterval(queryPayInterval)
    }

    const afterClose = () => {
      clearInterval(queryPayInterval)
      showIframe.value = false
      iframeSrc.value = ''
    }

    return {
      iframeSrc,
      showIframe,
      loading,
      error,
      payInfo,
      open,
      modalLoading,
      handleOpen,
      handleModalOk,
      handleCancel,
      afterClose,
      onIframeLoad
    }
  }
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
</style>
