package com.flux.plugin.example.model;

import io.jettra.flux.annotations.JettraViewModel;
import io.jettra.flux.annotations.PropertiesInRecord;
import io.jettra.flux.annotations.PropertiesLabel;
import io.jettra.rules.validations.Email;
import io.jettra.rules.validations.Min;
import io.jettra.rules.validations.NotNull;

@JettraViewModel
public class PersonModel {

    @PropertiesInRecord
    @PropertiesLabel(value = "person.name", label = "Name")
    @NotNull
    private String name;

    @PropertiesInRecord
    @PropertiesLabel(value = "person.email", label = "Email")
    @NotNull
    @Email
    private String email;

    @PropertiesInRecord
    @PropertiesLabel(value = "person.age", label = "Age")
    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer age;

    public PersonModel() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

}
