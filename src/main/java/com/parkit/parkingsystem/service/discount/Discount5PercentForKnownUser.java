package com.parkit.parkingsystem.service.discount;

import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class Discount5PercentForKnownUser implements IDiscount {
	
	private TicketDAO ticketDAO;
	
	public Discount5PercentForKnownUser(TicketDAO p_ticketDAO) {
		ticketDAO = p_ticketDAO;
	}
	@Override
	public double calculateDiscount(Ticket ticket) {
		if(ticketDAO != null && ticketDAO.getTicket(ticket.getVehicleRegNumber()) != null) {
			return (5/100);
		}
		return 1.;
	}

}
