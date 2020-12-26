package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.model.Ticket;

public interface IFareCalculatorService {
	
	public void calculateFare(Ticket ticket);

}
