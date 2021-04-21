package com.nr.instrumentation.context;

public class NRUtils {

	
	private static String managedServer = null;
	
	public static String getManagedServer() {
		if(managedServer == null) {
			managedServer = System.getProperty("weblogic.Name", "UnknownServer");
		}
		return managedServer;
	}
}
