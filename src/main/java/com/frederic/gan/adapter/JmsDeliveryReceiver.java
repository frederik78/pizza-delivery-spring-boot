package com.frederic.gan.adapter;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.frederic.gan.configuration.JmsConfiguration;
import com.frederic.gan.dao.OrderedPizzaRepository;
import com.frederic.gan.entities.OrderEntity;
import com.frederic.gan.entities.Status;
import com.frederic.gan.service.OrderBusinessLogic;

@Component
public class JmsDeliveryReceiver {

	@Autowired
	private ProcessEngine camunda;

	@Autowired
	private OrderedPizzaRepository orderedPizzaRepository;
	
	private static Logger LOGGER = Logger.getLogger(OrderBusinessLogic.class.getName());

	@JmsListener(destination = JmsConfiguration.PIZZA_FINISHED_TOPIC)
	public void startDelivery(Long orderId) {

		final OrderEntity orderEntity = orderedPizzaRepository.findOne(orderId);
		orderEntity.setStatus(Status.PICKED_UP);
		orderedPizzaRepository.save(orderEntity);
		
		camunda.getRuntimeService().createMessageCorrelation("Message_Pizza_Ready") //
				.setVariable("shipmentId", UUID.randomUUID().toString()) //
				.setVariable("orderId", orderId) //
				.correlateWithResult();
		LOGGER.log(Level.INFO,
				"\n***********************\n*                     *\n*   Pizza picked up   *\n*      in charge      *\n*                     *\n*                     *\n*                     *\n***********************\n");
	}

}
