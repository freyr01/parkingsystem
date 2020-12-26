package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.model.Ticket;

public interface IFareCalculatorService {
	
	/**
	 * Calculate the ticket price.
	 * @author Mathias Lauer
	 * 26 déc. 2020
	 */
	public void calculateFare(Ticket ticket);

}
