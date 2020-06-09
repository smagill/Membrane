<template>
  <div class="home-cointer"  id="main">
    <el-container>
      <el-aside width="200px">
        <div class="logo-container">
          <span class="logoName">Membrane</span>
        </div>
        <nav-left></nav-left>
      </el-aside>
      <el-container>
        <el-header>
          <div class="fr com-height">
            <el-dropdown trigger="click" @command="userSettings" @visible-change="vChange">
              <span style="cursor: pointer;">
                <i class="iconfont icon-user" style="color:#BCCEDA;"></i>
                <span>{{name}}</span>
                <i v-if="!listIon" class="el-icon-arrow-down"></i>
                <i v-if="listIon" class="el-icon-arrow-up"></i>
              </span>
              <el-dropdown-menu class="setting" slot="dropdown">
                <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
                <el-dropdown-item command="logOut">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>
        <el-main style="height:100%;">
          <el-col :span="24" class="breadcrumb-container nav-title">
            <el-breadcrumb separator=">" class="breadcrumb-inner">
              <el-breadcrumb-item
                v-for="item in navMenu"
                :key="item.path"
                :class="item.colClass"
              >{{ item.name }}</el-breadcrumb-item>
            </el-breadcrumb>
          </el-col>
          <el-col :span="24" id="content-wrapper" class="content-wrapper" style="display:block;">
            <transition name="fade" mode="out-in">
              <router-view></router-view>
            </transition>
          </el-col>
          <!--修改密码-->
          <el-dialog
            class="plusProModel"
            id="passwordDialog"
            title="修改密码"
            :visible.sync="passwordDialog"
            width="746px"
            :before-close="cancelChange"
            top="23vh"
          >
            <el-form :model="passwordForm" ref="passwordForm" @submit.native.prevent>
              <el-form-item label="原密码" required label-width="90px">
                <el-input
                  type="password"
                  minlength="6"
                  maxlength="12"
                  v-model.trim="passwordForm.oldPassword"
                  autocomplete="off"
                  placeholder="请输入原密码"
                  size="mini"
                ></el-input>
                <div id="oldPasswordError">{{passwordForm.oldPasswordError}}</div>
              </el-form-item>
              <el-form-item label="新密码" required label-width="90px">
                <el-input
                  type="password"
                  minlength="6"
                  maxlength="12"
                  v-model.trim="passwordForm.newPassword"
                  autocomplete="off"
                  placeholder="长度6~12位，支持大写字母、小写字母、数字、标点符号，并且至少包括其中两种"
                  size="mini"
                ></el-input>
                <div id="newPasswordError">{{passwordForm.newPasswordError}}</div>
              </el-form-item>
              <el-form-item label="确认新密码" required label-width="90px">
                <el-input
                  type="password"
                  minlength="6"
                  maxlength="12"
                  v-model.trim="passwordForm.surePassword"
                  autocomplete="off"
                  placeholder
                  size="mini"
                ></el-input>
                <div id="surePasswordError">{{passwordForm.surePasswordError}}</div>
              </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
              <el-button @click="cancelChange">取消</el-button>
              <el-button type="primary" @click="sureChange(passwordForm)">确定</el-button>
            </span>
          </el-dialog>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script>
import navLeft from "../components/nav-left.vue";
import { resetPassword, loginLogout,appId } from "../api/api.js";
import md5 from "js-md5"
export default {
  name: "home",
  components: {
    navLeft
  },
  data() {
    return {
      showOneMenu: false,
      navMenu: [],
      isClear: false,
      passwordDialog: false,
      options: [],
      name: "王小雨",
      passwordForm: {
        userId: null,
        oldPassword: null,
        newPassword: null,
        surePassword: null,
        oldPasswordError: null,
        newPasswordError: null,
        surePasswordError: null
      },
      listIon: false
    };
  },
  created() {},
  mounted() {
    this.handlerBread(this.$route.matched);
    if (localStorage.userName) {
      this.name = localStorage.userName;
    }
  },
  watch: {
    $route: {
      handler: function(val, oldVal) {
        this.handlerBread(val.matched);
        let wrapper = document.getElementById("content-wrapper"); // 让滚动条置顶
        wrapper.scrollTop = 0;
      },
      // 深度观察监听
      deep: true
    }
  },
  methods: {
    userSettings(value) {
      /**
       * @description 页面右上角 用户信息管理：修改密码、退出登录
       */
      if (value == "changePassword") {
        this.passwordDialog = true;
      } else if (value == "logOut") {
        /**
         * @description 退出登录
         * @param {String} appId 应用ID
         * @param {String} token 令牌
         */
        let sso_token = window.localStorage.getItem("sso_token");
        let obj = { appId: appId, token: sso_token };
        loginLogout(obj).then(res => {
          if(res.err_CODE==0){
            this.$message.success(res.err_MESSAGE);
            window.localStorage.removeItem("sso_token");
            this.$router.push({path:'/'});
          }else if(res.err_CODE==4099){ //无效的token
            this.$message.error(res.err_MESSAGE)
            location.reload();
          }else{
            this.$message.error(res.err_MESSAGE);
          }
        });
      }
    },
    vChange(value) {
      this.listIon = value;
    },
    cancelChange() {
      /**
       * @description 修改密码 弹出框取消
       */
      this.passwordDialog = false;
      this.$refs["passwordForm"].resetFields();
      this.$set(this.passwordForm, "oldPassword", null);
      this.$set(this.passwordForm, "newPassword", null);
      this.$set(this.passwordForm, "surePassword", null);
      this.passwordForm.oldPasswordError = this.passwordForm.newPasswordError = this.passwordForm.surePasswordError = null;
    },
    sureChange(value) {
      /**
       * @description 修改密码
       * @param {Number} userId
       * @param {String} appId 应用ID
       * @param {String} oldPassword 原密码
       * @param {String} newPassword 新密码
       * @param {String} rePassword 确认密码
       * @param {Number} flag
       */
      this.passwordForm.oldPasswordError = this.passwordForm.newPasswordError = this.passwordForm.surePasswordError = null;
      if (
        this.passwordForm.oldPassword == null ||
        this.passwordForm.oldPassword == ""
      ) {
        this.passwordForm.oldPasswordError = "请输入原密码";
      }
      if (
        this.passwordForm.newPassword == null ||
        this.passwordForm.newPassword == ""
      ) {
        this.passwordForm.newPasswordError = "请输入新密码";
      }
      if (
        this.passwordForm.surePassword == null ||
        this.passwordForm.surePassword == ""
      ) {
        this.passwordForm.surePasswordError = "请确认新密码";
      }
      if (this.passwordForm.newPassword != this.passwordForm.surePassword) {
        this.passwordForm.surePasswordError = "新密码输入不一致";
      }
      if (
        this.passwordForm.oldPasswordError ||
        this.passwordForm.oldPasswordError ||
        this.passwordForm.oldPasswordError
      ) {
        return;
      }
      let params = {
        userId: localStorage.userId,
        appId:appId,
        oldPassword: md5(this.passwordForm.oldPassword),
        newPassword: md5(this.passwordForm.newPassword),
        rePassword:  md5(this.passwordForm.surePassword),
        flag:1
      };
      resetPassword(params).then(res => {
        if (res.err_CODE == 0) {
          this.$message({ message: res.err_MESSAGE, type: "success" });
          this.cancelChange();
        }else{
          if (res.data.oldPassword) {
            this.passwordForm.oldPasswordError = res.data.oldPassword;
          }
          if (res.data.newPassword) {
            this.passwordForm.newPasswordError = res.data.newPassword;
          }
          if (res.data.rePassword) {
            this.passwordForm.surePasswordError = res.data.rePassword;
          }
        }
      });
    },
    copy(arr) {
      /**
       * @description 角色
       */
      let self = this;
      let obj = arr.constructor == Array ? [] : {};
      for (var item in arr) {
        if (typeof arr[item] === "object") {
          obj[item] = self.copy(arr[item]);
        } else if (typeof arr[item] === "number") {
          obj[item] = arr[item] + "";
        } else {
          obj[item] = arr[item];
        }
      }
      return obj;
    },
    handlerBread(val) {
      /**
       * @description 登录用户信息展示
       */
      let navArr = [];
      val.forEach(i => {
        navArr.push({ path: i.path, name: i.name, colClass: "" });
      });
      navArr.shift();

      navArr.forEach((item, idx) => {
        if (navArr.length == 1) {
          item.colClass = "default-col";
        } else if (idx == 0) {
          item.colClass = "parent-col";
        } else if (idx != 0) {
          item.colClass = "default-col";
        }
      });
      this.navMenu = navArr;
    }
  }
};
</script>
<style>

</style>
<style lang="scss" scoped>
@import "../styles/main.scss";
</style>
