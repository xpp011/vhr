package cn.xpp011.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import cn.xpp011.vhr.model.Employee;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EmployeeMapper {
    List<Employee> getAllEmp(Integer page, Integer size, @Param("emp") Employee employee,@Param("bed") Date[] bigEngDate);

    Long getTotal( @Param("emp") Employee employee,@Param("bed") Date[] bigEngDate);

    Integer addEmp(Employee employee);

    Integer getMaxworkID();

    Integer deleteEmpById(Integer id);

    Integer updateEmp(Employee employee);

    Employee getEmpById(Integer id);

    List<Employee> getAllEmployeeWithSalaryPage(Integer page, Integer size);
}