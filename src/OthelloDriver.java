/**Othelloクラスのコンストラクタにあるguiにかかわる部分はコメントアウトしたうえで実行確認している */
/**Undoクラスの動作も確認 */

import java.util.Scanner;

class OthelloDriver {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Player player1 = new Player_Self("Matthew", Othello.BLACK);
        Player player2 = new Player_Self("Olivia", Othello.WHITE);
        Othello othello = new Othello(false, player1, player2);

        // 対局
        while (!othello.finishFlag) {
            // Player1のターン
            othello.check(player1.player_color);
            othello.finishCheck();
            othello.count();
            System.out.println("黒の数=" + othello.black + " 白の数=" + othello.white);
            print(othello.board);
            System.out.println("player1" + player1.name + "の番です");
            positionDecide(player1);

            // Player2のターン
            othello.check(player2.player_color);
            othello.finishCheck();
            othello.count();
            System.out.println("黒の数=" + othello.black + " 白の数=" + othello.white);
            print(othello.board);
            System.out.println("player2" + player2.name + "の番です");
            positionDecide(player2);

        }

        System.out.println("黒" + othello.black + "個、白" + othello.white + "個");

        if (othello.black > othello.white) {
            System.out.println(othello.PBlack.name + "黒の勝ち");
        } else if (othello.black < othello.white) {
            System.out.println(othello.PWhite.name + "白の勝ち");
        } else if (othello.black == othello.white) {
            System.out.println("引き分け");
        }

    }

    static void print(int[][] board) {

        System.out.println();
        System.out.println("  0 1 2 3 4 5 6 7 (x)");
        for (int i = 0; i < 8; i++) {
            System.out.printf(i + " ");
            for (int j = 0; j < 8; j++) {
                switch (board[j][i]) {
                case 0:
                    System.out.printf("* ");// 空きマス
                    break;
                case Othello.BLACK:
                    System.out.printf("● "); // 黒
                    break;
                case Othello.WHITE:
                    System.out.printf("○ ");// 白
                    break;
                case 2:
                    System.out.printf("◎ ");// 石を置けるマス
                    break;
                }
            }
            System.out.println();
        }
        System.out.println("(y)");
        System.out.println();

    }

    static void positionDecide(Player player) {
        int x, y;
        Scanner scan = new Scanner(System.in);

        if (player.othello.canPutCount != 0) {// 置けるマスがある場合
            System.out.println("石を置く座標を入力して下さい");
            System.out.printf("x = ");
            x = scan.nextInt();
            System.out.printf("y = ");
            y = scan.nextInt();

            if (x == 8 && y == 8 && player.othello.putNum >= 2) {
                player.othello.undo[player.othello.putNum - 1].doUndo();
                player.othello.undo[player.othello.putNum - 1].doUndo();
                System.out.println("1手戻しました。");
                player.othello.check(player.player_color);
                print(player.othello.board);
                System.out.println("再び" + player.name + "の番です");
                positionDecide(player);
            } else if (x >= 0 && x < 8 && y >= 0 && y < 8 && player.othello.board[x][y] == 2) {// 盤上からはみ出ていないかつ石を置ける場所
                player.othello.put(player.player_color, x, y);

            } else {
                System.out.println("そのマスには石を打てません。");
                positionDecide(player);
            }
        } else {
            System.out.println("置けるところがありません。");
        }

    }

}