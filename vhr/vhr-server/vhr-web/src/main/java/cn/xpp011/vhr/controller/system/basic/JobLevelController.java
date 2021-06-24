package cn.xpp011.vhr.controller.system.basic;

import cn.xpp011.vhr.model.JobLevel;
import cn.xpp011.vhr.model.ResponseBean;
import cn.xpp011.vhr.service.JobLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/basic/job")
public class JobLevelController {

    @Autowired
    JobLevelService jobLevelService;

    @GetMapping("/")
    public ResponseBean getAllJobLevel(){
        List<JobLevel> jobLevels=jobLevelService.getAllJobLevel();
        if (jobLevels!=null){
            return ResponseBean.ok(null,jobLevels);
        }
        return ResponseBean.error("查询失败");
    }

    @PostMapping("/")
    public ResponseBean addJobLevel(@RequestBody JobLevel jobLevel){
        if (jobLevelService.addJobLevel(jobLevel)==1){
            return ResponseBean.ok("插入成功");
        }
        return ResponseBean.error("插入失败");
    }

    @PutMapping("/")
    public ResponseBean updateJobLevel(@RequestBody JobLevel jobLevel){
        if (jobLevelService.updateJobLevel(jobLevel)==1){
            return ResponseBean.ok("修改成功");
        }
        return ResponseBean.error("修改失败");
    }

    @DeleteMapping("/{id}")
    public ResponseBean deleteJobLevelById(@PathVariable("id") Integer id){
        if (jobLevelService.deleteJobLevelById(id)==1){
            return ResponseBean.ok("删除成功");
        }
        return ResponseBean.error("删除失败");
    }

    @DeleteMapping("/")
    public ResponseBean deleteJobLevelAllByIds(Integer [] ids){
        if (jobLevelService.deleteJobLevelAllByIds(ids)==ids.length){
            return ResponseBean.ok("批量删除成功");
        }
        return ResponseBean.error("批量删除失败");
    }
}
