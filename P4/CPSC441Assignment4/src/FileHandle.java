import java.io.*;
import java.net.DatagramSocket;

public class FileHandle {

    private String fileName; //name of the file

    private FileInputStream fIn; // input stream to read the file

    /**
     * constructs new FileHandle object and initializes the fileName
     * @param fileName the name of the file
     */
    public FileHandle(String fileName) throws FtpException{
        this.fileName = fileName;
        openFileStream(); //calls private function to open a stream to the specified file
    }

    /**
     * returns the input stream
     * @return the stream
     */
    public FileInputStream getFileInputStream(){
        return fIn; //return the input stream
    }

    /**
     * opens the file input stream
     */
    private void openFileStream() throws FtpException {
        File theInFile = new File(this.fileName); //create file

        try {
            fIn = new FileInputStream(theInFile); //open stream
        }catch(IOException e){
            throw new FtpException("error opening the file input stream");
        }
    }

    /**
     * closes the file input stream
     */
    public void cleanUp() throws FtpException{
        try{
            fIn.close(); //close the input stream
        }catch (IOException e){
            throw new FtpException("error closing the file input stream");
        }
    }


}
