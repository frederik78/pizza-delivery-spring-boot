package com.frederic.gan.controler;

import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.frederic.gan.dao.OrderedPizzaRepository;

@Controller
@RequestMapping("/app/reports/")
public class ReportControler {

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private OrderedPizzaRepository orderedPizzaRepository;

	@RequestMapping("/orderedpizzas")
	public String getReports(Model model) {

		model.addAttribute("name", processEngine.getIdentityService().getCurrentAuthentication().getUserId());
		model.addAttribute("allOrderedPizza", orderedPizzaRepository.findAll());
		return "orderedpizzas";
	}
}
