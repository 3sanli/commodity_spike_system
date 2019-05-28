package top.showtan.commodity_spike_system.util;

/**
 * @Author: sanli
 * @Date: 2019/3/19 23:34
 */
public class ResultUtil<T> {
    T data;
    int code;
    String msg;

    public ResultUtil(T data, int code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public ResultUtil(T data, CodeMsg msg) {
        this.data = data;
        this.code = msg.code;
        this.msg = msg.msg;
    }
    public static <T> ResultUtil SUCCESS(T data) {
        return new ResultUtil(data, 0, "成功");
    }

    public static <T> ResultUtil ERROR(CodeMsg msg) {
        return new ResultUtil("", msg);
    }



    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
