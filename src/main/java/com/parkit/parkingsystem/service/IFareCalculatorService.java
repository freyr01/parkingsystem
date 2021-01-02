package com.parkit.parkingsystem.service;

import java.util.List;

import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.discount.IDiscount;

public interface IFareCalculatorService {
	
	/**
	 * Calculate the ticket price.
	 * @author Mathias Lauer
	 * 26 déc. 2020
	 */
	public void calculateFare(Ticket ticket);

}
