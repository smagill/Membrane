package cloud.celldata.membrane.controller;

import cloud.celldata.membrane.excep.MembraneException;
import cloud.celldata.membrane.pojo.ResponseCode;
import cloud.celldata.membrane.pojo.bean.LoginBean;
import cloud.celldata.membrane.pojo.bean.LoginOffTokenBean;
import cloud.celldata.membrane.pojo.bean.ResponseBean;
import cloud.celldata.membrane.pojo.entity.UserLoginTokenEntity;
import cloud.celldata.membrane.service.UserService;
import cloud.celldata.membrane.service.impl.LoginTokenServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @ProjectName: membrane
 * @Package: cloud.celldata.membrane.controller
 * @ClassName: TokenController
 * @Description: token相关类
 * @Author: jiwang
 * @CreateDate: 2020/5/14 11:04
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/14 11:04
 */

@RestController
@CrossOrigin
@RequestMapping("/api/token")
public class TokenController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private LoginTokenServiceImpl loginTokenService;

    /**
     * 登录
     * @param loginBean 登录信息
     * @return 操作状态
     */
    @ApiOperation(value ="/login", notes = "登录")
    @PostMapping(value = "/login")
    public ResponseBean login(@RequestBody LoginBean loginBean){
        if (StringUtils.isEmpty(loginBean.getUserName())){
            throw new MembraneException(ResponseCode.USER_NAME_IS_NULL);
            //throw new MembraneException(ResponseCode.USER_NAME_IS_NULL, ResponseCode.NAME_NOTNULL)
        }
        if(StringUtils.isEmpty(loginBean.getPassword())){
            throw new MembraneException(ResponseCode.PASSWORD_IS_NULL);
        }
        UserLoginTokenEntity login = userService.login(loginBean);
        return getResponse(ResponseCode.SUCCESS_PROCESSED, login);
    }


    /**
     * 退户登录 删除redis中的token
     * @param loginOffTokenBean 退出登录信息（详见实体信息）
     * @return 操作状态
     */
    @ApiOperation(value ="/logout", notes = "退出")
    @PostMapping(value = "/logout")
    public ResponseBean logout(@RequestBody LoginOffTokenBean loginOffTokenBean){
        loginTokenService.removeToken(loginOffTokenBean);
        return getResponse(ResponseCode.SUCCESS_PROCESSED);

    }

}
