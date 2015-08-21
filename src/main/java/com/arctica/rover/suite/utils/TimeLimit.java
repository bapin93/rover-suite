package com.arctica.rover.suite.utils;

import java.util.concurrent.ExecutorService;

/**
 * <p>The TimeLimit class creates a timeout for pinging the server.</p>
 * 
 * @author andres
 */
public class TimeLimit implements IRunnable {

	//==================================================================
	// CONSTANTS
	//==================================================================

	//==================================================================
	// VARIABLES
	//==================================================================
	private final IRunnable _runnable;
	private final Integer _limit;
	private Throwable _throwable;
	private ExecutorService _executorService;

	//==================================================================
	// CONSTRUCTORS
	//==================================================================
	/**
	 * @param limit
	 * @param runnable
	 */
	public TimeLimit(final Integer limit, final IRunnable runnable) {
		_limit = limit;
		_runnable = runnable;
		_executorService = ServiceUtils.getExecutorService();
	}

	//==================================================================
	// PUBLIC METHODS
	//==================================================================
	/* (non-Javadoc)
	 * @see com.arctica.rover.suite.utils.IRunnable#run()
	 */
	public synchronized void run() throws Throwable {
		final Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					_runnable.run();
				} catch (final Throwable throwable) {
					_throwable = throwable;
				}
			}
		});

		_executorService.submit(thread);

		try {
			thread.join(_limit);
			if(thread.isAlive()) {
				thread.interrupt();
				throw new InterruptedException("Timeout");
			}
		} catch (final InterruptedException e) {
			if(_throwable == null)
				_throwable = e;
		}
		if(_throwable != null) {
			throw _throwable;
		}
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

	//==================================================================
	// INNER CLASSES
	//==================================================================

}



