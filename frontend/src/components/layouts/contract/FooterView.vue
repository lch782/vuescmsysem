<template>
  <div class="footer w-full h-20 flex justify-center items-center bg-white">
    <img v-if="loginCompanyCode === '10004'" src="@/assets/images/layout/웨이닝코리아-사업자내용.png" alt=""/>
    <img v-if="loginCompanyCode === '10005'" src="@/assets/images/layout/웨이닝코리아-사업자내용.png" alt=""/>
    <img v-if="loginCompanyCode === '10006'" src="@/assets/images/layout/웨이닝코리아-사업자내용.png" alt=""/>
    <img v-if="loginCompanyCode === '10007'" src="@/assets/images/layout/토트-사업자-내용.png" alt=""/>
    <img v-if="loginCompanyCode === '10008'" src="@/assets/images/layout/고고커피-사업자-내용.png" alt=""/>
    <img v-if="loginCompanyCode === '10009'" src="@/assets/images/layout/고고커피-사업자-내용.png" alt=""/>
    <img v-if="loginCompanyCode === '10010'" src="@/assets/images/layout/고고커피-사업자-내용.png" alt=""/>
    <img v-if="loginCompanyCode === '10011'" src="@/assets/images/layout/고고커피-사업자-내용.png" alt=""/>
    <img v-if="loginCompanyCode === '10012'" src="@/assets/images/layout/고고커피-사업자-내용.png" alt=""/>
  </div>
</template>

<script>
// import store from '@/store'
export default {
  name: 'FooterView',
  data () {
    return {
      loginCompanyCode: this.$store.getters.getAccount.companyCode
    }
  },
  mounted () {
    this.loginCompanyCode = this.$store.getters.getAccount.companyCode
  }
}
</script>

<style scoped>

</style>
