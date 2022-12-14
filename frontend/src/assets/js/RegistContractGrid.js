var TextInputRenderer = function(props) { // Textfield 커스텀 렌더러로 Border 설정
  var el = document.createElement('input');
  var {rowKey} = props; // props 에서 rowKey라는 키와 일치하는 것의 Value 추출
  el.type = 'text';

  el.className = 'grid1-textfield-'+rowKey;
  el.style.width = 'calc(100% - 10px)';
  el.style.padding = '6px 7px';
  el.style.border = 'solid 1px #ddd';
  el.style.margin = 'auto 5px';

  this.el = el;
  this.render(props);
}

TextInputRenderer.prototype.getElement = function () {
	return this.el;
}

TextInputRenderer.prototype.render = function (props) {
	this.el.value = props.value;
}

var CheckboxRenderer = function(props) {
  var el = document.createElement('input');
  var {rowKey} = props; // props 에서 rowKey라는 키와 일치하는 것의 Value 추출
  el.type = 'checkbox';
  el.className = 'grid1-checkbox-'+rowKey; // 후에 취소버튼을 눌렀을때 이벤트를 위해서 클래스 정의

  el.addEventListener('change', () => { // 체크박스의 상태가 변했을때
     var record = grid.getRow(rowKey);
     if (el.checked) {
        if (record.OrderQuantity === '' || record.OrderQuantity === '0') { // 수량이 없거나 0일경우
            alert('주문수량을 입력해주세요.');
            el.checked = false;
            document.getElementsByClassName('grid1-textfield-'+rowKey)[0].focus();
        }
        else { // 수량을 입력했을 경우
            el.disabled = true;
            var selectdata =
                {
                    Idx : rowKey,
                    ItemName: record.ItemName,
                    Standard: record.Standard,
                    Unit : record.Unit,
                    Price: record.Price,
                    Weight: record.Weight,
                    OrderQuantity: record.OrderQuantity,
                    OrderPrice : record.Price * record.OrderQuantity
                }
            ;
            grid2.appendRow(selectdata,0);
        }
     }
  });
  this.el = el;
  this.render(props);
}

CheckboxRenderer.prototype.getElement = function () {
	return this.el;
}

CheckboxRenderer.prototype.render = function (props) {
	this.el.value = props.value;
}

var ButtonRenderer = function(props) { // 버튼 생성하는 Renderer
  var el = document.createElement('div');
  var {rowKey} = props; // props 에서 rowKey라는 키와 일치하는 것의 Value 추출

  el.innerHTML = '<button class="w-20 bg-red-600 text-white">취소</button>';

  el.addEventListener('click', (ev) => {
     if (ev.target.nodeName === 'BUTTON') { // 클릭 이벤트가 발생한 지점이 Button일때
         var record = grid.getRow(rowKey);
         grid2.removeRow(rowKey);
         document.getElementsByClassName('grid1-textfield-'+rowKey)[0].value = ''; // Input Text 초기화
         grid.setValue(rowKey,'OrderQuantity',''); // Input Text 초기화 이후 값이 남아 기존 데이터에서도 초기화
         document.getElementsByClassName('grid1-checkbox-'+rowKey)[0].disabled = false; // 체크박스 disabled 해제
         document.getElementsByClassName('grid1-checkbox-'+rowKey)[0].checked = false; // 체크박스 체크 해제
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
        ItemName: '코코아',
        Standard: '제주도산',
        Unit : 'box',
        Price: '32000',
        Weight: '500g',
        Quantity:'5670',
        OrderQuantity:'',
        OrderOk:''
    },
    {
        ItemName: '최고급 원두',
        Standard: '시마네현',
        Unit : 'box',
        Price: '79000',
        Weight: '600g',
        Quantity:'430',
        OrderQuantity:'',
        OrderOk:''
    },
]
const grid = new tui.Grid({
  el: document.getElementById('registcontractgrid'),
  data: gridData,
  bodyHeight : 300,
  columns: [
    {
      header: '상품명',
      name: 'ItemName',
      align: 'center'
    },
    {
      header: '규격',
      name: 'Standard',
      align: 'center'
    },
    {
      header: '단위',
      name: 'Unit',
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
      header: '단위당 중량',
      name: 'Weight',
      align: 'center'
    },
    {
      header: '재고수량',
      name: 'Quantity',
      align: 'right',
      formatter: function(value) { // 포매터(금액의 3자리수마다 ,을 찍기위해 선언)
        var deposit = value.value;
        var result = deposit.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        return result;
      }
    },
    {
      header: '주문수량',
      name: 'OrderQuantity',
      editor:'text',
      renderer: {
        type: TextInputRenderer
      }
    },
    {
      header: '주문확인',
      name: 'OrderOk',
      align: 'center',
      renderer: {
        type: CheckboxRenderer
      }
    }
  ],
  showDummyRows : true,
  editingEvent: 'click'
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


const grid2 = new tui.Grid({
  el: document.getElementById('registcontractgrid2'),
  bodyHeight : 300,
  columns: [
    {
      header: '상품명',
      name: 'ItemName',
      align: 'center'
    },
    {
      header: '규격',
      name: 'Standard',
      align: 'center'
    },
    {
      header: '단위',
      name: 'Unit',
      align: 'center'
    },
    {
      header: '단가',
      name: 'Price',
      align: 'right',
      formatter: function(value) { // 포매터(금액의 3자리수마다 ,을 찍기위해 선언)
        var price = value.value;
        var result = price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        return result;
      }
    },
    {
      header: '단위당 중량',
      name: 'Weight',
      align: 'center'
    },
    {
      header: '주문수량',
      name: 'OrderQuantity',
      align: 'center'
    },
    {
      header: '주문금액',
      name: 'OrderPrice',
      align: 'right',
      formatter: function(value) { // 포매터(금액의 3자리수마다 ,을 찍기위해 선언)
        var deposit = value.value;
        var result = deposit.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        return result;
      }
    },
    {
      header: '취소',
      name: 'OrderCancel',
      align: 'center',
      renderer: {
        type: ButtonRenderer
      }
    }
  ],
  summary: {
    height:40,
    position:'bottom',
    columnContent: {
        OrderPrice: {
              template: function(valueMap) {
                  return '합계 : ' + valueMap.sum.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
              }
        },
        OrderCancel : {
          template: function(valueMap) {
              return '<button class="btn_order h-8 w-20 rounded-md bg-green-400 text-white">주문</button>';
          }
        }
      }
    },
  showDummyRows : true,
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

var el = document.getElementsByClassName('btn_order')[0];
  el.addEventListener('click', (ev) => {
  if (grid2.getRowCount() < 1) {
     alert('주문할 물품을 선택해주세요.');
   }
  });
document.getElementsByClassName('btn_order')[0].parentNode.style.textAlign = "center";