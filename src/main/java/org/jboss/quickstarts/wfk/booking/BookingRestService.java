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
package org.jboss.quickstarts.wfk.booking;

import io.swagger.annotations.*;
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
 * <p>This class produces a RESTful service exposing the functionality of {@link BookingService}.</p>
 *
 * <p>The Path annotation defines this as a REST Web Service using JAX-RS.</p>
 *
 * <p>By placing the Consumes and Produces annotations at the class level the methods all default to JSON.  However, they
 * can be overriden by adding the Consumes or Produces annotations to the individual methods.</p>
 *
 * <p>It is Stateless to "inform the container that this RESTful web service should also be treated as an EJB and allow
 * transaction demarcation when accessing the database." - Antonio Goncalves</p>
 *
 * <p>The full path for accessing endpoints defined herein is: api/bookings/*</p>
 * 
 * @author Joshua Wilson
 * @see BookingService
 * @see javax.ws.rs.core.Response
 */
@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/bookings", description = "Operations about bookings")
@Stateless
public class BookingRestService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private BookingService service;

    /**
     * <p>Return all the Bookings.  They are sorted alphabetically by name.</p>
     *
     * <p>The url may optionally include query parameters specifying a Booking's name</p>
     *
     * <p>Examples: <pre>GET api/bookings?firstname=John</pre>, <pre>GET api/bookings?firstname=John&lastname=Smith</pre></p>
     *
     * @return A Response containing a list of Bookings
     */
    @GET
    @ApiOperation(value = "Fetch all Bookings", notes = "Returns a JSON array of all stored Booking objects.")
    public Response retrieveAllBookings() {
        //Create an empty collection to contain the intersection of Bookings to be returned
        List<Booking> bookings = new ArrayList<>();

        try {
            bookings = service.findAllBookings();
        }catch (NoResultException ne){
            ne.printStackTrace();
            log.severe("NoResultException while retrieving all bookings : " + ne.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(bookings).build();
        } 
        return Response.ok(bookings).build();
    }

    /**
     * <p>Search for and return a Booking identified by id.</p>
     *
     * @param id The long parameter value provided as a Booking's id
     * @return A Response containing a single Booking
     */
    @GET
    @Cache
    @Path("customer/{customerId:[0-9]+}")
    @ApiOperation(
            value = "Fetch a Booking by customer_id",
            notes = "Returns a JSON representation of the Booking object with the provided id."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message ="Booking found"),
            @ApiResponse(code = 404, message = "Booking with id not found")
    })
    public Response retrieveBookingByCustomerId(
            @ApiParam(value = "CustomerId of Booking to be fetched", allowableValues = "range[0, infinity]", required = true)
            @PathParam("customerId")
            long customerId) {

    	List<Booking> bookings = service.findByCustomerId(customerId);
        if (bookings == null || bookings.size() == 0) {
            // Verify that the booking exists. Return 404, if not present.
        	throw new RestServiceException("No Booking of this customerId " + customerId + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("retrieveBookingByCustomerId " + customerId + ": found Booking = " + bookings.toString());

        return Response.ok(bookings).build();
    }

    /**
     * <p>Search for and return a Booking identified by id.</p>
     *
     * @param id The long parameter value provided as a Booking's id
     * @return A Response containing a single Booking
     */
    @GET
    @Cache
    @Path("/{bookingId:[0-9]+}")
    @ApiOperation(
            value = "Fetch a Booking by id",
            notes = "Returns a JSON representation of the Booking object with the provided id."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message ="Booking found"),
            @ApiResponse(code = 404, message = "Booking with id not found")
    })
    public Response retrieveBookingById(
            @ApiParam(value = "Id of Booking to be fetched", allowableValues = "range[0, infinity]", required = true)
            @PathParam("bookingid")
            long bookdingId) {

        Booking booking = service.findById(bookdingId);
        if (booking == null) {
            // Verify that the booking exists. Return 404, if not present.
            throw new RestServiceException("No Booking with the id " + bookdingId + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findById " + bookdingId + ": found Booking = " + booking.toString());

        return Response.ok(booking).build();
    }

    /**
     * <p>Creates a new booking from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param booking The Booking object, constructed automatically from JSON input, to be <i>created</i> via
     * {@link BookingService#create(Booking)}
     * @return A Response indicating the outcome of the create operation
     */
    @POST
    @ApiOperation(value = "Add a new Booking to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Booking created successfully."),
            @ApiResponse(code = 400, message = "Invalid Booking supplied in request body"),
            @ApiResponse(code = 409, message = "Booking supplied in request body conflicts with an existing Booking"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response createBooking(
            @ApiParam(value = "JSON representation of Booking object to be added to the database", required = true)
            Booking booking) {

    	if (booking == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
            // Go add the new Booking.
            service.create(booking);

            // Create a "Resource Created" 201 Response and pass the booking back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(booking);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

        } catch (UniqueBookingException ube) {
        	ube.printStackTrace();
        	Map<String, String> responseObj = new HashMap<>();
        	responseObj.put("booking", "The combination of the Flight and date of the booking is already existed, please use a unique combination");
        	throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, ube);
        }catch (FlightNotFoundException e) {
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("flightId", "The flightId does not exist");
                throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
            } catch (CustomerNotFoundException e) {
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("customerId", "The customerId does not exist");
                throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
            } catch (Exception e) {
                log.severe(e.getMessage());
                throw new RestServiceException(e);
            }

        log.info("createBooking completed. Booking = " + booking.toString());
        return builder.build();
    }

    /**
     * <p>Updates the booking with the ID provided in the database. Performs validation, and will return a JAX-RS response
     * with either 200 (ok), or with a map of fields, and related errors.</p>
     *
     * @param booking The Booking object, constructed automatically from JSON input, to be <i>updated</i> via
     * {@link BookingService#update(Booking)}
     * @param id The long parameter value provided as the id of the Booking to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9]+}")
    @ApiOperation(value = "Update a Booking in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Booking updated successfully"),
            @ApiResponse(code = 400, message = "Invalid Booking supplied in request body"),
            @ApiResponse(code = 404, message = "Booking with id not found"),
            @ApiResponse(code = 409, message = "Booking details supplied in request body conflict with another existing Booking"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response updateBooking(
            @ApiParam(value = "Id of Booking to be updated", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
            long id,
            @ApiParam(value = "JSON representation of Booking object to be updated in the database", required = true)
            Booking booking) {

        if (booking == null || booking.getId() == null) {
            throw new RestServiceException("Invalid Booking supplied in request body", Response.Status.BAD_REQUEST);
        }

        if (booking.getId() != null && booking.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("id", "The Booking ID in the request body must match that of the Booking being updated");
            throw new RestServiceException("Booking details supplied in request body conflict with another Booking",
                    responseObj, Response.Status.CONFLICT);
        }

        if (service.findById(booking.getId()) == null) {
            // Verify that the booking exists. Return 404, if not present.
            throw new RestServiceException("No Booking with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder;

        try {
            // Apply the changes the Booking.
            service.update(booking);

            // Create an OK Response and pass the booking back in case it is needed.
            builder = Response.ok(booking);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (UniqueBookingException e) {
            // Handle the unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("booking", "That booking is already used, please use a unique booking");
            throw new RestServiceException("Booking details supplied in request body conflict with another Booking",
                    responseObj, Response.Status.CONFLICT, e);
        } catch (InvalidAreaCodeException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("area_code", "The telephone area code provided is not recognised, please provide another");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }

        log.info("updateBooking completed. Booking = " + booking.toString());
        return builder.build();
    }

    /**
     * <p>Deletes a booking using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 204 NO CONTENT or with a map of fields, and related errors.</p>
     *
     * @param id The Long parameter value provided as the id of the Booking to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @ApiOperation(value = "Delete a Booking from the database")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The booking has been successfully deleted"),
            @ApiResponse(code = 400, message = "Invalid Booking id supplied"),
            @ApiResponse(code = 404, message = "Booking with id not found"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response deleteBooking(
            @ApiParam(value = "Id of Booking to be deleted", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
            long id) {

        Response.ResponseBuilder builder;

        Booking booking = service.findById(id);
        if (booking == null) {
            // Verify that the booking exists. Return 404, if not present.
            throw new RestServiceException("No Booking with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }
        
        log.info("booking is founding."+booking.toString() + "  Start delete.");
        try {
            service.delete(booking);

            builder = Response.noContent();

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        log.info("deleteBooking completed. Booking = " + booking.toString());
        return builder.build();
    }
}
