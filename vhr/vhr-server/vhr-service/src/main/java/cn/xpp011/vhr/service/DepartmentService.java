package cn.xpp011.vhr.service;

import cn.xpp011.vhr.mapper.DepartmentMapper;
import cn.xpp011.vhr.model.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    DepartmentMapper departmentMapper;


    public List<Department> getDepartmentAll() {
        return departmentMapper.getDepartmentAll(-1);
    }

    public void addDepartment(Department department) {
        if (department.getEnabled()==null){
            department.setEnabled(true);
        }
        departmentMapper.addDepartment(department);
    }

    public void deleteDepartmentById(Department department) {
        departmentMapper.deleteDepartmentById(department);
    }

    public List<Department> getDepartmentAllByExcel(){
        return departmentMapper.getDepartmentAllByExcel();
    }
}
