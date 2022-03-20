package org.jboss.quickstarts.wfk.travelagent;

import java.io.Serializable;
import java.util.Date;

public class TaxiBookingDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private Taxi taxi;
    private TaxiCustomer taxiCustomer;
    private Date bookingDate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Taxi getTaxi() {
		return taxi;
	}
	public void setTaxi(Taxi taxi) {
		this.taxi = taxi;
	}
	public TaxiCustomer getTaxiCustomer() {
		return taxiCustomer;
	}
	public void setTaxiCustomer(TaxiCustomer taxiCustomer) {
		this.taxiCustomer = taxiCustomer;
	}
	public Date getBookingDate() {
		return bookingDate;
	}
	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}
    
    

}
