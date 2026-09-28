package com.avbravo.facoweb.entity;

import io.jettra.rules.validations.Email;
import io.jettra.rules.validations.Min;
import io.jettra.rules.validations.NotNull;

public record Person(
        @NotNull String name,
        @Email String email,
        @Min(value = 0) Integer age) {
}
