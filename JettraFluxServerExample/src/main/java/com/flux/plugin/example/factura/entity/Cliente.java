/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.flux.plugin.example.factura.entity;

/**
 *
 * @author avbravo
 */
public record Cliente(
    String id,
    String nombre,
    String rucOIdentificacion,
    String email,
    String direccion
) {}