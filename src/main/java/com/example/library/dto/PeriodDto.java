package com.example.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class PeriodDto {
    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date from;
    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date to;
}
