<template>
<div>
  <el-tabs v-model="activeName" type="card">
    <el-tab-pane label="部门管理" name="Dep"><DepMana></DepMana></el-tab-pane>
    <el-tab-pane label="职位管理" name="Pos"><PosMana></PosMana></el-tab-pane>
    <el-tab-pane label="职称管理" name="JobLevel"><JobLevelMana></JobLevelMana></el-tab-pane>
    <el-tab-pane label="奖惩规则" name="Bc"><BcMana></BcMana></el-tab-pane>
    <el-tab-pane label="权限组" name="Permission"><PermissionMana></PermissionMana></el-tab-pane>
  </el-tabs>
</div>
</template>

<script>

import DepMana from '../../components/sys/basic/DepMana'
import PosMana from '../../components/sys/basic/PosMana'
import JobLevelMana from '../../components/sys/basic/JobLevelMana'
import BcMana from '../../components/sys/basic/BcMana'
import PermissionMana from '../../components/sys/basic/PermissionMana'

export default {
  name: "EmpBasic",
  data(){
    return {
      activeName:'Pos'
    }
  },
  components:{
    DepMana,
    PosMana,
    JobLevelMana,
    BcMana,
    PermissionMana
  }
}
</script>

<style scoped>

</style>