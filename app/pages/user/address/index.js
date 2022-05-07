const app = getApp();
let http = require('../../../utils/request')
Page({
	data: {
		StatusBar: app.globalData.StatusBar,
		CustomBar: app.globalData.CustomBar,
		TabbarBot: app.globalData.tabbar_bottom,
		hidden: true,
		addressList: [],
		userInfo: null
	},
	onShow: function () {
		this.isLogin()
	},
	isLogin() {
		wx.getStorage({
			key: 'userInfo',
			success: (res) => {
				this.setData({ userInfo: res.data })
				this.getAddressInfo(res.data.code)
			},
			fail: res => {
				wx.showToast({
					title: '请先进行登录',
					icon: 'none',
					duration: 2000
				})
				setTimeout(() => {
					wx.switchTab({
						url: '../index/index'
					})
				}, 1500)
			}
		})
	},
	getAddressInfo(userCode) {
		http.get(`address/${userCode}`).then((r) => {
			if (r.data !== null) {
				this.setData({ addressList: r.data })
			}
		})
	},
	addressAdd(address) {
		http.post('address', address).then((r) => {
			wx.showToast({
				title: '添加成功',
				icon: 'success',
				duration: 2000
			})
			this.getAddressInfo(this.data.userInfo.code)
		})
	},
	addressEdit(address) {
		http.post('addressEdit', address).then((r) => {
			wx.showToast({
				title: '修改成功',
				icon: 'success',
				duration: 2000
			})
		})
	},
	edit(event) {
		wx.navigateTo({
			url: '/pages/address/edit/index?addressId=' + event.currentTarget.dataset.addressid + ''
		});
	},
	add: function () {
		let that = this;
		wx.showModal({
			title: '提示',
			content: '是否获取微信的收货地址？',
			success(res) {
				if (res.confirm) {
					that.wxaddress();
				} else if (res.cancel) {
					wx.navigateTo({
						url: '/pages/address/add/index'
					});
				}
			}
		});
	},
	//获取微信的收货地址
	wxaddress: function () {
		let that = this;
		wx.getSetting({
			success: res => {
				if (res.authSetting['scope.address']) {
					wx.chooseAddress({
						success(res) {
							console.log(res)
							let address = res.provinceName + '' + res.cityName + '' + res.countyName + '' + res.detailInfo
							let data = { takeUserName: res.userName, takeUserPhone: res.telNumber, address, defaultAddress: 1, userCode: that.data.userInfo.code, province: res.provinceName, city: res.cityName, area: res.countyName }
							that.addressAdd(data)
						}
					})
				}
			}
		});
	}
});
