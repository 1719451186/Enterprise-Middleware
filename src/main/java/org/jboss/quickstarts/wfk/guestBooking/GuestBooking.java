package org.jboss.quickstarts.wfk.guestBooking;

import org.jboss.quickstarts.wfk.customer.Customer;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;




@XmlRootElement
public class GuestBooking implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Customer customer;

	private long flightId;
	
	private Date bookingDate;
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public long getFlightId() {
		return flightId;
	}

	public void setFlightId(long flightId) {
		this.flightId = flightId;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuestBooking that = (GuestBooking) o;
        return getCustomer().equals(that.getCustomer()) &&
                getBookingDate().equals(that.getBookingDate());
    }
    

    @Override
    public int hashCode() {
        return Objects.hash(getCustomer(), getFlightId(), getBookingDate());
    }
    
    @Override
    public String toString() {
        return "GuestBooking{" +
                "customer=" + customer +
                ", flightId=" + flightId +
                ", bookingDate=" + bookingDate +
                '}';
    }


}
