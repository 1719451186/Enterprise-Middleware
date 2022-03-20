
package org.jboss.quickstarts.wfk.travelagent;

import org.jboss.quickstarts.wfk.booking.Booking;

import java.util.HashSet;
import java.util.Set;


public class Hotel{
	    private Long id;
	    private String name;
	    private String phoneNumber;
	    private String postCode;

	    private Set<Booking> bookings = new HashSet<>();

	    public Set<Booking> getBookings() {
	        return bookings;
	    }

	    public void setBookings(Set<Booking> bookings) {
	        this.bookings = bookings;
	    }

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public String getPhoneNumber() {
	        return phoneNumber;
	    }

	    public void setPhoneNumber(String phoneNumber) {
	        this.phoneNumber = phoneNumber;
	    }
	    
	    public String getPostCode() {
	        return postCode;
	    }

	    public void setPostCode(String postCode) {
	        this.postCode = postCode;
	    }
	    
	    
	    public boolean equals(Object o) {
	        if (this == o) {
	            return true;
	        }
	        if (!(o instanceof Hotel)) {
	            return false;
	        }
	        Hotel hotel = (Hotel) o;
	        return phoneNumber.equals(hotel.phoneNumber);
	    }

	    public int hashCode() {
	        int result = 31;
	        result = 31 * result + phoneNumber.hashCode();
	        return result;
	    }

	    public String toString() {
	        return "Hotel name: " + name
	                + " Hotel phone number: " + phoneNumber
	                + " Post code: " + postCode;
	    }

}
