
// Fig. 27.7: Client.java
// Client portion of a stream-socket connection between client and server.
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Player extends JFrame {
	private JButton Hit;
	private JButton Stay;
	private JPanel buttons;
	private JTextArea displayArea; // display information to user
	private ObjectOutputStream output; // output stream to server
	private ObjectInputStream input; // input stream from server
	private Card card; // message from server
	private String message = "";
	private String chatServer; // host server for this application
	private Socket client; // socket to communicate with server
	private int cardamt = 0;
	private PlayerInfo playerInfo;
	BlackjackTable table;
	Decision request;
	Decision stand;
	// initialize chatServer and set up GUI
	public Player(String host) {
	
		chatServer = host; // set server to which this client connects
		this.createPlayerRequests();
		buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 2));
		Hit = new JButton("Pedir");
		Stay = new JButton("Quedarse");

		Hit.addActionListener(new ActionListener() {
			// send message to server
			public void actionPerformed(ActionEvent event) {
				sendData(request);
			} // end method actionPerformed
		} // end anonymous inner class
		); // end call to addActionListener

		Stay.addActionListener(new ActionListener() {
			// send message to server
			public void actionPerformed(ActionEvent event) {
				sendData(stand);
			} // end method actionPerformed
		} // end anonymous inner class
		); // end call to addActionListener

		buttons.add(Hit, BorderLayout.SOUTH);
		buttons.add(Stay, BorderLayout.SOUTH);
		buttons.setVisible(true);
		add(buttons, BorderLayout.SOUTH);
		displayArea = new JTextArea(); // create displayArea
		add(new JScrollPane(displayArea), BorderLayout.CENTER);
		add(this.createTableGame());
		setResizable(false);
		setSize(924,684); // set size of window
		setVisible(true); // show window
	} // end Client constructor
	
	public JPanel createTableGame() {
		 return table = new BlackjackTable();
		 
	}
	
	public void createPlayerRequests() {
		request = new Decision("Pedir");
		stand = new Decision("Quedarse");
	}
	
	 private void registerPlayer() {
	        String playerName = JOptionPane.showInputDialog("Ingrese su nombre:");
	        String host = JOptionPane.showInputDialog("Ingrese el host del servidor:");
	        int port = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el puerto del servidor:"));

	        playerInfo = new PlayerInfo(playerName, host, port);
	}
	
	// connect to server and process messages from server
	public void runClient() {
        registerPlayer();
		try // connect to server, get streams, process connection
		{
			connectToServer(); // create a Socket to make connection
			getStreams(); // get the input and output streams
			processConnection(); // process connection
		} // end try
		catch (EOFException eofException) {
			displayMessage("\nCliente terminó la conexión");
		} // end catch
		catch (IOException ioException) {
		} // end catch
		finally {
			closeConnection(); // close connection
		} // end finally
	} // end method runClient

	// connect to server
	private void connectToServer() throws IOException {
		displayMessage("Intentando conexión\n");

		// create Socket to make connection to server
		client = new Socket(InetAddress.getByName(chatServer), 23555);

		// display connection information
		displayMessage("Conectado a: " + client.getInetAddress().getHostName());
	} // end method connectToServer

	// get streams to send and receive data
	private void getStreams() throws IOException {
		// set up output stream for objects
		output = new ObjectOutputStream(client.getOutputStream());
		output.flush(); // flush output buffer to send header information

		// set up input stream for objects
		input = new ObjectInputStream(client.getInputStream());

		displayMessage("\nSe obtuvieron flujos de entrada/salida\n");
	} // end method getStreams
	public void procesarObjeto(Object objetoLeido,String message) {
	    if (objetoLeido instanceof Card) {
	        
	        Card carta = (Card) objetoLeido;
	        table.addImageCards(carta.toString());	        	
	    } else if(objetoLeido instanceof Decision){
	    	System.out.println("entra");
	        message = ((Decision)objetoLeido).getValue();
	    }
	}
	// process connection with server
	private void processConnection() throws IOException {
		message = "Por favor, espera";
		do // process messages sent from server
		{
			try // read message and display it
			{
				Object receivedObject = input.readObject();
	            if (receivedObject instanceof Card) {
	                Card receivedCard = (Card) receivedObject;
	                displayMessage("\nCarta recibida: " + receivedCard.toString() + "\n");
	                // Actualiza la interfaz gráfica para mostrar la carta recibida
	                table.addImageCards(receivedCard.toString());
	            } else if (receivedObject instanceof Decision) {
	                message = ((Decision) receivedObject).getValue();
	                displayMessage("\nMensaje del servidor: " + message + "\n");
	                if (message.contains("¡Te pasaste!") || message.contains("Por favor, espera")) {
	                    buttons.setVisible(false);
	                }
	            }

			} // end try
			catch (ClassNotFoundException classNotFoundException) {
				displayMessage("\nTipo de objeto desconocido recibido");
			} // end catch

		} while (!message.equals("SERVIDOR>>> TERMINADO"));
		System.out.println("salio");
	} // end method processConnection

	// close streams and socket
	private void closeConnection() {
		displayMessage("\nCerrando conexión");

		try {
			output.close(); // close output stream
			input.close(); // close input stream
			client.close(); // close socket
		} // end try
		catch (IOException ioException) {
		} // end catch
	} // end method closeConnection

	// send message to server
	private void sendData(Object message) {
		try // send object to server
		{
			output.writeObject(message);
			output.flush(); // flush data to output

		} // end try
		catch (IOException ioException) {
			displayArea.append("\nError al escribir el objeto");
		} // end catch
	} // end method sendData

	// manipulates displayArea in the event-dispatch thread
	private void displayMessage(final String messageToDisplay) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() // updates displayArea
			{
				displayArea.append(messageToDisplay);
			} // end method run
		} // end anonymous inner class
		); // end call to SwingUtilities.invokeLater
	} // end method displayMessage

}// end player class
