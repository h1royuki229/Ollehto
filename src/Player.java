import java.io.*;


abstract class Player { //プレイヤークラス。サブクラスしか使わないので、一応抽象クラスにしています。
    int x, y;
    int player_color;
    Othello othello;
    String name;

    Player(String name, int player_color) {
        this.name = name;
        this.player_color = player_color;
    }

    void positionDecide() {
    }
}

// このゲームをパソコンの前で操作しているプレイヤ
class Player_Self extends Player {
    Player_Self(String name, int player_color) {
        super(name, player_color);
    }

    void positionDecide() {
        if (othello.canPutCount != 0) { // 置けるマスがある場合
            int position = ClientProgram.gui.getPosition();
            x = position % 8;
            y = position / 8;
            System.out.printf("%d, %d\n", x, y);

            /********* Undoの処理（いわゆる「待った」の実装はしないので必要はない） */
            if (x == 8 && y == 8 && othello.putNum > 2) {
                othello.undo[othello.putNum - 1].doUndo(); // 今相手が打った手を元に戻す
                othello.undo[othello.putNum - 1].doUndo(); // 一手前に自分が打った手を元に戻す
                othello.check(player_color);
                othello.print_board(this);
                positionDecide();
            } else
            /***************************************************************** */
            if (x >= 0 && x < 8 && y >= 0 && y < 8 && othello.board[x][y] == 2) {// 盤上からはみ出ていない かつ 石を置けるマスである
                othello.put(player_color, x, y);
            } else {
                System.out.println("そのマスには石を打てません。");
                positionDecide();
            }

        } else { // 置けるマスがない場合
            ClientProgram.writeLog("置けるところがありません。");
            x = -1;
            y = -1;
        }

        if (othello.onlineFlag) { // オンライン対戦中であれば今打った石の情報をサーバに送信
            if(othello.finishFlag){
                x = -2;
                y = -2;
            }
            Message msg = new Message(311);
            msg.color = this.player_color;
            msg.x = x;
            msg.y = y;
            ClientProgram.sendAMessage(msg);
        }
    }
}

// オンライン対戦の相手プレイヤ
class Player_Online extends Player {
    Player_Online(String name, int player_color) {
        super(name, player_color);
    }

    void positionDecide() {
        System.out.println(name + "さんの番です。通信待機中...");
        Message msg;
        
        try{
            msg = (Message)(ClientProgram.ois.readObject()); // 相手の打った石の情報を受け取る
        
            if(msg.x == -3 && msg.y == -3){
                this.othello.finishFlag = true;
                this.othello.interruptedFlag = true;
            }else{
                System.out.printf("msg:(%d, %d)", msg.x, msg.y);
                if(othello.canPutCount != 0){
                    othello.put(player_color, msg.x, msg.y);
                }
            }   
        }catch(IOException | ClassNotFoundException e){
            // e.printStackTrace();
            System.out.println("エラーが発生しました。"+e);
        }
    }
}

// コンピュータ対戦（Level1）
class Player_CPU extends Player {
    Player_CPU(String name, int player_color) {
        super(name, player_color);
    }

    int x = y = -1;

    void positionDecide() {
        try {
            Thread.sleep(160);// 普通に計算させると反映が早すぎるので少し待たせる。
        } catch (Exception e) {
        }
        if (othello.canPutCount > 0) {
            int rndnum = (int) (Math.random() * othello.canPutCount);
            x = othello.canPutAreasX.get(rndnum);
            y = othello.canPutAreasY.get(rndnum);
            othello.put(player_color, x, y);
            System.out.print("コンピュータ" + name + "は");
            System.out.printf("(%d, %d)", x, y);
            System.out.println("に駒を置きました。");
        } else {
            System.out.println("コンピュータ" + name + "はおけるところがありませんでした。");
        }
    }

}