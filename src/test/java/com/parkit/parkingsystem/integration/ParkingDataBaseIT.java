package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.IFareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.service.discount.DiscountCalculatorService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {
	private static final Logger logger = LogManager.getLogger("ParkingDataBaseIT");
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static IFareCalculatorService fareCalculatorService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        fareCalculatorService = new FareCalculatorService(new DiscountCalculatorService());
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        //dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterEach
    private void cleanUpPerTest(){
    	dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    @DisplayName("Test if a vehicule enter process create a ticket and get an available slot setting it unavailable")
    public int testParkingACar(){
    	int ticketId = 0;
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, fareCalculatorService);
        parkingService.processIncomingVehicle();
     
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ticketId = ticket.getId();
        int nextAvailableSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        int ticketParkingSpot = ticket.getParkingSpot().getId();
        
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());	//Check if registration number of the ticket is the same as expected
        assertNotNull(ticket.getInTime());
        assertNull(ticket.getOutTime());
        assertNotEquals(nextAvailableSlot, ticketParkingSpot); // Check if next available slot and current ticket parking spot is not the same
        
        return ticketId;
    }

    @Test
    @DisplayName("Test if a vehicule exit process update correctly the right ticket and parking slot in database")
    public void testParkingLotExit(){
        int ticketId = testParkingACar();
   
        //Wait 1sec before exiting to prevent bad out time exception
        sleep1sec();

    	
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, fareCalculatorService);
        parkingService.processExitingVehicle();
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        int requestTicketId = ticket.getId();
        int parkingId = ticket.getParkingSpot().getId();
        
        //Get the parking spot used to verify availability
        Connection con = null;
        boolean parkingSpotIsAvailable = false;
        try {
	        con = dataBaseTestConfig.getConnection();
	        String request = "select p.available from parking p where PARKING_NUMBER = ? and AVAILABLE = 1";
	        PreparedStatement ps = con.prepareStatement(request);
	        ps.setInt(1, parkingId);
	        ResultSet rs = ps.executeQuery();
	        parkingSpotIsAvailable = rs.first();
        } catch (SQLException | ClassNotFoundException e) {
        	logger.error("Error fetching available slot in IT", e);
        }   finally {
            dataBaseTestConfig.closeConnection(con);
        }
        
        assertTrue(parkingSpotIsAvailable);
        assertNotNull(ticket.getOutTime());
        assertEquals(ticketId, requestTicketId);
    }
    
    @Test
    public void testRecurentUserTicketUpdate() {
    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, fareCalculatorService);
    	
    	int firstTicketId = testParkingACar();	//Proceed a first entry and get ticket id
    	sleep1sec();
         parkingService.processExitingVehicle(); //Proceed an exit
         Timestamp firstTicketOutTimestamp = null;
    	int secondTicketId = testParkingACar(); //Proceed a second entry and get ticket id
    	sleep1sec();
    	parkingService.processExitingVehicle(); //Proceed an exit
    	Timestamp secondTicketOutTimestamp = null;
    	
    	Connection con = null;
        try {
	        con = dataBaseTestConfig.getConnection();
	        String request = String.format("select t.out_time from ticket t where id=%d", firstTicketId);
	        PreparedStatement ps = con.prepareStatement(request);
	        ResultSet rs = ps.executeQuery();
	        if(rs.next()) {
	        	firstTicketOutTimestamp = rs.getTimestamp(1); 	//Get Out time of the first ticket in db
	        }
	        
	        String request2 = String.format("select t.out_time from ticket t where id=%d", secondTicketId);
	        PreparedStatement ps2 = con.prepareStatement(request2);
	        ResultSet rs2 = ps2.executeQuery();
	        if(rs2.next()) {
	        	secondTicketOutTimestamp = rs.getTimestamp(1); //Get Out time of the second ticket in db
	        }
	        
        } catch (SQLException | ClassNotFoundException e) {
        	logger.error("Error fetching available slot in IT", e);
        }   finally {
            dataBaseTestConfig.closeConnection(con);
        }
        
        //Check if all ticket was filled correctly
        assertNotNull(firstTicketOutTimestamp);
        assertNotNull(secondTicketOutTimestamp);
    }
    
    private void sleep1sec()
    {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.error("Sleep thread error: ", e);
		}
    }

}
