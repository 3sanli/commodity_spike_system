package top.showtan.commodity_spike_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import top.showtan.commodity_spike_system.util.FastDFSClient;
import top.showtan.commodity_spike_system.util.ResultUtil;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/picture")
public class PictureController {
    private static List<InetSocketAddress> inetSocketAddressList = new ArrayList<>();
    private final static String URL_PRE = "http://47.106.244.103:8080/";
    private final static String conf = "47.106.244.103:22122";
    static {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("47.106.244.103", 22122);
        inetSocketAddressList.add(inetSocketAddress);
    }


    @RequestMapping("/save")
    @ResponseBody
    public ResultUtil<String> save(@RequestParam("picture") MultipartFile picture) throws Exception {
        FastDFSClient client = new FastDFSClient(conf);
        String url = URL_PRE + client.uploadFile(picture.getBytes(), getExtName(picture.getOriginalFilename()));
        return ResultUtil.SUCCESS(url);
    }

    private String getExtName(String fileName) {
        int i = fileName.lastIndexOf(".") + 1;
        String extName = fileName.substring(i, fileName.length());
        return extName;
    }
}
