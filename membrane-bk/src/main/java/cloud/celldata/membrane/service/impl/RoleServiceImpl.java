package cloud.celldata.membrane.service.impl;


import cloud.celldata.membrane.excep.MembraneException;
import cloud.celldata.membrane.mapper.*;
import cloud.celldata.membrane.pojo.ResponseCode;
import cloud.celldata.membrane.pojo.bean.RoleListBean;
import cloud.celldata.membrane.pojo.bean.RoleListPaginationBean;
import cloud.celldata.membrane.pojo.bean.*;
import cloud.celldata.membrane.pojo.entity.ClientEntity;
import cloud.celldata.membrane.pojo.entity.Role;
import cloud.celldata.membrane.pojo.entity.RoleApi;
import cloud.celldata.membrane.pojo.entity.RoleEntity;
import cloud.celldata.membrane.pojo.role.RoleAddBean;
import cloud.celldata.membrane.pojo.role.RoleApiBean;
import cloud.celldata.membrane.pojo.role.RoleUpdateBean;
import cloud.celldata.membrane.service.RoleService;
import cloud.celldata.membrane.service.TokenService;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @ProjectName: membrane
 * @Package: cloud.celldata.membrane.service.impl
 * @ClassName: RoleServiceImpl
 * @Description: java类作用描述
 * @Author: jiwang
 * @CreateDate: 2020/5/15 15:06
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/15 15:06
 */
@Transactional
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private ApiMapper apiMapper;

    @Autowired
    private DataApiMapper dataApiMapper;

    @Autowired
    private RoleAuthorityMapper authorityMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenService tokenService;

    /**
     * 获取所有角色信息
     * @param clientId 应用ID
     * @return 角色列表
     */
    @Override
    public List<RoleListBean> selectAllRole(Integer clientId) {
        // 获取所有角色信息
        List<RoleEntity> roleByClientId = getRoleByClientId(clientId);
        List<RoleListBean> roles = Lists.newArrayList();
        roleByClientId.forEach(r -> {
            RoleListBean roleListBean = new RoleListBean();
            // 获取功能权限
            if (r.getIsAllApi() == 1) {
                ////TODO：全部权限更改为其他标示，不能用string
                roleListBean.setFunctionAuthority("全部权限");
            } else {
                List<Integer> roleApiIDListByRoleId = authorityMapper.selectRoleApiIDListByRoleId(r.getId());
                List<ModuleBean> moduleBeans = authorityMapper.selectModuleAndFunctionByApiIdList(r.getClientId(), roleApiIDListByRoleId);
                StringBuilder functionAuthority = new StringBuilder();
                for (ModuleBean moduleBean : moduleBeans) {
                    for (FunctionBean functionBean : moduleBean.getFunctionList()) {
                        functionAuthority.append(functionBean.getFunctionName() + ",");
                    }
                }

                if (functionAuthority.length()>1) {
                    functionAuthority.deleteCharAt(functionAuthority.length() - 1);
                }
                roleListBean.setFunctionAuthority(functionAuthority.toString());
            }
            // 获取平台信息
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setId(r.getId());
            ClientEntity client = roleMapper.selectClientByRoleId(r.getId());
            if(!Objects.isNull(client)){
                roleListBean.setClinetName(client.getAppName());
                roleListBean.setClientId(client.getId());
            }
            roleListBean.setRoleId(r.getId());
            roleListBean.setRoleName(r.getRoleName());
            roles.add(roleListBean);
        });
        return roles;
    }

    /**
     * 新增角色
     * @param roleAddBean 角色信息
     * @param creatorId 创建者id
     */
    @Override
    public void addRole(RoleAddBean roleAddBean, Integer creatorId) {
        Integer roleId = roleMapper.selectRoleName(-1, roleAddBean.getRoleName());
        if(roleId !=null && roleId > 0) {
            throw new MembraneException(ResponseCode.ROLE_NAME_EXISTS);
        }
        Date updateTime = new Date();


        if (roleAddBean.getAllFunctionAuthority().equals(true)) {
            //全部角色权限
            Role role = Role.RoleBuilder.aRole()
                    .withClientId(roleAddBean.getClientId())
                    .withRoleName(roleAddBean.getRoleName())
                    .withEnableFlag(1)
                    .withCreatorId(creatorId)
                    .withCreateTime(updateTime)
                    .withIsAllApi(1)
                    .build();
            roleMapper.addRole1(role);
        } else {
            // 部分角色权限
            Role role = Role.RoleBuilder.aRole()
                    .withClientId(roleAddBean.getClientId())
                    .withRoleName(roleAddBean.getRoleName())
                    .withEnableFlag(1)
                    .withCreatorId(creatorId)
                    .withCreateTime(updateTime)
                    .withIsAllApi(0)
                    .build();
            roleMapper.addRole1(role);
            insertRoleApi(roleAddBean.getFunctionAuthorityIds(), role.getId(), creatorId, updateTime);
        }
    }


    /**
     * 根据平台id获取角色信息
     * @param id 查询条件
     * @return 角色列表
     */
    @Override
    public List<RoleEntity> getRoleByClientId(Integer id) {
        // 获取所有角色信息
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(id);
        return roleMapper.selectRoleByClientId(clientEntity);
    }

    /**
     * 修改角色
     * @param roleUpdateBean 角色信息
     * @param updaterId 更新者id
     */
    @Override
    public void updateRole(RoleUpdateBean roleUpdateBean, Integer updaterId) {
        if (null == roleUpdateBean) {
            return;
        }

        // 根据角色id查询角色基本信息
        RoleApiBean roleApiBean = authorityMapper.selectRoleBasicByRoleId(roleUpdateBean.getRoleId());
        if (null == roleApiBean) {
            throw new MembraneException(ResponseCode.ROLE_IS_NULL);
        }
        Date updateTime = new Date();
        if (roleUpdateBean.getAllFunctionAuthority() && roleApiBean.getIsAllApi() == 1) {
            Integer countByRoleName = roleMapper.selectRoleName(roleUpdateBean.getRoleId(), roleUpdateBean.getRoleName());
            if (null != countByRoleName && countByRoleName > 0) {
                throw new MembraneException(ResponseCode.ROLE_NAME_EXISTS);
            }

            Role role = Role.RoleBuilder.aRole()
                    .withId(roleUpdateBean.getRoleId())
                    .withClientId(roleUpdateBean.getClientId())
                    .withRoleName(roleUpdateBean.getRoleName())
                    .withUpdaterId(updaterId)
                    .withUpdateTime(updateTime)
                    .build();
            roleMapper.updateRole1(role);
        }

        if (!roleUpdateBean.getAllFunctionAuthority() && roleApiBean.getIsAllApi() == 1) {
            Role role = Role.RoleBuilder.aRole()
                    .withId(roleUpdateBean.getRoleId())
                    .withClientId(roleUpdateBean.getClientId())
                    .withRoleName(roleUpdateBean.getRoleName())
                    .withUpdaterId(updaterId)
                    .withUpdateTime(updateTime)
                    .withIsAllApi(0)
                    .build();
            roleMapper.updateRole1(role);
            insertRoleApi(roleUpdateBean.getFunctionAuthorityIds(), roleUpdateBean.getRoleId(), updaterId, updateTime);

        }
        if (roleUpdateBean.getAllFunctionAuthority() && roleApiBean.getIsAllApi() != 1) {
            Role role = Role.RoleBuilder.aRole()
                    .withId(roleUpdateBean.getRoleId())
                    .withClientId(roleUpdateBean.getClientId())
                    .withRoleName(roleUpdateBean.getRoleName())
                    .withUpdaterId(updaterId)
                    .withUpdateTime(updateTime)
                    .withIsAllApi(1)
                    .build();
            roleMapper.updateRole1(role);
            roleMapper.removeRoleApi(roleUpdateBean.getRoleId(),updaterId);
        }
        if (!roleUpdateBean.getAllFunctionAuthority() && roleApiBean.getIsAllApi() != 1) {
            Role role = Role.RoleBuilder.aRole()
                    .withId(roleUpdateBean.getRoleId())
                    .withClientId(roleUpdateBean.getClientId())
                    .withRoleName(roleUpdateBean.getRoleName())
                    .withUpdaterId(updaterId)
                    .withUpdateTime(updateTime)
                    .build();
            roleMapper.updateRole1(role);
            roleMapper.removeRoleApi(roleUpdateBean.getRoleId(),updaterId);
            insertRoleApi(roleUpdateBean.getFunctionAuthorityIds(), roleUpdateBean.getRoleId(), updaterId, updateTime);
        }
    }

    /**
     * 删除角色
     * @param roleId 角色id
     * @param userId 用户id
     */
    @Override
    public void removeRole(int roleId, Integer userId) {
        /*List<Integer> userIdList = userMapper.selectUserByRoleId(roleId);
        if(userIdList.size() > 0){
            // 根据userId删除redis中的token
            for (Integer uid : userIdList) {
                tokenService.deleteUserToken(uid);
            }
        }*/
        // 用户表时间更新
        //roleMapper.updateUserUpdateTime(userId, roleId);
        // 删除角色表信息
        roleMapper.removeRole(roleId, userId);
        // 关联表功能权限
        roleMapper.removeRoleApi(roleId, userId);
        // 关联表数据权限
        //roleMapper.delRoleData(roleId, userId);
        // 关联用户表删除
        roleMapper.removeRoleUser(roleId, userId);
    }

    @Override
    public void copyRole(int roleId, String name, Integer userId) {
        // 根据角色ID获取角色信息
        RoleEntity roleByRoleId = roleMapper.getRoleByRoleId(roleId);
        if(Objects.isNull(roleByRoleId)){
            throw new MembraneException(ResponseCode.ROLE_IS_NULL);
        }
        // 开始进行复制添加
        roleByRoleId.setRoleName(name);
        roleByRoleId.setId(null);
        roleByRoleId.setCreateTime(new Date());
        roleByRoleId.setCreatorId(userId);
        roleByRoleId.setUpdateId(userId);
        roleByRoleId.setUpdateTime(new Date());
        roleByRoleId.setIsAllApi(roleByRoleId.getIsAllApi());
        // 添加前先查询此名称是否存在
        List<RoleEntity> roleByRoleName = roleMapper.selectRoleByRoleName(name);
        if(!CollectionUtils.isEmpty(roleByRoleName)){
            throw new MembraneException(ResponseCode.ROLE_NAME_EXISTS);
        }
        roleMapper.addRole(roleByRoleId);
        if (roleByRoleId.getIsAllApi() != 1) {
            // 获取功能权限
            List<Integer> integerList = authorityMapper.selectRoleApiIDListByRoleId(roleId);
            //插入功能权限
            insertRoleApi(integerList, roleByRoleId.getId(), userId, new Date());
        }


    }

    @Override
    public List<RoleIdAndNameBean> selectAllRoleForClientId(Integer clientId) {
// 获取所有角色信息
        List<RoleEntity> roleByClientId = getRoleByClientId(clientId);
        List<RoleIdAndNameBean> roles = Lists.newArrayList();
        roleByClientId.forEach(r -> {
            RoleIdAndNameBean roleListBean = new RoleIdAndNameBean();
            roleListBean.setRoleId(r.getId());
            roleListBean.setRoleName(r.getRoleName());
            roles.add(roleListBean);
        });
        return roles;

    }


    /**
     * 插入 角色对应功能权限
     *
     * @param ids    功能权限List
     * @param roleId 角色ID
     * @param userId 用户ID
     * @param date   时间
     */
    private void insertRoleApi(List<Integer> ids, Integer roleId, Integer userId, Date date) {
        List<RoleApi> roleApiList = new ArrayList<>();
        for (Integer id : ids) {
            roleApiList.add(RoleApi.RoleApiBuilder.aRoleApi()
                    .withRoleId(roleId)
                    .withApiId(id)
                    .withEnableFlag(1)
                    .withCreatorId(userId)
                    .withCreateTime(date)
                    .build());
        }
        if (roleApiList.size() > 0) {
            roleMapper.insertRoleApi(roleApiList);
        }

    }
}
