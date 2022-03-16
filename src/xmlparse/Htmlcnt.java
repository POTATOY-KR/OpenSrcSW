package xmlparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Htmlcnt{
	public static void main(String[] args) {
		try {
		int i = 0;
		File filess = new File(".");
        File rootPath = filess.getAbsoluteFile();
        String rotpath = rootPath.toString();
        rotpath = rotpath.substring(0, rotpath.length()-1);
		File dir = new File(rotpath + "/src/insert");
		FileFilter filter = new FileFilter() {
		    public boolean accept(File f) {
		        return f.getName().endsWith("html");
		    }
		};

		File files[] = dir.listFiles(filter);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element main = doc.createElement("docs");
		doc.appendChild(main);
		for (i = 0; i < files.length; i++) {
		    //System.out.println("file: " + files[i]);
		    String gettitles = "";
		    String alltext = "";
		    int checklines = -1;
		    try {
		        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(files[i]),"UTF-8"));
		        String str;
		        while ((str = in.readLine()) != null) {
		            if(str.indexOf("title")!=-1) {
		            	gettitles = str.substring(str.indexOf("title")+6, str.indexOf("</"));
		            }
		            if(str.indexOf("</div>")!=-1) {
		            	checklines = 0;
		            	alltext = alltext.substring(0,alltext.length()-1);
		            }
		            if(checklines == 1) {//³»¿ë
		            	alltext += str.substring(str.indexOf("<p>")+3, str.indexOf("</p>"));
		            	alltext += " ";
		            }
		            if(str.indexOf("<div")!=-1)checklines = 1;
		        }
		        in.close();
		    } catch (IOException e) {
		    	System.out.println(e.getMessage());
		    }
		    Element did = doc.createElement("doc");
			main.appendChild(did);
			did.setAttribute("id", Integer.toString(i));
			Element title = doc.createElement("title");
			title.appendChild(doc.createTextNode(gettitles));
			did.appendChild(title);
			Element body = doc.createElement("body");
			body.appendChild(doc.createTextNode(alltext));
			did.appendChild(body);
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult result = new StreamResult(new FileOutputStream(new File("src/data/result.xml")));
		transformer.transform(source, result);
		//System.out.println("END");
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}