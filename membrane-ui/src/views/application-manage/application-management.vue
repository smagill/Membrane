<template>
  <div id="application-manage" class="common-root" :style="{minHeight: minHeight + 'px'}">
    <!-- 条件筛选查询 -->
    <div class="select-container">
      <el-col :span="21">
        应用名称
        <el-select placeholder="请选择" v-model="filter.appId" @change="selectAppChange" size="mini">
          <el-option
           v-for="(item,index) in appList"
           :key="index"
           :value="item.clientId"
           :label="item.clientName"
          >
          </el-option>
        </el-select>
        认证方式
        <el-cascader
          v-model="filter.status"
          size="mini"
          :options="authStatus"
          @change="authChange"
        >
        </el-cascader>
        验证方式
        <el-select placeholder="请选择" v-model="filter.validMode" @change="validChange" size="mini">
          <el-option
          v-for="(item,index) in validates"
          :key="index"
          :value="item.modeId"
          :label="item.modeName"
          >
          </el-option>
        </el-select>
      </el-col>
      <el-col :span="3" class="fr" style="">
        <el-button type="primary" size="mini" style="border:none" icon="el-icon-plus" @click="addAppDialogVisible = true">新增应用</el-button>
      </el-col>
    </div>
    <!-- 列表区 -->
    <div class="table-container">
      <template>
        <el-table ref="table" :data="tabData" highlight-current-row>
          <el-table-column min-width="150" label="应用ID" prop="clientId" width="100">
          </el-table-column>
          <el-table-column label="应用名称" prop="clientName">
          </el-table-column>
          <el-table-column label="创建时间" prop="createTime">
            <template slot-scope="scope">
              {{scope.row.createTime }}
            </template>
          </el-table-column>
          <el-table-column label="认证状态" prop="authentication">
            <template slot-scope="scope">
              {{scope.row.authentication == null ? '--' :scope.row.authentication == 0 ? '未认证' : authCode(scope.row.authentication)}}
            </template>
          </el-table-column>
          <el-table-column label="验证方式" prop="tokenCheckType">
            <template slot-scope="scope">
              {{scope.row.tokenCheckType == null ? '--' : scope.row.tokenCheckType == "0" ? '安全模式' : '性能模式'}}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <el-button size="mini" type="text" class="operate" @click="toggleExpand(scope.row)">
                详情<i :class="!isExpand ? 'el-icon-caret-bottom' : 'el-icon-caret-top'"></i>
              </el-button>
            </template>
          </el-table-column>
          <!-- 表格中详情折叠布局 -->
          <el-table-column type="expand" width="1">
            <template slot-scope="props">
              <el-row>
                <el-col class="col-card">
                  <!-- 应用基本信息管理 -->
                  <app-information class="app-child" :appDetail="toChild" @refreshTable="childByValue"></app-information>
                  <!-- 应用认证管理 -->
                  <authentication-manage class="app-child" :appDetail="toChild"></authentication-manage>
                  <!-- 应用权限录入管理 -->
                  <permission-manage class="app-child" :appDetail="toChild"></permission-manage>
                </el-col>
              </el-row>
            </template>
          </el-table-column>
        </el-table>
      </template>
      <div class="paging fr">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="page.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="page.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="page.total"
          v-if="page.total"
        ></el-pagination>
      </div>
    </div>
    <!-- 新增应用 -->
    <el-dialog
      title="新增应用"
      :visible.sync="addAppDialogVisible"
      width="746px"
      top="20vh"
      class="appDialog"
      @close="resetForm('addAppForm')">
      <div class="app-content">
        <template>
          <el-form :model="addAppForm" :rules="addAppRules" ref="addAppForm" label-width="91px">
            <el-form-item label="应用logo" size="mini" label-width="81px" class="mg10">
              <!-- 应用logo上传 -->
              <div class="uploadLogo">
                <img alt="logo" src="../../assets/image/login-bg.png" />
                <el-upload
                  class="upload-demo"
                  action=""
                  list-type="picture">
                  <el-button size="small" type="text">上传</el-button>
                </el-upload>
              </div>
            </el-form-item>
            <el-form-item label="应用名称" size="mini" prop="clientName">
              <el-input placeholder="请输入应用名称" v-model="addAppForm.clientName"></el-input>
            </el-form-item>
            <el-form-item label="App Key" size="mini" prop="appId">
              <el-input placeholder="请输入4-16位由字母、数字组合而成的字符" v-model="addAppForm.appId"></el-input>
            </el-form-item>
            <el-form-item label="App Secret" size="mini"  label-width="81px" class="mg10">
              <el-button type="text" :disabled="true">重置</el-button>
            </el-form-item>
          </el-form>
        </template>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addAppDialogVisible = false; resetForm('addAppForm')">取消</el-button>
        <el-button type="primary" @click="addApplication('addAppForm')">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import {
  applicationList,
  addAppInfo,
} from "../../api/api.js";
import md5 from 'js-md5';
import constant from "../../labs/constant"
import appInformation from "./components/information-manage";
import authenticationManage from "./components/authentication-manage";
import permissionManage from "./components/permission-manage";
import { getDate } from '../../labs/tools';

export default {
  data(){
    return{
      minHeight: null,
      selectedApp:null,
      appList:null,//应用列表
      authStatus:null,//认证状态列表
      validates:null,//验证方式
      tabData:[],
      toChild:null,//传值给组件
      page: {
        pageSize: 10,
        pageNum: 1,
        total:0
      },
      filter:{
        appId:null,
        status:[null],
        validMode:null
      },//筛选条件
      activeName:'1',
      isExpand:false,//用于控制列表中详情是否展开
      addAppDialogVisible:false,
      addAppForm:{
        clientName:null,
        appId:null,
        appScert:null
      },//新增应用form
      addAppRules:{
        clientName:[
          { required : true, message : "请输入应用名称!", trigger : "blur" }
        ],
        appId:[
          { required : true, message : "请输入应用ID!", trigger : "blur" },
          { pattern:/^[a-zA-Z][a-zA-Z0-9]{3,15}$/, message:"请输入4-16位由字母、数字组合而成的字符!", trigger:'blur'}
        ]
      },//新增应用表单验证
    }
  },
  components:{
    appInformation,
    authenticationManage,
    permissionManage
  },
  methods:{
    getAppList(){
      /**
       * @description 获取应用列表
         * @param {Number} pageIndex
         * @param {Number} pageSize
         * @param {Number} authentication
         * @param {Number} tokenCheckType
         * @param {Number} verification
         * @param {Number} clientId
       */
      let self = this;
      let param = {
          sign:1,
          clientId:self.filter.appId,
          authentication:self.filter.status[0],
          tokenCheckType:self.filter.validMode,
          verification:self.filter.status.length > 1 ? self.filter.status[1] : null,
          pageIndex:self.pageIndex,
          pageSize:self.page.pageSize
        }
      self.appList = [];
      applicationList(param).then(res=>{
          if(res.err_CODE === 0){
            let appList = [
              { clientId : null,clientName : '全部' },
              ...res.data.list
            ];
            self.appList = appList;
            let list;
            list = res.data.list.map(item =>{
              // 转换时间格式
              item.createTime = item.createTime == null ? '--' : getDate(new Date(item.createTime).getTime()/1000);
              return item;
            })
            self.tabData = list;
            self.page.total = res.data.total;
          }else{
            self.$message.error(res.err_MESSAGE);
          }
        })
    },
    authCode(code){
      switch(code){
        case 0:
           code = "CAS";
           break;
        case 1 :
            code = "OAUTH";
            break;
        case 2:
            code = "SAML";
            break;
      }
      return code;
    },
    handleSizeChange(val) {
      /**
       * @description 分页方法
       */
      this.page.pageSize = val;
      this.getAppList();
    },
    handleCurrentChange(val) {
      /**
       * @description 当前页面查询
       */
      this.page.pageNum = val;
      this.getAppList();
    },
    toggleExpand(row) {
      /**
       * 详情展开交互事件
       */
      let $table = this.$refs.table;
      this.tabData.forEach((item) => {
        if (row.clientId != item.clientId) {
          $table.toggleRowExpansion(item, false)
        }
      })
      this.isExpand = !this.isExpand;
      this.toChild = row;
      $table.toggleRowExpansion(row);
    },
    addApplication(formName){
      /**
       * @description 新增应用
       * @param {String} appID 应用ID
       * @param {String} secret
       * @param {String} clientName 应用名称
       * @param {String} imgUrl logo Url
       */
      let self = this;
      //MD5加密后的APP Secret 进行加盐
      let encrypt = (Math.random()*9000+1000).toString().split('.')[0];
      self.$refs[formName].validate(valid =>{
        if(valid){
          // 使用MD5加密算法对输入的APPID进行加密后生成对应的APP Secret
          self.addAppForm.secret = md5(self.addAppForm.appId);
          let param = {
            appId:self.addAppForm.appId,
            secret:`${self.addAppForm.secret}${encrypt}`,
            clientName:self.addAppForm.clientName,
            imgUrl:''
          };
          addAppInfo(param).then(res=>{
            self.addAppDialogVisible = false;
            self.getAppList();
            self.$message.success(res.err_MESSAGE);
          })
        }else{
          return false;
        }
      })
    },
    resetForm(formName){
      /**
       * @description 弹出框关闭事件
       * @param {String} formName 表单名称
       */
      this.$refs[formName].resetFields();
    },
    childByValue(value){
      /**
       * @description 接收子组件传值给父组件
       * @param {Boolean} value 是否刷新表格
       */
      value ? this.getAppList() : '';
    },
    authChange(val){
      /**
       * @description 认证状态改变事件
       */
      this.filter.status = val;
      this.getAppList();
    },
    selectAppChange(val){
      /**
       * @description 根据选择的应用查询应用列表
       */
      this.filter.appId = val;
      this.getAppList();
    },
    validChange(val){
      /**
       * @description 根据验证方式查询应用列表
       */
      this.filter.validMode = val;
      this.getAppList();
    }
  },
  mounted(){
    this.minHeight = window.innerHeight - 95;
    this.getAppList();
  },
  created(){
    // 筛选下拉框数据渲染
    this.validates = constant.validMode;
    this.authStatus = constant.authStatus;
  }
}
</script>

<style lang="scss">
  @import "../../styles/application-manage/application-management.scss";
</style>


