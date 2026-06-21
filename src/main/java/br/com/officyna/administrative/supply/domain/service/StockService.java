package br.com.officyna.administrative.supply.domain.service;

import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.repository.SupplyRepository;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.serviceorder.domain.dto.SupplyDetailDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class StockService {

    private final SupplyRepository repository;

    public StockService(SupplyRepository repository) {
        this.repository = repository;
    }

    public void reserveSupplies(List<SupplyDetailDTO> supplies) {
        if (supplies == null || supplies.isEmpty()) return;
        supplies.forEach(item -> {
            Supply supply = findById(item.getId());
            supply.setReservedQuantity(supply.getReservedQuantity() + item.getQuantity());
            repository.save(supply);
            log.debug("Reserva: {} unidades de '{}'. Total reservado: {}",
                    item.getQuantity(), supply.getName(), supply.getReservedQuantity());
        });
    }

    public void consumeSupplies(List<SupplyDetailDTO> supplies) {
        if (supplies == null || supplies.isEmpty()) return;
        supplies.forEach(item -> {
            Supply supply = findById(item.getId());
            supply.setStockQuantity(supply.getStockQuantity() - item.getQuantity());
            supply.setReservedQuantity(Math.max(0, supply.getReservedQuantity() - item.getQuantity()));
            repository.save(supply);
            log.debug("Consumo: {} unidades de '{}'. Estoque atual: {}",
                    item.getQuantity(), supply.getName(), supply.getStockQuantity());
            if (supply.getStockQuantity() < supply.getMinimumQuantity()) {
                notifyLowStock(supply);
            }
        });
    }

    public void releaseSupplies(List<SupplyDetailDTO> supplies) {
        if (supplies == null || supplies.isEmpty()) return;
        supplies.forEach(item -> {
            Supply supply = findById(item.getId());
            supply.setReservedQuantity(Math.max(0, supply.getReservedQuantity() - item.getQuantity()));
            repository.save(supply);
            log.debug("Reserva liberada: {} unidades de '{}'. Total reservado: {}",
                    item.getQuantity(), supply.getName(), supply.getReservedQuantity());
        });
    }

    private void notifyLowStock(Supply supply) {
        //TODO: implementar notificacao para o gestor manager
        // Notificação mockada — estrutura pronta para integração com serviço real de e-mail/push
        log.warn("[ESTOQUE BAIXO] Insumo '{}' (ID: {}) abaixo do mínimo. Atual: {}, Mínimo: {}",
                supply.getName(), supply.getId(),
                supply.getStockQuantity(), supply.getMinimumQuantity());
    }

    private Supply findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Supply", id));
    }
}