import java.net.*;
import java.io.*;

class EchoServer{
    static Socket socket;
    static ObjectInputStream ois; 
    static ObjectOutputStream oos; 
    static Message msg;
    public static void main(String[] args) {
        
        try {
            ServerSocket server = new ServerSocket(10000);// 10000番ポートを利用する
            System.out.println("サーバが起動しました.");
            
            socket = server.accept();
        
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            msg = receiveAMessage();
            System.out.println("クライアント"+msg.username+"と接続しました。");
            msg = new Message(102);
            sendAMessage(msg);
            

			while (true) {
                msg = receiveAMessage();
                System.out.println("メッセージ"+msg.type+"番を受信しました。");
                switch(msg.type){
                    case 311:
                        sendAMessage(msg);
                        System.out.println("メッセージ:"+msg.type+"番でx="+msg.x+",y="+msg.y+"を送信します。");            
                        break;
                }
            }
            
		} catch (Exception e) {
			System.err.println("エラーが発生しました: " + e);
        }
        
    }

    
	// Messageオブジェクトを送信するメソッド
	public static boolean sendAMessage(Message m) {
		try {
			oos.writeObject(m);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	// Messageオブジェクトを受信するメソッド
	public static Message receiveAMessage() {
		try {
			Message msg = (Message) ois.readObject();
			return msg;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Message(0);

    }
    
}