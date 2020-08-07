package com.reidnet.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.reidnet.cursomc.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);

}
