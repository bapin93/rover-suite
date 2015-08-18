package com.arctica.rover.suite.utils;

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

	//==================================================================
	// CONSTRUCTORS
	//==================================================================
	public TimeLimit(final Integer limit, final IRunnable runnable) {
		_limit = limit;
		_runnable = runnable;
	}

	//==================================================================
	// PUBLIC METHODS
	//==================================================================
	@SuppressWarnings("deprecation")
	public synchronized void run() throws Throwable {
		final Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					_runnable.run();
				} catch (final Throwable t) {
					TimeLimit.this._throwable = t;
				}
			}
		});

		thread.start();

		try {
			thread.join(_limit);
			if(thread.isAlive()) {
				thread.stop();
				throw new InterruptedException("Timeout");
			}
		} catch (final InterruptedException e) {
			if(_throwable == null)
				_throwable = e;
		}
		if(_throwable != null) {
			final Throwable tt = _throwable;
			_throwable = null;
			throw tt;
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



