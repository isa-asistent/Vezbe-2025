package isa.vezbe1.validation_example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomConstraintValidator implements ConstraintValidator<CustomConstraint, String> {

    /*
         poziva se svaki put pre upotrebe instance validatora
     */
    @Override
    public void initialize(CustomConstraint constraintAnnotation) {
        // You can set up any required data here if needed
    }

    /*
    vrsi validaciju custom polja koje je anotirano
    */
    @Override
    public boolean isValid(String customField, ConstraintValidatorContext ctx) {
        if (customField == null) {
            return false;
        }
        return customField.matches("[0-9]{13}");
    }
}