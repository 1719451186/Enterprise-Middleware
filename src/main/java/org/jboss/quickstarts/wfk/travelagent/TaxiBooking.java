package org.jboss.quickstarts.wfk.travelagent;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class TaxiBooking implements Serializable {
	
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long TaxiId;
    private Date bookingDate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTaxiId() {
		return TaxiId;
	}
	public void setTaxiId(Long taxiId) {
		TaxiId = taxiId;
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
        TaxiBooking that = (TaxiBooking) o;
        return getId().equals(that.getId()) &&
                getTaxiId().equals(that.getTaxiId()) &&
                getBookingDate().equals(that.getBookingDate());
    }
	
	@Override
    public int hashCode() {
        return Objects.hash(getId(), getTaxiId(), getBookingDate());
    }
	
	@Override
    public String toString() {
        return "TaxiBooking{" +
                "id=" + id +
                ", taxiId=" + TaxiId +
                ", bookingDate=" + bookingDate +
                '}';
    }

}
