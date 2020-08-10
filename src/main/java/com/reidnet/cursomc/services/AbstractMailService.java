package com.reidnet.cursomc.services;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.reidnet.cursomc.domain.Pedido;

public abstract class AbstractMailService implements EmailService {
	
	@Autowired
	private TemplateEngine templateEngine;

	@Value("${default.sender}")
	private String sender;
	
	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public void sendOrderConfirmationEmail(Pedido obj) {

		SimpleMailMessage sm = prepareSimpleMailMesssageFromPedido(obj);
		sendEmail(sm);

	}

	protected SimpleMailMessage prepareSimpleMailMesssageFromPedido(Pedido obj) {

		SimpleMailMessage sm = new SimpleMailMessage();
		sm.setTo(obj.getCliente().getEmail());
		sm.setFrom(sender);
		sm.setSubject("Pedido Confirmado! Codigo:" + obj.getId());
		sm.setSentDate(new Date(System.currentTimeMillis()));
		sm.setText(obj.toString());
		return sm;
	}
	
	protected String htmlFromTemplatePedido(Pedido obj) {
		Context context = new Context();
		context.setVariable("pedido", obj);
		return templateEngine.process("email/confirmacaoPedido", context);
	}
	
	@Override
	public void sendOrderConfirmationHtmlEmail(Pedido obj) {
		
		MimeMessage mm;
		try {
			mm = prepareMimeMailMesssageFromPedido(obj);
			sendHtmlEmail(mm);
		} catch (MessagingException e) {
			sendOrderConfirmationEmail(obj);
		}
	}

	protected MimeMessage prepareMimeMailMesssageFromPedido(Pedido obj) throws MessagingException {
			MimeMessage mm = javaMailSender.createMimeMessage();
			MimeMessageHelper mmh = new MimeMessageHelper(mm, true);
			mmh.setTo(obj.getCliente().getEmail());
			mmh.setFrom(sender);
			mmh.setSubject("Pedido Confirmado" + obj.getId());
			mmh.setSentDate(new Date(System.currentTimeMillis()));
			mmh.setText(htmlFromTemplatePedido(obj), true);
					
		return mm;
	}

}
