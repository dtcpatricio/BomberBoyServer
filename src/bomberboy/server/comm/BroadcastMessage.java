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
    private String msg;

    public BroadcastMessage(String m, ArrayList<String> playersURL) {
	msg = m;
	urls = playersURL;
    }
    public BroadcastMessage(String m, String url) {
	msg = m;
	urls = new ArrayList<String>();
	urls.add(url);
    }
	
    @Override
    public void run() {
	try {
	    for(String s : urls) {
		Socket sock = new Socket();
		sock.connect(new InetSocketAddress("localhost", 8089));
		
		PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
		pw.write(msg);
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
