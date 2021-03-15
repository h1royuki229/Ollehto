import java.util.ArrayList;

// コンピュータ対戦（Level2）
class Player_HardCPU extends Player {

    int[][] boardValue = {
        {  30,-12,  0, -1, -1,  0,-12, 30 },
        { -12,-15, -3, -3, -3, -3,-15,-12 },
        {   0, -3,  0, -1, -1,  0, -3,  0 },
        {  -1, -3, -1, -1, -1, -1, -3, -1 },
        {  -1, -3, -1, -1, -1, -1, -3, -1 },
        {   0, -3,  0, -1, -1,  0, -3,  0 },
        { -12,-15, -3, -3, -3, -3,-15,-12 },
        {  30,-12,  0, -1, -1,  0,-12, 30 },
        };

    public static final int N = 10;// N手目からは終局まで評価する
    public static final int BLACK = 1;
    public static final int WHITE = -1;

    int[] value;// 置けるマスそれぞれの評価値を格納
    int valueMax;// 評価が最大のものを選ぶときに使う
    int bestX, bestY;// 評価値から選んだ最適なマスの座標
    int canX = 0, canY = 0;// 置けるマスの座標
    int random;

    Player_HardCPU(String name, int player_color) {
        super(name, player_color);
    }

    void positionDecide() {
        
        try {
            Thread.sleep(160); // 普通に計算させると反映が早すぎるので少し待たせる。
        } catch (Exception e) {

        }

        if (othello.canPutCount > 0) {// 置けるマスがあるとき

            value = new int[othello.canPutCount];

            // 置けるマスを1つずつ評価
            for (int i = 0; i < othello.canPutCount; i++) {

                canX = othello.canPutAreasX.get(i);
                canY = othello.canPutAreasY.get(i);

                othello.put(player_color, canX, canY); // 仮に石を置く（あとでUndoで元に戻す）

                if (othello.putNum < (60 - N)) {// N手目を境界にして評価方法を変える

                    value[i] = 0;
                    valueCal1(i);// 中盤までの評価計算

                } else {

                    value[i] = 64;
                    valueCal2(false, i);// 終盤の評価値計算

                }

                othello.undo[othello.putNum - 1].doUndo(); // 仮に置いた石を戻す
                othello.check(player_color);

                /******** 評価値表示(デバッグ用) */
                // System.out.println(
                // "(" + othello.canPutAreasX.get(i) + "," + othello.canPutAreasY.get(i) + ") "
                // + value[i]);
                /***************************** */

            }

            // 以下で評価値から今置けるマスの中での最適なマスを選択
            valueMax = value[0];
            bestX = othello.canPutAreasX.get(0);
            bestY = othello.canPutAreasY.get(0);

            for (int i = 0; i < othello.canPutCount; i++) {
                if (valueMax < value[i]) {// 評価の高いほうを最適解に

                    valueMax = value[i];
                    bestX = othello.canPutAreasX.get(i);
                    bestY = othello.canPutAreasY.get(i);

                } else if (valueMax == value[i]) {// 評価値が同じならランダムで選択

                    random = (int) (Math.random() * 2);
                    if (random == 0) {
                        valueMax = value[i];
                        bestX = othello.canPutAreasX.get(i);
                        bestY = othello.canPutAreasY.get(i);
                    }
                }
            }

            othello.put(player_color, bestX, bestY);
            System.out.print("コンピュータ" + name + "は");
            System.out.printf("(%d, %d)", bestX, bestY);
            System.out.println("に駒を置きました。");

        } else {

            System.out.println("コンピュータ" + name + "はおけるところがありませんでした。");

        }
    }

    // 中盤までの評価
    // （仮に）石を置いた後の盤面で、自分の石があるマス目の点数(→boardValue)を合計し、
    // いまある自石の数で割って平均値を出す。
    // さらに相手が次置けるマスの点数も考慮して評価値とする
    void valueCal1(int valueNum) {

        /******** 自分の置いた石の評価点数の平均をとる */
        int score = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (othello.board[j][i]) {
                case BLACK:
                    if (player_color == BLACK) {
                        score += boardValue[j][i];
                    }
                case WHITE:
                    if (player_color == WHITE) {
                        score += boardValue[j][i];
                    }
                }
            }
        }

        othello.count();
        if (player_color == BLACK) {
            // System.out.printf("score%d,black%d",score,othello.black); //評価値の表示
            value[valueNum] = score * 100 / othello.black;
        } else {
            // System.out.printf("score%d,white%d",score,othello.white); //評価値の表示
            value[valueNum] = score * 100 / othello.white;
        }
        /****************************************** */

        /**** 相手が次置けるマスの点数の最大値が低いほうがいいのでvalueに反映 */
        int eneNextScoreMax = 121;
        int karioki = 0;

        othello.check(-player_color);
        if (othello.canPutCount != 0) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    switch (othello.board[j][i]) {
                    case 2:
                        if (eneNextScoreMax == 121) {
                            eneNextScoreMax = boardValue[j][i];
                        }
                        karioki = boardValue[j][i];

                        if (karioki > eneNextScoreMax) {
                            eneNextScoreMax = karioki;
                        }
                    }
                }
            }

        }

        if (eneNextScoreMax != 121) {
            // System.out.printf(" eneNextScoreMax:%d\n", eneNextScoreMax); //評価値の表示
            value[valueNum] -= eneNextScoreMax;
        } else {
            value[valueNum] += eneNextScoreMax;
        }

        /************************************************** */

    }

    // ラストN手の評価
    // 終局まで全パターン計算し、終局後の黒石、白石の数から評価値を決める
    void valueCal2(boolean myturnFlag, int valueNum) {
        int putNumNow = othello.putNum;

        // このループはCPUの脳内で局面を進めて、先読みをしている
        // 実際の局面に反映されないようにUndoとcheckをして元に戻している
        while (!othello.finishFlag) {
            if (myturnFlag) {// 自分のターン
                othello.check(player_color);
                othello.finishCheck();

                for (int i = 0; i < othello.canPutCount; i++) {
                    othello.put(player_color, othello.canPutAreasX.get(i), othello.canPutAreasY.get(i));

                    valueCal2(false, valueNum);
                    othello.undo[putNumNow].doUndo();
                    othello.check(player_color);

                }

            } else {// 相手のターン ( player_color か -player_color かの違いしかない)
                othello.check(-player_color);
                othello.finishCheck();

                for (int i = 0; i < othello.canPutCount; i++) {
                    othello.put(-player_color, othello.canPutAreasX.get(i), othello.canPutAreasY.get(i));

                    valueCal2(true, valueNum);
                    othello.undo[putNumNow].doUndo();
                    othello.check(-player_color);
                }

            }
        }

        // 先読みした局面が終局したときに評価値を計算
        if (othello.finishFlag) {
            othello.finishFlag = false;

            othello.count();
            if (player_color == BLACK) {
                if (value[valueNum] > othello.black - othello.white)
                    value[valueNum] = othello.black - othello.white;
            } else {
                if (value[valueNum] > othello.white - othello.black)
                    value[valueNum] = othello.white - othello.black;
            }
            othello.black = 0;
            othello.white = 0;

        }

    }

}
