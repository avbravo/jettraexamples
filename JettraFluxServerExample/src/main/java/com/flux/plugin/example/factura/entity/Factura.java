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
public record Factura(
    String numeroFactura,
    LocalDate fechaEmision,
    Cliente cliente,
    List<ItemDetalle> items,
    BigDecimal tasaImpuesto, // Ej: 0.07 para 7% de ITBMS/IVA
    EstadoFactura estado,
    // Referencia opcional a la cotización original
    Cotizacion cotizacionOrigen 
) {
    public Factura {
        items = List.copyOf(items);
    }

    // Factory method conveniente para generar una factura directamente desde una cotización
    public static Factura desdeCotizacion(String numeroFactura, Cotizacion cotizacion, BigDecimal tasaImpuesto) {
        if (cotizacion.estado() != EstadoCotizacion.APROBADA) {
            throw new IllegalStateException("Solo se pueden facturar cotizaciones aprobadas.");
        }
        return new Factura(
            numeroFactura,
            LocalDate.now(),
            cotizacion.cliente(),
            cotizacion.items(),
            tasaImpuesto,
            EstadoFactura.PENDIENTE,
            cotizacion
        );
    }

    // Cálculos financieros integrados
    public BigDecimal subtotal() {
        return items.stream()
                    .map(ItemDetalle::subtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal impuesto() {
        return subtotal().multiply(tasaImpuesto);
    }

    public BigDecimal total() {
        return subtotal().add(impuesto());
    }
}