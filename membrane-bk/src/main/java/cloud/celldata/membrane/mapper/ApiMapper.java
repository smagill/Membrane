package cloud.celldata.membrane.mapper;

import cloud.celldata.membrane.pojo.bean.CertificationBean;
import cloud.celldata.membrane.pojo.bean.FunctionBean;
import cloud.celldata.membrane.pojo.bean.ModuleBean;
import cloud.celldata.membrane.pojo.dto.PrivilegeRoleDTO;
import cloud.celldata.membrane.pojo.entity.ApiEntity;
import cloud.celldata.membrane.pojo.entity.ApiUrlEntity;
import cloud.celldata.membrane.pojo.entity.RoleApiEntity;
import cloud.celldata.membrane.pojo.entity.UrlEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能权限Mapper
 */
public interface ApiMapper {

    /**
     * 根据角色id查询功能权限列表
     * @param privilegeRoleDTO 查询条件
     * @return 功能权限列表
     */
    List<ApiEntity> getApiByRoleId(PrivilegeRoleDTO privilegeRoleDTO);

    /**
     * 添加功能权限关系表数据
     * @param roleApiEntity 角色实体
     * @return 新增成功记录条数
     */
    int addRoleApi(RoleApiEntity roleApiEntity);

    /**
     * 新增模块，功能
     * @param apiEntityList
     * @return 主键Id
     */
    void insertModuleAndFunction(ApiEntity apiEntityList);

    /**
     * 新增功能对应URL
     * @param urlEntity URL实体
     *
     */
    void insertUrl(UrlEntity urlEntity);

    /**
     * 新增功能 和URL对应中间表
     * @param apiUrlEntity
     */
    void insertApiUrl(ApiUrlEntity apiUrlEntity);

    /**
     * 查询模块名 或者功能名是否重复
     *
     * @param clientId 应用ID
     * @param apiName  模块 或者 功能名
     * @param moduleId 模块ID
     * @return
     */
    Integer selectNameCountByClientId(@Param("clientId") Integer clientId,
                                      @Param("apiName") String apiName,
                                      @Param("moduleId") Integer moduleId
    );

    /**
     * 查询 应用对应 模块和功能
     *
     * @param clientID 应用ID
     * @param moduleId 模块ID
     * @return 模块和功能
     */
    List<ModuleBean> selectModuleAndFunctionByClientID(@Param("clientID")Integer clientID,@Param("moduleId")Integer moduleId);

    /**
     * 删除 功能
     *
     * @param functionId 功能ID
     * @param userId     删除用户ID
     */

    void removeFunctionApi(@Param("functionId") Integer functionId, @Param("userId") Integer userId);

    /**
     * 删除 功能和URL对应中间表
     *
     * @param functionId 功能ID
     * @param userId     删除用户ID
     */
    void removeFunctionApiURI(@Param("functionId") Integer functionId, @Param("userId") Integer userId);

    /**
     * 查询模块下有多少个功能
     *
     * @param moduleId
     * @return
     */
    Integer selectFunctionCountByModuleId(Integer moduleId);

    /**
     * 删除 模块
     *
     * @param moduleId 模块Id
     * @param userId   用户ID
     */
    void removeModuleApi(@Param("moduleId") Integer moduleId, @Param("userId") Integer userId);

    /**
     * 编辑模块
     *
     * @param moduleBean 模块实体
     * @param userId     编辑用户ID
     */
    void updateModule(@Param("moduleBean") ModuleBean moduleBean, @Param("userId") Integer userId);

    /**
     * 删除URI
     *
     * @param functionId 功能ID
     * @param userId     用户ID
     */
    void removeFunctionURI(@Param("functionId") Integer functionId, @Param("userId") Integer userId);

    /**
     * 功能编辑
     *
     * @param functionBean 功能实体
     * @param userId       用户ID
     */
    void updateFunction(@Param("functionBean") FunctionBean functionBean, @Param("userId") Integer userId);

    /**
     * 查询应用下有多少个模块
     *
     * @param clientId 应用ID
     * @return 模块数量
     */
    Integer selectModuleByClientid(Integer clientId);

    /**
     * 删除应用
     *
     * @param clientId 应用ID
     * @param userId   用户ID
     */
    void removeApp(@Param("clientId") Integer clientId, @Param("userId") Integer userId);

    /**
     * 认证管理
     *
     * @param certificationBean 认证管理实体
     * @param userId            用户ID
     */
    void certificationApp(@Param("certificationBean") CertificationBean certificationBean, @Param("userId") Integer userId);
}
