const app = getApp();
let http = require('../../../utils/request')
Page({
    data: {
        StatusBar: app.globalData.StatusBar,
        CustomBar: app.globalData.CustomBar,
        TabbarBot: app.globalData.tabbar_bottom,
        orderCode: null,
        orderInfo: null,
        orderItem: [],
        allPrice: 0,
        address: null
    },
    onLoad: function (options) {
        this.setData({ orderCode: options.orderCode })
        this.gerOrderDetail(options.orderCode)
    },
    gerOrderDetail(orderCode) {
        http.get(`order/detail/${orderCode}`).then((r) => {
            let allPricePay = 0 
            r.orderItem.forEach(item => {
                let allPrice = 0
                let amount = 0
                item.productList.forEach(iitem => {
                    iitem.image = iitem.images.split(',')[0]
                    allPrice += (Number(iitem.price) * Number(iitem.quantity))
                    amount += Number(iitem.quantity)
                })
                item.allPrice = allPrice.toFixed(2)
                item.amount = amount
                allPricePay += Number(item.allPrice)
            });
            this.setData({
                allPrice: allPricePay.toFixed(2),
                orderInfo: r.orderInfo,
                orderItem: r.orderItem,
                address: r.address
            })
        })
    }
});
