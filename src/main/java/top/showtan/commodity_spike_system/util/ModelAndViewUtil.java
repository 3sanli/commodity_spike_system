package top.showtan.commodity_spike_system.util;

import org.springframework.web.servlet.ModelAndView;

public class ModelAndViewUtil {
    public static ModelAndView CreateModelAndView(String contentView){
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("contentView",contentView);
        return mv;
    }
}
