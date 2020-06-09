package cloud.celldata.membrane.controller;

import cloud.celldata.membrane.excep.MembraneException;
import cloud.celldata.membrane.pojo.ResponseCode;
import cloud.celldata.membrane.pojo.bean.ClientBean;
import cloud.celldata.membrane.pojo.bean.ClientListBean;
import cloud.celldata.membrane.pojo.bean.ResponseBean;
import cloud.celldata.membrane.pojo.bean.*;
import cloud.celldata.membrane.pojo.enumeration.IsAuthenticationEnum;
import cloud.celldata.membrane.pojo.enumeration.TokenCheckTypeEnum;
import cloud.celldata.membrane.service.AppService;
import cloud.celldata.membrane.utils.ListPageUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;

/**
 * @ProjectName: membrane
 * @Package: cloud.celldata.membrane.controller
 * @ClassName: AppController
 * @Description: 应用类
 * @Author: jiwang
 * @CreateDate: 2020/5/21 11:09
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/21 11:09
 */
@RestController
@CrossOrigin
@RequestMapping("/api/app")
public class AppController extends BaseController{

    @Autowired
    private AppService appService;

    /**
     * 查询所有平台
     * @param sign sign=0时需过滤权限平台
     * @return 平台列表
     */
    @ApiOperation(value ="/list", notes = "所有平台集合")
    @GetMapping(value = "/list")
    public ResponseBean list(@RequestParam(required = false, value = "sign") Integer sign,
                             @RequestParam(required = false, value = "authentication") Integer authentication,
                             @RequestParam(required = false, value = "verification") Integer verification,
                             @RequestParam(required = false, value = "tokenCheckType") Integer tokenCheckType,
                             @RequestParam(required = false, value = "clientId") Integer clientId,
                             @RequestParam(required = false, value = "pageIndex") Integer pageIndex,
                             @RequestParam(required = false, value = "pageSize") Integer pageSize
    ) {
        try {

            List<ClientListBean> clientListBeans = appService.selectAllClient(sign, authentication,
                    verification, tokenCheckType, clientId);
            PageInfo pageInfo;
            if ( pageIndex== null ||  pageSize== null) {
                pageInfo = ListPageUtil.getPageInfo(clientListBeans, 1, clientListBeans.size());
            } else {
                pageInfo = ListPageUtil.getPageInfo(clientListBeans, pageIndex,pageSize);
            }
            return getResponse(ResponseCode.SUCCESS_PROCESSED, pageInfo);
        } catch (MembraneException e) {
            return getResponse(e.getCode());
        } catch (Exception e) {
            e.printStackTrace();
            return getResponse(ResponseCode.GENERIC_FAILURE);
        }
    }

    @ApiOperation(value = "/add",notes = "新增加应用")
    @PostMapping(value = "/add")
    public ResponseBean add(@RequestBody CertificationBean clientBean){

        Integer userId = getCurrentUserId();
        try {
            appService.addClient(clientBean,userId);
            return getResponse(ResponseCode.SUCCESS_PROCESSED);
        } catch (MembraneException e) {
            return getResponse(e.getCode());
        } catch (Exception e) {
            e.printStackTrace();
            return getResponse(ResponseCode.GENERIC_FAILURE);
        }
    }

    @ApiOperation(value = "/fetch",notes = "认证列表下拉框，在线离线列表下拉框")
    @GetMapping(value = "/fetch")
    public ResponseBean fetch(){
        try {
            HashMap<Object, Object> map = new HashMap<>();
            map.put("lineStatus", TokenCheckTypeEnum.tokenCheckTypes);
            map.put("authentication",IsAuthenticationEnum.isAuthenticationMapList);
            return getResponse(ResponseCode.SUCCESS_PROCESSED, map);
        } catch (MembraneException e) {
            return getResponse(e.getCode());
        } catch (Exception e) {
            e.printStackTrace();
            return getResponse(ResponseCode.GENERIC_FAILURE);
        }
    }

    @ApiOperation(value = "/update",notes = "编辑应用")
    @PostMapping(value = "/update")
    public ResponseBean updateApp(@RequestBody ClientBean clientBean){

        Integer userId = getCurrentUserId();
        try {
            appService.updateApp(clientBean,userId);
            return getResponse(ResponseCode.SUCCESS_PROCESSED);
        } catch (MembraneException e) {
            return getResponse(e.getCode());
        } catch (Exception e) {
            e.printStackTrace();
            return getResponse(ResponseCode.GENERIC_FAILURE);
        }
    }


    @ApiOperation(value = "/select", notes = "查询应用对应模块和功能")
    @GetMapping(value = "/select")
    public ResponseBean selectModuleAndFunctionByClientID(@RequestParam(required = false, value = "clientId") Integer clientId,
                                                          @RequestParam (required = false, value = "moduleId")Integer moduleId) {

        try {
            return getResponse(ResponseCode.SUCCESS_PROCESSED,
                    appService.selectModuleAndFunctionByClientID(clientId,moduleId));
        } catch (MembraneException e) {
            return getResponse(e.getCode());
        } catch (Exception e) {
            e.printStackTrace();
            return getResponse(ResponseCode.GENERIC_FAILURE);
        }
    }


    @ApiOperation(value = "/module/add", notes = "新增模块，功能")
    @PostMapping(value = "/module/add")
    public ResponseBean addModuleAndFunction(@RequestBody ModuleBean moduleBean) {

        try {
            Integer userId = getCurrentUserId();
            appService.addModuleAndFunction(moduleBean, userId);
            return getResponse(ResponseCode.SUCCESS_PROCESSED);
        } catch (MembraneException e) {
            return getResponse(e.getCode());
        } catch (Exception e) {
            return getResponse(ResponseCode.GENERIC_FAILURE);
        }
    }

    @ApiOperation(value = "/module/remove", notes = "删除模块")
    @PostMapping(value = "/module/remove")
    public ResponseBean removeModule(@RequestBody RemoveMoudleAndFuctionBean removeMoudleAndFuctionBean) {

        try {
            Integer userId = getCurrentUserId();
            appService.removeModule(removeMoudleAndFuctionBean, userId);
            return getResponse(ResponseCode.SUCCESS_PROCESSED);
        } catch (MembraneException e) {
            return getResponse(e.getCode());
        } catch (Exception e) {
            return getResponse(ResponseCode.GENERIC_FAILURE);
        }

    }


    @ApiOperation(value = "/module/function/remove", notes = "删除功能")
    @PostMapping(value = "/module/function/remove")
    public ResponseBean removeFunction(@RequestBody RemoveMoudleAndFuctionBean removeMoudleAndFuctionBean) {
        try {
            Integer userId = getCurrentUserId();
            appService.removeFunction(removeMoudleAndFuctionBean, userId);
            return getResponse(ResponseCode.SUCCESS_PROCESSED);
        } catch (MembraneException e) {
            return getResponse(e.getCode());
        } catch (Exception e) {
            return getResponse(ResponseCode.GENERIC_FAILURE);
        }

    }

    @ApiOperation(value = "/module/update", notes = "编辑模块")
    @PostMapping(value = "/module/update")
    public ResponseBean updateModule(@RequestBody ModuleBean moduleBean) {

        try {

            Integer userId = getCurrentUserId();
            appService.updateModule(moduleBean, userId);
            return getResponse(ResponseCode.SUCCESS_PROCESSED);
        } catch (MembraneException e) {
            return getResponse(e.getCode());
        } catch (Exception e) {
            return getResponse(ResponseCode.GENERIC_FAILURE);
        }

    }

    @ApiOperation(value = "/module/function/update", notes = "编辑功能")
    @PostMapping(value = "/module/function/update")
    public ResponseBean updateFunction(@RequestBody FunctionBean functionBean) {
        try {
            Integer userId = getCurrentUserId();
            appService.updateFunction(functionBean, userId);
            return getResponse(ResponseCode.SUCCESS_PROCESSED);
        } catch (MembraneException e) {
            return getResponse(e.getCode());
        } catch (Exception e) {
            return getResponse(ResponseCode.GENERIC_FAILURE);
        }
    }


    @ApiOperation(value = "/remove", notes = "删除应用")
    @GetMapping(value = "/remove")
    public ResponseBean removeApp(@RequestParam Integer clientId) {
        try {

            Integer userId = getCurrentUserId();
            appService.removeApp(clientId, userId);
            return getResponse(ResponseCode.SUCCESS_PROCESSED);
        } catch (MembraneException e) {
            return getResponse(e.getCode());
        } catch (Exception e) {
            return getResponse(ResponseCode.GENERIC_FAILURE);
        }
    }

    /**
     * 根据平台id查询所有权限
     *
     * @param clientId 平台ID
     * @param flag     标记 0代表功能权限 1代表数据权限
     * @return
     */
    ///api名字改成Claim
    @ApiOperation(value = "/permission/list", notes = "根据平台id查询权限")
    @GetMapping(value = "/permission/list")
    public ResponseBean selectFunctionByClientId(@RequestParam("clientId") Integer clientId,
                                                 @RequestParam("flag") Integer flag) {
        try {
            List list = appService.selectFunctionAuthorityByClientId(clientId, flag);
            return getResponse(ResponseCode.SUCCESS_PROCESSED, list);
        } catch (MembraneException e) {
            return getResponse(e.getCode());
        } catch (Exception e) {
            e.printStackTrace();
            return getResponse(ResponseCode.GENERIC_FAILURE);
        }
    }

    //这个是啥
    @ApiOperation(value = "/certification", notes = "认证管理")
    @PostMapping(value = "/certification")
    public ResponseBean certificationApp(@RequestBody CertificationBean certificationBean) {

        try {
            Integer userId = getCurrentUserId();
            appService.certificationApp(certificationBean, userId);
            return getResponse(ResponseCode.SUCCESS_PROCESSED);
        } catch (MembraneException e) {
            return getResponse(e.getCode());
        } catch (Exception e) {
            return getResponse(ResponseCode.GENERIC_FAILURE);
        }
    }



}
