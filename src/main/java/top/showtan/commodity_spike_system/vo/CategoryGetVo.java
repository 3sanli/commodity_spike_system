package top.showtan.commodity_spike_system.vo;

import javax.validation.constraints.NotNull;

/**
 * @Author: sanli
 * @Date: 2019/3/31 22:24
 */
public class CategoryGetVo {
    @NotNull
    private Integer id;
    @NotNull
    private String category_name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
