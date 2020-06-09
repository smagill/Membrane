package cloud.celldata.membrane.service.impl;

import cloud.celldata.membrane.excep.MembraneException;
import cloud.celldata.membrane.pojo.MembraneTokenEntity;
import cloud.celldata.membrane.pojo.ResponseCode;
import cloud.celldata.membrane.pojo.entity.ClientEntity;
import cloud.celldata.membrane.pojo.entity.UserEntity;
import cloud.celldata.membrane.service.AppService;
import cloud.celldata.membrane.service.TokenService;
import cloud.celldata.membrane.service.UserService;
import cloud.celldata.membrane.utils.JWTUtils;
import cloud.celldata.membrane.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: membrane
 * @Package: cloud.celldata.membrane.service.impl
 * @ClassName: TokenServiceImpl
 * @Description: java类作用描述
 * @Author: jiwang
 * @CreateDate: 2020/5/15 10:19
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/15 10:19
 */

@Service
@Transactional
public class TokenServiceImpl implements TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AppService appService;

    @Autowired
    private UserService userService;

    private static final Long EFFECTIVE_TIME = 15L;

    @Value("${membrane.secret}")
    private String membraneSecret;

    /**
     * 向redis 存业务token
     * sso-- sso_token 与其对应token
     * 业务平台-- appkey_tokenKey与其对应值
     * @param userId 用户id
     * @param applicationId 应用平台id
     * @param token token
     * @return token及key
     */
    @Override
    public Map<String, Object> setToken(Integer userId,Integer applicationId, String token) {
        Map<String, Object> resultMap = new HashMap<>();
        if(applicationId == null) {
            String appTokenKey = getAppTokenRedisKey("membrane");
            redisUtil.hmSet(getUserKey(userId), appTokenKey, token);

            // 放置是否权限变更、整体时间设置
            redisUtil.hmSet(getUserKey(userId), "valid", false);
            redisUtil.setHashTime(getUserKey(userId), EFFECTIVE_TIME, TimeUnit.MINUTES);
            Object userMap = redisUtil.getAllObjectForHashKey(getUserKey(userId));
            Map map = JSON.parseObject(JSON.toJSONString(userMap), Map.class);
            for (Object o : map.keySet()) {
                if (appTokenKey.equals(String.valueOf(o))) {
                    resultMap.put(String.valueOf(o), map.get(o));
                }
            }
        } else {
            ClientEntity clientEntity = new ClientEntity();
            clientEntity.setId(applicationId);
            ClientEntity appEntity = appService.getClient(clientEntity);

            UserEntity query = new UserEntity();
            query.setId(userId);
            UserEntity user = userService.getUserByUser(query).get(0);
            String tokenKeyKey = getTokenKeyKey(appEntity.getAppName());
            MembraneTokenEntity tokenEntity = new MembraneTokenEntity();
            tokenEntity.setUserId(userId);
            tokenEntity.setApplicationId(appEntity.getAppId());
            tokenEntity.setApplication_name(appEntity.getAppName());
            tokenEntity.setUser_name(user.getUserName());
            String appTokenKeyValue = JWTUtils.createJWT(tokenEntity,membraneSecret);
            setTokenKey(userId, tokenKeyKey, appTokenKeyValue);
            // 设置每个平台的tokenKey过期时间（存入为当前时间的时间戳）
            redisUtil.hmSet(getUserKey(userId), getAppTokenTimeKey(appEntity.getAppName()), System.currentTimeMillis());
            resultMap.put(tokenKeyKey, appTokenKeyValue);
        }
        return resultMap;
    }


    /**
     * 根据key获取redisKey
     * @param key key
     * @return redisKey
     */
    private String getAppTokenRedisKey(String key){
        return key + "_token";
    }

    /**
     * 根据userId获取key
     * @param userId 用户唯一标识
     * @return key
     */
    private String getUserKey(Integer userId){
        return "user_" + userId;
    }

    /**
     * 根据key生成tokenKey
     * @param key key
     * @return tokenKey
     */
    private String getTokenKeyKey(String key){
        return key + "_tokenKey";
    }

    private String getEmailVerification(Integer userID){
        return userID+"_EmailVerification";
    }
    private String getPhoneVerification(Integer userID){
        return userID+"_PhoneVerification";
    }

    @Override
    public void setRedisTimesByUser(Integer userId) {
        redisUtil.setHashTime(getUserKey(userId), EFFECTIVE_TIME, TimeUnit.MINUTES);
    }

    @Override
    public void setVerification(String verification, Integer userId, Integer falg) {
        switch (falg){
            case 0:
                redisUtil.set(getEmailVerification(userId),verification,1800L);
                break;
            case 1:
                redisUtil.set(getPhoneVerification(userId),verification,1800L);
                break;
            default:
                logger.error("非法参数");
                break;
        }
    }

    /**
     *
     * @param userId 用户ID
     * @param falg 标识 0代表邮箱验证 1代表手机验证
     * @return
     */
    @Override
    public Boolean verificationValid(Integer userId, Integer falg) {
        switch (falg){
            case 0:
                return redisUtil.exists(getEmailVerification(userId));
            case 1:
                return redisUtil.exists(getPhoneVerification(userId));
            default:
                logger.error("非法参数");
                return true;
        }
    }

    @Override
    public String getVerification(Integer userId, Integer falg) {
        switch (falg){
            case 0:
                return String.valueOf(redisUtil.get(getEmailVerification(userId)));
            case 1:
                return String.valueOf(redisUtil.get(getPhoneVerification(userId)));
            default:
                return null;
        }

    }

    @Override
    public void deleteVerification(Integer userId, Integer falg) {
        switch (falg){
            case 0:
                 redisUtil.remove(getEmailVerification(userId));
                break;
            case 1:
                 redisUtil.remove(getPhoneVerification(userId));
                break;
            default:
                logger.error("非法参数");
                break;
        }
    }

    /**
     * 根据appToken生成timeKey
     * @param key appToken
     * @return timeKey
     */
    private String getAppTokenTimeKey(String key){
        return key + "_token_time";
    }


    /**
     * 向redis 存tokenKey
     * @param userId 用户id
     * @param key key
     * @param token token
     */
    @Override
    public void setTokenKey(Integer userId, String key, String token) {
        redisUtil.hmSet(getUserKey(userId), key, token);
    }

    @Override
    public Boolean tokenValid(Integer userId, Integer appId, String token) {
        setRedisTimesByUser(userId);
        Object userMap = redisUtil.getAllObjectForHashKey(getUserKey(userId));
        Map map = JSON.parseObject(JSON.toJSONString(userMap), Map.class);
        logger.info("token值为;{}", JSON.toJSONString(token));
        logger.info("redis中存值为;{}", JSON.toJSONString(map));
        if(map.isEmpty()){
            throw new MembraneException(ResponseCode.AUTHORITY_ERROR);
        }
        Object valid = redisUtil.hmGet(getUserKey(userId), "valid");
        if(Boolean.valueOf(String.valueOf(valid))){
            throw new MembraneException(ResponseCode.CLAIMS_UPDATED);
        }
        Object result=null;
        if (appId ==null){
            result= redisUtil.hmGet(getUserKey(userId), getAppTokenRedisKey("membrane"));
        }else {
            //业务平台token


        }

        logger.info("校验token值为:" + result);
        token = token.trim();
        return token.equals(String.valueOf(result));
    }

    @Override
    public void removeUserToken(Integer userId) {
        redisUtil.remove(getUserKey(userId));
    }
}
