import axios from './http'
import {
    API_ROOT
} from './config'
//常量appId
export const appId = '444';
// 用户管理
export const fetchUserInfoList = params => { return axios.get(`${API_ROOT}user/list`, { params: params }).then(res=>res.data).catch(error=>{console.log(error)}); } // 用户列表
export const fetchClientRoleList = params => { return axios.get(`${API_ROOT}user/fetchClientRoleList`, { params: params }).then(res=>res.data); } // 用户状态
export const resetPassword = params => { return axios.post(`${API_ROOT}user/reset`, params).then(res => res.data).catch(error => { console.log(error); }) } // 重置密码
export const addUser = params => { return axios.post(`${API_ROOT}user/add`, params).then(res => res.data).catch(error => { console.log(error); }) } // 新增用户
export const updateUser = params => { return axios.post(`${API_ROOT}user/update`, params).then(res => res.data).catch(error => { console.log(error); }) } // 禁用启用
export const isExist = params => { return axios.get(`${API_ROOT}user/exists`, {params: params}).then(res => res.data).catch(error => { console.log(error); }) } // 判断用户名是否存在
export const sendCode = params => { return axios.get(`${API_ROOT}user/send`, { params: params }).then(res=>res.data); } // 发送验证码
export const forgetPassword = params =>{ return axios.post(`${API_ROOT}user/forget`, params).then(res => res.data).catch(error => { console.log(error); }) } //找回密码
export const roleAndUserStatus = params =>{return axios.get(`${API_ROOT}user/fetch`,{params:params}).then(res=>res.data).catch(error=>{console.log(error)})} //获取角色列表和用户状态
export const getRolesByCliengId = params =>{return axios.get(`${API_ROOT}role/select`,{params : params}).then(res=>res.data).catch(error=>{console.log(error)})}//根据应用ID获取该应用下的角色


//角色管理
export const selectAllRole = params => { return axios.get(`${API_ROOT}role/list`, {params:params}).then(res => res.data).catch(error => { console.log(error); }) } // 角色列表
export const roleSelectAllClient = params => { return axios.get(`${API_ROOT}client/selectAllClient`, { params: params }); } //角色平台下拉
export const roleDataAuthority = params => { return axios.get(`${API_ROOT}role/selectDataAuthority`, { params: params }); } //查询角色数据权限
export const roleFunctionAuthority = params => { return axios.get(`${API_ROOT}role/selectFunctionAuthority`, { params: params }); } //查询角色功能权限
export const addRole = params => { return axios.post(`${API_ROOT}role/add`, params).then(res => res.data).catch(error => { console.log(error); }) } // 新增角色
export const updateRole = params => { return axios.post(`${API_ROOT}role/update`, params).then(res => res.data).catch(error => { console.log(error); }) } // 编辑角色
export const copyRole = params => { return axios.get(`${API_ROOT}role/copy`, { params: params }).then(res=>res.data).catch(error=>{console.log(error)}); } //复制角色
export const roleDetail = params => { return axios.get(`${API_ROOT}role/fetch`, { params: params }).then(res=>res.data).catch(error=>{console.log(error)}); } //角色详情
export const roleDataByClientId = params => { return axios.get(`${API_ROOT}role/selectDataByClientId`, { params: params }); } //根据平台id查询所有数据权限
export const roleFunctionByClientId = params => { return axios.get(`${API_ROOT}role/selectFunctionByClientId`, { params: params }); } //根据平台id查询所有功能权限
export const deleteRole = params => { return axios.get(`${API_ROOT}role/remove`, { params: params }).then(res=>res.data).catch(error=>{console.log(error);}); } //删除角色
export const getSelectedRole = params => { return axios.get(`${API_ROOT}role/select`, { params: params }).then(res=>res.data).catch(error=>{console.log(error)}); } //进行角色编辑前的查询

//应用管理
export const applicationList = params =>{return axios.get(`${API_ROOT}app/list`,{params:params}).then(res=>res.data).catch(error=>{console.log(error)})};//获取应用列表
export const addAppInfo = params =>{return axios.post(`${API_ROOT}app/add`,params).then(res=>res.data).catch(error=>{console.log(error)})};//新增应用
export const editAppInfo = params =>{return axios.post(`${API_ROOT}app/update`,params).then(res=>res.data).catch(error=>{console.log(error)})};//编辑应用信息
export const delApp = params =>{return axios.get(`${API_ROOT}app/remove`,{params:params}).then(res=>res.data).catch(error =>{console.log(error)})};//删除应用
export const addAppModule = params =>{return axios.post(`${API_ROOT}app/module/add`,params).then(res=>res.data).catch(error =>{console.log(error)})};//添加模块
export const getModuleById = params =>{return axios.get(`${API_ROOT}app/select`,{params : params}).then(res=>res.data).catch(error =>{console.log(error)})};//根据应用ID获取对应的模块和功能
export const editAppModule = params =>{return axios.post(`${API_ROOT}app/module/update`,params).then(res=>res.data).catch(error =>{console.log(error)})};//编辑模块
export const delAppModule = params =>{return axios.post(`${API_ROOT}app/module/remove`,params).then(res=>res.data).catch(error =>{console.log(error)})};//删除模块
export const addAppFunciton = params =>{return axios.post(`${API_ROOT}app/function/remove`,params).then(res=>res.data).catch(error =>{console.log(error)})};//添加功能
export const editAppFunction = params =>{return axios.post(`${API_ROOT}app/module/function/update`,params).then(res=>res.data).catch(error =>{console.log(error)})};//编辑功能
export const delAppFunction = params =>{return axios.post(`${API_ROOT}app/module/function/remove`,params).then(res=>res.data).catch(error =>{console.log(error)})};//删除功能
export const authConfig = params =>{return axios.post(`${API_ROOT}app/certification`,params).then(res=>res.data).catch(error =>{console.log(error)})};//认证配置


//修改密码
export const modifyPassword = params => { return axios.post(`${API_ROOT}user/modifyPassword`, params).then(res => res.data).catch(error => { console.log(error); }) } // 修改密码
//Token验证
export const validateToken = params => {return axios.post(`${API_ROOT}token/validate`, params).then(res => res.data).catch(error => { console.log(error); }) }
export const login = params =>  {return axios.post(`${API_ROOT}token/login`, params).then(res => res.data).catch(error => { console.log(error); }) }
export const loginLogout = params =>  {return axios.post(`${API_ROOT}token/logout`, params).then(res => res.data).catch(error => { console.log(error); }) }//注销









