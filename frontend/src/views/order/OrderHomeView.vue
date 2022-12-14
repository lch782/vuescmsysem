<template>
  <div class="flex justify-center mt-32">
    <apexchart
      type='area'
      width='700'
      height='500'
      :series='chartMap.series'
      :options='chartMap.chartOptions'
    />
    <div>
      <table class="ml-20 h-full text-center" >
        <tr class="bg-[rgba(148,198,229,0.7)]">
          <td class="w-36">년 월</td>
          <td class="w-72">총 입고 누계 금액</td>
        </tr>
        <tr class="bg-[rgba(148,198,229,0.19)]" >
          <td>2022년 1월</td><td>{{jan}}</td>
        </tr>
        <tr class="" >
          <td>2022년 2월</td><td>{{feb}}</td>
        </tr>
        <tr class="bg-[rgba(148,198,229,0.19)]" >
          <td>2022년 3월</td><td>{{mar}}</td>
        </tr>
        <tr class="" >
          <td>2022년 4월</td><td>{{apr}}</td>
        </tr>
        <tr class="bg-[rgba(148,198,229,0.19)]" >
          <td>2022년 5월</td><td>{{may}}</td>
        </tr>
        <tr class="" >
          <td>2022년 6월</td><td>{{jun}}</td>
        </tr><tr class="bg-[rgba(148,198,229,0.19)]" >
        <td>2022년 7월</td><td>{{jul}}</td>
      </tr>
        <tr class="" >
          <td>2022년 8월</td><td>{{aug}}</td>
        </tr>
        <tr class="bg-[rgba(148,198,229,0.19)]" >
          <td>2022년 9월</td><td>{{sep}}</td>
        </tr>
        <tr class="" >
          <td>2022년 10월</td><td>{{oct}}</td>
        </tr>
        <tr class="bg-[rgba(148,198,229,0.19)]" >
          <td>2022년 11월</td><td>{{nov}}</td>
        </tr>
        <tr class="" >
          <td>2022년 12월</td><td>{{dec}}</td>
        </tr>
        <tr class="bg-[rgba(148,198,229,0.7)]">
          <td>합계</td>
          <td>{{jan+feb+mar+apr+may+jun+jul+aug+sep+oct+nov+dec}}</td>
        </tr>
      </table>
    </div>
  </div>
</template>

<script>
import { onMounted, ref } from 'vue'
import axios from 'axios'
import store from '@/store'

export default {
  name: 'OrderHomeView',
  setup () {
    const jan = ref(0)
    const feb = ref(0)
    const mar = ref(0)
    const apr = ref(0)
    const may = ref(0)
    const jun = ref(0)
    const jul = ref(0)
    const aug = ref(0)
    const sep = ref(0)
    const oct = ref(0)
    const nov = ref(0)
    const dec = ref(0)

    const getMontlySale = ref(new Array(12))

    onMounted(() => {
      axios.get('/api/order/getMontlySales', {
        params: {
          bizNo: store.state.account.bizno,
          custCode: store.state.account.custCode
        }
      }).then((res) => {
        getMontlySale.value = res.data
        console.log(getMontlySale.value)

        for (let i = 0; i < getMontlySale.value.length; i++) {
          if (getMontlySale.value[i][0] === 1) {
            jan.value = getMontlySale.value[i][1] - getMontlySale.value[i][2]
          } else if (getMontlySale.value[i][0] === 2) {
            feb.value = getMontlySale.value[i][1] - getMontlySale.value[i][2]
          } else if (getMontlySale.value[i][0] === 3) {
            mar.value = getMontlySale.value[i][1] - getMontlySale.value[i][2]
          } else if (getMontlySale.value[i][0] === 4) {
            apr.value = getMontlySale.value[i][1] - getMontlySale.value[i][2]
          } else if (getMontlySale.value[i][0] === 5) {
            may.value = getMontlySale.value[i][1] - getMontlySale.value[i][2]
          } else if (getMontlySale.value[i][0] === 6) {
            jun.value = getMontlySale.value[i][1] - getMontlySale.value[i][2]
          } else if (getMontlySale.value[i][0] === 7) {
            jul.value = getMontlySale.value[i][1] - getMontlySale.value[i][2]
          } else if (getMontlySale.value[i][0] === 8) {
            aug.value = getMontlySale.value[i][1] - getMontlySale.value[i][2]
          } else if (getMontlySale.value[i][0] === 9) {
            sep.value = getMontlySale.value[i][1] - getMontlySale.value[i][2]
          } else if (getMontlySale.value[i][0] === 10) {
            oct.value = getMontlySale.value[i][1] - getMontlySale.value[i][2]
          } else if (getMontlySale.value[i][0] === 11) {
            nov.value = getMontlySale.value[i][1] - getMontlySale.value[i][2]
          } else if (getMontlySale.value[i][0] === 12) {
            dec.value = getMontlySale.value[i][1] - getMontlySale.value[i][2]
          }
        }

        chartMap.series = [
          {
            data: [
              jan.value,
              feb.value,
              mar.value,
              apr.value,
              may.value,
              jun.value,
              jul.value,
              aug.value,
              sep.value,
              oct.value,
              nov.value,
              dec.value
            ]
          }
        ]
      })
    })

    const chartMap = {
      series: [
        {
          name: 'series-1'
        }
      ],
      chartOptions: {
        chart: {
          type: 'line',
          zoom: {
            enabled: false
          }
        },
        dataLabels: {
          enabled: true
        },
        stroke: {
          curve: 'straight'
        },
        title: {
          text: '2022년 월별 입고 누계금액 차트',
          align: 'center'
        },
        grid: {
          row: {
            colors: ['#f3f3f3', 'transparent'],
            opacity: 0.5
          }
        },
        xaxis: {
          categories: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월']
        }
      }
    }

    return {
      jan,
      feb,
      mar,
      apr,
      may,
      jun,
      jul,
      aug,
      sep,
      oct,
      nov,
      dec,
      getMontlySale,
      chartMap
    }
  }
}
</script>
