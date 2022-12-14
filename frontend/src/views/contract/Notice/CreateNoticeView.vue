<template>
  <div class="content">
    <div class="m-2">
      <table class="">
        <tr class="flex text-center gap-2">
          <th class="w-28 rounded-md bg-[#808080] text-white py-1">공지그룹</th>
          <td class="w-28 relative rounded-md bg-[#C7B299] text-white py-1">
            <input type="checkbox" value="theWainning" id="더웨이닝" v-model="compCode" class="mr-3">
            <label for="더웨이닝" class="">더웨이닝</label>
          </td>
          <td class="w-28 relative rounded-md bg-[#C7B299] text-white py-1">
            <input type="checkbox" value="directing" id="디렉팅" v-model="compCode" class="mr-3">
            <label for="디렉팅" class="w-full">디렉팅</label>
          </td>
          <td class="w-28 relative rounded-md bg-[#C7B299] text-white py-1">
            <input type="checkbox" value="tote" id="토트" v-model="compCode" class="mr-3">
            <label for="토트" class="w-full">토트</label>
          </td>
          <td class="w-28 relative rounded-md bg-[#C7B299] text-white py-1">
            <input type="checkbox" value="wholeSale" id="도매" v-model="compCode" class="mr-3">
            <label for="도매" class="w-full">도매</label>
          </td>
          <td class="w-28 relative rounded-md bg-[#C7B299] text-white py-1">
            <input type="checkbox" value="retailSale" id="소매" v-model="compCode" class="mr-3">
            <label for="소매" class="w-full">소매</label>
          </td>
          <td class="flex ml-5">
              <select v-model="numseq" class="border w-28 numseq_selectBox">
                <option value="0" selected>우선순위</option>
                <option :value="i" v-for="i in 10" :key="i">{{i}}</option>
              </select>
          </td>
        </tr>
      </table>
    </div>
    <div class="m-2">
      <table class=" border w-full border">
        <tr class="border">
          <th class="w-28 border bg-[#C7B299]">제목</th>
          <td class="border" colspan="1"><input type="text" class="w-full" v-model="title"></td>
        </tr>
        <tr class="border">
          <th class="border h-40 bg-[#C7B299]">홈화면
          </th>
          <td class="border">
            <quill-editor
              class="ql_summary"
              toolbar="full"
              v-model:value="state.summary"
              :options="state.editorOption"
              :disabled="state.disabled"/>
          </td>
<!--          <td class="border w-20 p-0">-->
<!--            <input type="file" class="w-52 file" name="file" value="" accept="image/jpeg" @change="insertFile">-->
<!--          </td>-->
        </tr>
        <tr class="border">
          <th class="border bg-[#C7B299]">본문</th>
          <td class="border" colspan="1">
            <quill-editor
              class="ql_content"
              toolbar="full"
              v-model:value="state.content"
              :options="state.editorOption"
              :disabled="state.disabled"/>
          </td>
        </tr>
      </table>
    </div>
    <div class="text-right gap-2">
      <div>{{compCode}}</div>
      <div>{{port}}</div>
      <button class="h-8 border w-16 mr-2 rounded-md bg-gray-300" @click="$router.push(`/contract/notice/contractNotice/${currentPage}`)">목록으로</button>
      <button class="h-8 border w-20 p-1 rounded-md font-bold text-white text-sm bg-[#808080]" @click="createNotice">작성하기</button>
    </div>
  </div>
</template>

<script>
import { useRoute } from 'vue-router'
import axios from 'axios'
import router from '@/router'
import store from '@/store'

import Quill from 'quill'
import ImageUploader from 'quill-image-uploader'
import { reactive, ref } from 'vue'
import { quillEditor } from 'vue3-quill'
Quill.register('modules/imageUploader', ImageUploader)

export default {
  name: 'CreateNoticeView',

  data () {
    return {
      // currentPage: useRoute().params.currentPage,
      files_name: []
    }
  },
  methods: {
    // insertFile (file) {
    //   const uploadfile = file
    //   const formdata = new FormData()
    //   formdata.append('file', uploadfile)
    //   axios({
    //     method: 'post',
    //     url: '/api/notice/uploadFile',
    //     data: formdata
    //   }).then((res) => {
    //     console.log(res.data)
    //   })
    // }
  },
  setup () {
    const state = reactive({
      content: '',
      summary: '',
      editorOption: {
        placeholder: '내용을 입력하세요',
        modules: {
          imageUploader: {
            upload: (file) => {
              return new Promise((resolve, reject) => {
                const uploadfile = file
                const formdata = new FormData()
                formdata.append('file', uploadfile)
                axios({
                  method: 'post',
                  url: '/api/notice/uploadFile',
                  data: formdata,
                  params: {
                    port: port
                  }
                }).then((res) => {
                  resolve(res.data)
                })
              })
            }
          }
        }
      },
      disabled: false
    })

    const title = ref('')
    const seq = ref('')
    const summary = ref('')
    const numseq = ref(0)
    const compCode = ref(['theWainning'])
    const fileName = ref('')
    const noticeRes = ref([])
    const port = location.port
    const currentPage = ref(useRoute().params.currentPage)

    const createNotice = () => {
      // if (this.title === '') {
      //   alert('제목을 입력하세요')
      //   return
      // } else if (this.summary === '') {
      //   alert('홈화면의 내용을 입력하세요')
      //   return
      // } else if (this.content === '') {
      //   alert('본문의 내용을 입력하세요')
      //   return
      // }
      axios.post('/api/notice/createNotice', {
        params: {
          title: title.value,
          summary: state.summary,
          content: state.content,
          seq: seq.value,
          numseq: numseq.value,
          compcode: compCode.value[0],
          imgname: fileName.value,
          port: port,
          userId: store.state.account.userId
        }
      }).then((res) => {
        if (res.status === 200) {
          // this.noticeRes = res.data
          alert('글이 등록되었습니다.')
          console.log()
          router.push('/contract/notice/contractNotice/' + currentPage.value)
        }
      }).catch((err) => {
        console.log(err)
      })
    }

    const insertFile = (e) => {
      const formData = new FormData()
      for (const file of e.target.files) {
        formData.append('file', file)
      }
      axios.post('/api/notice/uploadMainFile', formData, { headers: { 'Content-Type': 'multipart/form-data' }, params: { port: port } })
        .then((res) => {
          fileName.value = res.data
        })
    }

    return {
      state,
      quillEditor,
      compCode,
      createNotice,
      title,
      seq,
      numseq,
      fileName,
      noticeRes,
      summary,
      insertFile,
      port,
      currentPage
    }
  }
}
</script>

<style scoped>
.click:hover{
  cursor: pointer;
}
.click{
  background-color: deepskyblue;
}
input[type=radio]:hover{
  background-color: #4b96e6;
  color: white;
}
.numseq_selectBox{
  text-align: center;
}
.ql_summary {
  height: 100px;
}
.ql_content {
  height: 250px;
}
</style>
