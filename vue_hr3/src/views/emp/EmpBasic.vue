<template>
  <div>
    <div class="title">
      <div class="search">
        <el-input size="small"
                  clearable
                  @clear="initTableData"
                  placeholder="请输入员工姓名,查询员工"
                  prefix-icon="el-icon-search"
                  :disabled="showAdvancedView"
                  v-model="keyWork"
        >
        </el-input>
        <el-button size="small" type="primary" icon="el-icon-search"  :disabled="showAdvancedView" @click="initTableData">搜索</el-button>
        <el-button size="small" type="primary" :icon="showAdvancedView?'el-icon-caret-top':'el-icon-caret-bottom'" @click="advanceSearch">高级搜索</el-button>
      </div>
      <div>
        <el-upload
            :disabled="disabled"
            style="display: inline-flex;"
            class="upload-demo"
            :show-file-list="false"
            action="/employee/basic/import"
            :on-success="onSuccess"
            :on-error="onError"
            :before-upload="beforeUpload"
            >
          <el-button plain size="small" type="primary" :disabled="disabled" :icon="importDataBtnIcon">{{ importDataBtnText }}</el-button>
        </el-upload>

        <el-button @click="exportExcel" plain size="small" type="primary" icon="el-icon-download" >导出数据</el-button>
        <el-button size="small" type="warning" @click="showAddEmpView" icon="el-icon-circle-plus">添加员工</el-button>
      </div>
    </div>
    <div>
      <el-collapse-transition>
        <div v-show="showAdvancedView"
             style="border: 1px solid #409eff;border-radius: 5px;box-sizing: border-box;padding: 5px;margin: 10px 0px;">
          <el-row>
            <el-col :span="5">
              政治面貌:
              <el-select v-model="advancedEmp.politicId" placeholder="政治面貌" size="mini"
                         style="width: 130px;">
                <el-option
                    v-for="item in politicsstatuss"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="4">
              民族:
              <el-select  v-model="advancedEmp.nationId" placeholder="民族" size="mini"
                         style="width: 130px;">
                <el-option
                    v-for="item in nations"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="4">
              职位:
              <el-select v-model="advancedEmp.posId" placeholder="职位" size="mini" style="width: 130px;">
                <el-option
                    v-for="item in positions"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="4">
              职称:
              <el-select v-model="advancedEmp.jobLevelId"  placeholder="职称" size="mini"
                         style="width: 130px;">
                <el-option
                    v-for="item in joblevels"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="7">
              聘用形式:
              <el-radio-group v-model="advancedEmp.engageForm">
                <el-radio label="劳动合同">劳动合同</el-radio>
                <el-radio label="劳务合同">劳务合同</el-radio>
              </el-radio-group>
            </el-col>
          </el-row>
          <el-row style="margin-top: 10px">
            <el-col :span="5">
              所属部门:
              <el-popover
                  placement="right"
                  title="请选择部门"
                  width="200"
                  trigger="manual"
                  v-model="popVisible2">
                <el-tree default-expand-all :data="allDeps" :props="defaultProps" :expand-on-click-node="false"
                         @node-click="nodeAdvancedClick"></el-tree>
                <div slot="reference"
                     style="width: 150px;display: inline-flex;font-size: 13px;border: 1px solid #dedede;height: 26px;border-radius: 5px;cursor: pointer;align-items: center;padding-left: 8px;box-sizing: border-box"
                     @click="showadvancedTreeView">{{ inputDepName2 }}
                </div>
              </el-popover>
            </el-col>
            <el-col :span="10">
              入职日期:
              <el-date-picker
                  v-model="BigEngDate"
                  type="daterange"
                  size="mini"
                  unlink-panels
                  value-format="yyyy-MM-dd"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期">
              </el-date-picker>
            </el-col>
            <el-col :span="5" :offset="4">
              <el-button size="mini" @click="showadvancedView=!showadvancedView">取消</el-button>
              <el-button size="mini" icon="el-icon-search" type="primary" @click="initTableData">搜索</el-button>
            </el-col>
          </el-row>
        </div>
        </el-collapse-transition>

    </div>
    <div class="content">

      <el-table
          v-loading="loading"
          :data="tableData"
          border
          style="width: 100%">
        <el-table-column
            fixed="left"
            prop="选择"
            type="selection"
            width="45">
        </el-table-column>
        <el-table-column
            fixed="left"
            prop="name"
            label="姓名"
            width="100">
        </el-table-column>
        <el-table-column
            prop="gender"
            label="性别"
            width="100">
        </el-table-column>
        <el-table-column
            prop="workID"
            label="工号"
            width="100">
        </el-table-column>
        <el-table-column
            prop="birthday"
            label="出生日期"
            width="110">
        </el-table-column>
        <el-table-column
            prop="idCard"
            label="身份证号码"
            width="165">
        </el-table-column>
        <el-table-column
            prop="wedlock"
            label="婚姻状况"
            width="90">
        </el-table-column>
        <el-table-column
            prop="nation.name"
            label="民族"
            width="55">
        </el-table-column>
        <el-table-column
            prop="nativePlace"
            label="籍贯"
            width="90">
        </el-table-column>
        <el-table-column
            prop="politicsstatus.name"
            label="政治面貌"
            width="100">
        </el-table-column>
        <el-table-column
            prop="email"
            label="电子邮件"
            width="170">
        </el-table-column>
        <el-table-column
            prop="address"
            label="联系地址"
            width="220">
        </el-table-column>
        <el-table-column
            prop="department.name"
            label="所属部门"
            width="100">
        </el-table-column>
        <el-table-column
            prop="position.name"
            label="职位"
            width="120">
        </el-table-column>
        <el-table-column
            prop="engageForm"
            label="聘用形式"
            width="100">
        </el-table-column>
        <el-table-column
            prop="beginDate"
            label="入职日期"
            width="110">
        </el-table-column>
        <el-table-column
            prop="conversionTime"
            label="转正日期"
            width="110">
        </el-table-column>
        <el-table-column
            prop="beginContract"
            label="合同起始日期"
            width="110">
        </el-table-column>
        <el-table-column
            prop="endContract"
            label="合同终止日期"
            width="110">
        </el-table-column>
        <el-table-column

            label="合同期限"
            width="80">
          <template slot-scope="scope">
            {{ scope.row.contractTerm }}年
          </template>
        </el-table-column>

        <el-table-column
            prop="school"
            label="毕业院校"
            width="180">
        </el-table-column>
        <el-table-column
            prop="specialty"
            label="专业"
            width="180">
        </el-table-column>
        <el-table-column
            prop="tiptopDegree"
            label="最高学历"
            width="60">
        </el-table-column>
        <el-table-column
            prop="jobLevel.name"
            label="职称"
            width="120">
        </el-table-column>

        <el-table-column
            fixed="right"
            label="操作"
            width="190">
          <template slot-scope="scope">
            <el-button class="but" @click="updateHandleClick(scope.row)" type="primary" plain size="small">编辑</el-button>
            <el-button class="but" @click="handleClick(scope.row)" type="success" size="small">查看高级资料</el-button>
            <el-button class="but" @click="deleteHandleClick(scope.row)" type="primary" plain size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div>
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 30, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <div>
      <el-dialog
          :title="title"
          :visible.sync="dialogVisible"
          width="80%">
        <div>
          <el-form :model="emp" :rules="rules" :title="title"  ref="empForm">
            <el-row>
              <el-col :span="6">
                <el-form-item label="姓名:" prop="name">
                  <el-input size="mini" style="width: 150px" prefix-icon="el-icon-edit" v-model="emp.name"
                            placeholder="请输入员工姓名"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="性别:" prop="gender">
                  <el-radio-group v-model="emp.gender">
                    <el-radio label="男">男</el-radio>
                    <el-radio label="女">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="出生日期:" prop="birthday">
                  <el-date-picker
                      v-model="emp.birthday"
                      size="mini"
                      type="date"
                      value-format="yyyy-MM-dd"
                      style="width: 150px;"
                      placeholder="出生日期">
                  </el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="7">
                <el-form-item label="政治面貌:" prop="politicId">
                  <el-select v-model="emp.politicId" placeholder="政治面貌" size="mini" style="width: 200px;">
                    <el-option
                        v-for="item in politicsstatuss"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="6">
                <el-form-item label="民族:" prop="nationId">
                  <el-select v-model="emp.nationId" placeholder="民族" size="mini" style="width: 150px;">
                    <el-option
                        v-for="item in nations"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="籍贯:" prop="nativePlace">
                  <el-input size="mini" style="width: 120px" prefix-icon="el-icon-edit"
                            v-model="emp.nativePlace" placeholder="请输入籍贯"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="电子邮箱:" prop="email">
                  <el-input size="mini" style="width: 170px" prefix-icon="el-icon-message"
                            v-model="emp.email" placeholder="请输入电子邮箱"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="7">
                <el-form-item label="联系地址:" prop="address">
                  <el-input size="mini" style="width: 200px" prefix-icon="el-icon-edit"
                            v-model="emp.address" placeholder="请输入联系地址"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="6">
                <el-form-item label="职位:" prop="posId">
                  <el-select v-model="emp.posId" placeholder="职位" size="mini" style="width: 150px;">
                    <el-option
                        v-for="item in positions"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="职称:" prop="jobLevelId">
                  <el-select v-model="emp.jobLevelId" placeholder="职称" size="mini" style="width: 150px;">
                    <el-option
                        v-for="item in joblevels"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="所属部门:" prop="departmentId">
                  <el-popover
                      placement="right"
                      title="请选择部门"
                      width="200"
                      trigger="manual"
                      v-model="popVisible">
                    <el-tree default-expand-all :data="allDeps" :props="defaultProps" :expand-on-click-node="false"
                             @node-click="nodeClick"></el-tree>
                    <div slot="reference"
                         style="width: 150px;display: inline-flex;font-size: 13px;border: 1px solid #dedede;height: 26px;border-radius: 5px;cursor: pointer;align-items: center;padding-left: 8px;box-sizing: border-box"
                         @click="showTreeView">{{ inputDepName }}
                    </div>
                  </el-popover>
                </el-form-item>
              </el-col>
              <el-col :span="7">
                <el-form-item label="电话号码:" prop="phone">
                  <el-input size="mini" style="width: 200px" prefix-icon="el-icon-phone"
                            v-model="emp.phone" placeholder="电话号码"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="6">
                <el-form-item label="工号:" prop="workID">
                  <el-input size="mini" style="width: 150px" prefix-icon="el-icon-edit"
                            v-model="emp.workID" placeholder="工号" disabled></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="学历:" prop="tiptopDegree">
                  <el-select v-model="emp.tiptopDegree" placeholder="学历" size="mini"
                             style="width: 150px;">
                    <el-option
                        v-for="item in tiptopDegrees"
                        :key="item"
                        :label="item"
                        :value="item">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="毕业院校:" prop="school">
                  <el-input size="mini" style="width: 150px" prefix-icon="el-icon-edit"
                            v-model="emp.school" placeholder="毕业院校名称"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="7">
                <el-form-item label="专业名称:" prop="specialty">
                  <el-input size="mini" style="width: 200px" prefix-icon="el-icon-edit"
                            v-model="emp.specialty" placeholder="请输入专业名称"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="6">
                <el-form-item label="入职日期:" prop="beginDate">
                  <el-date-picker
                      v-model="emp.beginDate"
                      size="mini"
                      type="date"
                      value-format="yyyy-MM-dd"
                      style="width: 130px;"
                      placeholder="入职日期">
                  </el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="转正日期:" prop="conversionTime">
                  <el-date-picker
                      v-model="emp.conversionTime"
                      size="mini"
                      type="date"
                      value-format="yyyy-MM-dd"
                      style="width: 130px;"
                      placeholder="转正日期">
                  </el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="合同起始日期:" prop="beginContract">
                  <el-date-picker
                      v-model="emp.beginContract"
                      size="mini"
                      type="date"
                      value-format="yyyy-MM-dd"
                      style="width: 130px;"
                      placeholder="合同起始日期">
                  </el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="合同终止日期:" prop="endContract">
                  <el-date-picker
                      v-model="emp.endContract"
                      size="mini"
                      type="date"
                      value-format="yyyy-MM-dd"
                      style="width: 150px;"
                      placeholder="合同终止日期">
                  </el-date-picker>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="8">
                <el-form-item label="身份证号码:" prop="idCard">
                  <el-input size="mini" style="width: 180px" prefix-icon="el-icon-edit"
                            v-model="emp.idCard" placeholder="请输入身份证号码"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="聘用形式:" prop="engageForm">
                  <el-radio-group v-model="emp.engageForm">
                    <el-radio label="劳动合同">劳动合同</el-radio>
                    <el-radio label="劳务合同">劳务合同</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="婚姻状况:" prop="wedlock">
                  <el-radio-group v-model="emp.wedlock">
                    <el-radio label="已婚">已婚</el-radio>
                    <el-radio label="未婚">未婚</el-radio>
                    <el-radio label="离异">离异</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
        <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false;popVisible =false">取 消</el-button>
    <el-button type="primary" @click="doAddAndUpdateEmp">确 定</el-button>
  </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import {deleteJsonRequest, getJsonRequest, postJsonRequest, putJsonRequest} from "@/utils/api";

export default {
  name: "EmpBasic",
  data() {
    return {
      //搜索框
      keyWork:'',
      /*高级搜索需要的参数*/
      advancedEmp:{
        politicId:null,
        nationId:null,
        posId:null,
        jobLevelId:null,
        engageForm:null,
        departmentId:null
      },
      BigEngDate:null,


      tableData: [],
      emp: {
        name: "",
        gender: "",
        birthday: "",
        idCard: "",
        wedlock: "",
        nationId: 1,
        nativePlace: "",
        politicId: 13,
        email: "",
        phone: "",
        address: "",
        departmentId: null,
        jobLevelId: 9,
        posId: 29,
        engageForm: "",
        tiptopDegree: "",
        specialty: "",
        school: "",
        beginDate: "",
        workState: "",
        workID: "",
        conversionTime: "",
        notWorkDate: null,
        beginContract: "",
        endContract: "",
        workAge: null
      },
      title: '',
      dialogVisible: false,
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,

      tiptopDegrees: ['本科', '大专', '硕士', '博士', '高中', '初中', '小学', '其他'],
      popVisible: false,
      allDeps: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      inputDepName: '',
      nations: [],
      joblevels: [],
      politicsstatuss: [],
      positions: [],
      rules: {
        name: [{required: true, message: '请输入用户名', trigger: 'blur'}],
        gender: [{required: true, message: '请输入性别', trigger: 'blur'}],
        birthday: [{required: true, message: '请输入出生日期', trigger: 'blur'}],
        idCard: [{required: true, message: '请输入身份证号码', trigger: 'blur'}, {
          pattern: /(^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$)|(^[1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{2}$)/,
          message: '身份证号码格式不正确',
          trigger: 'blur'
        }],
        wedlock: [{required: true, message: '请输入婚姻状况', trigger: 'blur'}],
        nationId: [{required: true, message: '请输入您组', trigger: 'blur'}],
        nativePlace: [{required: true, message: '请输入籍贯', trigger: 'blur'}],
        politicId: [{required: true, message: '请输入政治面貌', trigger: 'blur'}],
        email: [{required: true, message: '请输入邮箱地址', trigger: 'blur'}, {
          type: 'email',
          message: '邮箱格式不正确',
          trigger: 'blur'
        }],
        phone: [{required: true, message: '请输入电话号码', trigger: 'blur'}],
        address: [{required: true, message: '请输入员工地址', trigger: 'blur'}],
        departmentId: [{required: true, message: '请输入部门名称', trigger: 'blur'}],
        jobLevelId: [{required: true, message: '请输入职称', trigger: 'blur'}],
        posId: [{required: true, message: '请输入职位', trigger: 'blur'}],
        engageForm: [{required: true, message: '请输入聘用形式', trigger: 'blur'}],
        tiptopDegree: [{required: true, message: '请输入学历', trigger: 'blur'}],
        specialty: [{required: true, message: '请输入专业', trigger: 'blur'}],
        school: [{required: true, message: '请输入毕业院校', trigger: 'blur'}],
        beginDate: [{required: true, message: '请输入入职日期', trigger: 'blur'}],
        workState: [{required: true, message: '请输入工作状态', trigger: 'blur'}],
        workID: [{required: true, message: '请输入工号', trigger: 'blur'}],
        contractTerm: [{required: true, message: '请输入合同期限', trigger: 'blur'}],
        conversionTime: [{required: true, message: '请输入转正日期', trigger: 'blur'}],
        notworkDate: [{required: true, message: '请输入离职日期', trigger: 'blur'}],
        beginContract: [{required: true, message: '请输入合同起始日期', trigger: 'blur'}],
        endContract: [{required: true, message: '请输入合同结束日期', trigger: 'blur'}],
        workAge: [{required: true, message: '请输入工龄', trigger: 'blur'}],
      },
      /*上传文件相关设置*/
      importDataBtnIcon:"el-icon-upload2",
      importDataBtnText:"上传文件",
      disabled:false,

      /*高级搜索相关设置*/
      popVisible2:false,
      inputDepName2:"所属部门",
      showAdvancedView:false
    }
  },
  mounted() {
    this.initTableData()
    this.initData()
  },
  methods: {
    //高级搜索方法
    //点击下拉或者上升时全部置空
    advanceSearch(){
      this.showAdvancedView=!this.showAdvancedView
      this.advancedEmp={
        politicId:null,
        nationId:null,
        posId:null,
        jobLevelId:null,
        engageForm:null,
        departmentId:null
      }
      this.BigEngDate=null
    },

    exportExcel(){
      window.open("/employee/basic/exportEmp","_parent")
    },

    showAddEmpView() {
      //添加时初始化emp信息
      this.emp= {
        name: "",
            gender: "",
            birthday: "",
            idCard: "",
            wedlock: "",
            nationId: 1,
            nativePlace: "",
            politicId: 1,
            email: "",
            phone: "",
            address: "",
            departmentId: null,
            jobLevelId: 9,
            posId: 29,
            engageForm: "",
            tiptopDegree: "",
            specialty: "",
            school: "",
            beginDate: "",
            workState: "",
            workID: "",
            contractTerm: 0,
            conversionTime: "",
            notWorkDate: null,
            beginContract: "",
            endContract: "",
            workAge: null
      },
      this.inputDepName='所属部门'
      this.title="添加员工"
      this.dialogVisible = true
      this.getMaxWordId()
      this.initData()
    },

    showTreeView(){
      this.popVisible=!this.popVisible
    },
    showadvancedTreeView(){
      this.popVisible2=!this.popVisible2
    },

    //工好的最大值  添加员工时需要
    getMaxWordId(){
      getJsonRequest("/employee/basic/MaxworkID").then(resp=>{
        this.emp.workID=resp.obj
      })
    },

    nodeClick(data,node,deps){
      this.popVisible=!this.popVisible
      this.inputDepName=data.name
      this.emp.departmentId=data.id
    },

    nodeAdvancedClick(data,node,deps){
      this.popVisible2=!this.popVisible2
      this.inputDepName2=data.name
      this.advancedEmp.departmentId=data.id
    },

    initTableData() {
      this.loading = true
      let url="/employee/basic/?page=" + this.currentPage + "&size=" + this.pageSize
        if (this.advancedEmp.politicId){
          url+="&politicId="+this.advancedEmp.politicId
        }
        if (this.advancedEmp.nationId){
          url+="&nationId="+this.advancedEmp.nationId
        }
        if (this.advancedEmp.posId){
          url+="&posId="+this.advancedEmp.posId
        }
        if (this.advancedEmp.jobLevelId){
          url+="&jobLevelId="+this.advancedEmp.jobLevelId
        }
        if (this.advancedEmp.engageForm){
          url+="&engageForm="+this.advancedEmp.engageForm
        }
        if (this.advancedEmp.departmentId){
          url+="&departmentId="+this.advancedEmp.departmentId
        }
        if (this.BigEngDate){
          url+="&bigEngDate="+this.BigEngDate[0]+"&bigEngDate="+this.BigEngDate[1]
        }
        if (this.keyWork){
          url+="&name="+this.keyWork
        }
      getJsonRequest(url).then(resp => {
        this.tableData = resp.obj.data
        this.total = resp.obj.total
        this.loading = false
      })
    },

    initData(){
      let joblevel=window.sessionStorage.getItem("joblevels")
      if (!joblevel){
        getJsonRequest("/employee/basic/joblevels").then(resp=>{
          this.joblevels=resp.obj
          window.sessionStorage.setItem("joblevels",JSON.stringify(resp.obj))
        })
      }else {
        this.joblevels=JSON.parse(joblevel);
      }


      let nation=window.sessionStorage.getItem("nations")
      if (!nation){
        getJsonRequest("/employee/basic/nations").then(resp=>{
          this.nations=resp.obj
          window.sessionStorage.setItem("nations",JSON.stringify(resp.obj))
        })
      }else {
        this.nations=JSON.parse(nation)
      }

      let politicsstatus=window.sessionStorage.getItem("politicsstatuss")
      if (!politicsstatus){
        getJsonRequest("/employee/basic/politicsstatus").then(resp=>{
          this.politicsstatuss=resp.obj
          window.sessionStorage.setItem("politicsstatuss",JSON.stringify(resp.obj))
        })
      }else {
        this.politicsstatuss=JSON.parse(politicsstatus)
      }

      let position=window.sessionStorage.getItem("positions")
      if (!position){
        getJsonRequest("/employee/basic/positions").then(resp=>{
          this.positions=resp.obj
          window.sessionStorage.setItem("positions",JSON.stringify(resp.obj))
        })
      }else {
        this.positions=JSON.parse(position)
      }

      let allDep=window.sessionStorage.getItem("allDeps")
      if (!allDep){
        getJsonRequest("/employee/basic/AllDeps").then(resp=>{
          this.allDeps=resp.obj;
          window.sessionStorage.setItem("allDeps",JSON.stringify(resp.obj))
        })
      }else {
        this.allDeps=JSON.parse(allDep)
      }

    },

    //当页面一行显示行数变化时的处理函数
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.initTableData()
    },
    //当前页发生变化的处理函数
    handleCurrentChange(currentPage) {
      this.currentPage = currentPage
      this.initTableData()
    },

    //处理添加员工函数和修改员工
    doAddAndUpdateEmp() {
      //判断一下当前的emp是否有id  有则是修改员工  没有则是添加员工
      if (this.emp.id){
        this.$refs['empForm'].validate(valid=>{
          if(valid){
            putJsonRequest("/employee/basic/",this.emp).then(resp=>{
              this.initTableData()
            })
            this.dialogVisible=false
            this.popVisible=false
          }
        })
      }else {
        this.$refs['empForm'].validate(valid=>{
          if(valid){
            postJsonRequest("/employee/basic/",this.emp).then(resp=>{
              this.initTableData()
            })
            this.dialogVisible=false
            this.popVisible=false
          }
        })
      }
    },

    deleteHandleClick(row){
      deleteJsonRequest("/employee/basic/"+row.id).then(resp=>{
        this.initTableData()
      })
    },

    //修改员工页面和  添加复用
    updateHandleClick(row){
      this.initData()
      this.title="修改员工信息"
      //编辑时 将emp修改为当前编辑员工信息
      this.inputDepName=row.department.name
      this.dialogVisible=true
      this.emp=row
    },


    handleClick() {

    },

    //处理文件上传函数

    //文件上传之前函数
    beforeUpload(){
      this.importDataBtnText="上传中"
      this.importDataBtnIcon="el-icon-loading"
      this.disabled=true
      this.initTableData()
    },

    //上传成功函数
    onSuccess(response, file, fileList){
      this.importDataBtnText="导入数据"
      this.importDataBtnIcon="el-icon-upload2"
      this.disabled=false
      this.initTableData()
    },

    //上传文件失败函数
    onError(err, file, fileList){
      this.importDataBtnText="导入数据"
      this.importDataBtnIcon="el-icon-upload2"
      this.disabled=false
    }

  }
}
</script>

<style>
.title, .search {
  display: flex;
  justify-content: space-between;
}

.title input {
  width: 350px;
}

.title button {
  margin: 0 15px;
}

.content {
  margin: 15px 0;
}

.but {
  padding: 2px;
}
</style>