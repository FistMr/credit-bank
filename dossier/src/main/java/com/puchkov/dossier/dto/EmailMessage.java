package com.puchkov.dossier.dto;

import com.puchkov.dossier.enums.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage {

    private String address;
    private Theme theme;
    private UUID statementId;
}
