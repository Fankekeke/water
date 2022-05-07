<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="商品编号"
                :labelCol="{span: 4}"
                :wrapperCol="{span: 18, offset: 2}">
                <a-input v-model="queryParams.code"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="商品名称"
                :labelCol="{span: 4}"
                :wrapperCol="{span: 18, offset: 2}">
                <a-input v-model="queryParams.name"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="商品类型"
                :labelCol="{span: 4}"
                :wrapperCol="{span: 18, offset: 2}">
                <a-select
                  :allowClear="true"
                  v-model="queryParams.productType"
                  style="width: 100%">
                  <a-select-option v-for="r in productTypeData" :key="r.keyy">{{r.valuee}}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="所属店铺"
                :labelCol="{span: 4}"
                :wrapperCol="{span: 18, offset: 2}">
                <a-input v-model="queryParams.shopName"/>
              </a-form-item>
            </a-col>
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <a-button type="primary" ghost @click="add">新增</a-button>
        <a-button @click="batchDelete">删除</a-button>
        <a-button type="primary" @click="tagView">标签管理</a-button>
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.id"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="productNameShow" slot-scope="text, record">
          <template>
            <a-tooltip>
              <template slot="title">
                {{ record.productName }}
              </template>
              <a @click="view(record)">{{ record.productName.slice(0, 8) }} ...</a>
            </a-tooltip>
          </template>
        </template>
        <template slot="contentShow" slot-scope="text, record">
          <template>
            <a-tooltip>
              <template slot="title">
                {{ record.content }}
              </template>
              {{ record.content.slice(0, 15) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="shopNameShow" slot-scope="text, record">
          <a-tooltip>
            <template slot="title">
              {{ record.shopName }}
            </template>
            {{ record.shopName.slice(0, 8) }} ...
          </a-tooltip>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon v-if="record.deleteFlag === 0" type="caret-down" @click="audit(record.id, 1)" title="下 架" style="margin-right: 10px"></a-icon>
          <a-icon v-if="record.deleteFlag === 1" type="caret-up" @click="audit(record.id, 0)" title="上 架" style="margin-right: 10px"></a-icon>
          <a-icon type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="修 改"></a-icon>
        </template>
      </a-table>
    </div>
    <product-add
      v-if="productAdd.visiable"
      @close="handleProductAddClose"
      @success="handleProductAddSuccess"
      :productAddVisiable="productAdd.visiable"
      :productTypeData="productTypeData"
      :shopData="shopData"
      :tagData="tagData">
    </product-add>
    <product-edit
      ref="productEdit"
      @close="handleProductEditClose"
      @success="handleProductEditSuccess"
      :productEditVisiable="productEdit.visiable"
      :productTypeData="productTypeData"
      :shopData="shopData"
      :tagData="tagData">
    </product-edit>
    <tag-view
      @close="handleTagClose"
      @success="handleTagSuccess"
      :tagVisiable="tagVisiable">
    </tag-view>
    <product-view
      @close="handleProductViewClose"
      :productShow="productView.visiable"
      :productData="productView.data">
    </product-view>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import ProductAdd from './ProductAdd'
import ProductEdit from './ProductEdit'
import ProductView from './ProductView'
import TagView from './TagView'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'product',
  components: {ProductAdd, ProductEdit, ProductView, TagView, RangeDate},
  data () {
    return {
      advanced: false,
      tagVisiable: false,
      productAdd: {
        visiable: false
      },
      productEdit: {
        visiable: false
      },
      productView: {
        visiable: false,
        data: null
      },
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      productTypeData: [],
      shopData: [],
      tagData: []
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '商品编号',
        dataIndex: 'code'
      }, {
        title: '商品名称',
        dataIndex: 'productName',
        scopedSlots: { customRender: 'productNameShow' }
      }, {
        title: '商品状态',
        dataIndex: 'deleteFlag',
        customRender: (text, row, index) => {
          switch (text) {
            case 0:
              return <a-tag color="#87d068">上架中</a-tag>
            case 1:
              return <a-tag color="#f50">下架中</a-tag>
            default:
              return '- -'
          }
        }
      }, {
        title: '商品类型',
        dataIndex: 'productTypeLabel'
      }, {
        title: '商品单价',
        dataIndex: 'price',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text + '元'
          } else {
            return '- -'
          }
        }
      }, {
        title: '当前库存',
        dataIndex: 'stock',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text + '件'
          } else {
            return '- -'
          }
        }
      }, {
        title: '介绍',
        dataIndex: 'content',
        scopedSlots: { customRender: 'contentShow' }
      }, {
        title: '所属店铺',
        dataIndex: 'shopName',
        scopedSlots: { customRender: 'shopNameShow' }
      }, {
        title: '商品照片',
        dataIndex: 'images',
        customRender: (text, record, index) => {
          if (!record.images) return <a-avatar shape="square" icon="user" />
          return <a-popover>
            <template slot="content">
              <a-avatar shape="square" size={132} icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.images.split(',')[0] } />
            </template>
            <a-avatar shape="square" icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.images.split(',')[0] } />
          </a-popover>
        }
      }, {
        title: '货物规格',
        dataIndex: 'standard',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        }
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: {customRender: 'operation'}
      }]
    }
  },
  mounted () {
    this.fetch()
    this.getProductTypeDict()
    this.getShopList()
    this.getTagList()
  },
  methods: {
    audit (id, flag) {
      this.$put('/cos/product-info/onPut', { id, flag }).then((r) => {
        if (r.data) {
          this.$message.success('状态成功更改😀')
          this.fetch()
        }
      })
    },
    getTagList () {
      this.$get('/cos/tag-info/list').then((r) => {
        this.tagData = r.data.data
      })
    },
    tagView () {
      this.tagVisiable = true
    },
    getShopList () {
      this.$get('/cos/shop-info/list').then((r) => {
        this.shopData = r.data.data
      })
    },
    getProductTypeDict () {
      this.$get('/dict', {tableName: 'product_info', fieldName: 'product_type'}).then((r) => {
        this.productTypeData = r.data.rows
      })
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    add () {
      this.productAdd.visiable = true
    },
    handleProductAddClose () {
      this.productAdd.visiable = false
    },
    handleProductAddSuccess () {
      this.productAdd.visiable = false
      this.$message.success('新增商品成功')
      this.search()
    },
    edit (record) {
      this.$refs.productEdit.setFormValues(record)
      this.productEdit.visiable = true
    },
    handleProductEditClose () {
      this.productEdit.visiable = false
    },
    view (row) {
      this.productView.data = row
      this.productView.visiable = true
    },
    handleTagClose () {
      this.tagVisiable = false
    },
    handleProductViewClose () {
      this.productView.visiable = false
    },
    handleProductEditSuccess () {
      this.productEdit.visiable = false
      this.$message.success('修改商品成功')
      this.search()
    },
    handleTagSuccess () {
      this.tagVisiable = false
      this.getTagList()
      this.$message.success('修改成功')
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/cos/product-info/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }
      if (params.productType === undefined) {
        delete params.productType
      }
      this.$get('/cos/product-info/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
@import "../../../../static/less/Common";
</style>
