package com.flux.plugin.example.model;

import com.flux.plugin.example.entity.Animal;
import io.jettra.flux.annotations.JettraViewModel;
import io.jettra.core.flux.FluxModelToRecordConversor;
import io.jettra.flux.annotations.PropertiesInRecord;
import io.jettra.flux.annotations.PropertiesLabel;
import io.jettra.flux.annotations.ViewSelectOne;
import io.jettra.flux.annotations.ViewSelectMany;
import io.jettra.flux.annotations.TableColumnField;
import io.jettra.rules.validations.NotNull;
import io.jettra.rules.validations.NotNull;
import java.util.UUID;

@JettraViewModel
@FluxModelToRecordConversor(goal = Animal.class)
public class AnimalModel {

    @PropertiesInRecord
    @PropertiesLabel(value = "animal.id", label = "Id")
    private UUID id;

    @PropertiesInRecord
    @PropertiesLabel(value = "animal.name", label = "Name")
    @NotNull
    private String name;

    public AnimalModel() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
