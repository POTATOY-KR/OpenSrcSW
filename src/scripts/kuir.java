package scripts;

public class kuir{
	public static void main(String[] args) {
		String command = args[0];
		String path = args[1];
		if(command.equals("-c")) {//html �б�+collection.xml �����  -c ./data/
			makeCollection collection = new makeCollection(path);
		}
		else if(command.equals("-k")) {//collection.xml �б�+index.xml �����  -k ./collection.xml
			makeKeyword keyword = new makeKeyword(path);
		}
		else if(command.equals("-i")) {//index.xml �б�+index.post �����  -i ./index.xml
			indexer makepost = new indexer(path);
		}
		else if(command.equals("-s")) {//-s ./index.post -q "�а��� �ҹ�"
			if(args.length>2) {
				String pluscommand = args[2];
				String question = args[3];//�Է¿��� ���Ⱑ ������ "" �ٿ��� args[3] �ȿ� �� ���� ������� ����
				if(pluscommand.equals("-q")) {
					searcher searcher = new searcher(path,question);
				}
			}
		}
		else if(command.equals("-m")) {//-m ./collection.xml -q "�а��� ���� ����"
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