package cn.xpp011.vhr.config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
//授权决策管理器  确定最后是否是授权
public class CustomAccessDecisionManager   implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (ConfigAttribute configAttribute : configAttributes) {
            //url没有匹配上  只需要登录就行
            if ("ROLE_Login".equals(configAttribute.getAttribute())){
                //判断用户是否为登录状态
                if (authentication instanceof AnonymousAuthenticationToken){
                    throw new AccessDeniedException("未登录,请前往登录");
                }
                return;
            }
            for (GrantedAuthority authority : authorities) {
                //判断当前用户的角色有没有和页面需要角色匹配上的
                if (authority.getAuthority().equals(configAttribute.getAttribute())){
                    if (authentication instanceof AnonymousAuthenticationToken){
                        throw new AccessDeniedException("未登录,请前往登录");
                    }
                    return;
                }
            }
        }
        //一波判断下来都没有匹配上则说明没有权限
        throw  new AccessDeniedException("未授权!!!");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
