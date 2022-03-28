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

public class kuir{
	public static void main(String[] args) {
		String command = args[0];
		String path = args[1];
		if(command.equals("-c")) {//html 읽기+collection.xml 만들기  -c ./data/
			makeCollection collection = new makeCollection(path);
		}
		else if(command.equals("-k")) {//collection.xml 읽기+index.xml 만들기  -k ./collection.xml
			makeKeyword keyword = new makeKeyword(path);
		}
		else if(command.equals("-i")) {//index.xml 읽기+index.post 만들기  -i ./index.xml
			indexer makepost = new indexer(path);
		}
	}
	public static void makepostfile() {
		/*ObjectInputStream 이용 역직렬화 hashMap 객체 읽기
		 * key-list(id wgt id wgt)*/
		/*hashmap 객체 저장 순서
		 * fileoutputstream 생성
		 * objectoutputstream생성
		 * haspmap객체저장
		 * objectoutputstream닫기*/
	}
}