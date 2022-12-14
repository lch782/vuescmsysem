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
    <button type="button" class="h-8 border w-16 rounded-md bg-blue-300" @click="updateNotice">수정하기</button>
    <button class="h-8 border w-16 ml-2 rounded-md bg-gray-300"
            @click="$router.push('/contract/notice/noticeDetailView/' + currentPage + '/' + seq)">취소하기
    </button>
  </div>
  </div>
</template>

<script>
import { useRoute } from 'vue-router'
import axios from 'axios'
import { onMounted, reactive, ref } from 'vue'
import { quillEditor } from 'vue3-quill'
import router from '@/router'
import store from '@/store'

export default {
  name: 'CreateNoticeView',
  data () {
    return {
      isClick: false,

      myPlugins: 'image',
      myToolbar: 'fontfamily fontsize | bold italic underline | forecolor backcolor | alignleft aligncenter alignright alignjustify | image | undo redo',
      myInit: { automatic_uploads: false, images_upload_url: '/api/upload-image', images_reuse_filename: true, height: 300, menubar: false, resize: false, skin: 'outside' },

      files_name: reactive([])
    }
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
                  data: formdata
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

    // const route = useRoute()
    const noticeContent = ref([])
    const title = ref('')
    const seq = useRoute().params.seq
    const summary = ref('')
    const numseq = ref('')
    const compCode = ref([])
    const filesName = ref('')
    const selqty = ref()
    const currentPage = store.getters.getNoticeNowPage

    onMounted(() => {
      axios.get('/api/notice/getNoticeContent', {
        params: {
          seq: seq
        }
      }).then((res) => {
        noticeContent.value = res.data
        console.log(noticeContent.value)
        title.value = noticeContent.value[0].title
        state.summary = noticeContent.value[0].summary
        state.content = noticeContent.value[0].contents
        numseq.value = noticeContent.value[0].numseq
        filesName.value = noticeContent.value[0].imgname
        compCode.value.push(noticeContent.value[0].compcode)
        selqty.value = noticeContent.value[0].selqty
      })
    })

    const updateNotice = () => {
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

      axios.patch('/api/notice/updateNoticeContent', {
        params: {
          title: title.value,
          summary: state.summary,
          content: state.content,
          seq: seq,
          numseq: numseq.value,
          compcode: compCode.value[0],
          imgname: filesName.value,
          selqty: selqty.value
        }
      }).then((res) => {
        if (res.status === 200) {
          alert('글이 등록되었습니다.')
          router.push(`/contract/notice/noticeDetailView/${seq}`)
        }
      }).catch((err) => {
        console.log(err)
      })
    }

    return {
      state,
      quillEditor,
      compCode,
      title,
      seq,
      currentPage,
      numseq,
      filesName,
      summary,
      noticeContent,
      updateNotice
    }
  },
  methods: {
    insertFile (e) {
      const formData = new FormData()
      for (const file of e.target.files) {
        formData.append('file', file)
      }

      axios.post('/api/notice/uploadFile'
        , formData
        , { headers: { 'Content-Type': 'multipart/form-data' } })
        .then((res) => {
          this.files_name = res.data
        })
    }
  }
}
</script>

<style scoped>
.click {
  background-color: deepskyblue;
}
.ql_summary {
  height: 100px;
}
.ql_content {
  height: 250px;
}
</style>
