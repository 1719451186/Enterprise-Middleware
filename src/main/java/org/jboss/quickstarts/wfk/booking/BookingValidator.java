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

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.jboss.quickstarts.wfk.flight.Flight;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>This class provides methods to check Booking objects against arbitrary requirements.</p>
 *
 * @author Joshua Wilson
 * @see Booking
 * @see BookingRepository
 * @see javax.validation.Validator
 */
public class BookingValidator {
    @Inject
    private Validator validator;

    @Inject
    private BookingRepository crud;
    
    @Inject
    private @Named("logger")
    Logger log;


    /**
     * <p>Validates the given Booking object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing booking with the same booking is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     *
     * @param booking The Booking object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If booking with the same booking already exists
     */
    public void validateBooking(Booking booking) throws ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        
        log.info(">VALIDATING> " + booking.toString());
        if (!violations.isEmpty()) {
        	Iterator<ConstraintViolation<Booking>> iterator = violations.iterator();
        	while (iterator.hasNext()){
                ConstraintViolation<Booking> next = iterator.next();
                log.info("> "+next.getMessage());
            }
        	log.info(">violations.size = "+violations.iterator());
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the booking address
        if (bookingAlreadyExists(booking.getFlight(), booking.getBookingDate())) {
            throw new UniqueBookingException("Unique Email Violation");
        }
    }

    /**
     * <p>Checks if a booking with the same booking address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "booking")" constraint from the Booking class.</p>
     *
     * <p>Since Update will being using an booking that is already in the database we need to make sure that it is the booking
     * from the record being updated.</p>
     *
     * @param booking The booking to check is unique
     * @param id The user id to check the booking against if it was found
     * @return boolean which represents whether the booking was found, and if so if it belongs to the user with id
     */
    private boolean bookingAlreadyExists(Flight flight, Date date){
        log.info(">: "+flight.toString());
        boolean boo = false;

        List<Booking> bookings = crud.findByFlightId(flight.getId());
        System.out.println(">:"+bookings);

        if (bookings==null){
            return boo;
        }
        if(bookings.size()>0){
            //check date
            for (Booking bk: bookings) {
                Date d1 = bk.getBookingDate();
                Calendar ca1 = Calendar.getInstance();
                ca1.setTime(d1);
                ca1.set(Calendar.HOUR_OF_DAY,0);
                ca1.set(Calendar.MINUTE,0);
                ca1.set(Calendar.SECOND,0);
                ca1.set(Calendar.MILLISECOND,0);
                Calendar ca2 = Calendar.getInstance();
                ca2.setTime(date);
                ca2.set(Calendar.HOUR_OF_DAY,0);
                ca2.set(Calendar.MINUTE,0);
                ca2.set(Calendar.SECOND,0);
                ca2.set(Calendar.MILLISECOND,0);
                if (ca1.getTime().compareTo(ca2.getTime()) == 0){
                    boo = true;
                    break;
                }
            }
        }
        return boo;
    }
}
