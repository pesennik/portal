package org.zametki.component.parsley;


import org.zametki.util.Limits;
import org.zametki.util.Plurals;

public class EmailJsValidator extends RequiredFieldJsValidator {

    public EmailJsValidator() {
        attributeMap.put("data-parsley-type", "email");
        attributeMap.put("data-parsley-maxlength", Limits.EMAIL_MAX_LENGTH);
        attributeMap.put("data-parsley-maxlength-message", "Максимально допустимая длина email: " + Plurals.npl(Limits.EMAIL_MAX_LENGTH, " symbol"));
    }
}
