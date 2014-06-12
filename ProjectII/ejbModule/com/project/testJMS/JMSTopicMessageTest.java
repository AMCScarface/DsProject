package com.project.testJMS;

import org.activemq.filter.mockrunner.Filter;
import org.activemq.selector.mockrunner.SelectorParser;

import junit.framework.TestCase;

import com.mockrunner.jms.ConfigurationManager;
import com.mockrunner.jms.DestinationManager;
import com.mockrunner.mock.jms.MockMapMessage;
import com.mockrunner.mock.jms.MockTextMessage;
import com.mockrunner.mock.jms.MockTopic;
import com.mockrunner.mock.jms.MockTopicConnection;

/**
 * With support of the MockrunnerAPI for JMS-Messaging testing, we could through MockMessages
 * test several properties and states of the current MockMessage and the Topic which handles
 * these Mockmessages - Remember that the connection and topic are just mocks and so the processes
 * are simulated instead of using a JBoss AS connection.
 * @author Group Sascha Scatà, Jan Raphael Schmid Niederkofler, Christine Lunger, Benjamin Egger
 *
 */
public class JMSTopicMessageTest extends TestCase
{
	//Create a MockConnection to the topic:
    private MockTopicConnection connection;
    private MockTopic topic;
    private SelectorParser parser;
    private MockTextMessage message;

    protected void setUp() throws Exception
    {
        super.setUp();
        DestinationManager destManager = new DestinationManager();
        ConfigurationManager confManager = new ConfigurationManager();
        connection = new MockTopicConnection(destManager, confManager);
        //Testing the mock-'testTopic':
        topic = new MockTopic("testTopic");
        parser = new SelectorParser();
        message = new MockTextMessage();
    }

    /**
     * In this testcase, 3 MockMessage will be sent to the topic; the three Messages will be 
     * consumed sequentially, testing at each consumed message the count of the remaining 
     * messages in the topic and assert if the topic is emtpy
     * @throws Exception - Error could be arise at message adding
     */
    public void testGetMessageList() throws Exception
    {
    	//Every time a message is consumed, check if empty:
        assertTrue(topic.isEmpty());
        assertEquals(0, topic.getCurrentMessageList().size());
        assertEquals(0, topic.getReceivedMessageList().size());
        assertNull(topic.getMessage());
        //3 Messages to be sent to topic:
        topic.addMessage(new MockTextMessage("message1"));
        topic.addMessage(new MockTextMessage("message2"));
        topic.addMessage(new MockTextMessage("message3"));
        assertFalse(topic.isEmpty());
        //3 messages are in topic:
        assertEquals(3, topic.getCurrentMessageList().size());
        assertEquals(3, topic.getReceivedMessageList().size());
        assertEquals(new MockTextMessage("message1"), topic.getMessage());
        assertFalse(topic.isEmpty());
        //2 messages are in topic:
        assertEquals(2, topic.getCurrentMessageList().size());
        assertEquals(3, topic.getReceivedMessageList().size());
        assertEquals(new MockTextMessage("message2"), topic.getMessage());
        assertFalse(topic.isEmpty());
        //1 messages are in topic:
        assertEquals(1, topic.getCurrentMessageList().size());
        assertEquals(3, topic.getReceivedMessageList().size());
        assertEquals(new MockTextMessage("message3"), topic.getMessage());
        assertTrue(topic.isEmpty());
        //No more messages are in the topic -> empty:
        assertEquals(0, topic.getCurrentMessageList().size());
        assertEquals(3, topic.getReceivedMessageList().size());
        assertNull(topic.getMessage());
    }
    
    /**
     * In this testcase, the filter of the message will be tested with again 3 mockmessages that
     * are created with a special filter for consuming.
     * @throws Exception - exception of adding a message or setting a string-property
     */
    public void testFilterMessage() throws Exception
    {        
    	//Set the filter-check for msg1:
        Filter stringPropertyFilter1 = parser.parse("stringProperty = 'msg1'");
        message.setStringProperty("stringProperty", "msg1");
        topic.addMessage(message);
        //Check if the message was filtered:
        assertTrue(stringPropertyFilter1.matches(topic.getMessage()));     
        
        //Set the filter-check for msg2:
        Filter stringPropertyFilter2 = parser.parse("stringProperty = 'msg2'");
        message.setStringProperty("stringProperty", "msg2");
        topic.addMessage(message);
        //Check if the message was filtered:
        assertTrue(stringPropertyFilter2.matches(topic.getMessage()));     
        
        //Set the filter-check for msg3:
        Filter stringPropertyFilter3 = parser.parse("stringProperty = 'msg3'");
        message.setStringProperty("stringProperty", "msg3");
        topic.addMessage(message);
        //Check if the message was filtered:
        assertTrue(stringPropertyFilter3.matches(topic.getMessage()));     
    }
    
    /**
     * Since the Topic in the project will handle MapMesssages, it is necessary to provide also a testcase
     * for this kind of message-processing
     * @throws Exception - can arise at setProperties of MapMessage
     */
    public void testEquals() throws Exception
    {
        MockMapMessage message1 = new MockMapMessage();
        //Properties of MapMessage Nr.1:
        message1.setInt("name1", 1);
        message1.setString("name2", "text");
        assertTrue(message1.equals(message1));
        MockMapMessage message2 = null;
        //Do some test with an empty MapMessage Nr.2:
        assertFalse(message1.equals(message2));
        message2 = new MockMapMessage();
        assertFalse(message1.equals(message2));
        assertTrue(message2.equals(new MockMapMessage()));
        assertEquals(message2.hashCode(), new MockMapMessage().hashCode());
        //Properties of MapMessage Nr.2:
        message2.setInt("name1", 1);
        message2.setString("name2", "text");
        assertTrue(message1.equals(message2));
        assertTrue(message2.equals(message1));
    }

}
