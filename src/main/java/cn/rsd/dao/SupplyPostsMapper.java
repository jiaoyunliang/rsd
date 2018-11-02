package cn.rsd.dao;

import cn.rsd.base.BaseMapper;
import cn.rsd.po.SupplyPostUser;
import cn.rsd.po.SupplyPosts;
import cn.rsd.po.Users;

import java.util.List;

/**
 * @author 焦云亮
 * @date 2018/9/26
 * @modifyUser
 * @modifyDate
 */
public interface SupplyPostsMapper extends BaseMapper<SupplyPosts> {

    int insertSupplyUser(SupplyPostUser supplyPostUser);

    int delSupplyUser(SupplyPostUser supplyPostUser);

    List<Users> selectUserBySupplyPosts(Long supplyPostId);

    List<SupplyPosts> selectSupplyPostsByUser(Long userId);
}
