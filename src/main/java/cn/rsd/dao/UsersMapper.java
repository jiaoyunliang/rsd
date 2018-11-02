package cn.rsd.dao;

import cn.rsd.base.BaseMapper;
import cn.rsd.po.Menu;
import cn.rsd.po.Users;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 焦云亮
 * @date 2018/7/30
 * @modifyUser
 * @modifyDate
 */
@Component
public interface UsersMapper extends BaseMapper<Users> {
    /**
     * 根据用户ID查找菜单信息
     * @param id
     * @return
     */
    List<Menu> findMenusByUserId(Long id);

    List<Users> selectUserByAccountAndRowBounds(Users users,RowBounds rowBounds);
}
