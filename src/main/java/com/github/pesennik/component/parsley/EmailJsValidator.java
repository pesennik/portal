package com.github.pesennik.component.parsley;


import com.github.pesennik.util.Limits;
import com.github.pesennik.util.Plurals;

public class EmailJsValidator extends RequiredFieldJsValidator {

    public EmailJsValidator() {
        attributeMap.put("data-parsley-type", "email");
        attributeMap.put("data-parsley-maxlength", Limits.EMAIL_MAX_LENGTH);
        attributeMap.put("data-parsley-maxlength-message", "Максимально допустимая длина email: " + Plurals.npl(Limits.EMAIL_MAX_LENGTH, " symbol"));
    }
}
