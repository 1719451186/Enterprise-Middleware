package org.jboss.quickstarts.wfk.travelagent;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.quickstarts.wfk.booking.Booking;
import org.jboss.quickstarts.wfk.booking.BookingService;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

@Dependent
public class TravelAgentService {
	
	@Inject
	private @Named("logger") Logger log;

	@Inject
	private TravelAgentRepository crud;

	@Inject
	private BookingService flightBookingService;

	private ResteasyClient client;

	public TravelAgentService(ResteasyClient client) {
		client = new ResteasyClientBuilder().build();
	}

	public TravelAgent create(TravelAgent travelAgentBooking) throws Exception {

		//Create client service instance to make REST requests to upstream service
		ResteasyWebTarget target = client.target(BookingTypeEnum.TAXI_BOOKING.getBookingUrl());
		TaxiBookingService taxiBookingService = target.proxy(TaxiBookingService.class);

		ResteasyWebTarget target1 = client.target(BookingTypeEnum.HOTEL_BOOKING.getBookingUrl());
		HotelBookingService hotelBookingService = target1.proxy(HotelBookingService.class);

		TaxiBookingDto taxiBookingDto = new TaxiBookingDto();
		TaxiCustomer taxiCustomer = new TaxiCustomer();
		//Taxi taxi = new Taxi();

		HotelBookingDto hotelBookingDto = new HotelBookingDto();
		HotelCustomer hotelCustomer = new HotelCustomer();
		//Hotel hotel = new Hotel();


		//get FlightBooking object
		Booking flightBooking = travelAgentBooking.getFlightBooking();
		Booking booking = flightBookingService.create(flightBooking);

		//get taxiBookingId
		TaxiBooking fb = travelAgentBooking.getTaxiBooking();
		//get taxiId from taxiBooking
		Long taxiId = fb.getTaxiId();

		//find ALREADY exists taxi
		Taxi taxiById = taxiBookingService.getTaxiById(taxiId);

		Customer customer = flightBooking.getCustomer();
		taxiCustomer.setName(customer.getName());
		taxiCustomer.setEmail(customer.getEmail());
		taxiCustomer.setPhoneNumber(customer.getPhoneNumber());

		taxiBookingDto.setTaxiCustomer(taxiCustomer);
		taxiBookingDto.setBookingDate(flightBooking.getBookingDate());
		taxiBookingDto.setTaxi(taxiById);

		//create taxiBooking
		taxiBookingService.createTaxiBooking(taxiBookingDto);

		//get hotelBookingId
		HotelBooking hb = travelAgentBooking.getHotelBooking();
		Long hotelId = hb.getHotelId();

		//find ALREADY existed hotel
		Hotel hotelById = hotelBookingService.getHotelById(hotelId);

		//set parameters to hotelCustomer
		hotelCustomer.setName(customer.getName());
		hotelCustomer.setEmail(customer.getEmail());
		hotelCustomer.setPhoneNumber(customer.getPhoneNumber());

		//set parameters to hotelBooking
		hotelBookingDto.setHotelCustomer(hotelCustomer);
		hotelBookingDto.setBookingDate(flightBooking.getBookingDate());
		hotelBookingDto.setHotel(hotelById);

		//create hotelBooking
		hotelBookingService.createHotelBooking(hotelBookingDto);

		travelAgentBooking.setAgentBookingDate(flightBooking.getBookingDate());
		travelAgentBooking.setFlightBooking(booking);

		// Write the contact to the database.
		return crud.create(travelAgentBooking);
	}

	TravelAgent delete(TravelAgent travelAgentBooking) throws Exception {
		log.info("delete() - Deleting " + travelAgentBooking.toString());


		//Create client service instance to make REST requests to upstream service
		ResteasyWebTarget target = client.target(BookingTypeEnum.TAXI_BOOKING.getBookingUrl());
		TaxiBookingService taxiBookingService = target.proxy(TaxiBookingService.class);

		ResteasyWebTarget target1 = client.target(BookingTypeEnum.HOTEL_BOOKING.getBookingUrl());
		HotelBookingService hotelBookingService = target1.proxy(HotelBookingService.class);

		TravelAgent deletedContact = null;

		if (travelAgentBooking.getId() != null) {
			deletedContact = crud.delete(travelAgentBooking);
			taxiBookingService.deleteTaxiBooking(travelAgentBooking.getTaxiBooking().getTaxiId());
			hotelBookingService.deleteHotelBooking(travelAgentBooking.getHotelBooking().getHotelId());
		} else {
			log.info("delete() - No ID was found so can't Delete.");
		}

		return deletedContact;
	}

	public TravelAgent findById(long id) {
		return crud.findById(id);
	}

	public List<TravelAgent> findAllBookings() {
		return crud.findAllAgentBookings();
	}

}
