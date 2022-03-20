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

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class provides methods to check Flight objects against arbitrary requirements.</p>
 *
 * @author Joshua Wilson
 * @see Flight
 * @see FlightRepository
 * @see javax.validation.Validator
 */
public class FlightValidator {
    @Inject
    private Validator validator;

    @Inject
    private FlightRepository crud;

    /**
     * <p>Validates the given Flight object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing flight with the same flightNo is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     *
     * @param flight The Flight object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If flight with the same flightNo already exists
     */
    public void validateFlight(Flight flight) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Flight>> violations = validator.validate(flight);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the flightNo address
        if (flightNoAlreadyExists(flight.getFlightNo(), flight.getId())) {
            throw new UniqueFlightNoException("Unique FlightNo Violation");
        }
    }

    /**
     * <p>Checks if a flight with the same flightNo address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "flightNo")" constraint from the Flight class.</p>
     *
     * <p>Since Update will being using an flightNo that is already in the database we need to make sure that it is the flightNo
     * from the record being updated.</p>
     *
     * @param flightNo The flightNo to check is unique
     * @param id The user id to check the flightNo against if it was found
     * @return boolean which represents whether the flightNo was found, and if so if it belongs to the user with id
     */
    private boolean flightNoAlreadyExists(String flightNo, Long id) {
        Flight flight = null;
        Flight flightWithID = null;
        try {
            flight = crud.findByFlightNo(flightNo);
        } catch (NoResultException e) {
            // ignore
        }

        if (flight != null && id != null) {
            try {
                flightWithID = crud.findById(id);
                if (flightWithID != null && flightWithID.getFlightNo().equals(flightNo)) {
                    flight = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return flight != null;
    }
}
