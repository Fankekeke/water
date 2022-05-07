const app = getApp();
let http = require('../../../utils/request')
Page({
    data: {
        StatusBar: app.globalData.StatusBar,
        CustomBar: app.globalData.CustomBar,
        TabbarBot: app.globalData.tabbar_bottom,
        orderType: 0,
        cartIds: [],
        addressInfo: null,
        addressList: [],
        productCode: null,
        orderList: [],
        allPrice: 0,
        userInfo: null,
        modalName: '',
        orderInfo: null,
        orderItem: []
    },
    onLoad: function (options) {
        wx.getStorage({
            key: 'userInfo',
            success: (res) => {
                this.setData({
                    orderType: options.type,
                    userInfo: res.data
                })
                if (options.type == 1) {
                    this.setData({
                        cartIds: options.cartIds.split(',')
                    })
                    this.getOrderList(this.data.cartIds)
                } else if (options.type == 2) {
                    this.getOrderByCode(options.orderCode)
                } else {
                    this.setData({
                        productCode: options.productCode
                    })
                    this.getGoodsDetail(this.data.productCode)
                }
                this.getUserAddress(res.data.code)
            },
            fail: res => {
                wx.showToast({
                    title: '请先进行登录',
                    icon: 'none',
                    duration: 2000
                })
            }
        })
    },
    showModal(e) {
        this.setData({
            modalName: e.currentTarget.dataset.target
        })
    },
    hideModal(e) {
        this.setData({
            modalName: null
        })
    },
    addressChange(e) {
        let index = e.detail.value
        this.data.addressList.forEach(item => {
            if (item.id == index) {
                this.setData({
                    addressInfo: item
                })
            }
        })
        this.hideModal()
    },
    getUserAddress(userCode) {
        http.get(`address/${userCode}`).then((r) => {
            r.data.forEach(item => {
                if (item.defaultAddress === 1) {
                    this.setData({ addressInfo: item })
                }
            })
            if (this.data.addressInfo === null) {
                this.setData({ addressInfo: r.data[0] })
            }
            this.setData({ addressList: r.data })
        })
    },
    getGoodsDetail(commodityId) {
        http.get('goodsDetail', { commodityId }).then((r) => {
            let order = r
            let allPrice = order.price
            order.image = order.images.split(',')[0]
            this.setData({ orderList: [order], allPrice: allPrice.toFixed(2) })
        })
    },
    getOrderByCode(orderCode) {
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
            this.setData({ orderList: r.data, allPrice: allPricePay.toFixed(2) })
            this.setData({
                orderInfo: r.orderInfo,
                orderItem: r.orderItem
            })
        })
    },
    /** 禁用 */
    getOrderList(cartIds) {
        http.get(`order/settlement/${cartIds.join(',')}`).then((r) => {
            let allPrice = 0 
            r.orderItem.forEach(item => {
                let allPrice = 0
                item.productList.forEach(iitem => {
                    iitem.image = iitem.images.split(',')[0]
                    allPrice += (Number(iitem.price) * Number(iitem.quantity))
                })
                item.allPrice = allPrice.toFixed(2)
            });
            this.setData({ orderList: r.data, allPrice: allPrice.toFixed(2) })
            this.setData({
                orderInfo: r.orderInfo,
                orderItem: r.orderItem
            })
        })
    },
    submit() {
        wx.showLoading({
            title: '正在模拟支付',
        })
        setTimeout(() => {
            if (this.data.orderType == 2) {
                let data = { ids: this.data.orderIds.join(',') }
                http.get('goodsCartComplete', data).then((r) => {
                    wx.showToast({
                        title: '支付成功',
                        icon: 'success',
                        duration: 1000
                    })
                    setTimeout(() => {
                        wx.navigateBack({ changed: true });
                    }, 1000)
                })
            } else {
                
            }
            wx.hideLoading()
        }, 1000)
    }
});
