package com.project.fipu;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * This class represents the exclusive parsing task for the first FIPU. This parsing class is only
 * working for the webservice provided by the API : api.flightstats.com.
 * @author Group Sascha Scat�, Jan Raphael Schmid Niederkofler, Christine Lunger, Benjamin Egger
 *
 */
public class ParseFIPU_Two {
	
	/**
	 * This method basically convert the given String in a XML-Document for further
	 * parsing tasks 
	 * @param xml - The XML in String-format
	 * @return  - The XML in Document-format
	 * @throws Exception - If the convertion will not work
	 */
	public static Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}
	
	/**
	 * This method will do the actual parsing of the given XML-Document; since the API provided by
	 * api.flightstats.com have this structure, the parsing must be hard-coded in order to extract
	 * the flight information
	 * @param doc - The converted XML-Document
	 * @return String - the extracted flight informations
	 */
	public static String parseRespone(Document doc){
		String cat = "\n";
		NodeList nList = doc.getElementsByTagName("scheduledFlight");
		for (int temp = 0; temp < nList.getLength(); temp++) {			 
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				cat += 
								getValue("departureAirportFsCode", eElement) +"\t" +
								getValue("arrivalAirportFsCode", eElement) + "\t" +
								getValue("departureTime", eElement)+ "\t\t" +
								getValue("arrivalTime", eElement) + "\t\t" +
								getValue("flightNumber", eElement) +"\n";
			}
		}
		return cat;
	}
	
	 private static String getValue(String tag, Element element) {
		 NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		 Node node = (Node) nodes.item(0);
		 return node.getNodeValue();
		 }

}
