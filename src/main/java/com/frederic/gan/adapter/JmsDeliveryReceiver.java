package com.frederic.gan.adapter;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.transaction.TransactionScoped;
import javax.transaction.Transactional;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
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
	
	@Autowired
	private DecisionService decisionService;
	
	private static Logger LOGGER = Logger.getLogger(OrderBusinessLogic.class.getName());

	@JmsListener(destination = JmsConfiguration.PIZZA_FINISHED_TOPIC)
	@Transactional
	public void startDelivery(Long orderId) {

		
		final OrderEntity orderEntity = orderedPizzaRepository.findOne(orderId);
		orderEntity.setStatus(Status.PICKED_UP);
		
		VariableMap variables = Variables.createVariables().putValue("zipCode", (String) orderEntity.getZipCode().substring(0, 3));
		DmnDecisionTableResult dishDecisionResult = decisionService.evaluateDecisionTableByKey("deliver", variables);
		String deliver = dishDecisionResult.getSingleEntry();

		System.out.println("deliver : " + deliver);

		orderEntity.setDeliver(deliver);
		
		orderedPizzaRepository.save(orderEntity);
		
		camunda.getRuntimeService().createMessageCorrelation("Message_Pizza_Ready") //
				.setVariable("shipmentId", UUID.randomUUID().toString()) //
				.setVariable("orderId", orderId) //
				.setVariable("deliver", deliver) //
				.setVariable("zipCode", orderEntity.getZipCode()) //
				.correlateWithResult();
		LOGGER.log(Level.INFO,
				"\n"//
				+ "***********************\n"//
				+ "*                     *\n"//
				+ "*   Pizza picked up   *\n"//
				+ "*      in charge      *\n"//
				+ "*                     *\n"//
				+ "*                     *\n"//
				+ "*                     *\n"//
				+ "***********************\n");
	}

}
