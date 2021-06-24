<template>
<div>
  <div style="float: left">
    <el-input
        style="width: 500px"
        prefix-icon="el-icon-search"
        placeholder="请输入需要查询的部门"
        v-model="filterText">
    </el-input>

    <el-tree
        :expand-on-click-node="false"
        class="filter-tree"
        :data="departments"
        :props="defaultProps"
        default-expand-all
        :filter-node-method="filterNode"
        ref="tree">
      <span class="custom-tree-node" slot-scope="{ node, data }"
            style="display: flex ; justify-content: space-between; width: 100%">
        <span>{{ node.label }}</span>
        <span >
          <el-button
              class="delBtn"
              type="primary"
              size="mini"
              @click="() => append(data)">
            添加部门
          </el-button>
          <el-button
              class="delBtn"
              type="danger"
              size="mini"
              @click="() => remove(node, data)">
            删除部门
          </el-button>
        </span>
      </span>
    </el-tree>
  </div>
<!--添加的消息框  -->
  <div>
    <el-dialog title="添加部门" :visible.sync="dialogFormVisible">
      <el-form :model="dep">
        <el-form-item label="上级部门" :label-width="formLabelWidth">
          <el-tag type="success" style="width: 200px;float: left;font-size: 16px">{{parentName}}</el-tag>
        </el-form-item>
        <el-form-item label="部门名称" :label-width="formLabelWidth">
          <el-input v-model="dep.name" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary"  @click="doAppend">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</div>
</template>

<script>
import {deleteJsonRequest, getJsonRequest, postJsonRequest} from "@/utils/api";

export default {
  name: "DepMana",
  data(){
    return{
      filterText: '',
      defaultProps: {
        label:'name',
        children:'children'
      },
      dialogFormVisible:false,
      formLabelWidth:'120px',
      departments:[],
      dep:{
        name:'',
        parentId:''
      },
      parentName:''
    }

  },

  mounted() {
    this.initDepartments()
  },

  //监控
  watch: {
    //监控input输入的值  val则为input的值
    filterText(val) {
      //调用控件tree的过滤方法  其实就是下面的filterNode方法
      this.$refs.tree.filter(val);
    }
  },

  methods:{
    //可以将看为递归调用  val则为inout的值  data则为departments的值
    filterNode(value, data) {
      //判断value是否为空   为空则返回true 说明保留该节点
      if (!value) return true;
      //判断当前节点的值是否和value匹配  不匹配表达式!== -1则为false 不保留该节点
      return data.name.indexOf(value) !== -1;
    },
    initDepartments(){
      getJsonRequest("/system/basic/dep/").then(resp=>{
        this.departments=resp.obj;
      })
    },

    //添加部门函数
    append(data){
      this.dialogFormVisible=true
      this.dep.parentId=data.id
      this.parentName=data.name
    },

    //处理添加部门函数
    doAppend(){
      postJsonRequest("/system/basic/dep/",this.dep).then(resp=>{
          this.renewalTerr(this.departments,resp.obj)
          this.dep.name=''
          this.dep.parentId=-1
          this.parentName=''
          this.dialogFormVisible=false
      })
    },

    //为了保证在插入部门时，Tree控件不收缩  我们选择在数组中递归寻找插入部门的父id的值在它的children中push新插入的部门 这样就保证了Tree控件不会刷新
    renewalTerr(deps,resp){
      for(let i=0;i<deps.length;i++) {
        if (deps[i].id == resp.parentId) {
          deps[i].children.push(resp)
          return
        }
          this.renewalTerr(deps[i].children,resp)
      }
    },


    deleteTree(deps,resp){
      for(let i=0;i<deps.length;i++){
        if (deps[i].id==resp.id){
          deps.splice(i,1)
          return
        }
        this.deleteTree(deps[i].children,resp)
      }
    },

    remove(node,data){
      this.$confirm('此操作将永久删除【'+data.name+'】部门, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteJsonRequest("/system/basic/dep/"+data.id).then(resp=>{
          this.deleteTree(this.departments,resp.obj)
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

<style >

.delBtn{
  padding: 2px;
}

</style>