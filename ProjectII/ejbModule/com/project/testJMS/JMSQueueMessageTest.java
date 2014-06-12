package com.project.testJMS;

import org.activemq.filter.mockrunner.Filter;
import org.activemq.selector.mockrunner.SelectorParser;

import junit.framework.TestCase;

import com.mockrunner.jms.ConfigurationManager;
import com.mockrunner.jms.DestinationManager;
import com.mockrunner.mock.jms.MockQueue;
import com.mockrunner.mock.jms.MockQueueConnection;
import com.mockrunner.mock.jms.MockTextMessage;

/**
 * With support of the MockrunnerAPI for JMS-Messaging testing, we could through MockMessages
 * test several properties and states of the current MockMessage and the Queue which handles
 * these Mockmessages - Remember that the connection and queue are just mocks and so the processes
 * are simulated instead of using a JBoss AS connection.
 * @author Group Sascha Scatà, Jan Raphael Schmid Niederkofler, Christine Lunger, Benjamin Egger
 *
 */
public class JMSQueueMessageTest extends TestCase
{
	//Create a MockConnection to the queue:
    private MockQueueConnection connection;
    private MockQueue queue;
    private SelectorParser parser;
    private MockTextMessage message;
    
    /**
     * Before each testcase, the setup-method will prepare a connection to the queue which
     * will receive a MockMessage and will process it for further tests
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        DestinationManager destManager = new DestinationManager();
        ConfigurationManager confManager = new ConfigurationManager();
        connection = new MockQueueConnection(destManager, confManager);
        //Testing the mock-'testQueue':
        queue = new MockQueue("testQueue");
        parser = new SelectorParser();
        message = new MockTextMessage();
    }

    /**
     * In this testcase, 3 MockMessage will be sent to the queue; the three Messages will be 
     * consumed sequentially, testing at each consumed message the count of the remaining 
     * messages in the queue and assert if the queue is emtpy
     * @throws Exception - Error could be arise at message adding
     */
    public void testGetMessageList() throws Exception
    {
    	//Every time a message is consumed, check if empty:
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.getCurrentMessageList().size());
        assertEquals(0, queue.getReceivedMessageList().size());
        assertNull(queue.getMessage());
        //3 Messages to be sent to queue:
        queue.addMessage(new MockTextMessage("message1"));
        queue.addMessage(new MockTextMessage("message2"));
        queue.addMessage(new MockTextMessage("message3"));
        assertFalse(queue.isEmpty());
        //3 messages are in queue:
        assertEquals(3, queue.getCurrentMessageList().size());
        assertEquals(3, queue.getReceivedMessageList().size());
        assertEquals(new MockTextMessage("message1"), queue.getMessage());
        assertFalse(queue.isEmpty());
        //2 messages are in queue:
        assertEquals(2, queue.getCurrentMessageList().size());
        assertEquals(3, queue.getReceivedMessageList().size());
        assertEquals(new MockTextMessage("message2"), queue.getMessage());
        assertFalse(queue.isEmpty());
        //1 messages are in queue:
        assertEquals(1, queue.getCurrentMessageList().size());
        assertEquals(3, queue.getReceivedMessageList().size());
        assertEquals(new MockTextMessage("message3"), queue.getMessage());
        assertTrue(queue.isEmpty());
        //No more messages are in the queue -> empty:
        assertEquals(0, queue.getCurrentMessageList().size());
        assertEquals(3, queue.getReceivedMessageList().size());
        assertNull(queue.getMessage());
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
        queue.addMessage(message);
        //Check if the message was filtered:
        assertTrue(stringPropertyFilter1.matches(queue.getMessage()));     
        
        //Set the filter-check for msg2:
        Filter stringPropertyFilter2 = parser.parse("stringProperty = 'msg2'");
        message.setStringProperty("stringProperty", "msg2");
        queue.addMessage(message);
        //Check if the message was filtered:
        assertTrue(stringPropertyFilter2.matches(queue.getMessage()));     
        
        //Set the filter-check for msg3:
        Filter stringPropertyFilter3 = parser.parse("stringProperty = 'msg3'");
        message.setStringProperty("stringProperty", "msg3");
        queue.addMessage(message);
        //Check if the message was filtered:
        assertTrue(stringPropertyFilter3.matches(queue.getMessage()));     
    }
}