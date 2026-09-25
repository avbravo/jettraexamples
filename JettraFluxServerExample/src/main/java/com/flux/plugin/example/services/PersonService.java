package com.flux.plugin.example.services;

import com.flux.plugin.example.entity.Person;
import com.flux.plugin.example.restclient.PersonRestClient;
import io.jettra.core.inject.annotation.Inject;
import java.util.List;
import io.jettra.rules.validations.Min;
import io.jettra.rules.validations.NotNull;

public class PersonService {

    @Inject
    private PersonRestClient client;

    public List<Person> findAll() {
        List<Person> records = client.findAll();
        if (records == null) return List.of();
        return records;
    }

    public void save(Person record) {
        client.save(record);
    }

    public void delete(String id) {
        client.delete(id);
    }

    public List<Person> findByName(String name) {
        List<Person> records = client.findByName(name);
        if (records == null) return List.of();
        return records;
    }

    public List<Person> findByEmail(String email) {
        List<Person> records = client.findByEmail(email);
        if (records == null) return List.of();
        return records;
    }

    public List<Person> findByAge(Integer age) {
        List<Person> records = client.findByAge(age);
        if (records == null) return List.of();
        return records;
    }
}
