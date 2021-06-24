<template>
  <div>
    <div style="display: flex;justify-content: flex-start">
      <el-input
          @keydown.enter.native="search"
          size="small"
          style="width: 500px;margin: 0 10px"
          placeholder="输入操作员名称,查询想要的操作员..."
          prefix-icon="el-icon-search"
          v-model="searchHrname">
      </el-input>
      <el-button  type="primary" size="small" style="width: 60px" @click="search">搜索</el-button>
    </div>

    <div>
      <el-card class="box-card card" shadow="hover" v-for="(hr,index) in hrs">
        <div slot="header" class="clearfix">
          <div class="title">
            <span style="font-size: 18px">{{ hr.name }}</span>
            <el-avatar fit="cover" :src="hr.userface"></el-avatar>
          </div>
          <el-button style="color: red;font-size: 20px" type="text" icon="el-icon-delete-solid"></el-button>
        </div>
        <div :key="hr.id" class="text item content">
          <div><span class="tag">用户名:</span><span>{{ hr.name }}</span></div>
          <div><span class="tag">手机号码:</span><span>{{ hr.phone }}</span></div>
          <div><span class="tag">座机号码:</span><span>{{ hr.telephone }}</span></div>
          <div><span class="tag">地址:</span><span>{{ hr.address }}</span></div>
          <div><span class="tag">用户状态:</span><span><el-switch
              @change="updateHr(hr)"
              style="display: inline-block;padding-bottom: 3px;"
              v-model="hr.enabled"
              active-color="#13ce66"
              inactive-color="#ff4949"
              active-text="启用"
              inactive-text="禁用">
</el-switch></span></div>
          <div><span class="tag">用户角色:</span><span><el-tag style="margin: 0 2px" size="small"
                                                           v-for="(role,index) in hr.roles"
                                                           :key="role.id+hr.id">{{ role.nameZh }}</el-tag>
  <el-popover
      @show="show(hr)"
      @hide="hide(hr)"
      placement="bottom"
      title="标题"
      width="200"
      trigger="click"
      >  <el-select  size="small" v-model="selectRoles" multiple placeholder="用户角色">
    <el-option
        v-for="item in roles"
        :key="item.id"
        :label="item.nameZh"
        :value="item.id">
    </el-option>
  </el-select>
    <el-button style="width: 30px;margin-left: 5px;font-size: 16px" type="text" slot="reference">...</el-button>
  </el-popover>
            </span>
          </div>
        </div>
      </el-card>

    </div>
  </div>
</template>

<script>
import {getJsonRequest, postJsonRequest, putJsonRequest} from "@/utils/api";

export default {
  name: "EmpBasic",
  data() {
    return {
      hrs: [],
      roles:[],
      selectRoles:[],
      searchHrname: ''
    }
  },

  mounted() {
    this.initHrs()
    this.initRole()
  },

  methods: {
    initRole(){
      getJsonRequest("/system/hr/Roles").then(resp=>{
        this.roles=resp.obj;
      })
    },

    initHrs() {
      getJsonRequest("/system/hr/?searchHrname="+this.searchHrname).then(resp => {
        this.hrs = resp.obj
      })
    },

    updateHr(hr) {
      delete hr.roles
      postJsonRequest("/system/hr/", hr).then(resp => {
        this.initHrs()
      })
    },

    show(hr){
      this.selectRoles=[]
      let hrRole=hr.roles;
      hrRole.forEach(v=>{
        this.selectRoles.push(v.id)
      })
    },

    hide(hr){
      //判断选择的角色 和 用户本身的角色有没有差异
      //有则直接跳过 不需要更新
      let flag=false;
      let arr= new Array(50)
      for (let i=0;i<arr.length;i++){
        arr[i]=0
        }
      for (let i=0;i<this.selectRoles.length;i++){
        let index=this.selectRoles[i]
        arr[index]+=1
      }
      for (let i=0;i<hr.roles.length;i++){
        let index=hr.roles[i].id
        arr[index]+=1
      }
      for (let i=0;i<arr.length;i++){
        if (arr[i]==1){
          flag=true
          break
        }
      }
      if (flag){
        let  url="/system/hr/?hid="+hr.id+'&'
        this.selectRoles.forEach(v=>{
          url+='rids='+v+'&'
        })
        putJsonRequest(url).then(resp=>{
          this.initHrs()
        })
      }
    },

    search(){
      this.initHrs()
    }
  }
}
</script>

<style>
.card {
  margin: 25px 10px;
}

.clearfix {
  display: flex;
  justify-content: space-between;
  padding: 0 25px;
}

.title {
  height: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 120px;
}

.title span {
  margin: 0 5px;
}


.content {
  display: flex;
  justify-content: flex-start;
  flex-flow: wrap;
  text-align: left;
  padding: 0 30px;
}

.content div {
  width: 50%;
  margin: 5px 0;

}

.tag {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  font-weight: 700;
  margin-right: 5px;
  color: rgba(29, 29, 29, 0.48);
}
</style>