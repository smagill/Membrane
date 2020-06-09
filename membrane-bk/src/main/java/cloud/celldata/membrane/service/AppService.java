package cloud.celldata.membrane.service;

import cloud.celldata.membrane.pojo.bean.*;
import cloud.celldata.membrane.pojo.entity.ClientEntity;

import java.util.List;

/**
 * @ProjectName: membrane
 * @Package: cloud.celldata.membrane.service
 * @ClassName: AppService
 * @Description: 平台信息管理业务逻辑层接口
 * @Author: jiwang
 * @CreateDate: 2020/5/19 14:21
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/19 14:21
 */

public interface AppService {

    /**
     * 查询平台详情
     * @param clientEntity 查询条件
     * @return 平台信息
     */
    ClientEntity getClient(ClientEntity clientEntity);

    /**
     * * 查询所有平台
     * @param sign  sign=0时需过滤权限平台
     * @param authentication 认证状态 0: "未认证", 1: "已认证"
     * @param verification 认证方式    0: "CAS" ,1: "OAUTH" , 2 :"SAML"
     * @param tokenCheckType Token 验证方式 0:安全优先, 1:性能优先
     * @param clientId 应用Id
     * @return
     */
    List<ClientListBean> selectAllClient(Integer sign, Integer authentication, Integer verification, Integer tokenCheckType, Integer clientId);

    /**
     * 添加应用
     * @param clientBean 应用实体
     * @param userId 添加用户ID
     */
    void addClient(CertificationBean clientBean, Integer userId);

    /**
     * 编辑用户
     * @param clientBean 用户实体
     * @param userId    编辑用户ID
     */
    void updateApp(ClientBean clientBean, Integer userId);

    /**
     * 新增模块，功能
     * @param moduleBean 模块功能实体
     * @param userId    用户ID
     */
    void addModuleAndFunction(ModuleBean moduleBean, Integer userId);

    /**
     * 查询应用详情
     *
     * @param clientId 应用ID
     * @param moduleId 模块ID
     */
    List<ModuleBean> selectModuleAndFunctionByClientID(Integer clientId,Integer moduleId);

    /**
     * 功能删除
     *
     * @param removeMoudleAndFuctionBean 功能Id
     * @param userId                     删除人的ID
     */
    void removeFunction(RemoveMoudleAndFuctionBean removeMoudleAndFuctionBean, Integer userId);

    /**
     * 模块删除
     *
     * @param removeMoudleAndFuctionBean 模块ID
     * @param userId                     删除人的ID
     */
    void removeModule(RemoveMoudleAndFuctionBean removeMoudleAndFuctionBean, Integer userId);

    /**
     * 模块编辑
     *
     * @param moduleBean 模块实体
     * @param userId     编辑用户ID
     */
    void updateModule(ModuleBean moduleBean, Integer userId);

    /**
     * 编辑功能
     *
     * @param functionBean 功能实体
     * @param userId       编辑用户ID
     */
    void updateFunction(FunctionBean functionBean, Integer userId);

    /**
     * 删除应用
     *
     * @param clientId 应用ID
     * @param userId   用户名
     */
    void removeApp(Integer clientId, Integer userId);

    /**
     * 认证管理
     *
     * @param certificationBean 认证管理实体
     * @param userId            用户ID
     */
    void certificationApp(CertificationBean certificationBean, Integer userId);

    /**
     * 根据平台ID 查询功能权限 数据权限
     *
     * @param clientId 平台ID
     * @param flag     0 代表功能权限
     * @return 权限List
     */
    List selectFunctionAuthorityByClientId(Integer clientId, Integer flag);
}
