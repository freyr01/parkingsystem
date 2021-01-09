package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.discount.Discount30MnFree;
import com.parkit.parkingsystem.service.discount.Discount5PercentForKnownUser;
import com.parkit.parkingsystem.service.discount.DiscountCalculatorService;

@ExtendWith(MockitoExtension.class)
public class DiscountCalculatorServiceTest {
	
	private static DiscountCalculatorService discountCalculator;
	
	@Mock
	private static Discount30MnFree discount30MnFree;
	
	@Mock
	private static Discount5PercentForKnownUser discount5PercentForKnownUser;
	
	@BeforeEach
	public void setUpPerTest()
	{
		discountCalculator = new DiscountCalculatorService();
		
		discountCalculator.activateDiscount(discount5PercentForKnownUser);
		discountCalculator.activateDiscount(discount30MnFree);
		
	}
	
	@Test
	public void calculTotalDiscountFor5PercentAndNoFreeAccess_ShouldReturnFactor95Percent() {
		//Given
		when(discount30MnFree.calculateDiscount(any(Ticket.class))).thenReturn(1.0);
		when(discount5PercentForKnownUser.calculateDiscount(any(Ticket.class))).thenReturn(5.0/100.0);
		
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );//1hour parking for know user time should give 5% discount
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        
        //When
		double totalDiscount = discountCalculator.calculateDiscounts(ticket);
		
		//Then
		verify(discount30MnFree, Mockito.times(1)).calculateDiscount(any(Ticket.class));
		verify(discount5PercentForKnownUser, Mockito.times(1)).calculateDiscount(any(Ticket.class));
		assertEquals(1. - (5./100.), totalDiscount);
		
	}
	
	@Test
	public void calculTotalDiscountWithOneGivingFreeAccess_shouldReturnFactor0() {
		//Given
		when(discount30MnFree.calculateDiscount(any(Ticket.class))).thenReturn(0.0);
		when(discount5PercentForKnownUser.calculateDiscount(any(Ticket.class))).thenReturn(5.0/100.0);
		
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );//1hour parking for know user time should give 5% discount
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        
        //When
		double totalDiscount = discountCalculator.calculateDiscounts(ticket);
		
		//Then
		verify(discount30MnFree, Mockito.times(1)).calculateDiscount(any(Ticket.class));
		verify(discount5PercentForKnownUser, Mockito.times(1)).calculateDiscount(any(Ticket.class));
		
		assertEquals(0., totalDiscount);
		
	}
}
