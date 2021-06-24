package cn.xpp011.vhr.controller.HrController;


import cn.xpp011.vhr.mapper.MenuRoleMapper;
import cn.xpp011.vhr.model.Hr;
import cn.xpp011.vhr.model.ResponseBean;
import cn.xpp011.vhr.model.Role;
import cn.xpp011.vhr.service.HrService;
import cn.xpp011.vhr.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/hr")
public class HrController {
    @Autowired
    HrService hrService;

    @Autowired
    RoleService roleService;


    //获取所以的hr对象
    @GetMapping("/")
    public ResponseBean getAllHrs(String searchHrname){
        List<Hr> list=hrService.getAllHrs(searchHrname);
        if (list!=null){
            return ResponseBean.ok(null,list);
        }

        return ResponseBean.error("用户信息查询事变");
    }

    //更新hr
    @PostMapping("/")
    public ResponseBean updateHr(@RequestBody Hr hr){
        Integer i=hrService.updateHr(hr);
        if (i>=1){
            return ResponseBean.ok("修改成功");
        }
        return ResponseBean.error("修改失败");
    }

    //获取角色
    @GetMapping("/Roles")
    public ResponseBean getRoleAll(){
        List<Role> roleAll = roleService.getRoleAll();
        if (roleAll!=null){
            return ResponseBean.ok(null,roleAll);
        }
        return ResponseBean.error("角色查询失败");
    }

    //更新用户的角色
    @PutMapping("/")
    public ResponseBean updateRolesByHid(Integer hid,Integer[] rids){
        Integer i=hrService.updateRolesByHid(hid,rids);
        if (i==rids.length){
            return ResponseBean.ok("更新成功");
        }
        return ResponseBean.error("更新失败");
    }

}
