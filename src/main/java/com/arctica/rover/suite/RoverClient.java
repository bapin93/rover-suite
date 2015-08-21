package com.arctica.rover.suite;

import com.arctica.rover.suite.utils.UIUtils;
import com.arctica.rover.suite.view.MainUI;

/**
 * <p>The RoverClient class provides a main method to execute the program.</p>
 * 
 * @author andres
 */
public class RoverClient extends MainUI {
	/**
	 * @param args
	 */
	public static void main(String [] args) {
		UIUtils.searchLocalIPs();
		launch(args);
	}
}