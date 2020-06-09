<template>
<!-- 应用基本信息管理 -->
  <div class="appComponent">
    <el-card>
      <div slot="header" class="clearfix">
        <span>应用信息</span>
      </div>
      <div class="text item">
        <div class="content">
          应用的详细信息
        </div>
        <div class="bottom clearfix">
          <el-button type="text" class="button detail" @click="AppDetailDialogVisible = true">查看详情</el-button>
          <el-button type="text" class="button edit" @click="editAppDialogVisible = true">编辑应用</el-button>
          <el-button type="text" class="button delete" @click="delAppDialogVisible = true">删除应用</el-button>
        </div>
      </div>
    </el-card>
    <!-- 编辑应用 -->
    <el-dialog
      title="编辑应用"
      :visible.sync="editAppDialogVisible"
      width="746px"
      top="20vh"
      class="appDialog"
      @close="resetForm('editAppForm')">
      <div class="app-content" >
        <template>
          <el-form :model="editAppForm" :rules="editAppRules" ref="editAppForm" label-width="91px">
            <el-form-item label="应用ID" size="mini" class="appId mg10" label-width="81px">
              <span class="appId">{{editAppForm.clientId}}</span>
            </el-form-item>
            <el-form-item label="应用logo" size="mini" class="mg10" label-width="81px">
              <!-- 应用logo上传 -->
              <div class="uploadLogo">
                <img alt="logo" src="../../../assets/image/login-bg.png" />
                <el-upload
                  class="upload-demo"
                  action=""
                  list-type="picture">
                  <el-button size="small" type="text">上传</el-button>
                </el-upload>
              </div>
            </el-form-item>
            <el-form-item label="应用名称" size="mini" prop="clientName">
              <el-input placeholder="请输入应用名称" v-model="editAppForm.clientName"></el-input>
            </el-form-item>
            <el-form-item label="App Key"  class="appId" size="mini">
              <span>{{editAppForm.appId}}</span>
            </el-form-item>
            <el-form-item label="App Secret" size="mini" class="appId"  label-width="81px">
              <el-button type="text" :disabled="true" class="showColor">{{isShow ? '******' : editAppForm.secret}}</el-button>
              <el-button type="text" :disabled="false" @click="isShow = !isShow">{{isShow ? '显示' : '隐藏'}}</el-button>
              <el-button type="text" :disabled="false" @click="resetSecretHandle">重置</el-button>
            </el-form-item>
          </el-form>
        </template>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editAppDialogVisible = false;resetForm('editAppForm')">取消</el-button>
        <el-button type="primary" @click="editApp('editAppForm')">确定</el-button>
      </span>
    </el-dialog>
    <!-- 应用基本信息查看 -->
    <el-dialog
      title="应用详情"
      :visible.sync="AppDetailDialogVisible"
      width="746px"
      height="400px"
      top="20vh"
      class="appDialog detail-dialog"
      :before-close="()=>AppDetailDialogVisible = false">
      <div class="app-content dialog-content" >
        <ul class="app-detail">
          <li class="li-logo">
            <span class="label-name">应用logo：</span>
            <span class="label-value"><img alt='logo' src="../../../assets/image/login-bg.png" /></span>
          </li>
          <li>
            <span class="label-name">应用ID：</span>
            <span class="label-value">{{appDetails === null ? '--' : appDetails.clientId}}</span>
          </li>
          <li>
            <span class="label-name">应用名称：</span>
            <span class="label-value">{{appDetails === null ? '--' : appDetails.clientName}}</span>
          </li>
          <li>
            <span class="label-name">App ID：</span>
            <span class="label-value">{{appDetails === null ? '--' : appDetails.appId}}</span>
          </li>
          <li>
            <span class="label-name">App Secret：</span>
            <span class="label-value">{{appDetails === null ? '--' : appDetails.secret}}</span>
          </li>
          <li>
            <span class="label-name">创建时间：</span>
            <span class="label-value">{{appDetails === null ? '--' : appDetails.createTime}}</span>
          </li>
        </ul>
      </div>
    </el-dialog>
    <!-- 删除应用 -->
    <el-dialog
      title="删除应用"
      :visible.sync="delAppDialogVisible"
      width="746px"
      top="20vh"
      class="appDialog"
      :before-close="()=>delAppDialogVisible=false">
      <div class="app-content">
        确定要删除应用：<i class="clientName">{{appDetail.clientName}}</i>吗？删除后不可恢复！
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="delAppDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="deleteApp">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {resetSecret,getDate} from "../../../labs/tools"
import {
  editAppInfo,
  delApp
} from "../../../api/api"
export default {
  props:{
    appDetail:Object
  },
  data(){
    return{
      appDetails:null,
      addAppDialogVisible:true,
      AppDetailDialogVisible:false,
      editAppDialogVisible:false,
      delAppDialogVisible:false,
      isShow:true,
      editAppForm:{
        clientId:null,
        clientName:null,
        appId:null,
        secret:null,
        url:null,
        createTime:null,
        authentication:null,
        verification:null,
        creatorId:null,
        updaterId:null
      },//编辑应用form
      editAppRules:{
        clientName:[
          { required : true, message : "请输入应用名称!", trigger : "blur" }
        ]
      },//编辑应用表单验证
    }
  },
  mounted(){
    this.editAppForm = JSON.parse(JSON.stringify(this.appDetails));
  },
  created(){
    this.appDetail !== undefined ? this.appDetails = JSON.parse(JSON.stringify(this.appDetail)) : this.appDetails = null;
    this.appDetails.createTime = getDate(new Date(this.appDetails.createTime).getTime()/1000);
    console.log(this.appDetails);
  },
  methods:{
    deleteApp(){
      /**
       * @description 删除应用
       * @param {Number} clientId 应用ID
       */
      let param = {clientId: this.appDetails.clientId};
      delApp(param).then(res=>{
        if(res.err_CODE === 0){
          this.delAppDialogVisible = false;
          this.$message.success(res.err_MESSAGE);
          this.$emit("refreshTable",true);
        }else{
          this.$message.error(res.err_MESSAGE);
        }
      })

    },
    resetSecretHandle(){
      /**
       * @description  根据APPID重置APP Secret
       */
      this.editAppForm.secret = resetSecret(this.editAppForm.appId)
    },
    editApp(formName){
      let self = this;
      let param = {
        clientId:self.editAppForm.clientId,
        clientName:self.editAppForm.clientName,
        appId:self.editAppForm.appId,
        imgUrl:null,
        secret:self.editAppForm.secret
      };
      self.$refs[formName].validate((valid) =>{
        if(valid){
          editAppInfo(param).then(res=>{
            if(res.err_CODE === 0){
              self.editAppDialogVisible = false;
              self.$message.success(res.err_MESSAGE);
              self.$emit('refreshTable',true)
            }else{
              self.$message.error(res.err_MESSAGE);
            }
          })
        }else{
          return false;
        }

      })
    },
    resetForm(formName){
      /**
       * @description form表单重置
       */
      this.$refs[formName].resetFields();
    }
  }
}
</script>
<style lang="scss">
  @import "../../../styles/application-manage/application-component.scss";
</style>
