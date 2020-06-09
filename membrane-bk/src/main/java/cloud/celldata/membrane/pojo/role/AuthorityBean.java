package cloud.celldata.membrane.pojo.role;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 权限信息实体
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorityBean {

    // 是否是全部数据权限
    private Boolean allDataAuthority;

    // 有数据权限的功能列表
    private List<DataAuthorityBean> haveDataAuthority;

    // 无数据权限的功能列表
    private List<Integer> noHaveDataAuthority;

    public Boolean getAllDataAuthority() {
        return allDataAuthority;
    }

    public void setAllDataAuthority(Boolean allDataAuthority) {
        this.allDataAuthority = allDataAuthority;
    }

    public List<DataAuthorityBean> getHaveDataAuthority() {
        return haveDataAuthority;
    }

    public void setHaveDataAuthority(List<DataAuthorityBean> haveDataAuthority) {
        this.haveDataAuthority = haveDataAuthority;
    }

    public List<Integer> getNoHaveDataAuthority() {
        return noHaveDataAuthority;
    }

    public void setNoHaveDataAuthority(List<Integer> noHaveDataAuthority) {
        this.noHaveDataAuthority = noHaveDataAuthority;
    }
}
