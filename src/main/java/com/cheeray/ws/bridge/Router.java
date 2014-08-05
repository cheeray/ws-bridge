package com.cheeray.ws.bridge;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

/**
 * A router maps REST URL with SOAP.
 * 
 * @author cheeray
 * 
 */
@Singleton
public class Router {
	
	Map<String, Route> routes = new ConcurrentHashMap<>();

	@PostConstruct
	private void init() {
		Route route = new Route();
		// TODO: populate routes.
		routes.put(route.getAction(), route);
	}
	
	public Route route(String action) {
		return routes.get(action);
	}

}
