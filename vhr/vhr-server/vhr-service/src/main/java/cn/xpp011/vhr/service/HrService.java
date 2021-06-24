package cn.xpp011.vhr.service;

import cn.xpp011.vhr.mapper.HrMapper;
import cn.xpp011.vhr.mapper.MenuRoleMapper;
import cn.xpp011.vhr.mapper.RoleMapper;
import cn.xpp011.vhr.model.Hr;
import cn.xpp011.vhr.model.Role;
import cn.xpp011.vhr.utils.HrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HrService implements UserDetailsService {

    @Autowired
    HrMapper hrMapper;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Hr hr = hrMapper.loadUserByUsername(username);
        if (hr==null){
            throw new UsernameNotFoundException("用户名不存在");
        }
        //设置获取当前用户所拥有的角色
        hr.setRoles(hrMapper.getRolesByHrId(hr.getId()));
        return hr;
    }

    public List<Hr> getAllHrs(String searchHrname) {
        //不包含当前用户的hr信息
        return hrMapper.getAllHrs(HrUtil.getHr().getId(),searchHrname);
    }

    public Integer updateHr(Hr hr) {
        return hrMapper.updateHr(hr);
    }


    @Transactional
    public Integer updateRolesByHid(Integer hid, Integer[] rids) {
        hrMapper.deleteRolesByHid(hid);
        return hrMapper.insertRoles(hid,rids);
    }

    public List<Hr> getAllHrsExceptCurrentHr() {
        return hrMapper.getAllHrsExceptCurrentHr(HrUtil.getHr().getId());
    }
}
