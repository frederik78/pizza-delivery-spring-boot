package com.frederic.gan.service;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frederic.gan.configuration.JmsConfiguration;
import com.frederic.gan.entities.OrderEntity;
import com.frederic.gan.jms.Email;

@Service
public class OrderBusinessLogic implements JavaDelegate {

	private static Logger LOGGER = Logger.getLogger(OrderBusinessLogic.class.getName());

	@Autowired
	private JmsTemplate jmsTemplate;

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void persistOrder(DelegateExecution delegateExecution) {
		// Create new order instance
		OrderEntity orderEntity = new OrderEntity();

		// Get all process variables
		Map<String, Object> variables = delegateExecution.getVariables();

		// Set order attributes
		orderEntity.setCustomer((String) variables.get("customer"));
		orderEntity.setAddress((String) variables.get("address"));
		orderEntity.setPizza((String) variables.get("pizza"));

		// Persist order instance and flush. After the flush the
		// id of the order instance is set.
		entityManager.persist(orderEntity);
		entityManager.flush();

		// Add newly created order id as process variable
		delegateExecution.setVariable("orderId", orderEntity.getId());
	}

	public OrderEntity getOrder(Long orderId) {
		// Load order entity from database
		return entityManager.find(OrderEntity.class, orderId);
	}

	public void rejectOrder(DelegateExecution delegateExecution) {
		OrderEntity order = getOrder((Long) delegateExecution.getVariable("orderId"));
		LOGGER.log(Level.INFO, "\n\n\nSending Email:\nDear {0}, your order {1} of a {2} pizza has been rejected.\n\n\n",
				new String[] { order.getCustomer(), String.valueOf(order.getId()), order.getPizza() });
		jmsTemplate.convertAndSend(JmsConfiguration.MAILBOX_TOPIC, new Email("info@example.com", "Pizza cannot be delivered"));
	}

	public void persistValidation(DelegateExecution delegateExecution) {
		OrderEntity orderEntity = getOrder((Long) delegateExecution.getVariable("orderId"));
		orderEntity.setApproved((Boolean) delegateExecution.getVariable("approved"));
		entityManager.persist(orderEntity);
		entityManager.flush();

	}

	public void pizzaIsFinished(DelegateExecution delegateExecution){
		
		LOGGER.log(Level.INFO, "\n***********************\n*                     *\n*   Pizza is ready    *\n*   to be delivered   *\n*                     *\n*                     *\n*                     *\n***********************\n");
		jmsTemplate.convertAndSend(JmsConfiguration.PIZZA_FINISHED_TOPIC, (Long) delegateExecution.getVariable("orderId"));
	}
	
	
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub

	}

}
