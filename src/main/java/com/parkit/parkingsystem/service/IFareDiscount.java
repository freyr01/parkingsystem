package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.model.Ticket;

public interface IFareDiscount {
	
	/**
	 * Calculate a discount.
	 * return A number to multiply other discount and price to obtain the final price.
	 * @author Mathias Lauer
	 * 27 déc. 2020
	 */
	double calculateDiscount(Ticket ticket);

}
