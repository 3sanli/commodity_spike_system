package top.showtan.commodity_spike_system.vo;

import javax.validation.constraints.NotNull;

/**
 * @Author: sanli
 * @Date: 2019/3/31 23:17
 */
public class CategoryAddVo {
    private Integer id;
    @NotNull
    private String categoryName;
    private Integer parentId;
    private Integer isParent;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getIsParent() {
        return isParent;
    }

    public void setIsParent(Integer isParent) {
        this.isParent = isParent;
    }
}
