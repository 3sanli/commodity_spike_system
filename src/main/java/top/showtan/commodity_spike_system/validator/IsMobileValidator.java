package top.showtan.commodity_spike_system.validator;

import top.showtan.commodity_spike_system.util.ValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author: sanli
 * @Date: 2019/3/19 23:19
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    //是否属于必须验证的内容
    private boolean isRequired = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        isRequired = true;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!isRequired) {
            return true;
        }
        return ValidatorUtil.isMobile(value);
    }
}
