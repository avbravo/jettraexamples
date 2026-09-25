package com.flux.plugin.example.restclient;

import com.flux.plugin.example.entity.Person;
import io.jettra.rest.annotations.DELETE;
import io.jettra.rest.annotations.GET;
import io.jettra.rest.annotations.POST;
import io.jettra.rest.annotations.PUT;
import io.jettra.rest.annotations.Path;
import io.jettra.rest.annotations.PathParam;
import io.jettra.rest.client.RestClient;
import java.util.List;
import io.jettra.rules.validations.Min;
import io.jettra.rules.validations.NotNull;

@RestClient(baseUri = "/api/example/persons")
public interface PersonRestClient {

    @GET
    List<Person> findAll();

    @POST
    void save(Person person);

    @PUT
    void update(Person person);

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id);

    @GET
    @Path("/name/{name}")
    List<Person> findByName(@PathParam("name") String name);

    @GET
    @Path("/email/{email}")
    List<Person> findByEmail(@PathParam("email") String email);

    @GET
    @Path("/age/{age}")
    List<Person> findByAge(@PathParam("age") Integer age);
}
