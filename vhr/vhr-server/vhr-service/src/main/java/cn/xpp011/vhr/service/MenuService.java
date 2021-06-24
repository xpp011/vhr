package cn.xpp011.vhr.service;

import cn.xpp011.vhr.mapper.MenuMapper;
import cn.xpp011.vhr.model.Hr;
import cn.xpp011.vhr.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    MenuMapper menuMapper;

    public List<Menu> getMenusByHrId() {
        return menuMapper.getMenusByHrId(((Hr)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
    }

    //由于每一请求都去数据库里查询url  浪费资源  可以放到缓存中
    //@Cacheable  需要加redis 待会统一加
    public List<Menu> getAllMenusWithRole() {
        return menuMapper.getAllMenusWithRole();
    }

    public List<Menu> getAllMenus() {
        return menuMapper.getAllMenus();
    }
}
