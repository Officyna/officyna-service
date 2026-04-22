package br.com.officyna.monitoring.api;

import br.com.officyna.monitoring.api.resources.ForceRecalcResponse;
import br.com.officyna.monitoring.api.resources.LaborMonitoringResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/api/monitoring")
@Tag(name = "Monitoring", description = "Service execution time monitoring")
@SecurityRequirement(name = "bearerAuth")
public interface MonitoringApi {

    @GetMapping("/labors")
    @Operation(summary = "List all labors with their average execution time")
    @ApiResponse(responseCode = "200", description = "List returned successfully")
    ResponseEntity<List<LaborMonitoringResponse>> findAll();

    @Hidden
    @PostMapping("/test-update")
    ResponseEntity<Void> testUpdate(
            @RequestParam String laborId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate
    );

    @Hidden
    @PutMapping("/force-recalc")
    @Operation(
            summary = "Force recalculation of average execution time for all labors",
            description = "Scans all service orders and recalculates the average execution time from scratch for every labor"
    )
    @ApiResponse(responseCode = "200", description = "Recalculation completed successfully")
    ResponseEntity<ForceRecalcResponse> forceRecalc();
}