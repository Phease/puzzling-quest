package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import ui.ApplicationWindow;

/**
 * The clients will use this class to make connections to the server.
 * it is responsible for parsing the data sent from the server back into commands and for sending data to the server to be re-broadcasted to other clients.
 * @author hammatalex 300327355 Alexander Hammatt
 * @author pheaseedwi 300315747 Edwin Phease - some server commands in run() only
 */
public class ClientConnection extends Thread
{
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;

	private boolean connected = false;

	private ApplicationWindow aw = null;//Reference to application window to make changes to game state.

	/**
	 * creates the socket connecting to an entered server address and port number
	 * creates an in and out stream for communication
	 * @param aw - the application window which this client will send instructions to.
	 */
	public ClientConnection(ApplicationWindow aw)
	{
		try
		{
			String address = JOptionPane.showInputDialog(null, "Enter server address: ", "localhost");
			Integer port = null;
			boolean portNumberEntered = false;

			while (!portNumberEntered)
			{
				try
				{
					port = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter port number between 0 and 65535: ", "12345"));
					if (port >= 0 && port <= 65535) portNumberEntered = true;
				}
				catch (NumberFormatException e)
				{

				}
			}

			socket = new Socket(address, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			this.aw = aw;

			this.connected=true;
		}
		catch(IOException e)
		{
			this.connected = false;
			JOptionPane.showMessageDialog(null, "Client Error: could not create socket to connect to server");
		}
	}

	/**
	 * Sends the string passed to the server that the client is connected to.
	 * the server will then take the data and transmit it to all other clients connected.
	 * @param data - the data to be transmitted to the server.
	 */
	public void send(String data)
	{
		out.println(data);
	}

	/**
	 * @return whether or not the client is connected to a server.
	 */
	public boolean isConnected()
	{
		return connected;
	}

	/**
	 * Receives data from the server and then parses them into commands to send to the main application.
	 */
	@Override
	public void run()
	{
		while (connected)
		{
			String fromServer = null;
			try
			{
				this.send("checkalive");//send a simple message to check the connection is still alive
				if (out.checkError()) throw new IOException();//if the connection is not still alive throw an exception
				while ((fromServer = in.readLine()) != null)//this line will block until client receives data
				{
					/*
					 * fromServer data: arg0, arg1
					 * first argument will be instruction, 2nd will be the player who has done the action
					 * possible more arguments exist for things like item ID's and positions.
					 */
					String[] arguments = fromServer.split(",");
					String instruction = arguments[0];

					switch(instruction)
					{
					case "newClient":
						aw.setPlayerID(Integer.valueOf(arguments[1]));
						break;
					case "move":
						aw.movePlayerForward(Integer.valueOf(arguments[1]));
						break;
					case "turnLeft":
						aw.turnLeft(Integer.valueOf(arguments[1]));
						break;
					case "turnRight":
						aw.turnRight(Integer.valueOf(arguments[1]));
						break;
					case "collect":
						//name of player (int), x(int), y(int)
						aw.collectItem(Integer.valueOf(arguments[1]), Integer.valueOf(arguments[2]), Integer.valueOf(arguments[3]));
						break;
					case "use":
						aw.useItem(Integer.valueOf(arguments[1]), Integer.valueOf(arguments[2]), Integer.valueOf(arguments[3]), Integer.valueOf(arguments[4]));
						break;
					case "drop":
						aw.dropItem(Integer.valueOf(arguments[1]), Integer.valueOf(arguments[2]));
						break;
					case "depositItem":
						aw.depositItem(Integer.valueOf(arguments[1]), Integer.valueOf(arguments[2]), Integer.valueOf(arguments[3]), Integer.valueOf(arguments[4]));
						break;
					case "takeItem":
						aw.takeItem(Integer.valueOf(arguments[1]), Integer.valueOf(arguments[2]), Integer.valueOf(arguments[3]), Integer.valueOf(arguments[4]));
						break;
					case "activate":
						aw.activate(Integer.valueOf(arguments[1]), Integer.valueOf(arguments[2]), Integer.valueOf(arguments[3]));
						break;
					case "useWall":
						aw.useWall(Integer.valueOf(arguments[1]), Integer.valueOf(arguments[2]), Integer.valueOf(arguments[3]));
						break;
					case "consume":
						aw.consume(Integer.valueOf(arguments[1]), Integer.valueOf(arguments[2]));
						break;
					case "clockTick":
						aw.clockTick();
						break;
					default://this should never happen!
						JOptionPane.showMessageDialog(null, "Client Error: Recieved unknown command "+instruction);
						break;
					}
				}
			}
			catch (IOException e)
			{
				this.connected = false;
				JOptionPane.showMessageDialog(null, "Client Error: could not recieve data from server. Have you or the server lost internet connection? Are you connecting to the right address? Has the game started?");
				break;
			}
		}
	}
}
