package com.frederic.gan.service;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frederic.gan.configuration.JmsConfiguration;
import com.frederic.gan.dao.OrderedPizzaRepository;
import com.frederic.gan.entities.OrderEntity;
import com.frederic.gan.entities.Status;
import com.frederic.gan.jms.Email;

@Service
public class OrderBusinessLogic implements JavaDelegate {

	private static Logger LOGGER = Logger.getLogger(OrderBusinessLogic.class.getName());

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private OrderedPizzaRepository orderedPizzaRepository;
	
	@Transactional
	public void persistOrder(DelegateExecution delegateExecution) {
		// Create new order instance
		OrderEntity orderEntity = new OrderEntity();

		// Get all process variables
		Map<String, Object> variables = delegateExecution.getVariables();

		// Set order attributes
		orderEntity.setCustomer((String) variables.get("customer"));
		orderEntity.setAddress((String) variables.get("address"));
		orderEntity.setZipCode((String) variables.get("zipCode"));
		orderEntity.setPizza((String) variables.get("pizza"));
		orderEntity.setStatus(Status.ORDERED);
		// Persist order instance and flush. After the flush the
		// id of the order instance is set.
		orderEntity = orderedPizzaRepository.save(orderEntity);

		// Add newly created order id as process variable
		delegateExecution.setVariable("orderId", orderEntity.getId());
	}

	public OrderEntity getOrder(Long orderId) {
		// Load order entity from database
		return orderedPizzaRepository.findOne(orderId);
	}

	public void rejectOrder(DelegateExecution delegateExecution) {
		OrderEntity order = getOrder((Long) delegateExecution.getVariable("orderId"));
		LOGGER.log(Level.INFO, "\n\n\nSending Email:\nDear {0}, your order {1} of a {2} pizza has been rejected.\n\n\n",
				new String[] { order.getCustomer(), String.valueOf(order.getId()), order.getPizza() });
		final OrderEntity orderEntity = getOrder((Long) delegateExecution.getVariable("orderId"));
		orderEntity.setStatus(Status.REJECTED);
		orderedPizzaRepository.save(orderEntity);
		jmsTemplate.convertAndSend(JmsConfiguration.MAILBOX_TOPIC, new Email("info@example.com", "Pizza cannot be delivered"));
	}

	public void persistValidation(DelegateExecution delegateExecution) {
		final OrderEntity orderEntity = getOrder((Long) delegateExecution.getVariable("orderId"));
		orderEntity.setApproved((Boolean) delegateExecution.getVariable("approved"));
		orderEntity.setStatus(Status.APPROVED);
		orderedPizzaRepository.save(orderEntity);
	}

	public void pizzaIsFinished(DelegateExecution delegateExecution){
		
		LOGGER.log(Level.INFO, "\n***********************\n*                     *\n*   Pizza is ready    *\n*   to be delivered   *\n*                     *\n*                     *\n*                     *\n***********************\n");
		final OrderEntity orderEntity = getOrder((Long) delegateExecution.getVariable("orderId"));
		orderEntity.setStatus(Status.FINISHED);
		orderedPizzaRepository.save(orderEntity);
		jmsTemplate.convertAndSend(JmsConfiguration.PIZZA_FINISHED_TOPIC, (Long) delegateExecution.getVariable("orderId"));
	}
	
	
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub

	}

}
