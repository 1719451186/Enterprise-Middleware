package org.jboss.quickstarts.wfk.travelagent;

public enum BookingTypeEnum {
	
	TAXI_BOOKING(null), HOTEL_BOOKING(null);
	
	private String bookingUrl;
	
	private BookingTypeEnum(String bookingUrl) {
		this.bookingUrl = bookingUrl;
	}
	
	public String getBookingUrl() {
		return bookingUrl;
	}
	
	public void setBookingUrl(String bookingUrl) {
        this.bookingUrl = bookingUrl;
    }

}
