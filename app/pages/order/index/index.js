const app = getApp();
let http = require('../../../utils/request')
Page({
    data: {
        StatusBar: app.globalData.StatusBar,
        CustomBar: app.globalData.CustomBar,
        TabbarBot: app.globalData.tabbar_bottom,
        TabCur: 0, scrollLeft: 0,
        SortMenu: [{ id: 0, name: "全部订单" }, { id: 1, name: "待付款" }, { id: 2, name: "待发货" }, { id: 3, name: "待收货" }, { id: 4, name: "已完成" }],
        userInfo: null,
        orderListCopy: [],
        orderList: [],
        show: false,
        value: 3,
        remarks: '',
        orderCode: null,
        fileList: []
    },
    onLoad: function (options) {
        wx.getStorage({
            key: 'userInfo',
            success: (res) => {
                this.setData({ userInfo: res.data })
                this.getOrderListByUserId(res.data.code)
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
    afterRead(event) {
        const { file } = event.detail;
        let that = this
        // 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
        wx.uploadFile({
            url: 'http://127.0.0.1:9527/file/fileUpload', // 仅为示例，非真实的接口地址
            filePath: file.url,
            name: 'avatar',
            success(res) {
                // 上传完成需要更新 fileList
                const { fileList = [] } = that.data;
                fileList.push({ ...file, url: res.data });
                that.setData({ fileList });
            },
        });
    },
    onChange: function (e) {
        this.setData({
            value: e.detail
        })
    },
    evaluation: function (e) {
        this.setData({
            show: true,
            orderCode: e.currentTarget.dataset['index']
        })
    },
    payment(e) {
        wx.navigateTo({
            url: '/pages/scar/order/index?type=2&orderCode=' + e.currentTarget.dataset.index + ''
        })
    },
    removeOrder(e) {
        http.del(`order/${e.currentTarget.dataset.index}`).then((r) => {
            this.getOrderListByUserId(this.data.userInfo.code)
        })
    },
    receipt: function (e) {
        wx.showModal({
            title: '提示',
            content: '确定要收货吗？',
            success: (sm) => {
                if (sm.confirm) {
                    http.put(`order/receipt/${e.currentTarget.dataset['index']}`).then((r) => {
                        wx.showToast({
                            title: '收货成功',
                            icon: 'success',
                            duration: 1000
                        })
                        setTimeout(() => {
                            this.getOrderListByUserId(this.data.userInfo.code)
                        }, 500)
                    })
                }
            }
        })
    },
    evaluationSubmit: function (e) {
        let that = this
        if (this.data.remarks != '') {
            let images = []
            this.data.fileList.forEach(item => {
                images.push(item.url)
            });
            http.post('evaluation', { orderCode: this.data.orderCode, score: this.data.value, content: this.data.remarks, userCode: this.data.userInfo.code, images: images.length !== 0 ? images.join(',') : null }).then((r) => {
                that.setData({
                    show: false
                })
                wx.showToast({
                    title: '评价成功',
                    icon: 'success',
                    duration: 1000
                })
                setTimeout(() => {
                    this.getOrderListByUserId(this.data.userInfo.code)
                }, 1000)
            })
        } else {
            wx.showToast({
                title: '请填写评价内容',
                icon: 'none',
                duration: 1000
            })
        }
    },
    onClose: function () {
        this.setData({
            show: false
        })
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
    getOrderListByUserId(userCode) {
        http.get(`order/list/${userCode}`).then((r) => {
            r.data.forEach(item => {
                if (item.evaluationStatus !== 0) {
                    item.evaluationContent = item.evaluationContent.slice(0, 20) + '...'
                }
                let amount = 0
                item.orderItem.forEach(iitem => {
                    amount += iitem.amount
                    iitem.image = iitem.images.split(',')[0]
                    if (iitem.tagIds !== null && iitem.tagIds !== '') {
                        iitem.tagArray = iitem.tagIds.split(',')
                    }
                })
                item.amount = amount
            });
            this.setData({
                TabCur: 0,
                orderList: r.data,
                orderListCopy: r.data
            })
        })
    },
    tabSelect(e) {
        this.setData({
            TabCur: e.currentTarget.dataset.id,
            scrollLeft: (e.currentTarget.dataset.id - 1) * 60
        })
        if (e.currentTarget.dataset.id == 0) {
            this.setData({
                orderList: this.data.orderListCopy
            })
        } else {
            let orderList = []
            this.data.orderListCopy.forEach(item => {
                if (item.orderStatus == e.currentTarget.dataset.id) {
                    orderList.push(item)
                }
            });
            this.setData({
                orderList
            })
        }
    }
});
