package cn.xpp011.vhr.config;

import cn.xpp011.vhr.mapper.MenuMapper;
import cn.xpp011.vhr.model.Menu;
import cn.xpp011.vhr.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

//Security的过滤器管理器 这里主要判断请求资源路径的需要的权限是否和用户角色相匹配
@Component
public class CustomFilterISMS implements FilterInvocationSecurityMetadataSource {

    @Autowired
    MenuService MenuService;

    AntPathMatcher antPathMatcher=new AntPathMatcher();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        String url = ((FilterInvocation) object).getRequestUrl();
        List<Menu> menusWithRole = MenuService.getAllMenusWithRole();
        for (Menu menu : menusWithRole) {
            //判断当前url和数据库的是否匹配
            if (antPathMatcher.match(menu.getUrl(),url)){
                //匹配上就将当前路径需要的角色返回
                String [] roles=new String[menu.getRoles().size()];
                for (int i = 0; i < menu.getRoles().size(); i++) {
                    roles[i]=menu.getRoles().get(i).getName();
                }
                return SecurityConfig.createList(roles);
            }
        }
        //都没匹配上则判断当前url只需要登录就可以访问
        return SecurityConfig.createList("ROLE_Login");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
