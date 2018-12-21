package cn.rsd.po;

import cn.rsd.base.BasePo;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author 焦云亮
 * @date 2018/9/26
 * @modifyUser
 * @modifyDate
 */
public class Roles extends BasePo implements GrantedAuthority {
    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + this.getId();
    }
}
