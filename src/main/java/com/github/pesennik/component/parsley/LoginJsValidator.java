package com.github.pesennik.component.parsley;

import com.github.pesennik.util.Limits;
import com.github.pesennik.util.PL;
import org.apache.wicket.Component;
import org.jetbrains.annotations.Nullable;

public class LoginJsValidator extends RequiredFieldJsValidator {

    public LoginJsValidator(@Nullable Component errorContainer) {
        super(errorContainer);
        attributeMap.put("data-parsley-minlength", Limits.LOGIN_MIN_LENGTH);
        attributeMap.put("data-parsley-maxlength", Limits.LOGIN_MAX_LENGTH);
        attributeMap.put("data-parsley-minlength-message", "Мин. длина имени пользователя: " + PL.npl(Limits.LOGIN_MIN_LENGTH, " символ"));
        attributeMap.put("data-parsley-maxlength-message", "Имя пользователя не может превышать " + PL.npl(Limits.LOGIN_MAX_LENGTH, " символ"));
    }

}
