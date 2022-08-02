
/**
 * GoBackFtp Class
 * 
 * GoBackFtp implements a basic FTP application based on UDP data transmission.
 * It implements a Go-Back-N protocol. The window size is an input parameter.
 * 
 * @author 	Majid Ghaderi
 * @version	2021
 *
 */


import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.logging.*;


public class GoBackFtp {
	// global logger	
	private static final Logger logger = Logger.getLogger("GoBackFtp");

	private int windowSize;

	private static DatagramSocket udpSocket;

	private static FtpTimer timer;
	private static TimeoutHandle timerTask;

	private static boolean sendRunning;

	/**
	 * Constructor to initialize the program 
	 * 
	 * @param windowSize	Size of the window for Go-Back_N in units of segments
	 * @param rtoTimer		The time-out interval for the retransmission timer
	 */
	public GoBackFtp(int windowSize, int rtoTimer) throws FtpException{
		this.windowSize = windowSize;

		try{
			this.udpSocket = new DatagramSocket();
			//this.udpSocket.setSoTimeout(rtoTimer);

			//create the TimerTask
			timerTask = new TimeoutHandle(udpSocket);
			timer = new FtpTimer(timerTask, rtoTimer);

		}catch(IOException e){
			throw new FtpException("Error opening the ftp socket");
		}


	}

	/**
	 * Send the specified file to the specified remote server
	 * 
	 * @param serverName	Name of the remote server
	 * @param serverPort	Port number of the remote server
	 * @param fileName		Name of the file to be trasferred to the rmeote server
	 * @throws FtpException If unrecoverable errors happen during file transfer
	 */
	public void send(String serverName, int serverPort, String fileName) throws FtpException{

		HandshakeHandle hsHandle = new HandshakeHandle(udpSocket.getLocalPort());

		SingletonTransmissionQueue queue = SingletonTransmissionQueue.getInstance();

		//handshake with the server
		File toSend = new File(fileName);
		if(toSend.exists()){
			hsHandle.handShake(serverName, serverPort, fileName);
		}

		//configure the sender and receiver threads
		SenderThread sender = new SenderThread(toSend, udpSocket, hsHandle.getInitialSeqNum(), serverName, hsHandle.getUPDPort(), windowSize);
		ReceiverThread rec = new ReceiverThread(udpSocket, serverName, hsHandle.getUPDPort());

		//start threads and set flags
		sender.start();
		sendRunning = true;

		rec.start();

		//wait for threads to complete excecution
		try {
			sender.join();
			rec.join();
		}catch (InterruptedException e){
			throw new FtpException("Error completing thread execution");
		}

		udpSocket.close(); //close the udp socket

	}

	/**
	 * stops the timer
	 */
	public static synchronized void timerStop(){
		timer.stop();
	}

	/**
	 * shuts down the timer
	 */
	public static void shutDown() {
		timer.shutdown();

	}

	/**
	 * starts the timer
	 */
	public synchronized static void timerStart(){
		timer.start();
	}

	/**
	 * restarts the timer
	 */
	public synchronized static void timerReset(){
		timer.restart();
	}

	/**
	 * changes the sendRunning flag to false
	 */
	public synchronized static void doneRunning(){
		sendRunning = false;
	}

	/**
	 * returns the status of the sendRunning flag
	 */
	public synchronized static boolean sendRunning(){
		return sendRunning;
	}

} // end of class