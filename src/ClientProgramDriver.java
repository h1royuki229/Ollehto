
class ClientProgramDriver{
    public static void main(String[] args) {
        int position;
        Message msg;
        boolean Flag = false;
        
        ClientProgram client = new ClientProgram();
        client.gui = new GUI();
        client.gui.openWindow();
        client.gui.changePage("Game");
        
        System.out.println("テスト用サーバに接続します。");
        if(client.connectToServer("Johnson")){
            System.out.println("サーバに接続しました。");
            Flag = true;
        }else{
            System.out.println("接続に失敗しました。");
            Flag = false;
        }
        
        while(Flag){
            msg = new Message(311);
            position = client.gui.getPosition();
            msg.x = position % 8;
            msg.y = position / 8;
            client.sendAMessage(msg);
            System.out.println("メッセージ:"+msg.type+"番でx="+msg.x+",y="+msg.y+"を送信します。");
            msg = client.receiveAMessage();
            System.out.println("メッセージ:"+msg.type+"番のx="+msg.x+",y="+msg.y+"を受信しました。");
            
        }

    }
}