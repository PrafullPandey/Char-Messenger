package messenger;

import java.io.* ;
import java.net. * ;
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;


public class Server extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow ;
	
	//communicate with help of stream 
	//output stream & input stream
	private ObjectOutputStream output ; 
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server(){
		//setting up gui
		super("Simple Instant messenger");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText , BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
	}
	
	//set up and run the server
	public void startRunning(){
		try{
			//(port_no,backlog(no of people to be waiting ))
			server =new ServerSocket(6789,100);
			while(true){
				try{
					//connect and have conversation
					waitForConnection();// establish and wait for connection
					setupStreams();//set up a stream bw two comp
					whileChatting();// send msg back and forth
				}
				catch(EOFException eofexception){
					showMessage("\n Server ended the connection");
				}
				finally{
					close();
				}
			}		
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	

	//wait for connection, then display information
	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to connect...");
		connection = server.accept();
		showMessage("Now connected to "+connection.getInetAddress().getHostName());	
	}
	
	//get stream to send and receive data
	private void setupStreams() throws IOException{
		
		//creating a pathway to connect to other comp.
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nStreams are now setup! \n");
	}
	
	//during the conversation
	public void whileChatting() throws IOException{
		String message = "You are now connected !";
		sendMessage(message);
		ableToType(true);
		do{
			//have a conversation
			try{
				message = (String)input.readObject();
				showMessage("\n"+message);
			}
			catch(ClassNotFoundException classNotFoundException){
				showMessage("\nidk what user sent");
			}
			
			
		}while(!message.equals("CLIENT - END"));
	}
	
	//close streams and sockets after done chatting
	public void close(){
		showMessage("\n closing connection..");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}
		catch(IOException ioexception){
			ioexception.printStackTrace();
		}
	}
	
	//send a message to client
	private void sendMessage(String message){
		try{
			output.writeObject("SERVER - "+message);
			output.flush();
			showMessage("\nSERVER - "+message);
			
		}catch(IOException ioexception){
			chatWindow.append("\nERROR: I cannot send that");
			ioexception.printStackTrace();
		}
	}
	
	//updates chatWindow
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(text);
				}
			}
		);
	}
	
	//let user type stuff into their box
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
