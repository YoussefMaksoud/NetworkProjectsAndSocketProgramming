import java.io.*;
import java.net.Socket;

public class ReadFromSocket implements Runnable{

    private Socket socket; //socket to the server
    private InputStream socketRead; //input stream to the socket
    private OutputFileHandler outFh; //helps with the file operations

    private int readBytes = 0; //number of bytes read from socket on client side

    /**
     * constructor for the class
     *
     * @param socket the open socket to the server
     * @param outName the name of the output file to be passed to the OutputFileHandler
     * @param buffSize the size of the buffer to be passed to the OutputFileHandler
     */
    public ReadFromSocket(Socket socket, String outName, int buffSize){
        this.socket = socket; //sets the socket
        outFh = new OutputFileHandler(outName, buffSize); //creates an instance of the output file handler

        try{
            socketRead = socket.getInputStream(); //opens an input stream from the socket
        }catch(IOException a){
            System.out.println("There was an error opening an output stream from the socket."); //throws exception if stream could not be opened for some reason
        }
    }

    /**
     * closes the output file stream in the fileHandler
     */
    public void cleanUp(){
            outFh.closeFileOutputStream(); //close file stream (in helper class)
    }

    /**
     * method to be invoked when thread is started, reads bytes from socket and writes to the file
     */
    @Override
    public void run(){

        FileOutputStream fOut = outFh.getFileOutputStream(); //get the output stream from the fileHandler

        try {

            while((readBytes = socketRead.read(outFh.getBuff())) != -1){ //while there is data to be read from the socket
                fOut.write(outFh.getBuff(), 0, readBytes); //write the data to the output file
                System.out.println("R " + readBytes); //print the number of read bytes
            }


        } catch (IOException e) {

            System.out.println("An error occurred while writing to the socket."); //throws exception if there is an issue reading data from the socket

        }finally {
            cleanUp(); //closes the streams necessary after all data from the socket has been read
        }
    }
}
