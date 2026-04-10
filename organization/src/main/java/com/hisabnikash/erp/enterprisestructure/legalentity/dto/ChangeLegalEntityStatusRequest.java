package com.hisabnikash.erp.enterprisestructure.legalentity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeLegalEntityStatusRequest {

    @NotBlank
    @Size(max = 30)
    private String status;
}
