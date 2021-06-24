package cn.xpp011.vhr.mapper;

import cn.xpp011.vhr.model.Salary;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SalaryMapper {

    List<Salary> getAllSalary();

    Integer insertSalary(Salary salary);

    Integer deleteSalaryById(Integer id);

    Integer updateSalary(Salary salary);

    Integer deleteSalaryWhitEmpById(Integer eid);

    Integer insertSalaryWhitEmpById(Integer eid, Integer sid);

}