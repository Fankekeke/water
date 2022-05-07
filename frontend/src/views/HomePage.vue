<template>
  <div :class="[multipage === true ? 'multi-page':'single-page', 'not-menu-page', 'home-page']">
    <a-row :gutter="15" class="head-info">
      <a-card class="head-info-card">
        <a-col :span="12">
          <div class="head-info-count">
            <div class="head-info-welcome">
              {{welcomeMessage}}
            </div>
            <div class="head-info-desc">
              <p>{{user.roleName ? user.roleName : '暂无角色'}}</p>
            </div>
            <div class="head-info-time">上次登录时间：{{user.lastLoginTime ? user.lastLoginTime : '第一次访问系统'}}</div>
          </div>
        </a-col>
        <a-col :span="12">
          <div>
            <a-row class="more-info">
              <a-col :span="4"></a-col>
              <a-col :span="4"></a-col>
              <a-col :span="4"></a-col>
              <a-col :span="4">
                <head-info title="用户量" :content="orderStatistics.userNum" :center="false" :bordered="false"/>
              </a-col>
              <a-col :span="4">
                <head-info title="订单数量" :content="orderStatistics.orderNum" :center="false" :bordered="false"/>
              </a-col>
              <a-col :span="4">
                <head-info title="交易金额" :content="orderStatistics.orderPrice" :center="false" />
              </a-col>
            </a-row>
          </div>
        </a-col>
      </a-card>
    </a-row>
    <a-row :gutter="15" class="count-info">
      <a-col :span="14" class="visit-count-wrapper">
        <div style="background: #ECECEC; padding: 30px">
          <a-row :gutter="16">
            <a-col :span="8">
              <a-skeleton active v-if="loading" />
              <a-row :gutter="8" v-if="!loading">
                <a-col :span="24">
                  <a-card>
                    <p style="font-size: 14px;margin-left: 5px;margin-bottom: 8px;color: #aaaaaa">本月订单（单）</p>
                    <a-icon type="arrow-up" style="color: red;font-size: 20px;"/>
                    <span style="color: red;font-size: 20px;margin-left: 5px">{{ orderMonthRevenue.orderNum }}</span> <span style="color: red;"> 单</span>
                  </a-card>
                </a-col>
                <a-col :span="24" style="margin-top: 60px">
                  <a-card>
                    <p style="font-size: 14px;margin-left: 5px;margin-bottom: 8px;color: #aaaaaa">本月收益（￥）</p>
                    <a-icon type="arrow-up" style="color: red;font-size: 20px;"/>
                    <span style="color: red;font-size: 20px;margin-left: 5px">{{ orderMonthRevenue.orderPrice }}</span> <span style="color: red;"> 元</span>
                  </a-card>
                </a-col>
              </a-row>
            </a-col>
            <a-col :span="16">
              <a-skeleton active v-if="loading" />
              <apexchart v-if="!loading" type="donut" height="309" :options="chartOptions2" :series="series2"></apexchart>
            </a-col>
          </a-row>
        </div>
      </a-col>
      <a-col :span="10">
        <a-skeleton active v-if="loading" />
        <a-row :gutter="15" v-if="!loading">
          <a-col :span="24" class="visit-count-wrapper">
            <a-card class="visit-count">
              <apexchart type="line" height="326" :options="chartOptions1" :series="series1"></apexchart>
            </a-card>
          </a-col>
        </a-row>
      </a-col>
    </a-row>
    <a-row :gutter="15" class="count-info" style="margin-top: 25px">
      <a-col :span="12" class="visit-count-wrapper">
        <a-card class="visit-count">
          <a-skeleton active v-if="loading" />
          <apexchart v-show="!loading" ref="count" type=bar height="326" :options="chartOptions" :series="series" />
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card class="visit-count" :title="'待发货商品' + '（' + orderRate.length + '）'">
          <a-skeleton active v-if="loading" />
          <a-list v-if="!loading" item-layout="vertical" size="large" :pagination="pagination" :data-source="orderRate">
            <a-list-item slot="renderItem" key="item.title" slot-scope="item, index">
              <template slot="actions">
                <span key="shopping-cart">
                  <a-icon type="shopping-cart" style="margin-right: 8px" />
                  {{ item.payDate }}
                </span>
                <span key="dollar">
                  <a-icon type="dollar" style="margin-right: 8px" />
                  ￥{{ item.allPrice }}
                </span>
              </template>
              <a-list-item-meta :description="item.name + ' - ' + item.address + ' - ' + item.phone">
                <a slot="title" :href="item.href">
                  <span style="font-size: 15px">{{ item.productName }}</span>
                  <span style="font-size: 12px;color: #aaaaaa;font-weight: 300">{{ item.code }}</span>
                </a>
                <a-avatar slot="avatar" :src="item.avatar" />
              </a-list-item-meta>
              <a-row :gutter="15">
                <a-col :span="14">
                  <a-avatar shape="square" :size="64" :src="'http://127.0.0.1:9527/imagesWeb/' + item.images.split(',')[0]" />
                  x {{ item.amount }}
                </a-col>
                <a-col :span="10"></a-col>
              </a-row>
            </a-list-item>
          </a-list>
        </a-card>
      </a-col>
    </a-row>
    <a-row :gutter="15" class="count-info" style="margin-top: 15px">
      <a-col :span="24" style="margin-bottom: 15px">
        <div style="float: right">
          <span>选择月份：</span>
          <a-month-picker placeholder="Select month" @change="monthChange"/>
        </div>
      </a-col>
      <a-col :span="24" class="visit-count-wrapper">
        <a-skeleton active v-if="monthloading" />
        <a-card class="visit-count" v-if="!monthloading">
          <apexchart type="area" height="350" :options="chartOptions4" :series="series4"></apexchart>
        </a-card>
      </a-col>
      <a-col :span="24" class="visit-count-wrapper" style="margin-top: 15px">
        <a-skeleton active v-if="monthloading" />
        <a-card class="visit-count" v-if="!monthloading">
          <apexchart type="bar" height="350" :options="chartOptions5" :series="series5"></apexchart>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>
<script>
import HeadInfo from '@/views/common/HeadInfo'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'HomePage',
  components: {HeadInfo},
  data () {
    return {
      series: [],
      chartOptions: {
        chart: {
          toolbar: {
            show: false
          }
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '35%'
          }
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          show: true,
          width: 2,
          colors: ['transparent']
        },
        xaxis: {
          categories: []
        },
        fill: {
          opacity: 1

        }
      },
      series1: [{
        name: '订单量',
        data: [34, 44, 54, 21, 12, 43, 33, 23, 66, 66, 58]
      }],
      chartOptions1: {
        chart: {
          type: 'line',
          height: 350
        },
        stroke: {
          curve: 'stepline'
        },
        dataLabels: {
          enabled: false
        },
        title: {
          text: '订单量统计',
          align: 'left'
        },
        xaxis: {
          categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep']
        },
        markers: {
          hover: {
            sizeOffset: 4
          }
        }
      },
      series2: [44, 55, 41, 17, 15],
      chartOptions2: {
        labels: [],
        chart: {
          type: 'donut'
        },
        responsive: [{
          breakpoint: 480,
          options: {
            chart: {
              width: 200
            },
            legend: {
              position: 'bottom'
            }
          }
        }]
      },
      series4: [{
        name: '本日交易额',
        data: [12, 34, 458, 45, 47]
      }],
      chartOptions4: {
        chart: {
          type: 'area',
          height: 350,
          zoom: {
            enabled: false
          }
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          curve: 'straight'
        },
        title: {
          text: '月统计',
          align: 'left'
        },
        subtitle: {
          text: '交易额',
          align: 'left'
        },
        labels: ['4-23', '4-24', '4-25', '4-26', '4-27'],
        yaxis: {
          opposite: true
        },
        tooltip: {
          y: {
            formatter: function (val) {
              return val + ' 元'
            }
          }
        },
        legend: {
          horizontalAlign: 'left'
        }
      },
      series5: [{
        name: '本日订单量',
        data: [44, 55, 57, 56, 61, 58, 63, 60, 66]
      }],
      chartOptions5: {
        chart: {
          type: 'bar',
          height: 350
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '55%',
            endingShape: 'rounded'
          }
        },
        title: {
          text: '月统计',
          align: 'left'
        },
        subtitle: {
          text: '订单量',
          align: 'left'
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          show: true,
          width: 2,
          colors: ['transparent']
        },
        xaxis: {
          categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct']
        },
        fill: {
          opacity: 1
        },
        tooltip: {
          y: {
            formatter: function (val) {
              return val + ' 单'
            }
          }
        }
      },
      orderMonthRevenue: {
        orderPrice: 0,
        orderNum: 0
      },
      orderRate: [],
      pagination: {
        pageSize: 3
      },
      orderStatistics: {
        userNum: 0,
        orderNum: 0,
        orderPrice: 0
      },
      todayIp: '',
      todayVisitCount: '',
      totalVisitCount: '',
      userRole: '',
      userDept: '',
      lastLoginTime: '',
      welcomeMessage: '',
      loading: false,
      monthloading: false
    }
  },
  computed: {
    ...mapState({
      multipage: state => state.setting.multipage,
      user: state => state.account.user
    }),
    avatar () {
      return `static/avatar/${this.user.avatar}`
    }
  },
  methods: {
    monthChange (date) {
      this.monthloading = true
      this.$get('/cos/order-info/orderDataByMonth', {date: moment(date).format('YYYY-MM-01')}).then((r) => {
        let orderDataByMonthLabel = []
        let orderNumDataByMonthData = []
        let orderPriceDataByMonthData = []
        r.data.data.forEach(item => {
          orderDataByMonthLabel.push(item.days)
          orderNumDataByMonthData.push(item.orderNum)
          orderPriceDataByMonthData.push(item.orderPrice)
        })
        this.series4[0].data = orderPriceDataByMonthData
        this.chartOptions4.labels = orderDataByMonthLabel
        this.series5[0].data = orderNumDataByMonthData
        this.chartOptions5.xaxis.categories = orderDataByMonthLabel
        setTimeout(() => {
          this.monthloading = false
        }, 200)
      })
    },
    homeData () {
      this.loading = true
      this.monthloading = true
      this.$get('/cos/order-info/home/data').then((r) => {
        this.orderStatistics.userNum = r.data.userNum
        this.orderStatistics.orderNum = r.data.orderNum
        this.orderStatistics.orderPrice = r.data.orderPrice
        this.orderMonthRevenue.orderPrice = r.data.orderStatisticsByCurrentMonth.price
        this.orderMonthRevenue.orderNum = r.data.orderStatisticsByCurrentMonth.num
        this.orderRate = r.data.pendingOrder
        let orderVolumeStatisticsData = []
        let orderVolumeStatisticsLable = []
        r.data.orderVolumeStatistics.forEach(item => {
          orderVolumeStatisticsData.push(item.orderNum)
          orderVolumeStatisticsLable.push(item.days)
        })
        this.series1[0].data = orderVolumeStatisticsData
        this.chartOptions1.xaxis.categories = orderVolumeStatisticsLable
        let orderStatisticsByCurrentMonthRateData = []
        let orderStatisticsByCurrentMonthRateLabel = []
        r.data.orderStatisticsByCurrentMonthRate.forEach(item => {
          orderStatisticsByCurrentMonthRateData.push(item.price)
          orderStatisticsByCurrentMonthRateLabel.push(item.name)
        })
        this.series2 = orderStatisticsByCurrentMonthRateData.length === 0 ? [100] : orderStatisticsByCurrentMonthRateData
        this.chartOptions2.labels = orderStatisticsByCurrentMonthRateLabel.length === 0 ? ['暂无'] : orderStatisticsByCurrentMonthRateLabel
        let orderDataByMonthLabel = []
        let orderNumDataByMonthData = []
        let orderPriceDataByMonthData = []
        r.data.orderDataByMonth.forEach(item => {
          orderDataByMonthLabel.push(item.days)
          orderNumDataByMonthData.push(item.orderNum)
          orderPriceDataByMonthData.push(item.orderPrice)
        })
        this.series4[0].data = orderPriceDataByMonthData
        this.chartOptions4.labels = orderDataByMonthLabel
        this.series5[0].data = orderNumDataByMonthData
        this.chartOptions5.xaxis.categories = orderDataByMonthLabel
        setTimeout(() => {
          this.loading = false
          this.monthloading = false
        }, 200)
      })
    },
    welcome () {
      const date = new Date()
      const hour = date.getHours()
      let time = hour < 6 ? '早上好' : (hour <= 11 ? '上午好' : (hour <= 13 ? '中午好' : (hour <= 18 ? '下午好' : '晚上好')))
      return `${time}，${this.user.username}`
    }
  },
  mounted () {
    this.homeData()
    this.welcomeMessage = this.welcome()
    this.$get(`index/${this.user.username}`).then((r) => {
      let data = r.data.data
      this.todayIp = data.todayIp
      this.todayVisitCount = data.todayVisitCount
      this.totalVisitCount = data.totalVisitCount
      let sevenVisitCount = []
      let dateArr = []
      for (let i = 6; i >= 0; i--) {
        let time = moment().subtract(i, 'days').format('MM-DD')
        let contain = false
        for (let o of data.lastSevenVisitCount) {
          if (o.days === time) {
            contain = true
            sevenVisitCount.push(o.count)
          }
        }
        if (!contain) {
          sevenVisitCount.push(0)
        }
        dateArr.push(time)
      }
      let sevenUserVistCount = []
      for (let i = 6; i >= 0; i--) {
        let time = moment().subtract(i, 'days').format('MM-DD')
        let contain = false
        for (let o of data.lastSevenUserVisitCount) {
          if (o.days === time) {
            contain = true
            sevenUserVistCount.push(o.count)
          }
        }
        if (!contain) {
          sevenUserVistCount.push(0)
        }
      }
      this.$refs.count.updateSeries([
        {
          name: '您',
          data: sevenUserVistCount
        },
        {
          name: '总数',
          data: sevenVisitCount
        }
      ], true)
      this.$refs.count.updateOptions({
        xaxis: {
          categories: dateArr
        },
        title: {
          text: '近七日系统访问记录',
          align: 'left'
        }
      }, true, true)
    }).catch((r) => {
      console.error(r)
      this.$message.error('获取首页信息失败')
    })
  }
}
</script>
<style lang="less">
  .home-page {
    .head-info {
      margin-bottom: .5rem;
      .head-info-card {
        padding: .5rem;
        border-color: #f1f1f1;
        .head-info-avatar {
          display: inline-block;
          float: left;
          margin-right: 1rem;
          img {
            width: 5rem;
            border-radius: 2px;
          }
        }
        .head-info-count {
          display: inline-block;
          float: left;
          .head-info-welcome {
            font-size: 1.05rem;
            margin-bottom: .1rem;
          }
          .head-info-desc {
            color: rgba(0, 0, 0, 0.45);
            font-size: .8rem;
            padding: .2rem 0;
            p {
              margin-bottom: 0;
            }
          }
          .head-info-time {
            color: rgba(0, 0, 0, 0.45);
            font-size: .8rem;
            padding: .2rem 0;
          }
        }
      }
    }
    .count-info {
      .visit-count-wrapper {
        padding-left: 0 !important;
        .visit-count {
          padding: .5rem;
          border-color: #f1f1f1;
          .ant-card-body {
            padding: .5rem 1rem !important;
          }
        }
      }
      .project-wrapper {
        padding-right: 0 !important;
        .project-card {
          border: none !important;
          .ant-card-head {
            border-left: 1px solid #f1f1f1 !important;
            border-top: 1px solid #f1f1f1 !important;
            border-right: 1px solid #f1f1f1 !important;
          }
          .ant-card-body {
            padding: 0 !important;
            table {
              width: 100%;
              td {
                width: 50%;
                border: 1px solid #f1f1f1;
                padding: .6rem;
                .project-avatar-wrapper {
                  display:inline-block;
                  float:left;
                  margin-right:.7rem;
                  .project-avatar {
                    color: #42b983;
                    background-color: #d6f8b8;
                  }
                }
              }
            }
          }
          .project-detail {
            display:inline-block;
            float:left;
            text-align:left;
            width: 78%;
            .project-name {
              font-size:.9rem;
              margin-top:-2px;
              font-weight:600;
            }
            .project-desc {
              color:rgba(0, 0, 0, 0.45);
              p {
                margin-bottom:0;
                font-size:.6rem;
                white-space:normal;
              }
            }
          }
        }
      }
    }
  }
</style>
