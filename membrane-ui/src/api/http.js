import axios from "axios";
import router from '../router/index.js'
var instance = axios.create({});
//instance.defaults.headers.common["sso_token"] = localStorage.getItem("sso_token");
instance.interceptors.request.use(function (config) {
  let url = config.url;
  if (url.indexOf("toLogin") > -1) {
    localStorage.setItem('sso_token', "");
    config.headers.Authorization = "";
  }
  // console.log(localStorage.getItem("sso_token"),'SSO-TOKEN')
  if (url.indexOf("toLogin") ==-1) {

    config.headers.common["ssotoken"] = localStorage.getItem("sso_token");
  }
  config.headers['Content-Type'] = 'application/json;charset=UTF-8'
  return config;
}, function (err) {
  return Promise.reject(err);
});


instance.interceptors.response.use(function (res) {
  if (res.headers.ssotoken) {
    localStorage.setItem('sso_token', res.headers.ssotoken);
  }
  return res;
}, function (err) {
	console.log("401结果"+JSON.stringify(err))
	var responseError = JSON.parse(JSON.stringify(err));
  var statusCode = responseError.response.status;
  if(statusCode === 401){
    localStorage.removeItem('appId');
    localStorage.removeItem('isHavaSSOAuthority');
    localStorage.removeItem('sso_token');
    localStorage.removeItem('userName');
    localStorage.removeItem('userId');
  	router.push({ path: '/' })
  }else {
  		return err;
  }
});


export default instance;
