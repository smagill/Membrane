<template>
<!-- 认证管理 -->
  <div class="appComponent">
    <el-card>
      <div slot="header" class="clearfix">
        <span>认证管理</span>
      </div>
      <div class="text item">
        <div class="content">
          应用的详细信息
        </div>
        <div class="bottom clearfix">
          <el-button type="text" class="button detail" @click="detailVisible=true">查看详情</el-button>
          <el-button type="text" class="button edit" @click="configVisible=true">认证配置</el-button>
        </div>
      </div>
    </el-card>
    <!-- 认证配置 -->
    <el-dialog
      title="认证管理"
      :visible.sync="configVisible"
      width="746px"
      height="400px"
      top="20vh"
      class="appDialog">
      <div class="app-content">
        <template>
          <el-form :model="authConfigForm" ref="authConfigForm" label-width="91px" >
            <el-form-item label="启用认证" size="mini" class="appId">
              <el-switch active-value="1" inactive-value="0" v-model="authConfigForm.authentication"  @change="isEnable = !isEnable;"></el-switch>
              <el-radio-group v-model="authConfigForm.verification" size="mini" :disabled="authConfigForm.authentication == '0'">
                <el-radio v-for="(item,index) in authStatus[0].children" :label="item.value" :key="index" border>{{item.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="验证方式" size="mini" class="appId mgl">
              <el-radio-group v-model="authConfigForm.tokenCheckType" size="mini">
                <el-radio v-for="(item,index) in validMode" :key="index" :label="item.modeId"  border>{{item.modeName}}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="Access Token" size="mini">
              <el-input-number size="mini" v-model="authConfigForm.accessValidity"></el-input-number>
            </el-form-item>
          </el-form>
        </template>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="configVisible=false">取消</el-button>
        <el-button type="primary" @click="authConfig">确定</el-button>
      </span>
    </el-dialog>
    <!-- 认证管理详情 -->
    <el-dialog
      title="认证管理详情"
      :visible.sync="detailVisible"
      width="746px"
      top="20vh"
      class="appDialog detail-dialog">
      <div class="app-content dialog-content">
        <ul class="certificate-detail">
          <li><span class="label-name">认证状态：</span><span class="label-value">{{appDetails.authentication ? `已认证：`+authCode(appDetails.verification) : '未认证'}}</span></li>
          <li><span class="label-name">验证方式：</span><span class="label-value">{{appDetails.tokenCheckType === 0 ? '安全模式' : '性能模式'}}</span></li>
          <li><span class="label-name">Access Token(m)：</span><span class="label-value">{{appDetails.accessValidity}}分钟</span></li>
        </ul>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import constant from "../../../labs/constant";
import {
  authConfig
} from "../../../api/api";
export default {
  props:['appDetail'],
  data(){
    return{
      configVisible:false,
      detailVisible:false,
      switchStatus:false,//认证状态未启用
      authType:null,//认证类型
      validate:null,
      tokenDate:1,
      isEnable:true,//是否启用
      appDetails:null,
      authStatus:null,
      validMode:null,
      authConfigForm:{
        clientId:null,
        accessValidity:null,
        authentication:null,
        tokenCheckType:null,
        verification:null
      }
    }
  },
  mounted(){

  },
  created(){
    this.appDetails = JSON.parse(JSON.stringify(this.appDetail)) ;
    this.authConfigForm = this.appDetails;
    this.authConfigForm.authentication = this.appDetails.authentication+'';

    // 认证select 数据绑定
    this.authStatus = constant.authStatus.filter(item => item.value == 1);
    // 验证select 数据绑定
    this.validMode = constant.validMode.filter(item => item.modeId != null);
  },
  methods:{
    authConfig(){
      /**
       * @description 认证配置
       * @param {Number} clientId
       * @param {Number} accessValidity
       * @param {Number} authentication
       * @param {Number} tokenCheckType
       * @param {Number} verification
       */
      let param = {
        clientId:this.appDetails.clientId,
        accessValidity:this.authConfigForm.accessValidity,
        authentication:this.authConfigForm.authentication,
        tokenCheckType:this.authConfigForm.tokenCheckType,
        verification:this.authConfigForm.verification
      };
      authConfig(param).then(res=>{
        if(res.err_CODE === 0){
          this.configVisible = false;
          this.$emit("refreshTable",true)
          this.$message.success(res.err_MESSAGE);
        }else{
          this.$message.error(res.err_MESSAGE);
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
    }
  }
}
</script>
<style lang="scss">
  @import "../../../styles/application-manage/application-component.scss";
</style>
