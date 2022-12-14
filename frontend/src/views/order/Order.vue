<template>
  <div class="flex justify-center">
    <div class="wrap">
      <Header/>
      <div class="flex">
<!--        <LeftMenu/>-->
        <div class="content_order mx-auto">
          <router-view/>
        </div>
<!--        <RightMenu/>-->
      </div>
    </div>
<!--    <Footer/>-->
  </div>
</template>

<script>
import Header from '@/components/layouts/order/HeaderOrder'
// import LeftMenu from '@/components/layouts/order/LeftMenuOrder'
// import RightMenu from '@/components/layouts/order/RightMenuOrder'
// import Footer from '@/components/layouts/order/FooterOrder'
export default {
  name: 'Order',
  components: { Header }
}
</script>

<style scoped>

</style>
