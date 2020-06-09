package cloud.celldata.membrane.service.impl;


import cloud.celldata.membrane.excep.MembraneException;
import cloud.celldata.membrane.mapper.UserMapper;
import cloud.celldata.membrane.pojo.RegularExpressionDictionary;
import cloud.celldata.membrane.pojo.ResponseCode;
import cloud.celldata.membrane.pojo.bean.LoginBean;
import cloud.celldata.membrane.pojo.entity.UserEntity;
import cloud.celldata.membrane.pojo.entity.UserLoginTokenEntity;
import cloud.celldata.membrane.pojo.enumeration.HaveAuthorityEnum;
import cloud.celldata.membrane.pojo.enumeration.UserStatusEnum;
import cloud.celldata.membrane.service.LoginTokenService;
import cloud.celldata.membrane.service.UserService;
import cloud.celldata.membrane.utils.Sha256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理业务逻辑层
 */

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTokenService loginTokenService;

    @Override
    public List<UserEntity> getUserByUser(UserEntity userEntity) {
        return null;
    }

    /**
     * 登陆
     * @param loginBean 登陆参数
     * @return token
     */
    @Override
    public UserLoginTokenEntity login(LoginBean loginBean) {
        String calcPassWord = Sha256.getSHA256(loginBean.getPassword());
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(calcPassWord);
        List<UserEntity> userList = new ArrayList<UserEntity>();
        if (loginBean.getUserName().matches(RegularExpressionDictionary.TELEPHONE_REGEX)){
            //手机号登陆
            userEntity.setTelephone(loginBean.getUserName());
            userList = userMapper.getUserByUser(userEntity);
        }else if (loginBean.getUserName().matches(RegularExpressionDictionary.EMAIL_REGEX)){
            //邮箱登陆
            userEntity.setEmail(loginBean.getUserName());
            userList = userMapper.getUserByUser(userEntity);
        }else {
            //用户名登陆
            userEntity.setUserName(loginBean.getUserName());
            userList = userMapper.getUserByUser(userEntity);
        }
        if(CollectionUtils.isEmpty(userList)){
            // 用户名密码错误
            throw new MembraneException(ResponseCode.USER_LOGIN_FAILURE);
        }
        if(1 == userList.size() && UserStatusEnum.REACTIVE.getCode().equals(userList.get(0).getActiveFlag())){
            throw new MembraneException(ResponseCode.USER_IS_INACTIVE);
        }

        //功能权限是否具备查询

        // 获取用户的id
        Integer id = userList.get(0).getId();
        // 进行登录



        UserLoginTokenEntity userLoginTokenEntity = loginTokenService.login(id, loginBean.getUserName(), loginBean.getAppId(), loginBean.getUrl());
        userLoginTokenEntity.setIsHavaAuthority(HaveAuthorityEnum.HAVEAUTHORITY.getCode());




        return userLoginTokenEntity;
    }


}
