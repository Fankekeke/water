<template>
  <a-modal v-model="show" title="添加商品信息" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose">
        取消
      </a-button>
      <a-button key="submit" type="primary" :loading="loading" @click="handleSubmit">
        提交
      </a-button>
    </template>
    <a-form :form="form" layout="vertical">
      <a-row :gutter="20">
        <a-col :span="8">
          <a-form-item label='商品名称' v-bind="formItemLayout">
            <a-input v-decorator="[
            'productName',
            { rules: [{ required: true, message: '请输入商品名称!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='商品类型' v-bind="formItemLayout">
            <a-select
              v-decorator="[
              'productType',
              { rules: [{ required: true, message: '请输入商品类型!' }] }
              ]"
              style="width: 100%">
              <a-select-option v-for="r in productTypeData" :key="r.keyy">{{r.valuee}}</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='规 格' v-bind="formItemLayout">
            <a-input v-decorator="[
            'standard',
            { rules: [{ required: true, message: '请输入规格!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='商品标签' v-bind="formItemLayout">
            <a-select
              mode="multiple"
              v-model="tags"
              style="width: 100%">
              <a-select-option v-for="r in tagData" :key="r.tagName">{{r.tagName}}</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='商品介绍' v-bind="formItemLayout">
            <a-textarea :rows="4" v-decorator="[
            'content'
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='商品图片' v-bind="formItemLayout">
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
          <span style="font-size: 12px">库存与价格</span>
        </a-divider>
        <a-col :span="8">
          <a-form-item label='当前库存' v-bind="formItemLayout">
            <a-input-number v-decorator="[
            'stock',
            { rules: [{ required: true, message: '请输入库存数量!' }] }
            ]" :min="1" :max="999999" style="width: 100%" />
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='售卖单价（元）' v-bind="formItemLayout">
            <a-input-number v-decorator="[
            'price',
            { rules: [{ required: true, message: '请输入售卖单价!' }] }
            ]" :min="1" :max="999999" style="width: 100%" />
          </a-form-item>
        </a-col>
        <a-divider orientation="left">
          <span style="font-size: 12px">所属店铺</span>
        </a-divider>
        <a-col :span="8">
          <a-form-item v-bind="formItemLayout">
            <a-select
              v-decorator="[
              'shopCode',
              { rules: [{ required: true, message: '请输入所属店铺!' }] }
              ]"
              style="width: 100%">
              <a-select-option v-for="r in shopData" :key="r.code">{{r.name}}</a-select-option>
            </a-select>
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
  name: 'productAdd',
  props: {
    productAddVisiable: {
      default: false
    },
    productTypeData: {
      type: Array
    },
    shopData: {
      type: Array
    },
    tagData: {
      type: Array
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.productAddVisiable
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      tags: []
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
        images.push(image.response)
      })
      if (images.length !== 0) {
        this.form.validateFields((err, values) => {
          values.images = images.length > 0 ? images.join(',') : null
          values.tagIds = this.tags.length > 0 ? this.tags.join(',') : null
          if (!err) {
            values.publisher = this.currentUser.userId
            this.loading = true
            this.$post('/cos/product-info', {
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
