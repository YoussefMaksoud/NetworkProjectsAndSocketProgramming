import java.io.*;
import java.net.Socket;

public class WriteToSocket implements Runnable{

    private Socket socket; //socket to the server
    private DataOutputStream socketWrite; //output stream to the socket
    private InputFileHandler inFh; //instance of the INputFileHandler

    private int writtenBytes = 0; //number of bytes written to socket

    /**
     * constructor for the class
     *
     * @param socket open socket to the server
     * @param inName name of the input file to be read from
     * @param buffSize size of the buffer to be used
     */
    public WriteToSocket(Socket socket, String inName, int buffSize){
        this.socket = socket; //set the socket
        inFh = new InputFileHandler(inName, buffSize); //initialize the instance of the FileHandler

        try{
            this.socketWrite = new DataOutputStream(socket.getOutputStream()); //attempts to open output stream to the socket
        }catch(IOException a){
            System.out.println("There was an error trying to open an input stream to the socket."); //throws io exception if there was an issue opening the stream
        }
    }

    /**
     * closes the required streams
     *
     */
    public void cleanUp(){
        try {
            inFh.closeFileInputStream(); //closes the input file stream using the FileHandler (helper class)
            socket.shutdownOutput(); //shuts down output to the socket signaling to the server side that no more data will be written tot the socket

        }catch(IOException e){
            System.out.println("Error closing socket stream."); //throws io exception if there was an issue close the stream or the output to socket
        }
    }

    /**
     * method to execute when the thread is started, reads from the input file and writes the data to the socket
     */
    @Override
    public void run() {

        FileInputStream fIn = inFh.getFileInputStream();

        try {

            while((writtenBytes = fIn.read(inFh.getBuff())) != -1){ //while there is still data to be read from the file
                socketWrite.write(inFh.getBuff(), 0, writtenBytes); //write the data into the socket
                socketWrite.flush(); //clear the contents of the socket
                System.out.println("W " + writtenBytes); //print the number of bytes written tot he socket
            }

        } catch (IOException e) {

            System.out.println("An error occurred while writing to the socket."); //throws an io exception if there is an issue reading from the file

        }finally{
            cleanUp(); //lastly, cleans up by closing the necessary streams
        }
    }
}
