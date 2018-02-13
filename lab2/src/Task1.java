import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Task1 {

    public static void main(String[] args) {
	    Scanner sc = new Scanner(System.in);
	    StringBuilder text = new StringBuilder();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

	    while(sc.hasNextLine()) {
	        text.append(sc.nextLine()).append("\n");
        }

		try {

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document xml = dBuilder.newDocument();
			Element xmlRoot = xml.createElement("xml");
			xml.appendChild(xmlRoot);

			Element xmlText = xml.createElement("txt");
			xmlText.setTextContent("\n" + text.toString());
			xmlRoot.appendChild(xmlText);

			Pattern postIdxPtr = Pattern.compile("(\\d{3})(\\d{3})");
			String []lines = text.toString().split("\n");

			for(int i = 0; i < lines.length; i++) {
				Matcher m = postIdxPtr.matcher(lines[i]);

				while (m.find()) {
					Element xmlNum = xml.createElement("num");
					xmlNum.setTextContent(m.group());
					Attr position = xml.createAttribute("pos");
					position.setValue(String.format("%d, %d", i + 1,  m.start() + 1));
					xmlNum.setAttributeNode(position);

					xmlRoot.appendChild(xmlNum);
				}
			}

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer xmlTransformer = tFactory.newTransformer();
			xmlTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xmlTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource domSource = new DOMSource(xml);
			StreamResult result = new StreamResult(System.out);
			xmlTransformer.transform(domSource, result);
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}
}