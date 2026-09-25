package com.flux.plugin.example.repository;

import com.flux.plugin.example.entity.Person;
import io.jettra.scoped.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class PersonRepositoryImpl implements PersonRepository {

    private static final List<Person> db = new ArrayList<>();

    @Override
    public List<Person> findAll() {
        return new ArrayList<>(db);
    }

    @Override
    public List<Person> findAll(int page, int size) {
        int from = (page - 1) * size;
        if (from >= db.size()) return new ArrayList<>();
        int to = Math.min(from + size, db.size());
        return new ArrayList<>(db.subList(from, to));
    }

    @Override
    public void save(Person record) {
        delete(record.name());
        db.add(record);
    }

    @Override
    public void delete(String id) {
        db.removeIf(r -> r.name().equals(id));
    }

    @Override
    public Optional<Person> findById(String id) {
        return db.stream().filter(r -> r.name().equals(id)).findFirst();
    }

    @Override
    public List<Person> findByName(String name) {
        return db.stream().filter(r -> r.name().equals(name)).collect(Collectors.toList());
    }

    @Override
    public List<Person> findByEmail(String email) {
        return db.stream().filter(r -> r.email().equals(email)).collect(Collectors.toList());
    }

    @Override
    public List<Person> findByAge(Integer age) {
        return db.stream().filter(r -> r.age().equals(age)).collect(Collectors.toList());
    }
}
