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
package org.jboss.quickstarts.wfk.customer;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.quickstarts.wfk.booking.Booking;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <p>This is a the Domain object. The Customer class represents how customer resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a customers are retrieved from the database (with @NamedQueries), and acceptable values
 * for Customer fields (with @NotNull, @Pattern etc...)<p/>
 *
 * @author Joshua Wilson
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Customer.FIND_ALL, query = "SELECT c FROM Customer c ORDER BY c.name ASC"),
        @NamedQuery(name = Customer.FIND_BY_EMAIL, query = "SELECT c FROM Customer c WHERE c.email = :email")
})
@XmlRootElement
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Customer implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Customer.findAll";
    public static final String FIND_BY_EMAIL = "Customer.findByEmail";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^([A-Za-z]+\\s?)*[A-Za-z]$", message = "Please enter a name, use space to divide first and last name.")
    @Column(name = "name")
    private String name;

    @NotNull
    @NotEmpty
    @Email(message = "The email address must be in the format of name@domain.com")
    @Column(name = "email")
    private String email;

    @NotNull
    @Pattern(regexp = "^0\\d{10}$", message = "Please use a sequence of numbers start with 0 and 11 digits in length.")
    @Column(name = "phone_number")
    private String phoneNumber;

    
    @JsonIgnore
    @OneToMany(cascade = {CascadeType.MERGE,CascadeType.REMOVE}, mappedBy = "customer")
    private Set<Booking> bookings = new HashSet<Booking>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        if (!email.equals(customer.email)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}
