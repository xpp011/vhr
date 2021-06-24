package cn.xpp011.vhr.controller.salary;

import cn.xpp011.vhr.model.ResponseBean;
import cn.xpp011.vhr.model.Salary;
import cn.xpp011.vhr.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salary/sob")
public class SalaryController {

    @Autowired
    SalaryService salaryService;

    @GetMapping("/")
    public ResponseBean getAllSalary(){
        List<Salary> list=salaryService.getAllSalary();
        if (list!=null){
            return ResponseBean.ok(null,list);
        }
        return ResponseBean.error("工资套账查询失败");
    }

    @PostMapping ("/")
    public ResponseBean addSalary(@RequestBody Salary salary){
        Integer i=salaryService.addSalary(salary);
        if (i>=1){
            return ResponseBean.ok("插入成功");
        }
        return ResponseBean.error("插入失败");
    }

    @DeleteMapping("/{id}")
    public ResponseBean deleteSalaryById(@PathVariable("id") Integer id){
        Integer i=salaryService.deleteSalaryById(id);
        if (i>=1){
            return ResponseBean.ok("删除成功");
        }
        return ResponseBean.error("删除失败");
    }

    @PutMapping("/")
    public ResponseBean updateSalary(@RequestBody Salary salary){
        Integer i=salaryService.updateSalary(salary);
        if (i>=1){
            return ResponseBean.ok("修改成功");
        }
        return ResponseBean.error("修改失败");
    }

}
