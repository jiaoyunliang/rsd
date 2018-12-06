package cn.rsd.po;

import com.github.pagehelper.PageRowBounds;
import org.springframework.util.NumberUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 焦云亮
 * @date 2018/5/9
 * @modifyUser
 * @modifyDate
 */
public class Page<T> extends PageRowBounds{
    public static final int DEFAULT_PAGE_SIZE = 20;

    public List<T> list;

    public Page(int offset, int limit) {
        super(offset, limit);
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Map<String,Object> createMap(){
        Map<String,Object> map = new HashMap<String,Object>(4);

        map.put("data",this.getList());
        map.put("recordsTotal",this.getTotal());
        map.put("currentTotal",this.getList().size());
        map.put("recordsFiltered",this.getTotal());
        map.put("error","");

        return map;
    }

    public static Page createPageControl(HttpServletRequest request) {
        int length = NumberUtils.parseNumber(request.getParameter("length"),Integer.class);
        int start = NumberUtils.parseNumber(request.getParameter("start"),Integer.class);
        if(length > 200){
            length = DEFAULT_PAGE_SIZE;
        }
        return new Page(start,length);
    }
}
