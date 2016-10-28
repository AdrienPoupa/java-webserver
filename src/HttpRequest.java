/**
 * Created by Adrien on 28/10/2016.
 */
import java.io.*;
import java.net.*;
import java.util.*;
final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    //Constructor
    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    // Implement the run() method of the Runnable interface.
    public void run() {
        try {
            processRequest();

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void processRequest() throws Exception {

        // Get a reference to the socket's input and output streams.
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        // Set up input stream filters.
        FilterInputStream fis;
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Get the request line of the HTTP request message.
        String requestLine = br.readLine();
        // Display request line.
        System.out.println();
        System.out.println(requestLine);

        // Get and display the header lines
        String headerline = null;
        while ((headerline = br.readLine()).length() != 0) {
            System.out.println(headerline);
        }
        // close streams and socket.

    }
}