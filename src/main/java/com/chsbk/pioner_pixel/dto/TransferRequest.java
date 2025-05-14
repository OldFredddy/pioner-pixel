package com.chsbk.pioner_pixel.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {

    @NotBlank
    private String to;

    @NotNull
    @Min(1)
    private BigDecimal amount;
}
