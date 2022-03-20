package org.jboss.quickstarts.wfk.flight;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jboss.quickstarts.wfk.booking.Booking;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Flight.class)
public abstract class Flight_ {

	public static volatile SingularAttribute<Flight, String> startPlace;
	public static volatile SingularAttribute<Flight, String> seatsNumber;
	public static volatile SingularAttribute<Flight, String> flightNo;
	public static volatile SingularAttribute<Flight, Date> flightDate;
	public static volatile SingularAttribute<Flight, String> destination;
	public static volatile SingularAttribute<Flight, Long> id;
	public static volatile SetAttribute<Flight, Booking> bookings;

}

