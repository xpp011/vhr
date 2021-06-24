package cn.xpp011.vhr.controller.salary;

import cn.xpp011.vhr.model.RespPageBean;
import cn.xpp011.vhr.model.ResponseBean;
import cn.xpp011.vhr.model.Salary;
import cn.xpp011.vhr.service.EmployeeService;
import cn.xpp011.vhr.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salary/sobcfg")
public class SobConfigController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    SalaryService salaryService;

    @GetMapping("/")
    public RespPageBean getAllEmployeeWithSalaryPage(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer size){
        RespPageBean pageBean=employeeService.getAllEmployeeWithSalaryPage(page,size);
        return pageBean;
    }

    @GetMapping("/AllSalary")
    public ResponseBean getAllSalary(){
        List<Salary> allSalary = salaryService.getAllSalary();
        if (allSalary!=null){
            return ResponseBean.ok(null,allSalary);
        }
        return ResponseBean.error("工资套账查询失败");
    }

    @PutMapping("/{eid}/{sid}")
    public ResponseBean updateEmpSalaryById(@PathVariable("eid") Integer eid,@PathVariable("sid") Integer sid){
        Integer i=salaryService.updateEmpSalaryById(eid,sid);
        if (i>=1){
            return ResponseBean.ok("修改成功");
        }
        return ResponseBean.error("修改失败");
    }

}
