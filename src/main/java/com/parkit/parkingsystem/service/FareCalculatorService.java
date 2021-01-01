package com.parkit.parkingsystem.service;


import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.discount.FareDiscount30MnFree;
import com.parkit.parkingsystem.service.discount.IFareDiscount;
import com.parkit.parkingsystem.service.discount.FareDiscount5PercentForKnownUser;

public class FareCalculatorService implements IFareCalculatorService {
	
	private List<IFareDiscount> discounts;

	public FareCalculatorService() {
		
		discounts = new ArrayList<IFareDiscount>();
		// Activate discount by adding a new instance in the list
		discounts.add(new FareDiscount30MnFree());			//Activate 30Mn free discount
		discounts.add(new FareDiscount5PercentForKnownUser()); //Activate 5 percent discount for already know user
	}
	
	@Override
	public void calculateFare(Ticket ticket) {
       if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

       
       //Convert Date type to Instant for use new Date-Time API
       Instant inTime = ticket.getInTime().toInstant(); 
       Instant outTime = ticket.getOutTime().toInstant();

       Duration duration = Duration.between(inTime, outTime);

       //Get the duration in minutes and convert to decimal hour.
       double durationInHour = (double)(duration.toMinutes()) / 60;
       
       //Calculate discounts multiply number
       double totalDiscount = calculateDiscounts(ticket);
       
       switch (ticket.getParkingSpot().getParkingType()){
	       case CAR: {
	           ticket.setPrice(durationInHour * Fare.CAR_RATE_PER_HOUR  * totalDiscount);
	           break;
	       }
	       case BIKE: {
	           ticket.setPrice(durationInHour * Fare.BIKE_RATE_PER_HOUR * totalDiscount);
	           break;
	       }
	       default: throw new IllegalArgumentException("Unkown Parking Type");
       }

	}
	
	
	/**
	 * Calculate all discounts to apply
	 * @return totalDiscount A number to multiply with price to obtain the final price
	 * @author Mathias Lauer
	 * 27 déc. 2020
	 */
	private double calculateDiscounts(Ticket ticket)
	{
		double totalDiscount = 1.0;
		for(IFareDiscount discount : discounts) {
			totalDiscount *= discount.calculateDiscount(ticket);
		}
		
		return totalDiscount;
	}
	
	/**
	 * Add a discount to the list
	 * @param discount
	 * @return Discount list updated
	 * @author Mathias Lauer
	 * 1 janv. 2021
	 */
	public List<IFareDiscount> addDiscount(IFareDiscount discount)
	{
		discounts.add(discount);
		
		return discounts;
	}
	
	/**
	 * Remove a discount to the list
	 * @param discount
	 * @return Discount list updated
	 * @author Mathias Lauer
	 * 1 janv. 2021
	 */
	public List<IFareDiscount> removeDiscount(IFareDiscount discount)
	{
		discounts.remove(discount);
		
		return discounts;
	}

}
