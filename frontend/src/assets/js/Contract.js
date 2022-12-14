export let contractLib
// //테이블 슬라이드 아코디언
// $(document).ready(function(){
//     $('.ContractList tr').click(function(){
//       var tr = $(this);
//       var hdetail = tr[0].id+"_hdetail";
//       var cdetail = tr[0].id+"_cdetail";
//
//       $('#'+hdetail).slideToggle("slow");
//       $('#'+cdetail).slideToggle("slow");
//     });
// });
//
//
// // 패스워드 모달
//
// $(document).ready(function(){
//     $("#btn_change_password").click(function(){
//         $("#modal_change_password").attr("style", "display:block");
//     });
//
//     $("#new_password_approval").click(function(){
//         $("#modal_change_password").attr("style", "display:none");
//     });
// });
//
// //데이트 피커
// $(document).ready(function(){
//     $(function(){
//         //input을 datepicker로 선언
//         $(".datepicker").datepicker({
//             dateFormat: 'yy-mm-dd' //달력 날짜 형태
//             ,showOtherMonths: true //빈 공간에 현재월의 앞뒤월의 날짜를 표시
//             ,showMonthAfterYear:true // 월- 년 순서가아닌 년도 - 월 순서
//             ,changeYear: true //option값 년 선택 가능
//             ,changeMonth: true //option값  월 선택 가능
//             ,showOn: "both" //button:버튼을 표시하고,버튼을 눌러야만 달력 표시 ^ both:버튼을 표시하고,버튼을 누르거나 input을 클릭하면 달력 표시
//             ,buttonImage: "http://jqueryui.com/resources/demos/datepicker/images/calendar.gif" //버튼 이미지 경로
//             // ,buttonImageOnly: true //버튼 이미지만 깔끔하게 보이게함
//             ,buttonText: "선택" //버튼 호버 텍스트
//             ,yearSuffix: "년" //달력의 년도 부분 뒤 텍스트
//             ,monthNamesShort: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'] //달력의 월 부분 텍스트
//             ,monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'] //달력의 월 부분 Tooltip
//             ,dayNamesMin: ['일','월','화','수','목','금','토'] //달력의 요일 텍스트
//             ,dayNames: ['일요일','월요일','화요일','수요일','목요일','금요일','토요일'] //달력의 요일 Tooltip
//             ,minDate: "-5Y" //최소 선택일자(-1D:하루전, -1M:한달전, -1Y:일년전)
//             ,maxDate: "+5y" //최대 선택일자(+1D:하루후, -1M:한달후, -1Y:일년후)
//         });
//
//         //초기값을 오늘 날짜로 설정해줘야 합니다.
//         $('#datepicker').datepicker('setDate', 'today'); //(-1D:하루전, -1M:한달전, -1Y:일년전), (+1D:하루후, -1M:한달후, -1Y:일년후)
//     })
// });
//
// // 반품 사유 모달
// $(document).ready(function(){
//     $("#return_goods_detail").click(function(){
//         $("#modal_return_detail").attr("style", "display:block");
//     });
//
//     $("#return_detail_confirm").click(function(){
//         $("#modal_return_detail").attr("style", "display:none");
//     });
// });
//
// //공지사항 모달
// $(document).ready(function(){
//     $(".notice_detail").click(function(){
//         $("#modal_notice_detail").attr("style", "display:block");
//     });
//
//     $("#notice_detail_confirm").click(function(){
//         $("#modal_notice_detail").attr("style", "display:none");
//     });
// });

const getTypeDiv = (type1) => {
  // console.log(type1[0].dcodename)
  const type1Div = []
  for (const i of type1) {
    const tmpArr = []
    tmpArr.dcode = i.dcode
    tmpArr.dcodename = i.dcodename
    type1Div.push(tmpArr)
  }
  return type1Div
}

const getTypeDivInfo = (type, typeInfo, type1, type2, type3, type2Now, type3Now, selectDiv) => {
  let resType1 = type1
  const tmpType2 = []
  const tmpType2se = []
  let resType2 = type2
  const tmpType3 = []
  const tmpType3se = []
  let resType3 = type3
  // 대분류를 선택한 경우
  if (selectDiv === 'select1') {
    for (const t of type1) {
      if (type === t.dcode) {
        resType1 = type1
        for (const ti of typeInfo) {
          if (ti.type1 === type) {
            tmpType2.push(ti)
          }
        }
        for (const tp2 of tmpType2) {
          for (const t2 of type2) {
            if (t2.dcode === tp2.type2) {
              tmpType2se.push(t2)
            }
          }
        }
        resType2 = tmpType2se.filter(function (item, index) {
          return tmpType2se.indexOf(item) === index
        })
      }
    }
  }
  // 중분류를 선택한 경우
  if (selectDiv === 'select2') {
    for (const t of type2) {
      if (type === t.dcode || type === '') {
        resType1 = type1
        resType2 = type2Now
        for (const ti of typeInfo) {
          if (ti.type2 === type) {
            tmpType3.push(ti)
          }
        }
        for (const tp3 of tmpType3) {
          for (const t3 of type3) {
            if (t3.dcode === tp3.type3) {
              tmpType3se.push(t3)
            }
          }
        }
        // resType3 = tmpType3se
        resType3 = tmpType3se.filter(function (item, index) {
          return tmpType3se.indexOf(item) === index
        })
      }
    }
  }

  // 소분류를 선택한 경우
  if (selectDiv === 'select3') {
    for (const t of type3) {
      if (type === t.dcode || type === '') {
        resType1 = type1
        resType2 = type2Now
        resType3 = type3Now
      }
    }
  }

  return {
    type1: resType1,
    type2: resType2,
    type3: resType3
  }
}

export { getTypeDiv, getTypeDivInfo }
