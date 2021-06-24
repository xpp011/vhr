package cn.xpp011.vhr.service;

import cn.xpp011.vhr.mapper.PositionMapper;
import cn.xpp011.vhr.model.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PositionService {

    @Autowired
    PositionMapper positionMapper;

    public List<Position> getAllPosition() {
        return positionMapper.getAllPositions();
    }

    public Integer addPosition(Position position) {
        position.setEnabled(true);
        position.setCreateDate(new Date());
        return positionMapper.addPosition(position);
    }

    public Integer updatePosition(Position position) {
        if (position.getEnabled()==null){
            position.setEnabled(false);
        }
        return positionMapper.updatePosition(position);
    }

    public Integer deletePositionById(Integer id) {
        return positionMapper.deletePositionById(id);
    }

    public Integer deletePositionAllByIds(Integer[] ids) {
        return positionMapper.deletePositionAllByIds(ids);
    }
}
