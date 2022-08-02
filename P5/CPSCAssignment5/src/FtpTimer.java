import java.util.Timer;

public class FtpTimer {

    private int tInterval; //timeout interval

    private TimeoutHandle th; //timer task
    private Timer t; //timer

    /**
     * constructor
     * @param th timer task
     * @param interval timeout interval
     */
    public FtpTimer(TimeoutHandle th, int interval){
        this.tInterval = interval;
        this.th = th;
    }

    /**
     * starts the timer
     */
    public void start(){
        t = new Timer();
        t.schedule(new TimeoutHandle(th), 200, tInterval);
    }

    /**
     * stops the timer
     */
    public void stop(){
        t.cancel();
    }

    /**
     * restarts the timer
     */
    public void restart(){
        stop();
        start();
    }

    /**
     * shuts down the timer
     */
    public void shutdown(){
        stop();
        t.purge();
    }


}
