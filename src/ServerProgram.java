import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.InetAddress;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;

class ServerProgram{

	private static int MAX = 100;// 最大接続数

	public static Client waitingClient; // ネットワーク対戦待機中のクライアント(配列である必要はない)
	public static ArrayList<Client> vsCPUClients =  new ArrayList<Client>(); // コンピュータ対戦中のクライアント(対戦リクエストを送信する用)

	// Messageオブジェクトを送信するメソッド
	public static boolean sendAMessage(Client cl, Message m) {
		try {
			cl.oos.writeObject(m);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	// Messageオブジェクトを受信するメソッド
	public static Message receiveAMessage(Client cl /* 受け取り手のクライアント */) {
		try {
			Message msg = (Message) cl.ois.readObject();
			return msg;
		} catch (/*IOException | ClassNotFound*/Exception e) {
			// e.printStackTrace();
            System.out.println("エラーが発生しました。"+e);
		}
		return new Message(0);

	}

	public static SearchOpponent searchOpponent(Client cl) {
		SearchOpponent s = new SearchOpponent(cl);
		s.start();
		return s;
	}

	public static class SearchOpponent extends Thread{
		Client cl;
		SearchOpponent(Client cl){
			this.cl = cl;
		}
		public void run() {

			// 「対戦相手を探しています」を送る
			System.out.println("クライアントNo." + cl.serialNum + "(" + cl.username + ")が");
			System.out.println("オンライン対戦を要求しています");

			sendAMessage(cl, new Message(212));

			// まず待ち状態のクライアントがいるか確認
			if (waitingClient != null) {// 待ち状態の人がいる場合
				new OnlineBattle(waitingClient, cl).start();

				Message msg;
				msg = new Message(302, cl.username);
				msg.color = 1; // 先にログインしているので先手
				sendAMessage(waitingClient, msg); // 待ち状態のクライアントに送信

				msg = new Message(302, waitingClient.username);
				msg.color = -1; // 後からログインしているので後手
				sendAMessage(cl, msg); // ネットワーク対戦を選択していたクライアントに送信

				waitingClient = null;
				return;
			} else{// 待ち状態の人がいない場合
				waitingClient = cl; // 待ち状態にする
				sendAMessage(cl, new Message(222, cl.username));

				System.out.println("vsCPUClientsにおるやつら：");
				for(Client c : vsCPUClients){
					System.out.println(c.username);
				}

				for (int i = 0; i < vsCPUClients.size(); i++) {	//CPU大戦中のクライアントに、リクエストを出していく
					cl.offeringClient = vsCPUClients.get(i);
					System.out.println("No." + cl.offeringClient.serialNum + "(" + cl.offeringClient.username + ")" + "のクライアントが対象");
					cl.offeringClient.acceptFlag = 0;
					if(cl.offeringClient.offeredBy == null){	//まだ、このクライアントからのリクエストを受け取っていない場合、リクエストを送る
						sendAMessage(cl.offeringClient, new Message(232, cl.username));
						System.out.println("対戦要求を出しています…");	
						cl.offeringClient.offeredBy = cl;
					}else if(cl.offeringClient.offeredBy != cl){
						System.out.println("すでに他のクライアントからのリクエストを受け取っているので、飛ばします");
						continue;				
					}

					//しばらく無限ループで待つ。
					while(cl.offeringClient.acceptFlag == 0 && waitingClient != null && cl.offeringClient.connectedFlag){
						System.out.print("");
					}
					if(!cl.offeringClient.connectedFlag){	//cl.offeringClientが切断されている場合、次のやつにいく。
						System.out.println("cl.offeringClientはすでに切断されているので、次にいきます");
						i--;	//CPUClientsからすでにclが消えているので、iをデクリメントする必要がある
						cl.offeringClient.offeredBy = null;
						continue;
					}

					if (cl.offeringClient.acceptFlag == 1) // 対戦リクエストが承認された場合
					{

						waitingClient = null;
						vsCPUClients.remove(cl.offeringClient);	//vsCPUClientsから消す！

						new OnlineBattle(cl.offeringClient, cl).start();

						Message msg;

						
						msg = new Message(302, cl.offeringClient.username);
						msg.color = -1; // 後からログインしているので後手
						if(!sendAMessage(cl, msg)){
							System.out.println("clにメッセージ送れなかったよ！");
							cl.offeringClient.offeredBy = null;
							sendAMessage(cl.offeringClient, new Message(332));
							return;
						} 

						msg = new Message(302, cl.username);
						msg.color = 1; // 先にログインしているので先手
						sendAMessage(cl.offeringClient, msg); 					

						cl.offeringClient.offeredBy = null;

						return;

					}

					if (waitingClient == null) {	//waitingClientが空だったら、もはやマッチング不可能。
						System.out.println("waitingClientが空だよ！");
						break;
						//途中で離脱したとき。
					}
					cl.offeringClient.offeredBy = null;
				}

				//まだclが待ち状態である場合は、「対戦相手を探しています…」状態にしたいのでメッセージ(212)を送る
				if(waitingClient == cl){
					sendAMessage(cl, new Message(212));
				}
							

			}
			System.out.println("searchingOpponentスレッドを脱出");
			return;
		}
	}


	// mainプログラム
	public static void main(String[] args) {

		try {
			System.out.println("サーバが起動しました.");
			ServerSocket server = new ServerSocket(10000);// 10000番ポートを利用する
			while (true) {
				Socket socket = server.accept();
				new Client(socket);// ソケットを渡しクライアントインスタンス生成
			}
		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
		}
	}
}

//Clientクラスの設計
class Client extends Thread {
	private static int I = 0;
	public static ArrayList<Client> allClients = new ArrayList<Client>();	//結局使わない
	public int serialNum;// 接続者の番号
	public Socket socket; // クライアントとのソケット
	public String username;// 接続者の名前
	public ObjectInputStream ois; 
	public ObjectOutputStream oos; 
	public OnlineBattle ob;
	public int acceptFlag = 0;	
	public boolean connectedFlag = true;	//サーバと切断されているかどうか。サーバとこのクライアントが切断されたら、falseになる
	public Client offeredBy;	//(CPU対戦中のとき)このクライアントへ、オンライン対戦リクエストを送っているクライアント
	public Client offeringClient;	//(CPU対戦中のとき)このクライアントが、オンライン対戦リクエストを送りつけているクライアント

	public Client(Socket socket) {
		this.socket = socket;
		try{
			this.ois = new ObjectInputStream(socket.getInputStream());
			this.oos = new ObjectOutputStream(socket.getOutputStream());
			Message msg = (Message) ois.readObject();
			if(msg.type == 101){
				this.username = msg.username;
				this.serialNum = I++;
				allClients.add(this);
				ServerProgram.sendAMessage(this, new Message(102));		// 接続成功の報告
				System.out.println("新たなクライアントNo." + serialNum + "(" + username + ")が接続されました");
			}

		}catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}
		this.start();
	}

	public void run() {	//メッセージリスナー
		int flagCount = 0;
		while (true) {// 無限ループで，ソケットへの入力を監視する
			try {

				Message msg = (Message) ois.readObject();
				System.out.println("\ntype = " + msg.type);
				switch(msg.type){
					case 201:	//オンライン対戦にエントリーしてきた場合
					ServerProgram.searchOpponent(this);
					break;

					case 231:	//(CPU対戦中のとき)オンライン対戦承認のメッセージを受け取った場合
					System.out.println("No." + serialNum + "(" + username + ")" + "のクライアントが");
					System.out.println("コンピュータ対戦からオンライン対戦に移行するのを承認しました");
					if( ServerProgram.waitingClient != this.offeredBy || ServerProgram.waitingClient.offeringClient != this){
						this.offeredBy = null;
						ServerProgram.sendAMessage(this, new Message(332));
						break;
					}
					acceptFlag = 1;
					break;

					case 241:	//(CPU対戦中のとき)オンライン対戦拒否のメッセージを受け取った場合
					System.out.println("No." + serialNum + "(" + username + ")" + "のクライアントが");
					System.out.println("コンピュータ対戦からオンライン対戦に移行するのを拒否しました");
					System.out.println(this.offeredBy);
					this.offeredBy = null;
					acceptFlag = 2;
					break;

					case 281:	//オンライン対戦エントリーを中断してトップ画面に戻った場合
					ServerProgram.sendAMessage(this, new Message(282));
					System.out.println("No." + serialNum + "(" + username + ")" + "のクライアントが");
					System.out.println("オンライン対戦要求をやめて、トップページに戻りました。");
					// if(waitingClient.offeringClient != null){
					// }
					ServerProgram.waitingClient = null;
					break;

					case 311:	//オンライン対戦中、手を打った場合
					System.out.println("color: " + msg.color);
					if(this.ob == null){
						if(flagCount == 1){
							flagCount = 0;
							break;
						}
						Message msg2 = new Message(311);	//対戦相手が離脱したことを伝えるメッセージにする
						msg2.x = -3;
						msg2.y = -3;
						ServerProgram.sendAMessage(this, msg2);						
						flagCount++;
					}else{
						System.out.println("No." + serialNum + "(" + username + ")" + "のクライアントが");
						System.out.println("手を打ちました");
						this.ob.serveMsg(msg, this);
					}
					break;

					case 401:	//コンピュータ対戦を開始した場合
					System.out.println("No." + serialNum + "(" + username + ")" + "のクライアントが");
					System.out.println("コンピュータ対戦モードを開始しました");
					ServerProgram.vsCPUClients.add(this);
					ServerProgram.sendAMessage(this, new Message(402));
					break;

					case 411:	//コンピュータ対戦を終了した場合
					System.out.println("No." + serialNum + "(" + username + ")" + "のクライアントが");
					System.out.println("コンピュータ対戦モードを終了しました");
					ServerProgram.vsCPUClients.remove(this);
					ServerProgram.sendAMessage(this, new Message(412));
					break;

				}
			} catch (/*IOException | ClassNotFound*/Exception e) {
				disconnect();
				// e.printStackTrace();
				return;
			}
		}

	}


	public void disconnect() {	//サーバからこのクライアントが切断されたときの処理
		System.out.println("No." + serialNum + "(" + username + ")" + "のクライアントとの接続が切断されました.");
		if(ServerProgram.waitingClient == this){
			ServerProgram.waitingClient = null;
			System.out.println("waitingClientに切断されたクライアントが代入されていたので、空にしました");
		}
		allClients.remove(this);
		ServerProgram.vsCPUClients.remove(this);
		connectedFlag = false;
		if(this.ob != null){
			Message msg = new Message(311);	//対戦相手が離脱したことを伝えるメッセージにする
			msg.x = -3;
			msg.y = -3;
			if(this.ob.a == 1){
				ServerProgram.sendAMessage(this.ob.clw, msg);
			}else{
				ServerProgram.sendAMessage(this.ob.clb, msg);
			}
			Message dammyMsg = new Message(0);// ダミーメッセージ
			ob.interruptedFlag = true;
			ob.msgFromB = dammyMsg;
			ob.msgFromW = dammyMsg;
			
		}

	}

}

class OnlineBattle extends Thread{
	public Client clb; // 先手のクライアント
	public Client clw; // 後手のクライアント
	public int a = 1;

	public Message msgFromB = null;
	public Message msgFromW = null;
	public boolean interruptedFlag = false;

	public OnlineBattle(Client clb, Client clw) {
		this.clb = clb;
		this.clw = clw;
		clb.ob = this;
		clw.ob = this;
	}

	public void serveMsg(Message msg, Client cl){
		if(cl == clb){
			if(a == -1){
				return;
			}
			if(msgFromB == null){
				msgFromB = msg;
			}
		}else{
			if(a == 1){
				return;
			}
			if(msgFromW == null){
				msgFromW = msg;
			}
		}
	}

	public void run() {
		// クライアントから打った手を受信
		System.out.println(clb.username + " vs " + clw.username + " 開始");
		while(true){
			if (a == 1) {
				while(msgFromB == null){
					System.out.print("");
				}
				if(interruptedFlag){
					break;
				}
				System.out.println(msgFromB.x + "," + msgFromB.y + "クライアント1（先手）から");
				ServerProgram.sendAMessage(clw, msgFromB);
				if(msgFromB.x == -2 && msgFromB.y == -2){
					break;
				}
				a = -1;
				msgFromB = null;
			} else {
				while(msgFromW == null){
					System.out.print("");
				}
				if(interruptedFlag){
					break;
				}
				System.out.println(msgFromW.x + "," + msgFromW.y + "クライアント2(後手)から");
				ServerProgram.sendAMessage(clb, msgFromW);
				if(msgFromW.x == -2 && msgFromW.y == -2){
					break;
				}
				a = 1;
				msgFromW = null;
			}
		}
		clb.ob = null;
		clw.ob = null;
		System.out.println(clb.username + " vs " + clw.username + " 終了");
	}

}
