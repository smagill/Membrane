package cloud.celldata.membrane.pojo.vo;

import cloud.celldata.membrane.pojo.enumeration.UserChangeFlagEnum;
import cloud.celldata.membrane.pojo.enumeration.UserStatusEnum;

import java.util.List;

/**
 * 用户信息实体
 *
 * @author wyw
 * @date 2019-04-25
 **/
public class UserVO {

    // 用户id
    private Integer id;

    // 用户名
    private String userName;

    // 姓名
    private String fullName;

    // 手机号
    private String telephone;

    // 邮箱
    private String email;

    // 0 代表编辑用户，1代表改变用户状态
    private UserChangeFlagEnum flag;

    // 状态码: 1-启用; 2-禁用
    private UserStatusEnum status;

    // 角色列表
    private List<SelectedClientRoleVO> roles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<SelectedClientRoleVO> getRoles() {
        return roles;
    }

    public void setRoles(List<SelectedClientRoleVO> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserChangeFlagEnum getFlag() {
        return flag;
    }

    public void setFlag(UserChangeFlagEnum flag) {
        this.flag = flag;
    }

    public UserStatusEnum getStatus() {
        return status;
    }

    public void setStatus(UserStatusEnum status) {
        this.status = status;
    }
}
