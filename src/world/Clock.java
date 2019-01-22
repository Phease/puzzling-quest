package world;

import networking.Server.ClientHandler;
import ui.ApplicationWindow;

/**
 * A simple thread which sends a message to all the clients every 10 seconds.
 * @author hammatalex 300327355 Alexander Hammatt
 *
 */
public class Clock extends Thread
{
	private ClientHandler ch = null;
	private ApplicationWindow aw = null;

	private boolean ticking;

	/**
	 * constructor for use of the server, gives it a client which can broadcast the information
	 * @param ch the client handler to send ticks to.
	 */
	public Clock(ClientHandler ch)
	{
		this.ch = ch;
		this.ticking=false;
	}

	/**
	 * for single player use, is given an application window instead of a client hander so that it can send ticks to the client direclty.
	 * @param aw the application window to send ticks to.
	 */
	public Clock(ApplicationWindow aw)
	{
		this.aw = aw;
		this.ticking=false;
	}

	/**
	 * every 10 seconds tell the client handler to send a tick to each connected client
	 */
	@Override
	public void run()
	{
		this.ticking = true;
		while (true)
		{
			try
			{
				Thread.sleep(10000);
				if (ch!=null) ch.broadcast("clockTick", null);
				else if (aw!=null)aw.clockTick();
			}
			catch (InterruptedException e)
			{
				System.out.println("Server Error: Clock thread could not sleep");
			}
		}
	}

	/**
	 * the thread will only be ticking after start()/run() has been called
	 * @return if the thread is ticking
	 */
	public boolean isTicking()
	{
		return ticking;
	}
}