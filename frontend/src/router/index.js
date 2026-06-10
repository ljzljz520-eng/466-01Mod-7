import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import AboutView from '@/views/AboutView.vue'
import BookingView from '@/views/BookingView.vue'
import AppointmentsView from '@/views/AppointmentsView.vue'
import BudgetView from '@/views/BudgetView.vue'
import SettlementView from '@/views/SettlementView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/about',
    name: 'about',
    component: AboutView
  },
  {
    path: '/booking',
    name: 'booking',
    component: BookingView
  },
  {
    path: '/appointments',
    name: 'appointments',
    component: AppointmentsView
  },
  {
    path: '/budget',
    name: 'budget',
    component: BudgetView
  },
  {
    path: '/settlement',
    name: 'settlement',
    component: SettlementView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

export default router
