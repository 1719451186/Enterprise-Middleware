/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.quickstarts.wfk.flight;

import io.swagger.annotations.*;

import org.apache.commons.lang3.StringUtils;
import org.jboss.quickstarts.wfk.area.InvalidAreaCodeException;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.resteasy.annotations.cache.Cache;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>This class produces a RESTful service exposing the functionality of {@link FlightService}.</p>
 *
 * <p>The Path annotation defines this as a REST Web Service using JAX-RS.</p>
 *
 * <p>By placing the Consumes and Produces annotations at the class level the methods all default to JSON.  However, they
 * can be overriden by adding the Consumes or Produces annotations to the individual methods.</p>
 *
 * <p>It is Stateless to "inform the container that this RESTful web service should also be treated as an EJB and allow
 * transaction demarcation when accessing the database." - Antonio Goncalves</p>
 *
 * <p>The full path for accessing endpoints defined herein is: api/flights/*</p>
 * 
 * @author Joshua Wilson
 * @see FlightService
 * @see javax.ws.rs.core.Response
 */
@Path("/flights")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/flights", description = "Operations about flights")
@Stateless
public class FlightRestService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private FlightService service;

    /**
     * <p>Return all the Flights.  They are sorted alphabetically by name.</p>
     *
     * <p>The url may optionally include query parameters specifying a Flight's name</p>
     *
     * <p>Examples: <pre>GET api/flights?startplace=John</pre>, <pre>GET api/flights?startplace=John&destination=Smith</pre></p>
     *
     * @return A Response containing a list of Flights
     */
    @GET
    @ApiOperation(value = "Fetch all Flights", notes = "Returns a JSON array of all stored Flight objects.")
    public Response retrieveAllFlights(@QueryParam("flightNo") String flightNo) {
        //Create an empty collection to contain the intersection of Flights to be returned
        List<Flight> flights = new ArrayList<>();

        try {
            if(StringUtils.isBlank(flightNo)) {
                flights = service.findAllFlights();
            } else {
                Flight taxi = service.findByFlightNo(flightNo);
                flights.add(taxi);
            }
        }catch (NoResultException ne){
            ne.printStackTrace();
            log.severe("NoResultException while retrieving all taxis : " + ne.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(flights).build();
        }
        return Response.ok(flights).build();
    }

    /**
     * <p>Search for and return a Flight identified by flightNo address.<p/>
     *
     * <p>Path annotation includes very simple regex to differentiate between flightNo addresses and Ids.
     * <strong>DO NOT</strong> attempt to use this regex to validate flightNo addresses.</p>
     *
     *
     * @param flightNo The string parameter value provided as a Flight's flightNo
     * @return A Response containing a single Flight
     */
    @GET
    @Cache
    @Path("/flightNo/{flightNo:.+[%40|@].+}")
    @ApiOperation(
            value = "Fetch a Flight by FlightNo",
            notes = "Returns a JSON representation of the Flight object with the provided flightNo."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message ="Flight found"),
            @ApiResponse(code = 404, message = "Flight with flightNo not found")
    })
    public Response retrieveFlightsByFlightNo(
            @ApiParam(value = "FlightNo of Flight to be fetched", required = true)
            @PathParam("flightNo")
            String flightNo) {

        Flight flight;
        try {
            flight = service.findByFlightNo(flightNo);
        } catch (NoResultException e) {
            // Verify that the flight exists. Return 404, if not present.
            throw new RestServiceException("No Flight with the flightNo " + flightNo + " was found!", Response.Status.NOT_FOUND);
        }
        return Response.ok(flight).build();
    }

    /**
     * <p>Search for and return a Flight identified by id.</p>
     *
     * @param id The long parameter value provided as a Flight's id
     * @return A Response containing a single Flight
     */
    @GET
    @Cache
    @Path("/{id:[0-9]+}")
    @ApiOperation(
            value = "Fetch a Flight by id",
            notes = "Returns a JSON representation of the Flight object with the provided id."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message ="Flight found"),
            @ApiResponse(code = 404, message = "Flight with id not found")
    })
    public Response retrieveFlightById(
            @ApiParam(value = "Id of Flight to be fetched", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
            long id) {

        Flight flight = service.findById(id);
        if (flight == null) {
            // Verify that the flight exists. Return 404, if not present.
            throw new RestServiceException("No Flight with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Flight = " + flight.toString());

        return Response.ok(flight).build();
    }

    /**
     * <p>Creates a new flight from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param flight The Flight object, constructed automatically from JSON input, to be <i>created</i> via
     * {@link FlightService#create(Flight)}
     * @return A Response indicating the outcome of the create operation
     */
    @POST
    @ApiOperation(value = "Add a new Flight to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Flight created successfully."),
            @ApiResponse(code = 400, message = "Invalid Flight supplied in request body"),
            @ApiResponse(code = 409, message = "Flight supplied in request body conflicts with an existing Flight"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response createFlight(
            @ApiParam(value = "JSON representation of Flight object to be added to the database", required = true)
            Flight flight) {


        if (flight == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
            // Go add the new Flight.
            service.create(flight);

            // Create a "Resource Created" 201 Response and pass the flight back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(flight);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

        } catch (UniqueFlightNoException e) {
            // Handle the unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("flightNo", "That flightNo is already used, please use a unique flightNo");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
        } catch (Exception e) {
            // Handle generic exceptions
        	log.severe(e.getMessage());
            throw new RestServiceException(e);
        }

        log.info("createFlight completed. Flight = " + flight.toString());
        return builder.build();
    }

    /**
     * <p>Updates the flight with the ID provided in the database. Performs validation, and will return a JAX-RS response
     * with either 200 (ok), or with a map of fields, and related errors.</p>
     *
     * @param flight The Flight object, constructed automatically from JSON input, to be <i>updated</i> via
     * {@link FlightService#update(Flight)}
     * @param id The long parameter value provided as the id of the Flight to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9]+}")
    @ApiOperation(value = "Update a Flight in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Flight updated successfully"),
            @ApiResponse(code = 400, message = "Invalid Flight supplied in request body"),
            @ApiResponse(code = 404, message = "Flight with id not found"),
            @ApiResponse(code = 409, message = "Flight details supplied in request body conflict with another existing Flight"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response updateFlight(
            @ApiParam(value = "Id of Flight to be updated", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
            long id,
            @ApiParam(value = "JSON representation of Flight object to be updated in the database", required = true)
            Flight flight) {

        if (flight == null || flight.getId() == null) {
            throw new RestServiceException("Invalid Flight supplied in request body", Response.Status.BAD_REQUEST);
        }

        if (flight.getId() != null && flight.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("id", "The Flight ID in the request body must match that of the Flight being updated");
            throw new RestServiceException("Flight details supplied in request body conflict with another Flight",
                    responseObj, Response.Status.CONFLICT);
        }

        if (service.findById(flight.getId()) == null) {
            // Verify that the flight exists. Return 404, if not present.
            throw new RestServiceException("No Flight with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder;

        try {
            // Apply the changes the Flight.
            service.update(flight);

            // Create an OK Response and pass the flight back in case it is needed.
            builder = Response.ok(flight);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (UniqueFlightNoException e) {
            // Handle the unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("flightNo", "That flightNo is already used, please use a unique flightNo");
            throw new RestServiceException("Flight details supplied in request body conflict with another Flight",
                    responseObj, Response.Status.CONFLICT, e);
        } catch (InvalidAreaCodeException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("area_code", "The telephone area code provided is not recognised, please provide another");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }

        log.info("updateFlight completed. Flight = " + flight.toString());
        return builder.build();
    }

    /**
     * <p>Deletes a flight using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 204 NO CONTENT or with a map of fields, and related errors.</p>
     *
     * @param id The Long parameter value provided as the id of the Flight to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @ApiOperation(value = "Delete a Flight from the database")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The flight has been successfully deleted"),
            @ApiResponse(code = 400, message = "Invalid Flight id supplied"),
            @ApiResponse(code = 404, message = "Flight with id not found"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response deleteFlight(
            @ApiParam(value = "Id of Flight to be deleted", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
            long id) {

        Response.ResponseBuilder builder;

        Flight flight = service.findById(id);
        if (flight == null) {
            // Verify that the flight exists. Return 404, if not present.
            throw new RestServiceException("No Flight with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        try {
            service.delete(flight);

            builder = Response.noContent();

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        log.info("deleteFlight completed. Flight = " + flight.toString());
        return builder.build();
    }
}
