import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
	
	// list of all users that connected to the server (no duplicated needed)
	static ArrayList<String> userNames = new ArrayList<String>();
	// when a client send a message to the server, then the server need to sent this message to all the other clients
	static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();
	
	public static void main(String [] args) throws Exception {
		
		System.out.println("Waiting for clients...");
		ServerSocket ss = new ServerSocket(9806);
		while(true) {
			Socket cs = ss.accept();
			System.out.println("Connection Established");
			ConversationHandler handler = new ConversationHandler(cs);
			handler.start();
		}
	}
}

class ConversationHandler extends Thread{
	
	Socket socket;
	BufferedReader in;
	//write to the socket output stream
	PrintWriter out;
	String name;
	
	//write into the txt file
	PrintWriter pw;
	static FileWriter fw;
	static BufferedWriter bw;
		
	public ConversationHandler(Socket cs) throws IOException {
		
		this.socket = cs;
		fw = new FileWriter("C:\\Users\\Ben Istaharov\\Desktop\\ChatServerLog.txt", true);
		bw = new BufferedWriter(fw);
		pw = new PrintWriter(bw, true);
		 
	}
	
	public void run() {
		try 
		{
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			// a loop till the user enter a unique name
			int count = 0;
			while (true)
			{
				if (count > 0 ) {
					out.println("NAMEALREADYEXISTS");
				}
				else
				{
					out.println("NAMEREQUIRED");
				}
				
				name = in.readLine();
				
				if (name == null)
						return;
				// if we find a unique name break the loop
				if (!ChatServer.userNames.contains(name)) {
					ChatServer.userNames.add(name);
					break;
				}
				count++;
			}
			
			out.println("NAMEACCEPTED" + name);
			ChatServer.printWriters.add(out);
			
			//read message from the client and send it to all the other clients
			while (true)
			{
				String message = in.readLine();
				if (message == null) 
				{
					return;
				}
				
				pw.println(name + ": " + message);
				
				for (PrintWriter writer : ChatServer.printWriters) {
					writer.println(name + ": " + message);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
}
