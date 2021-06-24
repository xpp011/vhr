import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import FriendChat from '../views/chat/FriendChat'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login,
  },{
    path: '/home',
    name: 'Home',
    component: Home,
    children:[
      {
        path: '/chat',
        name: '聊天室',
        component: FriendChat,
      }
    ]
  }
]

const router = new VueRouter({
  routes
})

export default router
