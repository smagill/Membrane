<template>
<!-- 权限录入 -->
  <div class="appComponent">
    <el-card>
      <div slot="header" class="clearfix">
        <span>权限录入</span>
      </div>
      <div class="text item">
        <div class="content">
          应用的组织授权关系
        </div>
        <div class="bottom clearfix">
          <el-button type="text" class="button detail" @click="permissionDetailVisible = true;getPermission(appDetails.clientId,null)">查看详情</el-button>
          <el-button type="text" class="button edit" @click="permissionHandle">编辑权限</el-button>
        </div>
      </div>
    </el-card>
    <!-- 权限录入 -->
    <el-dialog
      title="权限录入"
      :visible.sync="permissionVisible"
      width="746px"
      height="600px"
      top="20vh"
      class="appDialog permisson-dialog"
      @close="()=>{permissionVisible = false}">
      <div class="app-content">
        <el-container>
          <!-- 左侧树形模块 -->
          <el-aside width="180px" class="aside-dialog">
            <el-tree
              :data="permissionList"
              :props="defaultProps"
              node-key="id"
              default-expand-all
              :default-expanded-keys="checkedNode"
              :default-checked-keys ="checkedNode"
              accordion
              highlight-current
              @node-click="handleNodeClick">
            </el-tree>
          </el-aside>
          <el-main class="main-dialog">
            <!-- 功能列表区域 -->
            <el-row class="btn">
              <el-button type="primary" class="delsBtn" size="mini" @click="delHandle">批量删除</el-button>
              <el-button v-if="mdShow" type="primary" size="mini" @click="addModuleVisible=true">新增模块</el-button>
              <el-button v-else type="primary" size="mini" @click="addFunctionVisible=true">新增功能</el-button>
            </el-row>
            <!-- 应用下所有模块数据表格 -->
            <template>
              <el-table
                v-if="mdShow"
                ref="moduleTb"
                :data="moduleData"
                @selection-change="handleSelectionChange">
                <el-table-column
                  type="selection"
                  width="45">
                </el-table-column>
                <el-table-column label="模块名称" prop="moduleName">
                </el-table-column>
                <el-table-column label="操作" width="120">
                  <template slot-scope="scope">
                    <i class="el-icon-edit" @click="editDialogHandle('editModuleForm',scope.row)"></i>
                    <i v-show="scope.row.functionList.length === 0" class="el-icon-delete" @click="del(scope.row)"></i>
                  </template>
                </el-table-column>
              </el-table>
            <!-- 模块下所有功能数据表格 -->
              <el-table
                v-else
                ref="funTb"
                :data="functionData"
                @selection-change="handleSelectionChange">
                <el-table-column
                  type="selection"
                  width="45">
                </el-table-column>
                <el-table-column label="功能名称" prop="functionName">
                </el-table-column>
                <el-table-column label="功能类型" prop="apiType">
                  <template slot-scope="scope">
                    {{scope.row.apiType === 0 ? '编辑类' : '查询类'}}
                  </template>
                </el-table-column>
                <el-table-column label="URL">
                  <template slot-scope="scope">
                  <el-tooltip :content="scope.row.apiUrlList" placement="top" effect="light">
                     <span>{{scope.row.url}}</span>
                  </el-tooltip>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="70">
                  <template slot-scope="scope">
                    <i class="el-icon-edit" @click="editDialogHandle('editFunForm',scope.row)"></i>
                    <i class="el-icon-delete" @click="del(scope.row)"></i>
                  </template>
                </el-table-column>
              </el-table>
            </template>
          </el-main>
        </el-container>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="permissionVisible=false">取消</el-button>
        <el-button type="primary" @click="permissionVisible=false">确定</el-button>
      </span>
    </el-dialog>
    <!-- 权限录入详情 -->
    <el-dialog
      title="权限录入详情"
      :visible.sync="permissionDetailVisible"
      width="746px"
      top="20vh"
      class="appDialog permisson-dialog permission-detail">
      <div class="app-content">
        <el-tree :default-expand-all="true" :data="permissionDetail" :props="defaultProps" @node-click="handleNodeClick"></el-tree>
      </div>
    </el-dialog>
    <!-- 新增模块 -->
    <el-dialog
      title="新增模块"
      :visible.sync="addModuleVisible"
      width="404px"
      top="36vh"
      height="178px"
      class="appDialog add-module"
      :before-close="()=>{addModuleVisible = false}">
      <div class="app-content">
        <el-form :model="moduleForm" :rules="moduleRules" ref="moduleForm" label-width="81px" >
          <el-form-item label="模块名称" prop="moduleName" size="mini">
            <el-input v-model="moduleForm.moduleName"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer footer-fl" style="text-align:right">
        <el-button @click="cancelHandle('moduleForm')">取消</el-button>
        <el-button type="primary" @click="addAppModule('moduleForm')">确定</el-button>
      </span>
    </el-dialog>
    <!-- 编辑模块 -->
    <el-dialog
      title="编辑模块"
      :visible.sync="editModuleVisible"
      width="404px"
      top="36vh"
      height="178px"
      class="appDialog add-module"
      >
      <div class="app-content">
        <el-form :model="editModuleForm" :rules="moduleRules" ref="editModuleForm" label-width="81px" >
          <el-form-item label="模块名称" prop="moduleName" size="mini">
            <el-input v-model="editModuleForm.moduleName"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer footer-fl" style="text-align:right">
        <el-button @click="cancelHandle('editModuleForm')">取消</el-button>
        <el-button type="primary" @click="updateModule('editModuleForm')">确定</el-button>
      </span>
    </el-dialog>
    <!-- 新增功能 -->
    <el-dialog
      title="新增功能"
      :visible.sync="addFunctionVisible"
      width="604px"
      top="36vh"
      class="appDialog add-module">
      <div class="app-content">
        <el-form :model="functionForm" :rules="functionRules" ref="functionForm" label-width="81px" >
          <el-form-item label="功能名称" prop="functionName" size="mini">
            <el-input v-model="functionForm.functionName"></el-input>
          </el-form-item>
          <el-form-item label="功能类型" size="mini">
            <el-select v-model="functionForm.apiType">
              <el-option v-for="(item,index) in functionType" :key="index" :label="item.fName" :value="item.fId">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="URL" prop="apiUrlList" size="mini">
            <el-input v-model="functionForm.apiUrlList"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer footer-fl" style="text-align:right">
        <el-button @click="cancelHandle('functionForm')">取消</el-button>
        <el-button type="primary" @click="addAppModule('functionForm')">确定</el-button>
      </span>
    </el-dialog>
    <!-- 编辑功能 -->
    <el-dialog
      title="编辑功能"
      :visible.sync="editFunctionVisible"
      width="604px"
      top="36vh"
      class="appDialog add-module">
      <div class="app-content">
        <el-form :model="editFunForm" :rules="functionRules" ref="editFunForm" label-width="81px" >
          <el-form-item label="功能名称" prop="functionName" size="mini">
            <el-input v-model="editFunForm.functionName"></el-input>
          </el-form-item>
          <el-form-item label="功能类型" size="mini">
            <el-select v-model="editFunForm.apiType">
              <el-option v-for="(item,index) in functionType" :key="index" :label="item.fName" :value="item.fId">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="URL" prop="apiUrlList" size="mini">
            <el-input v-model="editFunForm.apiUrlList"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer footer-fl" style="text-align:right">
        <el-button @click="cancelHandle('editFunForm')">取消</el-button>
        <el-button type="primary" @click="updateFunc('editFunForm')">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getModuleById,
  addAppModule,
  editAppModule,
  editAppFunction,
  delAppModule,
  delAppFunction
} from "../../../api/api";
import constant from "../../../labs/constant"
export default {
  props:['appDetail'],
  data(){
    return{
      appDetails:null,
      permissionVisible:false,
      permissionDetailVisible:false,
      mdShow:true,
      AppDetailDialogVisible:false,
      addModuleVisible:false,
      addFunctionVisible:false,
      editModuleVisible:false,
      editFunctionVisible:false,
      functionType:[],
      defaultProps:{
        children:'children',
        label:'label',
      },
      publicParam:{
        clientId:null,
        moduleId:null
      },//公共参数
      checkedNode:[1],//默认权限列表展开的节点
      permissionList:[],
      moduleData:[],
      functionData:[],
      checkModules:[],
      checkFunctions:[],
      moduleForm:{
        moduleName:''
      },//新增模块Form
      editModuleForm:{
        moduleName:''
      },//编辑模块Form
      moduleRules:{
        moduleName:[
          { required:true, message:'请输入模块名称', trigger:'blur' },
          { min:3, max:50, message:'长度在2到50个字符！', trigger:'blur' }
        ]
      },
      appPermission:null,
      delModuleParam:[],
      delFunParam:[],
      functionForm:{
        functionName:null,
        apiUrlList:null,
        moduleId:null,
        clientId:null,
        moduleName:null,
        apiType:1
      },//新增功能Form
      editFunForm:{
        functionId:null,
        functionName:null,
        apiUrlList:null,
        moduleId:null,
        apiType:0
      },//编辑功能Form
      functionRules:{
        functionName:[
          { required:true, message:'请输入功能名称', trigger:'blur' },
          { min:3, max:50, message:'长度在2到50个字符！', trigger:'blur' }
        ],
        apiUrlList:[
          { required:true, message:'请输入url，超过1个时，请以英文逗号(,)隔开',trigger:'blur'}
        ]
      },
      permissionDetail:[],//权限录入详情
    }
  },
  mounted(){
    // 新增功能时，功能类型下拉菜单数据渲染
    this.functionType = constant.functionType;
    this.getPermission(this.appDetails.clientId,null);
  },
  created(){
    this.appDetails = JSON.parse(JSON.stringify(this.appDetail));
  },
  methods:{
    handleNodeClick(data,node){
      /**
       * @description 获取当前选中的节点数据,‘新增功能’ 与 ‘新增模块’按钮切换
       */

      if(data.level == 1){// 点击一级
        this.mdShow = true;
        this.moduleData = this.appPermission.children;

      }else if(data.level == 2){//点击二级
        this.mdShow = false;
        this.functionForm.moduleId = data.moduleId;
        let moduleInfo = this.appPermission.children.filter((item) => item.moduleId == data.moduleId);
        this.functionData = moduleInfo[0].children;

      }else if(data.level == 3){//点击三点
        this.mdShow = false;
        let moduleInfo = this.appPermission.children.filter((item) => item.moduleId == node.parent.data.moduleId);
        this.functionData = moduleInfo[0].children;
      }
    },
    handleSelectionChange(val){
      /**
       * @description 获取批量选中的数据
       */
      this.mdShow ? this.checkModules = val : this.checkFunctions = val;
    },
    addAppModule(formName){
      /**
       * @description 新增模块
       * @param {Number} clientId 应用ID
       * @param {Number} moduleId 模块ID
       * @param {String} moduleName 模块名称
       * @param {Array} functionList 功能列表：新增模块时：functionList:null,新增功能时：functionList[{functionName:'',apiType:'',apiUrlList:''}]
       * @param {String} functionName 功能名称
       * @param {Number} apiType 功能类型：0-编辑类，1-查询类
       * @param {Array} apiUrlList 功能对应的API
       */

      let param ={
        clientId: this.appDetails.clientId,
        moduleId:null,
        moduleName:this.moduleForm.moduleName,
        functionList:[]
      };
      // 根据formName 判断是新增模块还是新增功能,不同的新增传递不同的参数值
      if(formName === "moduleForm"){
        param.moduleId = null;
        param.functionList = [];
      }else{
        param.moduleId = this.functionForm.moduleId;
        param.moduleName = this.functionForm.moduleName;
        param.functionList.push(
          {
            apiType : this.functionForm.apiType,
            functionName: this.functionForm.functionName,
            apiUrlList: this.functionForm.apiUrlList.split(',')
          }
        )
      }
      addAppModule(param).then(res=>{
        this.$refs[formName].validate((valid) =>{
          if(valid){
              if(res.err_CODE === 0){
                this.addModuleVisible = false;
                this.addFunctionVisible = false;
                this.$message.success(res.err_MESSAGE);
                this.$refs[formName].resetFields();
                this.getPermission(param.clientId,null);
                this.getFunList(param.clientId,param.moduleId);
              }else{
                this.$message.error(res.err_MESSAGE);
              }
          }else{
            return false;
          }
        })

      })
    },
    editDialogHandle(formName,data){
      /**
       * @description 模块、功能的编辑交互及Form表单数据获取
       */
      if(formName == 'editModuleForm'){
        this.editModuleVisible = true;
        this.editModuleForm = JSON.parse(JSON.stringify(data));
      }else{
        this.editFunctionVisible = true;
        //转换成form表单中需要的数据类型（数组转换成字符串）
        this.editFunForm = JSON.parse(JSON.stringify(data));
      }

    },
    updateModule(formName){
      /**
       * @description 编辑模块
       * @param {Number} clientId 应用ID
       * @param {Number} moduleId 模块ID
       * @param {String} moduleName 模块名称
       */
      let param = {
        clientId:this.editModuleForm.clientId,
        moduleId:this.editModuleForm.moduleId,
        moduleName:this.editModuleForm.moduleName
      };
      editAppModule(param).then(res=>{
        if(res.err_CODE === 0){
          this.editModuleVisible = false;
          this.$refs[formName].resetFields();
          this.getPermission(this.editModuleForm.clientId,null);
        }else{
          this.$message.error(res.err_MESSAGE);
        }
      })
    },
    updateFunc(formName){
      /**
       * @description 编辑功能
       * @param {Number} clientId 应用ID
       * @param {Number} moduleId 模块ID
       * @param {Number} functionId 功能ID
       * @param {String} functionName 功能名称
       * @param {Number} apiType 功能类型 0-新增 1-编辑
       * @param {Array}  apiUrlList url
       */
      let param = {
        clientId: this.editFunForm.clientId,
        moduleId: this.editFunForm.moduleId,
        functionId: this.editFunForm.functionId,
        functionName: this.editFunForm.functionName,
        apiType: this.editFunForm.apiType,
        apiUrlList: this.editFunForm.apiUrlList.split(',')
      };
      this.$refs[formName].validate((valid) =>{
        if(valid){
          editAppFunction(param).then(res=>{
            if(res.err_CODE === 0){
              this.editFunctionVisible = false;
              this.editModuleVisible = false;
              this.$message.success(res.err_MESSAGE);
              this.$refs[formName].resetFields();
              this.getPermission(param.clientId,null);
              this.getFunList(this.editFunForm.clientId,this.editFunForm.moduleId);
            }else{
              this.$message.error(res.err_MESSAGE);
            }
          })
        }else{
          return false;
        }
      })
    },
    delHandle(){
      /**
       * @description 批量删除功能or模块
       */
      if(this.mdShow){
        /**
         * @description 删除模块
         * @param {Number} clientId 应用ID
         * @param {Array} ids 要删除模块ID
         */

        let param = {
          clientId:null,
          ids:[]
        };

        this.checkModules.forEach(item=>{
          param.clientId = item.clientId;
          param.ids.push(item.moduleId);
        })

        delAppModule(JSON.stringify(param)).then(res=>{
          if(res.err_CODE === 0){
            this.$message.success(res.err_MESSAGE);
            this.getPermission(this.checkModules[0].clientId,null);
          }else{
            this.$message.error(res.err_MESSAGE);
          }
        })
      }else{
        /**
         * @description 删除功能
         * @param {Number} clientId 应用ID
         * @param {Array} ids 要删除模块ID
         */
        let param = {
          clientId:'',
          ids:[]
        };
        this.checkFunctions.forEach(item=>{
          param.clientId = item.clientId;
          param.ids.push(item.functionId);
        })
        delAppFunction(param).then(res=>{
          if(res.err_CODE === 0){
            this.$message.success(res.err_MESSAGE);
            this.getPermission(param.clientId,null);
            this.getFunList(param.clientId,this.checkFunctions[0].moduleId);
          }else{
            this.$message.error(res.err_MESSAGE);
          }
        })
      }
    },
    del(data){
      if(this.mdShow){
        this.checkModules.push(data);
      }else{
        this.checkFunctions.push(data);
      }
      this.delHandle();
    },
    permissionHandle(){
      /**
       * @description 编辑权限弹出框数据渲染
       */
      this.permissionVisible = true;
      this.getPermission(this.appDetails.clientId,null);
    },
    getPermission(clientId,moduleId){
      /**
       * @description 获取应用下模块列表
       * @param {Number} clientId 应用ID
       * @param {Number} moduleId 模块ID：查询模块时，该字段对应的值为null
       */
      this.permissionList = [];
      this.moduleData = [];
      this.functionData = [];

      let param =  {
        clientId:clientId,
        moduleId:moduleId
      }
      getModuleById(param).then(res=>{
        if(res.err_CODE === 0){
            res.data.map(item=>{
                // 拼接成前端需要的数据结构
                item.id = item.moduleId;
                item.label = item.moduleName;
                item.level = 2;

                item.functionList.map(jtem =>{
                  jtem.id = jtem.functionId;
                  jtem.label = jtem.functionName;
                  jtem.level = 3;
                  jtem.moduleId = item.moduleId;
                  jtem.url = jtem.apiUrlList.length > 0 ? jtem.url = '...'.padStart(15,jtem.apiUrlList.join(',')) : jtem.url = '--';
                  jtem.apiUrlList = jtem.apiUrlList.length > 0 ? jtem.apiUrlList.join(',') : '--';
                })
                item.children = item.functionList;
                return item;
            })
            // 根节点数据拼接
            let root = {
                id:this.appDetails.clientId,
                label:this.appDetails.clientName,
                level:1,
                children:res.data
            }
            this.appPermission = JSON.parse(JSON.stringify(root));
            this.permissionList.push(this.appPermission);
            this.moduleData = this.appPermission.children;
            this.permissionDetail = this.permissionList;
        }else{
          this.$message.error(res.err_MESSAGE);
        }
      })
    },
    getFunList(clientId,moduleId){
      /**
       * @description 获取应用下模块列表
       * @param {Number} clientId 应用ID
       * @param {Number} moduleId 模块ID
       */
      this.functionData = [];

      let param =  {
        clientId:clientId,
        moduleId:moduleId
      }
      getModuleById(param).then(res=>{
        if(res.err_CODE === 0){
          this.functionData = res.data[0].functionList;
          this.functionData.map(item =>{
            item.moduleId = moduleId;
            item.url = item.apiUrlList.length > 0 ? item.url = '...'.padStart(15,item.apiUrlList.join(',')) : item.url = '--';
            item.apiUrlList = item.apiUrlList.length > 0 ? item.apiUrlList.join(',') : '--';
          })
        }else{
          this.$message.error(res.err_MESSAGE);
        }
      })
    },
    cancelHandle(formName){
      switch(formName){
        case "editFun" :
           this.editFunctionVisible = false;
           break;
        case "moduleForm":
            this.addModuleVisible = false;
            break;
        case "editModuleForm":
            this.editModuleVisible = false;
            break;
        case "functionForm":
            this.addFunctionVisible = false;
            break;
      }
      this.$refs[formName].resetFields();
    }
  }
}
</script>
<style lang="scss">
  @import "../../../styles/application-manage/application-component.scss";
</style>
