package cloud.celldata.membrane.pojo.entity;

/**
 * 平台信息实体
 */
public class ClientEntity {

    // 平台id
    private Integer id;

    // appId
    private String appId;

    // appKey
    private String appKey;

    // 平台名称
    private String appName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
