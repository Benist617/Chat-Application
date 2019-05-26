import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient {
	
	// all the components are static since its fit to all the clients.
	static JFrame chatWindow = new JFrame("Chat Application");
	//displaying all the messages received and sent (rows, col)
	static JTextArea chatArea = new JTextArea(22, 40);
	//used to entering the message
	static JTextField textField = new JTextField(40);
	//used to display blank space between the chat area and the txt field.
	static JLabel blankLabel = new JLabel("              ");
	static JButton sendButton = new JButton("Send");
	
	static BufferedReader in;
	static PrintWriter out;
	
	static JLabel nameLabel = new JLabel("              ");
	
	public ChatClient() {
		chatWindow.setLayout(new FlowLayout());
		chatWindow.add(nameLabel);
		// a scroller functionality to the user
		chatWindow.add(new JScrollPane(chatArea));
		chatWindow.add(blankLabel);
		chatWindow.add(textField);
		chatWindow.add(sendButton);
		
		chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chatWindow.setSize(475, 500);
		chatWindow.setVisible(true);
		// only permit when the connection established, user foe now will not be able to enter any text
		textField.setEditable(false);
		chatArea.setEditable(false);
		
		sendButton.addActionListener(new Listener());
		textField.addActionListener(new Listener());
			
	}
	
	void startChat() throws Exception
	{
		
		String ipAddress = JOptionPane.showInputDialog(
					chatWindow,
					"Enter IP Address",
					"IP Address is Required!",
					JOptionPane.PLAIN_MESSAGE);
		
		Socket cs = new Socket(ipAddress, 9806);
		
		in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
		out = new PrintWriter(cs.getOutputStream(), true);
		
		while (true) 	
		{
			//at first read the message from the server.
			String str = in.readLine();
			if (str.equals("NAMEREQUIRED"))
			{
				String name = JOptionPane.showInputDialog(
						chatWindow,
						"Enter a unique name",
						"Name Required!",
						JOptionPane.PLAIN_MESSAGE);
				
				out.println(name);
			}
			else if (str.equals("NAMEALREADYEXISTS"))
			{
				String name = JOptionPane.showInputDialog(
						chatWindow,
						"Enter another name",
						"Name Required!",
						JOptionPane.WARNING_MESSAGE);
				
				out.println(name);
			}
			else if (str.startsWith("NAMEACCEPTED")) 
			{
				textField.setEditable(true);
				nameLabel.setText("You are logged in as: "+ str.substring(12));
			}
			else
			{	//consider as a normal message
				chatArea.append(str + "\n");
			}
		}
	}
	 
	public static void main(String [] args) throws Exception {
		
		ChatClient client = new ChatClient();
		client.startChat();
	}
}

class Listener implements ActionListener{
	@Override
	public void actionPerformed(ActionEvent e) {
		ChatClient.out.println(ChatClient.textField.getText());
		ChatClient.textField.setText("");
	}
}


