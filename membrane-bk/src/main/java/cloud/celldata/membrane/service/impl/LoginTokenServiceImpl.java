package cloud.celldata.membrane.service.impl;

import cloud.celldata.membrane.excep.MembraneException;
import cloud.celldata.membrane.mapper.UserManagementMapper;
import cloud.celldata.membrane.pojo.MembraneTokenEntity;
import cloud.celldata.membrane.pojo.ResponseCode;
import cloud.celldata.membrane.pojo.bean.LoginOffTokenBean;
import cloud.celldata.membrane.pojo.entity.UserEntity;
import cloud.celldata.membrane.pojo.entity.UserLoginTokenEntity;
import cloud.celldata.membrane.service.AppService;
import cloud.celldata.membrane.service.LoginTokenService;
import cloud.celldata.membrane.service.TokenService;
import cloud.celldata.membrane.utils.JWTUtils;
import cloud.celldata.membrane.utils.Sha256;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: membrane
 * @Package: cloud.celldata.membrane.service.impl
 * @ClassName: LoginTokenServiceImpl
 * @Description: java类作用描述
 * @Author: jiwang
 * @CreateDate: 2020/5/14 16:40
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/14 16:40
 */
@Service
@Transactional
public class LoginTokenServiceImpl implements LoginTokenService {

    @Value("${active_time}")
    private long activeTime;

    @Autowired
    private UserManagementMapper userManagementMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AppService appService;

    @Value("${membrane.secret}")
    private String membraneSecret;

    /**
     * 执行登陆操作 存入token 返回数据
     * @param userId 用户id
     * @param appId appId
     * @param uri uri
     * @return 用户登录成功信息
     */
    @Override
    public UserLoginTokenEntity login(Integer userId, String userName, String appId, String uri) {
        // 先删除之前的token解除登陆
        tokenService.removeUserToken(userId);
        //String authorityTokenByAppKey = createAuthorityTokenByAppKey(userId, appId);


        // 生成sso_token
        String tokenByUserId = createTokenByUserId(userId);
        // 存入redis
        tokenService.setToken(userId,null, tokenByUserId);

        UserLoginTokenEntity userLoginTokenEntity = new UserLoginTokenEntity();
        userLoginTokenEntity.setUrl(uri);
        //userLoginTokenEntity.setTokenKey(tokenKey);
        userLoginTokenEntity.setToken(tokenByUserId);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userId);
        userInfo.put("username", userName);
        userLoginTokenEntity.setUserInfo(userInfo);

        return userLoginTokenEntity;
    }

    @Override
    public Boolean validateMembraneToken(String ssoToken) {
        if(StringUtils.isEmpty(ssoToken)){
            throw new MembraneException(ResponseCode.AUTHORITY_ERROR);
        }
        // 解密sso_token
        Claims claims;
        try {
            claims = JWTUtils.parseJWT(ssoToken, membraneSecret);
        } catch (Exception e) {
            return false;
        }
        if (null == claims) {
            return false;
        }
        Object objUserId = claims.get("user_id");
        if (null == objUserId) {
            return false;
        }
        Integer userId = Integer.valueOf(String.valueOf(objUserId));
        tokenService.setRedisTimesByUser(userId);

        return tokenService.tokenValid(userId, null, ssoToken);
    }


    /**
     * 根据appKey 和 权限map 生成带权限的token
     * @param userId  用户id
     * @param appId  平台appId
     * @return token
     */
    @Override
    public String createAuthorityTokenByAppKey(Integer userId, String appId) {
        // 根据appId查询平台信息
        /*ClientEntity clientEntity = new ClientEntity();
        clientEntity.setAppId(appId);
        ClientEntity client = clientMapper.getClient(clientEntity);
        if(null == client){
            throw new MembraneException(ResponseCode.ILLEGAL_APP_ID_CODE, ResponseCode.ILLEGAL_APP_ID_MSG);
        }*/
        //String appKey = client.getAppKey();

        // 根据userId、clientId查询权限
        UserEntity userEntity = userManagementMapper.selectUserById(userId);
        // 对权限集合进行加密
        MembraneTokenEntity token = new MembraneTokenEntity();
        token.setApplicationId(appId);
        token.setUserId(userId);
        token.setUser_name( userEntity.getUserName());
        return JWTUtils.createJWT(token, membraneSecret);
    }

    /**
     * 根据userId生成sso_token
     * @param userId 用户id
     * @return token
     */
    @Override
    public String createTokenByUserId(Integer userId) {
        // 根据用户id查询数据

        UserEntity user = userManagementMapper.selectUserById(userId);
        long time = new Date().getTime();

        MembraneTokenEntity tokenInfo = new MembraneTokenEntity();

        tokenInfo.setUser_name(user.getUserName());
        tokenInfo.setUserId(user.getId());
        tokenInfo.setApplication_name("membrane");
        tokenInfo.setApplicationId(null);

        // 对map进行加密
        return JWTUtils.createJWT(tokenInfo, membraneSecret);
    }

    /**
     * 退出登录 删除redis中的token
     * @param loginOffTokenBean 退出登录参数（详见实体属性）
     */
    @Override
    public void removeToken(LoginOffTokenBean loginOffTokenBean) {
        //String appId = loginOffTokenBean.getAppId();
        String token = loginOffTokenBean.getToken();
        // 根据appId查询
       /* ClientEntity clientEntity = new ClientEntity();
        clientEntity.setAppId(appId);
        ClientEntity client = clientMapper.getClient(clientEntity);
        if(null == client){
            throw new MembraneException(ResponseCode.USELESS_APP_ID_CODE, ResponseCode.USELESS_APP_ID_MSG);
        }*/
        Claims claims;
        try {
            claims = JWTUtils.parseJWT(token, membraneSecret);
        } catch (Exception e) {
            throw new MembraneException(ResponseCode.TOKEN_IS_INVALID);
        }
        Integer userId = Integer.valueOf(String.valueOf(claims.get("user_id")));
        tokenService.removeUserToken(userId);
    }
}
