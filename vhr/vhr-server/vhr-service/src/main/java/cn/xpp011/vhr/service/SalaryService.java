package cn.xpp011.vhr.service;

import cn.xpp011.vhr.mapper.SalaryMapper;
import cn.xpp011.vhr.model.Salary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SalaryService {

    @Autowired
    SalaryMapper salaryMapper;


    public List<Salary> getAllSalary() {
        return salaryMapper.getAllSalary();
    }

    public Integer addSalary(Salary salary) {
        salary.setCreateDate(new Date());
        return salaryMapper.insertSalary(salary);
    }

    public Integer deleteSalaryById(Integer id) {
        return salaryMapper.deleteSalaryById(id);
    }

    public Integer updateSalary(Salary salary) {
        salary.setCreateDate(new Date());
        return salaryMapper.updateSalary(salary);
    }

    @Transactional
    public Integer updateEmpSalaryById(Integer eid, Integer sid) {
        salaryMapper.deleteSalaryWhitEmpById(eid);
        return salaryMapper.insertSalaryWhitEmpById(eid,sid);
    }
}
