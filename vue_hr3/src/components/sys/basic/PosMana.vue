<template>
<div>
  <div style="float: left" class="top">
    <el-input
        clearable
        size="small"
        style="width: 350px;margin-right: 10px"
        placeholder="请输入职位名称"
        prefix-icon="el-icon-circle-plus"
        v-model="pos.name">
    </el-input>
    <el-button @click="handleAdd" size="small" type="primary" icon="el-icon-circle-plus"  :disabled="!pos.name">添加</el-button>
  </div>
  <div class="top">
    <el-table
        @selection-change="handleSelectionChange"
        :data="Positions"
        stripe
        border
        style="width: 100%">
      <el-table-column
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column
          prop="id"
          label="编号"
          width="50">
      </el-table-column>
      <el-table-column
          prop="name"
          label="职位名称"
          width="180">
      </el-table-column>
      <el-table-column
          prop="createDate"
          label="创建时间"
          width="280">
      </el-table-column>
      <el-table-column
          prop="enabled"
          label="是否启用"
          width="200">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enabled?'success':'danger'">{{ scope.row.enabled?"已启用":"未启用" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
              size="mini"
              @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button
              slot="reference"
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <div>
    <el-dialog title="编辑职位" :visible.sync="dialogFormVisible">
      <el-form :model="form">
        <el-form-item label="职位名字" :label-width="formLabelWidth">
          <el-input size="small" style="width: 240px;float: left" v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="是否启用" :label-width="formLabelWidth">
          <el-switch
              class="switch"
              v-model="form.enabled"
              active-color="#13ce66"
              inactive-color="#ff4949"
              active-text="启用"
              inactive-text="禁用">
          </el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dohandleEdit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
  <div style="float: left">
    <el-button type="danger" plain size="small" :disabled="multipleSelection.length==0" @click="handleDeleteAll">批量删除</el-button>
  </div>
</div>
</template>

<script>
import {deleteJsonRequest, getJsonRequest, postJsonRequest, putJsonRequest} from "@/utils/api";

export default {
  name: "PosMana",
  data(){
    return{
      pos:{
        name:''
      },
      dialogFormVisible:false,
      formLabelWidth: '120px',
      Positions:[],
      multipleSelection: [],
      form:{
        id:-1,
        name:'',
        enabled:null
      }
    }
  },
  mounted() {
    this.initPositions()
  },
  methods:{
    //初始化职位表
    initPositions(){
      getJsonRequest("/system/basic/pos/").then(resp=>{
        this.Positions=resp.obj
      })
    },

    //添加职位
    handleAdd(){
      if (this.pos.name){
        postJsonRequest("/system/basic/pos/",this.pos).then(resp=>{
            this.initPositions()
        })
        this.pos.name=''
      }else {
        this.$message({
          type: 'info',
          message: '职位名不能为空'
        });
      }
    },

    //编辑职位函数
    handleEdit(index,row){
      this.dialogFormVisible=true
      this.form.id=row.id
      this.form.name=row.name
      this.form.enabled=row.enabled
    },

    //处理编辑函数
    dohandleEdit(){
      putJsonRequest("/system/basic/pos/",this.form).then(resp=>{
        this.initPositions()
      })
      this.dialogFormVisible=false
    },

    //删除职位函数
    handleDelete(index,row){
      this.$confirm('是否删除该职位', '提示', {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
            deleteJsonRequest("/system/basic/pos/"+row.id).then(resp=>{
              this.initPositions()
        });

      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },

    //批量删除
    handleDeleteAll(){
      this.$confirm('将批量删除【'+this.multipleSelection.length+'】个职位, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let ids='?'
        this.multipleSelection.forEach(v => {
          ids+='ids='+v.id+'&'
        })
        deleteJsonRequest("/system/basic/pos/"+ids).then(resp=>{
          this.initPositions()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消批量删除'
        });
      });
    },

    //多选的回调函数
    handleSelectionChange(val) {
      this.multipleSelection = val;
    }
  }
}
</script>

<style>
.top{
  padding: 10px 0;
}

.switch{
  display: block;
  float: left;
  margin-top: 10px;
}

</style>