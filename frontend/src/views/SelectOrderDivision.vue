<template>
  <div class="flex justify-center w-full bg-gray-200">
    <div class="border border-black w-30 h-30 bg-gray-200">WAINNING LOGO</div>
    </div>
    <div class="container grid grid-cols-4" style="margin: 100px auto;">
      <button class="border border-black h-36 mb-16" style="margin: 0 auto 50px" @click="goOrder">CAFE MUSEO</button>
      <button class="border border-black h-36 mb-16" style="margin: 0 auto 50px">BEOBE</button>
      <button class="border border-black h-36 mb-16" style="margin: 0 auto 50px">GTBEANS</button>
      <button class="border border-black h-36 mb-16" style="margin: 0 auto 50px">SWEET CUP</button>
      <button class="border border-black h-36 mb-16" style="margin: 0 auto 50px">꽃샘</button>
      <button class="border border-black h-36 mb-16" style="margin: 0 auto 50px">(주)비츄인</button>
      <button class="border border-black h-36 mb-16" style="margin: 0 auto 50px">흥국 F&B</button>
      <button class="border border-black h-36 mb-16" style="margin: 0 auto 50px">MONIN</button>
    </div>
</template>
<script>
export default {
  setup () {
    const goOrder = () => {
    }

    return {
      goOrder
    }
  }
}
</script>
<style scoped >
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html, body {
  width: 100vw;
  height: 100%;
}
.container{
  width: 60%;
}
.border {
  width: 13rem;
}
@media only screen and (max-width : 1400px) {
  .container {
    width: 840px;
  }
  .border {
    width: 13rem;
  }
}
</style>
