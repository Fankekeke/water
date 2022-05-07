<template>
  <a-drawer
    title="商品标签管理"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="tagVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <div>
      <a-table :columns="columns" :data-source="tagData" :pagination=false :rowKey="record => record.id">
        <template slot="tagName" slot-scope="text, record">
          <a-input v-model="record.tagName"></a-input>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a @click="removeTag(record.tagName)">删除</a>
        </template>
      </a-table>
      <a-button @click="dataAdd" type="primary" ghost size="large" style="margin-top: 10px;width: 100%">
        添加标签
      </a-button>
    </div>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>

<script>
import {mapState} from 'vuex'

const formItemLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'TagView',
  props: {
    tagVisiable: {
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '标签名称',
        dataIndex: 'tagName',
        scopedSlots: { customRender: 'tagName' }
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: {customRender: 'operation'}
      }]
    }
  },
  watch: {
    tagVisiable: function (value) {
      if (value) {
        this.getTagList()
      }
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      tagData: []
    }
  },
  methods: {
    removeTag (tagName) {
      this.tagData = this.tagData.filter(v => v.tagName !== tagName)
    },
    dataAdd () {
      this.tagData.push({tagName: ''})
    },
    getTagList () {
      this.$get('/cos/tag-info/list').then((r) => {
        this.tagData = r.data.data
      })
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
      let tagDataCopy = this.tagData.filter(v => v.tagName !== '' && v.tagName !== undefined)
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          this.stock = values
          this.stock.consumeFlag = 1
          this.$post('/cos/tag-info/batch', {
            tags: JSON.stringify(tagDataCopy)
          }).then((r) => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
