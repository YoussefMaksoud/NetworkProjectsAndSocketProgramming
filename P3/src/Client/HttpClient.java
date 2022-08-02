package Client;
/**
 * HttpClient Class
 * 
 * CPSC 441
 * Assignment 2
 * 
 */


import java.io.*;
import java.net.Socket;
import java.util.logging.*;

public class HttpClient {

	private static final Logger logger = Logger.getLogger("HttpClient"); // global logger

	private Socket theSocket;
	private InputStream inStream;
	private OutputStream outStream;

	private ParsingHelper p;

	private byte[] buffer;

    /**
     * Default no-arg constructor
     */
	public HttpClient() {
		// nothing to do!
	}
	
    /**
     * Downloads the object specified by the parameter url.
	 *
     * @param url	URL of the object to be downloaded. It is a fully qualified URL.
     */
	public void get(String url){

		p = new ParsingHelper(url);

		establishConnection(); //parse url and establish connection

		sendGetRequest(); //send the get request

		StringBuffer response = new StringBuffer(); //string buffer to save the http header into for parsing
		FileWriter fw = new FileWriter(p.getObjectName()); //creates a helper class instance
		int readBytes; //bytes read from the socket or from the file

		try{

			while(response.indexOf("\r\n\r\n") == -1) { //reads the http header and saves the header into a string buffer
				readBytes = inStream.read(buffer, 0, 1);
				for (int i = 0; i < readBytes; i++) {
					response.append((char)buffer[i]);
				}
			}

		}catch(IOException e){
			System.out.println("Error reading the header");
		}

		System.out.println(response.toString());

		int payload = p.extractPayload(response);

		fw.setBuff(payload); //sets the size of the buffer to be read from

		if(p.getStatusCode() == 200) {


			fw.writeToFile(theSocket, inStream, payload);
		}
		else{
			System.out.println("There was a problem with the object recieved from the server");
		}

		fw.closeStream();
		disconnectFromServer();

	}

	/**
	 *
	 * establishes connection to the server
	 */
	public void establishConnection(){
		buffer = new byte[4096]; //new buffer with fixed size
		try{
			theSocket = new Socket(p.getServerAddi(), p.getPortNum()); //open a new socket to the server address
			openStreams(); //open the IO streams for the socket
		}catch(IOException e){
			System.out.println("Error opening a socket or stream to the server");
		}
	}

	/**
	 * opens the io streams to the socket
	 * @throws IOException is there is trouble openning one of the streams
	 */
	public void openStreams() throws IOException{

		inStream = theSocket.getInputStream(); //open the input stream
		outStream = theSocket.getOutputStream(); //open the output stream

	}

	/**
	 *
	 * constructs and send an HTTP get request to the server
	 */
	public void sendGetRequest(){

		//String rawRequest = "GET " + p.getObjectPath() + "?" + " HTTP/1.0\r\n"
		//		+ "Accept: */*\r\n" + "Host: "+ p.getServerAddi() +"\r\n"
		//		+ "Connection: Close\r\n\r\n"; //the overall request for the object

		String rawRequest = "GET /" + p.getObject() + " HTTP/1.0\r\n"
				+ "Host: "+ p.getServerAddi() +"\r\n\r\n";

		try{

			outStream.write(rawRequest.getBytes("US-ASCII")); //writes the raw request to the stream
			outStream.flush(); //flushes the output stream
			theSocket.shutdownOutput(); //shuts down the output stream

		}catch(UnsupportedEncodingException e){

			System.out.println("Error converting url to byte stream");

		}catch(IOException a){

			System.out.println("Error writing to the socket");

		}
	}

	/**
	 *
	 * disconnect form the server and closes all streams in use
	 */
	public void disconnectFromServer(){

		try{
			inStream.close(); //close the input stream
			theSocket.close(); //close the socket
		}catch(IOException e){
			System.out.println("Error closing the socket to the server");
		}
	}
}
