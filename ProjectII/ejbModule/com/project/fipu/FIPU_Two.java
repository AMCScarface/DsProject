package com.project.fipu;

import java.net.URI;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Message-Driven Bean implementation class for: FIPU Nr. 1
 * This class represents the Message-Driven Bean that will consume, after receiving a message from the CFE, the 
 * Webservice provided by api.flightstats.com.
 * @author Group Sascha Scatà, Jan Raphael Schmid Niederkofler, Christine Lunger, Benjamin Egger
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/MyTopic") })
public class FIPU_Two implements MessageListener {

	
	private ParseFIPU_Two parseResp;
	
	/**
	 * Set up the Parsing class for this MessageDriven-Bean
	 */
    public FIPU_Two() {
    	parseResp = new ParseFIPU_Two();
    }
	
    /**
	 * On incoming message, this method will extract the information previously sent by the CFE and then 
	 * through an external HttpClient consume the Webservice; note, that the provided Webservice will sent
	 * back an XML-String, which will be parsed by the parsing class linked to this class 
	 */
    public void onMessage(Message message) {
        System.out.println("ok");
		 try {
		        if (message instanceof MapMessage) {
		            MapMessage map = (MapMessage) message;
		            String from = map.getString("From");
		            String to = map.getString("To");
		            
		            //Date must be converted in order to get processed by this Webservice-API:
		            Date d = new Date(map.getString("Date"));   		
		    		DateFormat dfmt = new SimpleDateFormat("yyyy/MM/dd");
					String date = dfmt.format(d);
		            
		            //connection to the webservice
		            HttpClient client = HttpClientBuilder.create().build();
		            URIBuilder builder = new URIBuilder("https://api.flightstats.com/flex/schedules/rest/v1/xml/from/"+from+"/to/"+to+"/departing/"+date+"?appId=226c9816&appKey=454d533a34d647b511581cd57ba9f8c8");
		            URI uri = builder.build();
		            HttpGet request = new HttpGet(uri);
		          	HttpResponse response = client.execute(request);
		          	HttpEntity entity = response.getEntity();
		          	if (entity != null) {
		          		//Get the respone from the Webservice, obtaining a String:
		          		Document doc = parseResp.loadXMLFromString(EntityUtils.toString(entity));
		          		String res = parseResp.parseRespone(doc);
		          		connectToQueue(res);
		          	}
		        } 
		    } catch (JMSException e) {
		        e.printStackTrace();
		    } catch (Throwable e) {
		    	e.printStackTrace();
		    }
    }

    /**
	 * This method will, immediately after getting the parsed reponse from the Webservice, connect to the queue which
	 * will be used to send a response to the CFE.
	 * @param entity - The parsed respone of the Webservice
	 * @throws NamingException - Naming exception related to the Connectionfactory
	 * @throws JMSException - Other common erros related to the JMS
	 */
    public void connectToQueue(String entity) throws NamingException, JMSException {
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
				msg.setText(entity);
				msg.setStringProperty("NumberOfFIPU", "FIPU Nr. 2");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sender.send(msg);
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
