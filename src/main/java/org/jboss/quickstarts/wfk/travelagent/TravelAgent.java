/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.quickstarts.wfk.travelagent;

import org.jboss.quickstarts.wfk.booking.Booking;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>This is a the Domain object. The TravelAgent class represents how travelAgent resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a travelAgents are retrieved from the database (with @NamedQueries), and acceptable values
 * for TravelAgent fields (with @NotNull, @Pattern etc...)<p/>
 *
 * @author Joshua Wilson
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = TravelAgent.FIND_ALL, query = "SELECT c FROM TravelAgent c ORDER BY c.id ASC")
})
@XmlRootElement
@Table(name = "travelAgent", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class TravelAgent implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "TravelAgent.findAll";

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "taxi_booking_id", nullable = false)
	private TaxiBooking taxiBooking;

	@JoinColumn(name = "hotel_booking_id", nullable = false)
	private HotelBooking hotelBooking;

	@JoinColumn(name = "flight_booking_id", nullable = false)
	private Booking flightBooking;

	@NotNull(message = "Booking date could not be empty")
	@Future(message = "Booking date should be in the future")
	@Column(name = "agent_booking_date")
	private Date agentBookingDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public HotelBooking getHotelBooking() {
		return hotelBooking;
	}

	public void setHotelBooking(HotelBooking hotelBooking) {
		this.hotelBooking = hotelBooking;
	}

	public Booking getFlightBooking() {
		return flightBooking;
	}

	public void setFlightBooking(Booking flightBooking) {
		this.flightBooking = flightBooking;
	}

	public TaxiBooking getTaxiBooking() {
		return taxiBooking;
	}

	public void setTaxiBooking(TaxiBooking taxiBooking) {
		this.taxiBooking = taxiBooking;
	}

	public Date getAgentBookingDate() {
		return agentBookingDate;
	}

	public void setAgentBookingDate(Date agentBookingDate) {
		this.agentBookingDate = agentBookingDate;
	}

}
