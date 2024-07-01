import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/RegisterView.vue'
import Coco from '@/views/CocoView.vue'
import Factory from '@/views/FactoryView.vue'
import FactoryReview from '@/views/FactoryReviewView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/register',
      name: 'register',
      component: Register
    },
    {
      path: '/chocolates/:cocoId/:name', // Dodan ":" prije cocoId
      name: 'cocoUpdate',
      component: Coco
    },
    {
      path: '/chocolates',
      name: 'chocolates', // Ispravljeno ime rute
      component: Coco
    },
    {
      path: '/chocolates/factories/:factoryId/:name', // ":" za factoryId i ":name"
      name: 'cocoCreate',
      component: Coco
    },    
    {
      path: '/factories/:id', // Dodajte ":id" da biste očekivali parametar ID
      name: 'factoryReview',
      component: FactoryReview,
      props: true // Proslijedite parametre kao props
    },
    {
      path: '/chocolates/add', // Dodan ":" prije factoryId
      name: 'cocoSave',
      component: Login
    },
    {
      path: '/factories',
      name: 'factory',
      component: Factory
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue')
    }
  ]
})

export default router
