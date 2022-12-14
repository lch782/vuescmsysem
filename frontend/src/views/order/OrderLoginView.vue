<template>
  <div class="w-full h-screen">
    <div class="flex w-full h-full justify-center items-center">
      <div>
        <div class="flex justify-center">
          <img class="mb-10" src="@/assets/images/login/GNS-로고.png" alt="로고" width="176"/>
        </div>
        <div class="flex">
          <div class="">
            <div>
              <input type="text" class="border border-id_password w-52 h-14 mb-2" placeholder="아이디" v-model="state.form.userId"/>
            </div>
            <div>
              <input type="password" class="border border-id_password w-52 h-14" placeholder="비밀번호" v-model="state.form.password"/>
            </div>
          </div>
          <div>
            <button @click="submit()" class="w-32 h-30 ml-2 bg-login_box text-white font-semibold text-lg">Login</button>
          </div>
        </div>
        <div class="text-id_password font-noto_santos_r font_letter_spc font_word_spc">
          <div class="flex my-3">
            <p>아이디 기억하기</p>
            <input class="mx-2 login_checkBox" type="checkbox" />
          </div>
          <div class="flex">
            <p>아이디 / 비밀번호 분실 문의<strong> 051-714-7353</strong></p>
          </div>
        </div>
      </div>
    </div>
    <div class="login_footer">
      <div class="flex flex justify-center items-center">
        <img class="mx-6" src="@/assets/images/login/더웨이닝커피-하단.png" alt="" width="150" />
        <img class="mx-6" src="@/assets/images/login/고고커피-하단.png" alt="" width="80" />
        <img class="mx-6" src="@/assets/images/login/로스팅웨어-하단.png" alt="" width="150" />
        <img class="mx-6" src="@/assets/images/login/브루잉토트-하단.png" alt="" width="150" />
        <img class="mx-6" src="@/assets/images/login/바이킹컴퍼니-하단.png" alt="" width="150" />
        <img class="mx-6" src="@/assets/images/login/아이엠-디자인-하단.png" alt="" width="80" />
        <img class="mx-6" src="@/assets/images/login/성륜-하단.png" alt="" width="150" />
      </div>
    </div>
  </div>
</template>

<script>
import { reactive } from 'vue'
import axios from 'axios'
import store from '@/store'
import router from '@/router'

export default {
  name: 'LoginView',
  setup () {
    const state = reactive({
      form: {
        userId: '',
        password: ''
      }
    })
    const submit = () => {
      console.log(state.form.userId)
      axios.post('/api/account/login', state.form).then((res) => {
        store.commit('setAccount', res.data)
        login(res.data)
      }).catch(() => {
        window.alert('로그인 정보가 존재하지 않습니다.')
      })
    }
    function login (logInfo) {
      const custType = logInfo.custType

      if (custType === '1') {
        router.push({ path: '/order/orderHome' })
      } else if (custType === '2') {
        alert('로그인 주소를 확인해 주십시오')
      } else if (custType === '3') {
        router.push({ path: '/order/orderHome' })
      }
    }
    return {
      state,
      submit
    }
  }
}
</script>

<style scoped>

</style>
