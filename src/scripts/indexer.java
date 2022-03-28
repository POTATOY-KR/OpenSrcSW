package scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
public class indexer {
	public indexer(String inputpath) {
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
				ArrayList<Integer> indexnum = new ArrayList<Integer>();
	    		HashMap<String,String> makeindex = new HashMap<String,String>();
			    try {
			        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(files[0]),"UTF-8"));
			        String str;
			        while ((str = in.readLine()) != null) {
			            if(str.indexOf("<doc")!=-1&&str.indexOf("id=")!=-1) {
			            	indexnum.add(Integer.parseInt(str.substring(str.indexOf("id=")+4,str.length()-2)));
			            }
			        }
			        in.close();
			    } catch (IOException e) {
			    	System.out.println(e.getMessage());
			    }
			    if(indexnum.size()>0) {
			    	i = -1;
			    	String alltext[] = new String[indexnum.size()];
			    	ArrayList<String> alldataname = new ArrayList<String>();
			    	ArrayList<ArrayList<Integer>> alldatas = new ArrayList<ArrayList<Integer>>();
			    	try {
				        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(files[0]),"UTF-8"));
				        String str;
				        while ((str = in.readLine()) != null) {
				            if(str.indexOf("<doc")!=-1&&str.indexOf("id=")!=-1) {
				            	i++;
				            }
				            if(i!=-1) {
				            	if(str.indexOf("<body>")!=-1) {
				            		alltext[i] = str.substring(str.indexOf("body")+5, str.indexOf("</body>"));
				            		String[] textsp = alltext[i].split("#");
				            		for (j = 0; j < textsp.length; j++) {
				            			String[] sphelp = textsp[j].split(":");
				            			if(sphelp.length==2) {
				            				ArrayList<Integer> datashelp = new ArrayList<Integer>();
				            				if(alldataname.contains(sphelp[0])) {//중복단어
				            					int whdata = alldataname.indexOf(sphelp[0]);
				            					datashelp = alldatas.get(whdata);
				            					datashelp.add(indexnum.get(i));
				            					datashelp.add(Integer.parseInt(sphelp[1]));
				            					alldatas.set(whdata, datashelp);
				            				}
				            				else {//중복x
				            					alldataname.add(sphelp[0]);
				            					datashelp.add(indexnum.get(i));
				            					datashelp.add(Integer.parseInt(sphelp[1]));
				            					alldatas.add(datashelp);
				            				}
				            			}
				            		}
				            	}
				            }
				        }
				    	//System.out.println(alldataname.size());
				        ArrayList<Integer> sophelp = new ArrayList<Integer>();
				        int k = 0;
				        for(i = 0; i < alldataname.size(); i++) {
				        	sophelp = alldatas.get(i);
				        	if(sophelp.size()>0) {
				        		String poststr = "";
				        		double[] postnum = new double[indexnum.size()];
				        		//sophelp*ln(indexnum.size()/(sophelp.size()/2)) 값 세자리에서 반올림
				        		for(j = 0; j < indexnum.size(); j++) {//문서 y
			        				postnum[j] = 0.0;
				        			for(k = 0; k < sophelp.size(); k+=2) {//단어빈도
				        				if(sophelp.get(k).equals(indexnum.get(j))) {
				        					double helpln = Double.valueOf(indexnum.size()/(sophelp.size()/2));
				        					postnum[j] = Double.valueOf(sophelp.get(k+1))*Math.log(helpln);
				        					postnum[j] = Math.round(postnum[j]*100.0)/100.0;//2자리 자르기
				        					break;
				        				}
				        			}
				        			
				        			if(poststr.equals(""))poststr = indexnum.get(j)+" "+String.valueOf(postnum[j]);
				        			else poststr = poststr.concat(" " + indexnum.get(j)+" "+String.valueOf(postnum[j]));
				        		}
			        			//System.out.println(poststr);
			        			makeindex.put(alldataname.get(i), poststr);
				        	}
				        }
				        savepostfile(makeindex,"src/", "index.post");
				        //readpostfile("src/index.post");
				        in.close();
				    } catch (IOException e) {
				    	System.out.println(e.getMessage());
				    }
			    }
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	private void savepostfile(HashMap<String,String> getSource, String fileLocation, String fileName) {
		try {
			FileOutputStream fileStream = new FileOutputStream(fileLocation+fileName);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);
			objectOutputStream.writeObject(getSource);
			objectOutputStream.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	private void readpostfile(String filelo) {
		try {
			FileInputStream fileStream = new FileInputStream(filelo);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);
			Object object = objectInputStream.readObject();
			objectInputStream.close();
			System.out.println("읽어온 객체의 타입: "+object.getClass());
			HashMap<String,String> readhash = (HashMap)object;
			Iterator<String> it = readhash.keySet().iterator();
			while(it.hasNext()) {
				String key = it.next();
				String value = (String)readhash.get(key);
				System.out.println(key+" -> " + value);
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
