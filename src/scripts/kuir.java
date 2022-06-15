package scripts;

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
		else if(command.equals("-s")) {//-s ./index.post -q "밀가루 소바"
			if(args.length>2) {
				String pluscommand = args[2];
				String question = args[3];//입력에서 띄어쓰기가 있으면 "" 붙여서 args[3] 안에 다 들어가게 만들어질 예정
				if(pluscommand.equals("-q")) {
					searcher searcher = new searcher(path,question);
				}
			}
		}
		else if(command.equals("-m")) {//-m ./collection.xml -q "밀가루 넣은 반죽"
			if(args.length>2) {
				String pluscommand = args[2];
				String question = args[3];
				if(pluscommand.equals("-q")) {
					MidTerm midterm = new MidTerm(path,question);
				}
			}
		}
	}
}