package cn.rsd.service.impl;

import cn.rsd.dao.SupplyPostsMapper;
import cn.rsd.dao.UserAccountMapper;
import cn.rsd.dao.UserTrackMapper;
import cn.rsd.dao.UsersMapper;
import cn.rsd.po.*;
import cn.rsd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 焦云亮
 * @date 2018/5/4
 * @modifyUser
 * @modifyDate
 */
@Service("myUserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService,ClientDetailsService {

    @Autowired
    private UsersMapper userMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private SupplyPostsMapper supplyPostsMapper;

    @Autowired
    private UserTrackMapper userTrackMapper;

    @Override
    public Users loadByUserName(String userName) throws Exception {
        Users user = new Users();

        user.setUserName(userName);
        return this.userMapper.selectOne(user);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        Example  example = new Example(Users.class);
        List<Long> roles = new ArrayList<>();
        roles.add(1L);
        roles.add(2L);

        example.createCriteria().andEqualTo("userName",userName).andIn("role", roles);

        List<Users> usersList = this.userMapper.selectByExample(example);

        if(usersList == null || usersList.isEmpty()){
            throw new UsernameNotFoundException("用户名没有找到");
        }
        return usersList.get(0);

    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return null;
    }

    @Override
    public List<Menu> loadMenusByUserId(Long id) throws Exception {
        return this.userMapper.findMenusByUserId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(Users users, Long supplyPostId) throws Exception {

        int returnVal;

        if(users.getId() == null){
            //新增用户设置状态为正常
            users.setState(1);
            returnVal = this.userMapper.insertUseGeneratedKeys(users);
        }else{
            returnVal = this.userMapper.updateByPrimaryKey(users);
        }

        //暂时同一用户只能属于一个供应点.
        SupplyPostUser supplyPostUser = new SupplyPostUser();
        //supplyPostUser.setSupplyId(supplyPostId);
        supplyPostUser.setUserId(users.getId());

        this.supplyPostsMapper.delSupplyUser(supplyPostUser);

        this.supplyPostsMapper.insertSupplyUser(supplyPostUser);

        return returnVal;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int accountRecharge(UserAccount userAccount, Integer balance) throws Exception {

        //未找到用户账户信息,则创建一个.
        UserAccount oldAccount = new UserAccount();
        oldAccount.setUserId(userAccount.getUserId());
        oldAccount = this.userAccountMapper.selectOne(oldAccount);

        if(oldAccount == null){
            oldAccount = new UserAccount();
            oldAccount.setCreateDate(new Date());
            oldAccount.setBalance(balance);
            oldAccount.setUserId(userAccount.getUserId());
            oldAccount.setCreateUser(userAccount.getUpdateUser());
            oldAccount.setUpdateDate(userAccount.getUpdateDate());
            oldAccount.setUpdateUser(userAccount.getUpdateUser());

            this.userAccountMapper.insertUseGeneratedKeys(oldAccount);
            userAccount = oldAccount;

        }else {
            oldAccount.setUpdateDate(userAccount.getUpdateDate());
            oldAccount.setUpdateUser(userAccount.getUpdateUser());
            oldAccount.setBalance(oldAccount.getBalance() + balance);
            this.userAccountMapper.updateByPrimaryKeySelective(oldAccount);
        }
        //增加交易记录
        UserTrack userTrack = new UserTrack();
        userTrack.setOrderId(null);
        userTrack.setAccountBalance(balance);
        userTrack.setTrackType(UserTrack.TrackTypeEnum.PAY.ordinal());
        userTrack.setCreateDate(new Date());
        userTrack.setDeductionsAccount(oldAccount.getId());

        this.userTrackMapper.insert(userTrack);

        return 0;
    }
}
