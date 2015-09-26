
package com.arctica.rover.suite.utils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * <p>The UIUtils class provides utilities for the UI components</p>
 * 
 * @author andres
 */
public class UIUtils {

	//==================================================================
	// VARIABLES
	//==================================================================
	private static ExecutorService _executorService;
	private static List<String> _ipAddressValues;

	//==================================================================
	// PUBLIC METHODS
	//==================================================================
	/**
	 * Scans for local IP Addresses to connect to
	 */
	public static void searchLocalIPs() {
		_executorService = ServiceUtils.getExecutorService();
		_ipAddressValues = new ArrayList<String>();
		_executorService.submit(new Runnable() {
			public void run() {
				try {
					String localIP = InetAddress.getLocalHost().getHostAddress();
					String [] splitIP = localIP.split("\\.");
					String subnet = splitIP[0] + "." + splitIP[1] + "." + splitIP[2];
					int timeout = 3000;
					for (int i = 1; i < 7; i++){
						String host = subnet + "." + i;
						if (InetAddress.getByName(host).isReachable(timeout)){
							System.out.println(host + " is reachable");
							if(!_ipAddressValues.contains(host)) {
								_ipAddressValues.add(host);
							}
						}
					}
				} catch (UnknownHostException e) {
					System.out.println(e.getMessage());
				} catch (ConnectException e) {
					System.out.println(e.getMessage());
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}

			}

		});

		_executorService.submit(new Runnable() {
			public void run() {
				try {
					String localIP = InetAddress.getLocalHost().getHostAddress();
					String [] splitIP = localIP.split("\\.");
					String subnet = splitIP[0] + "." + splitIP[1] + "." + splitIP[2];
					int timeout = 3000;
					for (int i = 8; i < 126; i++){
						String host = subnet + "." + i;
						if (InetAddress.getByName(host).isReachable(timeout)) {
							System.out.println(host + " is reachable");
							if(!_ipAddressValues.contains(host)) {
								_ipAddressValues.add(host);
							}
						}
					}
				} catch (UnknownHostException e) {
					System.out.println(e.getMessage());
				} catch (ConnectException e) {
					System.out.println(e.getMessage());
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		});

		_executorService.submit(new Runnable() {
			public void run() {
				try {
					String localIP = InetAddress.getLocalHost().getHostAddress();
					String [] splitIP = localIP.split("\\.");
					String subnet = splitIP[0] + "." + splitIP[1] + "." + splitIP[2];
					int timeout = 3000;
					for (int i = 127; i < 255; i++){
						String host = subnet + "." + i;
						if (InetAddress.getByName(host).isReachable(timeout)){
							System.out.println(host + " is reachable");
							if(!_ipAddressValues.contains(host)) {
								_ipAddressValues.add(host);
							}
						}
					}
				} catch (UnknownHostException e) {
					System.out.println(e.getMessage());
				} catch (ConnectException e) {
					System.out.println(e.getMessage());
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}

		});
	}

	/**
	 * Returns the current list of scanned ip addresses
	 * @return _ipAddressValues
	 */
	public static List<String> getIPAddressValues() {
		return _ipAddressValues;
	}
}
