package com.parkit.parkingsystem.service.discount;

import java.util.ArrayList;
import java.util.List;

import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

/**
 * This class handle the discount calculation logic
 * @author Mathias Lauer
 * 3 janv. 2021
 */
public class DiscountCalculatorService {
	
	private List<IDiscount> discounts;
	
	/**
	 * Constructor
	 * @param p_discounts A list of discounts to calculate
	 * @author Mathias Lauer
	 * 3 janv. 2021
	 */
	public DiscountCalculatorService()
	{
		discounts = new ArrayList<IDiscount>();
	}
	
	
	/**
	 * Calculate all discounts to apply
	 * @return totalDiscount A number to multiply with price to obtain the final price
	 * @author Mathias Lauer
	 * 27 déc. 2020
	 */
	public double calculateDiscounts(Ticket ticket)
	{
		double result = 1.0;
		double totalDiscount = 0.0;
		
		for(IDiscount discount : discounts) {
			double discountPercent = discount.calculateDiscount(ticket);
			if(discountPercent == 0.0) return 0.0;			//If a discount return 0 then it give a free price, no point to browse other discounts
			if(discountPercent < 1.0) {						//1.0 represent 100% of the price, in other word this mean its not discounted
				totalDiscount += discountPercent;			//If it is minus that 100%, add the percent to the total percent calculation
			}
		}
		
		if(totalDiscount < 1.0) {	
			result -= totalDiscount;
		}
		
		return result;
	}
	
	/**
	 * Activate a discount
	 * @param dicounts The discount to activate
	 */
	public void activateDiscount(IDiscount discount) {
		if(discount != null) {
			discounts.add(discount);
		}
	}
	
	/**
	 * Deactivate a discount
	 * @param discount The discount will be deactivated
	 */
	public void deactivateDiscount(IDiscount discount) {
		discounts.remove(discount);
	}

}
