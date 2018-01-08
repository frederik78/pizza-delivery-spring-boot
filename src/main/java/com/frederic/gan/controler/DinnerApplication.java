package com.frederic.gan.controler;

import java.util.List;

import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

@ProcessApplication("Dinner App DMN")
public class DinnerApplication extends ServletProcessApplication
{

    @PostDeploy
    public void evaluateDecisionTable(ProcessEngine processEngine) {

      DecisionService decisionService = processEngine.getDecisionService();

      VariableMap variables = Variables.createVariables()
        .putValue("season", "Spring")
        .putValue("guestCount", 10)
        .putValue("guestsWithChildren", false);

      DmnDecisionTableResult dishDecisionResult = decisionService.evaluateDecisionTableByKey("dish", variables);
      String desiredDish = dishDecisionResult.getSingleEntry();

      System.out.println("Desired dish: " + desiredDish);

      DmnDecisionTableResult beveragesDecisionResult = decisionService.evaluateDecisionTableByKey("beverages", variables);
      List<Object> beverages = beveragesDecisionResult.collectEntries("beverages");

      System.out.println("Desired beverages: " + beverages);
    }

}