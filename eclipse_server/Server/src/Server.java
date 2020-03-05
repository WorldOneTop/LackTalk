import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//�������� ������ Ŭ���̾�Ʈ�� ��ü �ҽǽ� -1�� �ݼ� ����� �ش� ��ü�� ���Ұ�
//����Ȳ���� ������ ��� Ŭ���̾�Ʈ������  Broken pipe -IOExc ĳġ ��� �� �ش� ��ü ����
//������ ������ Ŭ���̾�Ʈ�� ��ä �ҽǽ�  netSocketExc ĳġ�� ����
//����Ȳ���� ������ ����� ĳġ�� java.net.SocketException: Connection reset ����
//���ۿ뷮�� ��ġ�� ��ǲ�� �´ٸ�
public class Server {
	public static final String FILE_USER="user";//user������ ���̵��.txt�ȿ� ��� �ؽ����� �� ����
	public static final int PORT = 12345;
	static ServerSocket server;
	static ArrayList<ThreadServer> arrayList;//������ ������ü �� ������ ��
	public static void main(String[] args) {
		arrayList = new ArrayList<ThreadServer>();
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println("���� ��Ʈ ���� ���� "+e);
		}
		
		//���� init
		File user_file =  new File(FILE_USER);
		if(!user_file.exists())	user_file.mkdir();		
		
		
		while(true) {
			try {
				arrayList.add(new ThreadServer(server.accept()).start());
			} catch (IOException e) {
				System.out.println("�����Ŵ������� "+e+"IOExc");
			} catch (Exception e) {
				System.out.println("�����Ŵ������� "+e+"Exc");
			}
		}
	}
	public static void killThread(ThreadServer object) {
		System.out.println("�̿��� :"+object.getID()+"(��)�� �����");
		arrayList.remove(object);
	}
//	public static boolean checkIDOverlap(String id) { // ���̵� �ߺ� ����
//		return new File(FILE_USER,id+".txt").exists();
//	}
//	public static boolean checkIDPW(String id, String pw) { // ���̵��� �´��� üũ
//		File file = new File(FILE_USER,id+".txt");
//		if(!file.exists()) return false;//�ش� ���̵���ü�� ����
//		
//	}
//	public static void saveIDPW(String id, String pw) { // ID PW ���Ϸ� ����
//
//	}
//	public static String readFileStr(String dir,String name) {//�ִ� ���ϸ� �־����
//		File file = new File(dir,name);
//		byte[] byarr = new byte[2048];
//		
//		file.read
//		
//	}

}

class ThreadServer {
	public static final int PORT = 12345;
	private Socket socket;
	private boolean flag_exit;
	private String ID;
	public ThreadServer(Socket s) throws IOException {
		socket = s;
		flag_exit = false;// ���� �÷���
		ID= null;
	}
	public String getID() {
		return ID;
	}
	public void setID(String id) {
		ID = id;
	}

	public ThreadServer start() {
		try {
			new Recv(socket.getInputStream()).run();
			new Send(socket.getOutputStream()).run();
		} catch (IOException e) {
			System.out.println("������ �����ܿ��� IOExc"+e);
		}
		
		return this;
	}

	public void finish() {
		flag_exit = true;
		Server.killThread(this);
	}
	private class Recv extends Thread {
		BufferedReader in;
		public Recv(InputStream i) throws UnsupportedEncodingException { 
			in = new BufferedReader(new InputStreamReader(i,"UTF-8")); 
		}
		@Override
		public void run() {
			try {
				char[] readByte = new char[512];
				int readNum;
				while (!flag_exit) {
					readNum = in.read(readByte);
					if(readNum == -1) break;
					
					System.out.println("ID : "+new String(readByte));
//					if(in.readLine()==null) break;
//					System.out.println("ID : "+ID+new String(readByte, 0,readNum, "UTF-8"));
				}
			} catch (IOException e) {
				System.out.println("id : "+ID+" �� Recv���� "+e+"IOExc ");
			} catch (Exception e) {
				System.out.println("id : "+ID+" �� Recv���� "+e+"Exc");
			}finally {
				if(!flag_exit)//�÷������ᰡ�ƴ� ����ĳġ�� �����ٸ�
					finish();
			}
		}
	}
	private class Send extends Thread {
		OutputStream out;	
		public Send(OutputStream o) { out = o; }
		@Override
		public void run() {
//			try {
//				while (!flag_exit) {
//					
//				}
//			} catch (IOException e) {
//				System.out.println("id : "+ID+" �� Send���� "+e+"IOExc");
//			} catch (Exception e) {
//				System.out.println("id : "+ID+" �� Send���� "+e+"Exc");
//			}finally {
//				if(!flag_exit)//�÷������ᰡ�ƴ� ����ĳġ�� �����ٸ�
//					finish();
//			}
		}
	}
	
	
}
