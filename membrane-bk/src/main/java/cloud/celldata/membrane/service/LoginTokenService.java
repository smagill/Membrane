package cloud.celldata.membrane.service;


import cloud.celldata.membrane.pojo.bean.LoginOffTokenBean;
import cloud.celldata.membrane.pojo.entity.UserLoginTokenEntity;

/**
 * 登录token管理业务逻辑层接口
 */
public interface LoginTokenService {

    /**
     * 根据userId生成sso_token
     * @param userId 用户id
     * @return token
     */
    String createTokenByUserId(Integer userId);

    /**
     * 退出登录  删除redis中的token
     * @param token token
     */
    void removeToken(LoginOffTokenBean token);

    /**
     * 根据appKey 和 权限map 生成带权限的token
     * @param userId  用户id
     * @param appId  平台appId
     * @return token
     */
    String createAuthorityTokenByAppKey(Integer userId, String appId);

    /**
     * 执行登陆操作 存入token 返回数据
     * @param userId 用户id
     * @param appId appId
     * @param uri uri
     * @return 用户登录成功信息
     */
    UserLoginTokenEntity login(Integer userId, String userName, String appId, String uri);

    /**
     * 校验sso_token是否有效
     * @param ssoToken token
     * @return 是否有效
     */
    Boolean validateMembraneToken(String ssoToken);

}
