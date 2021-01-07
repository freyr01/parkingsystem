package com.parkit.parkingsystem.service.discount;

import com.parkit.parkingsystem.model.Ticket;

/**
 * Implementation of this should calculate some discount we want to apply to the fare.
 * @author Mathias Lauer
 * 5 janv. 2021
 */
public interface IDiscountCalculatorService {
	
	/**
	 * Should return the discount factor, multiply this to the price and you obtain the final price.
	 * @param ticket Ticket to use for discount calculation
	 * @return a number to multiply, can be 0. to 1., 0. give free price, 1. don't affect price, >0.0 and <1.0 apply a discount.
	 * @author Mathias Lauer
	 * 5 janv. 2021
	 */
	public double calculateDiscounts(Ticket ticket);
	
	/**
	 * Activate a discount
	 * @param discount to activate
	 * @author Mathias Lauer
	 * 5 janv. 2021
	 */
	public void activateDiscount(IDiscount discount);
	
	/**
	 * Deactivate a discount
	 * @param discount to deactivate
	 * @author Mathias Lauer
	 * 5 janv. 2021
	 */
	public void deactivateDiscount(IDiscount discount);
	
	/**
	 * Check if a discount is activated or not
	 * @param discount class
	 * @return true if it is, false otherwise
	 */
	public boolean isActive(Class<?> discountClass);

}
