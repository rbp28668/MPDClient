/**
 * 
 */
package uk.co.alvagem.mpdclient.application;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * <tr>
 * <td><a href="http://www.bbc.co.uk/radio1/"><b>BBC Radio 1</b></a></td>
 * <td>National</td>
 * <td><img src="wma.gif" width="12" height="12" alt="Windows Media" /><br /><img src="aacplus.gif" width="12" height="12" alt="aacplus" /></td>
 * <td><a href="http://bbc.co.uk/radio/listen/live/r1.asx">128 Kbps</a><br /><a href="http://www.bbc.co.uk/radio/listen/live/r1_aaclca.pls">128 Kbps</a></td>
 * <td>Top 40/Dance</td>
 * </tr>
 */


/**
 * 
 * @author bruce.porteous
 *
 */
public class HTMLPageParser {

	Document document;
	XPath xpath;
	
	/**
	 * 

	 */
	public HTMLPageParser(InputStream is) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		document = db.parse(is);
		//document.normalizeDocument();
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		xpath = xPathfactory.newXPath();
	}

	
	public RadioChannel[] loadChannels() throws Exception {
	
		List<RadioChannel> channels = new LinkedList<RadioChannel>();

		XPathExpression expr = xpath.compile("//tr");		
		NodeList nl = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		for(int i=0; i<nl.getLength(); ++i){
			Node n = nl.item(i);
			processRow(n, channels);
		}
		
		
		
		return channels.toArray(new RadioChannel[channels.size()]);

	}
	
	/**
	 * @param n
	 * @param channels
	 */
	private void processRow(Node row, List<RadioChannel> channels) throws Exception{
		XPathExpression expr = xpath.compile("td");		
		NodeList nl = (NodeList) expr.evaluate(row, XPathConstants.NODESET);
		// Expecting 5 nodes:
		if(nl.getLength() == 5) {
			// item 0 has name of station
			String name = nl.item(0).getTextContent();
			
			// item 1 has area/region
			String area = nl.item(1).getTextContent();
			
			// item 2 has type icons.
			String[] types = getTypes(nl.item(2));
			
			// 3 has URLS! (and speeds in the link)
			String[] urls = getURLs(nl.item(3));
			String[] speeds = getSpeeds(nl.item(3));
			
			// 4 has comments.
			String comment = nl.item(4).getTextContent();
			
			assert(types.length == urls.length);
			
			for(int i=0; i<types.length; ++i) {
				System.out.println(name + "  (" + area + ", " + types[i] + ", " + speeds[i] + ") | " + urls[i]);
			}
			
		} else {
			System.out.println("Not 5 nodes in " + row.getTextContent() + " has " + nl.getLength());
		}
	}


	/**
	 * @param item
	 * @return
	 */
	private String[] getTypes(Node item) throws Exception {
		//showNode(item);
		XPathExpression expr = xpath.compile("img/@alt");		
		NodeList nl = (NodeList) expr.evaluate(item, XPathConstants.NODESET);
		String[] urls = new String[nl.getLength()];
		for(int i=0; i<nl.getLength(); ++i){
			urls[i] = nl.item(i).getTextContent();
		}
		return urls;
	}



	/**
	 * @param item
	 * @return
	 */
	private String[] getURLs(Node item) throws Exception {
		//showNode(item);

		XPathExpression expr = xpath.compile("a/@href");		
		NodeList nl = (NodeList) expr.evaluate(item, XPathConstants.NODESET);
		String[] types = new String[nl.getLength()];
		for(int i=0; i<nl.getLength(); ++i){
			types[i] = nl.item(i).getTextContent();
		}
		return types;
	}

	private String[] getSpeeds(Node item) throws Exception {
		//showNode(item);

		XPathExpression expr = xpath.compile("a[@href]");		
		NodeList nl = (NodeList) expr.evaluate(item, XPathConstants.NODESET);
		String[] speeds = new String[nl.getLength()];
		for(int i=0; i<nl.getLength(); ++i){
			speeds[i] = nl.item(i).getTextContent();
		}
		return speeds;
	}

	/**
	 * @param item
	 */
	public void showNode(Node item) {
		System.out.println(item.getNodeName() + "-->" + item.getTextContent() + " {");
		NodeList children = item.getChildNodes();
		for(int c = 0; c<children.getLength(); ++c){
			System.out.print( " " + children.item(c).getNodeName());
		}
		System.out.println(" }");
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "C:\\Projects\\workspace\\MPDClient\\WEB-INF\\data\\stations.xml";
		
		try {
			HTMLPageParser parser = new HTMLPageParser(new FileInputStream(path));
			parser.loadChannels();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
