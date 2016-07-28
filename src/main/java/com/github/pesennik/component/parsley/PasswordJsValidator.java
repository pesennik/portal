package com.github.pesennik.component.parsley;


import com.github.pesennik.util.Limits;
import com.github.pesennik.util.PL;
import org.apache.wicket.Component;
import org.jetbrains.annotations.Nullable;

public class PasswordJsValidator extends RequiredFieldJsValidator {

    public PasswordJsValidator(@Nullable Component errorContainer) {
        super(errorContainer);

        attributeMap.put("data-parsley-minlength", Limits.PASSWORD_MIN_LENGTH);
        attributeMap.put("data-parsley-maxlength", Limits.PASSWORD_MAX_LENGTH);

        attributeMap.put("data-parsley-minlength-message", "Мин. длина пароля: " + PL.npl(Limits.PASSWORD_MIN_LENGTH, " символ"));
        attributeMap.put("data-parsley-maxlength-message", "Пароль не может превышать " + PL.npl(Limits.PASSWORD_MAX_LENGTH, " символ"));

    }
}
