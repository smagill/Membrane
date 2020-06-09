import Vue from 'vue'
import Router from 'vue-router'
import Main from '../views/main.vue'
import Login from '../views/login/login.vue'  //登陆页面
import UserManagement from '../views/user-manage/user-management.vue' //用户管理
import RoleManagement from '../views/role-manage/role-management.vue'  //角色管理
import ApplicationManagement from "../views/application-manage/application-management.vue" //应用管理

Vue.use(Router)
const whiteList = ['/']; // 不重定向白名单
const router = new Router({
    routes: [{
            path: '/user',
            name: 'home',
            component: Main,
            redirect: '/applictionManagement',
            children: [
              {
                path:'/applictionManagement',
                name:'应用管理',
                iconClass:'icon-scan',
                meta:{
                  active:'/applictionManagement'
                },
                leaf:true,
                component:ApplicationManagement,
              },
              {
                    path: '/userManagement',
                    name: '用户管理',
                    iconClass: 'icon-uservart',
                    meta: {
                        active: '/userManagement'
                    },
                    leaf: true,
                    component: UserManagement,
                },
                {
                    path: '/roleManagement',
                    name: '角色管理',
                    iconClass: 'icon-jsgl',
                    meta: {
                        active: '/roleManagement'
                    },
                    leaf: true,
                    component: RoleManagement,
                }
            ]
        },{
            path: '/',
            name: 'login',
            component: Login,
        }
    ]
})

router.beforeEach((to, from, next) => {
    var token = window.localStorage.getItem("sso_token");
    let haveSSOAuthority = window.localStorage.getItem("haveSSOAuthority");
    if(whiteList.indexOf(to.path) !== -1){
		next()
	}else {
		if(token && haveSSOAuthority) {
			next();
		}else {
			next('/')
		}
	}
})

export default router
