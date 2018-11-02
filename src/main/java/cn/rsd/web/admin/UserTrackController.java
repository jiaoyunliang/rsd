package cn.rsd.web.admin;

import cn.rsd.dao.UserAccountMapper;
import cn.rsd.dao.UserTrackMapper;
import cn.rsd.po.Page;
import cn.rsd.po.UserAccount;
import cn.rsd.po.UserTrack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author 焦云亮
 * @date 2018/11/1
 * @modifyUser
 * @modifyDate
 */
@Controller("admin/user/track")
@RequestMapping("/admin/user/track")
public class UserTrackController {

    @Autowired
    private UserTrackMapper userTrackMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;


    @RequestMapping(value = "list",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> trucksList(Long userId,UserTrack userTrack, HttpServletRequest request){

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userId);
        userAccount = this.userAccountMapper.selectOne(userAccount);

        if(userAccount == null){
            return null;
        }

        userTrack.setDeductionsAccount(userAccount.getId());
        Page<UserTrack> page = Page.createPageControl(request);
        List<UserTrack> list= this.userTrackMapper.selectByRowBounds(userTrack,page);
        page.setList(list);
        Map<String,Object> map = page.createMap();
        return map;
    }
}
