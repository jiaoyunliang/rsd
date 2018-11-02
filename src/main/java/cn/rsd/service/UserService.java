package cn.rsd.service;



import cn.rsd.po.UserAccount;
import cn.rsd.po.Users;
import cn.rsd.po.Menu;
import java.util.List;

/**
 * @author 焦云亮
 * @date 2018/5/4
 * @modifyUser
 * @modifyDate
 */
public interface UserService {
    Users loadByUserName(String userName)throws Exception;

    List<Menu> loadMenusByUserId(Long id)throws Exception;

    int save(Users users,Long supplyPostId)throws Exception;

    int accountRecharge(UserAccount userAccount,Integer balance)throws Exception;
}
