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

//서버에서 받을때 클라이언트의 객체 소실시 -1만 반송 연결된 해당 객체는 사용불가
//위상황에서 서버가 끊어도 클라이언트측에서  Broken pipe -IOExc 캐치 계속 함 해당 객체 못씀
//서버가 보낼때 클라이언트의 객채 소실시  netSocketExc 캐치로 잡음
//위상황에서 서버가 끊기면 캐치로 java.net.SocketException: Connection reset 잡음
//버퍼용량이 넘치는 인풋이 온다면
public class Server {
	public static final String FILE_USER="user";//user폴더에 아이디명.txt안에 비번 해쉬문자 들어갈 예정
	public static final int PORT = 12345;
	static ServerSocket server;
	static ArrayList<ThreadServer> arrayList;//삭제는 같은객체 비교 삭제로 함
	public static void main(String[] args) {
		arrayList = new ArrayList<ThreadServer>();
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println("서버 포트 생성 실패 "+e);
		}
		
		//파일 init
		File user_file =  new File(FILE_USER);
		if(!user_file.exists())	user_file.mkdir();		
		
		
		while(true) {
			try {
				arrayList.add(new ThreadServer(server.accept()).start());
			} catch (IOException e) {
				System.out.println("서버매니저에서 "+e+"IOExc");
			} catch (Exception e) {
				System.out.println("서버매니저에서 "+e+"Exc");
			}
		}
	}
	public static void killThread(ThreadServer object) {
		System.out.println("이용자 :"+object.getID()+"(이)가 종료됨");
		arrayList.remove(object);
	}
//	public static boolean checkIDOverlap(String id) { // 아이디 중복 여부
//		return new File(FILE_USER,id+".txt").exists();
//	}
//	public static boolean checkIDPW(String id, String pw) { // 아이디비번 맞는지 체크
//		File file = new File(FILE_USER,id+".txt");
//		if(!file.exists()) return false;//해당 아이디자체가 없음
//		
//	}
//	public static void saveIDPW(String id, String pw) { // ID PW 파일로 저장
//
//	}
//	public static String readFileStr(String dir,String name) {//있는 파일만 넣어야함
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
		flag_exit = false;// 종료 플래그
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
			System.out.println("쓰레드 서버단에서 IOExc"+e);
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
				System.out.println("id : "+ID+" 가 Recv에서 "+e+"IOExc ");
			} catch (Exception e) {
				System.out.println("id : "+ID+" 가 Recv에서 "+e+"Exc");
			}finally {
				if(!flag_exit)//플래그종료가아닌 에러캐치로 나갔다면
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
//				System.out.println("id : "+ID+" 가 Send에서 "+e+"IOExc");
//			} catch (Exception e) {
//				System.out.println("id : "+ID+" 가 Send에서 "+e+"Exc");
//			}finally {
//				if(!flag_exit)//플래그종료가아닌 에러캐치로 나갔다면
//					finish();
//			}
		}
	}
	
	
}
