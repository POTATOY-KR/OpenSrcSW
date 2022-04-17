package scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;

public class searcher {
	private boolean debugprintln;
	private class Clhelp{
		double frnt;
		double bck;
	}
	public searcher(String inputpath, String mainquery) {
		/*질문 입력 -> kkma형태소 활용 -> 나온 키워드를 통해 indexer의 계산식을 각 문서와 계산하여 유사도 계산 -> 상위 3개의 문서 title 출력*/
		/*수학-내적: 두 벡터가 비슷하면 커지고 다르면 0*/
		//System.out.println(mainquery);
		try {
			debugprintln = false;
			int maxlistnum = 3;//출력 상위문서 개수
			KeywordExtractor ke = new KeywordExtractor();
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
				String kkmatext;
				KeywordList kl;
			    Keyword kwrd;
				//System.out.println(dir);
				
				kl = ke.extractKeyword(mainquery, true);
				kkmatext = "";
				if(kl.size()>0) {
					String[] querytext = new String[kl.size()];
					int[] querycnt = new int[kl.size()];
					for(j = 0; j < kl.size(); j++ ) {
						kwrd = kl.get(j);
						querytext[j] = kwrd.getString();
						querycnt[j] = kwrd.getCnt();
						kkmatext += kwrd.getString() + ":" + kwrd.getCnt() + "#";
					}
					kkmatext = kkmatext.substring(0, kkmatext.length()-1);
					//System.out.println(kkmatext);
					ArrayList<Integer> indexnum = new ArrayList<Integer>();
					ArrayList<String> indexttl = new ArrayList<String>();
		    		HashMap<String,String> getindex = readpostfile(inputpath+"/"+filename);
		    		if(getindex!=null) {
					    try {
					        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dir+"/index.xml"),"UTF-8"));
					        String str;
					        while ((str = in.readLine()) != null) {
					            if(str.indexOf("<doc")!=-1&&str.indexOf("id=")!=-1) {
					            	indexnum.add(Integer.parseInt(str.substring(str.indexOf("id=")+4,str.length()-2)));
					            }
					            if(str.indexOf("<title>")!=-1&&str.indexOf("</title>")!=-1) {
					            	indexttl.add(str.substring(str.indexOf("<title>")+7, str.indexOf("</title>")));
					            }
					        }
					        in.close();
					    } catch (IOException e) {
					    	System.out.println(e.getMessage());
					    }
					    if(indexnum.size()>0) {
					    	/*querycnt와 getindex의 해당 querytext 값을 가져오기*/
					    	Iterator<String> it = getindex.keySet().iterator();
							ArrayList<String> indexkey = new ArrayList<String>();
							ArrayList<Double> indexvalue = new ArrayList<Double>();
							while(it.hasNext()) {
								String key = it.next();
								String value = (String)getindex.get(key);
								for(i = 0; i < kl.size(); i++) {
									if(key.equals(querytext[i])) {
										indexkey.add(key);
										String[] str = value.split(" ");
										if(str.length==indexnum.size()*2) {
											for(j = 0; j < indexnum.size(); j++) {
												indexvalue.add(Double.parseDouble(str[(j*2)+1]));
											}
										}
										if(debugprintln)System.out.println(key+" -> " + value);
									}
								}
							}
							if(indexvalue.size()==kl.size()*indexnum.size()) {//필요한 데이터 다 가져옴
								ArrayList<Double> simcalc = CalcSim(querytext,querycnt,indexkey,indexvalue);//CalcSim 작동
								//ArrayList<Double> simcalc = InnerProduct(querytext,querycnt,indexkey,indexvalue);//InnerProduct 작동
								Double[] maxnum = new Double[maxlistnum];//최대 유사도 찾기
								int[] maxindex = new int[maxlistnum];
								int k = 0;
								for (i = 0; i < maxlistnum; i++) {
									maxnum[i] = -1.0;
									maxindex[i] = -1;
								}
								for (i = 0; i < indexnum.size(); i++) {
									//System.out.println(simcalc.get(i));
									for(j = 0; j < maxlistnum; j++) {
										if(maxnum[j]<simcalc.get(i)) {
											for(k = maxlistnum-1; k > j; k--) {
												maxnum[k] = maxnum[k-1];
												maxindex[k] = maxindex[k-1];
											}
											maxnum[j] = simcalc.get(i);
											maxindex[j] = i;
											break;
										}
									}
								}
								for (i = 0; i < maxlistnum; i++) {//최종출력
									//System.out.println(maxindex[i]+" : "+maxnum[i]);
									if(maxindex[i]==-1) {//출력할 상위문서 순위의 개수 > 총 문서 개수
										System.out.println("더 이상 순위를 매길 수 없습니다.");
										break;
									}
									else {
										if(maxnum[i].equals(0.0)) {//질의응답 결과: 유사도가 0일때 출력 안하기
											System.out.println("검색된 문서가 없습니다.");
											break;
										}
										else {
											System.out.println((i+1)+"순위 문서 이름: "+indexttl.get(maxindex[i])+" / 유사도: "+maxnum[i]);
										}
									}
								}
							}
					    }
		    		}
				}
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	private ArrayList<Double> CalcSim(String[] nameget, int[] tcnt, ArrayList<String> idxn, ArrayList<Double> indexp) {
		int k = 0, l = 0, ii = 0, indexsize = indexp.size()/tcnt.length;
		ArrayList<Double> clc = new ArrayList<Double>();//tfcnt.length=idxn.size()
		Clhelp[] clhpidx = new Clhelp[2];
		for (k = 0; k < 2; k++) {
			clhpidx[k] = new Clhelp();
		}
		ArrayList<Clhelp> clhp = new ArrayList<Clhelp>();
		for (k = 0; k < indexsize; k++) {
			clhpidx[0].frnt=0.0;
			clhpidx[0].bck=0.0;
			clhp.add(clhpidx[0]);
			clc.add(0.0);
		}
		for (k = 0; k < tcnt.length; k++) {
			for (l = 0; l < idxn.size(); l++) {
				if(nameget[k].equals(idxn.get(l))) {
					if(debugprintln)System.out.println(k+","+l);
					for(ii = 0; ii < indexsize; ii++) {//l*indexsize~(l+1)*indexsize
						clhpidx[0] = clhp.get(ii);
						clhpidx[1].frnt = Math.pow(Double.valueOf(tcnt[k]),2);//1q 제곱
						clhpidx[1].bck = Math.pow(indexp.get((l*indexsize)+ii), 2);//10 제곱
						clhpidx[0].frnt = Math.round((clhpidx[0].frnt + clhpidx[1].frnt)*100.0)/100.0;//2자리 자르기
						clhpidx[0].bck = Math.round((clhpidx[0].bck + clhpidx[1].bck)*100.0)/100.0;//2자리 자르기
						clhp.set(ii, clhpidx[0]);//갱신
						clc.set(ii, clc.get(ii)+Double.valueOf(tcnt[k])*indexp.get((l*indexsize)+ii));
						clc.set(ii, Math.round(clc.get(ii)*100.0)/100.0);//2자리 자르기
						if(debugprintln)System.out.println(ii+": "+clc.get(ii)+" by "+tcnt[k]+"&"+indexp.get((l*indexsize)+ii));
					}
					break;
				}
			}
		}
		/*분자 값:clc.get(k),분모 값:clhp->clhpidx[1].frnt&clhpidx[1].bck(제곱근 이전)*/
		double clhpdb;
		for(k =  0; k < indexsize; k++) {
			clhpidx[1] = clhp.get(k);
			clhpdb = Math.sqrt(clhpidx[1].frnt)*Math.sqrt(clhpidx[1].bck);
			if(clhpdb>0.0) {
				clc.set(k, clc.get(k)/clhpdb);
				clc.set(k, Math.round(clc.get(k)*100.0)/100.0);//2자리 자르기
			}
			else if(clhpdb==0.0) {
				System.out.println(k+" -> 분모=0");
			}
			else {
				System.out.println("오류");
			}
		}
		return clc;
	}
	private ArrayList<Double> InnerProduct(String[] tfnm, int[] tfcnt, ArrayList<String> indexn, ArrayList<Double> indexp) {
		/*두 벡터 내적 활용: 쿼리 벡터 엘리먼트 4개 (1,1,1,1) X index.post 인덱스*/
		int k = 0, l = 0, ii = 0, indexsize = indexp.size()/tfcnt.length;
		ArrayList<Double> clc = new ArrayList<Double>();//tfcnt.length=indexn.size()
		for (k = 0; k < indexsize; k++)clc.add(0.0);
		for (k = 0; k < tfcnt.length; k++) {
			for (l = 0; l < indexn.size(); l++) {
				if(tfnm[k].equals(indexn.get(l))) {
					if(debugprintln)System.out.println(k+","+l);
					for(ii = 0; ii < indexsize; ii++) {//l*indexsize~(l+1)*indexsize
						clc.set(ii, clc.get(ii)+Double.valueOf(tfcnt[k])*indexp.get((l*indexsize)+ii));
						clc.set(ii, Math.round(clc.get(ii)*100.0)/100.0);//2자리 자르기
						if(debugprintln)System.out.println(ii+": "+clc.get(ii)+" by "+tfcnt[k]+"&"+indexp.get((l*indexsize)+ii));
					}
					break;
				}
			}
		}
		return clc;
	}
	private HashMap<String,String> readpostfile(String filelo) {
		try {
			if(filelo.indexOf("/")==0)filelo = filelo.substring(1, filelo.length());
			FileInputStream fileStream = new FileInputStream(filelo);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);
			Object object = objectInputStream.readObject();
			objectInputStream.close();
	//		System.out.println("읽어온 객체의 타입: "+object.getClass());
			HashMap<String,String> readhash = (HashMap)object;/*
			Iterator<String> it = readhash.keySet().iterator();
			while(it.hasNext()) {
				String key = it.next();
				String value = (String)readhash.get(key);
				System.out.println(key+" -> " + value);
			}*/
			return readhash;
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}
