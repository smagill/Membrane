package cloud.celldata.membrane.pojo;

import cloud.celldata.membrane.utils.Sha256;
import org.springframework.beans.factory.annotation.Value;


import java.util.*;


public class MembraneTokenEntity {
    @Value("${membrane.issuer}")
    private String issuer;
    private Long issue_time;
    private String applicationId;
    private String user_name;
    private String application_name;
    private Integer userId;
    private List<Object> functions; //功能权限
    private List<Object> Scopes; //数据权限
    private String tokenId;

    public MembraneTokenEntity(){
        issue_time =System.currentTimeMillis();
        tokenId = UUID.randomUUID().toString().replaceAll("-","");
    }

    public String getIssuer() {
        return issuer;
    }

    public Long getIssue_time() {
        return issue_time;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<Object> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Object> functions) {
        this.functions = functions;
    }

    public List<Object> getScopes() {
        return Scopes;
    }

    public void setScopes(List<Object> scopes) {
        Scopes = scopes;
    }



    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getApplication_name() {
        return application_name;
    }

    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    public String getTokenId() {
        return tokenId;
    }
}
