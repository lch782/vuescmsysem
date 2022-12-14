var gridData = [
  {
    OrderNo: 'A22092831001',
    DepositDate: '2022-09-28',
    DepositAmount: '25000',
    DepositState: '입금'
  },
  {
    OrderNo: 'A22092831002',
    DepositDate: '2022-09-28',
    DepositAmount: '8500',
    DepositState: '입금'
  },
  {
    OrderNo: 'A22092831003',
    DepositDate: '2022-09-28',
    DepositAmount: '45000',
    DepositState: '입금'
  },
  {
    OrderNo: 'A22092931005',
    DepositDate: '2022-09-29',
    DepositAmount: '65000',
    DepositState: '입금'
  },
]

const grid = new tui.Grid({
  el: document.getElementById('depositgrid'),
  data: gridData,
  columns: [
    {
      header: '주문번호', // 그리드에 보이는 이름
      name: 'OrderNo',  // 필드 이름
      align: 'center' // 정렬
    },
    {
      header: '입금일자',
      name: 'DepositDate',
      align: 'center'
    },
    {
      header: '입금금액',
      name: 'DepositAmount',
      align: 'right',
      formatter: function(value) { // 포매터(금액의 3자리수마다 ,을 찍기위해 선언)
        var deposit = value.value;
        var result = deposit.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        return result;
      }
    },
    {
      header: '입금상태',
      name: 'DepositState',
      align: 'center',
      renderer : { // 해당 필드에 스타일 주기위해 선언
        styles: {
          fontWeight: 'bold',
          color : '#FF0000'
        },
      }
    }
  ],
  summary: {
    height:40,
    position:'bottom',
    columnContent: {
      DepositAmount: {
        template: function(valueMap) {
          return '합계 : 148,500';
        }
      }
    }
  }
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

export default test

/*
// 주문번호 클릭시 주문내역을 보여준다.
grid.on('click', (ev) => {
  const {rowKey, columnName} = ev;
  var res = grid.getValue(rowKey, columnName);
  if (columnName === 'OrderNo' && rowKey >= 0){
      $("#modal_search_contract").attr("style", "display:block");
  }
});
*/


