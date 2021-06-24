package cn.xpp011.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import cn.xpp011.vhr.model.Position;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionMapper {

    public List<Position> getAllPositions();

    public Integer addPosition(Position position);

    public Integer updatePosition(Position position);

    public Integer deletePositionById(Integer id);

    Integer deletePositionAllByIds(Integer[] ids);
}