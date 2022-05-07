const app = getApp();
let http = require('../../../utils/request')
Page({
	data: {
		StatusBar: app.globalData.StatusBar,
		CustomBar: app.globalData.CustomBar,
		hidden: true,
		current: 0,lines: 0,
		swiperlist: [{
			id: 0,
			url: 'https://img12.360buyimg.com/cms/jfs/t1/217298/15/17818/507171/626252abE6ecfbb17/602dc4afc9ea94cc.jpg',
			type: 1
		}, {
			id: 1,
			url: 'https://img12.360buyimg.com/cms/jfs/t1/210578/17/15039/449705/61d55f42Ef2f37995/8db355929368dd5f.jpg',
			type: 2

		}, {
			id: 2,
			url: 'https://img11.360buyimg.com/cms/jfs/t1/163126/3/7519/176829/6034d2d6E97640e7e/12810f4f531582a7.jpg',
			type: 3
		}],
		iconList: [{
			id: 1,
			icon: 'questionfill',
			color: 'red',
			name: '商品',
			type: 1
		}, {
			id: 3,
			icon: 'shopfill',
			color: 'yellow',
			name: '店铺',
			type: 3
		}, {
			id: 4,
			icon: 'discoverfill',
			color: 'olive',
			name: '公告',
			type: 4
		}],
		Headlines: [{
			id:1,
			title:"放心购",
			type: 1
		},{
			id:2,
			title:"厂商配送",
			type: 2
		}],
		product: [],
		shop: [],
		key: '',
		videosrc: "http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400",

	},
	onLoad: function () {
		this.home()
	},
	home() {
		http.get('home').then((r) => {
			r.product.forEach(item => {
				item.image = item.images.split(',')[0]
				item.shopImage = item.shopImages.split(',')[0]
			});
			r.shop.forEach(item => {
				item.image = item.images.split(',')[0]
			});
			this.setData({
				product: r.product,
				shop: r.shop
			})
		})
	},
	swiperchange: function (e) {
		this.setData({
			current:e.detail.current
		});
	},
	swipclick: function (e) {
		let that = this;
		var swip = that.data.swiperlist[that.data.current];
		console.log(swip);
		if (swip.type === 1) {
			wx.navigateTo({
				url: '/pages/home/doc/index?id=' + swip.id
			});
		}
	},
	getKeyValue(e) {
		this.setData({ key: e.detail.value })
	},
	lineschange: function (e) {
		this.setData({
			lines:e.detail.current
		});
	},
	linesclick: function (e) {
		let that = this;
		var swip = that.data.Headlines[that.data.current];
		console.log(swip);
		if (swip.type === 1) {
			wx.navigateTo({
				url: '/pages/home/doc/index?id=' + swip.id
			});
		}
	},
	itemckcred: function (e) {
		let that = this;
		var item = e.currentTarget.dataset;
		if (item.itemtype === 1) {
			wx.switchTab({
				url: '/pages/scar/goods/index'
			})
		}
		if (item.itemtype === 3) {
			wx.navigateTo({
				url: '/pages/home/groom/index'
			});
		}
		if (item.itemtype === 4) {
			wx.navigateTo({
				url: '/pages/coupon/index/index'
			});
		}
	},
	search: function () {
		wx.navigateTo({
			url: '/pages/home/search/index?key=' + this.data.key
		});
	}
});
