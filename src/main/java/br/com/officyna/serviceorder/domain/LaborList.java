package br.com.officyna.serviceorder.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaborList {

    private String laborId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
