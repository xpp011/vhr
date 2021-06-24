package cn.xpp011.vhr.controller.system.basic;

import cn.xpp011.vhr.model.Department;
import cn.xpp011.vhr.model.ResponseBean;
import cn.xpp011.vhr.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/basic/dep")
public class DepartmentController {

    @Autowired
    DepartmentService departmentService;

    @GetMapping("/")
    public ResponseBean getDepartmentAll(){
        List<Department> list=departmentService.getDepartmentAll();
        if (list!=null){
            return ResponseBean.ok(null,list);
        }
        return ResponseBean.ok("部门组查询失败");
    }

    @PostMapping("/")
    public ResponseBean addDepartment(@RequestBody Department department){
        departmentService.addDepartment(department);
        if (department.getResult()==1){
            return ResponseBean.ok("插入成功",department);
        }
        return ResponseBean.error("插入失败");
    }

    @DeleteMapping("/{id}")
    public ResponseBean deleteDepartmentById(@PathVariable("id") Integer id){
        Department department=new Department();
        department.setId(id);
        departmentService.deleteDepartmentById(department);
        if (department.getResult()==-2){
            return ResponseBean.error("该部门为顶级部门,无法删除");
        }else if (department.getResult()==-1){
            return ResponseBean.error("该部门还有员工,删除失败");
        }else if (department.getResult()==1){
            return ResponseBean.ok("删除成功",department);
        }
        return ResponseBean.error("删除失败");
    }
}
