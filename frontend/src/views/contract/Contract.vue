<template>
  <div class="">
    <div class="full_page bg-screen_back_ground">
      <HeaderView/>
      <div class="wrap_contract flex justify-center">
        <LeftMenuView class=""/>
        <router-view class="border-t border-category_hover"/>
      </div>
      <FooterView/>
    </div>
  </div>
</template>

<script>
import LeftMenuView from '@/components/layouts/contract/LeftMenuView'
import HeaderView from '@/components/layouts/contract/HeaderView'
import FooterView from '@/components/layouts/contract/FooterView'
// import RightMenu from '@/components/layouts/contract/RightMenu'

export default {
  name: 'Contract',
  components: { FooterView, HeaderView, LeftMenuView }
}
</script>

<style scoped>

</style>
