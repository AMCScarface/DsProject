package com.project.client;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.swing.JOptionPane;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;

/**
 * This class represents the message-sending-task to the FIPU. The inputs entered at the CFE will be processed
 * in order to get sent to the topic; every subscriber (FIPUs in this case) will receive the message send by this
 * class, the Publisher
 * @author Group Sascha Scatà, Jan Raphael Schmid Niederkofler, Christine Lunger, Benjamin Egger
 *
 */
public class TopicPublisher {


	/**
	 * This method will send all the information provided by the CFE to the topic, where every subscriber 
	 * will do further processing. The method is using a MapMessage that will be sent, where every single 
	 * input-parameter will be registered before sending. The connection will be closed at the end of the
	 * sending process.
	 * @param from - The code of the departure airport
	 * @param to - The code of the arrival airport
	 * @param date - The departure date
	 * @throws Exception - If the connection cannot be established, show a MessageBox containing the reason of
	 * the error
	 */
	public void sendToFIPU(String from, String to, String date)
			throws Exception {

		TransportConfiguration transportConfiguration = new TransportConfiguration(
				NettyConnectorFactory.class.getName());

		ConnectionFactory factory = (ConnectionFactory) HornetQJMSClient
				.createConnectionFactoryWithoutHA(JMSFactoryType.CF,
						transportConfiguration);

		// The queue name should match the jms-queue name in standalone.xml
		Topic topic = HornetQJMSClient.createTopic("testTopic");
		Connection connection;
		try {
			connection = factory.createConnection();
			Session session = connection.createSession(false,
					QueueSession.AUTO_ACKNOWLEDGE);

			MessageProducer producer = session.createProducer(topic);
			//Sending TextMessage to the Topic by MapMessage:
			MapMessage mapMessage = session.createMapMessage();
			mapMessage.setString("From", from);
			mapMessage.setString("To", to);
			mapMessage.setString("Date", date);
			mapMessage.setString("Key", "556B85E8-EBB2-4D01-B79A-E3E3071FD9D6");
			producer.send(mapMessage);

			producer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			JOptionPane.showMessageDialog(null,
					e.getMessage(), "Insane error",
					JOptionPane.ERROR_MESSAGE);
		}
		// receiveFromFIPU();
	}
}