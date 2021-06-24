package cn.xpp011.vhr.controller.system.basic;

import cn.xpp011.vhr.model.Menu;
import cn.xpp011.vhr.model.ResponseBean;
import cn.xpp011.vhr.model.Role;
import cn.xpp011.vhr.service.MenuService;
import cn.xpp011.vhr.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/basic/per")
public class PermissionController {

    @Autowired
    RoleService roleService;

    @Autowired
    MenuService menuService;

    @GetMapping("/")
    public ResponseBean getRoleAll(){
        List<Role> list=roleService.getRoleAll();
        if (list!=null){
            return ResponseBean.ok(null,list);
        }
        return ResponseBean.error("查询失败");
    }

    @GetMapping("/menus")
    public ResponseBean getAllMenus(){
        List<Menu> list=menuService.getAllMenus();
        if (list!=null){
            return ResponseBean.ok(null,list);
        }
        return ResponseBean.error("menus查询失败");
    }

    //获取当前角色可访问资源的id
    @GetMapping("/menus/{rid}")
    public ResponseBean getMidsByRid(@PathVariable("rid") Integer rid){
        return ResponseBean.ok(null,roleService.getMidsByRid(rid));
    }


    @PutMapping("/menus/")
    public ResponseBean doUpdateMidsByRid(Integer rid,Integer[] mids){
        //如果mids为null   则说明用户想要取消该角色的所有可访问资源  所以在删除后直接返回不用插入
        if (mids==null) {
            roleService.deleteMidsByRid(rid);
            return ResponseBean.ok("修改成功");
        }
        Integer i=roleService.doUpdateMidsByRid(rid,mids);
        if (i==mids.length){
            return ResponseBean.ok("修改成功");
        }
        return ResponseBean.error("修改失败");
    }

    @PostMapping("/")
    public ResponseBean addRole(@RequestBody Role role){
        Integer i=roleService.addRole(role);
        if (i>=1){
            return ResponseBean.ok("插入成功");
        }
        return ResponseBean.error("插入失败");
    }

    @DeleteMapping("/{rid}")
    public ResponseBean deleteRole(@PathVariable("rid") Integer rid){
        Integer i=roleService.deleteRoleById(rid);
        if (i>=1){
            return ResponseBean.ok("删除成功");
        }
        return ResponseBean.error("删除失败");
    }
}
