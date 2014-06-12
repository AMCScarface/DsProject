package com.project.client;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.JOptionPane;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.w3c.dom.Document;

/**
 * This class represents the separate Thread that is listening for incoming messages at the response-queue.
 * If an incoming message exists, the proper method "onMessage" will extract the message from the queue and 
 * will register the incoming message (containing the flight informations of the FIPUs) in the appropiate static
 * variable 'resp' (will be invocated by the CFE to update the TextArea).
 * @author Group Sascha Scatà, Jan Raphael Schmid Niederkofler, Christine Lunger, Benjamin Egger
 *
 */
public class QueueListenerThread extends Thread implements MessageListener{
	
	//Response-Message of the FIPUs:
	public static String resp = "";
	
	/**
	 * Start the thread by creating a connection to the Queue and begin as a MessageConsumer the Listening
	 * at the queue (asynchronous message consuming).
	 */
	public void run(){
		TransportConfiguration transportConfiguration = new TransportConfiguration(
				NettyConnectorFactory.class.getName());

		ConnectionFactory factory = (ConnectionFactory) HornetQJMSClient
				.createConnectionFactoryWithoutHA(JMSFactoryType.CF,
						transportConfiguration);

		// The queue name should match the jms-queue name in standalone.xml
		Queue queue = HornetQJMSClient.createQueue("testQueue");
		Connection connection;
		try {
			connection = factory.createConnection();
			Session session = connection.createSession(false,
					QueueSession.AUTO_ACKNOWLEDGE);
			connection.start();
			//MessageConsumer asynchronously:
			MessageConsumer consumer = session.createConsumer(queue);
			consumer.setMessageListener(new QueueListenerThread());

		} catch (JMSException e) {
			JOptionPane.showMessageDialog(null,
					e.getMessage(), "Insane error",
					JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * This implemented method of the MessageListener - interface will handle the incoming message
	 * from the queue; this incoming message contains the flight information provided by the FIPU which sends
	 * the results back to the CFE through this Listener-method; once a incoming message was handled, this method
	 * will store the results in the resp-attribute for further processing (updating the TextArea in the GUI)
	 * @param - The incoming message from the queue
	 */
	@Override
	public void onMessage(Message message) {
		TextMessage msg = (TextMessage) message;
	      try {
			resp += message.getStringProperty("NumberOfFIPU") +"\n" +msg.getText() +"\n";
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
