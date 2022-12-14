<template>
  <div></div>
</template>

<script>
import axios from 'axios'
// import store from '@/store'

export default {
  name: 'CommonFunc',
  methods: {
    // 총 갯수를 넣으면 낱개와 박스로 환산해 주는 함수
    getTotalToQtyBox (itemCode, totalQty, qtyPerBox, isBasket) {
      if (qtyPerBox) {
        const qty = parseInt(totalQty) % parseInt(qtyPerBox)
        let box = parseInt(totalQty) / parseInt(qtyPerBox)
        box = Math.floor(box)
        if (isBasket === true) {
          // store.commit('setQtyField', qty)
          // store.commit('setBoxField', box)
        }
        return {
          qty: qty,
          box: box
        }
      } else {
        axios.get('/api/contract/getTotalToQtyBox', {
          params: {
            itemCode: itemCode,
            totalQty: parseInt(totalQty)
          }
        })
          .then((res) => {
            console.log(res)
          })
      }
    },
    // 낱개와 박스에 따라서 총갯수를 계산해 준다.
    setTotalQty (qty, box, qtyPerBox) {
      if (qtyPerBox < 0 || qtyPerBox === null) {
        qtyPerBox = 1
      }
      const resQty = parseInt(qty) % parseInt(qtyPerBox)
      const resBox = Math.floor((parseInt(qty) / parseInt(qtyPerBox)) + parseInt(box))
      const totalQty = parseInt(qty) + (parseInt(box) * parseInt(qtyPerBox))

      return {
        resQty: resQty,
        resBox: resBox,
        totalQty: totalQty
      }
    }
  }
}
</script>

<style scoped>

</style>
