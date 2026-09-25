/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.flux.plugin.example.factura.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author avbravo
 */
public record Cotizacion(
    String numeroCotizacion,
    LocalDate fechaEmision,
    LocalDate fechaVencimiento,
    Cliente cliente,
    List<ItemDetalle> items,
    EstadoCotizacion estado
) {
    public Cotizacion {
        // Garantizar que la lista de ítems sea inmutable
        items = List.copyOf(items);
    }

    // Cálculo dinámico del total acumulado de la cotización
    public BigDecimal total() {
        return items.stream()
                    .map(ItemDetalle::subtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}