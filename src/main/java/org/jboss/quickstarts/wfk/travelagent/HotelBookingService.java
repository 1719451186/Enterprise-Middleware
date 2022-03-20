package org.jboss.quickstarts.wfk.travelagent;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public interface HotelBookingService {
	@GET
    @Path("/Hotel/{id:[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    Hotel getHotelById(@PathParam("id") Long id);

    @POST
    @Path("/Booking")
    @Consumes(MediaType.APPLICATION_JSON)
    HotelBookingDto createHotelBooking(HotelBookingDto hotelBookingDto);

    @DELETE
    @Path("/Booking/{id:[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    void deleteHotelBooking(@PathParam("id") Long id);
}
