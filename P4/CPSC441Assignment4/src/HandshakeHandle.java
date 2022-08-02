import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HandshakeHandle {

    private Socket tcpSocket;

    private DataInputStream in; //reads from socket
    private DataOutputStream out; //writes to socket

    private int localUDPPort; //port number of clients udp port

    private int serverUDPPort; //port number of the servers udp port
    private int initialSeqNum; //sequence number of first datagram

    /**
     * constructs new filehandle object
     * @param localPortudp the local udp port number
     */
    public HandshakeHandle(int localPortudp){
        this.localUDPPort = localPortudp;
    }

    /**
     *
     * @return the servers udp port number
     */
    public int getUPDPort(){
        return this.serverUDPPort;
    }

    /**
     *
     * @return the sequence number of first datagram to be transmitted
     */
    public int getInitialSeqNum(){
        return this.initialSeqNum;
    }

    /**
     * opens a tcp connection to the server to exchange "handshake message"
     * @param serverName the name of the server
     * @param serverPort the port number to connect to
     * @param fileName the name of the file to be sent
     */
    public void handShake(String serverName, int serverPort, String fileName) throws FtpException{

        //preparing the socket to read and write
        prepareTCPSocket(serverName, serverPort);

        //turning fileName into utf encoded string and getting its length
        long length = getFileLength(fileName);

        //send the relevant info through tcp socket
        sendFileInfo(fileName, length);
        //receive the udp port and the initial sequence number
        receiveUDPInfo();

        //close input stream and socket
        cleanUp();

    }

    private void prepareTCPSocket(String server, int port) throws FtpException{
        try{
            tcpSocket = new Socket(server, port); // opens a tcp connection to the server
            in = new DataInputStream(tcpSocket.getInputStream()); // open socket input stream
            out = new DataOutputStream(tcpSocket.getOutputStream()); //open socket output stream
        }catch(IOException e){
            throw new FtpException("error opening I/O to socket");
        }
    }

    /**
     * gets the length of the specified file
     * @param fileName name of the file
     * @return the length of the file
     */
    private long getFileLength(String fileName){
        File theFile = new File(fileName); //create new file
        return theFile.length(); //get its length
    }

    /**
     * send the info regarding file to the server so that it knows what to expect
     * @param fileName name of the file
     * @param length length pf the file
     */
    private void sendFileInfo(String fileName, long length) throws FtpException{

        try{
            out.writeUTF(fileName); //write filename as utf encoded string
            out.flush(); //flush the output stream
            out.writeLong(length); //write the length of the file
            out.flush(); //flush the output stream
            out.writeInt(localUDPPort); //write the local udp port number
            out.flush(); //flush the output stream
            tcpSocket.shutdownOutput(); //shutdown output to the tcp socket
        }catch(IOException e){
            throw new FtpException("error writing to tcp socket");
        }
    }

    /**
     * gets the essential server side udp info
     */
    private void receiveUDPInfo() throws FtpException{
        try{
            this.serverUDPPort = in.readInt(); //the server's usp port number
            this.initialSeqNum = in.readInt(); // the initial sequence number

        }catch(IOException e){
            throw new FtpException("error reading from tcp socket");
        }
    }

    /**
     * closes the streams and the socket when handshake is completed
     */
    private void cleanUp() throws FtpException{
        try{
            in.close(); //close the iput stream
            tcpSocket.close(); //close the socket
        }catch(IOException e){
            throw new FtpException("error closing socket");
        }
    }
}
