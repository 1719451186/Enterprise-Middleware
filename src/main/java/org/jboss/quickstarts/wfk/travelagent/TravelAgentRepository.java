package org.jboss.quickstarts.wfk.travelagent;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

public class TravelAgentRepository {
	
	@Inject
	private @Named("logger") Logger log;

	@Inject
	private EntityManager em;

	/**
	 * <p>
	 * Returns a List of all persisted {@link Booking} objects, sorted
	 * alphabetically by last name.
	 * </p>
	 *
	 * @return List of Booking objects
	 */
	List<TravelAgent> findAllOrderedById() {
		TypedQuery<TravelAgent> query = em.createNamedQuery(TravelAgent.FIND_ALL,
				TravelAgent.class);
		return query.getResultList();
	}

	/**
	 * <p>
	 * Returns a single Booking object, specified by a Long id.
	 * </p>
	 *
	 * @param id The id field of the Booking to be returned
	 * @return The Booking with the specified id
	 */
	TravelAgent findById(Long id) {
		return em.find(TravelAgent.class, id);
	}

	/**
	 * <p>
	 * Persists the provided TravelAgent object to the application database using the
	 * EntityManager.
	 * </p>
	 *
	 * @param booking The TravelAgent object to be persisted
	 * @return booking The TravelAgent object that has been persisted
	 * @throws ConstraintViolationException, ValidationException, Exception
	 */
	TravelAgent create(TravelAgent booking) throws ConstraintViolationException, ValidationException, Exception {
		log.info("TravelAgentRepository.create() - Creating " + booking.getId());
		// Persist TravelAgent to the database.
		em.persist(booking);

		return booking;
	}

	/**
	 * <p>
	 * Deletes the provided Booking object from the application database if found
	 * there
	 * </p>
	 *
	 * @param booking The Booking object to be removed from the application database
	 * @return The Booking object that has been successfully removed from the application database; or null
	 * @throws Exception
	 */
	TravelAgent delete(TravelAgent booking) throws Exception {
		log.info("BookingRepository.delete() - Deleting " + booking.getId());

		if (booking.getId() != null) {
			em.remove(em.merge(booking));

		} else {
			log.info("BookingRepository.delete() - No ID was found so can't Delete.");
		}

		return booking;
	}

	public List<TravelAgent> findAllAgentBookings() {
		TypedQuery<TravelAgent> query = em.createNamedQuery(TravelAgent.FIND_ALL, TravelAgent.class);
		return query.getResultList();
	}

}
