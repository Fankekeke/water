const app = getApp();
let http = require('../../../utils/request')
Page({
  data: {
    StatusBar: app.globalData.StatusBar,
    CustomBar: app.globalData.CustomBar,
    typeList: [],
    goodsList: [],
    typeData: [],
    activeKey: 0
  },
  onShow() {
    this.selProductType()
  },
  onChange(event) {
    let goodsList = this.data.typeData[event.detail].products
    goodsList.forEach(item => {
      item.image = item.images.split(',')[0]
      if (item.tagIds !== null && item.tagIds !== '') {
        item.tagArray = item.tagIds.split(',')
      }
    })
    this.setData({
      activeKey: event.detail,
      goodsList
    })
  },
  goodsDetail(e) {
    wx.navigateTo({
      url: '/pages/shop/goods/details?code=' + e.currentTarget.dataset.code + ''
    })
  },
  selProductType() {
    http.get('product/type').then((r) => {
      let typeList = []
      r.data.forEach(item => {
        typeList.push(item.name)
      })
      let goodsList = r.data[0].products
      goodsList.forEach(item => {
        item.image = item.images.split(',')[0]
        if (item.tagIds !== null && item.tagIds !== '') {
          item.tagArray = item.tagIds.split(',')
        }
      })
      this.setData({
        activeKey: 0,
        typeList,
        typeData: r.data,
        goodsList
      })
    })
  }
});
