package cn.xpp011.vhr.controller.system.basic;

import cn.xpp011.vhr.model.Position;
import cn.xpp011.vhr.model.ResponseBean;
import cn.xpp011.vhr.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/basic/pos")
public class PositionController {

    @Autowired
    PositionService positionService;

    @GetMapping("/")
    public ResponseBean getAllPosition(){
        List<Position> list=positionService.getAllPosition();
        if (list!=null){
            return ResponseBean.ok(null,list);
        }
        return ResponseBean.error("职位查询失败");
    }

    @PostMapping("/")
    public ResponseBean addPosition(@RequestBody Position position){
        Integer i=positionService.addPosition(position);
        if (i>=1){
            return ResponseBean.ok("插入成功");
        }
        return ResponseBean.error("插入失败");
    }

    @PutMapping("/")
    public ResponseBean updatePosition(@RequestBody Position position){
        Integer i=positionService.updatePosition(position);
        if (i>=1){
            return ResponseBean.ok("修改成功");
        }
        return ResponseBean.error("修改失败");
    }

    @DeleteMapping("/{id}")
    public ResponseBean deletePositionById(@PathVariable Integer id){
        Integer i=positionService.deletePositionById(id);
        if (i>=1){
            return ResponseBean.ok("删除成功");
        }
        return ResponseBean.error("删除失败，该职位正在被使用");
    }

    @DeleteMapping("/")
    public ResponseBean deletePositionAllByIds(Integer [] ids){
        if (positionService.deletePositionAllByIds(ids)==ids.length){
            return ResponseBean.ok("批量删除成功");
        }
        return ResponseBean.error("批量删除失败");
    }

}
