const app = getApp();
let http = require('../../../utils/request')
Page({
  data: {
    StatusBar: app.globalData.StatusBar,
    CustomBar: app.globalData.CustomBar,
    TabbarBot: app.globalData.tabbar_bottom,
    TabCur: 1, scrollLeft: 0,
    postInfo: null,
    imagesList: [],
    content: ''
  },
  onLoad: function (options) {
    this.getPostInfo(options.postId)
  },
  getContent(e) {
    this.setData({ content: e.detail.value })
  },
  getPostInfo(postId) {
    http.get(`bulletin/${postId}`).then((r) => {
      let images = []
      if (r.data.images !== null && r.data.images !== '') {
        images = r.data.images.split(',')
      }
      this.setData({ imagesList: images, postInfo: r.data })
    })
  }
});
