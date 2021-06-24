package cn.xpp011.vhr.controller.emp;

import cn.xpp011.vhr.model.*;
import cn.xpp011.vhr.service.*;
import cn.xpp011.vhr.utils.POIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/employee/basic")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    PoliticsstatusService politicsstatusService;

    @Autowired
    NationService nationService;

    @Autowired
    PositionService positionService;

    @Autowired
    JobLevelService jobLevelService;

    @Autowired
    DepartmentService departmentService;

    SimpleDateFormat yearFormat=new SimpleDateFormat("yyyy");
    SimpleDateFormat monthFormat=new SimpleDateFormat("MM");
    DecimalFormat decimalFormat=new DecimalFormat("##.00");

    @GetMapping("/")
    public ResponseBean getAllEmp(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size, Employee employee, Date[] bigEngDate) {
        RespPageBean pageBean = new RespPageBean();
        pageBean.setData(employeeService.getAllEmp(page,size,employee,bigEngDate));
        pageBean.setTotal(employeeService.getTotal(employee,bigEngDate));
        if (pageBean.getData() != null && pageBean.getTotal() != null) {
            return ResponseBean.ok(null, pageBean);
        }
        return ResponseBean.error("数据查询失败");
    }

    @PostMapping("/")
    public ResponseBean addEmp(@RequestBody Employee employee) {
        if (employee.getWorkState()==null || "".equals(employee.getWorkState())){
            employee.setWorkState("在职");
        }
        //根据前端传来的合同起始时间和结束时间  计算合同有效时间
        if (contractTime(employee)) return ResponseBean.error("插入失败");

        Integer i = employeeService.addEmp(employee);
        if (i >= 1) {
            return ResponseBean.ok("插入成功");
        }
        return ResponseBean.error("插入失败");
    }

    @GetMapping("/politicsstatus")
    public ResponseBean getPoliticsstatus() {
        List<Politicsstatus> list = politicsstatusService.getPoliticsstatus();
        if (list != null) {
            return ResponseBean.ok(null, list);
        }
        return ResponseBean.error("政治面貌查询失败");
    }

    @GetMapping("/nations")
    public ResponseBean getNations(){
        List<Nation> list=nationService.getNations();
        if (list != null) {
            return ResponseBean.ok(null, list);
        }
        return ResponseBean.error("民族查询失败");
    }

    @GetMapping("/positions")
    public ResponseBean getPositions(){
        List<Position> list=positionService.getAllPosition();
        if (list != null) {
            return ResponseBean.ok(null, list);
        }
        return ResponseBean.error("职位查询失败");
    }

    @GetMapping("/joblevels")
    public ResponseBean getJoblevels(){
        List<JobLevel> list=jobLevelService.getAllJobLevel();
        if (list != null) {
            return ResponseBean.ok(null, list);
        }
        return ResponseBean.error("职称查询失败");
    }

    @GetMapping("/MaxworkID")
    public ResponseBean getMaxworkID(){
        Integer maxWorkID=employeeService.getMaxworkID();
        return ResponseBean.ok(null,String.format("%08d",maxWorkID+1));
    }

    @GetMapping("/AllDeps")
    public ResponseBean getAllDeps(){
        List<Department> departmentAll = departmentService.getDepartmentAll();
        if (departmentAll!=null){
            return ResponseBean.ok(null,departmentAll);
        }
        return ResponseBean.error("部门集合查询失败");
    }

    @DeleteMapping("/{id}")
    public ResponseBean deleteEmpById(@PathVariable Integer id){
        Integer i=employeeService.deleteEmpById(id);
        if (i>=1){
            return ResponseBean.ok("删除成功");
        }
        return ResponseBean.error("删除失败");
    }

    @PutMapping("/")
    public ResponseBean updateEmp(@RequestBody Employee employee){
        //根据前端传来的合同起始时间和结束时间  计算合同有效时间
        if (contractTime(employee)) return ResponseBean.error("插入失败");

        Integer i=employeeService.updateEmp(employee);
        if (i>=1){
            return ResponseBean.ok("修改成功");
        }
        return ResponseBean.error("修改失败");
    }


    //导出员工信息的excel
    @GetMapping("/exportEmp")
    //ResponseEntity可以作为controller的返回值，比如对于一个处理下载二进制文件的接口
    public ResponseEntity<byte[]> exportEmp(){
        List<Employee> list=employeeService.getAllEmp(null,null, null,null);
        return POIUtil.employeeExcel(list);
    }

    /**
     * 将Excel解析成Employee集合后插入数据库
     * @param file  上传的Excel
     * @return 返回是否上传成功
     */
    //上传文件导入Excel数据
    @PostMapping("/import")
    public ResponseBean importEmp(MultipartFile file){
        try {
            List<Employee>  list=POIUtil.importEmp(file,nationService.getNations(),politicsstatusService.getPoliticsstatus(),
                    departmentService.getDepartmentAllByExcel(),positionService.getAllPosition(),jobLevelService.getAllJobLevel());
            //开始插入数据
            Integer i=employeeService.insertEmps(list);
            if (i==list.size()){
                return ResponseBean.ok("所有数据插入成功");
            }
            return ResponseBean.error("插入数据不完整;共插入"+i+"条数据");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBean.error("上传失败,请检查Excel表单");
        }
    }






    private boolean contractTime(@RequestBody Employee employee) {
        try {
            //年差值
            Double year=Double.parseDouble(yearFormat.format(employee.getEndContract()))-Double.parseDouble(yearFormat.format(employee.getBeginContract()));
            //月差值
            Double month=Double.parseDouble(monthFormat.format(employee.getEndContract()))-Double.parseDouble(monthFormat.format(employee.getBeginContract()));
            Double res=Double.parseDouble(decimalFormat.format((year*12+month)/12));
            employee.setContractTerm(res);
        }catch (Exception e){
            return true;
        }
        return false;
    }



}