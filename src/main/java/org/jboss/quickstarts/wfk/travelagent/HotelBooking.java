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

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


public class HotelBooking implements Serializable{

	private static final long serialVersionUID = 1L;
	private Long id;
    private Long hotelId;
    private Date bookingDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
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
        HotelBooking that = (HotelBooking) o;
        return getId().equals(that.getId()) &&
                getHotelId().equals(that.getHotelId()) &&
                getBookingDate().equals(that.getBookingDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getHotelId(), getBookingDate());
    }

    @Override
    public String toString() {
        return "HotelBooking{" +
                "id=" + id +
                ", hotelId=" + hotelId +
                ", bookingDate=" + bookingDate +
                '}';
    }

}
