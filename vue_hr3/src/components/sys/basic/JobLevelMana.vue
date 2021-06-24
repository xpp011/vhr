<template>
  <div>
    <div style="float: left" class="top">
      <el-input
          clearable
          size="small"
          style="width: 350px;margin-right: 10px"
          placeholder="请输入职称..."
          prefix-icon="el-icon-circle-plus"
          v-model="jobLevel.name">
      </el-input>
      <el-select size="small" v-model="jobLevel.titleLevel" clearable placeholder="请选择级别">
        <el-option
            v-for="(item,index) in titleLevels"
            :key="index"
            :value="item">
        </el-option>
      </el-select>
      <el-button style="margin-left: 10px" @click="handleAdd" size="small" type="primary" icon="el-icon-circle-plus"  :disabled="!jobLevel.name || !jobLevel.titleLevel">添加</el-button>
    </div>
    <div class="top">
      <el-table
          @selection-change="handleSelectionChange"
          :data="jobLevels"
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
            width="70">
        </el-table-column>
        <el-table-column
            prop="name"
            label="职称名称"
            width="180">
        </el-table-column>
        <el-table-column
            prop="titleLevel"
            label="职称级别"
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
        <el-table-column
            label="操作"
            >
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
      <el-dialog title="编辑职称" :visible.sync="dialogFormVisible">
        <el-form :model="form">
          <el-form-item label="职称名字" :label-width="formLabelWidth">
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
          <el-form-item label="活动区域" :label-width="formLabelWidth">
            <el-select style="float: left" size="small" v-model="form.titleLevel" clearable placeholder="请选择级别">
              <el-option
                  v-for="(item,index) in titleLevels"
                  :key="index"
                  :value="item">
              </el-option>
            </el-select>
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
  name: "JobLevelMana",
  data(){
    return{
      jobLevel:{
        name:'',
        titleLevel:''
      },
      dialogFormVisible:false,
      formLabelWidth: '120px',
      jobLevels:[],
      multipleSelection: [],
      titleLevels:['正高级','副高级','员级','中级','初级'],
      form:{
        id:-1,
        name:'',
        enabled:null,
        titleLevel:''
      }
    }
  },
  //钩子函数  当Vue组件生成时调用该函数
  mounted() {
    this.initJobLevels()
  },
  methods:{
    //初始化职称表
    initJobLevels(){
      getJsonRequest("/system/basic/job/").then(resp=>{
        this.jobLevels=resp.obj;
      })
    },

    //添加职称
    handleAdd(){
      if (this.jobLevel.name && this.jobLevel.titleLevel){
        postJsonRequest("/system/basic/job/",this.jobLevel).then(resp=>{
          this.initJobLevels()
        })
        this.jobLevel.name= ''
        this.jobLevel.titleLevel=''
      }else {
        this.$message({
          type: 'info',
          message: '请填写完所有字段'
        });
      }
    },

    //编辑职称函数
    handleEdit(index,row) {
      this.dialogFormVisible=true
      this.form.id=row.id
      this.form.name=row.name
      this.form.enabled=row.enabled
      this.form.titleLevel=row.titleLevel
    },

    //处理编辑职称函数
    dohandleEdit(){
      putJsonRequest("/system/basic/job/",this.form).then(resp=>{
        this.initJobLevels()
      })
      this.dialogFormVisible = false
    },

    //删除职称函数
    handleDelete(index,row){
      deleteJsonRequest("/system/basic/job/"+row.id).then(resp=>{
        this.initJobLevels()
      })
    },

    //批量删除
    handleDeleteAll(){
      let ids='?'
      this.multipleSelection.forEach(v=>{
        ids+='ids='+v.id+'&'
      })
      deleteJsonRequest("/system/basic/job/"+ids).then(resp=>{
        this.initJobLevels()
      })
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