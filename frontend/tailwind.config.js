/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        'login_box' : '#3E3D3C',
        'id_password' : '#999999',
        'bottom_logo' : '#595957',
        'logout_btn' : '#333333',
        'screen_back_ground' : '#F2F2F2',
        'category_box' : '#E6E6E6',
        'category_hover' : '#B3B3B3',
        'small_category_hover' : '#cccccc',
        'bottom_logo_bizText' : '#808080',
        'header_welcome' : '#999999',
        'category_text' : '#333333',
        'content_text_box_bg' : '#E6E6E6',
        'content_text_box_border' : '#B6B6B6',
        'date_preset_button' : '#999999',
        'date_preset_button_border' : '#333333',
        'basket_title' : '#0071DC',
        'delete_selected_btn' : '#4d4d4d',
        'request_selected_Item' : '#808080',
      //  장바구니 화면
        'basket_text_bottom_normal' : '#666666',
        'basket_text_bottom_won' : '#000000',
        'basket_text_bottom_contractAmount' : '#333333',
        'basket_text_bottom_number' : '#29ABE2',
        'basket_text_bottom_number_total' : '#ED1C24',
        'basket_bottom_box_bg' : '#F2F2F2',
        'basket_request_select_item' : '#808080',
      // 주문하기 화면
        'regist_third_category_button_bg' : '#B3B3B3',
        'regist_third_category_button_border' : '#333333'
      },
      height: {
        'loginBtm' : '7.5rem'
      },
      width: {
        'search_box' : '38.4rem',
        'delivery_field' : '46rem',
        'sms_field' : '20rem',
        'basket_bottom_text' : '30rem',
        '120' : '30rem'
      },
      height: {
        'home_notice' : '30rem'
      },
      fontFamily: {
        'noto_santos_r' : ['NotoSansKR-Regular'],
        'noto_santos_b' : ['NotoSansKR-Bold'],
        'noto_santos_l' : ['NotoSansKR-Light'],
        'noto_santos_m' : ['NotoSansKR-Medium']
      },
      fontWeight: {
        medium_bold: 600,
      }
    },
  },
  plugins: [],
}
