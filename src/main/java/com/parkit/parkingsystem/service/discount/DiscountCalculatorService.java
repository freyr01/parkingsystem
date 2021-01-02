package com.parkit.parkingsystem.service.discount;

import java.util.ArrayList;
import java.util.List;

import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class DiscountCalculatorService {
	
	private TicketDAO ticketDAO;
	
	private List<IDiscount> discounts;
	
	public DiscountCalculatorService(TicketDAO p_ticketDAO)
	{
		ticketDAO = p_ticketDAO;
		
		discounts = new ArrayList<IDiscount>();
		// Activate discount by adding a new instance in the list
		discounts.add(new Discount30MnFree());			//Activate 30Mn free discount
		discounts.add(new Discount5PercentForKnownUser(ticketDAO)); //Activate 5 percent discount for known user
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
			if(discountPercent == 0.0) return 0.0;
			if(discountPercent != 1.0) {
				totalDiscount += discountPercent;
			}
		}
		
			if(totalDiscount < 1.0) {	
				result -= totalDiscount;
			}
		return result;
	}

}
