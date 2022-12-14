<template>
  <div class="w-full h-screen">

    <div class="absolute flex h-24 w-full bg-blue-600 py-3 text-white">
      <h3 class="px-3 text-6xl font-bold">SCM</h3>
      <p class="flex items-center text-5xl">System</p>
    </div>
    <div class="h-full bg-gray-50">
      <div class="flex h-full items-center justify-center pt-16">
        <div class="flex h-72 w-3/5 items-center justify-center bg-gray-50">
          <div>
            <a href="/contract/contractHome">
              <button class="mr-7 h-32 w-80 rounded-2xl bg-zinc-300 text-4xl font-semibold text-white shadow-lg shadow-slate-700">발주하기</button>
            </a>
            <div class="mt-6 font-medium ml-10">
              <p class="">웨이닝코리아 / 고고커피에</p>
              <div class="flex">
                <p class="font-semibold">상품을 주문</p>
                <p>하는 화면 입니다.</p>
              </div>
            </div>
          </div>
          <div>
            <a href="/order/orderHome">
              <button class="ml-7 h-32 w-80 rounded-2xl bg-zinc-400 text-4xl font-semibold text-white shadow-lg shadow-slate-700">주문서 확인하기</button>
            </a>
            <div class="mt-6 font-medium ml-10">
              <p class="">웨이닝코리아 / 고고커피가 귀사에</p>
              <div class="flex">
                <p class="font-semibold">주문한 발주서를 확인</p>
                <p>하는 화면 입니다.</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: 'SelectDivisionView'
}
</script>

<style scoped>

</style>
