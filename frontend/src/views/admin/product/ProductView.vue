<template>
  <a-modal v-model="show" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="danger">
        关闭
      </a-button>
    </template>
    <div style="font-size: 13px" v-if="productData !== null">
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 24px">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">基础信息</span></a-col>
        <a-col :span="8"><b>商品编号：</b>
          {{ productData.code }}
        </a-col>
        <a-col :span="8"><b>商品名称：</b>
          <a-tooltip>
            <template slot="title">
              {{ productData.productName }}
            </template>
            {{ productData.productName.slice(0, 8) }} ...
          </a-tooltip>
        </a-col>
        <a-col :span="8"><b>商品类型：</b>
          {{ productData.productTypeLabel }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>规 格：</b>
          {{ productData.standard }}
        </a-col>
        <a-col :span="8"><b>商品单价：</b>
          {{ productData.price.toFixed(2) }} 元
        </a-col>
        <a-col :span="8"><b>当前库存：</b>
          {{ productData.stock }} 件
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>上架状态：</b>
          <a-tag v-if="productData.deleteFlag === 0" color="green">上架中</a-tag>
          <a-tag v-if="productData.deleteFlag === 1" color="red">已下架</a-tag>
        </a-col>
        <a-col :span="8"><b>创建时间：</b>
          {{ productData.createDate }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 24px" v-if="productData.tagIds !== null && productData.tagIds !== ''">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">商品标签</span></a-col>
        <a-col :span="24">
          <a-tag v-for="(item, index) in productData.tagIds.split(',')" :key="index">{{ item }}</a-tag>
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 24px">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">商品介绍</span></a-col>
        <a-col :span="24">
          {{ productData.content !== null ? productData.content : '暂无信息！' }}
        </a-col>
      </a-row>
      <br/>
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
        <span style="font-size: 12px">店铺信息</span>
      </a-divider>
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 24px">
        <a-col :span="8"><b>店铺名称：</b>
          {{ productData.shopName }}
        </a-col>
        <a-col :span="8"><b>负责人：</b>
          {{ productData.principal }}
        </a-col>
        <a-col :span="8"><b>联系方式：</b>
          {{ productData.phone }}
        </a-col>
      </a-row>
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 24px">
        <a-col :span="24"><b>地 址：</b>
          {{ productData.address }}
        </a-col>
      </a-row>
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
  name: 'ProductView',
  props: {
    productShow: {
      type: Boolean,
      default: false
    },
    productData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.productShow
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
      replyList: []
    }
  },
  watch: {
    productShow: function (value) {
      if (value && this.productData.images !== null && this.productData.images !== '') {
        this.imagesInit(this.productData.images)
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
