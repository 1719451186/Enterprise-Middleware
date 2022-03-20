--
-- JBoss, Home of Professional Open Source
-- Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
-- contributors by the @authors tag. See the copyright.txt in the
-- distribution for a full listing of individual contributors.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- http://www.apache.org/licenses/LICENSE-2.0
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- You can use this file to load seed data into the database using SQL statements
-- Since the database doesn't know to increase the Sequence to match what is manually loaded here it starts at 1 and tries
--  to enter a record with the same PK and create an error.  If we use a high we don't interfere with the sequencing (at least until later).
-- NOTE: this file should be removed for production systems. 

insert into Contact (id, first_name, last_name, email, phone_number, birth_date) values (2, 'John', 'Smith', 'john.smith@mailinator.com', '(212) 555-1212', '1963-06-03');
insert into Contact (id, first_name, last_name, email, phone_number, birth_date) values (1, 'Davey', 'Jones', 'davey.jones@locker.com', '(212) 555-3333', '1996-08-07');
insert into Customer (id, name, email, phone_number) values (1, 'Wu Boyuan', 'wuboyuan@123.com', '07934409060');
insert into Customer (id, name, email, phone_number) values (2, 'Song Bowen', 'songbowen@123.com', '07934401230');
insert into Flight (id, start_place, destination, seats_number, flightNo, flight_date) values (1, 'Beijing', 'Shanghai', '05B', 'SC8600','2022-1-12');
insert into Flight (id, start_place, destination, seats_number, flightNo, flight_date) values (2, 'Newcastle', 'London', '23B', 'SC8611','2022-12-12');
insert into Booking (id, customer_id, flight_id, bookingDate) values (1, 1, 1, '2022-12-12');



