package com.flux.plugin.example.controller;

import com.flux.plugin.example.entity.Person;
import com.flux.plugin.example.repository.PersonRepository;
import io.jettra.core.inject.annotation.Inject;
import io.jettra.rest.annotations.Consumes;
import io.jettra.rest.annotations.DELETE;
import io.jettra.rest.annotations.GET;
import io.jettra.rest.annotations.POST;
import io.jettra.rest.annotations.PUT;
import io.jettra.rest.annotations.Path;
import io.jettra.rest.annotations.PathParam;
import io.jettra.rest.annotations.QueryParam;
import io.jettra.rest.annotations.Produces;
import io.jettra.rest.annotations.Secured;
import io.jettra.rest.annotations.accreditation.RolesAllowed;
import io.jettra.rest.core.Response;
import io.jettra.server.discoverer.Discovered;
import io.jettra.server.openapi.annotations.OpenApi;
import io.jettra.server.openapi.annotations.Operation;
import jcf.systemRole;
import java.util.List;

@Secured
@Path("/plugin/example/person")
@RolesAllowed({systemRole.ADMIN})
@Discovered
@OpenApi(title = "Person", version = "v1.0", description = "API for Person management")
public class PersonController {

    @Inject
    private PersonRepository personRepository;

    @GET
    @Path("/")
    @Produces("application/json")
    @Operation(summary = "findAll", description = "Returns all records")
    public List<Person> findAll(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {
        if (page != null && size != null) {
            return personRepository.findAll(page, size);
        }
        return personRepository.findAll();
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Operation(summary = "save", description = "Saves a new Person")
    public Response save(Person record) {
        personRepository.save(record);
        return Response.ok("{\"message\": \"Saved successfully\"}").build();
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    @Operation(summary = "update", description = "Updates an existing Person")
    public Response update(Person record) {
        personRepository.save(record);
        return Response.ok("{\"message\": \"Updated successfully\"}").build();
    }

    @DELETE
    @Path("/{id}")
    @Produces("application/json")
    @Operation(summary = "delete", description = "Deletes a Person by id")
    public Response delete(@PathParam("id") String id) {
        personRepository.delete(id);
        return Response.ok("{\"message\": \"Deleted successfully\"}").build();
    }

    @GET
    @Path("/name/{name}")
    @Produces("application/json")
    @Operation(summary = "findByName", description = "Finds records by name")
    public List<Person> findByName(@PathParam("name") String name) {
        return personRepository.findByName(name);
    }

    @GET
    @Path("/email/{email}")
    @Produces("application/json")
    @Operation(summary = "findByEmail", description = "Finds records by email")
    public List<Person> findByEmail(@PathParam("email") String email) {
        return personRepository.findByEmail(email);
    }

    @GET
    @Path("/age/{age}")
    @Produces("application/json")
    @Operation(summary = "findByAge", description = "Finds records by age")
    public List<Person> findByAge(@PathParam("age") Integer age) {
        return personRepository.findByAge(age);
    }
}
