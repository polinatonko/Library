package com.example.library.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class BookingDto {
    @NonNull
    @NotEmpty
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date lastDay;
}
