package Server;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;

	public Server() {
		super("Messenger Server");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(350, 150);
		setVisible(true);
	}
	
	public void startRunning() {
		try {
			server = new ServerSocket(4500, 100);
			while(true) {
				try {
					waitForConnection();
					setupStream();
					whileChatting();
				}catch(EOFException ex) {
					showMessage("Server ended the connection");
				}finally {
					closeCrap();
				}
			}
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	private void waitForConnection() {
		showMessage("Waiting for a Client");
		try {
			connection = server.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showMessage("Connected to " + connection.getInetAddress().getHostName());
	}
	
	private void setupStream() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush(); 
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Stream are now setup\n");
		
	}
	
	private void whileChatting() throws IOException {
		String message = " You are connected";
		sendMessage(message);
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch (ClassNotFoundException ex) {
				showMessage("\n Message can't be sent");
			}
		}while(!message.equals("Quit"));
	}
	
	
	//close stream when end chatting
	private void closeCrap() {
		showMessage("Close connection ...\n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Send message to client
	private void sendMessage(String mess) {
		try {
			output.writeObject("SERVER: " + mess);
			output.flush();
			showMessage("\nSERVER: " + mess);
		} catch (IOException ioex) {
			chatWindow.append("\n ERROR: ");
		}
	}
	
	private void showMessage(final String mess) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						chatWindow.append(mess);
					}
				}
				);
	}
	
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(tof);
			}
		});
	}
}
