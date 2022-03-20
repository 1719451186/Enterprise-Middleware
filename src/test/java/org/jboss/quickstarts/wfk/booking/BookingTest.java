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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerRestService;
import org.jboss.quickstarts.wfk.customer.CustomerService;
import org.jboss.quickstarts.wfk.flight.Flight;
import org.jboss.quickstarts.wfk.flight.FlightRestService;
import org.jboss.quickstarts.wfk.flight.FlightService;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Date;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Booking creation functionality
 * (see {@link BookingRestService#createBooking(Booking) createBooking(Booking)}).<p/>
 *
 * @author balunasj
 * @author Joshua Wilson
 * @see BookingRestService
 */
@RunWith(Arquillian.class)
public class BookingTest {

    /**
     * <p>Compiles an Archive using Shrinkwrap, containing those external dependencies necessary to run the tests.</p>
     *
     * <p>Note: This code will be needed at the start of each Arquillian test, but should not need to be edited, except
     * to pass *.class values to .addClasses(...) which are appropriate to the functionality you are trying to test.</p>
     *
     * @return Micro test war to be deployed and executed.
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        // This is currently not well tested. If you run into issues, comment line 67 (the contents of 'resolve') and
        // uncomment 65. This will build our war with all dependencies instead.
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
//                .importRuntimeAndTestDependencies()
                .resolve(
                        "io.swagger:swagger-jaxrs:1.5.16"
        ).withTransitivity().asFile();

        return ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addPackages(true, "org.jboss.quickstarts.wfk")
                .addAsLibraries(libs)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("arquillian-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    FlightRestService flightRestService;

    @Inject
    FlightService flightService;

    @Inject
    CustomerRestService customerRestService;

    @Inject
    CustomerService customerService;

    @Inject
    BookingRestService bookingRestService;

    @Inject
    @Named("logger") Logger log;

    //Set millis 498484800000 from 1985-10-10T12:00:00.000Z
    private Date date = new Date(498484800000L);

    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
    	Customer customer0 = createCustomerInstance("Jane Doe", "jane@mailinator.com", "07744754955");
        Flight flight0 = createFlightInstance("SC8888", "36A");
        Booking booking = createBookingInstance(customer0,flight0, date);
        Response response = bookingRestService.createBooking(booking);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New booking was persisted and returned status " + response.getStatus());
    }

    @Test
    @InSequence(2)
    public void testInvalidRegister() {
    	Customer customer1 = createCustomerInstance("", "", "");
    	Flight flight1 = createFlightInstance("", "");
        Booking booking = createBookingInstance(customer1,flight1, date);

        try {
            bookingRestService.createBooking(booking);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
            assertEquals("Unexpected response body", 4, e.getReasons().size());
            log.info("Invalid booking register attempt failed with return code " + e.getStatus());
        }

    }

    @Test
    @InSequence(3)
    public void testDuplicateEmail() throws Exception {
        // Register an initial user
    	Customer customer0 = createCustomerInstance("Jane Doe", "jane@mailinator.com", "07934409060");
        Flight flight0 = createFlightInstance("SC8888", "36A");
        Booking booking = createBookingInstance(customer0,flight0, date);
        bookingRestService.createBooking(booking);
        
        // Register a different user with the same email        
        Customer customer1 = createCustomerInstance("Jane Doe", "jane@mailinator.com", "07744754955");
        Flight flight1 = createFlightInstance("SC8888", "36A");
        Booking anotherBooking = createBookingInstance(customer1,flight1, date);

        try {
            bookingRestService.createBooking(anotherBooking);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.CONFLICT, e.getStatus());
            assertTrue("Unexecpted error. Should be Unique email violation", e.getCause() instanceof UniqueBookingException);
            assertEquals("Unexpected response body", 1, e.getReasons().size());
            log.info("Duplicate booking register attempt failed with return code " + e.getStatus());
        }

    }

    /**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.booking.Booking Booking} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param firstName The first name of the Booking being created
     * @param lastName  The last name of the Booking being created
     * @param email     The email address of the Booking being created
     * @param phone     The phone number of the Booking being created
     * @param birthDate The birth date of the Booking being created
     * @return The Booking object create
     */
    private Booking createBookingInstance(Customer customer, Flight flight, Date bookingDate) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setFlight(flight);
        booking.setBookingDate(bookingDate);
        return booking;
    }
    
    private Customer createCustomerInstance(String name , String email , String phoneNumber) {
    	Customer customer = new Customer();
    	customer.setName(name);
    	customer.setEmail(email);
    	customer.setPhoneNumber(phoneNumber);
    	return customer;
    	
    }
    
    private Flight createFlightInstance(String flightNo , String seatsNumber) {
    	Flight flight = new Flight();
    	flight.setFlightNo(flightNo);
    	flight.setSeatsNumber(seatsNumber);    	
    	return flight;
    	
    }
}
    
