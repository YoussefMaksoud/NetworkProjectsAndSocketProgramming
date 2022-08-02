import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InputFileHandler {

    private String inFile; //name of the input file
    private FileInputStream fIn; //input file stream

    private byte[] buff; //buffer

    /**
     * constructor for the object
     *
     * @param inFile name of the input file
     * @param buffSize size of the buffer
     */
    public InputFileHandler(String inFile, int buffSize) {
        this.inFile = inFile; //sets the input file name
        this.buff = new byte[buffSize]; //creates a new buffer to read into from a file
    }

    /**
     * called to establish an input filestream to be read from.
     *
     * @return the FileInputstream of the specified file
     */
    public FileInputStream getFileInputStream(){
        File input = new File(inFile); //creates a new file object
        try{
            fIn = new FileInputStream(input); //attempts to open an inputstream from the file
        }catch (FileNotFoundException e){
            System.out.println("An error occurred while opening the input file."); //throws exception if the file is not found or couldn't be opened
        }

        return fIn;
    }

    /**
     * closes the file input stream
     *
     */
    public void closeFileInputStream(){
        try{
            fIn.close(); //closes the file input stream when done reading
        }catch(IOException e){
            System.out.println("An error occurred while closing the input file."); //throws io exception if stream cannot be closed
        }
    }

    /**
     * gets the buffer for reading from the file
     *
     * @return the buffer
     */
    public byte[] getBuff() {
        return buff;
    }
}
