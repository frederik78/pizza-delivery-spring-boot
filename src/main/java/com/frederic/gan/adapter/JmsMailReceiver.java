package com.frederic.gan.adapter;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.frederic.gan.configuration.JmsConfiguration;
import com.frederic.gan.jms.Email;

@Component
public class JmsMailReceiver {
	private static Logger LOGGER = Logger.getLogger(JmsMailReceiver.class.getName());

	@JmsListener(destination = JmsConfiguration.MAILBOX_TOPIC, containerFactory = "myFactory")
	public void receiveMessage(Email email) {

		LOGGER.log(Level.INFO,
				"\n***********************\n*                     *\n* Email has been sent *\n*                     *\n*                     *\n*                     *\n***********************\n");

		System.out.println("Received <" + email + ">");
	}

}
