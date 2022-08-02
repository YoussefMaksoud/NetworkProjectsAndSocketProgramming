

/**
 * WebServer Class
 * 
 * Implements a multi-threaded web server
 * supporting non-persistent connections.
 * 
 * @author 	Majid Ghaderi
 * @version	2021
 *
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;


public class WebServer extends Thread {

	private ServerSocket serverSocket;
	private int portNum;
	private ExecutorService pool;
	private boolean active;

	private static int maxPoolSize = 500;

	// global logger object, configures in the driver class
	private static final Logger logger = Logger.getLogger("WebServer");

    /**
     * Constructor to initialize the web server
     * 
     * @param port 	The server port at which the web server listens > 1024
     * 
     */
	public WebServer(int port){
		if(port > 1024) {
			this.portNum = port; // port number
			this.pool = Executors.newFixedThreadPool(maxPoolSize); //thread pool
			this.active = true; //actuve server flag

			try{
				serverSocket = new ServerSocket(port);
				serverSocket.setSoTimeout(10000); //timeout set to 10 seconds to allow enough time for a client to connect
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		else{
			System.out.println("Port Number is not within the required range.");
		}
	}

    /**
	 * Main web server method.
	 * The web server remains in listening mode 
	 * and accepts connection requests from clients 
	 * until the shutdown method is called.
	 *
     */
	public void run() {
		try {


			System.out.println("Waiting for connection");
			while (active == true) { //as long as the flag is true and unchanged by ann worker threads

				Socket workerSocket = serverSocket.accept(); //listen for new connections
				pool.execute(new WorkerThread(workerSocket, this)); //start the thread if connected
				System.out.println("Connection Established to: IP: " + workerSocket.getInetAddress().toString().replaceAll("/", "") + " Port: " + workerSocket.getPort());

			}

		}catch (SocketTimeoutException a) {
			a.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}

		shutdown(); //reached only if the flag is changed by a worker thread if "quit" is detected otherwise keep running after timeout
	}

    /**
     * Signals the web server to shutdown.
	 *
     */
	public void shutdown(){
		try{
			pool.awaitTermination(500, TimeUnit.MILLISECONDS); //wait for threads to finish
			pool.shutdownNow(); //shutdown the threadpool
			serverSocket.close(); //close the server socket
		}catch(IOException e){
			e.printStackTrace();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	/**
	 * sets the active fag to false
	 */
	public void activeFalse(){
		active = false;
	}
	
}
