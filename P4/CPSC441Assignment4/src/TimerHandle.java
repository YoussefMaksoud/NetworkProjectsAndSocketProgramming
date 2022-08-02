import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.TimerTask;

public class TimerHandle extends TimerTask{

    DatagramPacket retransPkt; //packet to be retransmitted //the socket opened to the server
    DatagramSocket udpSocket;

    /**
     * constructor
     * @param theSocket the socket where data is being sent
     */
    public TimerHandle(DatagramSocket theSocket){
        this.udpSocket = theSocket; //open communication socket
    }

    /**
     * sets the packet that needs to be retransmitted
     * @param d
     */
    public void setRetransPkt(DatagramPacket d){
        this.retransPkt = d;
    }

    /**
     * when timeout occurs, this method runs concurrent to the main program
     */
    @Override
    public void run() {
        System.out.println("timeout"); //print that a timeout occured

        IOException e = new IOException(new FtpException("Error retransmitting the packet"));

        try {
            udpSocket.send(retransPkt); //retranmit the datagram
            System.out.println("retx " + new FtpSegment(retransPkt).getSeqNum()); //print that a retransmission occured along with the retransmitted sequence number
        }catch(IOException x){
            e.getMessage();
        }

    }
}
