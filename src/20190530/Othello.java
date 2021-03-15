import java.util.*;


class Othello extends Thread {
    public static final int BLACK = 1;
    public static final int WHITE = -1;
    int[][] board = new int[8][8]; // 盤面の状態(0:石なし 1:黒石 -1:白石 2:石を置ける場所)

    int direction[][] = { { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 0 }, { -1, -1 } }; // 上方向から時計回りに8方向（探索用）
    int canPutCount = 0; // 石を置ける場所の数
    ArrayList<Integer> canPutAreasX; // 石を置けるマスのx座標
    ArrayList<Integer> canPutAreasY;

    boolean couldntPut = false; // 石を置けるマスがないときに立つ
    boolean finishFlag = false; // couldntPutが立っていてかつ石を置けるマスがないときに立つ（終局）
    boolean onlineFlag;
    boolean interruptedFlag = false;

    int black = 0, white = 0; // それぞれの石の数をカウント

    Boolean blackTurnNow = true;

    Player PBlack, PWhite;

    Undo[] undo = new Undo[64]; // 置いた石を元に戻すための情報を格納
    int putNum = 1; // 置いた石の数（undoの格納番号に対応）


    Message receiveAMessage() { // サーバからメッセージを受信できたらそのMessageインスタンスを 失敗するとnew Message(0) を返す
        try {
            Message msg = (Message) ClientProgram.ois.readObject();
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(0);
        }
    }


    Othello(boolean onlineFlag, Player P1, Player P2) {
        this.onlineFlag = onlineFlag;

        P1.othello = this;
        P2.othello = this;
        if (P1.player_color == BLACK) {
            this.PBlack = P1;
            this.PWhite = P2;
        } else {
            this.PBlack = P2;
            this.PWhite = P1;
        }

        // 盤面初期化
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[j][i] = 0;
            }
        }
        board[3][3] = WHITE;
        board[3][4] = BLACK;
        board[4][3] = BLACK;
        board[4][4] = WHITE;

        // GUI
        ClientProgram.gui.plbname.setText(PBlack.name);
        ClientProgram.gui.plwname.setText(PWhite.name);
        ClientProgram.gui.updateBoard(this, PBlack);
    }

    // ゲームを進行させるスレッド
    public void run() {
        while (!finishFlag) {
            turnStep();
        }
        if(interruptedFlag){
            if(this.onlineFlag){
                ClientProgram.writeLog("対戦相手が離脱しました");
                ClientProgram.gui.btnRetTop.setVisible(true);
            }else{
                ClientProgram.sendAMessage(new Message(411));
            }
            return;
        }

        count();
        finishTask();
    }

    // 各ターンの進行
    void turnStep() {
        if (blackTurnNow) {
            System.out.println(PBlack.name + "さんの番です。");
            ClientProgram.writeLog(PBlack.name + "さんの番です。");
            check(PBlack.player_color);
            finishCheck();
            print_board(PBlack);
            PBlack.positionDecide();
            blackTurnNow = false;
        } else {
            System.out.println(PWhite.name + "さんの番です。");
            ClientProgram.writeLog(PWhite.name + "さんの番です。");
            check(PWhite.player_color);
            finishCheck();
            print_board(PWhite);
            PWhite.positionDecide();
            blackTurnNow = true;
        }
    }

    // 盤面表示
    void print_board(Player pl) {
        ClientProgram.gui.updateBoard(this, pl);
        count();
        ClientProgram.gui.plbstones.setText(black + "個");
        ClientProgram.gui.plwstones.setText(white + "個");
        System.out.println();

    }



    // 置けるマスをチェックして印をつける
    void check(int player_color) {
        canPutCount = 0;
        canPutAreasX = new ArrayList<Integer>();
        canPutAreasY = new ArrayList<Integer>();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[x][y] == 0) {
                    for (int i = 0; i < 8 && board[x][y] != 2; i++) {// 8方向の探索
                        int dx = x, dy = y;

                        dx += direction[i][0];
                        dy += direction[i][1];

                        while ((dx >= 0 && dx < 8 && dy >= 0 && dy < 8) && (board[dx][dy] == -player_color)) { // 盤上をはみ出ない かつ 敵石が並ぶ間その方向に進む
                            dx += direction[i][0];
                            dy += direction[i][1];

                            if ((dx >= 0 && dx < 8 && dy >= 0 && dy < 8) && board[dx][dy] == player_color) {
                                board[x][y] = 2;
                                canPutAreasX.add(x);
                                canPutAreasY.add(y);
                                canPutCount++;
                            }

                        }

                    }

                }

            }
        }

    }


    // 石を置く
    void put(int player_color, int x, int y) {
        board[x][y] = player_color;
        undo[putNum] = new Undo(this, player_color, x, y);/**** 置いた石の取り消し用：CPUの探索に使用 */

        for (int i = 0; i < 8; i++) {// 8方向の探索
            int dx = x, dy = y;

            dx += direction[i][0];
            dy += direction[i][1];
            while ((dx >= 0 && dx < 8 && dy >= 0 && dy < 8) && (board[dx][dy] == -player_color)) { // 盤上をはみ出ない かつ 敵石が並ぶ間その方向に進む
                dx += direction[i][0];
                dy += direction[i][1];

                if ((dx >= 0 && dx < 8 && dy >= 0 && dy < 8) && board[dx][dy] == player_color) {
                    // 探索方向の先に自分の石がある（自分の石で挟まれる）とき
                    // 1マスずつ打った手の方向に戻って石の色をひっくり返していく
                    while (!(dx == x && dy == y)) {
                        dx -= direction[i][0];
                        dy -= direction[i][1];
                        board[dx][dy] = player_color;

                        undo[putNum].get(dx, dy); // 置いた石を取り消すための準備作業：CPUの探索で使用

                    }

                }

            }

        }

        putNum++;// 置いた石をカウント

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[j][i] == 2)
                    board[j][i] = 0;
            }
        }
    }

    // 石数を数える
    void count() {
        black = 0;
        white = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (board[j][i]) {
                case BLACK:
                    black++;
                    break;
                case WHITE:
                    white++;
                    break;
                default:
                    break;
                }
            }
        }

    }
    
    // 終局判定
    void finishCheck() {
        // 両者とも石を置けなくなったら終了→finishフラグが立つ
        if (!couldntPut && canPutCount == 0) {
            couldntPut = true;
        } else if (couldntPut && canPutCount == 0) {
            finishFlag = true;
            couldntPut = false;
        } else {
            couldntPut = false;
        }
    }

    // 終了時の表示
    void finishTask() {

        System.out.println("黒" + black + "個、白" + white + "個");

        if (black > white) {
            ClientProgram.writeLog(PBlack.name + ":黒 の勝ち");
        } else if (black < white) {
            ClientProgram.writeLog(PWhite.name + ":白 の勝ち");
        } else if (black == white) {
            ClientProgram.writeLog("引き分け");
        }

        if(!onlineFlag){
            ClientProgram.gui.warikomi.setVisible(false);
            ClientProgram.sendAMessage(new Message(411));
        }
        ClientProgram.gui.btnRetTop.setVisible(true);
    }
    
}

// 一手戻す
// コンピュータ対戦で使う
class Undo {
    int putX, putY; // 石を置いた座標
    int player_color;
    ArrayList<Integer> changedX; // ひっくり返った石の座標
    ArrayList<Integer> changedY;
    Othello othello;

    Undo(Othello othello, int player_color, int putX, int putY) {
        changedX = new ArrayList<Integer>();
        changedY = new ArrayList<Integer>();
        this.othello = othello;
        this.putX = putX;
        this.putY = putY;
        this.player_color = player_color;
    }

    // どこの石がひっくり返ったかを受け取る
    void get(int x, int y) {
        changedX.add(x);
        changedY.add(y);
    }

    void doUndo() {

        // ひっくり返った石は元の色に
        for (int i = 0; i < changedX.size(); i++) {
            othello.board[changedX.get(i)][changedY.get(i)] = -othello.board[changedX.get(i)][changedY.get(i)];
        }
        // 置いた石は無かったことに
        othello.board[putX][putY] = 0;

        // check()をやり直すために◎にしたところを空白に戻す
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (othello.board[j][i] == 2)
                    othello.board[j][i] = 0;
            }
        }

        othello.putNum--; // 石を取り除いたので置いた石の数を1つ減らす
    }
}
