<template>
<div>
  <div class="inputTool">
    <el-input size="small" placeholder="请输入角色英文名" v-model="role.name">
      <template slot="prepend">ROLE_</template>
    </el-input>
    <el-input size="small" placeholder="请输入角色中文名" v-model="role.nameZh"> </el-input>
    <el-button size="small" icon="el-icon-circle-plus" @click="addRole" type="primary" :disabled="!role.name || !role.nameZh">添加角色</el-button>
  </div>

  <div>
    <el-collapse accordion style="width: 810px;" @change="change" v-model="activeNames">
      <el-collapse-item :title="r.nameZh" :name="r.id" v-for="(r,index) in roles"  :key="index">
        <el-card class="box-card cardClass">
          <div slot="header" class="clearfix">
            <span>可访问资源</span>
            <el-button class="deleteButton" type="text" icon="el-icon-delete-solid" @click="deleteRole(r)"></el-button>
          </div>
          <el-tree
              ref="tree"
              :default-checked-keys="defaultKeys"
              :data="menuAll"
              show-checkbox
              node-key="id"
              :props="defaultProps"
              :key="r.id"
          >
          </el-tree>
        </el-card>
        <div style="display: flex;justify-content: flex-end;margin:7px 300px" >
          <el-button size="mini" @click="activeNames=-1">放弃修改</el-button>
          <el-button size="mini" type="danger" @click="doUpdate(r.id,index)">保存修改</el-button>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</div>
</template>

<script>
import {deleteJsonRequest, getJsonRequest, postJsonRequest, putJsonRequest} from "@/utils/api";

export default {
  name: "PermissionMana",
  data(){
    return{
      role:{
        name:'',
        nameZh:''
      },
      roles:[],
      menuAll:[],
      defaultProps:{
        label:'name',
        children:'children'
      },
      defaultKeys:[],
      activeNames:-1
    }
  },
  mounted() {
    this.initRoles()
  },
  methods:{
    initRoles(){
      getJsonRequest("/system/basic/per/").then(resp=>{
        this.roles=resp.obj;
      })
    },
    change(rid){
      if (rid){
        this.defaultKeys=[]
        getJsonRequest("/system/basic/per/menus").then(resp=>{
          this.menuAll=resp.obj
        })
        getJsonRequest("/system/basic/per/menus/"+rid).then(resp=>{
          this.defaultKeys=resp.obj
        })

      }
    },

    //处理修改可访问资源函数
    doUpdate(rid,index){
      /*getCheckedKeys
      * 若节点可被选择（即 show-checkbox 为 true），则返回目前被选中的节点的 key 所组成的数组	(leafOnly) 接收一个 boolean 类型的参数，
      * 若为 true 则仅返回被选中的叶子节点的 keys，默认值为 false*/
      let url="/system/basic/per/menus/?rid="+rid+"&"
      let keys=this.$refs.tree[index].getCheckedKeys(true);
      keys.forEach(v=>{
        url+="mids="+v+"&"
      })
      putJsonRequest(url).then(resp=>{
        // getJsonRequest("/system/basic/per/menus/"+rid).then(resp=>{
        //   this.defaultKeys=resp.obj
        // })
      })
      this.activeNames=-1
    },


    addRole(){
      if (this.role.name && this.role.nameZh){
        this.role.name="ROLE_"+this.role.name
        postJsonRequest("/system/basic/per/",this.role).then(resp=>{
          this.initRoles()
          this.role.name='',
          this.role.nameZh=''
        })
      }else {
        this.$message.info("字段不能为空")
      }
    },

    deleteRole(r){
      this.$confirm('此操作将永久删除【'+r.nameZh+'】角色 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteJsonRequest("/system/basic/per/"+r.id).then(resp=>{
          this.initRoles()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });

    }
  }
}
</script>

<style>

.inputTool{
  width: 815px;
  display: flex;
  justify-content: flex-start;
  margin-bottom: 10px;
}

.inputTool div input{
  width: 300px;
}

.cardClass{
  width: 500px;
  text-align: left;
  padding-left: 10px;
}

.deleteButton{
  float: right;
  padding: 3px 10px;
  color: red;
  font-size: 16px;
}


</style>