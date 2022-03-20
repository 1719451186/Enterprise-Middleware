package org.jboss.quickstarts.wfk.travelagent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import io.swagger.annotations.*;
import javax.ws.rs.core.MediaType;

import org.jboss.quickstarts.wfk.booking.Booking;
import org.jboss.quickstarts.wfk.booking.BookingService;
import org.jboss.quickstarts.wfk.util.RestServiceException;

import io.swagger.annotations.Api;

@Path("/travelAgent")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/travelAgent", description = "Travel agent manage all kinds of bookings")
@Stateless
public class TravelAgentRestService {
	
	@Inject
	private @Named("logger") Logger log;

	@Inject
	private BookingService bookingService;

	@Inject
	private UserTransaction userTransaction;

	@POST
	@ApiOperation(value = "Add a new Booking to the database")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Booking created successfully."),
			@ApiResponse(code = 400, message = "Invalid Booking supplied in request body"),
			@ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request") })
	public Response createBooking(
			@ApiParam(value = "JSON representation of Booking object to be added to the database", required = true)
                    Booking travelAgentBooking) {

		if (travelAgentBooking == null) {
			throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
		}
		try{
			userTransaction.begin();
			bookingService.create(travelAgentBooking);
			userTransaction.rollback();
		} catch(Exception e) {
			e.printStackTrace();
			try {
				bookingService.delete(travelAgentBooking);
				userTransaction.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			throw new RestServiceException(e);
		}

		log.info("created travelAgentBooking = " + travelAgentBooking.toString());
		return Response.status(Response.Status.CREATED).entity(travelAgentBooking).build();

	}

	@DELETE
	@Path("/{id:[0-9]+}")
	@ApiOperation(value = "Delete a Booking from the database")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "The booking has been successfully deleted"),
			@ApiResponse(code = 400, message = "Invalid Booking id supplied"),
			@ApiResponse(code = 404, message = "Booking with id not found"),
			@ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request") })
	public Response deleteBooking(
			@ApiParam(value = "Id of Booking to be deleted", allowableValues = "range[0, infinity]", required = true)
			@PathParam("id") long id) {

		Response.ResponseBuilder builder = null;
		Booking booking = bookingService.findById(id);
		if (booking == null) {
			// Verify booking exists. if not present return 404.
			throw new RestServiceException("No Booking with the id " + id + " was found!", Response.Status.NOT_FOUND);
		}
		log.info("Booking found!!!!!!!!!!!!!!!!!!!!!!!"+booking.toString()); 
		try {
			bookingService.delete(booking);
			builder = Response.noContent();
		} catch (Exception e) {
			// Handle generic exceptions
			throw new RestServiceException(e);
		}
		log.info("delete Booking completed. Booking : " + booking.toString());
		return builder.build();
	}

	/**
	 * <p> BookingRestService retrieve all Bookings</p>
	 *
	 * @return javax.ws.rs.core.Response
	 */
	@GET
	@Path("/all")
	@ApiOperation(value = "Fetch all Bookings", notes = "Returns a JSON array of all stored Booking objects.")
	public Response retrieveAllBookings() {
		List<Booking> allBookings = new ArrayList<>();
		try {
			allBookings = bookingService.findAllBookings();
		}catch (NoResultException ne){
			ne.printStackTrace();
			log.severe("NoResultException while retrieving all Bookings : " + ne.getMessage());
			return Response.status(Response.Status.NOT_FOUND).entity(allBookings).build();
		}
		return Response.ok(allBookings).build();
	}

}
