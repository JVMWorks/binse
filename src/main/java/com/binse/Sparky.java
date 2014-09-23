package com.binse;

import static spark.Spark.get;
import static spark.Spark.setPort;
import spark.Request;
import spark.Response;
import spark.Route;

public class Sparky {
	
	public static void main(String[] args) {
		
		setPort(Integer.parseInt(System.getenv("PORT")));
		
		get(new Route("/hi") {
			@Override
			public Object handle(Request req, Response res) {
				return "HelloWorld";
			}
		});
		
	}
	
}
