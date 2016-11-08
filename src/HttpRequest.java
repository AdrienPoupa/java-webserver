import java.io.*;
import java.net.*;
import java.util.*;
final class HttpRequest implements Runnable {
    private final static String CRLF = "\r\n";
    private Socket socket;

    //Constructor
    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    // Implement the run() method of the Runnable interface.
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private static String contentType(String filename) {
        // for html file
        if (filename.endsWith(".htm") || filename.endsWith(".html")) {
            return "text/html";
        }

        // Mime type of GIF files
        if (filename.endsWith(".gif")) {
            return "image/gif";
        }

        // Mime type of PNG files
        if (filename.endsWith(".png")) {
            return "image/png";
        }

        // Mime type of JPEG files, matches jpg and jpeg extensions
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        }

        // default: application/octet-stream
        return "application/octet-stream";

    }

    private static void sendBytes (FileInputStream fis, DataOutputStream os) throws Exception {
        // Constructs a 1K Buffer to hold bytes on their way to the socket
        byte[] buffer = new byte[1024];
        int bytes = 0;

        // Copy requested file into the socket's output stream
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private void processRequest() throws Exception {

        // Get a reference to the socket's input and output streams.
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        // Set up input stream filters.
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Get the request line of the HTTP request message.
        String requestLine = br.readLine();

        // Display request line.
        System.out.println();
        System.out.println(requestLine);

        // Get and display the header lines
        String headerLine;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        // Extract the filename from the request line
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken(); // Skip over the method, which should be "GET"
        String fileName = tokens.nextToken(); // Get the filename

        // Make sure to display the correct index page
        if (fileName.equals("/")) {
            fileName = "/home.htm";
        }

        // Prepend a "." so that file request is within the current directory
        fileName = "." + fileName;

        // Open the request file.
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false; // If we have caught an exception, the file doesn't exist
        }

        // Construct the response message
        String statusLine = null; // HTTP header with code
        String contentTypeLine = null; // Content-type such as HTML, JPG...
        String entityBody = null; // The content itself, either the bytes or the HTML code
        // If file exists, send the header code 200 ans display the file itself
        if (fileExists) {
            statusLine = "HTTP/1.0 200 OK" + CRLF;
            contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
        }
        // Otherwise, "throw" an error into the HTML and display it
        else {
            statusLine = "HTTP/1.0 404 Not Found" + CRLF;
            contentTypeLine = "Content-type: text/html" + CRLF;
            entityBody = "<html>" +
                    "<head>" +
                    "<title>Not Found</title>" +
                    "</head>" +
                    "<body>Not Found</body>" +
                    "</html>";
        }

        // Send the status line
        os.writeBytes(statusLine);

        // Send the content type line
        os.writeBytes(contentTypeLine);

        // Send a blank line to indicate the end of the header lines
        os.writeBytes(CRLF);

        // Send the entity body
        if (fileExists) {
            try {
                // Try to send the data, only if the file exists
                sendBytes(fis, os);
            } catch (Exception e) {
                statusLine = "HTTP/1.0 500 Internal Error" + CRLF;
                entityBody = "<html>" +
                        "<head>" +
                        "<title>Internal Error</title>" +
                        "</head>" +
                        "<body>" +
                        "<h1>Internal Error</h1>" +
                        "<p>" + e.toString() + "</p>" +
                        "</body>" +
                        "</html>";
                os.writeBytes(entityBody);
            } finally {
                // Close the file input stream once we're done
                fis.close();
            }
        }
        else {
            // Write the "Not found" HTML code
            os.writeBytes(entityBody);
        }

        // close streams and socket.
        os.close();
        br.close();
        socket.close();
    }
}