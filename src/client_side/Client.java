package client_side;

import java.io.*  ;
import java.net.* ;
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;

public class Client extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow ;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String  serverIP;
	private Socket connection ;
	
	
	public Client(String host){
		super("Simple Instant Messenger");
		serverIP =host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				 new ActionListener(){
					 public void actionPerformed(ActionEvent event){
						 sendData(event.getActionCommand());
						 userText.setText("");
					 }
				 }
			);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow) , BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
	
	}
	//connect to server
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
			
		}catch(EOFException eofException){
			showMessage("\nClient terminated the connection");
		}catch(IOException ioexception){
			ioexception.printStackTrace();
		}finally{
			close();
		}
	}
	
	//connect to server
	private void connectToServer() throws IOException{
		showMessage("Attempting connection.. \n");
		connection = new Socket(InetAddress.getByName(serverIP),6739);
		showMessage("\nConnected to: "+connection.getInetAddress().getHostName());
		
	}
	
	//setting up streams
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are good to go");
		
	}
	
	//chatting with server
	public void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String)input.readObject();
				showMessage("\n"+message);
				
				
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\n idk the object type");
				
			}
		}while(!message.equals("SERVER - END"));
	}
	
	//closing the streams and sockets
	private void close(){
		showMessage("\n Closing the connnection");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioexception){
			ioexception.printStackTrace();
		}
	}
	
	//send messages to server
	private void sendData(String message){
		try{
			output.writeObject("CLIENT - "+message);
			output.flush();
			showMessage("\nCLIENT - "+message);
			
		}catch(IOException ioException){
			chatWindow.append("\n something messed up");
		}
	}
	
	//append messages on the chat window
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
		 new Runnable(){
			 public void run(){
				 chatWindow.append(text);
			 }
		 }
		);
	}
	
	//give user permission to type
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
		 new Runnable(){
			 public void run(){
				 userText.setEditable(tof);
			 }
		 }
		);
	}
	
}
