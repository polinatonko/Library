package com.example.library.validation;

import com.example.library.dto.UserDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {
        UserDto userDto = (UserDto) o;
        boolean isValid = userDto.getPassword().equals(userDto.getMatchingPassword());
        if (!isValid)
        {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("matchingPassword").addConstraintViolation();
        }
        return isValid;
    }
}
