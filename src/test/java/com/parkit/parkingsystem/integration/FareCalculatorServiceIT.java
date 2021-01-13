package com.parkit.parkingsystem.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.discount.Discount30MnFree;
import com.parkit.parkingsystem.service.discount.Discount5PercentForKnownUser;
import com.parkit.parkingsystem.service.discount.DiscountCalculatorService;

@ExtendWith(MockitoExtension.class)
public class FareCalculatorServiceIT {
	
	private static FareCalculatorService fareCalculator;
	private final String VEHICLE_REG_NUMBER = "ABCDEF";
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static DataBasePrepareService dataBasePrepareService;
	private static TicketDAO ticketDAO;
	
	@BeforeAll
	public static void setUp() {
		ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
		DiscountCalculatorService discountCalculator = new DiscountCalculatorService();
		discountCalculator.activateDiscount(new Discount30MnFree());
		discountCalculator.activateDiscount(new Discount5PercentForKnownUser(ticketDAO));
		fareCalculator = new FareCalculatorService(discountCalculator);
	}
	
	@BeforeEach
	public void setUpPerTest()
	{

	}
	
	  @AfterEach
	    private void cleanUpPerTest(){
	    	dataBasePrepareService.clearDataBaseEntries();
	    }
	
	@Test
	public void fareCalculationITFor1HourKnownUser_shouldReturnRatePerHourMinus5Percent() {
		//Given
		//Create a previous ticket
        Ticket dbTicket = new Ticket();
        dbTicket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000 * 48)));
        dbTicket.setOutTime(new Date(System.currentTimeMillis() - (60*60*1000 * 40)));
        dbTicket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR,false));
        dbTicket.setVehicleRegNumber(VEHICLE_REG_NUMBER);
        dbTicket.setPrice(1.95);
        ticketDAO.saveTicket(dbTicket);
        
        //Create current ticket
		Ticket ticket = new Ticket();
		Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );//1hour parking for know user time should give 5% discount
        
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        //When
        ticket.setInTime(inTime);
        ticket.setParkingSpot(parkingSpot); 
        ticket.setVehicleRegNumber(VEHICLE_REG_NUMBER);
        //Save current ticket
        ticketDAO.saveTicket(ticket);
        
        ticket.setOutTime(outTime);

		fareCalculator.calculateFare(ticket);
		
		//Then
		assertEquals(1.42, ticket.getPrice());
	}
	
	@Test
	public void fareCalculationITFor1HourUnknownUser_shouldReturnRatePerHourWithoutDiscount() {
		//Given
        
        //Create current ticket
		Ticket ticket = new Ticket();
		Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );//1hour parking for know user time should give 5% discount
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        //When
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot); 
        ticket.setVehicleRegNumber(VEHICLE_REG_NUMBER);
		fareCalculator.calculateFare(ticket);
		
		//Then
		assertEquals(1.5, ticket.getPrice());
	}
	
	@Test
	public void fareCalculationITFor29mn_shouldReturnFreePrice() {
		//Given
        
        //Create current ticket
		Ticket ticket = new Ticket();
		Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  29 * 60 * 1000) );//29mn parking should give free price
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        //When
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot); 
        ticket.setVehicleRegNumber(VEHICLE_REG_NUMBER);
		fareCalculator.calculateFare(ticket);
		
		//Then
		assertEquals(0.0, ticket.getPrice());
	}
	

}
