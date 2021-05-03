import java.util.*;
import java.net.*;
import java.io.*;



public class ClientProgram {

    public static final int BLACK = 1;
    public static final int WHITE = -1;

    public static String server = "127.0.0.1";
    // public static String server = "192.168.100.35";
    public static int port = 10000;

    static Othello othelloNow;

    static GUI gui;

    static String username;

    public static int acceptFlag = 0;

    private static Socket socket;
    public static ObjectInputStream ois;
    public static ObjectOutputStream oos;

    public static MsgReceiver mr;
    public static MyThread mt;

    public static boolean retTopFlag = false;

    public static void main(String[] args) {
        server = args[0];
        gui = new GUI();
        gui.openWindow();
        gui.changePage("Top_A");
    }

    static void nameEntered(String name) {
        username = name;
        gui.nameField.setEnabled(false);
        gui.btnOK.setEnabled(false);
        gui.btnCancel.setEnabled(false);
        gui.tpLabelA2.setText("サーバに接続しています・・・");
        if (connectToServer(name)) {
            gui.tpLabelA2.setText("サーバに接続されました！");
            gui.tpLabelB2.setText("ようこそ" + username + "さん");
            gui.changePage("Top_B");

        } else {
            gui.changePage("Top_A");
            gui.tpLabelA2.setText("接続失敗！ 名前を入力してください");
        }
    }

    static void writeLog(String name) { // GUIにログを出力するメソッド
        gui.gameLog.setText(name);
    }

    static void writeEntryLog(String name) { // GUIにログを出力するメソッド
        gui.etLabel.setText(name);
    }

    static void returnToTop() { // GUIにログを出力するメソッド
        if(!retTopFlag){
            retTopFlag = true;
            sendAMessage(new Message(281));
        }
    }

    static class MyThread extends Thread{
            public boolean flag = true;
            public void run(){
            writeEntryLog("オンライン対戦エントリー");
            sendAMessage(new Message(201));
            while (flag) {
                System.out.println("メッセージ受付中");
                Message msg = receiveAMessage();
                System.out.println("メッセージ受け取りました type=" + msg.type);
                if(!flag){
                    break;
                }
                switch (msg.type) {
                case 302:
                    writeEntryLog("対戦相手が見つかりました！");
                    flag = false;
                    startVSOnlineUser(msg.color, msg.username);
                    break;
                case 212:
                    writeEntryLog("対戦相手を探しています...");
                    break;
                case 222:
                    writeEntryLog("<html>コンピュータ対戦中のユーザに<br>対戦を申し込んでいます...<html>");
                    break;
                case 282:
                    writeEntryLog("Topページに戻ります");
                    flag = false;
                    gui.changePage("Top_B");
                    retTopFlag = false;
                    break;
                case 292:
                    writeEntryLog("対戦相手が見つかりませんでした");
                    flag = false;
                    break;
                default:
                    writeEntryLog("エラー：不正なメッセージ(type:" + msg.type + ")を受け取りました");
                    flag = false;
                    break;
                }
            }               
            System.out.println("MyThread脱出");

        }

    }
    static void entryOnlineMatch() {
        gui.changePage("Entry");
        mt = new MyThread();
        mt.start();
        
    }

    static Boolean sendAMessage(Message msg) { // サーバへメッセージを送信できたらtrue 失敗するとfalse を返す
        try {
            oos.writeObject(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static Message receiveAMessage() { // サーバからメッセージを受信できたらそのMessageインスタンスを 失敗するとnew Message(0) を返す
        try {
            Message msg = (Message) ois.readObject();
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(0);
        }
    }

    static Boolean connectToServer(String username) { // サーバへの接続が成功したらtrue 失敗するとfalse を返す

        try{
            socket = new Socket(server, port); // ソケットって、メソッドを跨いで共有できないもんかね？
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        }catch(IOException e){
            return false;            
        }
        Message msg = new Message(101, username);
        if (sendAMessage(msg)) {
            if (receiveAMessage().type == 102) {
                return true;
            }
        }
        return false;

    }


    static void startVSOnlineUser(int colorOfP1, String nameOfOpponent) {
        Player P1 = new Player_Self(username, colorOfP1);
        Player P2 = new Player_Online(nameOfOpponent, -colorOfP1);
        othelloNow = new Othello(true, P1, P2);
        writeLog("ゲーム開始！");
        gui.btnRetTop.setVisible(false);
        gui.changePage("Game");
        othelloNow.start();
    }

    static void startVSCPU(int colorOfP1) {
        Player P1 = new Player_Self(username, colorOfP1);
        Player P2 = new Player_CPU("CPU", -colorOfP1);
        sendAMessage(new Message(401));
        if(receiveAMessage().type != 402){
            System.out.println("通信エラー発生！");
            System.exit(1);
        }
        othelloNow = new Othello(false, P1, P2);
        writeLog("ゲーム開始！");
        mr = new MsgReceiver(othelloNow);
        mr.start();
        gui.btnRetTop.setVisible(true);
        gui.changePage("Game");
        othelloNow.start();
    }

    static void startVSHardCPU(int colorOfP1) {
        Player P1 = new Player_Self(username, colorOfP1);
        Player P2 = new Player_HardCPU("CPU(Hard)", -colorOfP1);
        sendAMessage(new Message(401));
        if(receiveAMessage().type != 402){
            System.out.println("通信エラー発生！");
            System.exit(1);
        }
        othelloNow = new Othello(false, P1, P2);
        writeLog("ゲーム開始！");
        mr = new MsgReceiver(othelloNow);
        mr.start();
        gui.btnRetTop.setVisible(true);
        gui.changePage("Game");
        othelloNow.start();

    }

    static class MsgReceiver extends Thread{    //対戦リクエストを監視するスレッド
        private boolean flag = true;
        private Othello otl;
        MsgReceiver(Othello otl){
            this.otl = otl;
        }
        public void run(){
            while(flag){
                System.out.println("MsgReceiver作動中です・・・");
                Message msg = receiveAMessage(); 
                System.out.println("MsgReceiverがメッセージを受け取りました！type:" + msg.type);
                if(msg.type == 412){    //CPU対戦終了処理
                    acceptFlag = 0;
                    break;
                }
                if(msg.type == 232){
                    gui.btnAccept.setEnabled(true);
                    gui.btnDeny.setEnabled(true);
                    gui.warikomi.setVisible(true);
                    gui.warikomiLabel.setText(msg.username + "さんから対戦申込が届いています");
                    while(acceptFlag == 0 && !othelloNow.finishFlag){
                        System.out.print("");
                    }

                    if(acceptFlag == 1){
                        sendAMessage(new Message(231));
                        msg = receiveAMessage();
                        if(msg.type == 302){
                            flag = false;
                            otl.finishFlag = true;
                            otl.interruptedFlag = true;
                            gui.changePage("Entry");
                            startVSOnlineUser(msg.color, msg.username);
                        }else if(msg.type == 332){    
                            System.out.println("相手が離脱しました。");
                            gui.btnAccept.setEnabled(false);
                            gui.btnDeny.setEnabled(false);
                            gui.warikomiLabel.setText("相手が離脱しました…");
                            try {
                                Thread.sleep(2000);
                            } catch (Exception e) {
                                //TODO: handle exception
                            }
                        }
                    }else if(acceptFlag == 2 || othelloNow.finishFlag){  //対戦拒否の処理
                        sendAMessage(new Message(241));
                    }
                    gui.warikomi.setVisible(false);
                    acceptFlag = 0;
                }else{
                    break;
                }
            }
        }
    }

}
