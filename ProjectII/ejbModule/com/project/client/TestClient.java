package com.project.client;
 
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
 
 
public class TestClient {
 
    public static void main(String[] args) {
        TransportConfiguration transportConfiguration = 
                        new TransportConfiguration(
                NettyConnectorFactory.class.getName());  
                       
        ConnectionFactory factory = (ConnectionFactory)
            HornetQJMSClient.createConnectionFactoryWithoutHA(
                JMSFactoryType.CF,
                transportConfiguration);
         
        //The queue name should match the jms-queue name in standalone.xml
        Topic topic = HornetQJMSClient.createTopic("testTopic");
        Connection connection;
        try {
            connection = factory.createConnection();
            Session session = connection.createSession(
                        false,
                        QueueSession.AUTO_ACKNOWLEDGE);
             
            MessageProducer producer = session.createProducer(topic);   
            //1. Sending TextMessage to the Queue 
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("From", "MIL");
            mapMessage.setString("To", "ROM");
            mapMessage.setString("Date", "05/02/14");
            mapMessage.setString("Key", "8D7FE248-2333-4E12-89C9-90F633C9F6F4");
            producer.send(mapMessage);
            System.out.println(producer.getDestination());
            System.out.println("1. Sent TextMessage to the Queue");
            
            
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }               
    }
}