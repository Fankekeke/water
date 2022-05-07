const app = getApp();
let http = require('../../../utils/request')
Page({
    data: {
        StatusBar: app.globalData.StatusBar,
        CustomBar: app.globalData.CustomBar,
        TabbarBot: app.globalData.tabbar_bottom,
        TabCur: 0,scrollLeft:0,
        SortMenu: [{id:0,name:"商品列表"},{id:1,name:"店铺列表"}],
        key: '',
        commodity: [],
        shop: []
    },
    onLoad: function (options) {
        this.setData({ key: options.key })
        this.getGoodsFuzzy()
    },
    getGoodsFuzzy() {
        http.get(`fuzzy`, { name: this.data.key}).then((r) => {
            r.product.forEach(item => {
                item.image = item.images.split(',')[0]
            })
            r.shop.forEach(item => {
                item.image = item.images.split(',')[0]
                item.content = item.content.slice(0, 20) + '...'
            })
            this.setData({
                commodity: r.product,
                shop: r.shop
            })
		})
    },
    goodsDetail(e) {
        wx.navigateTo({
			url: '/pages/shop/goods/details?code='+e.currentTarget.dataset.code+''
		});
    },
    shopDetail(e) {
		wx.navigateTo({
			url: '/pages/shop/index/index?shopCode='+e.currentTarget.dataset.code+''
		});
	},
    search() {
        this.getGoodsFuzzy()
    },
    getKeyValue(e) {
		this.setData({ key: e.detail.value })
	},
    tabSelect(e) {
        console.log(e.currentTarget.dataset.id);
        this.setData({
            TabCur: e.currentTarget.dataset.id,
            scrollLeft: (e.currentTarget.dataset.id-1)*60
        })
    }
});
