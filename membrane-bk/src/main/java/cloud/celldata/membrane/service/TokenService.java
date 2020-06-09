package cloud.celldata.membrane.service;



import java.util.Map;

/**
 * token管理业务逻辑层接口
 *
 */
public interface TokenService {

    /**
     * 向redis 存业务token
     * sso-- sso_token 与其对应token
     * 业务平台-- appkey_tokenKey与其对应值
     * @param userId 用户id
     * @param  applicationId, 如果为membrane自身的token则置空
     * @param token token
     * @return token及key
     */
    Map<String,Object> setToken(Integer userId, Integer applicationId, String token);

    /**
     * 向redis 存tokenKey
     * @param userId 用户id
     * @param key key
     * @param token token
     */
    void setTokenKey(Integer userId, String key, String token);

    /**
     * 根据userId 和key、token判断此平台token是否有效 key为APPKey
     * @param userId 用户id
     * @param appid  应用ID
     * @param token token
     * @return 是否有效的token
     */
    Boolean tokenValid(Integer userId, Integer appid, String token);

    /**
     * 删除该用户整体在redis的缓存(注销)
     * @param userId 用户id
     */
    void removeUserToken(Integer userId);

    /**
     * 根据userId更新时间
     * @param userId 用户id
     */
    void setRedisTimesByUser(Integer userId);


    /**
     *
     * @param verification 验证码
     * @param userId        用户ID
     * @param falg          0代表 邮箱 1代表手机
     */
    void setVerification(String verification,Integer userId,Integer falg);

    /**
     *
     * @param userId 用户ID
     * @param falg 0代表邮箱。1代表手机
     * @return
     */
    Boolean verificationValid(Integer userId,Integer falg);

    /**
     *
     * @param userId 用户ID
     * @param falg 0代表邮箱。1代表手机
     * @return
     */
    String getVerification(Integer userId,Integer falg);

    void deleteVerification(Integer userId,Integer falg);

}
