<template>
  <div v-for="info in purchaseInfo" :key="info" >
    <div class="purchaseOrder" >
      <div class="flex justify-center text-5xl mt-8"><p class="underline underline-offset-8">발주서</p></div>
      <div class="w-full flex justify-center mt-12" >
        <div class="" style="width: 500px">
          <table class="border-2 border-black w-full h-full" style="font-size: 13px">
            <tr class="text-center">
              <th class="p-2 border-2 border-black bg-gray-100 text-center border-b text-gray-600 w-20">일련번호</th>
              <td class="p-2 border-2 border-black text-left">{{ info.buyCode }}</td>
            </tr>
            <tr>
              <th class="p-2 border-2 border-black h-full border bg-gray-100 text-center border-b text-gray-600">수신</th>
              <td class="p-2 border-2 border-black h-full">{{ info.custName }}</td>
            </tr>
            <tr>
              <th class="p-2 border-2 border-black border bg-gray-100 text-center border-b text-gray-600">TEL</th>
              <td class="p-2 border-2 border-black">{{ info.custTel }}</td>
            </tr>
            <tr class="border-b">
              <th class="p-2 border-2 border-black border bg-gray-100 text-center border-b text-gray-600">FAX</th>
              <td class="p-2 border-2 border-black">{{ info.custFax }}</td>
            </tr>
            <tr class="row-span-3">
              <td colspan="2" class="p-2 border-2 border-black text-center">납기일자 : {{ info.inDate }}</td>
            </tr>
          </table>
        </div>
        <div class="w-full ml-2" >
          <table class="w-full border-2 border-black" style="font-size: 13px">
            <tr class="bg-gray-100 text-center ">
              <td colspan="2" class="h-20 p-2 border-2 border-black"></td>
            </tr>
            <tr>
              <th class="p-2 border-2 border-black w-32">사업자등록번호</th>
              <td class="p-2 border-2 border-black">{{ info.bizNo }}</td>
            </tr>
            <tr>
              <th class="p-2 border-2 border-black">회사명/대표</th>
              <td class="p-2 border-2 border-black">{{ info.compName }} / {{ info.ceoName }}</td>
            </tr>
            <tr>
              <th class="p-2 border-2 border-black">주 소</th>
              <td class="p-2 border-2 border-black">{{ info.address }}</td>
            </tr>
            <tr>
              <th class="p-2 border-2 border-black">연락처</th>
              <td class="p-2 border-2 border-black">T. {{ info.compTel }} / F. {{ info.compFax }}</td>
            </tr>
            <tr>
              <th class="p-2 border-2 border-black">담당/연락처</th>
              <td class="p-2 border-2 border-black">{{ info.manager }}()</td>
            </tr>
          </table>
        </div>
      </div>
      <div class="flex justify-center w-full" style="font-size: 13px">
        <table class="border border-2 border-black w-full">
          <tr>
            <td class="p-2 border border-2 border-black font-bold text-lg">배송지 : {{ info.custAddr }}</td>
          </tr>
          <tr>
            <td class="p-2 font-bold text-lg">참 조 : {{ info.remark }}</td>
          </tr>
        </table>
      </div>
      <div class="flex justify-center w-full">
        <table class="w-full border border-2 border-slate-300 mt-5" >
          <tr class="bg-gray-100 text-center border border-2 border-slate-300" >
            <th class="w-10">No</th>
            <th class="p-2 border border-2 border-slate-300 ">품목명</th>
            <th class="p-2 ">수량</th>
          </tr>

          <tr v-for="item in itemArr" :key="item" >
            <template v-if="info.buyCode === item.buyCode">
              <td class="p-2 border border-2 border-slate-300 text-center" >{{item.rowNo}}</td>
              <td class="p-2 border border-2 border-slate-300" >{{item.itemName}}</td>
              <td class="p-2 text-right border border-2 border-slate-300" >{{item.itemQty}}</td>
            </template>
          </tr>
          <tr class="bg-gray-100 text-center border border-2 border-slate-300" v-for="item in itemArr" :key="item">
            <template v-if="item.rowNo % 10 === 1">
              <template v-if="info.buyCode === item.buyCode">
                <th class="p-2 border border-2 border-slate-300" colspan="2">총 합계</th>
                <th class="p-2 text-right">{{item.sumQty}}</th>
              </template>
            </template>
          </tr>
        </table>
      </div>
    </div>
  </div>
</template>
<script>

import { onMounted, ref } from 'vue'
import axios from 'axios'
import store from '@/store'

export default {
  name: 'purchaseOrder',
  setup () {
    const purchaseInfo = ref()
    const purchaseItem = ref()
    const itemArr = ref([])
    // const sumQty = ref(0)
    // const itemInfo = ref([])

    onMounted(() => {
      axios.get('/api/order/getPurchaseInfo', {
        params: {
          fromDate: store.state.fromDate,
          toDate: store.state.toDate,
          type1: '',
          type2: '',
          type3: '',
          itemName: '',
          id: '',
          buySts: ''
        }
      }).then((res) => {
        purchaseInfo.value = res.data
        console.log(purchaseInfo.value)
      })
      axios.get('/api/order/getPurchaseItem', {
        params: {
          id: store.state.account.userId,
          fromDate: store.state.fromDate,
          toDate: store.state.toDate,
          type1: '',
          type2: '',
          type3: '',
          itemName: '',
          buySts: '00'
        }
      }).then((res) => {
        itemArr.value = res.data
        console.log(itemArr.value)
      })
    })

    return {
      purchaseInfo,
      purchaseItem,
      itemArr
      // itemInfo,
      // rowNo
    }
  }
}
</script>
<style scoped>
* {
  box-sizing: border-box;
  -moz-box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}

.purchaseOrder {
  width: 21cm;
  min-height: 29.7cm;
  padding: 1.5cm 1.5cm 2cm 1.5cm;
}
</style>
