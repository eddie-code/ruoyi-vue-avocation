<template>
  <a-modal
    :title="payInfo.desc"
    :open="open"
    :confirm-loading="modalLoading"
    :afterClose="afterClose"
    :closable="false"
    style="top: 20px"
    width="400px"
  >
    <template #footer>
      <a-button key="back" @click="handleCancel" size="large"><CloseOutlined />取消支付</a-button>
      <a-button key="submit" type="primary" :loading="modalLoading" @click="handleModalOk" size="large"><CheckOutlined />我已支付</a-button>
    </template>
    <div class="pay-info">
      <div style="font-size: 25px; margin: 20px; ">
        <img style="width: 35px" src="/image/alipay-icon.jpg"/>&nbsp;支付宝扫码支付
      </div>
      <iframe name='zfb-qrcode' style="height: 240px; border: none; width: 200px; text-align: center" align="middle"></iframe>
      <div style="font-size: 16px;">打开手机支付宝，扫码支付<span style="color: red">{{payInfo.amount}}</span>元</div>
    </div>
  </a-modal>
</template>

<script>
import { defineComponent, onMounted, ref } from 'vue'
import axios from 'axios';
import {message, notification} from 'ant-design-vue';
import { queryOrderStatusApi } from '@/api/audio/voiceRecognition/filetrans-upload';

export default defineComponent({
  // name: 'the-alipay',
  emits: ['after-pay', 'afterPay'],
  setup (props, context) {
    const payInfo = ref();
    payInfo.value = {};
    const orderNo = ref();
    orderNo.value = "";
    const open = ref(false);
    const modalLoading = ref(false);

    const handleModalOk = () => {
      queryOrder(orderNo.value);
    };

    /**
     * 新增
     */
    const handleOpen = (info) => {
      payInfo.value = info;
      console.log(info)
      open.value = true;

      // 需要放到异步，才能在iframe里显示支付宝二维码，否则浏览器会弹出新tab
      setTimeout(function () {
        let divForm = document.getElementsByTagName('divform');
        if (divForm.length) {
          document.body.removeChild(divForm[0])
        }
        const div = document.createElement('divform');
        div.innerHTML = info.qrcode; // 支付宝返回的form
        document.body.appendChild(div);
        document.forms[0].setAttribute('target', 'zfb-qrcode');
        document.forms[0].submit();
      }, 100);
      queryPayResult(info.orderNo);
    };

    /**
     * 取消
     */
    const handleCancel = () => {
      open.value = false;
      clearInterval(queryPayInterval);
    };

    /**
     * 取消
     */
    const afterClose = () => {
      console.log("afterClose")
      clearInterval(queryPayInterval);
    };

    // 支付成功后查询
    let queryPayInterval;

    // 定时查询支付结果
    const queryPayResult = (_orderNo) => {
      orderNo.value = _orderNo;
      queryPayInterval = setInterval(function () {
        queryOrder(_orderNo);
      }, 2000);
    };

    // 屏蔽原来的写法, 将API接口统一写在filetrans-upload.ts
    //
    // const queryOrder = (_orderNo) => {
    //   axios.get('/nls/web/order-info/query-order-status/' + _orderNo).then((response) => {
    //     const data = response.data;
    //     if (data.success) {
    //       const status = data.content;
    //       if (status === 'S') {
    //         clearInterval(queryPayInterval);
    //         notification['success']({
    //           message: '支付宝支付提示',
    //           description: "支付成功，感谢您的使用！",
    //         });
    //         open.value = false;
    //         context.emit("after-pay", "S");
    //       } else if (status === 'F') {
    //         clearInterval(queryPayInterval);
    //         notification['error']({
    //           message: '支付宝支付失败',
    //           description: "支付失败！请重新发起支付！",
    //         });
    //         context.emit("after-pay", "F");
    //       } else {
    //         // message.warning("支付未成功！状态：" + status);
    //         console.log("支付未成功！状态：" + status);
    //       }
    //     } else {
    //       message.error(data.message);
    //     }
    //   });
    // };

    const queryOrder = async (_orderNo) => {
      try {
        const response = await queryOrderStatusApi(_orderNo);
        const data = response.data;
        if (response.code === 200) {
          const status = data.status;
          if (status === 'S') {
            clearInterval(queryPayInterval);
            notification.success({ message: '支付成功' });
            open.value = false;
            context.emit("after-pay", "S");
          } else if (status === 'F') {
            clearInterval(queryPayInterval);
            notification.error({ message: '支付失败' });
            context.emit("after-pay", "F");
          } else {
            // message.warning("支付未成功！状态：" + status);
            console.log("支付未成功！状态：" + status);
          }
        }
      } catch (error) {
        message.error('查询失败');
      }
    };

    onMounted(() => {
      console.log('the-alipay onMounted');
    });

    return {
      payInfo,
      open,
      modalLoading,
      handleModalOk,
      handleOpen,
      handleCancel,
      afterClose
    }
  }
});
</script>

<style scoped>
.pay-info {
  text-align: center;
  margin-bottom: 20px;
}
.zfb-modal {
  z-index: 1001;
}
</style>
