package top.showtan.commodity_spike_system.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import top.showtan.commodity_spike_system.util.CodeMsg;
import top.showtan.commodity_spike_system.util.ResultUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/3/20 10:07
 */
@ControllerAdvice
@ResponseBody
public class MyExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResultUtil<String> exceptionHandler(HttpServletRequest request, Exception e) {
        if (e instanceof BindingResult) {
            List<ObjectError> errors = ((BindingResult) e).getAllErrors();
            ObjectError error = errors.get(0);
            CodeMsg cms = new CodeMsg();
            return ResultUtil.ERROR(cms.fillError(error.getDefaultMessage()));
        } else if (e instanceof GlobalException) {
            return ResultUtil.ERROR(((GlobalException) e).getCms());
        } else {
            e.printStackTrace();
            return ResultUtil.ERROR(CodeMsg.COMMON_EXCEPTION);
        }
    }
}
