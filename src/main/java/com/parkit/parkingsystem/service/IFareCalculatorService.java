package com.parkit.parkingsystem.service;

import java.util.List;

import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.discount.IDiscount;
import com.parkit.parkingsystem.service.discount.IDiscountCalculatorService;

public interface IFareCalculatorService {
	
	/**
	 * Calculate the ticket price.
	 * @author Mathias Lauer
	 * 26 déc. 2020
	 */
	public void calculateFare(Ticket ticket);
	
	/**
	 * @return Instance of IDiscountCalculatorService used for calculate discounts
	 * @author Mathias Lauer
	 * 7 janv. 2021
	 */
	public IDiscountCalculatorService getDiscountCalculatorService();

}
