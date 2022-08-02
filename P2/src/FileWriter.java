import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class FileWriter {
    private String outFile; //name of the output file
    private FileOutputStream fOut;

    private byte[] buff;

    /**
     *
     * class constructor
     * @param fileName name fo the out
     */
    public FileWriter(String fileName){
        this.outFile = fileName;

        try{
            fOut = new FileOutputStream(outFile);
        }catch(FileNotFoundException e){
            System.out.println("There was an error opening the output file");
        }

    }

    public void writeToFile(Socket s, InputStream in, int payload){
        int readBytes = 0;
        try {
            while ((readBytes = in.read(buff, 0, payload)) != -1) {
                fOut.write(buff, 0, readBytes); //write the data to the output file
            }
        } catch (IOException e) {

            System.out.println("An error occurred while writing to the socket."); //throws exception if there is an issue reading data from the socket

        }
    }


    /**
     * setter for the buffer
     * @param size
     */
    public void setBuff(int size){
        this.buff = new byte[size];
    }

    /**
     *
     * @return
     */
    public FileOutputStream getfOut() {
        return fOut;
    }

    /**
     * getter for the buffer
     * @return
     */
    public byte[] getBuff() {
        return buff;
    }

    /**
     *
     * closes the output stream to the file
     */
    public void closeStream() {
        try{
            fOut.close(); //close stream
        }catch(IOException e){
            System.out.println("error closing the output file");
        }

    }

}
