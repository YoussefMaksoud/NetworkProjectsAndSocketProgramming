import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SenderThread extends Thread {

    private File toSend; //file to be sent
    private DatagramSocket udpSocket; //server socket

    private int initialSeqNum; //the initial sequence number
    private String serverName; //name of the server
    private int udpPort; //server port
    private int windowSize; //size of the window


    /**
     * constructor
     * @param toSend
     * @param udpSocket
     * @param seqNum
     * @param server
     * @param udpPort
     * @param windowSize
     */
    public SenderThread(File toSend, DatagramSocket udpSocket, int seqNum, String server, int udpPort, int windowSize){
        this.toSend = toSend;
        this.udpSocket = udpSocket;
        this.initialSeqNum = seqNum;
        this.serverName = server;
        this.udpPort = udpPort;
        this.windowSize = windowSize;
    }

    /**
     * calls the send method this way a new FtpException can be thrown if needed
     */
    @Override
    public void run(){
        try{
            send();
        }catch (FtpException e){
            e.getMessage();
        }
    }

    /**
     * sends the file to the server
     * @throws FtpException
     */
    public void send() throws FtpException {

        byte[] payload = new byte[FtpSegment.MAX_PAYLOAD_SIZE]; //payload array

        //get the singleton instance of the transmission queue
        SingletonTransmissionQueue queue = SingletonTransmissionQueue.getInstance();

        int seqNum = initialSeqNum;

        try{
            int readBytes = 0;
            FileInputStream fin = new FileInputStream(toSend); //open file input stream

            //-------while not end of file-------//
            while((readBytes = fin.read(payload)) != -1){

                //---------read and create segment---------//
                FtpSegment seg = new FtpSegment(seqNum, payload, readBytes); //make segment to send
                DatagramPacket pkt = FtpSegment.makePacket(seg, InetAddress.getByName(serverName), udpPort); // make a datagram using the segment

                //--------wait if transmission queue is full--------//
                while(queue.getQueue().size() == windowSize){
                    Thread.yield();

                }

                //--------send the segment--------//
                udpSocket.send(pkt);

                //--------add datagram to queue--------//
                queue.getQueue().add(pkt);
                queue.getAckQueue().add(seg.getSeqNum());

                System.out.println("Sent: " + seqNum);

                //------if datagram first in queue start timer-----//
                if(queue.getQueue().size() == 1) {
                    GoBackFtp.timerStart();
                }
                seqNum ++;
            }

            fin.close(); //close the file input stream
        }catch(IOException e){
            throw new FtpException("Error reading from the file");
        }

        GoBackFtp.doneRunning(); //raise flag that thread is done running
    }
}
