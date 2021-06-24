package cn.xpp011.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import cn.xpp011.vhr.model.Hr;
import cn.xpp011.vhr.model.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HrMapper {

    Hr loadUserByUsername(String username);

    List<Role> getRolesByHrId(Integer id);

    List<Hr> getAllHrs(Integer id,String searchHrname);

    Integer updateHr(Hr hr);

    Integer deleteRolesByHid(Integer hid);

    Integer insertRoles(Integer hid, Integer[] rids);

    List<Hr> getAllHrsExceptCurrentHr(Integer id);
}