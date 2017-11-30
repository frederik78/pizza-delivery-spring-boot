package com.frederic.gan.dao;

import org.springframework.data.repository.CrudRepository;

import com.frederic.gan.entities.OrderEntity;

public interface OrderedPizzaRepository extends CrudRepository<OrderEntity, Long>{

}
