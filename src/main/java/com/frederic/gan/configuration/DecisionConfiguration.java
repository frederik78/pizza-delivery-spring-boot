package com.frederic.gan.configuration;

import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DecisionConfiguration {
	
	
	@Bean
	DecisionService getDecisionService(ProcessEngine engine){
		return engine.getDecisionService();
	}
	
	

}
