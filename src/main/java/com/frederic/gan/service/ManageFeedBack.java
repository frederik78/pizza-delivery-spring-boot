package com.frederic.gan.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//import com.camunda.demo.springboot.ProcessConstants;

@Component
@ConfigurationProperties
public class ManageFeedBack implements JavaDelegate {

  @Autowired
  private RestTemplate rest;

  private String restProxyHost = "localhost";
  private String restProxyPort = "8090";

  private String restEndpoint() {
    return "http://" + restProxyHost + ":" + restProxyPort + "/approveorder/delivered/";
  }
  

  @Override
  public void execute(DelegateExecution ctx) throws Exception {
    Long orderId = (Long) ctx.getVariable("orderId");
    HttpEntity<Long> request = new HttpEntity<>(orderId);
    Long response = rest.postForObject( //
        restEndpoint()+"/"+orderId, //
        request, //
        Long.class);
    
  }

  public String getRestProxyHost() {
    return restProxyHost;
  }

  public void setRestProxyHost(String restProxyHost) {
    this.restProxyHost = restProxyHost;
  }

  public String getRestProxyPort() {
    return restProxyPort;
  }

  public void setRestProxyPort(String restProxyPort) {
    this.restProxyPort = restProxyPort;
  }

}
