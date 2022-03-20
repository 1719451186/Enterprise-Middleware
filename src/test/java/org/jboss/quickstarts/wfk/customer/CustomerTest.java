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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Customer creation functionality
 * (see {@link CustomerRestService#createCustomer(Customer) createCustomer(Customer)}).<p/>
 *
 * @author balunasj
 * @author Joshua Wilson
 * @see CustomerRestService
 */
@RunWith(Arquillian.class)
public class CustomerTest {

    /**
     * <p>Compiles an Archive using Shrinkwrap, containing those external dependencies necessary to run the tests.</p>
     *
     * <p>Note: This code will be needed at the start of each Arquillian test, but should not need to be edited, except
     * to pass *.class values to .addClasses(...) which are appropriate to the functionality you are trying to test.</p>
     *
     * @return Micro test war to be deployed and executed.
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        // This is currently not well tested. If you run into issues, comment line 67 (the contents of 'resolve') and
        // uncomment 65. This will build our war with all dependencies instead.
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
//                .importRuntimeAndTestDependencies()
                .resolve(
                        "io.swagger:swagger-jaxrs:1.5.16"
        ).withTransitivity().asFile();

        return ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addPackages(true, "org.jboss.quickstarts.wfk")
                .addAsLibraries(libs)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("arquillian-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    CustomerRestService customerRestService;

    @Inject
    @Named("logger") Logger log;

    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
        Customer customer = createCustomerInstance("Jack Doe", "jack@mailinator.com", "07934409060");
        Response response = customerRestService.createCustomer(customer);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New customer was persisted and returned status " + response.getStatus());
    }

    @Test
    @InSequence(2)
    public void testInvalidRegister() {
        Customer customer = createCustomerInstance("", "", "");

        try {
            customerRestService.createCustomer(customer);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
            assertEquals("Unexpected response body", 4, e.getReasons().size());
            log.info("Invalid customer register attempt failed with return code " + e.getStatus());
        }

    }

    @Test
    @InSequence(3)
    public void testDuplicateEmail() throws Exception {
        // Register an initial user
        Customer customer = createCustomerInstance("Jack Doe", "jack@mailinator.com", "(044)07934409060");
        customerRestService.createCustomer(customer);

        // Register a different user with the same email
        Customer anotherCustomer = createCustomerInstance("Jack Doe", "jack@mailinator.com", "(045)07934409060");

        try {
            customerRestService.createCustomer(anotherCustomer);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.CONFLICT, e.getStatus());
            assertTrue("Unexecpted error. Should be Unique email violation", e.getCause() instanceof UniqueCustomerException);
            assertEquals("Unexpected response body", 1, e.getReasons().size());
            log.info("Duplicate customer register attempt failed with return code " + e.getStatus());
        }

    }

    /**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.customer.Customer Customer} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param firstName The first name of the Customer being created
     * @param lastName  The last name of the Customer being created
     * @param email     The email address of the Customer being created
     * @param phone     The phone number of the Customer being created
     * @param birthDate The birth date of the Customer being created
     * @return The Customer object create
     */
    private Customer createCustomerInstance(String name, String email, String phoneNumber) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        return customer;
    }

}
