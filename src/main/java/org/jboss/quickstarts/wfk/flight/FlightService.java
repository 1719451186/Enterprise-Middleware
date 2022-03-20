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

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
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
 * @see FlightValidator
 * @see FlightRepository
 */
//The @Dependent is the default scope is listed here so that you know what scope is being used.
@Dependent
public class FlightService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private FlightValidator validator;

    @Inject
    private FlightRepository crud;

    private ResteasyClient client;

    /**
     * <p>Create a new client which will be used for our outgoing REST client communication</p>
     */
    public FlightService() {
        // Create client service instance to make REST requests to upstream service
        client = new ResteasyClientBuilder().build();
    }

    /**
     * <p>Returns a List of all persisted {@link Flight} objects, sorted alphabetically by last name.<p/>
     *
     * @return List of Flight objects
     */
    public List<Flight> findAllFlights() {
        return crud.findAllFlights();
    }

    /**
     * <p>Returns a single Flight object, specified by a Long id.<p/>
     *
     * @param id The id field of the Flight to be returned
     * @return The Flight with the specified id
     */
    public Flight findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Flight object, specified by a String flightNo.</p>
     *
     * <p>If there is more than one Flight with the specified flightNo, only the first encountered will be returned.<p/>
     *
     * @param flightNo The flightNo field of the Flight to be returned
     * @return The first Flight with the specified flightNo
     */
    public Flight findByFlightNo(String flightNo) {
        return crud.findByFlightNo(flightNo);
    }

    /**
     * <p>Writes the provided Flight object to the application database.<p/>
     *
     * <p>Validates the data in the provided Flight object using a {@link FlightValidator} object.<p/>
     *
     * @param flight The Flight object to be written to the database using a {@link FlightRepository} object
     * @return The Flight object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Flight create(Flight flight) throws ConstraintViolationException, ValidationException, Exception {
        log.info("FlightService.create() - Creating " + flight.getFlightNo() + " " + flight.getSeatsNumber());        
        // Check to make sure the data fits with the parameters in the Flight model and passes validation.
        validator.validateFlight(flight);
        // Write the flight to the database.
        return crud.create(flight);
    }

    /**
     * <p>Updates an existing Flight object in the application database with the provided Flight object.<p/>
     *
     * <p>Validates the data in the provided Flight object using a FlightValidator object.<p/>
     *
     * @param flight The Flight object to be passed as an update to the application database
     * @return The Flight object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Flight update(Flight flight) throws ConstraintViolationException, ValidationException, Exception {
        log.info("FlightService.update() - Updating " + flight.getFlightNo() + " " + flight.getSeatsNumber());
        
        // Check to make sure the data fits with the parameters in the Flight model and passes validation.
        validator.validateFlight(flight);
        // Either update the flight or add it if it can't be found.
        return crud.update(flight);
    }

    /**
     * <p>Deletes the provided Flight object from the application database if found there.<p/>
     *
     * @param flight The Flight object to be removed from the application database
     * @return The Flight object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    public Flight delete(Flight flight) throws Exception {
        log.info("delete() - Deleting " + flight.toString());
        Flight deletedFlight = null;
        if (flight.getId() != null) {
            deletedFlight = crud.delete(flight);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedFlight;
    }
}
