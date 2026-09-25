/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.flux.plugin.example.factura.entity;

import java.math.BigDecimal;

/**
 *
 * @author avbravo
 */
public record ItemDetalle(
    String idProducto,
    String descripcion,
    int cantidad,
    BigDecimal precioUnitario
) {
    // Constructor compacto para validar datos
    public ItemDetalle {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }
    }

    // Método helper para calcular el subtotal del ítem
    public BigDecimal subtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}