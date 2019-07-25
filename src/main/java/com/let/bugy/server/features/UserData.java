package com.let.bugy.server.features;

import com.sun.net.httpserver.HttpExchange;

public class UserData {

	String IPAddress;

	UserData (HttpExchange he){
		IPAddress = he.getRemoteAddress().getAddress().toString();
	}
}
