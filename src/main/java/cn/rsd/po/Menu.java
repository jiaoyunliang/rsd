package cn.rsd.po;

import cn.rsd.base.BasePo;

import javax.persistence.Table;

/**
 * @author 焦云亮
 * @date 2018/5/9
 * @modifyUser
 * @modifyDate
 */
@Table(name = "MENU")
public class Menu extends BasePo {


    private String menuName;

    private String category;

    private String icon;

    private String url;

    private Integer seq;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
