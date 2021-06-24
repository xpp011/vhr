package cn.xpp011.vhr.service;

import cn.xpp011.vhr.mapper.EmployeeMapper;
import cn.xpp011.vhr.model.Employee;
import cn.xpp011.vhr.model.RespPageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    public final static Logger logger=LoggerFactory.getLogger(EmployeeService.class);


    public List<Employee> getAllEmp(Integer page, Integer size,  Employee employee, Date[] bigEngDate) {
        if (page!=null && size!=null){
            page=(page-1)*size;
        }
        return employeeMapper.getAllEmp(page,size,employee,bigEngDate);
    }

    public Long getTotal( Employee employee, Date[] bigEngDate) {
        return employeeMapper.getTotal(employee,bigEngDate);
    }

    public Integer addEmp(Employee employee) {
        //由于需要发送邮件  在插入emp时  我们还需要用到它的id  这就需要在mapper的sql语句中加入主键回调
        //将插入好的主键返回来
        Integer i = employeeMapper.addEmp(employee);
        if (i>=1){
            //根据回填的id在把emp查回来  主要是获取他的部门名字，职称名字，职位名字
            Employee emp = employeeMapper.getEmpById(employee.getId());
            //像队列xpp011Queue 发送对象employee
            rabbitTemplate.convertAndSend("xpp011Queue",emp);
            logger.info("服务层生产"+emp.toString());
        }
        return i;
    }

    public Integer getMaxworkID() {
        return employeeMapper.getMaxworkID();
    }

    public Integer deleteEmpById(Integer id) {
        return employeeMapper.deleteEmpById(id);
    }

    public Integer updateEmp(Employee employee) {
        return employeeMapper.updateEmp(employee);
    }

    public Integer insertEmps(List<Employee> list) {
        Integer i=0;
        for (Employee employee : list) {
            i+=employeeMapper.addEmp(employee);
        }
        return i;
    }

    public RespPageBean getAllEmployeeWithSalaryPage(Integer page, Integer size) {

        if (page!=null && size!=null){
            page=(page-1)*size;
        }

        List<Employee> list=employeeMapper.getAllEmployeeWithSalaryPage(page,size);
        Long total = employeeMapper.getTotal(null, null);

        RespPageBean pageBean = new RespPageBean();
        pageBean.setData(list);
        pageBean.setTotal(total);

        return pageBean;
    }
}
