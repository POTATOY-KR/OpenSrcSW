package scripts;

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

public class makeKeyword {
	public makeKeyword(String inputpath) {//./collection.xml
		try {
			String defaultsrc = "/src";
			if(inputpath.indexOf(".")==0)inputpath = inputpath.substring(1,inputpath.length());
			inputpath = defaultsrc.concat(inputpath);
			String filename = inputpath.substring(inputpath.lastIndexOf("/")+1,inputpath.length());
			inputpath = inputpath.substring(0,inputpath.lastIndexOf("/"));
			int i = 0, j = 0;
			File filess = new File(".");
	        File rootPath = filess.getAbsoluteFile();
	        String rotpath = rootPath.toString();
	        rotpath = rotpath.substring(0, rotpath.length()-1);
			File dir = new File(rotpath + inputpath);
			FileFilter filter = new FileFilter() {
			    public boolean accept(File f) {
			        return f.getName().endsWith(filename);
			    }
			};

			File files[] = dir.listFiles(filter);
			if(files.length==1) {
			    KeywordExtractor ke = new KeywordExtractor();
				DocumentBuilderFactory docFactorykkma = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilderkkma = docFactorykkma.newDocumentBuilder();
				Document dockkma = docBuilderkkma.newDocument();
				Element mainkkma = dockkma.createElement("docs");
				dockkma.appendChild(mainkkma);
			    int indexmax = 0;
			    try {
			        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(files[0]),"UTF-8"));
			        String str;
			        while ((str = in.readLine()) != null) {
			            if(str.indexOf("<doc")!=-1&&str.indexOf("id=")!=-1) {
			            	indexmax++;
			            }
			        }
			        in.close();
			    } catch (IOException e) {
			    	System.out.println(e.getMessage());
			    }
			    if(indexmax>0) {
				    try {
					    KeywordList[] kl = new KeywordList[indexmax];
					    Keyword[] kwrd = new Keyword[indexmax];
					    String gettitles[] = new String[indexmax];
					    String alltext[] = new String[indexmax];
					    String kkmatext[] = new String[indexmax];
					    i = -1;
				        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(files[0]),"UTF-8"));
				        String str;
				        while ((str = in.readLine()) != null) {
				        	if(str.indexOf("<doc")!=-1&&str.indexOf("id=")!=-1) {
				        		i = Integer.parseInt(str.substring(str.indexOf("id=")+4,str.length()-2));
				        	}
				        	if(i!=-1) {
					            if(str.indexOf("title")!=-1) {
					            	gettitles[i] = str.substring(str.indexOf("title")+6, str.indexOf("</title>"));
					            }
					            if(str.indexOf("<body>")!=-1) {
					            	alltext[i] = str.substring(str.indexOf("body")+5, str.indexOf("</body>"));
					            	Element didkkma = dockkma.createElement("doc");
									mainkkma.appendChild(didkkma);
									didkkma.setAttribute("id", Integer.toString(i));
									Element titlekkma = dockkma.createElement("title");
									titlekkma.appendChild(dockkma.createTextNode(gettitles[i]));
									didkkma.appendChild(titlekkma);
									kl[i] = ke.extractKeyword(alltext[i], true);
									kkmatext[i] = "";
									for(j = 0; j < kl[i].size(); j++ ) {
										kwrd[i] = kl[i].get(j);
										kkmatext[i] += kwrd[i].getString() + ":" + kwrd[i].getCnt() + "#";
									}
									kkmatext[i] = kkmatext[i].substring(0, kkmatext[i].length()-1);
									Element bodykkma = dockkma.createElement("body");
									bodykkma.appendChild(dockkma.createTextNode(kkmatext[i]));
									didkkma.appendChild(bodykkma);
					            }
				        	}
				        }
				        in.close();
				    } catch (IOException e) {
				    	System.out.println(e.getMessage());
				    }
					//System.out.println("------------------"+"\n"+"------------------");
				    savexmlfile(dockkma,"src/", "index.xml");
			    }
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	private void savexmlfile(Document getSource, String fileLocation, String fileName) {
		TransformerFactory transformerFactory = null;
		Transformer transformer = null;
		DOMSource source = null;
		StreamResult result = null;
		try {
			transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			source = new DOMSource(getSource);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			result = new StreamResult(new FileOutputStream(new File(fileLocation+fileName)));
			transformer.transform(source, result);
			//System.out.println("\""+fileName+"\" Save Complete");
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
