package com.parkit.parkingsystem.service;


import java.time.Duration;
import java.time.Instant;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorServiceV2 implements IFareCalculatorService {

	
	@Override
	public void calculateFare(Ticket ticket) {
       if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

       
       Instant inTime = ticket.getInTime().toInstant(); 
       Instant outTime = ticket.getOutTime().toInstant();

       Duration duration = Duration.between(inTime, outTime);

       float durationInHour = (float)duration.toMinutes() / (float)60;
      
       switch (ticket.getParkingSpot().getParkingType()){
	       case CAR: {
	           ticket.setPrice(durationInHour * Fare.CAR_RATE_PER_HOUR);
	           break;
	       }
	       case BIKE: {
	           ticket.setPrice(durationInHour * Fare.BIKE_RATE_PER_HOUR);
	           break;
	       }
	       default: throw new IllegalArgumentException("Unkown Parking Type");
       }

	}

}
