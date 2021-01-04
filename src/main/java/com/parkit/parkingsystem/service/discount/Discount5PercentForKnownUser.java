package com.parkit.parkingsystem.service.discount;

import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

/**
 * Implement the discount
 * 5 Percent reduction for known user
 * @author Mathias Lauer
 * 3 janv. 2021
 */
public class Discount5PercentForKnownUser implements IDiscount {
	
	private TicketDAO ticketDAO;
	
	public Discount5PercentForKnownUser(TicketDAO p_ticketDAO) {
		ticketDAO = p_ticketDAO;
	}
	
	@Override
	/**
	 * Check if a ticket in the db is found with the same reg number
	 * @return The discount percent
	 * @author Mathias Lauer
	 * 3 janv. 2021
	 */
	public double calculateDiscount(Ticket ticket) {
		if(ticketDAO != null) {
			Ticket dbTicket = ticketDAO.getTicket(ticket.getVehicleRegNumber());
			if(dbTicket != null && dbTicket.getOutTime() != null) {
				return 5./100.;
			}
		}
		return 1.;
	}

}
