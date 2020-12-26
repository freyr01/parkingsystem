package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorServiceV2 implements IFareCalculatorService {

	@Override
	public void calculateFare(Ticket ticket) {
       if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

       long inTime = ticket.getInTime().getTime();
       long outTime = ticket.getOutTime().getTime();
       
       long durationInHour = Duration.ofMillis(outTime - inTime).getSeconds() / 60 / 60;
 
       
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
