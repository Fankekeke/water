const app = getApp();
let http = require('../../../utils/request')
Page({
	data: {
		StatusBar: app.globalData.StatusBar,
		CustomBar: app.globalData.CustomBar,
		hidden: true,
		shopList: []
	},
	onLoad: function (option) {
		this.getShopList()
	},
	shopDeatil(e) {
		wx.navigateTo({
			url: '/pages/shop/index/index?shopCode='+e.currentTarget.dataset.shopcode+''
		});
	},
	getShopList() {
		http.get('shop/list').then((r) => {
			r.data.forEach(element => {
				element.image = element.images.split(',')[0]
				if (element.productList) {
					element.productList.forEach(item => {
						item.image = item.images.split(',')[0]
					});
				}
			});
            this.setData({
                shopList: r.data
            })
        })
	}
});
