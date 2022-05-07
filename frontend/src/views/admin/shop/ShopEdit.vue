<template>
  <a-modal v-model="show" title="修改商铺信息" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose">
        取消
      </a-button>
      <a-button key="submit" type="primary" :loading="loading" @click="handleSubmit">
        修改
      </a-button>
    </template>
    <a-form :form="form" layout="vertical">
      <a-row :gutter="20">
        <a-col :span="8">
          <a-form-item label='店铺名称' v-bind="formItemLayout">
            <a-input v-decorator="[
            'name',
            { rules: [{ required: true, message: '请输入店铺名称!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='负责人' v-bind="formItemLayout">
            <a-input v-decorator="[
            'principal',
            { rules: [{ required: true, message: '请输入负责人!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='联系方式' v-bind="formItemLayout">
            <a-input v-decorator="[
            'phone',
            { rules: [{ required: true, message: '请输入联系方式!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='店铺地址' v-bind="formItemLayout">
            <a-input v-decorator="[
            'address',
            { rules: [{ required: true, message: '请输入店铺地址!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='店铺介绍' v-bind="formItemLayout">
            <a-textarea :rows="4" v-decorator="[
            'content'
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='店铺图片' v-bind="formItemLayout">
            <a-upload
              name="avatar"
              action="http://127.0.0.1:9527/file/fileUpload/"
              list-type="picture-card"
              :file-list="fileList"
              @preview="handlePreview"
              @change="picHandleChange"
            >
              <div v-if="fileList.length < 8">
                <a-icon type="plus" />
              </div>
            </a-upload>
            <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
              <img alt="example" style="width: 100%" :src="previewImage" />
            </a-modal>
          </a-form-item>
        </a-col>
        <a-divider orientation="left">
          <span style="font-size: 12px">资质照片</span>
        </a-divider>
        <a-col :span="24">
          <a-form-item v-bind="formItemLayout">
            <a-upload
              name="avatar"
              action="http://127.0.0.1:9527/file/fileUpload/"
              list-type="picture-card"
              :file-list="fileListQualified"
              @preview="handlePreviewQualified"
              @change="picHandleChangeQualified"
            >
              <div v-if="fileListQualified.length < 8">
                <a-icon type="plus" />
              </div>
            </a-upload>
            <a-modal :visible="previewVisibleQualified" :footer="null" @cancel="handleCancelQualified">
              <img alt="example" style="width: 100%" :src="previewImageQualified" />
            </a-modal>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
import {mapState} from 'vuex'
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}
export default {
  name: 'ShopEdit',
  props: {
    shopEditVisiable: {
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.shopEditVisiable
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      rowId: null,
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      fileList: [],
      fileListQualified: [],
      previewVisible: false,
      previewVisibleQualified: false,
      previewImage: '',
      previewImageQualified: ''
    }
  },
  methods: {
    handleCancel () {
      this.previewVisible = false
    },
    handleCancelQualified () {
      this.previewVisibleQualified = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    async handlePreviewQualified (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImageQualified = file.url || file.preview
      this.previewVisibleQualified = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    picHandleChangeQualified ({ fileList }) {
      this.fileListQualified = fileList
    },
    imagesInit (images, qualification) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileList = imageList
      }
      if (qualification !== null && qualification !== '') {
        let imageList = []
        qualification.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileListQualified = imageList
      }
    },
    setFormValues ({...shop}) {
      this.rowId = shop.id
      let fields = ['name', 'content', 'phone', 'principal', 'address']
      let obj = {}
      Object.keys(shop).forEach((key) => {
        if (key === 'images') {
          this.fileList = []
          this.imagesInit(shop['images'], shop['qualification'])
        }
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          obj[key] = shop[key]
        }
      })
      this.form.setFieldsValue(obj)
    },
    reset () {
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleSubmit () {
      let images = []
      this.fileList.forEach(image => {
        if (image.response !== undefined) {
          images.push(image.response)
        } else {
          images.push(image.name)
        }
      })
      let imagesQualified = []
      this.fileListQualified.forEach(image => {
        if (image.response !== undefined) {
          imagesQualified.push(image.response)
        } else {
          imagesQualified.push(image.name)
        }
      })
      if (imagesQualified.length !== 0 && images.length !== 0) {
        this.form.validateFields((err, values) => {
          values.id = this.rowId
          values.images = images.length > 0 ? images.join(',') : null
          values.qualification = imagesQualified.length > 0 ? imagesQualified.join(',') : null
          if (!err) {
            this.loading = true
            this.$put('/cos/shop-info', {
              ...values
            }).then((r) => {
              this.reset()
              this.$emit('success')
            }).catch(() => {
              this.loading = false
            })
          }
        })
      } else {
        this.$message.error('图片不可为空！')
      }
    }
  }
}
</script>

<style scoped>

</style>
