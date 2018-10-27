/**
 * 
 */
package uk.co.alvagem.mpdclient.application;

import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.co.alvagem.mpdclient.client.MPDClientException;

/**
 * Parser for ASX metadata files.
 * see http://msdn.microsoft.com/en-us/library/ms910265.aspx
 * <ASX version = "3.0">
 * <ENTRY>
 * <REF HREF = "mms://wm.bbc.co.uk/support/redirect.asf?BBC-UID=35b3390a09ac1433b73e52f0c1dbfec73b97b456f000d10434ef69f70d8601ef_n&amp;SSO2-UID=" />
 * </ENTRY>
 * </ASX>
 * @author bruce.porteous
 *
 */
class ASXParser implements Parser{
	public final static String CONTENT_TYPE = "video/x-ms-asf";
	
	private final static String ABSTRACT = "ABSTRACT";
	private final static String AUTHOR= "AUTHOR"; 
	private final static String ASX= "ASX"; 
	private final static String BASE= "BASE"; 
	private final static String COPYRIGHT= "COPYRIGHT"; 
	private final static String DURATION= "DURATION"; 
	private final static String ENDMARKER= "ENDMARKER"; 
	private final static String ENTRY= "ENTRY"; 
	private final static String ENTRYREF= "ENTRYREF"; 
	private final static String HREF= "HREF"; 
	private final static String MOREINFO= "MOREINFO"; 
	private final static String PARAM= "PARAM"; 
	private final static String REF= "REF"; 
	private final static String REPEAT= "REPEAT"; 
	private final static String STARTMARKER= "STARTMARKER"; 
	private final static String STARTTIME= "STARTTIME"; 
	private final static String TITLE= "TITLE";
	
	private String href = null;
	
	@Override
	public URL getTarget() throws Exception {
		return new URL(href);
	}
	
	@Override
	public void parse(URLConnection connection) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(connection.getInputStream());
		
		document.normalizeDocument();
		
		Element root = document.getDocumentElement();
		String rootName = root.getNodeName();
		System.out.println("Root element :" + rootName);
		if(rootName.equalsIgnoreCase(ASX)) {
			parseASX(root);
		} else {
			throw new MPDClientException("Expected ASX as root node in file, found " + rootName);
		}
		
	}

	/**
	 * Parses the ASX element.
	 * Valid children are: ABSTRACT, AUTHOR, BASE, COPYRIGHT, DURATION, ENTRY, ENTRYREF, MOREINFO, PARAM, REPEAT, TITLE
	 * @param e
	 */
	private void parseASX(Element e) throws Exception{
		NodeList nList = e.getChildNodes();
		for(int i=0; i<nList.getLength(); ++i){
			Node node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) node;
				String name = child.getNodeName().toUpperCase();
				if(name.equals(ABSTRACT)) {
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(AUTHOR)) {
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(BASE)){
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(COPYRIGHT)) {
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(DURATION)) {
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(ENTRY)) {
					parseEntry(child);
				} else if(name.equals(ENTRYREF)){
					throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(MOREINFO)){
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(PARAM)) {
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(REPEAT)) {
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(TITLE)) {
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else {
					throw new MPDClientException("Unknown ASX field " + name);
				}
			}
	 
		}
		
	}

	/**
	 * Parses the ENTRY element.
	 * @param e
	 * @throws Exception
	 */
	private void parseEntry(Element e) throws Exception {
		assert(e.getNodeName().equalsIgnoreCase(ENTRY));
		NodeList nList = e.getChildNodes();
		for(int i=0; i<nList.getLength(); ++i){
			Node node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) node;
				String name = child.getNodeName().toUpperCase();
				if(name.equals(ABSTRACT)) { 
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(AUTHOR)) { 
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(BASE)) { 
					throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(COPYRIGHT)) { 
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(DURATION)) { 
					throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(ENDMARKER)) { 
					throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(MOREINFO)) { 
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(PARAM)) { 
					throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(REF)) { 
					parseRef(child);
				} else if(name.equals(STARTMARKER)) { 
					throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(STARTTIME)) { 
					throw new MPDClientException("Unsupported ASX tag " + name);
				} else if(name.equals(TITLE)) {
					//throw new MPDClientException("Unsupported ASX tag " + name);
				} else {
					throw new MPDClientException("Unknown ASX field " + name);
				}
			}
		}

	}
	
	/**
	 * Parse REF element.
	 * Required attribute HREF
	 * Valid children are: DURATION, ENDMARKER, STARTMARKER, STARTTIME
	 * @param e
	 */
	private void parseRef(Element e) throws Exception{
		assert(e.getNodeName().equalsIgnoreCase(REF));

		NamedNodeMap attrs = e.getAttributes();
		for(int i=0; i<attrs.getLength(); ++i){
			Node node = attrs.item(i);
			assert(node.getNodeType() == Node.ATTRIBUTE_NODE); 

			if(node.getNodeName().equalsIgnoreCase(HREF)){
				href = node.getNodeValue();
				System.out.println("HREF: " + href);
			} else {
				throw new MPDClientException("Unknown attribute on REF: " + node.getNodeName());
			}
			
		}

		NodeList nList = e.getChildNodes();
		for(int i=0; i<nList.getLength(); ++i){
			Node node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) node;
				String name = child.getNodeName().toUpperCase();
				throw new MPDClientException("Unsupported ASX tag " + name);
			}
		}
	}
	
}