package com.arctica.rover.suite.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.arctica.rover.suite.controller.RoverController;
import com.arctica.rover.suite.utils.UIUtils;

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
	private RoverController _roverController;
	private Timer _pingTimer;
	private Boolean [] _keys;
	private Integer _camera0LL;
	private Integer _camera0UL;
	private String _loadingBar;

	private JTextArea _displayArea;
	private JComboBox<String> _ipAddress;
	private JButton _exit;
	private JButton _submit;
	private JButton _back;
	private JButton _refresh;
	private JLabel _loadingLabel;

	//==================================================================
	// CONSTRUCTORS
	//==================================================================
	public RoverUI(String name) {
		setLocation(475, 220);
		setLayout(new BorderLayout());
		setTitle(name);
		_camera0LL = 17;
		_camera0UL = 90;
		_keys = new Boolean[256];
		_loadingBar = "|";
		setVisible(true);

		setResizable(false);

		setContentPane(loadingView());
		load();
		
		setContentPane(connectView());
		revalidate();
		repaint();
		
	}

	//==================================================================
	// PUBLIC METHODS
	//==================================================================
	public void actionPerformed(ActionEvent event) {
		if( event.getSource() == _exit) {
			System.exit(0);
		} else if(event.getSource() == _submit) {
			initializeRoverSuite((String)_ipAddress.getSelectedItem());
			setContentPane(controlView());
			revalidate();
			repaint();
		} else if(event.getSource() == _back) {
			setContentPane(connectView());
			revalidate();
			repaint();
		} else if(event.getSource() == _refresh) {
			refreshIPs();
		}
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
			} else if(e.getKeyCode() == _AKey) {
				_roverController.stop();
				_roverController.left();
				displayKey(e, "-arcticarover: Turning Left: ");
			} else if(e.getKeyCode() == _SKey) {
				_roverController.stop();
				_roverController.backward();
				displayKey(e, "-arcticarover: Going Backward: ");
			} else if(e.getKeyCode() == _DKey) {
				_roverController.stop();
				_roverController.right();
				displayKey(e, "-arcticarover: Turning Right: ");
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
	private Container loadingView() {
		final Container result = new JPanel();
		setSize(350, 150);
		
		JPanel labelPanel = new JPanel();
		JLabel label = new JLabel("Scanning Network...");
		labelPanel.add(label);
		
	    _loadingLabel = new JLabel();
		_loadingLabel.setText(_loadingBar);
		
		result.add(labelPanel, BorderLayout.PAGE_START);
		result.add(_loadingLabel, BorderLayout.PAGE_END);
		
		return result;
	}
	
	private Container connectView() {
		final Container result = new JPanel();
		result.setLayout(new GridLayout(3,1));

		setSize(350, 150);

		_exit = new JButton("Exit");
		_exit.addActionListener(this);

		_submit = new JButton("Submit");
		_submit.addActionListener(this);
		
		_refresh = new JButton("Refresh");
		_refresh.addActionListener(this);

		JLabel label = new JLabel();
		label.setText(" Please select the ip address of your raspberry pi.");

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(_exit);
		buttonPanel.add(_submit);	

		_ipAddress = new JComboBox<String>();
		_ipAddress.setPreferredSize(new Dimension(200, 20));
		for(String s : UIUtils.getIPAddressValues()) {
			_ipAddress.addItem(s);
		}
		
		JPanel ipPanel = new JPanel();
		ipPanel.add(_ipAddress);
		ipPanel.add(_refresh);
		

		result.add(label, BorderLayout.CENTER);
		result.add(ipPanel);
		result.add(buttonPanel);

		result.setVisible(true);
		return result;
	}

	/**
	 * @param name
	 */
	private Container controlView() {
		final Container result = new JPanel();

		setSize(500, 360);

		_back = new JButton("Change IP");
		_back.addActionListener(this);

		_exit = new JButton("Exit");
		
		_displayArea = new JTextArea();
		_displayArea.setEditable(false);
		
		JTextArea pane = new JTextArea(_NEW_LINE + " Enter Commands:  w,a,s,d  for direction & 1-9 for speed " + _NEW_LINE);
		JScrollPane scrollPane = new JScrollPane(_displayArea);

		pane.setEditable(false);
		pane.addKeyListener(this);
		scrollPane.setPreferredSize(new Dimension(450, 230));
		scrollPane.addKeyListener(this);

		

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(_back);
		buttonPanel.add(_exit);

		_exit.addActionListener(this);
		result.add(pane, BorderLayout.PAGE_START);
		result.add(scrollPane, BorderLayout.CENTER);
		result.add(buttonPanel, BorderLayout.PAGE_END);

		_displayArea.append("Welcome to the Arctica Rover Control Center");
		_displayArea.append("\nroot@arcticarover:~# ");

		result.setVisible(true);

		return result;
	}

	private void initializeRoverSuite(String ipAddress) {
		
		for (int i = 0; i < _keys.length; i++) {
			_keys[i] = Boolean.FALSE;
		}
		
		_roverController = new RoverController(ipAddress);

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
	
	private void refreshIPs() {
		_ipAddress.removeAllItems();
		for(String s : UIUtils.getIPAddressValues()) {
			_ipAddress.addItem(s);
		}
	}
	
	private void load() {
		for(int i = 0; i < 30; i++) {
			_loadingBar += "|";
			_loadingLabel.setText(_loadingBar);
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(_loadingBar);
			revalidate();
			repaint();
		}
	}
	
	private void displayKey(KeyEvent e, String s) {
		char id = e.getKeyChar();
		String keyStatus = s;
		_displayArea.append(keyStatus + id);
		_displayArea.append(_NEW_LINE + "root@arcticarover:~# ");
		_displayArea.setCaretPosition(_displayArea.getDocument().getLength());
	}

	@SuppressWarnings("unused")
	private void displayKeyCode(KeyEvent e) {
		_displayArea.append(_NEW_LINE + "KeyCode: " + e.getKeyCode());
		_displayArea.append(_NEW_LINE + "root@arcticarover:~# ");
		_displayArea.setCaretPosition(_displayArea.getDocument().getLength());
	}

	//==================================================================
	// INNER CLASSES
	//==================================================================

}
