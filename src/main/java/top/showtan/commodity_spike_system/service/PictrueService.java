package top.showtan.commodity_spike_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.showtan.commodity_spike_system.dao.PictureMapper;
import top.showtan.commodity_spike_system.entity.Picture;

/**
 * @Author: sanli
 * @Date: 2019/5/2 10:38
 */
@Service
public class PictrueService {
    @Autowired
    PictureMapper pictureMapper;


    public void add(Picture picture){
        pictureMapper.add(picture);
    }
}
