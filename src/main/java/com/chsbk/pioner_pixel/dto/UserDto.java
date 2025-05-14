package com.chsbk.pioner_pixel.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private Set<String> emails;
    private Set<String> phones;
    private BigDecimal balance;
    private BigDecimal initialBalance;
}
