package cloud.celldata.membrane.service.impl;

import cloud.celldata.membrane.excep.MembraneException;
import cloud.celldata.membrane.mapper.ApiMapper;
import cloud.celldata.membrane.mapper.ClientMapper;
import cloud.celldata.membrane.pojo.ResponseCode;
import cloud.celldata.membrane.pojo.bean.ClientBean;
import cloud.celldata.membrane.pojo.bean.ClientListBean;
import cloud.celldata.membrane.pojo.bean.FunctionBean;
import cloud.celldata.membrane.pojo.bean.ModuleBean;
import cloud.celldata.membrane.pojo.bean.*;
import cloud.celldata.membrane.pojo.entity.ApiEntity;
import cloud.celldata.membrane.pojo.entity.ApiUrlEntity;
import cloud.celldata.membrane.pojo.entity.ClientEntity;
import cloud.celldata.membrane.pojo.entity.UrlEntity;
import cloud.celldata.membrane.pojo.enumeration.IsAuthenticationEnum;
import cloud.celldata.membrane.service.AppService;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @ProjectName: membrane
 * @Package: cloud.celldata.membrane.service.impl
 * @ClassName: AppServiceImpl
 * @Description: 平台信息管理业务逻辑层
 * @Author: jiwang
 * @CreateDate: 2020/5/19 14:22
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/19 14:22
 */
@Service
@Transactional
public class AppServiceImpl implements AppService {

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ApiMapper apiMapper;

    @Override
    public ClientEntity getClient(ClientEntity clientEntity) {

        return clientMapper.getClient(clientEntity);
    }

    @Override
    public List<ClientListBean> selectAllClient(Integer sign, Integer authentication, Integer verification,
                                                Integer tokenCheckType, Integer clientId) {
        List<ClientListBean> ClientListBeans = clientMapper.selectAllClient(authentication, verification, tokenCheckType, clientId);
        if(0 == sign){
            ClientListBeans = ClientListBeans.stream().filter(c -> !c.getClientName().equals("权限管理系统")).collect(Collectors.toList());
        }
        return ClientListBeans;
    }

    @Override
    public void addClient(CertificationBean clientBean, Integer userId) {

        if (clientMapper.countClientByclientName(clientBean.getClientName(), null) > 0) {
            throw new MembraneException(ResponseCode.APP_NAME_EXISTS);
        }
        if (clientMapper.countClientByAppId(clientBean.getAppId(), null) > 0) {
            throw new MembraneException(ResponseCode.APP_ID_EXISTS);
        }
        clientBean.setAppKey(UUID.randomUUID().toString().replaceAll("-",""));
        if(clientBean.getAuthentication().getCode().equals(IsAuthenticationEnum.UNVERIFIED.getCode())){
            clientBean.setVerification(null);
        }
        clientMapper.addClient(clientBean,userId);
    }

    @Override
    public void updateApp(ClientBean clientBean, Integer userId) {

        if (clientMapper.countClientByclientName(clientBean.getClientName(), clientBean.getClientId()) > 0) {
            throw new MembraneException(ResponseCode.APP_NAME_EXISTS);
        }
        if (clientMapper.countClientByAppId(clientBean.getAppId(), clientBean.getClientId()) > 0) {
            throw new MembraneException(ResponseCode.APP_ID_EXISTS);

        }
        clientMapper.updateApp(clientBean,userId);
    }


    @Override
    public void addModuleAndFunction(ModuleBean moduleBean, Integer userId) {
        Date createTime = new Date();
        if (moduleBean.getFunctionList() == null || moduleBean.getFunctionList().size() == 0) {
            //新增模块
            Integer countName = apiMapper.selectNameCountByClientId(moduleBean.getClientId(), moduleBean.getModuleName(), null);
            if (countName > 0){
                throw new MembraneException(ResponseCode.MODULE_NAME_EXISTS);
            }
            ApiEntity apiEntity = new ApiEntity();
            apiEntity.setParentId(0);
            apiEntity.setClientId(moduleBean.getClientId());
            apiEntity.setApiName(moduleBean.getModuleName());
            apiEntity.setApiNum(1);
            apiEntity.setCreatorId(userId);
            apiEntity.setCreateTime(createTime);
            apiEntity.setEnableFlag(1);
            apiMapper.insertModuleAndFunction(apiEntity);
        }else{
            //新增功能

            List<FunctionBean> functionList = moduleBean.getFunctionList();
            for (FunctionBean functionBean : functionList) {
                Integer countName = apiMapper.selectNameCountByClientId(functionBean.getClientId(), functionBean.getFunctionName(), null);

                if (countName > 0){
                    throw new MembraneException(ResponseCode.MODULE_NAME_EXISTS);
                }
                ApiEntity apiEntity = new ApiEntity();
                apiEntity.setParentId(moduleBean.getModuleId());
                apiEntity.setClientId(moduleBean.getClientId());
                apiEntity.setApiName(functionBean.getFunctionName());
                apiEntity.setEnableFlag(1);
                apiEntity.setApiNum(2);
                apiEntity.setCreatorId(userId);
                apiEntity.setCreateTime(createTime);
                apiEntity.setIsTree(1);
                apiEntity.setType(functionBean.getApiType());
                //插入模块对应功能表
                apiMapper.insertModuleAndFunction(apiEntity);
                List<String> apiUrlList = functionBean.getApiUrlList();
                for (String s : apiUrlList) {
                    UrlEntity urlEntity = new UrlEntity();
                    urlEntity.setFunctionUrl(s);
                    urlEntity.setEnableFlag(1);
                    urlEntity.setCreatorId(userId);
                    urlEntity.setCreateTime(createTime);
                    //插入功能对应URL表 一对多
                    apiMapper.insertUrl(urlEntity);
                    ApiUrlEntity apiUrlEntity = new ApiUrlEntity();
                    apiUrlEntity.setApiId(apiEntity.getId());
                    apiUrlEntity.setFunctionUrlId(urlEntity.getId());
                    apiUrlEntity.setEnableFlag(1);
                    apiUrlEntity.setCreatorId(userId);
                    apiUrlEntity.setCreateTime(createTime);
                    //插入功能和URL对应中间表
                    apiMapper.insertApiUrl(apiUrlEntity);
                }
            }

        }
    }

    @Override
    public List<ModuleBean> selectModuleAndFunctionByClientID(Integer clientId,Integer moduleId) {
        return apiMapper.selectModuleAndFunctionByClientID(clientId,moduleId);
    }

    @Override
    public void removeFunction(RemoveMoudleAndFuctionBean removeMoudleAndFuctionBean, Integer userId) {
        for (Integer id : removeMoudleAndFuctionBean.getIds()) {
            //删除URI表
            apiMapper.removeFunctionURI(id, userId);
            //删除中间表
            apiMapper.removeFunctionApiURI(id, userId);
            //删除功能表
            apiMapper.removeFunctionApi(id, userId);
        }


    }

    @Override
    public void removeModule(RemoveMoudleAndFuctionBean removeMoudleAndFuctionBean, Integer userId) {

        for (Integer id : removeMoudleAndFuctionBean.getIds()) {
            //判断 该模块下 是否有应用
            Integer functionCount = apiMapper.selectFunctionCountByModuleId(id);
            if (functionCount > 0) {
                throw new MembraneException(ResponseCode.MODULE_NOT_EMPTY);
            }
            apiMapper.removeModuleApi(id, userId);
        }
    }

    @Override
    public void updateModule(ModuleBean moduleBean, Integer userId) {
        Integer countName = apiMapper.selectNameCountByClientId(moduleBean.getClientId(), moduleBean.getModuleName(), moduleBean.getModuleId());
        if (countName > 0) {
            throw new MembraneException(ResponseCode.MODULE_NAME_EXISTS);
        }
        apiMapper.updateModule(moduleBean, userId);
    }

    @Override
    public void updateFunction(FunctionBean functionBean, Integer userId) {
        Integer countName = apiMapper.selectNameCountByClientId(functionBean.getClientId(), functionBean.getFunctionName(), functionBean.getFunctionId());
        if (countName > 0) {
            throw new MembraneException(ResponseCode.FUNCTION_NAME_EXISTS);
        }
        Date createTime = new Date();

        apiMapper.updateFunction(functionBean, userId);

        //删除URI表
        apiMapper.removeFunctionURI(functionBean.getFunctionId(), userId);
        //删除中间表
        apiMapper.removeFunctionApiURI(functionBean.getFunctionId(), userId);
        if (!Collections.isEmpty(functionBean.getApiUrlList())) {
            for (String s : functionBean.getApiUrlList()) {
                UrlEntity urlEntity = new UrlEntity();
                urlEntity.setFunctionUrl(s);
                urlEntity.setEnableFlag(1);
                urlEntity.setCreatorId(userId);
                urlEntity.setCreateTime(createTime);
                //插入功能对应URL表 一对多
                apiMapper.insertUrl(urlEntity);
                ApiUrlEntity apiUrlEntity = new ApiUrlEntity();
                apiUrlEntity.setApiId(functionBean.getFunctionId());
                apiUrlEntity.setFunctionUrlId(urlEntity.getId());
                apiUrlEntity.setEnableFlag(1);
                apiUrlEntity.setCreatorId(userId);
                apiUrlEntity.setCreateTime(createTime);
                //插入功能和URL对应中间表
                apiMapper.insertApiUrl(apiUrlEntity);
            }
        }

    }

    @Override
    public void removeApp(Integer clientId, Integer userId) {
        //查询应用下是否有模块
        Integer countModule = apiMapper.selectModuleByClientid(clientId);
        if (countModule > 0) {
            throw new MembraneException(ResponseCode.APP_NOT_EMPTY);
        }

        apiMapper.removeApp(clientId, userId);
    }

    @Override
    public void certificationApp(CertificationBean certificationBean, Integer userId) {
        if(certificationBean.getAuthentication().getCode().equals(IsAuthenticationEnum.UNVERIFIED.getCode())){
            certificationBean.setVerification(null);
        }
        apiMapper.certificationApp(certificationBean, userId);
    }

    @Override
    public List selectFunctionAuthorityByClientId(Integer clientId, Integer flag) {

        if (flag == 0) {
            //功能权限
            List<ModuleBean> moduleBeans = apiMapper.selectModuleAndFunctionByClientID(clientId,null);
            return moduleBeans;
        } else {
            return null;
        }

    }


}
