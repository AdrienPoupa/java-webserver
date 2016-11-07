/**
 * Created by Adrien on 28/10/2016.
 */

import java.io.*;
import java.net.*;
import java.util.*;
public final class WebServer
{
    public static void main(String argv[])
        throws Exception
    {

        System.out.println("Starting web server, listening on port 6789 ...");
        // Set the port number
        int port = 6789;

        // Establish the listen socket
        ServerSocket listenSocket = new ServerSocket(6789);

        //Process HTTP service requests in an infinite loop
        while (true)
        {
            //Listen for a TCP connection request
            Socket connectionSocket = listenSocket.accept();

            // Construct an object to process the HTTP request message.
            HttpRequest request = new HttpRequest(connectionSocket);
            // Create a new thread to process the request.
            Thread thread = new Thread(request);
            // Start the thread.
            thread.start();
        }
    }
}
