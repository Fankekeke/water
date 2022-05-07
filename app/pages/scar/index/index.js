const app = getApp();
let http = require('../../../utils/request')
Page({
    data: {
        StatusBar: app.globalData.StatusBar,
        CustomBar: app.globalData.CustomBar,
        orderList: [],
        userInfo: null,
        allPrice: 0,
        allChecked: false
    },
    onShow() {
        this.isLogin()
    },
    isLogin() {
        wx.getStorage({
            key: 'userInfo',
            success: (res) => {
                this.setData({
                    userInfo: res.data
                })
                this.selGoodsCart(res.data.code)
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
    onBlur(event) {
        let quantity = event.detail.value
        let row = event.currentTarget.dataset.row
        let index = event.currentTarget.dataset.index
        if (quantity > row.stock) {
            row.quantity = row.stock
        } else if (quantity <= 0) {
            row.quantity = Number(1)
        } else {
            row.quantity = quantity
        }
        row.itemPrice = (row.quantity * row.price).toFixed(2)
        let newRow = 'orderList[' + index + ']'
        this.setData({
            [newRow]: row
        })
    },
    deleteCart(e) {
        http.del(`cart/${e.currentTarget.dataset.index}`).then((r) => {
            this.setData({
                allPrice: 0,
                allChecked: false
            })
            this.selGoodsCart(this.data.userInfo.code)
        })
    },
    removeGoods() {
        let cartIds = []
        this.data.orderList.forEach(item => {
            if (item.checked) {
                cartIds.push(item.id)
            }
        })
        if (cartIds.length === 0) {
            wx.showToast({
                title: '请选择要删除的商品',
                icon: 'none',
                duration: 2000
            })
            return
        }
        http.del(`cart/${cartIds.join(',')}`).then((r) => {
            this.setData({
                allPrice: 0,
                allChecked: false
            })
            this.selGoodsCart(this.data.userInfo.code)
        })
    },
    priceCheck() {
        let allPrice = 0
        this.data.orderList.forEach(item => {
            if (item.checked) {
                allPrice += Number(item.itemPrice)
            }
        })
        this.setData({
            allPrice: allPrice.toFixed(2)
        })
    },
    quantityReduce(e) {
        let row = e.currentTarget.dataset.row
        let index = e.currentTarget.dataset.index
        if (Number(row.quantity) - 1 === 0) {
            return
        }
        row.quantity = Number(row.quantity) - 1
        row.itemPrice = (row.quantity * row.price).toFixed(2)
        let newRow = 'orderList[' + index + ']'
        this.setData({
            [newRow]: row
        })
        this.priceCheck()
    },
    quantityAdd(e) {
        let row = e.currentTarget.dataset.row
        let index = e.currentTarget.dataset.index
        if (Number(row.quantity) + 1 > row.stock) {
            wx.showToast({
                title: '库存已到上限',
                icon: 'none',
                duration: 2000
            })
            return
        }
        row.quantity = Number(row.quantity) + 1
        row.itemPrice = (row.quantity * row.price).toFixed(2)
        let newRow = 'orderList[' + index + ']'
        this.setData({
            [newRow]: row
        })
        this.priceCheck()
    },
    timeFormat(time) {
        var nowTime = new Date();
        var day = nowTime.getDate();
        var hours = parseInt(nowTime.getHours());
        var minutes = nowTime.getMinutes();
        // 开始分解付入的时间
        var timeday = time.substring(8, 10);
        var timehours = parseInt(time.substring(11, 13));
        var timeminutes = time.substring(14, 16);
        var d_day = Math.abs(day - timeday);
        var d_hours = hours - timehours;
        var d_minutes = Math.abs(minutes - timeminutes);
        if (d_day <= 1) {
            switch (d_day) {
                case 0:
                    if (d_hours == 0 && d_minutes > 0) {
                        return d_minutes + '分钟前';
                    } else if (d_hours == 0 && d_minutes == 0) {
                        return '1分钟前';
                    } else {
                        return d_hours + '小时前';
                    }
                    break;
                case 1:
                    if (d_hours < 0) {
                        return (24 + d_hours) + '小时前';
                    } else {
                        return d_day + '天前';
                    }
                    break;
            }
        } else if (d_day > 1 && d_day < 10) {
            return d_day + '天前';
        } else {
            return time;
        }
    },
    selGoodsCart(userCode) {
        http.get('cart', { userCode }).then((r) => {
            r.data.forEach(item => {
                item.itemPrice = (item.quantity * item.price).toFixed(2)
                item.checked = false
                item.image = item.images.split(',')[0]
                if (item.tagIds !== null && item.tagIds !== '') {
                    item.tagArray = item.tagIds.split(',')
                }
            });
            this.setData({ orderList: r.data })
        })
    },
    checkAll(e) {
        let orderList = this.data.orderList
        if (this.data.allChecked) {
            orderList.forEach(item => {
                item.checked = false
            });
            this.setData({
                allPrice: 0,
                orderList,
                allChecked: false
            })
        } else {
            let allPrice = 0
            orderList.forEach(item => {
                item.checked = true
                allPrice += Number(item.itemPrice)
            });
            this.setData({
                allPrice,
                orderList,
                allChecked: true
            })
        }
    },
    checkboxChange(e) {
        let orderList = this.data.orderList
        orderList.forEach(item => {
            item.checked = false
        });
        if (e.detail.value.length == 0) {
            this.setData({ allPrice: 0, orderList })
        } else {
            let allPrice = 0
            orderList.forEach(item => {
                e.detail.value.forEach(element => {
                    if (element == item.id) {
                        item.checked = true
                        allPrice += Number(item.itemPrice)
                    }
                });
            });
            this.setData({ allPrice: allPrice, orderList })
        }
    },
    submit: function () {
        let cartIds = []
        this.data.orderList.forEach(item => {
            if (item.checked) {
                cartIds.push(item.id)
            }
        });
        if (cartIds.length == 0) {
            wx.showToast({
                title: '请选择商品',
                icon: 'none',
                duration: 2000
            })
        } else {
            http.get(`address/${this.data.userInfo.code}`).then((r) => {
                if (r.data.length === 0) {
                    wx.showModal({
                        title: '提示',
                        content: '当前并没有收货信息，请进行添加！',
                        success: (sm) => {
                            if (sm.confirm) {
                                wx.navigateTo({
                                    url: '/pages/user/address/index'
                                })
                            }
                        }
                    })
                } else {
                    let cartList = []
                    this.data.orderList.forEach(item => {
                        cartList.push({ id: item.id, quantity: item.quantity })
                    });
                    http.get(`order/settlement`, { cardListStr: JSON.stringify(cartList), listIds: cartIds.join(',') }).then((res) => {
                        wx.navigateTo({
                            url: '/pages/scar/order/index?type=2&orderCode=' + res.msg + ''
                        })  
                    })
                }
            })
        }
    }
});
