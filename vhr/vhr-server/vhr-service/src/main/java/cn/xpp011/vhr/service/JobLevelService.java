package cn.xpp011.vhr.service;

import cn.xpp011.vhr.mapper.JobLevelMapper;
import cn.xpp011.vhr.model.JobLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JobLevelService {

    @Autowired
    JobLevelMapper jobLevelMapper;


    public List<JobLevel> getAllJobLevel() {
        return jobLevelMapper.getAllJobLevel();
    }

    public Integer addJobLevel(JobLevel jobLevel) {
        jobLevel.setCreateDate(new Date());
        if (jobLevel.getEnabled()==null){
            jobLevel.setEnabled(true);
        }
        return jobLevelMapper.addJobLevel(jobLevel);
    }

    public Integer updateJobLevel(JobLevel jobLevel) {
        if (jobLevel.getEnabled()==null){
            jobLevel.setEnabled(false);
        }
        return jobLevelMapper.updateJobLevel(jobLevel);
    }

    public Integer deleteJobLevelById(Integer id) {
        return jobLevelMapper.deleteJobLevelById(id);
    }


    public Integer deleteJobLevelAllByIds(Integer[] ids) {
        return jobLevelMapper.deleteJobLevelAllByIds(ids);
    }
}
