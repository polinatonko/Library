package com.example.library.validation;


import com.example.library.repositories.PublisherRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class UniqueISBNPrefixValidator implements ConstraintValidator<UniqueISBNPrefix, String> {

    @Autowired
    private PublisherRepository publisherRepository;

    @Override
    public boolean isValid(String prefix, ConstraintValidatorContext context) {
        return !publisherRepository.existsByISBNPrefix(prefix);
    }
}