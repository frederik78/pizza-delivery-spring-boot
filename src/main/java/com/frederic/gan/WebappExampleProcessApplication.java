package com.frederic.gan;

import static org.camunda.bpm.engine.authorization.Authorization.ANY;
import static org.camunda.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;
import static org.camunda.bpm.engine.authorization.Permissions.ALL;

import java.util.List;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Groups;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class WebappExampleProcessApplication {

	public static void main(String... args) {
		SpringApplication.run(WebappExampleProcessApplication.class, args);
		// do default setup of platform
		ProcessEngine engine = BpmPlatform.getDefaultProcessEngine();
//		DecisionService decisionService = engine.getDecisionService();
//
//		VariableMap variables = Variables.createVariables().putValue("season", "Spring").putValue("guestCount", 10)
//				.putValue("guestsWithChildren", false);
//
//		DmnDecisionTableResult dishDecisionResult = decisionService.evaluateDecisionTableByKey("dish", variables);
//		String desiredDish = dishDecisionResult.getSingleEntry();
//
//		System.out.println("Desired dish: " + desiredDish);
//
//		DmnDecisionTableResult beveragesDecisionResult = decisionService.evaluateDecisionTableByKey("beverages",
//				variables);
//		List<Object> beverages = beveragesDecisionResult.collectEntries("beverages");
//
//		System.out.println("Desired beverages: " + beverages); // engine.getRuntimeService().startProcessInstanceByKey("orderPizza");
		createDefaultUser(engine);
		// setCamundaEELicenseKey(engine);
	}

	public static void createDefaultUser(ProcessEngine engine) {
		// and add default user to Camunda to be ready-to-go
		if (engine.getIdentityService().createUserQuery().userId("luigi").count() == 0) {
			User user = engine.getIdentityService().newUser("luigi");
			user.setFirstName("Luigi");
			user.setLastName("Calzone");
			user.setPassword("demo");
			user.setEmail("luigi@camunda.org");
			engine.getIdentityService().saveUser(user);

			Group group = engine.getIdentityService().newGroup("waiter");
			group.setName("Waiter");
			group.setType(Groups.GROUP_TYPE_WORKFLOW);
			engine.getIdentityService().saveGroup(group);

			for (Resource resource : Resources.values()) {
				Authorization auth = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
				auth.setGroupId(Groups.GROUP_TYPE_WORKFLOW);
				auth.addPermission(ALL);
				auth.setResourceId(ANY);
				auth.setResource(resource);
				engine.getAuthorizationService().saveAuthorization(auth);
			}

			engine.getIdentityService().createMembership("luigi", "waiter");
		}

		if (engine.getIdentityService().createUserQuery().userId("mario").count() == 0) {
			User user = engine.getIdentityService().newUser("mario");
			user.setFirstName("Mario");
			user.setLastName("Margarita");
			user.setPassword("demo");
			user.setEmail("mario@camunda.org");
			engine.getIdentityService().saveUser(user);

			Group group = engine.getIdentityService().newGroup("pizza-chef");
			group.setName("Pizza Chef");
			group.setType(Groups.GROUP_TYPE_WORKFLOW);
			engine.getIdentityService().saveGroup(group);

			// for (Resource resource : Resources.values()) {
			// Authorization auth =
			// engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
			// auth.setGroupId(Groups.GROUP_TYPE_WORKFLOW);
			// auth.addPermission(ALL);
			// auth.setResourceId(ANY);
			// auth.setResource(resource);
			// engine.getAuthorizationService().saveAuthorization(auth);
			// }

			engine.getIdentityService().createMembership("mario", "pizza-chef");
		}

		// // create default "all tasks" filter
		// if
		// (engine.getFilterService().createFilterQuery().filterName("mario").count()
		// == 0) {
		//
		// Map<String, Object> filterProperties = new HashMap<String, Object>();
		// filterProperties.put("description", "Alle Aufgaben");
		// filterProperties.put("priority", 10);
		//
		// Filter filter = engine.getFilterService().newTaskFilter() //
		// .setName("Alle") //
		// .setProperties(filterProperties)//
		// .setOwner("demo")//
		// .setQuery(engine.getTaskService().createTaskQuery());
		// engine.getFilterService().saveFilter(filter);
		//
		// // and authorize demo user for it
		// if
		// (engine.getAuthorizationService().createAuthorizationQuery().resourceType(FILTER)
		// .resourceId(filter.getId()) //
		// .userIdIn("demo").count() == 0) {
		// Authorization managementGroupFilterRead =
		// engine.getAuthorizationService()
		// .createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
		// managementGroupFilterRead.setResource(FILTER);
		// managementGroupFilterRead.setResourceId(filter.getId());
		// managementGroupFilterRead.addPermission(ALL);
		// managementGroupFilterRead.setUserId("demo");
		// engine.getAuthorizationService().saveAuthorization(managementGroupFilterRead);
		// }
		//
		// }
	}

	public static void setCamundaEELicenseKey(ProcessEngine engine) {
		engine.getManagementService().setProperty("camunda-license-key", "xxxx");
	}

}
