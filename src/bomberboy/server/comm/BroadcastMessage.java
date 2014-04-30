package bomberboy.server.comm;

import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class BroadcastMessage extends Thread
{

    private List<Socket> playersConn = new Vector<Socket>();
    private ArrayList<String> urls;
    private PrintWriter pw;
    private String msg;
	
    public BroadcastMessage(String m, ArrayList<String> playersURL)
    {
	msg = m;
	urls = playersURL;
    }
	
    @Override
    public void run()
    {
	System.err.println("running thread");
	try {
	    for(String s : urls) {
		System.err.println(s);
		Socket sock = new Socket();
		//sock.bind(new InetSocketAddress("194.210.230.236",8089));
		System.err.println("about to connect....");
		sock.connect(new InetSocketAddress("localhost", 8089));
		//		Socket sock = new Socket(s, 4444);
		System.err.println("done");
		
		pw = new PrintWriter(sock.getOutputStream(), true);
		pw.write(msg);
		System.err.println(msg);
		pw.flush();
		pw.close();
		sock.close();
	    }
	} catch(UnknownHostException uhe) {
	    System.err.println("UnknownHostException: " + uhe.getMessage());
	} catch(IOException ioe) {
	    System.err.println("IOException: " + ioe.getMessage());
	}
    }
    
}
