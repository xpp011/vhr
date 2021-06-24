import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store/idnex'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import {getJsonRequest} from "@/utils/api";
import {initMenu} from "@/utils/menus";
import './assets/font/iconfont.css'

Vue.use(ElementUI);

Vue.config.productionTip = false

/*全局前置守卫 保证用户在刷新页面时菜单信息保存在Vuex中*/
router.beforeEach((to, from, next) => {
    /*前往登录页面直接放行*/
    if (to.path != '/') {
      //不是前往登录页   判断当前用户是否登录  登录则照常
      if (sessionStorage.getItem("user")) {
        /*初始化菜单的信息*/
        initMenu(router, store)
      }else {
        //没有登录  强制前往登录页
        //to.path 记录用户想要强制前往的路径   在登录后直接转跳到该页面
        next('/?record='+to.path);
      }
    }
    next();
})

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
