package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;

import world.Clock;
import networking.ClientConnection;
import networking.Server;

/**
 * The MainWindow is where the players begin the game.
 * They can start the server and start a client to join a running server.
 * Once the game has begun, the server will not accept new connections.
 * @author pheaseedwi 300315747 Edwin Phease
 * @author hammatalex 300327355 Alexander Hammatt - minor tweaks only.
 *
 */
public class MainWindow {

	private JFrame frame;
	private ApplicationWindow appWindow;
	private ClientConnection serverClient;
	private JButton multiplayerButton;

	private static final String gameTitle = "Puzzling Quest";
	private static final int frameWidth = 300;
	private static final int frameHeight = 230;

	public MainWindow() {
		initialiseWindow();
	}

	/**
	 * Initialise the display of the game, by creating the JFrame,
	 * and adding the buttons and text display.
	 */
	public void initialiseWindow() {

		frame = new JFrame();
		frame.setTitle(gameTitle);
		frame.setSize(frameWidth, frameHeight);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		//make frame display confirmation on close
	    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
	        @Override
	        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
	        	exit(appWindow, frame);
	        }
	    });

		//put frame in centre of screen
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    frame.setLocation(dim.width/2-frame.getSize().width/2,
	    		dim.height/2-frame.getSize().height/2);

	    //Loading message to display while game is loading
	    JPanel loadingPanel = new JPanel();
	    loadingPanel.setLayout(new FlowLayout());
		JTextField loading = new JTextField("Loading game...");
		loading.setEditable(false);
		loadingPanel.add(loading);
		frame.add(loadingPanel);
		frame.setVisible(true);

		appWindow = new ApplicationWindow();
		frame.setVisible(false);
		frame.remove(loadingPanel);

		addMenu(frame,appWindow);

		//add the buttons to the frame, each in their own panel
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		JButton serverButton = new JButton("Start server");
		serverButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Server server = new Server();
				if (server!=null) server.start();
				if (server.isRunning()) {
					serverButton.setEnabled(false);
					serverButton.setText("Server started");
				}
			}
		});
		buttonPanel.add(serverButton);
		frame.add(buttonPanel);

		JPanel buttonPanel2 = new JPanel();
		buttonPanel2.setLayout(new FlowLayout());
		JButton clientButton = new JButton("Start client");
		clientButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				serverClient = new ClientConnection(appWindow);
				if (serverClient!=null) serverClient.start();

				//connecting
				clientButton.setEnabled(false);
				clientButton.setText("Connecting...");
				try {
					//wait for the client to be able to check if the connection has actually worked
					Thread.sleep(200);
				}
				catch (Exception e){}

				/*If it successfully connected, enable use of Multiplayer button
					and assign client to Application Window.*/
				if (serverClient.isConnected()) {
					multiplayerButton.setEnabled(true);
					clientButton.setEnabled(false);
					clientButton.setText("Client connected");
					appWindow.setClient(serverClient);
				}
				/*The creation of the client has failed so we have to re-enable the button
					with the original text in case the user wants to try again. */
				else {
					clientButton.setEnabled(true);
					clientButton.setText("Start client");
				}
				frame.repaint();
			}
		});
		buttonPanel2.add(clientButton);
		frame.add(buttonPanel2);

		JPanel buttonPanel3 = new JPanel();
		buttonPanel3.setLayout(new FlowLayout());
		JButton singleplayerButton = new JButton("Start singleplayer game");
		singleplayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false);
				if (serverClient!=null) {
					serverClient.send("disconnect");
				}
				appWindow.setClient(null);
				Clock clock = new Clock(appWindow);
				clock.start();
				appWindow.initialiseDisplay();
			}
		});
		buttonPanel3.add(singleplayerButton);
		frame.add(buttonPanel3);

		JPanel buttonPanel4 = new JPanel();
		buttonPanel4.setLayout(new FlowLayout());
		multiplayerButton = new JButton("Start multiplayer game");
		multiplayerButton.setEnabled(false);
		multiplayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false);
				if (serverClient!=null) {
					serverClient.send("gameStarted");
				}
				appWindow.initialiseDisplay();
			}
		});
		buttonPanel4.add(multiplayerButton);
		frame.add(buttonPanel4);
		
		

		frame.setVisible(true);
	}

	/**
	 * Statically add the JMenuBar and its items to a given frame.
	 * Items include the Exit button and the Help buttons.
	 * @param frame
	 * @param window
	 */
	public static void addMenu(JFrame frame, ApplicationWindow window) {
		JMenuBar menubar = new JMenuBar();
	    //make menubar appear above canvas
	    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	    ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

	    JMenu file = new JMenu("File");
	    JMenuItem exit = new JMenuItem("Exit");
	    exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exit(window, frame);
			}
		});
	    file.add(exit);
	    menubar.add(file);

	    //A list of all the helpItems to be displayed from buttons.
	    String[] helpItems = new String[]{"All help","","Starting the game","Startup",
	    		"Movement","Movement","Actions","Actions","Credits","Credits",
	    		"Walkthrough","Walkthrough"};
	    JMenu helpMenu = new JMenu("Help");

	    for (int i=0; i<helpItems.length-1; i=i+2) {
	    	JMenuItem helpItem = new JMenuItem(helpItems[i]);
	    	String s = helpItems[i+1];
	    	helpItem.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent arg0) {
	    			helpPart(s,frame);
	    		}
	    	});
	    	helpMenu.add(helpItem);
	    }

	    menubar.add(helpMenu);

	    frame.setJMenuBar(menubar);
	}

	/**
	 * Loads in the help message from the README file, and displays it over the frame.
	 * If section is blank, show all help, otherwise show only the part headed by -section-
	 * @param section
	 * @param frame
	 */
	private static void helpPart(String section, JFrame frame)
	{
		String helpMessage = "";
		try
		{
			Scanner scan = new Scanner(new File("readme.txt"));
			/*skip through lines until we find one with -section- to denote the
			 * start of the section, or if section is empty, show all text */
			while (!section.equals("")&&!scan.hasNext("-"+section+"-")) scan.nextLine();
			scan.nextLine();
			/*from the start of this section, each line until we see another "-section-"
				is part of this section of the readme*/
			do
			{
				helpMessage+=scan.nextLine();
				helpMessage+="\r\n";
			} while (scan.hasNext() && !scan.hasNext("-"+section+"-"));
			scan.close();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(frame, "Error reading readme.txt");
		}
		//make text scrollable
		JTextArea textArea = new JTextArea(15,40);
	    textArea.setLineWrap(true);
	    textArea.setWrapStyleWord(true);
	    textArea.setText(helpMessage);
	    textArea.setCaretPosition(0);
	    textArea.setEditable(false);
	    JScrollPane scrollPane = new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JOptionPane.showMessageDialog(frame, scrollPane, gameTitle,
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * After confirming with the user, disconnect from the server and exit the game.
	 * @param window
	 * @param frame
	 */
	public static void exit(ApplicationWindow window, JFrame frame) {
        if (JOptionPane.showConfirmDialog(frame,
            "Are you sure you wish to close Puzzling Quest?", gameTitle,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
        	if (window!=null) window.command("disconnect");
            System.exit(0);
        }
	}

	public static void main(String[] args) {
		new MainWindow();
	}

}
