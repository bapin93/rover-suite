package com.arctica.rover.suite.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

import com.arctica.rover.suite.controller.RoverController;

/**
 * <p>The RoverUI class provides a UI for interacting with the Arctica Rover.</p>
 * 
 * @author andres
 */
public class RoverUI extends JFrame implements KeyListener, ActionListener {

	//==================================================================
	// CONSTANTS
	//==================================================================
	private static final long serialVersionUID = -7960462021660977334L;
	private static final String _NEW_LINE = System.getProperty("line.separator");
	private static final int _AKey = 65;
	private static final int _WKey = 87;
	private static final int _SKey = 83;
	private static final int _DKey = 68;

	//==================================================================
	// VARIABLES
	//==================================================================
	private JTextArea _displayArea = new JTextArea();
	private RoverController _roverController;
	private Timer _pingTimer;
	private Boolean [] _keys;
	private Integer _camera0LL;
	private Integer _camera0UL;

	//==================================================================
	// CONSTRUCTORS
	//==================================================================
	public RoverUI(String name) {
		_camera0LL = 17;
		_camera0UL = 90;
		_keys = new Boolean[256];
		initializeRoverSuite();
		createGUI(name);
	}

	//==================================================================
	// PUBLIC METHODS
	//==================================================================
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	public void keyPressed(KeyEvent e) {
		_keys[e.getKeyCode()] = true;
		if(_roverController.isServerOnline()){
			if(e.getKeyCode() == _WKey) {
				_roverController.stop();
				_roverController.forward();
				displayKey(e, "-arcticarover: Going Forward: ");
				displayKeyCode(e);
			} else if(e.getKeyCode() == _AKey) {
				_roverController.stop();
				_roverController.left();
				displayKey(e, "-arcticarover: Turning Left: ");
				displayKeyCode(e);
			} else if(e.getKeyCode() == _SKey) {
				_roverController.stop();
				_roverController.backward();
				displayKey(e, "-arcticarover: Going Backward: ");
				displayKeyCode(e);
			} else if(e.getKeyCode() == _DKey) {
				_roverController.stop();
				_roverController.right();
				displayKey(e, "-arcticarover: Turning Right: ");
				displayKeyCode(e);
			} else if(e.getKeyCode() == 37) {
				if (_roverController.getCamera0Position() >= _camera0LL) {
					_roverController.setCamera0Position(_roverController.getCamera0Position() - 1);
					_roverController.cam0Position(_roverController.getCamera0Position());;
					_displayArea.append("-arcticarover: Camera Left ");
					_displayArea.append(_NEW_LINE + "root@arcticarover:~# ");
					_displayArea.setCaretPosition(_displayArea.getDocument().getLength());
				}
			} else if(e.getKeyCode() == 39) {
				if (_roverController.getCamera0Position() <= _camera0UL) {
					_roverController.setCamera0Position(_roverController.getCamera0Position() + 1);
					_roverController.cam0Position(_roverController.getCamera0Position());;
					_displayArea.append("-arcticarover: Camera Right ");
					_displayArea.append(_NEW_LINE + "root@arcticarover:~# ");
					_displayArea.setCaretPosition(_displayArea.getDocument().getLength());
				}
			}
			else{
				_displayArea.append(_NEW_LINE + "KeyCode: " + e.getKeyCode());
				_displayArea.append(_NEW_LINE + "root@arcticarover:~# ");
				_displayArea.setCaretPosition(_displayArea.getDocument().getLength());
			}
		} else {
			
			_displayArea.append(_NEW_LINE + "-arcticarover: rover offline");
		}
	}


	public void keyReleased(KeyEvent e) {
		//displayKey(e, "Key Released: ");
		if(_roverController.isServerOnline()){
			_keys[e.getKeyCode()] = false;
			if(e.getKeyCode() >= 49 && e.getKeyCode() <= 58){
				_roverController.setSpeed(e.getKeyChar() - '0');
				displayKey(e, "-arcticarover: Setting Speed: ");
			} else if(_keys[_WKey] && !(_keys[_AKey] || _keys[_SKey] || _keys[_DKey])){
				_roverController.stop();
				_roverController.forward();
			} else if(_keys[_AKey] && !(_keys[_WKey] || _keys[_SKey] || _keys[_DKey])){
				_roverController.stop();
				_roverController.left();
			} else if(_keys[_SKey] && !(_keys[_AKey] || _keys[_WKey] || _keys[_DKey])){
				_roverController.stop();
				_roverController.backward();
			} else if(_keys[_DKey] && !(_keys[_AKey] || _keys[_SKey] || _keys[_WKey])){
				_roverController.stop();
				_roverController.right();
			} else if(!(_keys[_DKey] ||_keys[_AKey] || _keys[_SKey] || _keys[_WKey])) {
				_roverController.stop();
			}
		} else {
			_displayArea.append(_NEW_LINE + "-arcticarover: rover offline");
			_displayArea.append(_NEW_LINE + "root@arcticarover:~# ");
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
	private void createGUI(String name) {
		JFrame frame = new JFrame(name);
		Container contentPane = frame.getContentPane();
		final JButton exit = new JButton("Exit");
		JTextArea pane = new JTextArea(_NEW_LINE + "Enter Commands:  w,a,s,d  &  1-9 for speed");
		JScrollPane scrollPane = new JScrollPane(_displayArea);

		pane.setEditable(false);
		pane.addKeyListener(this);
		frame.setSize(450, 300);
		frame.setLocation(475, 220);
		scrollPane.setPreferredSize(new Dimension(375, 125));
		_displayArea.setEditable(false);

		ActionListener listener = new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				if( event.getSource() == exit ) {
					System.exit(0);
				}
			}
		};

		exit.addActionListener(listener);
		contentPane.add(pane, BorderLayout.PAGE_START);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(exit, BorderLayout.PAGE_END);

		_displayArea.append("Welcome to the Arctica Rover Control Center");
		_displayArea.append("\nroot@arcticarover:~# ");

		frame.setVisible(true);
	}

	private void initializeRoverSuite() {

		for (int i = 0; i < _keys.length; i++) {
			_keys[i] = Boolean.FALSE;
		}

		_roverController = new RoverController();

		TimerTask pingServer = new TimerTask() {
			boolean displayOnlineStatus= true, displayOfflineStatus = true;
			@Override
			public void run() {
				if (!_roverController.isServerOnline()) {
					if (displayOfflineStatus) {
						_displayArea.append(_NEW_LINE + "-arcticarover: rover disconnected");
						_displayArea.append(_NEW_LINE + "root@arcticarover:~# ");
						displayOfflineStatus = false; displayOnlineStatus = true;
					}
				}
				else if(displayOnlineStatus) {
					_displayArea.append(_NEW_LINE + "-arcticarover: rover connection successful!");
					_displayArea.append(_NEW_LINE + "root@arcticarover:~# ");
					displayOnlineStatus = false;
					displayOfflineStatus = true;
				}
			}
		};
		_pingTimer = new Timer("Ping Server");
		_pingTimer.scheduleAtFixedRate(pingServer, 0, 3000);
	}
	
	private void displayKey(KeyEvent e, String s) {
		char id = e.getKeyChar();
		String keyStatus = s;
		_displayArea.append(keyStatus + id);
		_displayArea.append(_NEW_LINE + "root@arcticarover:~# ");
		_displayArea.setCaretPosition(_displayArea.getDocument().getLength());
	}
	
	private void displayKeyCode(KeyEvent e) {
		_displayArea.append(_NEW_LINE + "KeyCode: " + e.getKeyCode());
		_displayArea.append(_NEW_LINE + "root@arcticarover:~# ");
		_displayArea.setCaretPosition(_displayArea.getDocument().getLength());
	}

	//==================================================================
	// INNER CLASSES
	//==================================================================

}
