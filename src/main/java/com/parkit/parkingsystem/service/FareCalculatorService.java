package com.parkit.parkingsystem.service;


import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.discount.Discount30MnFree;
import com.parkit.parkingsystem.service.discount.IDiscount;
import com.parkit.parkingsystem.service.discount.IDiscountCalculatorService;
import com.parkit.parkingsystem.service.discount.Discount5PercentForKnownUser;
import com.parkit.parkingsystem.service.discount.DiscountCalculatorService;

public class FareCalculatorService implements IFareCalculatorService {
	private static final Logger logger = LogManager.getLogger("FareCalculatorService");
	private DiscountCalculatorService discountCalculator;
	private static final int DECIMAL_ACCURACY = 100;
	
	/**
	 * Constructor
	 * p_discountCalculatorService Need a DiscountCalculatorService instance to proceed the discounts
	 * @author Mathias Lauer
	 * 3 janv. 2021
	 */
	public FareCalculatorService(DiscountCalculatorService p_discountCalculatorService) {
		discountCalculator = p_discountCalculatorService;
	}
	
	/**
	 * Calculate the price of a ticket and set the result using Ticket.setPrice(result);
	 * @param ticket A ticket to calculate
	 * @author Mathias Lauer
	 * 3 janv. 2021
	 */
	@Override
	public void calculateFare(Ticket ticket) {
       if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
       logger.info("Calculating fare for vehicle reg number: {}...", ticket.getVehicleRegNumber());
       
       //Convert Date type to Instant for use new Date-Time API
       Instant inTime = ticket.getInTime().toInstant(); 
       Instant outTime = ticket.getOutTime().toInstant();

       Duration duration = Duration.between(inTime, outTime);
       logger.info("With duration: {}", duration);

       //Get the duration in minutes and convert to decimal hour.
       double durationInHour = (double)(duration.toMinutes()) / 60;
       
       //Calculate discounts factor number
       double totalDiscount = discountCalculator.calculateDiscounts(ticket);
       logger.info("And discount factor: {}", totalDiscount);
       
       //Get the rate per hour for vehicle type
       double ratePerHour = 0.;
       switch (ticket.getParkingSpot().getParkingType()){
	       case CAR: {
	    	   	ratePerHour = Fare.CAR_RATE_PER_HOUR;
	           break;
	       }
	       case BIKE: {
	           	ratePerHour = Fare.BIKE_RATE_PER_HOUR;
	           break;
	       }
	       default: throw new IllegalArgumentException("Unkown Parking Type");
       }
       logger.info("And vehicle factor: {}", ratePerHour);
       
       //Round two decimals after comma
       double price = roundDecimal(durationInHour * ratePerHour * totalDiscount);
       
       //Set the final result
       logger.info("Result is: {}", price);
       ticket.setPrice(price);

	}
	
	private double roundDecimal(double nbr)
	{
		return (double)Math.round(nbr * DECIMAL_ACCURACY) / DECIMAL_ACCURACY;
	}
	
	public IDiscountCalculatorService getDiscountCalculatorService() {
		return discountCalculator;
	}
}
