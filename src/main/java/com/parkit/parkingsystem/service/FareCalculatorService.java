package com.parkit.parkingsystem.service;


import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.discount.Discount30MnFree;
import com.parkit.parkingsystem.service.discount.IDiscount;
import com.parkit.parkingsystem.service.discount.Discount5PercentForKnownUser;
import com.parkit.parkingsystem.service.discount.DiscountCalculatorService;

public class FareCalculatorService implements IFareCalculatorService {

	private DiscountCalculatorService discountCalculator;
	private static final int roundDecimalAccuracy = 100;
	
	public FareCalculatorService(DiscountCalculatorService p_discountCalculatorService) {
		discountCalculator = p_discountCalculatorService;
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
       
       //Calculate discounts factor number
       double totalDiscount = discountCalculator.calculateDiscounts(ticket);
       
       switch (ticket.getParkingSpot().getParkingType()){
	       case CAR: {
	           ticket.setPrice(roundDecimal(durationInHour * Fare.CAR_RATE_PER_HOUR  * totalDiscount));
	           break;
	       }
	       case BIKE: {
	           ticket.setPrice(roundDecimal(durationInHour * Fare.BIKE_RATE_PER_HOUR * totalDiscount));
	           break;
	       }
	       default: throw new IllegalArgumentException("Unkown Parking Type");
       }

	}
	
	private double roundDecimal(double nbr)
	{
		return (double)Math.round(nbr * roundDecimalAccuracy) / roundDecimalAccuracy;
	}
}
