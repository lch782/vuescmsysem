var ButtonRenderer = function(props) { // 버튼 생성하는 Renderer
  var el = document.createElement('div');
  var {rowKey,value} = props; // props 에서 rowKey라는 키와 일치하는 것의 Value 추출

  if (value === '배송완료') {
    el.innerHTML = '<span class="font-bold text-red-600">배송완료</span><button class="w-10 bg-red-600 text-white mx-2">반품<button>';
  }
  else {
    el.innerHTML = '<span class="font-bold text-red-600">'+value+'</span>';
  }
  el.addEventListener('click', (ev) => {
     if (ev.target.nodeName === 'BUTTON') { // 클릭 이벤트가 발생한 지점이 Button일때
        $("#modal_request_return").attr("style", "display:block");
        /*
         var record = grid.getRow(rowKey);
         grid2.removeRow(rowKey);
         document.getElementsByClassName('grid1-textfield-'+rowKey)[0].value = ''; // Input Text 초기화
         grid.setValue(rowKey,'OrderQuantity',''); // Input Text 초기화 이후 값이 남아 기존 데이터에서도 초기화
         document.getElementsByClassName('grid1-checkbox-'+rowKey)[0].disabled = false; // 체크박스 disabled 해제
         document.getElementsByClassName('grid1-checkbox-'+rowKey)[0].checked = false; // 체크박스 체크 해제
         document.getElementsByClassName('btn_order')[0].parentNode.classList.add('hidden');
         */
         /* 반품 요청에 대한 펑션
         grid.on('click', (ev) => {
             const {rowKey, columnName} = ev;
             var res = grid.getValue(rowKey, columnName);
             if (res === '배송완료'){
                 $("#modal_request_return").attr("style", "display:block");
             }
         });
         */
     }
  });
  this.el = el;
  this.render(props);
}

ButtonRenderer.prototype.getElement = function () {
	return this.el;
}

ButtonRenderer.prototype.render = function (props) {
	this.el.value = props.value;
}

var gridData = [
    {
        ItemCode : 'C2831001',
        OrderDate:'2022-09-28',
        OrderNo:'A22092831001',
        ItemName : '아라비카',
        Price:'12500',
        Quantity:'2',
        TotalPrice:'25,000',
        OrderState:'입금완료',
        _children : [
        ]
    },
    {
        ItemCode : 'C2831002',
        OrderDate:'2022-09-28',
        OrderNo:'A22092831002',
        ItemName : '카네포라',
        Price:'8500',
        Quantity:'1',
        TotalPrice:'8500',
        OrderState:'배송준비중',
        _children : [
        ]
    },
    {
        ItemCode : 'C2831003',
        OrderDate:'2022-09-28',
        OrderNo:'A22092831003',
        ItemName : '리베리카',
        Price:'15000',
        Quantity:'3',
        TotalPrice: '45000',
        OrderState:'배송완료',
        _children : [
        ]
    },
    {
        ItemCode : 'C2831004',
        OrderDate:'2022-09-29',
        OrderNo:'A22092931004',
        ItemName : '산토스',
        Price:'11000',
        Quantity:'7',
        TotalPrice: '77000',
        OrderState:'반품',
        _children : [
        ]
    },
    {
        ItemCode : 'C2831005',
        OrderDate:'2022-09-29',
        OrderNo:'A22092931005',
        ItemName : 'AA',
        Price:'13000',
        Quantity:'5',
        TotalPrice: '65000',
        OrderState:'배송완료',
        _children : [
        ]
    },
]
const grid = new tui.Grid({
  el: document.getElementById('contractgrid'),
  data: gridData,
  bodyHeight : 600,
  columns: [
    {
      header: '주문일자', // 그리드에 보이는 이름
      name: 'OrderDate',  // 필드 이름
      align: 'center' // 정렬
    },
    {
      header: '주문번호',
      name: 'OrderNo',
      align: 'center'
    },
    {
      header: '상품명',
      name: 'ItemName',
      align: 'center'
    },
    {
      header: '단가',
      name: 'Price',
      align: 'right',
      formatter: function(value) { // 포매터(금액의 3자리수마다 ,을 찍기위해 선언)
        var deposit = value.value;
        var result = deposit.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        return result;
      }
    },
    {
      header: '수량',
      name: 'Quantity',
      align: 'right',
      formatter: function(value) { // 포매터(금액의 3자리수마다 ,을 찍기위해 선언)
        var deposit = value.value;
        var result = deposit.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        return result;
      }
    },
    {
      header: '합계',
      name: 'TotalPrice',
      align: 'right',
      formatter: function(value) { // 포매터(금액의 3자리수마다 ,을 찍기위해 선언)
        var deposit = value.value;
        var result = deposit.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        return result;
      }
    },
    {
      header: '주문상태',
      name: 'OrderState',
      align:'center',
      renderer : {
        type: ButtonRenderer,
      }
    }
  ],
  treeColumnOptions: {
    name : 'OrderDate',
    useIcon : false
  },
  summary: {
    height:40,
    position:'bottom',
    columnContent: {
        TotalPrice: {
            template: function(valueMap) {
                return '합계 : 148,500';
            }
        }
    }
  },
  showDummyRows : true
});

 // 그리드에 테마 및 셀 스타일 부여
tui.Grid.applyTheme('stripe', {
  cell: {
    normal: {
      background: '#fff',
      border: '#e0e0e0',
      showVerticalBorder: true,
      showHorizontalBorder: false
    },
    header: {
      background: '#FFF7ED',
      border: '#e0e0e0'
    },
    selectedHeader: {
      background: '#e0e0e0'
    },
    evenRow: {
      background: '#fee'
    }
  }
});

grid.on('expand', (ev) => {
  const {rowKey} = ev; // 이벤트가 일어난 요소 rowKey값 저장
  const descendantRows = grid.getDescendantRows(rowKey);
  var record = grid.getRow(rowKey); // rowKey에 해당하는 row의 데이터
  if (descendantRows.length < 1) { // 하위 요소가 없을때(이거 안하면 무한루프)
      var treeData = {
        OrderDate : '상품코드',
        OrderNo:'중량',
        ItemName : '원산지',
        Price:'',
        Quantity:'',
        TotalPrice:'',
        OrderState:'',
        _attributes: {
            expanded: true
        },
        _children : [
            {
                OrderDate : record.ItemCode,
                OrderNo:'200g',
                ItemName : '에티오피아',
                Price:'',
                Quantity:'',
                TotalPrice:'',
                OrderState:'',
            }
        ]
      }
      const options = { // optional
        parentRowKey: rowKey, // 하위 요소가 들어갈 부모 rowKey
      };
      grid.appendTreeRow(treeData, options);
  }
});

// 반품 요청 모달
$(document).ready(function(){
    $("#request_return_confirm").click(function(){
        $("#modal_request_return").attr("style", "display:none");
    });
});