package com.flux.plugin.example.converter;

import com.flux.plugin.example.entity.Person;
import com.flux.plugin.example.model.PersonModel;
import io.jettra.scoped.ApplicationScoped;
import io.jettra.rules.validations.Min;
import io.jettra.rules.validations.NotNull;

@ApplicationScoped
public class PersonModelConverter {
  public PersonModel toModel(Person record) {
    if (record == null) {
      return null;
    }
    PersonModel model = new PersonModel();
    model.setName(record.name());
    model.setEmail(record.email());
    model.setAge(record.age());
    return model;
  }

  public Person toRecord(PersonModel model) {
    if (model == null) {
      return null;
    }
    return new Person(
          model.getName(),
          model.getEmail(),
          model.getAge()
        );
  }
}
