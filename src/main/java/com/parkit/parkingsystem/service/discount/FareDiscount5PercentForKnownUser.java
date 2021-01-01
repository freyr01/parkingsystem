package com.parkit.parkingsystem.service.discount;

import com.parkit.parkingsystem.model.Ticket;

public class FareDiscount5PercentForKnownUser implements IFareDiscount {

	@Override
	public double calculateDiscount(Ticket ticket) {
		// TODO Auto-generated method stub
		return 1.;
	}

}
