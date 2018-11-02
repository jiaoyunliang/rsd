package cn.rsd.base;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author 焦云亮
 * @date 2018/7/30
 * @modifyUser
 * @modifyDate
 */
public class BasePo implements Serializable {

    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
