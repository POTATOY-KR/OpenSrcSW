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

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Htmlcnt{
	public static void main(String[] args) {
		makecollect();
	}
	public static void makecollect() {
		try {
		int i = 0, j = 0;
	    KeywordExtractor ke = new KeywordExtractor();
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
		DocumentBuilderFactory docFactorykkma = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilderkkma = docFactorykkma.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Document dockkma = docBuilderkkma.newDocument();
		Element main = doc.createElement("docs");
		Element mainkkma = dockkma.createElement("docs");
		doc.appendChild(main);
		dockkma.appendChild(mainkkma);
	    KeywordList[] kl = new KeywordList[files.length];
	    Keyword[] kwrd = new Keyword[files.length];
		for (i = 0; i < files.length; i++) {
		    //System.out.println("file: " + files[i]);
		    String gettitles = "";
		    String alltext = "";
		    String kkmatext = "";
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
		    Element didkkma = dockkma.createElement("doc");
			main.appendChild(did);
			mainkkma.appendChild(didkkma);
			did.setAttribute("id", Integer.toString(i));
			didkkma.setAttribute("id", Integer.toString(i));
			Element title = doc.createElement("title");
			Element titlekkma = dockkma.createElement("title");
			title.appendChild(doc.createTextNode(gettitles));
			titlekkma.appendChild(dockkma.createTextNode(gettitles));
			did.appendChild(title);
			didkkma.appendChild(titlekkma);
			Element body = doc.createElement("body");
			body.appendChild(doc.createTextNode(alltext));
			did.appendChild(body);
			kl[i] = ke.extractKeyword(alltext, true);
			for(j = 0; j < kl[i].size(); j++ ) {
				kwrd[i] = kl[i].get(j);
				kkmatext += kwrd[i].getString() + ":" + kwrd[i].getCnt() + "#";
				//System.out.println(kwrd[i].getString() + "\t" + kwrd[i].getCnt());
			}
			kkmatext = kkmatext.substring(0, kkmatext.length()-1);
			Element bodykkma = dockkma.createElement("body");
			bodykkma.appendChild(dockkma.createTextNode(kkmatext));
			didkkma.appendChild(bodykkma);
			//System.out.println("------------------"+"\n"+"------------------");
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		TransformerFactory transformerFactorykkma = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		Transformer transformerkkma = transformerFactorykkma.newTransformer();
		DOMSource source = new DOMSource(doc);
		DOMSource sourcekkma = new DOMSource(dockkma);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformerkkma.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult result = new StreamResult(new FileOutputStream(new File("src/data/collection.xml")));
		StreamResult resultkkma = new StreamResult(new FileOutputStream(new File("src/data/index.xml")));
		transformer.transform(source, result);
		transformerkkma.transform(sourcekkma, resultkkma);
		//System.out.println("END");
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}