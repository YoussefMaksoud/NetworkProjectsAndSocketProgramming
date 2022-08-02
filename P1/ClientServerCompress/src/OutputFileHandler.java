import java.io.*;

public class OutputFileHandler {

    private String outFile; //name of the output file
    private FileOutputStream fOut; //output stream to write to the file

    private byte[] buff; //buffer

    /**
     * constructor for the class
     *
     * @param outFile name of the output file
     * @param buffSize size of the buffer
     */
    public OutputFileHandler(String outFile, int buffSize) {
        this.outFile = outFile; //sets the output file name
        this.buff = new byte[buffSize]; //creates a new buffer
    }

    /**
     * creates a file and opens an output data stream to that file to be written to
     *
     * @return the data output stream to the file
     */
    public FileOutputStream getFileOutputStream(){
        File output = new File(outFile); //creates a new file with the output file name specified
        try{
            fOut = new FileOutputStream(output); //attempts to open an output stream to the file
        }catch (FileNotFoundException e){
            System.out.println("An error occurred while opening the output file."); //throws io exception if there was trouble opening the output stream
        }

        return fOut;
    }

    /**
     * closes the file output stream
     *
     */
    public void closeFileOutputStream(){
        try{
            fOut.close(); //close the stream
        }catch(IOException e){
            System.out.println("An error occurred while closing the output file."); //trows an exception if there was an error cosing the stream
        }
    }

    /**
     * gets the buffer to be read from
     *
     * @return the buffer
     */
    public byte[] getBuff() {
        return buff;
    }
}
