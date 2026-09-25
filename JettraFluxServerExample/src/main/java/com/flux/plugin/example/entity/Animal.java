/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.flux.plugin.example.entity;

import io.jettra.rules.validations.NotNull;
import java.util.UUID;

/**
 *
 * @author avbravo
 */
public record Animal(@NotNull UUID id, @NotNull String name) {

}
