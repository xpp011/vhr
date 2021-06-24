<template>
  <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm loginBody">
    <h1>登录</h1>
    <el-form-item label="用户名" prop="username" class="center">
      <el-input v-model="ruleForm.username"></el-input>
    </el-form-item>

    <el-form-item label="密码" prop="password" class="center">
      <el-input show-password v-model="ruleForm.password"></el-input>
    </el-form-item>


    <el-form-item class="center rememberMe">
      <el-checkbox-group v-model="ruleForm.rememberMe">
        <el-checkbox label="记住我" name="type" value="remember"></el-checkbox>
      </el-checkbox-group>
    </el-form-item>

    <el-form-item class="center loginButton">
      <el-button type="primary" style="width: 100px" @click="submitForm('ruleForm')">登录</el-button>
    </el-form-item>
  </el-form>
</template>

<script>

import {postJsonRequest} from "@/utils/api";

export default {
  name: "Login",
  data() {
    return {
      ruleForm: {
        username: 'admin',
        password: '123',
        rememberMe:''
      },
      rules: {
        username: [{required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 3, max: 9, message: '长度在3到9个字符之间', trigger: 'blur'}],

        password: [{required: true, message: '请输入密码', trigger: 'blur'}],
      },
      //登录加载标志位
      fullscreenLoading: false
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const loading = this.$loading({
            lock: true,
            text: '正在登录中,请稍后',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
          });
          postJsonRequest("/doLogin",this.ruleForm).then(resp=>{
            loading.close();
            if (resp){
              //登录之前销毁之前的数据
              sessionStorage.clear()
              this.$store.commit("initCurrentHr",resp.obj)
              sessionStorage.setItem('user',JSON.stringify(resp.obj))
              //require没有两个返回按钮    push有两个返回按钮
              let record="/home" //初始用户前往的登录模块是/home
              if(this.$route.query.record){//判断我们用户是否强制登录其他模块
                record=this.$route.query.record;
              }
              this.$router.replace(record)
            }
          })
        } else {
          this.$message({
            showClose: true,
            message: '请按要求填写',
            type: 'error'
          });
          return false;
        }
      });
    }
  }
}
</script>

<style >
  .loginBody{
    margin: 100px auto;
    width: 500px;
    height: auto;
    background: #eeeded;
    border-radius: 7px;
    padding: 35px 35px 15px 35px;
    text-align: center;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }

  .center{
    width: 400px;
    margin: 50px 25px;
  }

  .rememberMe{
    margin-left: -90px;
    margin-top: 20px;
  }

  .loginButton{
    width: 100px;
    margin-left: 100px;
  }
</style>