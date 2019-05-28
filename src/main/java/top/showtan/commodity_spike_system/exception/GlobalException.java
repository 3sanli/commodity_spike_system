package top.showtan.commodity_spike_system.exception;


import top.showtan.commodity_spike_system.util.CodeMsg;

/**
 * @Author: sanli
 * @Date: 2019/3/20 10:07
 */
public class GlobalException extends RuntimeException {
    private CodeMsg cms;

    public GlobalException(CodeMsg cms) {
        super(cms.getMsg());
        this.cms = cms;
    }

    public CodeMsg getCms() {
        return cms;
    }
}
