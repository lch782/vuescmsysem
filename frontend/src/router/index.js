import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView'
import ContractNoticeView from '@/views/contract/Notice/ContractNoticeView'
import SelectOrderDivision from '@/views/SelectOrderDivision'
import ContractHomeView from '@/views/contract/ContractHomeView'
import RecontractView from '@/views/contract/RecontractView'
import RegistContractView from '@/views/contract/RegistContractView'
import ContractBasketView from '@/views/contract/ContractBasketView'
import SearchContractListView from '@/views/contract/SearchContractList/SearchContractListView'
import SearchContractDeliveryListView from '@/views/contract/SearchContractDeliveryListView'
import OrderLoginView from '@/views/order/OrderLoginView'
import OrderHomeView from '@/views/order/OrderHomeView'
import CheckOrderView from '@/views/order/CheckOrderView'
import RegistDeliveryView from '@/views/order/RegistDeliveryView'
import ConfirmDeliveryView from '@/views/order/ConfirmDeliveryView'
import TradingStatementView from '@/views/order/TradingStatementView'
import SalesPerformanceView from '@/views/order/SalesPerformanceView'
import DepositHistoryView from '@/views/contract/SearchContractList/DepositHistoryView'
import TaxBillViewContractDate from '@/views/contract/SearchContractList/TaxBillContractDateView'
import CreateNoticeView from '@/views/contract/Notice/CreateNoticeView'
import UpdateNoticeView from '@/views/contract/Notice/UpdateNoticeView'
import NoticeDetailView from '@/views/contract/Notice/NoticeDetailView'
import PurchaseOrder from '@/components/popup/PurchaseOrder'
import TradingStatement from '@/components/popup/TradingStatement'
import test from '@/views/contract/Notice/test'
import TaxBillPublishedDateView from '@/views/contract/SearchContractList/TaxBillPublishedDateView'
import ContractLoginView from '@/views/contract/ContractLoginView'

const routes = [
  {
    path: '/',
    name: 'home',
    component: LoginView
  },
  {
    path: '/selectDivision',
    name: 'SelectDivisionView',
    component: () => import(/* webpackChunkName: "about" */ '../views/SelectDivisionView.vue')
  },
  {
    path: '/selectOrderDivision',
    name: 'SelectOrderDivision',
    component: SelectOrderDivision
  },
  {
    path: '/home',
    name: 'homeTest',
    component: () => import(/* webpackChunkName: "about" */ '../views/HomeView.vue')
  },
  {
    path: '/contract/login',
    name: 'ContractLoginView',
    component: ContractLoginView
  },
  {
    path: '/contract',
    name: 'contract',
    component: () => import(/* webpackChunkName: "about" */ '../views/contract/Contract'),
    children: [
      {
        path: 'contractHome',
        name: 'ContractHomeView',
        component: ContractHomeView
      },
      {
        path: 'recontract',
        name: 'RecontractView',
        component: RecontractView
      },
      {
        path: 'registContract',
        name: 'RegistContractView',
        component: RegistContractView
      },
      {
        path: 'contractBasket',
        name: 'ContractBasketView',
        component: ContractBasketView
      },
      {
        path: 'searchContractList',
        name: 'SearchContractListView',
        component: SearchContractListView
      },
      {
        path: 'depositHistoryView',
        name: 'DepositHistoryView',
        component: DepositHistoryView
      },
      {
        path: 'taxBillViewContractDate',
        name: 'TaxBillViewContractDate',
        component: TaxBillViewContractDate
      },
      {
        path: 'taxBillPublishedDate',
        name: 'TaxBillPublishedDate',
        component: TaxBillPublishedDateView
      },
      {
        path: 'searchContractDeliveryList',
        name: 'SearchContractDeliveryListView',
        component: SearchContractDeliveryListView
      },
      {
        path: 'notice/contractNotice/:currentPage',
        name: 'ContractNoticeView',
        component: ContractNoticeView,
        props: true
      },
      {
        path: 'notice/createNotice/:currentPage',
        name: 'CreateNoticeView',
        component: CreateNoticeView,
        props: true
      },
      {
        path: 'notice/updateNotice/:currentPage/:seq',
        name: 'UpdateNoticeView',
        component: UpdateNoticeView,
        props: true
      },
      {
        path: 'notice/noticeDetailView/:seq',
        name: 'NoticeDetailView',
        component: NoticeDetailView,
        props: true
      },
      {
        path: 'notice/test',
        name: 'test',
        component: test
      }
    ]
  },
  {
    path: '/order/selectCompany',
    name: '/SelectCompanyView',
    component: () => import(/* webpackChunkName: "about" */ '../views/order/SelectCompanyView')
  },
  {
    path: '/order/login',
    name: 'OrderLoginView',
    component: OrderLoginView
  },
  {
    path: '/order',
    name: 'order',
    component: () => import(/* webpackChunkName: "about" */ '../views/order/Order'),
    children: [
      {
        path: 'orderHome',
        name: 'OrderHomeView',
        component: OrderHomeView
      },
      {
        path: 'checkOrder',
        name: 'CheckOrderView',
        component: CheckOrderView
      },
      {
        path: 'registDelivery',
        name: 'RegistDeliveryView',
        component: RegistDeliveryView
      },
      {
        path: 'confirmDelivery',
        name: 'ConfirmDeliveryView',
        component: ConfirmDeliveryView
      },
      {
        path: 'tradingStatement',
        name: 'TradingStatementView',
        component: TradingStatementView
      },
      {
        path: 'salesPerformance',
        name: 'SalesPerformanceView',
        component: SalesPerformanceView
      }
    ]
  },
  {
    path: '/testPopup',
    name: 'testPopup',
    children: [
      {
        path: 'purchaseOrder',
        name: 'PurchaseOrder',
        component: PurchaseOrder
      },
      {
        path: 'tradingStatement',
        name: 'TradingStatement',
        component: TradingStatement
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
