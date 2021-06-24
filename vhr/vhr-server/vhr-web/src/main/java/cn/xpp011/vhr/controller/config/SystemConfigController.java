package cn.xpp011.vhr.controller.config;

import cn.xpp011.vhr.model.Hr;
import cn.xpp011.vhr.model.Menu;
import cn.xpp011.vhr.model.ResponseBean;
import cn.xpp011.vhr.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/config")
public class SystemConfigController {
    @Autowired
    MenuService menuService;

    //菜单请求
    @GetMapping("/menu")
    public ResponseBean getMenusByHrId(){
        List<Menu> list=menuService.getMenusByHrId();
        if (list!=null){
            return ResponseBean.ok(null,list);
        }
        return ResponseBean.error("菜单加载查询失败");

    }
}
