
package com.project.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * This class represents the Client-Front End (CFE) of the project; this class extends JFrame to implementing
 * the GUI; this GUI provides 3 Input-Textfields where the User can insert AirportCode-From, AirportCode-To and 
 * DepartureDate in order to get the flight information provided by the FIPU-Webservices. A TextArea collects the
 * founded flight information and shows them to the user.
 * @author Group Sascha Scatà, Jan Raphael Schmid Niederkofler, Christine Lunger, Benjamin Egger
 *
 */
public class ClientGUI extends JFrame {

	private JPanel contentPane;
	private JTextField fromText, toText, dateText;
	private JLabel fromLabel, toLabel, dateLabel;
	// public TestClient tc;
	public JTextArea answerText;
	public JScrollPane scrollPane;
	public String fromString, toString, dateString;
	public TopicPublisher tC;
	public Thread t;

	//The Format of the departure date:
	static SimpleDateFormat format = new SimpleDateFormat("MM/dd/YY");
	private JButton btnClear;

	/**
	 * The main method of the CFE-Gui
	 * @param args -
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI frame = new ClientGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					System.out.println("ClientGUI: " + e.getMessage());
				}
			}
		});
	}

	/**
	 * The constructor will start a separate Thread that represents the listening of the response-queue. In addition
	 * to this task, a timer will update every second the TextArea that collects the results provided by the FIPU 
	 * through the queue.
	 */
	public ClientGUI() {
		tC = new TopicPublisher();
		//Start listening at the reponse-queue:
		t = new QueueListenerThread();
		t.start();

		//Timer for updating the TextArea:
		new java.util.Timer().schedule(new TimerTask() {
			public void run() {
				answerText.setText(QueueListenerThread.resp);
			}
		}, 3000, 1000);
		
		setResizable(false);
		setTitle("Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 692, 395);
		getContentPane().setLayout(new BorderLayout());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());

		JPanel formPanel = new JPanel();
		fromText = new JTextField();
		fromText.setHorizontalAlignment(SwingConstants.LEFT);
		fromText.setColumns(3);

		toText = new JTextField();
		toText.setHorizontalAlignment(SwingConstants.LEFT);
		toText.setColumns(3);

		dateText = new JTextField();
		dateText.setHorizontalAlignment(SwingConstants.LEFT);
		dateText.setColumns(11);

		fromLabel = new JLabel("From:");
		toLabel = new JLabel("To:");
		dateLabel = new JLabel("Date:");

		JButton sendButton = new JButton("Send");

		formPanel.add(fromLabel);
		formPanel.add(fromText);
		formPanel.add(toLabel);
		formPanel.add(toText);
		formPanel.add(dateLabel);
		formPanel.add(dateText);
		formPanel.add(sendButton);

		answerText = new JTextArea();
		scrollPane = new JScrollPane(answerText);

		//Controll if all inputs are entered correctly:
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (fromText.getText().equals("")
							|| fromText.getText() == null
							|| toText.getText().equals("")
							|| toText.getText() == null
							|| dateText.getText().equals("")
							|| dateText.getText() == null) {
						JOptionPane
								.showMessageDialog(
										null,
										"Attention ! All three Inputs should be inserted !",
										"Insane error",
										JOptionPane.ERROR_MESSAGE);
					} else {
						if (fromText.getText().length() != 3
								|| toText.getText().length() != 3) {
							JOptionPane
									.showMessageDialog(
											null,
											"Attention ! From and To must be exactly 3 characters !",
											"Insane error",
											JOptionPane.ERROR_MESSAGE);
						}

						if (isValidDate(dateText.getText()))
							tC.sendToFIPU(fromText.getText(), toText.getText(),
									dateText.getText());

					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

		contentPane.add(formPanel, BorderLayout.NORTH);
		
		btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				QueueListenerThread.resp="";
			}
		});
		formPanel.add(btnClear);
		contentPane.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * This method is needed to validate the departureDate entered by the user - The correct format
	 * is MM/dd/YY
	 * @param input - the entered departureDate by the user
	 * @return - true if the date was entered correctly
	 */
	public boolean isValidDate(String input) {
		try {
			format.parse(input);
			return true;
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(null,
					"Attention ! Date format is MM/dd/YY !", "Insane error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

}
