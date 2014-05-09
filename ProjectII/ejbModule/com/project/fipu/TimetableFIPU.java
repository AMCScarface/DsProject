package com.project.fipu;


import java.io.IOException;
import java.net.URI;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.*;
import javax.naming.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;




/**
 * Message-Driven Bean implementation class for: Webservice1
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/MyTopic") })
public class TimetableFIPU implements MessageListener {

	/**
	 * Default constructor.
	 */
	public TimetableFIPU() throws Exception {
	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		System.out.println("ok");
		 try {
		        if (message instanceof MapMessage) {
		            MapMessage map = (MapMessage) message;
		            String from = map.getString("From");
		            String to = map.getString("To");
		            String date = map.getString("Date");
		            String key = map.getString("Key");
		            //getTimetable(from, to, date, key);
		            HttpClient client = HttpClientBuilder.create().build();
		            URIBuilder builder = new URIBuilder("http://api.flightlookup.com/otatimetable/v1/TimeTable/");
		            builder.setParameter("key", "8D7FE248-2333-4E12-89C9-90F633C9F6F4");
		            builder.setParameter("From", "MIL");
		            builder.setParameter("To", "ROM");
		            builder.setParameter("Date", "05/02/14");
		            URI uri = builder.build();
		            HttpGet request = new HttpGet(uri);
		          	HttpResponse response = client.execute(request);
		          	HttpEntity entity = response.getEntity();
		          	if (entity != null) {
		          		//System.out.println(EntityUtils.toString(entity));
		          		connectToQueue(entity);
		          	}
		        } 
		    } catch (JMSException e) {
		        e.printStackTrace();
		    } catch (Throwable e) {
		    	e.printStackTrace();
		    }
	}

	public void connectToQueue(HttpEntity entity) throws NamingException, JMSException {
		Context ctx = null;
		QueueConnection connect = null;
		QueueSession session = null;
		Queue queue = null;
		QueueSender sender = null;
		try {
			ctx = new InitialContext();
			QueueConnectionFactory fact = (QueueConnectionFactory) ctx
					.lookup("ConnectionFactory");
			connect = fact.createQueueConnection();
			session = connect.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			try {
				queue = (Queue) ctx.lookup("queue/MyQueue");
			} catch (NameNotFoundException ex) {
				queue = session.createQueue("queue/MyQueue");
				ctx.bind("queue/MyQueue", queue);
			}
			sender = session.createSender(queue);
			connect.start();
			
			
			TextMessage msg = session.createTextMessage();
			try {
				msg.setText(EntityUtils.toString(entity));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sender.send(msg);
			System.out.println("Sending ");
			// System.out.println( "Sending " + msg.toString() );
		} finally {
			try {
				if (null != sender)
					sender.close();
			} catch (Exception ex) {/* ok */
			}
			try {
				if (null != session)
					session.close();
			} catch (Exception ex) {/* ok */
			}
			try {
				if (null != connect)
					connect.close();
			} catch (Exception ex) {/* ok */
			}
			try {
				if (null != ctx)
					ctx.close();
			} catch (Exception ex) {/* ok */
			}
		}
	}
}
