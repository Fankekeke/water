const app = getApp();
let http = require('../../../utils/request')
Page({
	data: {
		StatusBar: app.globalData.StatusBar,
		CustomBar: app.globalData.CustomBar,
		TabbarBot: app.globalData.tabbar_bottom,
		hidden: true,
		region: ['重庆市', '重庆市', '江北区'],
		name: '',
		phone: '',
		address: '',
		defaultAddress: 1
	},
	onLoad: function (option) {
		wx.getSetting({
			success: res => {
				if (!res.authSetting['scope.userInfo']) {
					wx.redirectTo({
						url: '/pages/auth/auth'
					})
				}
			}
		});
	},
	RegionChange: function (e) {
		this.setData({
			region: e.detail.value
		})
	},
	add() {
		wx.getStorage({
			key: 'userInfo',
			success: (res) => {
				let data = { takeUserName: this.data.name, takeUserPhone: this.data.phone, address: this.data.address, defaultAddress: this.data.defaultAddress, userCode: res.data.code, province: this.data.region[0], city: this.data.region[1], area: this.data.region[2] }
				if (data.name === '' || data.takeUserPhone === '' || data.address === '' || data.province === '') {
					wx.showToast({
						title: '请完整填写！',
						icon: 'error',
						duration: 2000
					})
				} else {
					if (this.isMobileNumber(data.takeUserPhone)) {
						this.addressAdd(data)
					}
				}
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
	getNameValue(e) {
		this.setData({ name: e.detail.value })
	},
	getAddressValue(e) {
		this.setData({ address: e.detail.value })
	},
	getPhoneValue(e) {
		this.setData({ phone: e.detail.value })
	},
	addressAdd(address) {
		http.post('address', address).then((r) => {
			wx.showToast({
				title: '添加成功',
				icon: 'success',
				duration: 2000
			})
			setTimeout(() => {
				wx.navigateBack({
					delta: 1
				})
			}, 1000)
		})
	},
	switch1Change(event) {
		let defaultAddress = event.detail.value ? 1 : 0
		this.setData({ defaultAddress })
	},
	isMobileNumber(phone) {
		var flag = false
		var myreg = /^(((13[0-9]{1})|(14[0-9]{1})|(17[0-9]{1})|(15[0-3]{1})|(15[4-9]{1})|(18[0-9]{1})|(199))+\d{8})$/
		if (phone == '') {
			wx.showToast({
				title: '手机号码不能为空！',
				icon: 'error',
				duration: 2000
			})
		} else if (phone.length != 11) {
			wx.showToast({
				title: '无效的手机号码！',
				icon: 'error',
				duration: 2000
			})
		} else if (!myreg.test(phone)) {
			wx.showToast({
				title: '无效的手机号码！',
				icon: 'error',
				duration: 2000
			})
		} else {
			flag = true
		}
		return flag
	}
});
