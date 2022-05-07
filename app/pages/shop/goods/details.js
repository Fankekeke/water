const app = getApp();
let http = require('../../../utils/request')
Page({
    data: {
        StatusBar: app.globalData.StatusBar + 6,
        TabbarBot: app.globalData.tabbar_bottom,
        swiperlist: [],
        goods: null,
        productCode: null,
        tagIdList: [],
        evaluation: []
    },
    onLoad: function (options) {
        this.setData({ productCode: options.code })
        this.getGoodsDetail(options.code)
    },
    getGoodsDetail(productCode) {
        let tagIdList = []
        http.get(`product/detail/${productCode}`).then((r) => {
            if (r.productDetail.tagIds !== null && r.productDetail.tagIds !== '') {
                tagIdList = r.productDetail.tagIds.split(',')
            }
            this.setData({
                swiperlist: r.productDetail.images.split(','),
                goods: r.productDetail,
                tagIdList: tagIdList,
                evaluation: r.productEvaluation
            })
        })
    },
    shopDetail() {
        wx.navigateTo({
            url: '/pages/shop/index/index?shopCode=' + this.data.goods.shopCode + ''
        });
    },
    cartView() {
        wx.switchTab({
            url: '/pages/scar/index/index'
        })
    },
    addCart() {
        if (this.data.goods.deleteFlag == 1) {
            wx.showToast({
                title: '该商品已下架',
                icon: 'none',
                duration: 2000
            })
        } else {
            wx.getStorage({
                key: 'userInfo',
                success: (res) => {
                    let data = { userCode: res.data.code, productCode: this.data.goods.code, quantity: 1 }
                    http.post('cart', data).then((r) => {
                        if (r.data) {
                            wx.showToast({
                                title: '添加购物车成功',
                                icon: 'success',
                                duration: 2000
                            })
                        } else {
                            wx.showToast({
                                title: '购物车内已存在',
                                icon: 'none',
                                duration: 2000
                            })
                        }
                    })
                },
                fail: res => {
                    wx.showToast({
                        title: '请先进行登录',
                        icon: 'none',
                        duration: 2000
                    })
                }
            })
        }
    },
    buyGoods() {
        if (this.data.goods.onPut == 0) {
            wx.showToast({
                title: '该商品已下架',
                icon: 'none',
                duration: 2000
            })
        } else {
            wx.getStorage({
                key: 'userInfo',
                success: (res) => {
                    http.get('selDefaultAddress', { userId: res.data.id }).then((r) => {
                        if (r.data == null) {
                            wx.showToast({
                                title: '请先设置默认收货地址',
                                icon: 'none',
                                duration: 1000
                            })
                        } else {
                            wx.navigateTo({
                                url: '/pages/scar/order/index?type=2&commodityId=' + this.data.commoditId + ''
                            })
                        }
                    })
                },
                fail: res => {
                    wx.showToast({
                        title: '请先进行登录',
                        icon: 'none',
                        duration: 2000
                    })
                }
            })

        }
    }
});
