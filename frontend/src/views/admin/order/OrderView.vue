<template>
  <a-modal v-model="show" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="danger">
        关闭
      </a-button>
    </template>
    <div style="font-size: 13px" v-if="orderData !== null">
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 24px">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">订单信息</span></a-col>
        <a-col :span="8"><b>订单编号：</b>
          {{ orderData.code }}
        </a-col>
        <a-col :span="8"><b>订单总额：</b>
          {{ orderData.allPrice }} 元
        </a-col>
        <a-col :span="8"><b>订单状态：</b>
          <a-tag v-if="orderData.orderStatus === 1">待付款</a-tag>
          <a-tag v-if="orderData.orderStatus === 2" color="blue">待发货</a-tag>
          <a-tag v-if="orderData.orderStatus === 3" color="blue">待收货</a-tag>
          <a-tag v-if="orderData.orderStatus === 4" color="green">已完成</a-tag>
          <a-tag v-if="orderData.orderStatus === 5" color="red">已取消</a-tag>
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>下单时间：</b>
          {{ orderData.placeOrderDate }}
        </a-col>
        <a-col :span="8"><b>用户编号：</b>
          {{ orderData.userCode }}
        </a-col>
        <a-col :span="8"><b>用户名称：</b>
          {{ orderData.userName }}
        </a-col>
      </a-row>
      <br/>
      <a-divider orientation="left">
        <span style="font-size: 12px">收货信息</span>
      </a-divider>
      <div>
        <a-skeleton active v-if="addressData == null"/>
        <a-row style="padding-left: 24px;padding-right: 24px;" v-if="addressData !== null">
          <a-col :span="8"><b>收货人姓名：</b>
            {{ addressData.takeUserName }}
          </a-col>
          <a-col :span="8"><b>收货人电话：</b>
            {{ addressData.takeUserPhone }}
          </a-col>
          <a-col :span="8"><b>省份：</b>
            {{ addressData.province }}
          </a-col>
        </a-row>
        <br/>
        <a-row style="padding-left: 24px;padding-right: 24px;" v-if="addressData !== null">
          <a-col :span="8"><b>市区：</b>
            {{ addressData.city }}
          </a-col>
          <a-col :span="8"><b>区域：</b>
            {{ addressData.area }}
          </a-col>
        </a-row>
        <br/>
        <a-row style="padding-left: 24px;padding-right: 24px;" v-if="addressData !== null">
          <a-col :span="24"><b>详细地址：</b>
            {{ addressData.address }}
          </a-col>
        </a-row>
      </div>
      <a-divider orientation="left">
        <span style="font-size: 12px">购买商品</span>
      </a-divider>
      <a-card hoverable v-for="(item, index) in productList" :key="index" style="margin-bottom: 10px">
        <a-card-meta :title="item.productName + ' x' + item.amount" :description="item.shopName">
          <a-avatar
            :size="100"
            slot="avatar"
            :src="'http://127.0.0.1:9527/imagesWeb/' + item.images.split(',')[0]"
          />
        </a-card-meta>
        <div style="margin-top: 15px">
          <a-row :gutter="15">
            <a-col :span="20">
              <a-icon type="paper-clip" style="font-size: 16px"/>
              <span>{{ item.unitPrice }} * {{ item.amount }}件</span>
            </a-col>
            <a-col :span="4">
              总价：<span style="color: #f5222d">{{ (item.unitPrice * item.amount).toFixed(2) }} 元</span>
            </a-col>
          </a-row>
        </div>
      </a-card>
      <a-divider orientation="left" v-if="orderData.orderStatus > 1">
        <span style="font-size: 12px">提交发货</span>
      </a-divider>
      <a-row style="padding-left: 24px;padding-right: 24px;" v-if="orderData.orderStatus > 1">
        <a-col :span="16">
          <b>快递单号：</b>
          <a-input-search placeholder="请填写快递单号" v-model="mailingNumber" enter-button="修改" @search="trackingNumberCommit"  style="margin-top: 10px"/>
        </a-col>
      </a-row>
      <br/>
    </div>
  </a-modal>
</template>

<script>
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
export default {
  name: 'OrderView',
  props: {
    orderShow: {
      type: Boolean,
      default: false
    },
    orderData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.orderShow
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      addressData: null,
      productList: [],
      trackingNumber: null,
      mailingNumber: ''
    }
  },
  watch: {
    orderShow: function (value) {
      if (value) {
        this.mailingNumber = this.orderData.mailingNumber
        this.getAddress(this.orderData.addressCode)
        this.getProduct(this.orderData.code)
      }
    }
  },
  methods: {
    trackingNumberCommit () {
      if (this.mailingNumber === '') {
        this.$message.error('快递单号不能为空！')
      } else {
        this.orderData.mailingNumber = this.mailingNumber
        this.orderData.orderStatus = 3
        this.$put('/cos/order-info/order/trackingNumber', this.orderData).then((r) => {
          this.$message.success('快递单号修改成功！')
        })
      }
    },
    getAddress (code) {
      this.$get(`/cos/address-info/info/${code}`).then((r) => {
        setTimeout(() => {
          this.addressData = r.data.data
        }, 200)
      })
    },
    getProduct (code) {
      this.$get(`/cos/order-item/${code}`).then((r) => {
        setTimeout(() => {
          this.productList = r.data.data
        }, 200)
      })
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    imagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileList = imageList
      }
    },
    onClose () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>
  /deep/ .ant-card-meta-title {
    font-size: 13px;
  }
</style>
