<template>
  <a-modal v-model="show" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="danger">
        关闭
      </a-button>
    </template>
    <div style="font-size: 13px" v-if="evaluationData !== null">
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 24px">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">评价信息</span></a-col>
        <a-col :span="8"><b>评价人：</b>
          {{ evaluationData.userName }}
        </a-col>
        <a-col :span="8"><b>分 数：</b>
          <a-rate v-model="evaluationData.score" disabled/>
        </a-col>
      </a-row>
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 24px">
        <a-col :span="24"><b>评价内容：</b>
          {{ evaluationData.content != null ? evaluationData.content : '暂无内容' }}
        </a-col>
      </a-row>
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 24px">
        <a-col :span="24">
          <a-upload
            name="avatar"
            action="http://127.0.0.1:9527/file/fileUpload/"
            list-type="picture-card"
            disabled
            :file-list="fileListEva"
            @preview="handlePreviewEva"
            @change="picHandleChangeEva"
          >
          </a-upload>
          <a-modal :visible="previewVisibleEva" :footer="null" @cancel="handleCancelEva">
            <img alt="example" style="width: 100%" :src="previewImageEva" />
          </a-modal>
        </a-col>
      </a-row>
      <br/>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 24px">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">订单信息</span></a-col>
        <a-col :span="8"><b>订单编号：</b>
          {{ evaluationData.code }}
        </a-col>
        <a-col :span="8"><b>订单总额：</b>
          {{ evaluationData.allPrice }} 元
        </a-col>
        <a-col :span="8"><b>购买数量：</b>
          {{ evaluationData.amount }} 件
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>商品单价：</b>
          {{ evaluationData.unitPrice }} 元
        </a-col>
        <a-col :span="8"><b>商品名称：</b>
          <a-tooltip>
            <template slot="title">
              {{ evaluationData.productName }}
            </template>
            {{ evaluationData.productName.slice(0, 8) }} ...
          </a-tooltip>
        </a-col>
        <a-col :span="8"><b>商品类型：</b>
          {{ evaluationData.productTypeLabel }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="24"><b>所属店铺：</b>
          {{ evaluationData.shopName }}
        </a-col>
      </a-row>
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 24px">
        <a-col :span="24">
          <a-upload
            name="avatar"
            action="http://127.0.0.1:9527/file/fileUpload/"
            list-type="picture-card"
            disabled
            :file-list="fileList"
            @preview="handlePreview"
            @change="picHandleChange"
          >
          </a-upload>
          <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
            <img alt="example" style="width: 100%" :src="previewImage" />
          </a-modal>
        </a-col>
      </a-row>
      <a-divider orientation="left">
        <span style="font-size: 12px">评价回复信息</span>
      </a-divider>
      <div style="padding-left: 24px;padding-right: 24px;">
        <a-skeleton active v-if="replyDataLoading"/>
        <a-list item-layout="horizontal" :pagination="pagination" :data-source="replyData" v-if="!replyDataLoading">
          <a-list-item slot="renderItem" slot-scope="item, index">
            <a-list-item-meta
              :description="item.content">
              <a slot="title">{{ item.userName }} <span style="font-size: 12px;color: #aaaaaa;font-weight: 300;"> - {{ item.createDate }}</span></a>
              <a-avatar
                slot="avatar"
                :src="item.avatar"
              />
            </a-list-item-meta>
          </a-list-item>
        </a-list>
        <br/>
      </div>
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
  name: 'evaluationView',
  props: {
    evaluationShow: {
      type: Boolean,
      default: false
    },
    evaluationData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.evaluationShow
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      pagination: {
        pageSize: 5
      },
      loading: false,
      fileList: [],
      fileListEva: [],
      previewVisible: false,
      previewVisibleEva: false,
      previewImage: '',
      previewImageEva: '',
      replyData: [],
      replyDataLoading: false
    }
  },
  watch: {
    evaluationShow: function (value) {
      if (value) {
        this.replyList(this.evaluationData.code)
        this.imagesInit(this.evaluationData.images, this.evaluationData.productImages)
      }
    }
  },
  methods: {
    replyList (code) {
      this.replyDataLoading = true
      this.$get(`/cos/reply-info/list/${code}`).then((r) => {
        this.replyData = r.data.data
        setTimeout(() => {
          this.replyDataLoading = false
        }, 500)
      })
    },
    handleCancel () {
      this.previewVisible = false
    },
    handleCancelEva () {
      this.previewVisibleEva = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    async handlePreviewEva (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImageEva = file.url || file.preview
      this.previewVisibleEva = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    picHandleChangeEva ({ fileList }) {
      this.fileListEva = fileList
    },
    imagesInit (images, productImages) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileListEva = imageList
      }
      if (productImages !== null && productImages !== '') {
        let imageList = []
        productImages.split(',').forEach((image, index) => {
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
