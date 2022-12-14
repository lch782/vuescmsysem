<template>
  <div class="tradingStatement p-1">
    <div class="p-2 border border-2 border-black w-full">
      <div class="flex justify-between">
        <div class="w-28"></div>
        <div class="title">
          <div class="flex justify-center ">
            <p class="text-4xl underline underline-offset-8 decoration-1">거&nbsp;&nbsp;래&nbsp;&nbsp;명&nbsp;&nbsp;세&nbsp;&nbsp;표&nbsp;</p>
            <p class="subtitle underline underline-offset-8 decoration-1">&lt; 공급자 보관용 &gt;</p>
          </div>
        </div>
        <div class="border border-black mr-2 mt-2" style="width: 100px; height: 100px;"><img src="" alt="검수번호"></div>
      </div>
      <div class="">
        <div class="flex ml-1">
          검수번호 : <p class="underline underline-offset-4 p-1"></p>
        </div>
      </div>
      <div class="info my-1 w-full" style="">
        <table class="border-2 border-black w-full" style="">
          <tr>
            <th class="border border-black w-10 " rowspan="4" >공<br>급<br>자</th>
            <th class="border border-black w-20 h-12" >등록번호</th>
            <td class="border border-black text-center font-bold text-sm" colspan="3" style="font-size: 20px"></td>
            <th class="border border-black w-10" rowspan="4" style="border-left: 3px solid black;">공<br>급<br>받<br>는<br>자</th>
            <th class="border border-black w-20" style="height: 40px;">등록번호</th>
            <td class="border border-black text-center font-bold text-sm" colspan="3" style="font-size: 20px"></td>
          </tr>
          <tr>
            <th class="border border-black ">상&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;호<br>(법인명)</th>
            <td class="border border-black font-bold " style="font-size: 15px"></td>
            <th class="border border-black w-20">성&nbsp;&nbsp;&nbsp;명</th>
            <td class="border border-black w-28"><span class="font-bold "
                                                        style="font-size: 15px"></span><span>(인)</span></td>
            <th class="border border-black ">상&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;호<br>(법인명)</th>
            <td class="border border-black font-bold " style="font-size: 15px"></td>
            <th class="border border-black w-20">성&nbsp;&nbsp;&nbsp;명</th>
            <td class="border border-black w-28"><span class="font-bold "
                                                        style="font-size: 15px"></span><span>(인)</span></td>
          </tr>
          <tr>
            <th class="border border-black ">사 업 장<br>주&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;소</th>
            <td class="border border-black " colspan="3" style="font-size: 12px;"></td>
            <th class="border border-black ">사 업 장<br>주&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;소</th>
            <td class="border border-black " colspan="3" style="font-size: 12px;"></td>
          </tr>
          <tr style="height: 35px">
            <th class="border border-black h-12">업&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;태</th>
            <td class="border border-black " style="font-size: 15px"></td>
            <th class="border border-black ">종&nbsp;&nbsp;&nbsp;목</th>
            <td class="border border-black " style="font-size: 15px"></td>
            <th class="border border-black ">업&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;태</th>
            <td class="border border-black " style="font-size: 15px"></td>
            <th class="border border-black ">종&nbsp;&nbsp;&nbsp;목</th>
            <td class="border border-black " style="font-size: 15px"></td>
          </tr>
        </table>
      </div>
      <div>
        <table class="border-2 border-black p-2 w-full" style="font-size: 13px">
          <thead>
            <tr style="font-size: 16px">
              <th class="border border-black" style="width: 120px">거래일자</th>
              <th class="border border-black" style="width: 426px;">공급가액</th>
              <th class="border border-black" style="width: 217px;">세액</th>
              <th class="border border-black">비고</th>
            </tr>
          </thead>
          <tbody>
            <tr class="h-7">
              <td class="border border-black "></td>
              <td class="border border-black "></td>
              <td class="border border-black "></td>
              <td class="border border-black "></td>
              <td class="border border-black "></td>
            </tr>
          </tbody>
        </table>
      </div>
      <table class="list border-2 border-black w-full mt-1" style="font-size: 13px">
        <thead>
        <tr style="font-size: 16px">
          <th class="border border-black p-2 w-10">No</th>
          <th class="border border-black p-2 " style="width: 130px">품목코드</th>
          <th class="border border-black p-2 " style="">품명</th>
          <th class="border border-black p-2 " style="">발주번호</th>
          <th class="border border-black p-2 " style="">수량</th>
          <th class="border border-black p-2 " style="">공급가액</th>
          <th class="border border-black p-2 " style="">세 액</th>
          <th class="border border-black p-2 " style="width: 112px">비 고</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="i in 10" :key="i" >
          <td class="border border-black px-2 text-center">{{ i }}</td>
          <td class="border border-black px-2" ></td>
          <td class="border border-black px-2" style="width: 376px"></td>
          <td class="border border-black px-2" style="width: 130px;"></td>
          <td class="border border-black px-2 text-right" style="width: 87px"></td>
          <td class="border border-black px-2 text-right"></td>
          <td class="border border-black px-2 text-right"></td>
          <td class="border border-black px-2 text-right"></td>
        </tr>
        </tbody>
      </table>
      <table class="border-2 border-black w-full mt-1" style="font-size: 13px">
        <thead style="font-size: 16px">
          <tr>
            <th class="border border-black">합계금액</th>
            <th class="border border-black" style="width: 152px">확 인</th>
            <th class="border border-black" style="width: 152px">담 당</th>
            <th class="border border-black" style="width: 152px">팀 장</th>
            <th class="border border-black" style="width: 152px">부 서 장</th>
            <th class="border border-black" style="width: 112px">비 고</th>
          </tr>
        </thead>
        <tbody>
          <tr class="h-7">
            <th class="border border-black"></th>
            <th class="border border-black"></th>
            <th class="border border-black"></th>
            <th class="border border-black"></th>
            <th class="border border-black"></th>
            <th class="border border-black"></th>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { onMounted } from 'vue'

export default {
  name: 'tradingStatement',
  setup () {
    onMounted(() => {

    })
  }
}
</script>

<style>
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

.tradingStatement {
  width: 29.7cm;
  height: 20cm;
}

.subtitle {
  margin-top: 17.5px;
}

.insNo {
  word-break: break-all;
}

.list {
  border-bottom: none;
}
</style>
