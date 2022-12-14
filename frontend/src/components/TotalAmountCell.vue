<template>
  <span>
    <span>{{cellValue}}</span>
  </span>
</template>

<script>
// import store from '@/store'

export default {
  name: 'TotalAmountCell',
  data: function () {
    return {
      cellValue: 0
    }
  },
  methods: {
    setTotalQty (qty, box, qtyPerBox) {
      // console.log(qty)
      // console.log(box)
      // console.log(qtyPerBox)
      // if (qty < 0) {
      //   qty = 0
      // }
      if (box < 0) {
        box = 0
      }
      if (qtyPerBox < 0 || qtyPerBox === null) {
        qtyPerBox = 1
      }
      const totalQty = parseInt(qty) + (parseInt(box) * parseInt(qtyPerBox))
      return totalQty
      // store.commit('setTotalQty', totalQty)
    }
  },
  computed: {
    // getTotalQty () {
    //   return this.$store.state.agGridReg[this.$store.state.nowStoreRowNum].totalQtyField
    // }
  },
  watch: {
    // getTotalQty (v) {
    //   this.cellValue = v
    // }
  },
  mounted () {
    this.cellValue = 0
  }
}
</script>

<style scoped>

</style>
