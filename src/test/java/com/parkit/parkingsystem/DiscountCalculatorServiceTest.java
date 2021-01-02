package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.discount.Discount30MnFree;
import com.parkit.parkingsystem.service.discount.Discount5PercentForKnownUser;
import com.parkit.parkingsystem.service.discount.IDiscount;

@ExtendWith(MockitoExtension.class)
public class DiscountCalculatorServiceTest {
    
    @Mock
    private static TicketDAO ticketDAO;
    
    @Test
    public void calculateDiscount30mnFreeFor29MnUse()
    {
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  29 * 60 * 1000) );//1hour parking for know user time should give 5% discount
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        
        IDiscount discount30mnFree = new Discount30MnFree();
        double factor = discount30mnFree.calculateDiscount(ticket);
        
        assertEquals(0.0, factor);
    }
    
    @Test
    public void calculateDiscount30mnFreeFor35MnUse()
    {
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  35 * 60 * 1000) );//1hour parking for know user time should give 5% discount
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        
        IDiscount discount30mnFree = new Discount30MnFree();
        double factor = discount30mnFree.calculateDiscount(ticket);
        
        assertEquals(1.0, factor);
    }
    
    @Test
    public void calculateDiscount5PercentForKnownUser() {
        Ticket dbTicket = new Ticket();
        dbTicket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000 * 48)));
        dbTicket.setOutTime(new Date(System.currentTimeMillis() - (60*60*1000 * 40)));
        dbTicket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR,false));
        dbTicket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(dbTicket);
       
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );//1hour parking for know user time should give 5% discount
        Date outTime = new Date();
        Ticket ticket = new Ticket();
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR,false));
        ticket.setVehicleRegNumber("ABCDEF");
        
        IDiscount discount5Percent = new Discount5PercentForKnownUser(ticketDAO);
        double factor = discount5Percent.calculateDiscount(ticket);
        
        verify(ticketDAO, Mockito.times(1)).getTicket(anyString());
        assertEquals(5/100, factor);
        

    }
    
    @Test
    public void calculateDiscount5PercentForUnknownUser() {
        when(ticketDAO.getTicket(anyString())).thenReturn(null);
       
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  29 * 60 * 1000) );//1hour parking for know user time should give 5% discount
        Date outTime = new Date();
        Ticket ticket = new Ticket();
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR,false));
        ticket.setVehicleRegNumber("ABCDEF");
        
        IDiscount discount5Percent = new Discount5PercentForKnownUser(ticketDAO);
        double factor = discount5Percent.calculateDiscount(ticket);
        
        verify(ticketDAO, Mockito.times(1)).getTicket(anyString());
        assertEquals(1, factor);
        

    }


}
