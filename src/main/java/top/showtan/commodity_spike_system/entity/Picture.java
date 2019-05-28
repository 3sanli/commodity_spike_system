package top.showtan.commodity_spike_system.entity;

/**
 * @Author: sanli
 * @Date: 2019/3/19 22:32
 */
public class Picture {
    private Integer id;
    private String pictureAddress;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPictureAddress() {
        return pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", pictureAddress='" + pictureAddress + '\'' +
                '}';
    }
}
