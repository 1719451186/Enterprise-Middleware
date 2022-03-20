package org.jboss.quickstarts.wfk.guestBooking;

import io.swagger.annotations.*;
import org.jboss.quickstarts.wfk.area.InvalidAreaCodeException;
import org.jboss.quickstarts.wfk.booking.Booking;
import org.jboss.quickstarts.wfk.booking.BookingService;
import org.jboss.quickstarts.wfk.booking.UniqueBookingException;
import org.jboss.quickstarts.wfk.contact.UniqueEmailException;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerService;
import org.jboss.quickstarts.wfk.flight.Flight;
import org.jboss.quickstarts.wfk.flight.FlightService;
import org.jboss.quickstarts.wfk.util.RestServiceException;

import com.google.common.base.Strings;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;



@TransactionManagement(value = javax.ejb.TransactionManagementType.BEAN)
@Path("/guestBooking")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/guestBooking", description = "Operations about guestBooking")
@Stateless

public class GuestBookingRestService {
	
	
	
	@Inject
	private UserTransaction userTransaction;
	
    @Inject
    private @Named("logger") Logger log;

    @Inject
    private CustomerService customerService;

    @Inject
    private BookingService bookingService; 
    
    @Inject
    private FlightService flightService;
    
    
    @POST
    @ApiOperation(value = "Bean-Managed Transaction")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Booking created successfully."),
            @ApiResponse(code = 400, message = "Invalid GuestBooking supplied in request body"),
            @ApiResponse(code = 409, message = "GuestBooking supplied in request body conflicts with an existing Booking"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response createBooking(
            @ApiParam(value = "JSON representation of GuestBooking object", required = true)
            GuestBooking guestBooking) {

    		
    	  if (guestBooking == null) {
              throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
          }


    	  Booking booking = null;
          try {
              userTransaction.begin();
              //Not be able to create a Customer/Flight/Booking record with incomplete or invalid information.
              //If customer's name doesn't exist in the database, then create a new customer
              Customer customer = guestBooking.getCustomer();
              log.info("-----------------guestBooking's Customer---------------"+customer);
              //If can find the customer by id, and find customer equals this customer, set the booking's customer
              //directly.
              Long id = customer.getId();
              Customer createdCustomer = new Customer();
              try {
                  if (id!=null){
                      Customer findOne = customerService.findById(id);
                      log.info("+++++++findOne+++++++"+findOne);
                      createdCustomer.setEmail(customer.getEmail());
                      createdCustomer.setName(customer.getName());
                      createdCustomer.setPhoneNumber(customer.getPhoneNumber());
                  }else{
                      createdCustomer = customer;
                  }
              }catch (NotFoundException ne){
                  log.severe(String.format("NotFoundException-Flight not found : %s", ne.getMessage()));
              }
              log.info("~~~~~~~~~~~~~~~~"+createdCustomer.toString()+"~~~~~~~~~~~~~~~~");
              customerService.create(createdCustomer);

              /*Booking flight & date: A combination of the flight for which a booking
              is made and the date for which it is made should be unique */

              //create a Booking record, with a Customer id, a Flight id & a future date.
              Long flightId = guestBooking.getFlightId();
              //use flightId to get Flight object to validate
              if (Strings.isNullOrEmpty(flightId+"")){
                  throw new IllegalArgumentException("flightId is null or blank");
              }
              Flight flight = null;
              try {
                  flight = flightService.findById(guestBooking.getFlightId());
              }catch (NotFoundException ne){
                  log.severe(String.format("NotFoundException-Flight not found : %s", ne.getMessage()));
              }

              Date bookingDate = guestBooking.getBookingDate();
              assert bookingDate != null;
              assert flight != null;
              booking = new Booking();
              booking.setFlight(flight);
              booking.setCustomer(createdCustomer);
              booking.setBookingDate(bookingDate);
              log.info("BBBBBBBBBBBBB"+booking.toString());
              bookingService.create(booking);

              userTransaction.commit();
          } catch(UniqueEmailException uee){
              uee.printStackTrace();
              Map<String, String> responseObj = new HashMap<>();
              responseObj.put("email", "Customer's email is already used, please use a unique email");
              try {
                  userTransaction.rollback();
              } catch (SystemException ex) {
                  ex.printStackTrace();
              }
              throw new RestServiceException("Conflict", responseObj, Response.Status.CONFLICT, uee);
          } catch (UniqueBookingException ube){
              ube.printStackTrace();
              Map<String, String> responseObj = new HashMap<>();
              responseObj.put("booking", "The combination of the Flight and date of the booking is already existed, please use a unique combination");
              try {
                  userTransaction.rollback();
              } catch (SystemException ex) {
                  ex.printStackTrace();
              }
              throw new RestServiceException("Conflict", responseObj, Response.Status.CONFLICT, ube);
          }catch (ConstraintViolationException ce){
              ce.printStackTrace();
              Map<String, String> responseObj = new HashMap<>();

              for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                  responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
              }
              try {
                  userTransaction.rollback();
              } catch (SystemException ex) {
                  ex.printStackTrace();
              }
              throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
          }catch (ValidationException ve){
              ve.printStackTrace();
              Map<String, String> responseObj = new HashMap<>();
              responseObj.put("validation", "validation failed");
              try {
                  userTransaction.rollback();
              } catch (SystemException ex) {
                  ex.printStackTrace();
              }
              throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ve);
          } catch(Exception e) {
              e.printStackTrace();
              try {
                  userTransaction.rollback();
              } catch (SystemException ex) {
                  ex.printStackTrace();
              }
              throw new RestServiceException(e);
          }
          return Response.status(Response.Status.CREATED).entity(booking).build();
          //return Response.ok(booking).build();
      }


}
