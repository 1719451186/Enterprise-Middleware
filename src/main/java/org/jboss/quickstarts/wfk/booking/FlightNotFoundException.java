package org.jboss.quickstarts.wfk.booking;

import javax.validation.ValidationException;

public class FlightNotFoundException extends ValidationException{

	private static final long serialVersionUID = 1L;
	
	public FlightNotFoundException(String message) {
        super(message);
    }

    public FlightNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlightNotFoundException(Throwable cause) {
        super(cause);
    }

}
