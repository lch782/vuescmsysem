function getParameter(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

const chart_gubun = getParameter("chart");

let category_selected = null;
let data_selected = null;
let title_selected = null;

const category_month = [
    '01/01/2020',
    '02/01/2020',
    '03/01/2020',
    '04/01/2020',
    '05/01/2020',
    '06/01/2020',
    '07/01/2020',
    '08/01/2020',
    '09/01/2020',
    '10/01/2020',
    '11/01/2020',
    '12/01/2020',
];

const category_week = [
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
];

const category_day = [
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/10/03',
    '2022/09/30',
    '2022/10/01',
    '2022/10/02',
    '2022/10/03',
];

const data_month = [470000, 395000, 488000, 519000, 378000, 644000, 727374, 556123, 625500, 801220, 677600, 422345];
const data_week = [47000, 1200, 77800, 23900, 88800, 51400, 42737, 35612, 82550, 60120, 77700, 52245];
const data_day = [4700, 39500, 0, 56743, 0, 69000, 320000, 120000, 0, 0, 67760, 42345];

const title_month = '월별 주문 금액';
const title_week = '주별 주문 금액';
const title_day = '일별 주문 금액';

if (chart_gubun === 'm'){
    category_selected = category_month;
    data_selected = data_month;
    title_selected = title_month;
}
else if (chart_gubun === 'w'){
    category_selected = category_week;
    data_selected = data_week;
    title_selected = title_week;
}
else if (chart_gubun === 'd'){
    category_selected = category_day;
    data_selected = data_day;
    title_selected = title_day;
}
else {
    category_selected = category_month;
    data_selected = data_month;
    title_selected = title_month;
}




// 라인 차트 예시
const Chart_month = toastui.Chart;
const el = document.getElementById('chart_area');
const data = {
    categories: category_selected,
    series: [
        // {
        //     name: '콩커피',
        //     data: [-3.5, -1.1, 4.0, 11.3, 17.5, 21.5, 25.9, 27.2, 24.4, 13.9, 6.6, -0.6],
        // },
        // {
        //     name: '이디야커피',
        //     data: [3.8, 5.6, 7.0, 9.1, 12.4, 15.3, 17.5, 17.8, 15.0, 10.6, 6.6, 3.7],
        // },
        // {
        //     name: '스타벅스',
        //     data: [22.1, 22.0, 20.9, 18.3, 15.2, 12.8, 11.8, 13.0, 15.2, 17.6, 19.4, 21.2],
        // },
        {
            name: '커피빈',
            data: data_selected,
        },
        // {
        //     name: '빽다방',
        //     data: [-13.2, -13.7, -13.1, -10.3, -6.1, -3.2, 0.0, -0.1, -1.8, -4.5, -9.0, -10.9],
        // },
    ],
};
const options = {
    chart: { title: title_selected, width: 1000, height: 500},
    xAxis: {
        title: 'Month',
    },
    yAxis: {
        title: 'Amount',
    },
    tooltip: {
        formatter: (value) => `${value}원`,
    },
    legend: {
        align: 'bottom',
    },
};

const chart_m = Chart_month.lineChart({ el, data, options });

