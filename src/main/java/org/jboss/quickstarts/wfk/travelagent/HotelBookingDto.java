package org.jboss.quickstarts.wfk.travelagent;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class HotelBookingDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
    private HotelCustomer hotelCustomer;
    private Hotel hotel;
    private Date bookingDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HotelCustomer getHotelCustomer() {
        return hotelCustomer;
    }

    public void setHotelCustomer(HotelCustomer hotelCustomer) {
        this.hotelCustomer = hotelCustomer;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
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
        HotelBookingDto that = (HotelBookingDto) o;
        return getId().equals(that.getId()) &&
                getHotelCustomer().equals(that.getHotelCustomer()) &&
                getHotel().equals(that.getHotel()) &&
                getBookingDate().equals(that.getBookingDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getHotelCustomer(), getHotel(), getBookingDate());
    }

    @Override
    public String toString() {
        return "HotelBooking{" +
                "id=" + id +
                ", hotelCustomer=" + hotelCustomer +
                ", hotel=" + hotel +
                ", bookingDate=" + bookingDate +
                '}';
    }

}
