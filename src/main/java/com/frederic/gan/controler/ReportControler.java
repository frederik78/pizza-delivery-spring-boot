package com.frederic.gan.controler;

import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.frederic.gan.dao.OrderedPizzaRepository;
import com.frederic.gan.report.StatusReports;

import ar.com.fdvs.dj.domain.DynamicReport;
import net.sf.jasperreports.view.JasperDesignViewer;
import net.sf.jasperreports.view.JasperViewer;

@Controller
@RequestMapping("/app/reports/")
public class ReportControler {

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private OrderedPizzaRepository orderedPizzaRepository;

	@Autowired
	private StatusReports statusReports;
	
	@RequestMapping("/orderedpizzas")
	public String getReports(Model model) {

		model.addAttribute("name", processEngine.getIdentityService().getCurrentAuthentication().getUserId());
		model.addAttribute("allOrderedPizza", orderedPizzaRepository.findAll());
		return "orderedpizzas";
	}
	
	@RequestMapping("/orderedpizzas/jasper")
	public String getReportsWithJasper(Model model) throws Exception {

		statusReports.testReport();
//		JasperViewer.viewReport(statusReports.jp);	//finally display the report report
//		JasperDesignViewer.viewReportDesign(statusReports.jr);
		
		
		model.addAttribute("name", processEngine.getIdentityService().getCurrentAuthentication().getUserId());
		model.addAttribute("allOrderedPizza", orderedPizzaRepository.findAll());
		return "orderedpizzas";
	}
}
