package br.com.officyna.serviceorder.api.resources;

import br.com.officyna.serviceorder.domain.enums.LaborSituation;

public record ModifySituationRequest(
        String laborId,
        LaborSituation situation
) {}
