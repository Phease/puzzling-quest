package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import world.Clock;

/**
 * This class represents a server which handles up to four clients.
 * the server maintains the connections to each client through a client handler which maintains each one as a new thread.
 * every time the server receives a message from a client, it will broadcast the data to all other connected clients
 * @author hammatalex 300327355 Alexander Hammatt
 */
public class Server extends Thread
{
	private int portNumber;
	private ServerSocket serverSocket;
	private ClientHandler clientHandler;

	private boolean running = false;


	/**
	 * method will accept up to 4 connections and maintain them as clients.
	 * each time that a client sends a message (a change) the server will then send that change to all other clients.
	 */
	private void acceptConnections()
	{
		try
		{
			clientHandler.newClient(serverSocket.accept());
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, "Server Error: Error handling client socket. " + e.getMessage());
		}
	}

	/**
	 * create the clienthandler and ask for the port
	 */
	public Server()
	{

		clientHandler = new ClientHandler();

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

		this.portNumber = port;

		try
		{
			//create a server socket on the port number passed in
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Server Started on Port "+portNumber);

			this.running = true;
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, "Server Error: could not create socket");
		}
	}

	/**
	 * will permanently accept connections while the amount of clients is less then 4 otherwise will just sleep
	 */
	@Override
	public void run()
	{
		while (running)
		{
			if (this.serverSocket!=null && this.clientHandler.clientCount<4)
			{//if server socket exists and client handler has less than 4 clients we are ready to accept a new connection
				acceptConnections();
			}
			else
			{//otherwise we must sleep so we do not consume resources
				try
				{
					sleep(100000);
				}
				catch (InterruptedException e)
				{
					this.running = false;
					JOptionPane.showMessageDialog(null, "Server Error: could not sleep");
				}
			}
		}
	}

	public boolean isRunning()
	{
		return running;
	}

	/**
	 * This class handles all of the clients inside of it and handles the re-broadcasting of recieved messages.
	 * @author hammatalex 300327355 Alexander Hammatt
	 */
	public class ClientHandler
	{
		private Client[] clients;
		private Clock clock = null;
		private int clientCount = 0;
		private boolean gameStarted = false;

		/**
		 * make space for up to 4 clients.
		 */
		private ClientHandler()
		{
			clients = new Client[4];//max 4 clients
			//create a clock to send clock updates to the clients
			clock = new Clock(this);
		}


		/**
		 * create a new client as a thread which we can broadcast to.
		 * @param socket - the socket that the client is using.
		 */
		private void newClient(Socket socket)
		{
			if (!gameStarted)
			{//new connections will only be turned into clients if the game has not started yet.
				clients[clientCount] = new Client(socket, this);
				clients[clientCount].start();
				System.out.println("Client Number " + clientCount + " Connected on Port "+portNumber);
				clients[clientCount].send("newClient,"+clientCount);//send out the players ID to them
				clientCount++;
			}
			else
			{//the game has started to shut down input to the socket and close it so that it knows it is not being used.
				try
				{
					socket.shutdownInput();
					socket.shutdownOutput();
					socket.close();
				}
				catch (IOException e)
				{

				}
			}
		}

		/**
		 * @param c - the client which wants to get it's ID
		 * @return the position in the array of that client or -1 if client not found
		 */
		private int getClientId(Client c)
		{
			for (int i = 0; i < clientCount; i++)
			{
				if (clients[i]==c) return i;
			}
			return -1;
		}

		/**
		 * disconnect the client given by telling it to disconnect and then removing the reference.
		 * @param c - the client to disconnect.
		 */
		private void disconnectClient(Client c)
		{
			for (int i = 0; i < clientCount; i++)
			{
				if (clients[i]==c)
				{
					clients[i].disconnect();
					clients[i]=null;
				}
			}
		}


		/**
		 * broadcast the data to all clients except for the source client.
		 * @param data - the data to be broadcasted
		 * @param source - the source of the data
		 */
		public void broadcast(String data, Client source)
		{

			if (source==null)
			{
				System.out.println("Clock Tick");
				for (int i = 0; i < clientCount; i++)
				{
					if (clients[i]!=null) clients[i].send(data);
				}
			}
			else if (data.equals("disconnect"))
			{
				this.disconnectClient(source);
			}
			else if(data.equals("gameStarted"))
			{
				this.gameStarted = true;//new players cannot connect once the game has started
				if (!clock.isTicking()) this.clock.start();//start ticking the clock, thiss will cause the clients to recieve a clock tick every 10 seconds.
			}
			else if (data.equals("Client connected.")) ;//do nothing, client sends this as a test to make sure it can send data.
			else if (data.equals("checkalive")) ;//do nothing, the client sends this to make sure that it is still connected properly.
			else
			{
				System.out.println("server broadcasting");
				for (int i = 0; i < clientCount; i++)
				{
					if (clients[i]!=source && clients[i]!=null)
					{
						clients[i].send(data);
					}
				}
			}
		}


	}

	/**
	 * the Client represents the client from the servers perspective, it listens for information and can send information back.
	 * @author hammatalex 300327355 Alexander Hammatt
	 */
	private class Client extends Thread
	{
		private Socket socket;
		private ClientHandler clientHandler;//store the client handler so we can send out information we receive
		private BufferedReader serverInput;
		private boolean connected = false;

		/**
		 * create a representation of a client.
		 * @param socket - the socket that the client is connected through.
		 * @param clientHandler - this clients client handler which it will send messages to.
		 */
		private Client (Socket socket, ClientHandler clientHandler)
		{
			this.socket = socket;
			this.clientHandler = clientHandler;
			try
			{
				serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch(IOException e)
			{
				JOptionPane.showMessageDialog(null, "Server Error: could not create buffered reader to listen to client");
			}
			this.connected = true;
		}

		/**
		 * Sends data to the client represented by this object.
		 * @param data - the string data to send
		 */
		private void send(String data)
		{
			try
			{
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println(data);
				data = null;
			}
			catch(IOException e)
			{
				JOptionPane.showMessageDialog(null, "Server Error: could not send buffer containing: " + new String(data));
			}
		}

		/**
		 * method called when client disconnects so that the server stops listening for it.
		 */
		public void disconnect()
		{
			connected=false;
		}

		/**
		 * Listen for data from clients.
		 * when data is received, tell the client handler to re-broadcast the information.
		 */
		@Override
		public void run()
		{
			while (connected)
			{

				String inputLine = null;
				int timeout = 0;
				try
				{
					timeout = 0;
					if ((inputLine = serverInput.readLine()) != null) //this line will block until server receives data
					{//some data has been sent so we have to make sure all the other clients know
						System.out.println("Server Recieved "+inputLine+" from client "+clientHandler.getClientId(this));
						clientHandler.broadcast(inputLine, this);
					}
				}
				catch(IOException e)
				{
					System.out.println("Server Error: could not recieve data, tried " + timeout + " times");
					timeout++;
					if (timeout>10) this.clientHandler.disconnectClient(this);
				}
			}
		}
	}
}
