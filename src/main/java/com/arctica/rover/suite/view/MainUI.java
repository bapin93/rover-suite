package com.arctica.rover.suite.view;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

import com.arctica.rover.suite.controller.RoverController;
import com.arctica.rover.suite.utils.ServiceUtils;
import com.arctica.rover.suite.utils.UIUtils;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * <p>
 * The RoverUI class provides a UI for interacting with the Arctica Rover.
 * </p>
 * 
 * @author andres
 */
@SuppressWarnings("rawtypes")
public class MainUI extends Application implements EventHandler {

	// ==================================================================
	// CONSTANTS
	// ==================================================================
	private static final String _NEW_LINE = System.getProperty("line.separator");
	private static final int _AKey = 65;
	private static final int _WKey = 87;
	private static final int _SKey = 83;
	private static final int _DKey = 68;
	private static ExecutorService _executorService;

	// ==================================================================
	// VARIABLES
	// ==================================================================
	private Stage _primaryStage;
	private Text _addressUpdated;
	private TextArea _textArea;
	private Button _exit;
	private Button _submit;
	private Button _refresh;
	private Button _changeIP;
	private ComboBox<String> _ipAddress;
	private ProgressBar _progressBar;

	private RoverController _roverController;
	private Timer _pingTimer;
	private Boolean [] _keys;

	// ==================================================================
	// CONSTRUCTORS
	// ==================================================================

	// ==================================================================
	// PUBLIC METHODS
	// ==================================================================
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			_keys = new Boolean[256];
			_executorService = ServiceUtils.getExecutorService();
			_primaryStage = primaryStage;
			// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			_primaryStage.setScene(connectView());
			_primaryStage.setResizable(false);
			_primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void handle(Event event) {
		if (event instanceof ActionEvent) {
			if (event.getSource() == _exit) {
				System.exit(0);
			} else if (event.getSource() == _submit) {
				_primaryStage.setScene(controlView());
				initializeRoverSuite((String)_ipAddress.getValue());
			} else if (event.getSource() == _changeIP) {
				_primaryStage.setScene(connectView());
			} else if (event.getSource() == _refresh) {
				_addressUpdated.setVisible(false);
				_progressBar.setVisible(true);
				refreshIPs();
			}
		} else if (event instanceof KeyEvent) {
			KeyEvent keyEvent = (KeyEvent)event;

			if (((KeyEvent) keyEvent).getEventType() == KeyEvent.KEY_PRESSED) {
				if(_keys[keyEvent.getCode().impl_getCode()] != true) {
					_keys[keyEvent.getCode().impl_getCode()] = true;
					if(_roverController.isServerOnline()){
						if(keyEvent.getCode().impl_getCode() == _WKey) {
							_roverController.stop();
							_roverController.forward();
							displayKey(keyEvent, "-arcticarover: Going Forward: ");
						} else if(keyEvent.getCode().impl_getCode() == _AKey) {
							_roverController.left();
							displayKey(keyEvent, "-arcticarover: Turning Left: ");
						} else if(keyEvent.getCode().impl_getCode() == _SKey) {
							_roverController.stop();
							_roverController.backward();
							displayKey(keyEvent, "-arcticarover: Going Backward: ");
						} else if(keyEvent.getCode().impl_getCode() == _DKey) {
							_roverController.right();
							displayKey(keyEvent, "-arcticarover: Turning Right: ");
						} else {
							_textArea.appendText(_NEW_LINE + "KeyCode: " + keyEvent.getCode().impl_getCode());
							_textArea.appendText(_NEW_LINE + "root@arcticarover:~# ");
							_textArea.positionCaret(_textArea.getLength());
						}
					} else {
						_textArea.appendText(_NEW_LINE + "-arcticarover: rover offline");
					}
				}
			} else if (((KeyEvent) keyEvent).getEventType() == KeyEvent.KEY_RELEASED) {
				if(_roverController.isServerOnline()){
					_keys[keyEvent.getCode().impl_getCode()] = false;
					if(keyEvent.getCode().impl_getCode() >= 49 && keyEvent.getCode().impl_getCode() <= 58){
						_roverController.setSpeed(Integer.valueOf(keyEvent.getText().charAt(0) - '0'));
						displayKey(keyEvent, "-arcticarover: Setting Speed: " + Integer.valueOf(keyEvent.getText().charAt(0) - '0'));
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
		}
	}

	// ==================================================================
	// PROTECTED METHODS
	// ==================================================================

	// ==================================================================
	// DEFAULT METHODS
	// ==================================================================

	// ==================================================================
	// PRIVATE METHODS
	// ==================================================================

	/**
	 * Returns a new scene for the connect view.
	 * 
	 * @return result
	 */
	@SuppressWarnings("unchecked")
	private Scene connectView() {
		BorderPane mainPane = new BorderPane();
		mainPane.setBlendMode(BlendMode.SRC_OVER);
		final Scene result = new Scene(mainPane, 450, 250);

		BorderPane centerPane = new BorderPane();
		centerPane.setPrefSize(200, 200);
		centerPane.setBlendMode(BlendMode.SRC_OVER);

		GridPane topGrid = new GridPane();
		for (int i = 0; i < 5; i++) {
			if (i == 0 || i == 4) {
				ColumnConstraints column = new ColumnConstraints(60);
				topGrid.getColumnConstraints().add(column);
			} else {
				ColumnConstraints column = new ColumnConstraints(109);
				topGrid.getColumnConstraints().add(column);
			}
		}
		for (int i = 0; i < 5; i++) {
			RowConstraints row = new RowConstraints(30);
			topGrid.getRowConstraints().add(row);
		}

		GridPane buttonGrid = new GridPane();
		for (int i = 0; i < 3; i++) {
			if (i == 0 || i == 2) {
				ColumnConstraints column = new ColumnConstraints(205);
				buttonGrid.getColumnConstraints().add(column);
			} else {
				ColumnConstraints column = new ColumnConstraints(40);
				buttonGrid.getColumnConstraints().add(column);
			}
		}
		for (int i = 0; i < 2; i++) {
			RowConstraints row = new RowConstraints(35);
			buttonGrid.getRowConstraints().add(row);
		}

		_ipAddress = new ComboBox<String>();
		_ipAddress.setPrefWidth(160);
		GridPane.setHalignment(_ipAddress, HPos.LEFT);
		_ipAddress.getItems().addAll(UIUtils.getIPAddressValues());

		_refresh = new Button("Refresh");
		_refresh.setOnAction(this);
		_refresh.setPrefWidth(160);
		GridPane.setHalignment(_refresh, HPos.RIGHT);

		_addressUpdated = new Text("Press refresh to scan network");
		_addressUpdated.setVisible(true);
		GridPane.setHalignment(_addressUpdated, HPos.CENTER);

		_progressBar = new ProgressBar();
		_progressBar.setPrefWidth(200);
		_progressBar.setVisible(false);
		GridPane.setHalignment(_progressBar, HPos.CENTER);

		_submit = new Button("Submit");
		_submit.setOnAction(this);
		_submit.setPrefSize(70, 40);
		GridPane.setHalignment(_submit, HPos.LEFT);

		_exit = new Button("Exit");
		_exit.setOnAction(this);
		_exit.setPrefSize(70, 40);
		GridPane.setHalignment(_exit, HPos.RIGHT);

		topGrid.add(_ipAddress, 1, 2, 2, 1);
		topGrid.add(_refresh, 2, 2, 2, 1);
		topGrid.add(_progressBar, 1, 4, 3, 1);
		topGrid.add(_addressUpdated, 1, 4, 3, 1);

		buttonGrid.add(_exit, 0, 0);
		buttonGrid.add(_submit, 2, 0);

		centerPane.setTop(topGrid);
		centerPane.setBottom(buttonGrid);
		mainPane.setCenter(centerPane);

		return result;
	}

	/**
	 * Returns a new scene for the control view.
	 * 
	 * @return result
	 */
	@SuppressWarnings("unchecked")
	private Scene controlView() {
		BorderPane mainPane = new BorderPane();
		mainPane.setBlendMode(BlendMode.SRC_OVER);
		final Scene result = new Scene(mainPane, 550, 410);

		result.setOnKeyPressed(this);
		result.setOnKeyReleased(this);

		BorderPane topPane = new BorderPane();
		topPane.setPrefSize(550, 196);
		topPane.setBlendMode(BlendMode.SRC_OVER);

		GridPane buttonGrid = new GridPane();
		for (int i = 0; i < 3; i++) {
			if (i == 0 || i == 2) {
				ColumnConstraints column = new ColumnConstraints(255);
				buttonGrid.getColumnConstraints().add(column);
			} else {
				ColumnConstraints column = new ColumnConstraints(40);
				buttonGrid.getColumnConstraints().add(column);
			}
		}
		for (int i = 0; i < 2; i++) {
			RowConstraints row = new RowConstraints(35);
			buttonGrid.getRowConstraints().add(row);
		}

		GridPane pictureGrid = new GridPane();
		for (int i = 0; i < 3; i++) {
			if (i == 0 || i == 2) {
				ColumnConstraints column = new ColumnConstraints(255);
				pictureGrid.getColumnConstraints().add(column);
			} else {
				ColumnConstraints column = new ColumnConstraints(40);
				pictureGrid.getColumnConstraints().add(column);
			}
		}
		for (int i = 0; i < 2; i++) {
			RowConstraints row = new RowConstraints(40);
			pictureGrid.getRowConstraints().add(row);
		}

		File wFile = new File("src/main/resources/keys/w.png");
		Image wImage = new Image(wFile.toURI().toString());
		ImageView wKey = new ImageView();
		wKey.setImage(wImage);
		wKey.setPreserveRatio(true);
		wKey.setFitHeight(35);
		GridPane.setHalignment(wKey, HPos.CENTER);

		File aFile = new File("src/main/resources/keys/a.png");
		Image aImage = new Image(aFile.toURI().toString());
		ImageView aKey = new ImageView();
		aKey.setImage(aImage);
		aKey.setPreserveRatio(true);
		aKey.setFitHeight(35);
		GridPane.setHalignment(aKey, HPos.RIGHT);

		File sFile = new File("src/main/resources/keys/s.png");
		Image sImage = new Image(sFile.toURI().toString());
		ImageView sKey = new ImageView();
		sKey.setImage(sImage);
		sKey.setPreserveRatio(true);
		sKey.setFitHeight(35);
		GridPane.setHalignment(sKey, HPos.CENTER);

		File dFile = new File("src/main/resources/keys/d.png");
		Image dImage = new Image(dFile.toURI().toString());
		ImageView dKey = new ImageView();
		dKey.setImage(dImage);
		dKey.setPreserveRatio(true);
		dKey.setFitHeight(34);
		GridPane.setHalignment(dKey, HPos.LEFT);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setPrefSize(500, 120);
		scrollPane.setMaxSize(500, 120);
		BorderPane.setAlignment(scrollPane, Pos.CENTER);
		scrollPane.setOnKeyPressed(this);

		_textArea = new TextArea();
		_textArea.setPrefSize(490, 110);
		_textArea.setTranslateX(4);
		_textArea.setTranslateY(4);
		_textArea.setWrapText(true);
		_textArea.setEditable(false);
		_textArea.setOnKeyPressed(this);

		_exit = new Button("Exit");
		_exit.setOnAction(this);
		_exit.setPrefSize(80, 40);
		GridPane.setHalignment(_exit, HPos.LEFT);

		_changeIP = new Button("Change IP");
		_changeIP.setOnAction(this);
		_changeIP.setPrefSize(80, 40);
		GridPane.setHalignment(_changeIP, HPos.RIGHT);

		pictureGrid.add(wKey, 1, 0);
		pictureGrid.add(aKey, 0, 1);
		pictureGrid.add(sKey, 1, 1);
		pictureGrid.add(dKey, 2, 1);

		buttonGrid.add(_exit, 2, 0);
		buttonGrid.add(_changeIP, 0, 0);

		scrollPane.setContent(_textArea);
		topPane.setBottom(pictureGrid);
		mainPane.setTop(topPane);
		mainPane.setCenter(scrollPane);
		mainPane.setBottom(buttonGrid);

		return result;
	}

	/**
	 * Refreshes the IP address values in the IP selector.
	 */
	private void refreshIPs() {
		UIUtils.searchLocalIPs();
		_executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					_ipAddress.getItems().clear();
					_ipAddress.getItems().addAll(UIUtils.getIPAddressValues());
					_progressBar.setVisible(false);
					_addressUpdated.setText("Scan Complete!");
					_addressUpdated.setVisible(true);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initializes the rover suite.
	 * 
	 * @param ipAddress
	 */
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
						_textArea.appendText(_NEW_LINE + "-arcticarover: rover disconnected");
						_textArea.appendText(_NEW_LINE + "root@arcticarover:~# ");
						displayOfflineStatus = false; displayOnlineStatus = true;
					}
				}
				else if(displayOnlineStatus) {
					_textArea.appendText(_NEW_LINE + "-arcticarover: rover connection successful!");
					_textArea.appendText(_NEW_LINE + "root@arcticarover:~# ");
					displayOnlineStatus = false;
					displayOfflineStatus = true;
				}
			}
		};
		_pingTimer = new Timer("Ping Server");
		_pingTimer.scheduleAtFixedRate(pingServer, 0, 3000);
	}

	/**
	 * Display for key event.
	 * 
	 * @param event
	 * @param status
	 */
	private void displayKey(KeyEvent event, String status) {
		String id = event.getCharacter();
		String keyStatus = status;
		_textArea.appendText(keyStatus + id);
		_textArea.appendText(_NEW_LINE + "root@arcticarover:~# ");
		_textArea.positionCaret(_textArea.getLength());
	}

	// ==================================================================
	// INNER CLASSES
	// ==================================================================
}
