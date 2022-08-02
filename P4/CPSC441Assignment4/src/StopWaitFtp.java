

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.logging.*;

public class StopWaitFtp {
	
	private static final Logger logger = Logger.getLogger("StopWaitFtp"); // global logger

	private int timeoutInterval;
	private DatagramSocket udpSocket;

	/**
	 * Constructor to initialize the program 
	 * 
	 * @param timeout		The time-out interval for the retransmission timer
	 */
	public StopWaitFtp(int timeout) throws FtpException{
		this.timeoutInterval = timeout;
		try {
			udpSocket = new DatagramSocket();
		}catch(IOException e){
			throw new FtpException("error initializing new udp socket");
		}
	}


	/**
	 * Send the specified file to the specified remote server
	 * 
	 * @param serverName	Name of the remote server
	 * @param serverPort	Port number of the remote server
	 * @param fileName		Name of the file to be trasferred to the rmeote server
	 * @throws FtpException If anything goes wrong while sending the file
	 */
	public void send(String serverName, int serverPort, String fileName) throws FtpException{

		FileHandle fh = new FileHandle(fileName); //create a new file handler
		FileInputStream fin = fh.getFileInputStream(); //get the input stream to the file

		byte[] payload = new byte[FtpSegment.MAX_PAYLOAD_SIZE]; //array tp get payload

		//establish connection by handshaking
		HandshakeHandle handshaker = new HandshakeHandle(udpSocket.getLocalPort()); //handshake with server
		handshaker.handShake(serverName, serverPort, fileName);
		int seqNum = handshaker.getInitialSeqNum(); //set the obtained seq num from handshake

		Timer t = new Timer(); //create a timer
		TimerHandle th = new TimerHandle(udpSocket); //create a timer task
		t.scheduleAtFixedRate(th,timeoutInterval, timeoutInterval); //schedule the timeout of the timer task (periodic)

		try {
			int readBytes = 0;
			while ((readBytes = fin.read(payload)) != -1) { //while theres remaining data in the file

				//construct packet
				FtpSegment seg = new FtpSegment(seqNum, payload, readBytes); //make a segment
				DatagramPacket pkt = FtpSegment.makePacket(seg, InetAddress.getByName(serverName), handshaker.getUPDPort()); //turn segment into datagram

				DatagramPacket returnPkt = FtpSegment.makePacket(new FtpSegment(), InetAddress.getByName(serverName), handshaker.getUPDPort()); //make dummy datagram to save ack
				//send the packet
				udpSocket.send(pkt); //send the datagram
				System.out.println("send " + seg.getSeqNum()); //print the sequence number of sent datagram
				//start timer and save version of the packet
				th.setRetransPkt(pkt); //set the sent datagram in case of retransmission

				//listen for ack
				udpSocket.receive(returnPkt); //recieve the ack

				FtpSegment returnSeg = new FtpSegment(returnPkt); //extract received datagram info
				System.out.println("ack " + returnSeg.getSeqNum()); //print the seq num of ack

				while(ackCheck((seqNum + 1), returnSeg.getSeqNum()) == false){ //check if ack is as expected
					//returnSeg = null;
					udpSocket.receive(returnPkt); //keep receiving as long as the ack is not seqnum + 1
					returnSeg = new FtpSegment(returnPkt); //extract data
					System.out.println("ack " + returnSeg.getSeqNum()); //print the ack

					//if no ack is received, timeout and retransmission occurs

				}

				seqNum = returnSeg.getSeqNum(); //set the sequence number of the next datagram

			}

			fin.close(); //close the file input stream

		}catch (IOException e){
			throw new FtpException("error regarding udp I/O");
		}



		udpSocket.close(); //close the socket
		shutDownTimer(t); //stop the timer
	}

	/**
	 * shuts down the timer scheduled
	 * @param t the timer
	 */
	public void shutDownTimer(Timer t){
		t.cancel();
		t.purge();
	}

	/**
	 * check the ack
	 * @param expectedAck the ack required to send next datgram
	 * @param actualAck the actual ack received
	 * @return true if the ack is as expected
	 */
	public boolean ackCheck(int expectedAck, int actualAck){
		return expectedAck == actualAck;
	}



} // end of class