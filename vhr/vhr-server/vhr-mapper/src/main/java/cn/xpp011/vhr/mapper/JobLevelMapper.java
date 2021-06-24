package cn.xpp011.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import cn.xpp011.vhr.model.JobLevel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobLevelMapper {

    List<JobLevel> getAllJobLevel();

    Integer addJobLevel(JobLevel jobLevel);

    Integer updateJobLevel(JobLevel jobLevel);

    Integer deleteJobLevelById(Integer id);

    Integer deleteJobLevelAllByIds(Integer[] ids);
}