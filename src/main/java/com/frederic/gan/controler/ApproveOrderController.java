package com.frederic.gan.controler;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.frederic.gan.entities.OrderEntity;
import com.frederic.gan.service.OrderBusinessLogic;

@RestController
@RequestMapping("/approveorder")
public class ApproveOrderController implements Serializable {
	private static Logger LOGGER = Logger.getLogger(ApproveOrderController.class.getName());
	private static final long serialVersionUID = 1L;

	@PersistenceContext
	private EntityManager entityManager;

	// Inject the OrderBusinessLogic to update the persisted order
	@Autowired
	private OrderBusinessLogic orderBusinessLogic;

	// Caches the OrderEntity during the conversation
	private OrderEntity orderEntity;
	
	@RequestMapping(method=RequestMethod.GET)
	public OrderEntity getOrderEntity(Long orderId) {
		if (orderEntity == null) {
			orderEntity = orderBusinessLogic.getOrder(orderId);
		}
		return orderEntity;
	}
	
	@RequestMapping(value="/delivered/{orderId}", method=RequestMethod.POST)
	public void sendEmail(@PathVariable(name="orderId") Long orderId){
		LOGGER.log(Level.INFO, "\n***********************\n*                     *\n*   Pizza delivered   *\n*                     *\n*                     *\n*                     *\n***********************\n");
	}
}

