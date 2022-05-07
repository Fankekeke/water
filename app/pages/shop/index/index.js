const app = getApp();
let http = require('../../../utils/request')
Page({
    data: {
        StatusBar: app.globalData.StatusBar + 6,
        TabbarBot: app.globalData.tabbar_bottom,
        TabCur: 0,scrollLeft:0,
        SortMenu: [{id:0,name:"综合"},{id:1,name:"销量"},{id:2,name:"新品"},{id:3,name:"价格"}],
        ShopList: [],
        shopCode: null,
        shopInfo: null,
        key: ''
    },
    onLoad: function (options) {
        this.setData({
            shopCode: options.shopCode
        })
        this.selShopDetail(options.shopCode)
        this.commoditSort(options.shopCode, 0)
    },
    selShopDetail(shopCode) {
        http.get(`shop/detail/${shopCode}`).then((r) => {
            this.setData({
                shopInfo: r.data
            })
        })
    },
    commoditSort(shopCode, sort) {
        http.get('product/sort', {shopCode, sort}).then((r) => {
            let ShopList = []
			r.data.forEach(item => {
                ShopList.push({ index: item.code, image: item.images.split(',')[0], title: item.productName, price: item.price, sales: item.orderNum })
			});
            this.setData({
                ShopList
            })
        })
    },
    tabSelect(e) {
        this.commoditSort(this.data.shopCode, e.currentTarget.dataset.id)
        this.setData({
            TabCur: e.currentTarget.dataset.id,
            scrollLeft: (e.currentTarget.dataset.id-1)*60
        })
    },
    btnback: function () {
        wx.navigateBack();
    },
    getKeyValue(e) {
		this.setData({ key: e.detail.value })
	},
    search: function () {
        wx.navigateTo({
            url: '/pages/shop/search/index?key='+this.data.key+'&shopCode='+this.data.shopCode+''
        });
    }
});
