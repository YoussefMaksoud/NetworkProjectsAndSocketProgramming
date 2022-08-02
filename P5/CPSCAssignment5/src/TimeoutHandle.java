import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Iterator;
import java.util.TimerTask;

public class TimeoutHandle extends TimerTask {
    private DatagramSocket udpSocket;

    public TimeoutHandle(DatagramSocket socket){
        this.udpSocket = socket;
    }

    /**
     * copy constructor
     * @param t the timer task to be copied
     */
    public TimeoutHandle(TimeoutHandle t){
        this.udpSocket = t.udpSocket;
    }

    /**
     * calls the retransmit function, this way a new ftp exception can be thrown if needed
     */
    @Override
    public void run() {
        try{
            retransmit(); //retransmit
        }catch (FtpException e){
            e.getMessage();
        }
    }

    /**
     * retransmits the packets in the queue when a timeout occurs
     * @throws FtpException
     */
    public void retransmit() throws FtpException{

        //get instance of the transmission queue
        SingletonTransmissionQueue transmissionQueue = SingletonTransmissionQueue.getInstance();

        System.out.println("-------timeout-------");

        //iterators to traverse the queue
        Iterator<DatagramPacket> iter = transmissionQueue.getQueue().iterator();
        Iterator<Integer> iterAck = transmissionQueue.getAckQueue().iterator();

        //while there is another element in the queue
        while (iter.hasNext()) {
            try {
                DatagramPacket toSend = iter.next(); //set the packet
                udpSocket.send(toSend); //send the packet
                if (iterAck.hasNext()) {
                    System.out.println("retx: " + iterAck.next().intValue()); //print the retransmitted packet
                }
            } catch (IOException e) {
                throw new FtpException("Error retransmitting the datagrams");
            }

        }
    }
}
