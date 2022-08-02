package Client;
public class ParsingHelper {

    private String serverAddi;
    private String objectPath;
    private String objectName;
    private int portNum;

    private int statusCode;

    public ParsingHelper(String url) {
        parseURL(url);
    }

    /**
     *
     * @param url url of the object being obtained
     */
    private void parseURL(String url){

        String[] temp1 = url.split("/"); //splits object path and server address

        if(url.contains(":")){
            String[] temp2 = temp1[0].split(":"); //splits first part of url into server address and port number

            serverAddi = temp2[0]; //first element is the address
            portNum = Integer.parseInt(temp2[1]); //second element is the port
        }
        else{
            serverAddi = temp1[0]; //first element is the address
            portNum = 80; //second element is the port, since none was specified, default is 80
        }

        objectName = temp1[temp1.length - 1]; //the name of the file being obtained
        objectPath = ""; //the path of the object to be obtained

        for(int i = 1; i < temp1.length; i++){ //the rest of the url is the object path
            objectPath += "/";
            objectPath += temp1[i];
        }

        System.out.println(portNum);
        System.out.println(objectName);
        System.out.println(objectPath);
        System.out.println(serverAddi);
    }

    /**
     *
     * parses the http header and extracts the size of the payload
     * @param response is the http header
     * @return the size of the payload
     */
    public int extractPayload(StringBuffer response){

        String responseHeader = response.substring(0, response.indexOf("\r\n\r\n")); //parses the http header to determine the payload of the packet
        String[] temp = responseHeader.split("\r\n"); //splits lines
        String[] temp2 = temp[4].split(" "); //splits the line containing content length
        String[] temp3 = temp[0].split(" "); //splits the line containing status
        statusCode = Integer.parseInt(temp3[0]);

        return Integer.parseInt(temp2[1]);

    }

    /**
     *
     * @return host of the server running where the object is
     * to be obtained from
     */
    public String getServerAddi() {
        return serverAddi;
    }

    /**
     *
     * @return path of the object being obtained
     */
    public String getObjectPath() {
        return objectPath;
    }

    /**
     *
     * @return //name of the object being obtained
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     *
     * @return portnumber of process
     */
    public int getPortNum() {
        return portNum;
    }

    /**
     *
     * @return the status code of the HTTP header
     */
    public int getStatusCode() {
        return statusCode;
    }
    public String getObject(){
        return objectName;
    }
}
