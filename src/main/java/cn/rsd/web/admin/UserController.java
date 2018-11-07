package cn.rsd.web.admin;

import cn.rsd.ReturnCode;
import cn.rsd.ReturnMessage;
import cn.rsd.dao.RolesMapper;
import cn.rsd.dao.SupplyPostsMapper;
import cn.rsd.dao.UsersMapper;
import cn.rsd.po.Page;
import cn.rsd.po.Roles;
import cn.rsd.po.SupplyPosts;
import cn.rsd.po.Users;
import cn.rsd.service.UserService;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author 焦云亮
 * @date 2018/9/26
 * @modifyUser
 * @modifyDate
 */
@Controller("admin/user")
@RequestMapping("/admin/user")
public class UserController {
    protected static Logger logger = LogManager.getLogger(UserController.class);
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private RolesMapper rolesMapper;

    @Autowired
    private SupplyPostsMapper supplyPostsMapper;

    @Value("${uploadPath}")
    private String uploadPath;

    @Autowired
    private UserService userService;
    /**             用户管理开始                                          **/
    @RequestMapping("index")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("users");

        List<SupplyPosts> supplyPosts = this.supplyPostsMapper.selectAll();
        List<Roles> roles = this.rolesMapper.selectAll();

        view.addObject("supplyPosts",supplyPosts);
        view.addObject("roles",roles);
        return view;
    }

    @RequestMapping(value = "list",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> usersList(Users users, HttpServletRequest request){
        Page<Users> page = Page.createPageControl(request);
        List<Users> list= this.usersMapper.selectByRowBounds(users,page);

        for(Users user:list){
            List<SupplyPosts> supplyPosts = this.supplyPostsMapper.selectSupplyPostsByUser(user.getId());

            if(supplyPosts != null && !supplyPosts.isEmpty()){
                user.setSupplyPostId(supplyPosts.get(0).getId());
            }
        }

        page.setList(list);
        Map<String,Object> map = page.createMap();

        return map;
    }

    @RequestMapping("/fileUploadPicture")
    @ResponseBody
    public ReturnMessage uploadImg(MultipartFile addressPicFile) throws IOException {
        ReturnMessage message = ReturnMessage.buildMessage();
        String dir = System.getProperty("rsd.root");
        String path = dir+ uploadPath ;

        try {
            //如果文件夹不存在
            File fileDir = new File(path);
            fileDir.mkdirs();

            String newFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(addressPicFile.getOriginalFilename());

            FileCopyUtils.copy(addressPicFile.getBytes(), new File(path + File.separator + newFileName));

            message.putData("status","ok");
            message.putData("path",uploadPath+File.separator+newFileName);
        }catch (Exception e){
            message.putData("status","error");
            message.putData("message",e.getMessage());
        }

        return message;
    }
    @RequestMapping(value = "save",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage save(Users users,Long supplyPostId){
        ReturnMessage message = ReturnMessage.buildMessage();

        try{
            userService.save(users,supplyPostId);
        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }
    /**             用户管理结束                                          **/
}
