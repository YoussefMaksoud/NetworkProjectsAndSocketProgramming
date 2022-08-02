import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkerThread extends Thread implements Runnable{

    private Socket theSocket;
    private InputStream in;
    private OutputStream out;
    private FileInputStream fileIn;
    private WebServer main;

    private String requestType;
    private String path;
    private String version;
    private String host = "ThatCoolServerAtTheCornerStore.org";

    private String responseStatus;

    private byte[] buffer;

    /**
     * constructor
     * @param theSocket the socket for communication with the client
     * @param main the main htread to change flag if needed
     */
    public WorkerThread(Socket theSocket, WebServer main){
        this.theSocket = theSocket;
        buffer = new byte[4096];
        this.main = main;

        try {

            in = theSocket.getInputStream(); //open streams
            out = theSocket.getOutputStream();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * send requested object back through the socket
     */
    @Override
    public void run(){

        StringBuffer request = readTheRequest(); //read the request
        System.out.println(request);

        File toSend = parseHTTPRequest(request.toString()); //parse the request and retunr the file

        try {
            fileIn = new FileInputStream(toSend); //file input stream
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz");
        Date date = new Date();

        if(responseStatus == "400 Bad Request" || responseStatus == "404 Not Found" || request.equals("quit")){
            //format the response header
            String response = responseStatus + "\r\n" +
                    "Date " + df.format(date) + "\r\n" +
                    "Server " + host + "\r\n" +
                    "Connection: close" + "\r\n\r\n";

            System.out.println(response);

            //write the response to the socket
                try {
                    out.write(response.getBytes("US-ASCII"));
                    out.flush();
                    out.close();
                    main.activeFalse(); //set the active flag in the main thread to false to initiate a shutdown
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
        else if(responseStatus == "200 OK"){
            String response = null;
            try {
                //format the header response
                response = responseStatus + "\r\n" +
                        "Date " + df.format(date) + "\r\n" +
                        "Server ThatCoolServerAtTheCornerStore.org\r\n" +
                        "Last-Modified " + toSend.lastModified() + "\r\n" +
                        "Content-Length " + toSend.length() + "\r\n" +
                        "Content-Type " + Files.probeContentType(Paths.get(this.path)) + "\r\n" +
                        "Connection: close\r\n\r\n";

                System.out.println(response);

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                //send the header
                buffer = response.getBytes("US-ASCII");
                try {
                    out.write(buffer);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //send the file through the socket
                int readBytes = 0;
                while((readBytes = fileIn.read(buffer)) != -1){
                    out.write(buffer, 0, readBytes);
                }

                //close th einput stram and shutdown output to the socket
                fileIn.close();
                theSocket.shutdownOutput();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }
        }

        cleanUp(); //close streams

    }

    /**
     * reads the request from the socket
     * @return a buffer containing the request
     */
    public StringBuffer readTheRequest(){

        StringBuffer request = new StringBuffer();
        int readBytes;

        try {

            while (request.indexOf("\r\n\r\n") == -1) {

                readBytes = in.read(buffer, 0, 1);
                for (int i = 0; i < readBytes; i++) {
                    request.append((char)buffer[i]); //read the request into a string buffer
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println(request);

        return request;
    }

    /**
     * parses the HTTP request
     * @param request the get request
     * @return the file to be obtained
     */
    public File parseHTTPRequest(String request){
        //GET path HTTP/1.x\r\n
        //Host: hostname\r\n
        //\r\n

        String[] lines = request.split("\r\n");
        String[] line1 = lines[0].split(" ");
        String[] line2 = lines[1].split(" ");

        this.requestType = line1[0].trim(); //request type
        String objectpath = line1[1];
        String[] object = objectpath.split("/");
        this.version = line1[2].trim(); //http version
        this.host = line2[1].trim(); //host name

        String currentDir = System.getProperty("user.dir");

        this.path = currentDir + "/" + objectpath;
        File toSend = new File(this.path); //file to send

        if(!requestType.equals("GET") || !version.contains("HTTP/1.") || !line2[0].equals("Host:")){
            responseStatus = "400 Bad Request"; //check bad request
        }
        else if(!toSend.isFile()){
            responseStatus = "404 Not Found"; //check a not found object
        }
        else{
            responseStatus = "200 OK"; //check ok request
        }

        return toSend;
    }

    /**
     * closes streams
     */
    public void cleanUp(){

        try{

            in.close(); //close input stream
            out.close(); //close output stream

        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
