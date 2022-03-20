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
public interface TaxiBookingService {
	
	@GET
    @Path("/taxis/{id:[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    Taxi getTaxiById(@PathParam("id") Long id);

    @POST
    @Path("/bookings")
    @Consumes(MediaType.APPLICATION_JSON)
    TaxiBookingDto createTaxiBooking(TaxiBookingDto flightBooking);

    @DELETE
    @Path("/bookings/{id:[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    void deleteTaxiBooking(@PathParam("id") Long id);

}
