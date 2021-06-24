<template>
  <div>
    <div style="display: flex;justify-content: space-between;padding: 0 10px;margin: 20px 0">
      <el-button size="small" type="primary" icon="el-icon-circle-plus-outline"
                 @click="showAddSalary">添加工资套账
      </el-button>
      <el-button size="small" type="success" icon="el-icon-refresh" @click="initSalaryTable"></el-button>
    </div>
    <div>
      <el-table :data="salaryTable">
        <el-table-column type="selection" width="40"></el-table-column>
        <el-table-column label="套账名称" prop="name" width="120" align="center"></el-table-column>
        <el-table-column label="基本工资" prop="basicSalary" width="90" align="center"></el-table-column>
        <el-table-column label="交通补助" prop="trafficSalary" width="90" align="center"></el-table-column>
        <el-table-column label="午餐补助" prop="lunchSalary" width="90" align="center"></el-table-column>
        <el-table-column label="奖金" prop="bonus" width="90" align="center"></el-table-column>
        <el-table-column label="启用时间" prop="createDate" width="100" align="center"></el-table-column>
        <el-table-column label="养老金" align="center">
          <el-table-column label="比率" prop="pensionPer" width="80" align="center"></el-table-column>
          <el-table-column label="基数" prop="pensionBase" width="80" align="center"></el-table-column>
        </el-table-column>
        <el-table-column label="医疗保险" align="center">
          <el-table-column label="比率" prop="medicalPer" width="80" align="center"></el-table-column>
          <el-table-column label="基数" prop="medicalBase" width="80" align="center"></el-table-column>
        </el-table-column>
        <el-table-column label="公积金" align="center">
          <el-table-column label="比率" prop="accumulationFundPer" width="80" align="center"></el-table-column>
          <el-table-column label="基数" prop="accumulationFundBase" width="80" align="center"></el-table-column>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button type="primary" icon="el-icon-s-grid" size="small" @click="nextStep(scope.row)">编辑</el-button>
            <el-button type="danger" icon="el-icon-delete" size="small" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>

      </el-table>
      <div >
        <el-dialog title="添加员工套账" width="90%" @close="closeDialog"  :visible.sync="dialogTableVisible" simple >
          <el-steps :active="salaryItemIndex" >
            <el-step :title="item" v-for="(item,index) in salaryItemName" :key="index"></el-step>
          </el-steps>
          <div style="margin-top: 50px ;" v-for="(value,key,index) in salary" :key="index" v-show="salaryItemIndex==index">
            <span style="margin-right: 12px;font-size: 18px">{{salaryItemName[index]}}:</span>
            <el-input size="small" style="width: 60%" :placeholder="'请输入'+salaryItemName[index]+'信息'" v-model="salary[key]"></el-input>
          </div>
          <span slot="footer" class="dialog-footer">
              <el-button @click="preStep">{{salaryItemIndex==0?'取消':'上一步'}}</el-button>
              <el-button type="primary" @click="nextStep(null)">{{ salaryItemIndex==this.salaryItemName.length?'完成':'下一步' }}</el-button>
          </span>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import {deleteJsonRequest, getJsonRequest, postJsonRequest, putJsonRequest} from "@/utils/api";

export default {
  name: "SalSob",
  data() {
    return {
      dialogTableVisible: false,
      salaryItemIndex:0,
      salaryTable: [],
      salaryItemName:[
          "套账名称","基本工资","交通补助","午餐补助","奖金","养老金比率","养老金基数",
        "医疗保险比率","医疗保险基数","公积金比率","公积金基金"
      ],
      salary:{
        name:'',
        basicSalary:null,
        trafficSalary:null,
        lunchSalary:null,
        bonus:null,
        pensionPer:null,
        pensionBase:null,
        medicalPer:null,
        medicalBase:null,
        accumulationFundPer:null,
        accumulationFundBase:null
      },
      salaryId:null
    }
  },
  mounted() {
    this.initSalaryTable()
  },
  methods: {

    showAddSalary(){
      this.salary={
        name:'',
        basicSalary:null,
        trafficSalary:null,
        lunchSalary:null,
        bonus:null,
        pensionPer:null,
        pensionBase:null,
        medicalPer:null,
        medicalBase:null,
        accumulationFundPer:null,
        accumulationFundBase:null
      }
      this.salaryId=null
      this.dialogTableVisible=!this.dialogTableVisible
    },

    preStep(){
      if (this.salaryItemIndex==0){
        this.dialogTableVisible=false
        return
      }
      this.salaryItemIndex--
    },
    nextStep(data){
      if (data!=null){
        this.salaryId=data.id
        this.salary.name=data.name
        this.salary.basicSalary=data.basicSalary
        this.salary.trafficSalary=data.trafficSalary
        this.salary.lunchSalary=data.lunchSalary
        this.salary.bonus=data.bonus
        this.salary.pensionPer=data.pensionPer
        this.salary.pensionBase=data.pensionBase
        this.salary.medicalPer=data.medicalPer
        this.salary.medicalBase=data.medicalBase
        this.salary.accumulationFundPer=data.accumulationFundPer
        this.salary.accumulationFundBase=data.accumulationFundBase
        this.dialogTableVisible=true
        return;
      }
      if (this.salaryItemIndex==this.salaryItemName.length){
        if (this.salaryId){

          let updateSalary={
            id:this.salaryId,
            name:this.salary.name,
            basicSalary:this.salary.basicSalary,
            trafficSalary:this.salary.trafficSalary,
            lunchSalary:this.salary.lunchSalary,
            bonus:this.salary.bonus,
            pensionPer:this.salary.pensionPer,
            pensionBase:this.salary.pensionBase,
            medicalPer:this.salary.medicalPer,
            medicalBase:this.salary.medicalBase,
            accumulationFundPer:this.salary.accumulationFundPer,
            accumulationFundBase:this.salary.accumulationFundBase
          }

          //更新数据
          putJsonRequest("/salary/sob/",updateSalary).then(resp=>{
            this.initSalaryTable()
          })
          //关闭对话框
          this.dialogTableVisible=false
          //初始化index
          this.salaryItemIndex=0
        }else {
          //提交数据
          postJsonRequest("/salary/sob/",this.salary).then(resp=>{
            this.initSalaryTable()
          })
          //关闭对话框
          this.dialogTableVisible=false
          //初始化index
          this.salaryItemIndex=0
        }
        return
      }
      this.salaryItemIndex++

    },
    closeDialog(){
      this.salaryItemIndex=0
    },
    initSalaryTable() {
      getJsonRequest("/salary/sob/").then(resp => {
        this.salaryTable = resp.obj;
      })
    },
    //删除操作
    handleDelete(index,row){
      this.$confirm('此操作将永久删除【'+row.name+'】套账, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteJsonRequest("/salary/sob/"+row.id).then(resp=>{
          this.initSalaryTable()
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

<style scoped>
.steps .el-dialog{
  width: 90%;
}
</style>