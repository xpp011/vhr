<template>
<div>
  <div>
    <el-table
        size="small"
        :data="employeeTable"
        stripe
        style="width: 100%">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column
          prop="name"
          label="姓名"
          width="120">
      </el-table-column>
      <el-table-column
          prop="workID"
          label="工号"
          width="180">
      </el-table-column>
      <el-table-column
          prop="email"
          label="电子邮箱">
      </el-table-column>
      <el-table-column
          prop="phone"
          label="联系电话">
      </el-table-column>
      <el-table-column
          prop="department.name"
          label="所属部门">
      </el-table-column>
      <el-table-column
          label="工资账套">
        <template slot-scope="scope">
          <el-tooltip placement="right" v-if="scope.row.salary">
            <div slot="content">
              <div v-for="(value,key,index) in scope.row.salary" :key="index"  v-show="key!='name'" v-if="key!='id'">
                <el-tag style="margin-right: 5px" type="success">{{salaryName[index]}}</el-tag>
                <span >{{value}}</span>
              </div>
            </div>
            <el-tag>{{scope.row.salary.name}}</el-tag>
          </el-tooltip>
          <el-tag v-else type="warning">尚未设置</el-tag>
        </template>
      </el-table-column>
      <el-table-column
          label="操作">
        <template slot-scope="scope">
          <el-popover
              no-match-text="请选择"
              @show="show(scope.row)"
              placement="right"
              title="标题"
              width="200"
              trigger="click">
            <el-select size="small" v-model="salaryId" placeholder="请选择"  @change="change">
              <el-option
                  v-for="item in salsrys"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
              </el-option>
            </el-select>
            <el-button  slot="reference" size="small" type="danger">修改工资账套</el-button>
          </el-popover>

        </template>
      </el-table-column>
    </el-table>
  </div>
  <div>
    <el-pagination
        @size-change="sizeChange"
        @current-change="currentChange"
        :current-page="page"
        :page-sizes="[10, 30, 50, 100]"
        :page-size="size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>
  </div>
</div>
</template>

<script>
import {getJsonRequest, putJsonRequest} from "@/utils/api";

export default {
  name: "SalSobCfg",
  data(){
    return{
      updateSalaryId:-1,
      updateEmpID:-1,
      employeeTable:[],
      salsrys:[],
      salaryId:0,
      total:0,
      salaryName:["id","基本工资","奖金","午餐补助","交通补助","子工资套账","养老金基数","养老金比率","启用时间","医疗保险基数","医疗保险比率","公积金基数","公积金比率","name"],
      size:10,
      page:1
    }
  },
  mounted() {
    this.initEmployeeTable()
    this.initSalsrys()
  },
  methods:{
    initEmployeeTable(){
      let url="/salary/sobcfg/?page="+this.page+"&size="+this.size
      getJsonRequest(url).then(resp=>{
            this.total=resp.total
            this.employeeTable=resp.data
          })
    },
    initSalsrys(){
      getJsonRequest("/salary/sobcfg/AllSalary").then(resp=>{
        this.salsrys=resp.obj
      })
    },
    sizeChange(size){
      this.size=size
      this.initEmployeeTable()
    },

    currentChange(page){
      this.page=page
      this.initEmployeeTable()
    },

    change(value){
      putJsonRequest("/salary/sobcfg/"+this.updateEmpID+"/"+value).then(resp=>{
        this.initEmployeeTable()
      })
    },

    show(data){
      if (data.salary){
        this.salaryId=data.salary.id
      }else {
        this.salaryId = null
      }
      this.updateEmpID=data.id
    }

  }
}
</script>

<style scoped>

</style>