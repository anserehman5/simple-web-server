import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class WebServer {

    public static void main(String args[]) throws IOException {
    	//declare new ServerScoket, print confirmation to system
    	int port = 7500;
    	try {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Listening for connection on port " + port);
        while(true)
        {
        	Socket serverClient = server.accept();
            System.out.println("Connected");
            //create new thread via separate class
            HandleRequest sct = new HandleRequest(serverClient);
            //call run() function in MultiServer
            sct.start();
        }
    	}catch(IOException e)
    	{
    	System.out.println("Cannot connect to port " + port);
    	}
    }
    
}

class HandleRequest  extends Thread {
	Socket serverClient;
	
	HandleRequest(Socket inSocket)
	{
	    this.serverClient = inSocket;
	}

	public void run(){
	try {
		InputStream sis = serverClient.getInputStream();
      	BufferedReader br = new BufferedReader(new InputStreamReader(sis));
      	
      	//get URL and split it so you have the request (everything after "localhost:7500/")
      	String request = br.readLine();
      	String[] requestParam = request.split(" /");
      	String reqN = requestParam[1];
      	String[] pathN = reqN.split(" ");
      	String path = pathN[0];
      	
      	PrintWriter out = new PrintWriter(serverClient.getOutputStream(), true);
      	
      	//declare file using current working directory + Media folder where files are stored + request
      	File file = new File(System.getProperty("user.dir") + "/Media/" + path);
      	
      	//if we can not find the file just print a message to the browser
      	if (!file.exists()) 
      	{
      		//send HTTP "404 Not Found" error response, which creates page
      		String httpResponse = "HTTP/1.1 404 Not Found\n";
      		httpResponse += "Content-Length: " + 24 + "\n\n";
      		httpResponse += "CANNOT FIND YOUR REQUEST";
      		serverClient.getOutputStream().write(httpResponse.getBytes("UTF-8"));
      		
      	} else {
      	//else, if the requested file does exist
      	//
      	String httpResponse = "HTTP/1.1";
      	httpResponse += " 200 OK \n";
      	httpResponse += "Content-Length: " + file.length()+"\n\n";
      	// send HTTP header
      	serverClient.getOutputStream().write(httpResponse.getBytes("UTF-8"));
      	
      	// Copy file to browser
      	Files.copy(file.toPath(), serverClient.getOutputStream());
      	
      	br.close();
      	out.close();
	}
      }catch(Exception ex)
	  {
    	  //when page is loaded, print confirmation to system
    	  System.out.println("999999999");
	  }
		
	}
}
     