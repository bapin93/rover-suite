package com.arctica.rover.suite.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.arctica.rover.suite.utils.IRunnable;
import com.arctica.rover.suite.utils.TimeLimit;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;

/**
 * <p>The RoverController class communicates with the ArcticaRover using JSON-RPC.</p>
 *
 * @author Andres Pineda
 */
public class RoverController {
	//==================================================================
	// CONSTANTS
	//==================================================================

	//==================================================================
	// VARIABLES
	//==================================================================
	private JSONRPC2Session _rpc;
	private Integer _nextRequestID;
	private Boolean _isServerOnline;

	//==================================================================
	// CONSTRUCTORS
	//==================================================================
	/**
	 * no-args constructor
	 * Construct a new BlimpController object that connects to the server at SERVER_ADDR.
	 */
	public RoverController(String ipAddress) {
		_nextRequestID = 0;
		try {
			URL url = new URL("http://" + ipAddress + ":10101");
			System.out.println("Connected to: " + ipAddress);
			_rpc = new JSONRPC2Session(url);
			_isServerOnline = true;
		} catch (Exception e) {
			e.printStackTrace();
			_isServerOnline = false;
		}
	}

	//==================================================================
	// PUBLIC METHODS
	//==================================================================

	/**
	 * Ping the server to check if it is online.
	 *
	 * @return true if the server initialized correctly and responds to a ping,
	 *   false otherwise
	 */
	public boolean isServerOnline() {
		if (_rpc == null) {
			return false;
		}
		_isServerOnline = simplePing();
		return _isServerOnline;
	}

	/**
	 * Creates a JSON request to stop the rover
	 */
	public void stop() {
		if (_isServerOnline) {
			JSONRPC2Request request = new JSONRPC2Request("stop", newRequestID());
			try {
				_rpc.send(request);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates a JSON request to move the rover backwards
	 */
	@SuppressWarnings("unused")
	public void backward() {
		if (_isServerOnline) {
			List<Object> params = new ArrayList<Object>();
			JSONRPC2Request request = new JSONRPC2Request("goBackward", newRequestID());
			try {
				_rpc.send(request);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates a JSON request to move the rover forwards
	 */
	@SuppressWarnings("unused")
	public void forward() {
		if (_isServerOnline) {
			List<Object> params = new ArrayList<Object>();
			JSONRPC2Request request = new JSONRPC2Request("goForward", newRequestID());
			try {
				_rpc.send(request);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates a JSON request to move the rover left
	 */
	@SuppressWarnings("unused")
	public void left() {
		if (_isServerOnline) {
			List<Object> params = new ArrayList<Object>();
			JSONRPC2Request request = new JSONRPC2Request("pivotLeft", newRequestID());
			try {
				_rpc.send(request);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates a JSON request to move the rover right
	 */
	@SuppressWarnings("unused")
	public void right() {
		if (_isServerOnline) {
			List<Object> params = new ArrayList<Object>();
			JSONRPC2Request request = new JSONRPC2Request("pivotRight", newRequestID());
			try {
				_rpc.send(request);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param percentage
	 */
	public void cam0Position(final Integer percentage) {
		if (_isServerOnline) {
			List<Object> params = new ArrayList<Object>();
			params.add(percentage);
			JSONRPC2Request request = new JSONRPC2Request("setCam0Position", params, newRequestID());
			try {
				_rpc.send(request);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates a JSON request to set the speed of the rover
	 * @param speed
	 */
	public void setSpeed(final Integer speed) {
		Integer newSpeed = speed;
		newSpeed = (newSpeed + 1) * 10;
		if (_isServerOnline) {
			List<Object> params = new ArrayList<Object>();
			params.add(newSpeed);
			JSONRPC2Request request = new JSONRPC2Request("setSpeed", params, newRequestID());
			try {
				_rpc.send(request);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Ping the server to check connection status.
	 * @return
	 */
	public Boolean ping() {
		final Boolean [] result = new Boolean [1];
		try {
			new TimeLimit(3000, new IRunnable() {
				public void run() throws Exception {
					JSONRPC2Request request = new JSONRPC2Request("ping", newRequestID());
					JSONRPC2Response response = _rpc.send(request);
					if (response.getResult().equals(true)) {
						result[0] = true;
					} else {
						result[0] = false;
					}
				}
			});

		} catch (Exception e) {
			return false;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return result[0];
	}

	/**
	 * Ping the server without threading
	 * @return
	 */
	public Boolean simplePing() {
		Boolean result = false;
		try {
			JSONRPC2Request request = new JSONRPC2Request("ping", newRequestID());
			JSONRPC2Response response = _rpc.send(request);
			if (response.getResult().equals(true)) {
				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
			return false;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return result;
	}

	//==================================================================
	// PROTECTED METHODS
	//==================================================================

	//==================================================================
	// DEFAULT METHODS
	//==================================================================

	//==================================================================
	// PRIVATE METHODS
	//==================================================================
	/**
	 * Get a new ID for the next request to send.
	 * @return
	 */
	private Integer newRequestID() {
		Integer result = _nextRequestID;
		_nextRequestID++;
		return result;
	}

	//==================================================================
	// INNER CLASSES
	//==================================================================

}













