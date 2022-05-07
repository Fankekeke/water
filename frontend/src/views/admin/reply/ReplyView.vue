<template>
  <a-modal v-model="show" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="danger">
        关闭
      </a-button>
    </template>
    <div style="font-size: 13px" v-if="replyData !== null">
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 24px">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">评价信息</span></a-col>
        <a-col :span="8"><b>评价人：</b>
          {{ replyData.userName }}
        </a-col>
        <a-col :span="8"><b>分 数：</b>
          <a-rate v-model="replyData.score" disabled/>
        </a-col>
      </a-row>
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 24px">
        <a-col :span="24"><b>评价内容：</b>
          {{ replyData.content != null ? replyData.content : '暂无内容' }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="24"><b>所属店铺：</b>
          {{ replyData.shopName }}
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
  name: 'ReplyView',
  props: {
    replyShow: {
      type: Boolean,
      default: false
    },
    replyData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.replyShow
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
      addressData: null
    }
  },
  watch: {
    replyShow: function (value) {
      if (value) {
        this.imagesInit(this.replyData.images, this.replyData.productImages)
      }
    }
  },
  methods: {
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

</style>
