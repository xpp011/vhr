<template>
  <div>
    <el-container>
      <!--头部-->
      <el-header class="HomeHeader">
        <div class="title">微人事</div>

        <el-col :span="12" class="container">
          <div><el-button icon="el-icon-message-solid" type="text" style="color: #2c3e50;width: 100px;font-size: 21px" @click="goChat"></el-button></div>
          <div>
            <el-avatar :src="user.userface"></el-avatar>
          </div>
          <el-dropdown @command="handleCommand">
      <span class="el-dropdown-link">
        {{ user.name }}<i class="el-icon-arrow-down el-icon--right"></i>
      </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="userinfo" icon="el-icon-user-solid">个人中心</el-dropdown-item>
              <el-dropdown-item command="setting" icon="el-icon-setting">设置</el-dropdown-item>
              <el-dropdown-item command="logout" icon="el-icon-switch-button" divided>注销</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

        </el-col>

      </el-header>
      <!--侧边栏部分  icon-management-->
      <el-container>
        <el-aside width="200px">
          <el-menu
              unique-opened
              background-color="#EBEEF5"
              class="el-menu-vertical-demo"
              @select="clickHandler">
            <!--循环遍历路由 通过路由内部的标志位show的false和true判断该组件是否显示-->
            <el-submenu :index="index+''" v-for="(nav,index) in routes"  :key="index">
              <template slot="title">
                <i style="margin-right: 9px;color: #409EFF;font-size: 20px" :class="nav.iconCls"></i>
                <span>{{nav.name}}</span>
              </template>
              <el-menu-item-group background-color="#ffffff">
                <el-menu-item background-color="#ffffff" 	 :index="opion.path" v-for="(opion,indexj) in nav.children" :key="indexj">{{opion.name}}</el-menu-item>
              </el-menu-item-group>
            </el-submenu>
          </el-menu>

        </el-aside>
        <el-main>
          <!--标签头-->
          <el-page-header @back="goBack" :content="this.$router.currentRoute.name" v-show="this.$router.currentRoute.path!='/home'">
          </el-page-header>
          <div>
          <!--首页标题-->
            <h1 class="welcome" v-show="$router.currentRoute.path=='/home'">欢迎来到微人事</h1>
          </div>
          <router-view style="margin-top: 13px" />
        </el-main>
      </el-container>
    </el-container>
  </div>

</template>


<script>
import {getJsonRequest} from "@/utils/api";

export default {
  name: "Home",
  data() {
    return {
      user: JSON.parse(window.sessionStorage.getItem("user")),

    }
  },
  computed:{
    routes(){
      return this.$store.state.routes
    }
  },
  methods: {
    //前往聊天室
    goChat(){
      this.$router.push("/chat")
    },

    //顶部标题处理函数
    handleCommand(command) {
      if (command == "logout") {
        this.$confirm('是否确认注销', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          getJsonRequest("/Logout")
          window.sessionStorage.removeItem("user")
          this.$router.replace('/')
          /*注销后清除菜单数据，确保下次登录从数据请求菜单数据*/
          this.$store.state.routes=[]
          console.log("发起注销登录")
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '取消注销'
          });
        });
      }
    },

    //菜单处理器
    clickHandler(index) {
      /*index为el-menu-item标签的index属性则转跳的URL地址*/
      this.$router.push(index)
    },

    //菜单头点击事件
    goBack() {
      this.$router.push('/home')
    }
  }
}
</script>

<style>

.welcome{
  font-family: "Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB","Microsoft YaHei","微软雅黑",Arial,sans-serif;
  font-size: 50px;
  color: #303133;
}

.HomeHeader {
  background-color: #409EFF;
  display: flex;
  padding: 0 50px;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
  box-sizing: border-box;
}

.HomeHeader .title {
  font-size: 30px;
  text-align: center;
  line-height: 55px;
  font-family: "微软雅黑 Light";
  font-weight: 700;
  color: aliceblue;
}

.HomeHeader .container {
  cursor: pointer;
}

.HomeHeader .el-dropdown-link {
  color: #eeeded;
  margin-left: 25px;
}

.HomeHeader .el-col-12 {
  width: 20%;
  float: right;
  display: flex;
  align-items: center;
}
</style>