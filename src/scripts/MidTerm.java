package scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MidTerm {
	private boolean fdebug = false;
	private class snip{
		int tid;
		String title;
		String snippet;
		int cnt;
	}
	public MidTerm(String inputpath, String mainquery) {
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
				String kkmatext = "";
			    int indexmax = 0;
			    ArrayList<Integer> indexnum = new ArrayList<Integer>();
			    KeywordList klquery;
			    Keyword kwrd;
			    KeywordExtractor ke = new KeywordExtractor();
			    klquery = ke.extractKeyword(mainquery, true);
			    if(klquery.size()>0) {
			    	ArrayList<snip> finalsnippet = new ArrayList<snip>();
					String[] querytext = new String[klquery.size()];
					int[] querycnt = new int[klquery.size()];
					for(j = 0; j < klquery.size(); j++ ) {
						kwrd = klquery.get(j);
						querytext[j] = kwrd.getString();
						querycnt[j] = kwrd.getCnt();
						kkmatext += kwrd.getString() + " ";
					}
					kkmatext = kkmatext.substring(0, kkmatext.length()-1);
					if(fdebug)System.out.println(kkmatext);
					try {
				        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(files[0]),"UTF-8"));
				        String str;
				        while ((str = in.readLine()) != null) {
				            if(str.indexOf("<doc")!=-1&&str.indexOf("id=")!=-1) {
				            	indexmax++;
				            	indexnum.add(Integer.parseInt(str.substring(str.indexOf("id=")+4,str.length()-2)));
				            }
				        }
				        in.close();
				    } catch (IOException e) {
				    	System.out.println(e.getMessage());
				    }
				    if(indexmax>0) {
					    try {
						    String gettitles[] = new String[indexmax];
						    String alltext[] = new String[indexmax];
						    i = -1;
					        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(files[0]),"UTF-8"));
					        String str;
					        int getidnum = -1;
					        
					        while ((str = in.readLine()) != null) {
					        	if(str.indexOf("<doc")!=-1&&str.indexOf("id=")!=-1) {
					        		getidnum = Integer.parseInt(str.substring(str.indexOf("id=")+4,str.length()-2));
					        		//System.out.println(Integer.parseInt(str.substring(str.indexOf("id=")+4,str.length()-2)));
					        		i++;
					        	}
					        	if(i!=-1) {
						            if(str.indexOf("title")!=-1) {
						            	gettitles[i] = str.substring(str.indexOf("title")+6, str.indexOf("</title>"));
						            }
						            if(str.indexOf("<body>")!=-1) {
						            	alltext[i] = str.substring(str.indexOf("body")+5, str.indexOf("</body>"));
						            	snip getres = new snip();
						            	getres = showSnippet(getidnum,gettitles[i],alltext[i],querytext);
						            	finalsnippet.add(getres);
						            }
					        	}
					        }
					        in.close();
					    } catch (IOException e) {
					    	System.out.println(e.getMessage());
					    }
					    //snippet 정렬 및 출력
					    for(i = 0; i < finalsnippet.size() - 1; i++) {
					    	for(j = i+1; j < finalsnippet.size(); j++) {
					    		if(finalsnippet.get(i).tid>finalsnippet.get(j).tid) {
					    			snip swaps = new snip();
					    			snip swapy = new snip();
					    			swaps = finalsnippet.get(i);
					    			swapy = finalsnippet.get(j);
					    			finalsnippet.set(i, swapy);
					    			finalsnippet.set(j, swaps);
					    		}
					    	}
					    }
					    for(i = 0; i < finalsnippet.size(); i++) {
					    	if(finalsnippet.get(i).cnt == 0) {//매칭점수 0
					    		//출력안함
					    	}
					    	else if(finalsnippet.get(i).cnt>0){
					    		System.out.println(finalsnippet.get(i).title+", "+finalsnippet.get(i).snippet+", "+finalsnippet.get(i).cnt);
					    	}
					    }
						//System.out.println("------------------"+"\n"+"------------------");
				    }
			    
			    }
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	private snip showSnippet(int idn, String idtitle, String data, String[] cpdata) {
		snip resultsnip = new snip();
		resultsnip.tid = idn;
		resultsnip.title = idtitle;
		resultsnip.cnt = 0;
		resultsnip.snippet = "";
		int tcnt = 0;
		String tsnippet = "";
		int pos = data.length();
		int mas = cpdata.length;
		int k = 0, l = 0, ii = 0, jj = 0;
		for (k = 0; k < pos-30; k++) {
			tsnippet = data.substring(k, k+30);
			tcnt = 0;
			for(l = 0; l < mas; l++) {
				int lth = cpdata[l].length();
				for (ii = 0; ii < 30-lth; ii++) {
					String queryd = data.substring(k+ii,k+ii+lth);
					if(queryd.equals(cpdata[l])) {
						tcnt++;
					}
				}
			}
			if(tcnt>resultsnip.cnt) {
				if(fdebug)System.out.println(tcnt+" & "+tsnippet);
				resultsnip.cnt = tcnt;
				resultsnip.snippet = tsnippet;
			}
		}
		return resultsnip;
	}
}
