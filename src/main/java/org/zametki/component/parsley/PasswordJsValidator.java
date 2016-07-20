package org.zametki.component.parsley;


import org.zametki.util.Limits;
import org.zametki.util.Plurals;

public class PasswordJsValidator extends RequiredFieldJsValidator {

    public PasswordJsValidator() {
        attributeMap.put("data-parsley-minlength", Limits.PASSWORD_MIN_LENGTH);
        attributeMap.put("data-parsley-maxlength", Limits.PASSWORD_MAX_LENGTH);

        attributeMap.put("data-parsley-minlength-message", "Мин. длина пароля: " + Plurals.npl(Limits.PASSWORD_MIN_LENGTH, " symbol"));
        attributeMap.put("data-parsley-maxlength-message", "Пароль не может превышать " + Plurals.npl(Limits.PASSWORD_MAX_LENGTH, " symbol"));

    }
}
