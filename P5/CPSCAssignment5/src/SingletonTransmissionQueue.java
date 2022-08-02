import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A Singleton of the transmission queues to be accessed by all threads that require it
 */
public class SingletonTransmissionQueue {

    private static SingletonTransmissionQueue instance = new SingletonTransmissionQueue(); //the single instance of the object

    private ConcurrentLinkedQueue<DatagramPacket> transmissionQueue; //transmission queue

    private ConcurrentLinkedQueue<Integer> ackQueue; //queue containing acks to facilitate relevent checks

    /**
     * private constructor to avoid creation of multiple instances
     */
    private SingletonTransmissionQueue(){
        transmissionQueue = new ConcurrentLinkedQueue();
        ackQueue = new ConcurrentLinkedQueue();
    }

    /**
     * returns the instance
     * @return instance
     */
    public static SingletonTransmissionQueue getInstance(){
        return instance;
    }

    /**
     * returns the transmission queue
     * @return transmissionQueue
     */
    public ConcurrentLinkedQueue<DatagramPacket> getQueue(){
        return this.transmissionQueue;
    }

    /**
     * returns the ack queue
     * @return ackQueue
     */
    public ConcurrentLinkedQueue<Integer> getAckQueue(){
        return ackQueue;
    }


}
