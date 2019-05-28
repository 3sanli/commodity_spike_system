package top.showtan.commodity_spike_system.vo;

import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/3/28 15:45
 */
public class PageVo<T> {
    //条件查询的所有数据
    private List<T> data;
    //总的数目，用于显示分页信息
    private Long dataCounts;
    //总的页数
    private Integer pageCount;
    //一页多少条
    private Integer pageNums;
    private Long offset;
    private Integer take;

    //封装分页查询条件
    public void createPageSearchConstant(int page, int pageNums) {
        //TODO 没有处理好offset
        page = page <= 0 ? 0 : page - 1;
        Long offset = (long) (page * pageNums);
        setOffset(offset);
        setTake(pageNums);
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Long getDataCounts() {
        return dataCounts;
    }

    public void setDataCounts(Long dataCounts) {
        this.dataCounts = dataCounts;
//        int pageCount = (int) ((dataCounts + pageNums) / pageNums);
        setPageCount(1);
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getPageNums() {
        return pageNums;
    }

    public void setPageNums(Integer pageNums) {
        this.pageNums = pageNums;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Integer getTake() {
        return take;
    }

    public void setTake(Integer take) {
        this.take = take;
    }
}
