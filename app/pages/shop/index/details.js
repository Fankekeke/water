const app = getApp();
let http = require('../../../utils/request')
Page({
    data: {
        StatusBar: app.globalData.StatusBar,
        CustomBar: app.globalData.CustomBar,
        TabbarBot: app.globalData.tabbar_bottom,
        swiperlist: [
            'https://image.weilanwl.com/img/4x3-1.jpg',
            'https://image.weilanwl.com/img/4x3-2.jpg',
            'https://image.weilanwl.com/img/4x3-3.jpg',
            'https://image.weilanwl.com/img/4x3-4.jpg',
        ],
        shopInfo: null
    },
    onLoad: function (options) {
        this.selShopDetail(options.shopCode)
    },
    selShopDetail(shopCode) {
        http.get(`shop/detail/${shopCode}`).then((r) => {
            r.data.image = r.data.images.split(',')[0]
            this.setData({
                shopInfo: r.data
            })
        })
    }
});
