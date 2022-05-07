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
        shopCode: null,
        goodsList: []
    },
    onLoad: function (options) {
        this.setData({
            key: options.key,
            shopCode: options.shopCode
        })
        this.search()
    },
    getKeyValue(e) {
		this.setData({ key: e.detail.value })
	},
    goodsDetail(e) {
        wx.navigateTo({
			url: '/pages/shop/goods/details?code='+e.currentTarget.dataset.code+''
		});
    },
    search() {
        http.get('product/sort', {shopCode: this.data.shopCode, sort: 0, key: this.data.key}).then((r) => {
            let goodsList = []
			r.data.forEach(item => {
                item.image = item.images.split(',')[0]
			});
            this.setData({
                goodsList: r.data
            })
        })
    },
    tabSelect(e) {
        console.log(e.currentTarget.dataset.id);
        this.setData({
            TabCur: e.currentTarget.dataset.id,
            scrollLeft: (e.currentTarget.dataset.id-1)*60
        })
    }
});
