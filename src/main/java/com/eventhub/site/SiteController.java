package com.eventhub.site;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.eventhub.site.model.Consumer;
import com.eventhub.site.model.EventDefinition;
import com.eventhub.site.model.Source;
import com.eventhub.site.model.Target;
import com.eventhub.site.model.User;
@Controller
@SessionAttributes("user")
public class SiteController {

	private String daoApiEndpoint = "http://event-hub-dao-service:8080";
	private String schemaApiEndpoint = "http://event-hub-schema-service:8080";
	private String publisherApiEndpoint = "http://event-hub-publisher-service:8080";
	
	@Autowired
	RestTemplate restTemplate;

	@GetMapping(value = "/events")
	public String events() {
		return "events";
	}

	@GetMapping(value = "/eventHistory")
	public String eventHistory() {
		return "eventHistory";
	}
	
	@GetMapping(value = "/definitions")
	public ModelAndView definitions(@ModelAttribute("user") User user) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("manageDefinitions");
		
		List<EventDefinition> definitions = restTemplate.exchange(daoApiEndpoint + "/organization/eventDefinitions?orgId=" + 
				user.getOrgId() + "&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<EventDefinition>>() {
				}).getBody();
		
		List<Source> sources = restTemplate.exchange(daoApiEndpoint + "/organization/sources?orgId=" + user.getOrgId() + 
				"&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Source>>() {
				}).getBody();
		
		for (EventDefinition definition : definitions) {
			for (Source source : sources) {
				if (definition.getSourceId().equals(source.getId())) {
					definition.setSourceName(source.getName());
					break;
				}
			}
		}
		modelAndView.addObject("definitions", definitions);
		modelAndView.addObject("sources", sources);
		
		return modelAndView;
	}
	
	@GetMapping(value = "/editDefinition")
	public ModelAndView editDefinition(@ModelAttribute("user") User user, @RequestParam(name="id") String id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("editDefinition");
		
		EventDefinition definition = restTemplate.exchange(daoApiEndpoint + "eventDefinition?id=" + 
				id, HttpMethod.GET, null,
				new ParameterizedTypeReference<EventDefinition>() {
				}).getBody();
		
		List<Source> sources = restTemplate.exchange(daoApiEndpoint + "/organization/sources?orgId=" + user.getOrgId() + 
				"&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Source>>() {
				}).getBody();
		
		for (Source source : sources) {
			if (definition.getSourceId().equals(source.getId())) {
				definition.setSourceName(source.getName());
				break;
			}
		}
		modelAndView.addObject("definition", definition);
		modelAndView.addObject("sources", sources);
		
		return modelAndView;
	}

	@GetMapping(value = "/sources")
	public String sources(Model model, @ModelAttribute("user") User user) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		List<Source> sources = restTemplate.exchange(daoApiEndpoint + "/organization/sources?orgId=" + user.getOrgId() + 
				"&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Source>>() {
				}).getBody();
		model.addAttribute("sources", sources);
		
		/*List<Source> protocols = restTemplate.exchange("http://localhost:8081/protocols", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Source>>() {
				}).getBody();
		model.addAttribute("protocols", protocols);*/
		
		return "manageSources";
	}
	
	@GetMapping(value = "/targets")
	public String targets(Model model) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		List<Target> targets = restTemplate.exchange(daoApiEndpoint + "/targets", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Target>>() {
				}).getBody();
		model.addAttribute("targets", targets);
		
		List<Target> orgTargets = restTemplate.exchange(daoApiEndpoint + "/organization/targets?orgId=dzFyTqq4dT7YIai8mogz", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Target>>() {
				}).getBody();
		
		for (Target orgTarget : orgTargets) {
			for (Target target : targets) {
				if (orgTarget.getParentId().equals(target.getId())) {
					orgTarget.setName(target.getName());
					break;
				}
			}
		}
		model.addAttribute("orgTargets", orgTargets);
		
		
		return "manageTargets";
	}
	
	@GetMapping(value = "/integrations")
	public String integrations(Model model, @ModelAttribute("user") User user) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		List<Source> sources = restTemplate.exchange(daoApiEndpoint + "/organization/sources?orgId=" + user.getOrgId() + 
				"&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Source>>() {
				}).getBody();
		model.addAttribute("sources", sources);
		
		List<Target> targets = restTemplate.exchange(daoApiEndpoint + "/targets", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Target>>() {
				}).getBody();
		model.addAttribute("targets", targets);
		
		return "manageIntegrations";
	}
	
	@GetMapping(value = "/consumers")
	public String consumers(@ModelAttribute("user") User user, Model model) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		List<Consumer> consumers = restTemplate.exchange(daoApiEndpoint + "/organization/consumers?orgId=" + 
				user.getOrgId() + "&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Consumer>>() {
				}).getBody();
		model.addAttribute("consumers", consumers);
		
		return "manageConsumers";
	}
	
	@GetMapping(value = "/registration")
	public String registration(Model model) {
		return "registration";
	}
	
	@PostMapping(value = "/login")
	public ModelAndView login(@RequestParam(name="email") String email, @RequestParam(name="name") String name) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dashboard");
		
        modelAndView.addObject("user", getUserDetails(email, name));
        
		return modelAndView;
	}
	
	private User getUserDetails(String email, String name) {
	
		User userDetails = restTemplate.exchange(daoApiEndpoint + "/organization/user?email=" + email, HttpMethod.GET, null,
				new ParameterizedTypeReference<User>() {
				}).getBody();
		
		userDetails.setName(name);
		return userDetails;
	}
	
	@GetMapping(value = "/eventTester")
	public ModelAndView eventTester(@ModelAttribute("user") User user) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("eventTester");
		
		List<EventDefinition> definitions = restTemplate.exchange(daoApiEndpoint + "/organization/eventDefinitions?orgId=" + 
				user.getOrgId() + "&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<EventDefinition>>() {
				}).getBody();
		
		List<Source> sources = restTemplate.exchange(daoApiEndpoint + "/organization/sources?orgId=" + user.getOrgId() + 
				"&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Source>>() {
				}).getBody();
		
		for (EventDefinition definition : definitions) {
			for (Source source : sources) {
				if (definition.getSourceId().equals(source.getId())) {
					definition.setSourceName(source.getName());
					break;
				}
			}
		}
		modelAndView.addObject("definitions", definitions);
		modelAndView.addObject("sources", sources);
		
		return modelAndView;
	}
	
	@PostMapping(value = "/validateEventData")
	@ResponseStatus(HttpStatus.OK)
	public void validateEventData(@RequestParam(name="eventId") String eventId, @RequestParam(name="jsonData") String jsonData) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		EventDefinition definition = restTemplate.exchange(daoApiEndpoint + "/eventDefinition?id=" + 
				eventId, HttpMethod.GET, null,
				new ParameterizedTypeReference<EventDefinition>() {
				}).getBody();
		
		HttpHeaders headers1 = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("jsonSchema",  definition.getSchema());
		map.add("jsonData",  jsonData);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers1);

		ResponseEntity<String> response = restTemplate.postForEntity( schemaApiEndpoint + "/validate", request , String.class );
		
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new RuntimeException(response.getBody());
		}
	}
	
	@PostMapping(value = "/publishEvent")
	@ResponseStatus(HttpStatus.OK)
	public void publishEvent(@RequestParam(name="jsonData") String jsonData) {
		HttpHeaders headers1 = new HttpHeaders();
		//headers1.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("jsonData",  jsonData);
		System.out.println("jsonData == " + jsonData);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers1);

		ResponseEntity<String> response = restTemplate.postForEntity( publisherApiEndpoint + "/publish", request , String.class );
		
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new RuntimeException(response.getBody());
		}
	}
}
