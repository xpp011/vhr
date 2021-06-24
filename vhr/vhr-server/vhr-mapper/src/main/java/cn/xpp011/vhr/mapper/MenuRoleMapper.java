package cn.xpp011.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import cn.xpp011.vhr.model.MenuRole;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRoleMapper {

    List<Integer> getMidsByRid(Integer rid);

    Integer deleteMidsByRid(Integer rid);

    Integer insertMidsByRid(@Param("rid") Integer rid,@Param("mids") Integer[] mids);
}