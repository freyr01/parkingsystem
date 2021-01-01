package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.model.Ticket;

public class IFareDiscount5PourcentForKnownUser implements IFareDiscount {

	@Override
	public double calculateDiscount(Ticket ticket) {
		// TODO Auto-generated method stub
		return 1.;
	}

}
