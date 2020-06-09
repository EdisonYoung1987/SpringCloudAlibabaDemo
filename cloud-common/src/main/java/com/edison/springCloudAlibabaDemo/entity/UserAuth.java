package com.edison.springCloudAlibabaDemo.entity;


import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**用于接收从user-microsvcs返回的用户信息，含密码，用于认证<p/>
 * 还包含其他用户基本信息</>*/
@ToString
public class UserAuth extends User {
    private static final long serialVersionUID=1L;

    public UserAuth(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    /**@param credentialsNonExpired 密钥未过期
     * @param accountNonLocked 用户未被锁*/
    public UserAuth(String username, String password,   boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, true, true, credentialsNonExpired, accountNonLocked, authorities);
    }
}
