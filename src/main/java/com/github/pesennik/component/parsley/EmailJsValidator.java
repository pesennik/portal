package com.github.pesennik.component.parsley;


import com.github.pesennik.util.Limits;
import com.github.pesennik.util.PL;
import org.apache.wicket.Component;
import org.jetbrains.annotations.Nullable;

public class EmailJsValidator extends RequiredFieldJsValidator {

    public EmailJsValidator(@Nullable Component errorContainer) {
        super(errorContainer);

        attributeMap.put("data-parsley-type", "email");
        attributeMap.put("data-parsley-maxlength", Limits.EMAIL_MAX_LENGTH);
        attributeMap.put("data-parsley-maxlength-message", "Максимально допустимая длина email: " + PL.npl(Limits.EMAIL_MAX_LENGTH, " символ"));
    }
}
