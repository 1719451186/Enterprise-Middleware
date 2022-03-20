package org.jboss.quickstarts.wfk.travelagent;

import java.util.HashSet;
import java.util.Set;

import org.jboss.quickstarts.wfk.booking.Booking;

public class Taxi {
	
    private Long id;
    private String registraion;
    private String seatsNumber;
    
    private Set<Booking> bookings = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSeatsNumber() {
		return seatsNumber;
	}

	public void setSeatsNumber(String seatsNumber) {
		this.seatsNumber = seatsNumber;
	}

	public String getRegistraion() {
		return registraion;
	}

	public void setRegistraion(String registraion) {
		this.registraion = registraion;
	}

	public Set<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(Set<Booking> bookings) {
		this.bookings = bookings;
	}

}
