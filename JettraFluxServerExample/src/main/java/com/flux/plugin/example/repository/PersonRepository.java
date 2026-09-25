package com.flux.plugin.example.repository;

import com.flux.plugin.example.entity.Person;
import java.util.List;
import java.util.Optional;

public interface PersonRepository {
    List<Person> findAll();
    List<Person> findAll(int page, int size);
    void save(Person record);
    void delete(String id);
    Optional<Person> findById(String id);
    List<Person> findByName(String name);
    List<Person> findByEmail(String email);
    List<Person> findByAge(Integer age);
}
