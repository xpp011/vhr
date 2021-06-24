package cn.xpp011.vhr.mapper;

import cn.xpp011.vhr.model.Department;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentMapper {

    List<Department> getDepartmentAll(Integer parentId);

    void addDepartment(Department department);

    void deleteDepartmentById(Department department);

    List<Department> getDepartmentAllByExcel();

}