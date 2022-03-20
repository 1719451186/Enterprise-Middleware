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
package org.jboss.quickstarts.wfk.flight;

import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.quickstarts.wfk.booking.Booking;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <p>This is a the Domain object. The Flight class represents how flight resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a flight are retrieved from the database (with @NamedQueries), and acceptable values
 * for Flight fields (with @NotNull, @Pattern etc...)<p/>
 *
 * @author Joshua Wilson
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Flight.FIND_ALL, query = "SELECT f FROM Flight f ORDER BY f.flightNo ASC, f.seatsNumber ASC"),
        @NamedQuery(name = Flight.FIND_BY_FLIGHT, query = "SELECT c FROM Flight c WHERE c.flightNo = :flightNo")
})
@XmlRootElement
@Table(name = "flight", uniqueConstraints = @UniqueConstraint(columnNames = "flightNo"))
public class Flight implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Flight.findAll";
    public static final String FIND_BY_FLIGHT = "Flight.findByFlightNo";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials")
    @Column(name = "start_place")
    private String startPlace;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials")
    @Column(name = "destination")
    private String destination;
    
    @NotNull
    @Size(min = 1, max = 25)
    @ApiModelProperty(example = "05B")
    @Column(name = "seats_number")
    private String seatsNumber;

    @NotNull
    @NotEmpty
    @ApiModelProperty(example = "SC8600")
    @Pattern(regexp = "[A-Z]{2}\\d{4}$" , message =  "The flight No must be in two capital letters plus four digits. For example, 'SC8600'.")
    @Column(name = "flightNo")
    private String flightNo;


    @NotNull
    @Future(message = "Flightdates can not be in the past. Please choose one from the future")
    @Column(name = "flight_date")
    private Date flightDate;
    
    @JsonIgnore
    @OneToMany(cascade = {CascadeType.MERGE,CascadeType.REMOVE}, fetch = FetchType.EAGER,mappedBy = "flight")
    private Set<Booking> bookings = new HashSet<Booking>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public String getSeatsNumber() {
        return seatsNumber;
    }

    public void setSeatsNumber(String seatsNumber) {
        this.seatsNumber = seatsNumber;
    }

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }
    
    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flight)) return false;
        Flight flight = (Flight) o;
        if (!flightNo.equals(flight.flightNo)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(flightNo);
    }
}
