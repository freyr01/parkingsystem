package com.parkit.parkingsystem.service.discount;

import java.time.Duration;
import java.time.Instant;

import com.parkit.parkingsystem.model.Ticket;

/**
 * Implementation of IFareDiscount
 * Apply free price for minus than 30mn use
 * @author Mathias Lauer
 * 27 déc. 2020
 */
public class Discount30MnFree implements IDiscount {

	@Override
	/**
	 * 
	 * @param ticket Ticket to apply the discount, some information can also be used to calculate the discount.
	 * @return result Should return 1.0 if there is no discount to apply, 0.0 if the discount give free access
	 * @author Mathias Lauer
	 * 27 déc. 2020
	 */
	public double calculateDiscount(Ticket ticket) {
		double result = 1.0;
		Instant inTime = ticket.getInTime().toInstant();
		Instant outTime = ticket.getOutTime().toInstant();

		Duration duration = Duration.between(inTime, outTime);
		if (duration.toMinutes() <= 30) {
			result = 0.0;
		} else {
			result = 1.0;
		}

		return result;
	}

}
