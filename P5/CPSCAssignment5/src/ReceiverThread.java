
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class ReceiverThread extends Thread {

    private DatagramSocket udpSocket;

    private String serverName; //name of the server
    private int udpPort; //server udp port

    /**
     * constructor
     * @param udpSocket server socket
     * @param server server name
     * @param udpPort server port
     */
    public ReceiverThread(DatagramSocket udpSocket, String server, int udpPort){
        this.udpSocket = udpSocket;
        this.serverName = server;
        this.udpPort = udpPort;
    }

    /**
     * calls the receive method this way a new FtpException can be thrown id needed
     */
    @Override
    public void run(){
        try{
            receive();
        }catch (FtpException e){
            e.getMessage();
        }

    }

    /**
     * recieves the ack
     * @throws FtpException
     */
    public void receive() throws FtpException{
        //get singleton instance of the transmission queue
        SingletonTransmissionQueue queue = SingletonTransmissionQueue.getInstance();

        try {
            while (GoBackFtp.sendRunning() == true || !queue.getQueue().isEmpty()) { //while thread is not finished or transmission queue has elements

                try {

                    //---------receiving the ack-----------//

                    DatagramPacket returnPkt = FtpSegment.makePacket(new FtpSegment(), InetAddress.getByName(serverName), udpPort); // make a dummy packet

                    udpSocket.receive(returnPkt); //receive the packet

                    FtpSegment returnSeg = new FtpSegment(returnPkt); //get the return segment

                    System.out.println("ack: " + returnSeg.getSeqNum()); //print ack message

                    //-------if ack is valid--------//

                    if (queue.getAckQueue().contains((returnSeg.getSeqNum() - 1))) {

                        //-------stop the timer-------//
                        GoBackFtp.timerStop();

                        //-----------Update the queue----------//

                        if (queue.getAckQueue().peek() != returnSeg.getSeqNum()) { // while the next element in ack queue is not the ack

                            //remove the ack and the datagram from the queues
                            queue.getAckQueue().poll();
                            queue.getQueue().poll();


                        }

                        //--------restart the timer if queue not empty--------//
                        if(!queue.getQueue().isEmpty()){
                            GoBackFtp.timerReset();
                        }else{
                            GoBackFtp.shutDown();
                        }
                    }
                }catch(SocketTimeoutException e){
                    throw new FtpException("The socket timed out");
                }
            }
        }catch(IOException e){
            throw new FtpException("Error receiving from udp socket");
        }
    }
}
