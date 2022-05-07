const app = getApp();
let http = require('../../../utils/request')
Page({
	data: {
		StatusBar: app.globalData.StatusBar,
		CustomBar: app.globalData.CustomBar,
		hidden: true,
		region: ['重庆市', '重庆市', '江北区'],
		name: '',
		phone: '',
		address: '',
		defaultAddress: 1,
		addressId: ''
	},
	onLoad: function (option) {
		this.setData({ addressId: option.addressId })
		this.getAddressInfo(option.addressId)
	},
	RegionChange: function (e) {
		this.setData({
			region: e.detail.value
		})
	},
	getAddressInfo(addressId) {
		http.get(`/address/info/${addressId}`).then((r) => {
			let region = [r.data.province, r.data.city, r.data.area]
			this.setData({
				name: r.data.takeUserName,
				address: r.data.address,
				phone: r.data.takeUserPhone,
				defaultAddress: r.data.defaultAddress,
				region
			})
		})
	},
	delete() {
		let that = this
		wx.showModal({
			title: '提示',
			content: '确定要删除吗？',
			success: function (sm) {
				if (sm.confirm) {
					http.del(`address/${that.data.addressId}`).then((r) => {
						wx.showToast({
							title: '删除成功',
							icon: 'success',
							duration: 2000
						})
						setTimeout(() => {
							wx.navigateBack({
								delta: 1
							})
						}, 1000)
					})
				} else if (sm.cancel) {
					console.log('取消')
				}
			}
		})
	},
	edit() {
		wx.getStorage({
			key: 'userInfo',
			success: (res) => {
				let data = { id: this.data.addressId, takeUserName: this.data.name, takeUserPhone: this.data.phone, address: this.data.address, defaultAddress: this.data.defaultAddress, userCode: res.data.code, province: this.data.region[0], city: this.data.region[1], area: this.data.region[2] }
				if (data.name === '' || data.takeUserPhone === '' || data.address === '' || data.province === '') {
					wx.showToast({
						title: '请完整填写！',
						icon: 'error',
						duration: 2000
					})
				} else {
					if (this.isMobileNumber(data.takeUserPhone)) {
						this.addressEdit(data)
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
	addressEdit(address) {
		http.put('address', address).then((r) => {
			wx.showToast({
				title: '修改成功',
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
	},
	switch1Change(event) {
		let defaultAddress = event.detail.value ? 1 : 0
		this.setData({ defaultAddress })
	}
});
