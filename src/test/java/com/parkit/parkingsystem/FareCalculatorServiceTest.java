package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.IFareCalculatorService;
import com.parkit.parkingsystem.service.discount.DiscountCalculatorService;

@ExtendWith(MockitoExtension.class)
public class FareCalculatorServiceTest {

    private static IFareCalculatorService fareCalculatorService;
    private Ticket ticket;
    
    @Mock
    private static DiscountCalculatorService discountCalculator;

    @BeforeAll
    private static void setUp() { 
        
    }

    @BeforeEach
    private void setUpPerTest() {
    	fareCalculatorService = new FareCalculatorService(discountCalculator);
        ticket = new Ticket();
    }
    
    @AfterEach
    private void endPerTest() {
    }

    @Test
    public void calculateFareCar(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        when(discountCalculator.calculateDiscounts(any(Ticket.class))).thenReturn(1.0);
        fareCalculatorService.calculateFare(ticket);
    	verify(discountCalculator, Mockito.times(1)).calculateDiscounts(any(Ticket.class));

        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        when(discountCalculator.calculateDiscounts(any(Ticket.class))).thenReturn(1.0);
        fareCalculatorService.calculateFare(ticket);
        verify(discountCalculator, Mockito.times(1)).calculateDiscounts(any(Ticket.class));
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
       
        ticket.setOutTime(outTime);
        
        ticket.setParkingSpot(parkingSpot);
       // when(discountCalculator.calculateDiscounts(any(Ticket.class))).thenReturn(1.0);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        when(discountCalculator.calculateDiscounts(any(Ticket.class))).thenReturn(1.0);
        fareCalculatorService.calculateFare(ticket);
        verify(discountCalculator, Mockito.times(1)).calculateDiscounts(any(Ticket.class));
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
      
        ticket.setOutTime(outTime);
    
        ticket.setParkingSpot(parkingSpot);
        when(discountCalculator.calculateDiscounts(any(Ticket.class))).thenReturn(1.0);
        fareCalculatorService.calculateFare(ticket);
        verify(discountCalculator, Mockito.times(1)).calculateDiscounts(any(Ticket.class));
        assertEquals( roundToTwoDecimal(0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        when(discountCalculator.calculateDiscounts(any(Ticket.class))).thenReturn(1.0);
        fareCalculatorService.calculateFare(ticket);
        verify(discountCalculator, Mockito.times(1)).calculateDiscounts(any(Ticket.class));
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarWith30MnFreeDiscount()
    {
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  30 * 60 * 1000) );//30 minutes parking time should give the 30mn free discount
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
      
        ticket.setOutTime(outTime);
    
        ticket.setParkingSpot(parkingSpot);
        when(discountCalculator.calculateDiscounts(any(Ticket.class))).thenReturn(0.0);
        fareCalculatorService.calculateFare(ticket);
        verify(discountCalculator, Mockito.times(1)).calculateDiscounts(any(Ticket.class));
        assertEquals(0.0, ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarWith5PourcentDiscountForKnownUser()
    {
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );//1hour parking for know user time should give 5% discount
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
      
        ticket.setOutTime(outTime);
    
        ticket.setParkingSpot(parkingSpot);
        when(discountCalculator.calculateDiscounts(any(Ticket.class))).thenReturn(0.95);
        fareCalculatorService.calculateFare(ticket);
        verify(discountCalculator, Mockito.times(1)).calculateDiscounts(any(Ticket.class));
        
        assertEquals(roundToTwoDecimal(Fare.CAR_RATE_PER_HOUR * 0.95), ticket.getPrice());
    }
    
    private double roundToTwoDecimal(double nbr)
    {
    	return (double)Math.round(nbr * 100) / 100;
    }

}
