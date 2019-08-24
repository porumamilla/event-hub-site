package com.eventhub.site;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import com.eventhub.site.config.ApiEndPointUri;
import com.eventhub.site.form.IntegrationsForm;
import com.eventhub.site.form.RegistrationForm;
import com.eventhub.site.model.Consumer;
import com.eventhub.site.model.Event;
import com.eventhub.site.model.EventCountsByDay;
import com.eventhub.site.model.EventDefinition;
import com.eventhub.site.model.Source;
import com.eventhub.site.model.SourceType;
import com.eventhub.site.model.Target;
import com.eventhub.site.model.User;
import com.eventhub.site.model.Workspace;
@Controller
@SessionAttributes("user")
public class SiteController {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ApiEndPointUri apiEndPointUri;
	
	
	@GetMapping(value = "/index")
	public String index() {
		return "index";
	}

	@GetMapping(value = "/events")
	public String events() {
		return "events";
	}

	@GetMapping(value = "/definitions")
	public ModelAndView definitions(@ModelAttribute("user") User user) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("manageDefinitions");
		
		List<EventDefinition> definitions = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/eventDefinitions?orgId=" +
				user.getOrgId() + "&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<EventDefinition>>() {
				}).getBody();

		List<Source> sources = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/sourceTypes?orgId=" + user.getOrgId() +
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

	@PostMapping(value = "/createDefinition")
	public ModelAndView createDefinition(@ModelAttribute("user") User user, @ModelAttribute EventDefinition eventDefinition) {
		System.out.println("Entered in createDefinition == " + eventDefinition.getEventName());
		eventDefinition.setOrgId(user.getOrgId());
		eventDefinition.setWorkspace(user.getDefaultWorkspace());
		
		HttpEntity<EventDefinition> requestUpdate = new HttpEntity<>(eventDefinition, null);
		ResponseEntity<String> response = restTemplate.exchange( apiEndPointUri.getDaoApiEndpoint() + "/organization/eventDefinition", HttpMethod.PUT, requestUpdate , String.class );
	
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new RuntimeException(response.getBody());
		}

		return definitions(user);
	}
	
	@GetMapping(value = "/editDefinition")
	public ModelAndView editDefinition(@ModelAttribute("user") User user, @RequestParam(name="id") String id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("editDefinition");

		EventDefinition definition = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "eventDefinition?id=" +
				id, HttpMethod.GET, null,
				new ParameterizedTypeReference<EventDefinition>() {
				}).getBody();

		List<Source> sources = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/sourceTypes?orgId=" + user.getOrgId() +
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
	
	@PostMapping(value = "/updateDefinition")
	@ResponseStatus(HttpStatus.OK)
	public void updateDefinition(@ModelAttribute("user") User user, @ModelAttribute EventDefinition eventDefinition) {
		System.out.println(eventDefinition.getSchema());
		EventDefinition definitionFromDB = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "eventDefinition?id=" +
				eventDefinition.getId(), HttpMethod.GET, null,
				new ParameterizedTypeReference<EventDefinition>() {
				}).getBody();
		eventDefinition.setEventName(definitionFromDB.getEventName());
		eventDefinition.setSourceId(definitionFromDB.getSourceId());
		eventDefinition.setWorkspace(definitionFromDB.getWorkspace());
		eventDefinition.setOrgId(user.getOrgId());
		eventDefinition.setWorkspace(user.getDefaultWorkspace());
		
		HttpEntity<EventDefinition> requestUpdate = new HttpEntity<>(eventDefinition, null);
		ResponseEntity<String> response = restTemplate.exchange( apiEndPointUri.getDaoApiEndpoint() + "/organization/eventDefinition", HttpMethod.POST, requestUpdate , String.class );
	
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new RuntimeException(response.getBody());
		}

		//return definitions(user);
	}
	
	@PostMapping(value = "/deleteDefinition")
	public ModelAndView deleteDefinition(Model model, @ModelAttribute("user") User user, @RequestParam(name="id") String id) {
		ResponseEntity<String> response = restTemplate.exchange( apiEndPointUri.getDaoApiEndpoint() + "/organization/eventDefinition?id=" + id, HttpMethod.DELETE, null , String.class );
	
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new RuntimeException(response.getBody());
		}
		return definitions(user);
	}

	@GetMapping(value = "/sources")
	public String sources(Model model, @ModelAttribute("user") User user) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		List<SourceType> orgSourceTypes = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/sourceTypes?orgId=" + user.getOrgId() +
				"&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<SourceType>>() {
				}).getBody();
		model.addAttribute("orgSourceTypes", orgSourceTypes);

		List<SourceType> sourceTypes = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/sourceTypes", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<SourceType>>() {
				}).getBody();
		model.addAttribute("sourceTypes", sourceTypes);

		return "manageSources";
	}
	
	@GetMapping(value = "/sourceType")
	public String source(Model model, @ModelAttribute("user") User user, @RequestParam(name="id") String id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		List<SourceType> orgSourceTypes = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/sourceTypes?orgId=" + user.getOrgId() +
				"&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<SourceType>>() {
				}).getBody();
		for (SourceType orgSourceType : orgSourceTypes) {
			if (orgSourceType.getId().equals(id)) {
				model.addAttribute("orgSourceType", orgSourceType);
				break;
			}
		}
		

		return "sourceType";
	}
	
	@PostMapping(value = "/createSource")
	public String createSource(Model model, @ModelAttribute("user") User user, @RequestParam(name="name") String name, @RequestParam(name="type") String type) {
		SourceType sourceType = new SourceType();
		sourceType.setName(name);
		sourceType.setType(type);
		sourceType.setWorkspace(user.getDefaultWorkspace());
		HttpEntity<SourceType> requestUpdate = new HttpEntity<>(sourceType, null);
		
		ResponseEntity<String> response = restTemplate.exchange( apiEndPointUri.getDaoApiEndpoint() + "/organization/sourceType?orgId=" + user.getOrgId(), HttpMethod.PUT, requestUpdate , String.class );
	
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new RuntimeException(response.getBody());
		}
		return sources(model, user);
	}
	
	@PostMapping(value = "/deleteSource")
	public String deleteSource(Model model, @ModelAttribute("user") User user, @RequestParam(name="id") String id) {
		SourceType sourceType = new SourceType();
		sourceType.setId(id);
		HttpEntity<SourceType> requestUpdate = new HttpEntity<>(sourceType, null);
		ResponseEntity<String> response = restTemplate.exchange( apiEndPointUri.getDaoApiEndpoint() + "/organization/sourceType?orgId=" + user.getOrgId(), HttpMethod.DELETE, requestUpdate , String.class );
	
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new RuntimeException(response.getBody());
		}
		return sources(model, user);
	}

	@GetMapping(value = "/targets")
	public String targets(Model model) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		List<Target> targets = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/targets", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Target>>() {
				}).getBody();
		model.addAttribute("targets", targets);

		List<Target> orgTargets = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/targets?orgId=dzFyTqq4dT7YIai8mogz", HttpMethod.GET, null,
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

		List<Source> sources = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/sources?orgId=" + user.getOrgId() +
				"&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Source>>() {
				}).getBody();
		model.addAttribute("sources", sources);

		List<Target> targets = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/targets", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Target>>() {
				}).getBody();
		model.addAttribute("targets", targets);
		model.addAttribute("integrationsForm", new IntegrationsForm());
		return "manageIntegrations";
	}

	@GetMapping(value = "/consumers")
	public String consumers(@ModelAttribute("user") User user, Model model) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		List<Consumer> consumers = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/consumers?orgId=" +
				user.getOrgId() + "&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Consumer>>() {
				}).getBody();
		model.addAttribute("consumers", consumers);

		return "manageConsumers";
	}
	
	@GetMapping(value="/showLogin")
	public String showLogin() {
		return "login";
	}
	
	@GetMapping(value="/manageWorkspaces")
	public ModelAndView manageWorkspaces(@ModelAttribute("user") User user) {
		ModelAndView modelAndView = new ModelAndView();
		System.out.println("User org id == " + user.getOrgId());
		List<Workspace> workspaces = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/workspaces?orgId=" +
				user.getOrgId() , HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Workspace>>() {
				}).getBody();
		modelAndView.addObject("workspaces", workspaces);
		modelAndView.setViewName("manageWorkspaces");
		return modelAndView;
	}
	
	@PostMapping(value = "/createWorkspace")
	public ModelAndView createWorkspace(@ModelAttribute("user") User user, @RequestParam(name="workspace") String workspace) {
		Workspace workspaceObj = new Workspace();
		workspaceObj.setName(workspace);
		workspaceObj.setOrgId(user.getOrgId());
		HttpEntity<Workspace> requestUpdate = new HttpEntity<>(workspaceObj, null);
		ResponseEntity<String> response = restTemplate.exchange( apiEndPointUri.getDaoApiEndpoint() + "/organization/workspace", HttpMethod.PUT, requestUpdate , String.class );
		user.setDefaultWorkspace(workspace);
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new RuntimeException(response.getBody());
		}
		return changeWorkspaceHelper(user, workspace);
	}
	
	@PostMapping(value = "/changeWorkspace")
	public ModelAndView changeWorkspace(@ModelAttribute("user") User user, @RequestParam(name="workspace") String workspace) {
		return changeWorkspaceHelper(user, workspace);
	}
	
	private ModelAndView changeWorkspaceHelper(User user, String workspace) {
		ModelAndView modelAndView = new ModelAndView();
		
		HttpHeaders headers1 = new HttpHeaders();
		headers1.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("userId",  user.getId());
		map.add("workspace",  workspace);
		System.out.println("workspace == " + workspace);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers1);

		ResponseEntity<String> response = restTemplate.postForEntity( apiEndPointUri.getDaoApiEndpoint() + "/user/changeWorkspace", request , String.class );
		user.setDefaultWorkspace(workspace);
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new RuntimeException(response.getBody());
		}
		modelAndView.setViewName("dashboard");
		return modelAndView;
	}
	
	@PostMapping(value = "/login")
	public ModelAndView login(@RequestParam(name="email") String email, @RequestParam(name="name") String name) {
		ModelAndView modelAndView = new ModelAndView();
		
		User user = getUserDetails(email, name);
        modelAndView.addObject("user", user);
        if (user != null && user.getId() != null) {
        	modelAndView.setViewName("dashboard");
        } else {
        	RegistrationForm registrationForm = new RegistrationForm();
        	registrationForm.setEmail(email);
        	registrationForm.setName(name);
        	modelAndView.addObject("registrationForm", registrationForm);
        	modelAndView.setViewName("registration");
        }
		return modelAndView;
	}
	
	@PostMapping(value = "/register")
	public ModelAndView register(@ModelAttribute RegistrationForm registrationForm) {
		ModelAndView modelAndView = new ModelAndView();
		
		//User user = getUserDetails(registrationForm.getEmail(), registrationForm.getName());
		
        //modelAndView.addObject("user", user);
        ResponseEntity<String> orgResponse = restTemplate.postForEntity( apiEndPointUri.getDaoApiEndpoint() + "/organization", registrationForm.getOrg() , String.class );
        String orgId = orgResponse.getBody();
        User user = registrationForm.getUser(orgId);
        ResponseEntity<String> userResponse = restTemplate.postForEntity( apiEndPointUri.getDaoApiEndpoint() + "/user", user , String.class );
        String userId = userResponse.getBody();
        user.setId(userId);
        modelAndView.addObject("user", user);
        
        modelAndView.setViewName("dashboard");
		return modelAndView;
	}

	private User getUserDetails(String email, String name) {
		User userDetails = new User();
	
		try {
			userDetails = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/user?email=" + email, HttpMethod.GET, null,
				new ParameterizedTypeReference<User>() {
				}).getBody();
			userDetails.setName(name);
		} catch(Exception error) {
			error.printStackTrace();
		}
		
		return userDetails;
	}

	@GetMapping(value = "/eventTester")
	public ModelAndView eventTester(@ModelAttribute("user") User user) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("eventTester");

		List<EventDefinition> definitions = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/eventDefinitions?orgId=" +
				user.getOrgId() + "&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<EventDefinition>>() {
				}).getBody();

		List<Source> sources = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/sources?orgId=" + user.getOrgId() +
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

		EventDefinition definition = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/eventDefinition?id=" +
				eventId, HttpMethod.GET, null,
				new ParameterizedTypeReference<EventDefinition>() {
				}).getBody();

		HttpHeaders headers1 = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("jsonSchema",  definition.getSchema());
		map.add("jsonData",  jsonData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers1);

		ResponseEntity<String> response = restTemplate.postForEntity( apiEndPointUri.getSchemaApiEndpoint() + "/validate", request , String.class );

		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new RuntimeException(response.getBody());
		}
	}

	@PostMapping(value = "/publishEvent")
	@ResponseStatus(HttpStatus.OK)
	public void publishEvent(@RequestParam(name="jsonData") String jsonData) {
		HttpHeaders headers1 = new HttpHeaders();
		headers1.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(jsonData, headers1);
		ResponseEntity<String> response = restTemplate.postForEntity( apiEndPointUri.getPublisherApiEndpoint() + "/publish", httpEntity , String.class );

		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new RuntimeException(response.getBody());
		}
	}
	
	@GetMapping(value = "/eventHistory")
	public String eventHistory(Model model, @ModelAttribute("user") User user) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		List<SourceType> orgSourceTypes = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/sourceTypes?orgId=" + user.getOrgId() +
				"&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<SourceType>>() {
				}).getBody();
		model.addAttribute("orgSourceTypes", orgSourceTypes);

		List<Event> events = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/events?orgId=" + user.getOrgId() + "&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Event>>() {
				}).getBody();
		setSourceType(orgSourceTypes, events);
		model.addAttribute("events", events);
		
		List<EventCountsByDay> eventCounts = restTemplate.exchange(apiEndPointUri.getDaoApiEndpoint() + "/organization/eventCountsForPast7Days?orgId=" + user.getOrgId() + "&workspace=" + user.getDefaultWorkspace(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<EventCountsByDay>>() {
				}).getBody();

		model.addAttribute("eventCounts", eventCounts);
		
		return "eventHistory";
	}
	
	private void setSourceType(List<SourceType> orgSourceTypes , List<Event> events) {
		for (Event event : events) {
			for (SourceType orgSourceType : orgSourceTypes) {
				if (orgSourceType.getKey().equals(event.getSourceKey())) {
					//System.out.println("inside if");
					event.setSourceName(orgSourceType.getName());
					break;
				}
			}
		}
	}
}
