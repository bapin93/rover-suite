package com.arctica.rover.suite;

import com.arctica.rover.suite.utils.UIUtils;
import com.arctica.rover.suite.view.RoverUI;

/**
 * <p>The RoverClient class provides a main method to execute the program.</p>
 * 
 * @author andres
 */
public class RoverClient {
	public static void main(String [] args) {
		UIUtils.searchLocalIPs();
		@SuppressWarnings("unused")
		RoverUI face = new RoverUI("Rover Control Suite");
	}
}
