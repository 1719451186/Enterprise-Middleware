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

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service assumes the Control responsibility in the ECB pattern.</p>
 *
 * <p>The validation is done here so that it may be used by other Boundary Resources. Other Business Logic would go here
 * as well.</p>
 *
 * <p>There are no access modifiers on the methods, making them 'package' scope.  They should only be accessed by a
 * Boundary / Web Service class with public methods.</p>
 *
 *
 * @author Joshua Wilson
 * @see BookingValidator
 * @see BookingRepository
 */
//The @Dependent is the default scope is listed here so that you know what scope is being used.
@Dependent
public class BookingService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private BookingValidator validator;

    @Inject
    private BookingRepository crud;


    /**
     * <p>Create a new client which will be used for our outgoing REST client communication</p>
     */
    public BookingService() {
        new ResteasyClientBuilder().build();
    }

    /**
     * <p>Returns a List of all persisted {@link Booking} objects, sorted alphabetically by last name.<p/>
     *
     * @return List of Booking objects
     */
    public List<Booking> findAllBookings() {
        return crud.findAllBookings();
    }

    /**
     * <p>Returns a single Booking object, specified by a Long id.<p/>
     *
     * @param id The id field of the Booking to be returned
     * @return The Booking with the specified id
     */
    public Booking findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Booking object, specified by a String email.</p>
     *
     * <p>If there is more than one Booking with the specified email, only the first encountered will be returned.<p/>
     *
     * @param email The email field of the Booking to be returned
     * @return The first Booking with the specified email
     */
    public List<Booking> findByCustomerId(Long customerId) {
        return crud.findByCustomerId(customerId);
    }
    
    
    
    public List<Booking> list(Customer customer){
        return crud.findByName(customer.getName());
        }



    /**
     * <p>Writes the provided Booking object to the application database.<p/>
     *
     * <p>Validates the data in the provided Booking object using a {@link BookingValidator} object.<p/>
     *
     * @param booking The Booking object to be written to the database using a {@link BookingRepository} object
     * @return The Booking object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Booking create(Booking booking){
        log.info("BookingService.create() - Creating ");
        
        // Check to make sure the data fits with the parameters in the Booking model and passes validation.
        validator.validateBooking(booking);

        // Write the booking to the database.
        return crud.create(booking);
    }

    /**
     * <p>Updates an existing Booking object in the application database with the provided Booking object.<p/>
     *
     * <p>Validates the data in the provided Booking object using a BookingValidator object.<p/>
     *
     * @param booking The Booking object to be passed as an update to the application database
     * @return The Booking object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Booking update(Booking booking){
        log.info("BookingService.update() - Updating " + booking.getFlight() + " " + booking.getCustomer() + " " + booking.getBookingDate());
        
        // Check to make sure the data fits with the parameters in the Booking model and passes validation.
        validator.validateBooking(booking);

        // Either update the booking or add it if it can't be found.
        return crud.update(booking);
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there.<p/>
     *
     * @param booking The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    public Booking delete(Booking booking) throws Exception {
        log.info("delete() - Deleting " + booking.toString());

        Booking deletedBooking = null;

        if (booking.getId() != null) {
            deletedBooking = crud.delete(booking);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedBooking;
    }
}
