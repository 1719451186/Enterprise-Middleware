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
package org.jboss.quickstarts.wfk.booking;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.flight.Flight;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * <p>This is a the Domain object. The Booking class represents how booking resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a bookings are retrieved from the database (with @NamedQueries), and acceptable values
 * for Booking fields (with @NotNull, @Pattern etc...)<p/>
 *
 * @author Joshua Wilson
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Booking.FIND_ALL, query = "SELECT b FROM Booking b"),
        @NamedQuery(name = Booking.FIND_BY_CUSTOMER, query = "SELECT b FROM Booking b WHERE b.customer.id = :customerId"),
        @NamedQuery(name = Booking.FIND_BY_FLIGHT, query = "SELECT b FROM Booking b WHERE b.flight.id = :flightId")
})
@XmlRootElement
@Table(name = "booking")
public class Booking implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Booking.findAll";
    public static final String FIND_BY_CUSTOMER = "Booking.findByCustomerId";
    public static final String FIND_BY_FLIGHT = "Booking.findByFlightId";    

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    
    
    @Column(name = "customer_id")
    private long customerId;
    
    @Column(name = "flight_id")
    private long flightId;
    
    
    @NotNull
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "customer")
    private Customer customer;
    
    @NotNull
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "flight")
    private Flight flight;
    
    @NotNull
    @Future(message = "Booking date can not be in the past. Please choose one from the future")
    @Column(name = "booking_date")
    @Temporal(TemporalType.DATE)
    private Date bookingDate;
    

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public long getFlightId() {
		return flightId;
	}

	public void setFlightId(long flightId) {
		this.flightId = flightId;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
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
        Booking booking = (Booking) o;
        if (!getId().equals(booking.getId()) &&
                getCustomer().equals(booking.getCustomer()) &&
                getFlight().equals(booking.getFlight()) &&
                getBookingDate().equals(booking.getBookingDate())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCustomer(), getFlight(), getBookingDate());
    }
    
    @Override
    public String toString() {
        return "Booking{" + "id=" + id + ", customer=" + customer + ", flight=" + flight + ", bookingDate=" + bookingDate + '}';
    }
}
