
/**
 * GzipClient Class
 * 
 * CPSC 441
 * Assignment 1
 * 
 *
 */

import java.io.*;
import java.net.Socket;
import java.util.logging.*;


public class GzipClient {

	private static final Logger logger = Logger.getLogger("GzipClient"); // global logger

	private Socket socket; //tcp socket connection

	private String serverName; //name of the server
	private int serverPort; //port number of the server
	private int bufferSize;

	/**
	 * Constructor to initialize the class.
	 * 
	 * @param serverName	remote server name
	 * @param serverPort	remote server port number
	 * @param bufferSize	buffer size used for read/write
	 */
	public GzipClient(String serverName, int serverPort, int bufferSize){

		this.serverName = serverName; //destination server name
		this.serverPort = serverPort; ///destination server port both uses to open the socket
		this.bufferSize = bufferSize; //set the buffer size

	}

	/**
	 * Compress the specified file via the remote server.
	 * 
	 * @param inName		name of the input file to be compressed
	 * @param outName		name of the output compressed file
	 */
	public void gzip(String inName, String outName){

		establishConnectionToServer(); //establishes a tcp connection to the server

		Thread readFromFile = new Thread(new WriteToSocket(this.socket, inName, bufferSize)); //creates the thread that reads file and writes it to socket
		Thread writeToFile = new Thread(new ReadFromSocket(this.socket, outName, bufferSize)); //creates thread that reads socket and writes to a file

		readFromFile.start(); //starts the file reading thread
		writeToFile.start(); //starts the file writing thread

		try{
			readFromFile.join(); //waits for file reading thread to finish execution
			writeToFile.join(); //waits for file writing thread to finish execution
		}
		catch(InterruptedException e){
			System.out.println("Thread was interrupted."); //catches io exception and prints error message
		}
		finally{
			disconnectFromServer(); //closes the tcp connection to the server
		}
	}

	/**
	 * establishes a tcp connection to the server
	 *
	 * @throws IOException
	 */
	public void establishConnectionToServer() {

		try{
			socket = new Socket(serverName, serverPort); //open a socket to the server
		}catch(IOException e){
			System.out.println("There was an error while trying to connect to the server."); //throws io exception if there is an issue opening the socket
		}
	}

	/**
	 * disconnects from the server and closes all sockets and pipes
	 *
	 * @throws IOException
	 */

	public void disconnectFromServer() {

		try{
			socket.close(); //close the socket
		}catch(IOException e){
			System.out.println("Error closing socket output stream"); //throws io exception if there is an issue closing the socket
		}
	}

}
