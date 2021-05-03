import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI extends JFrame implements ActionListener {
    public static final int BLACK = 1;
    public static final int WHITE = -1;
    private boolean posiFlag = false;
    private int position;
    private String pageNow = "Game";
    private JPanel cardPanel;
    private CardLayout cl;

    //タイトル画像
    private ImageIcon titleIcon;

    public JLabel tpLabelA2;
    public JTextField nameField;
    public JButton btnOK;
    public JButton btnCancel;

    public JLabel tpLabelB2;


    public JLabel etLabel;

    private ImageIcon whiteIcon;
    private ImageIcon blackIcon;
    private ImageIcon boardIcon;
    private ImageIcon availableIcon;
    private ImageIcon newblackIcon;
    private ImageIcon newwhiteIcon;
    private JButton buttonArray[];

    public JLabel plbname;
    public JLabel plbstones;
    public JLabel plwname;
    public JLabel plwstones;
    public JLabel gameLog;
    public JPanel warikomi;
    public JLabel warikomiLabel;
    public JButton btnAccept;
    public JButton btnDeny;
    public JButton btnRetTop;

    public static void main(String args[]) {
        GUI gui = new GUI();
        gui.openWindow();
    }

    public void actionPerformed(ActionEvent e) {
        String buttonName = e.getActionCommand();
        System.out.println(buttonName);
        switch (buttonName) {
        case "Top_A:btnOK":
            if(nameField.getText().length() == 0){
                System.out.println("NOGI");
                break;
            }
            ClientProgram.nameEntered(nameField.getText());
            break;
        case "Top_A:btnCancel":
            nameField.setText("");
            break;
        case "Top_B:btnOnline":
            ClientProgram.entryOnlineMatch();
            break;
        case "Top_B:btnCPUBlack":
            ClientProgram.startVSCPU(BLACK);
            break;
        case "Top_B:btnCPUWhite":
            ClientProgram.startVSCPU(WHITE);
            break;
        case "Top_B:btnHardCPUBlack":
            ClientProgram.startVSHardCPU(BLACK);
            break;
        case "Top_B:btnHardCPUWhite":
            ClientProgram.startVSHardCPU(WHITE);
            break;
        case "Game:btnAccept":
            ClientProgram.acceptFlag = 1;
            break;
        case "Game:btnDeny":
            ClientProgram.acceptFlag = 2;
            break;
        case "Entry:btnRetTop":
            ClientProgram.returnToTop();
            break;
        case "Game:btnRetTop":
            ClientProgram.othelloNow.interruptedFlag = true;
            ClientProgram.othelloNow.finishFlag = true;
            ClientProgram.othelloNow.board[0][0] = 2;
            position = 0;
            posiFlag = false;
            changePage("Top_B");
            break;
        default:    //盤面のマス目に相当するボタンが押されたときの処理
            if(posiFlag){
                position = Integer.parseInt(buttonName);
                posiFlag = false;
            }
            break;

        }
    }

    int getPosition(){  //盤面上のクリックされたマスの位置を取得する関数
        posiFlag = true;
        while(posiFlag){
            System.out.print("");
        }
        return position;

    }

    void updateBoard(Othello othello, Player pl) {    //盤面を更新する関数
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int btnID = 8 * i + j;
                switch (othello.board[j][i]) {

                case BLACK:
                    buttonArray[btnID].setIcon(blackIcon);
                    break;
                case WHITE:

                    buttonArray[btnID].setIcon(whiteIcon);// 白
                    break;
                case 2:
                    if(pl instanceof Player_Self){
                        buttonArray[btnID].setIcon(availableIcon);// 石を置けるマス
                        break;
                    }
                case 0:
                    buttonArray[btnID].setIcon(boardIcon); // 空きマス
                    break;
                }

                //最後に打たれた石を目印付きの画像にする
                if(othello.putNum > 1 && j==othello.undo[othello.putNum-1].putX && i==othello.undo[othello.putNum-1].putY){
                    switch(othello.board[j][i]){
                        case BLACK:
                            buttonArray[btnID].setIcon(newblackIcon);
                            break;
                        case WHITE:
                            buttonArray[btnID].setIcon(newwhiteIcon);
                            break;
                    }
                }
            }
        }

    }

    GUI() {
        super("OLLEHTO");

        //画面の切り替えを行うから、カードレイアウトのパネルを用意する
        cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout());
        cl = (CardLayout) (cardPanel.getLayout());

        add(cardPanel, "Center");

        draw_topPage_A();   //ページ「Top_A」を描画
        draw_topPage_B();   //ページ「Top_B」を描画
        draw_entryPage();   //ページ「Entry」を描画
        draw_gamePage();   //ページ「Game」を描画

        cl.show(cardPanel, pageNow);

    }

    void changePage(String page) {
        switch (page){
            case "Top_A":
            tpLabelA2.setText("名前を入力してください");
            nameField.setEnabled(true);
            btnOK.setEnabled(true);
            btnCancel.setEnabled(true);
            case "Game":
            warikomi.setVisible(false);
        }
        pageNow = page;
        cl.show(cardPanel, pageNow);
    }

    void openWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 650);
        setVisible(true);
    }

    private void draw_topPage_A() {
        /* トップページ(ログイン)の描画処理を記述した関数 */

        JPanel topPage_A = new JPanel();
        // topPage_A.setLayout(new FlowLayout(FlowLayout.CENTER));
        topPage_A.setLayout(new BoxLayout(topPage_A, BoxLayout.PAGE_AXIS));

        topPage_A.add(Box.createRigidArea(new Dimension(1, 100)));

        //タイトル
        titleIcon = new ImageIcon("../img/OthelloTitle.png");
        Image title = titleIcon.getImage().getScaledInstance((int) (titleIcon.getIconWidth() * 0.35), -1,Image.SCALE_SMOOTH);
        titleIcon = new ImageIcon(title);
        JLabel tpLabelA1 = new JLabel();
        tpLabelA1.setIcon(titleIcon);
        tpLabelA1.setAlignmentX(0.5f);
        topPage_A.add(tpLabelA1);

        tpLabelA2 = new JLabel("名前を入力してください");
        tpLabelA2.setFont(new Font("メイリオ", Font.PLAIN, 30));
        tpLabelA2.setAlignmentX(0.5f);
        topPage_A.add(tpLabelA2);

        nameField = new JTextField(18);
        nameField.setFont(new Font("メイリオ", Font.PLAIN, 40));
        nameField.setAlignmentX(0.5f);
        nameField.setMaximumSize(new Dimension(300, 50));
        topPage_A.add(nameField);

        JPanel btnsA_P = new JPanel();
        btnsA_P.setLayout(new BoxLayout(btnsA_P, BoxLayout.LINE_AXIS));

        topPage_A.add(Box.createRigidArea(new Dimension(1, 20)));

        btnOK = new JButton("OK");
        btnOK.setActionCommand("Top_A:btnOK");
        btnOK.addActionListener(this);
        btnOK.setFont(new Font("メイリオ", Font.PLAIN, 20));

        btnCancel = new JButton("取消");
        btnCancel.setActionCommand("Top_A:btnCancel");
        btnCancel.addActionListener(this);
        btnCancel.setFont(new Font("メイリオ", Font.PLAIN, 20));

        getRootPane().setDefaultButton(btnOK);


        btnsA_P.add(btnOK);
        btnsA_P.add(Box.createRigidArea(new Dimension(20, 1)));
        btnsA_P.add(btnCancel);


        topPage_A.add(btnsA_P);

        topPage_A.add(Box.createRigidArea(new Dimension(1, 30)));

        cardPanel.add(topPage_A, "Top_A");

    }

    private void draw_topPage_B() {
        /* トップページ(対戦方法選択)の描画処理を記述した関数*/

        JPanel topPage_B = new JPanel();
        topPage_B.setLayout(new BoxLayout(topPage_B, BoxLayout.PAGE_AXIS));
        topPage_B.add(Box.createRigidArea(new Dimension(1, 20)));

        Image title = titleIcon.getImage().getScaledInstance((int) (titleIcon.getIconWidth() * 0.75), -1,Image.SCALE_SMOOTH);
        titleIcon = new ImageIcon(title);
        JLabel tpLabelB1 = new JLabel();
        tpLabelB1.setIcon(titleIcon);
        tpLabelB1.setAlignmentX(0.5f);
        topPage_B.add(tpLabelB1);

        tpLabelB2 = new JLabel("ようこそ〇〇さん");
        tpLabelB2.setFont(new Font("メイリオ", Font.BOLD, 40));
        tpLabelB2.setAlignmentX(0.5f);
        topPage_B.add(tpLabelB2);

        JLabel tpLabelB3 = new JLabel("対戦方法を選択してください");
        tpLabelB3.setFont(new Font("メイリオ", Font.PLAIN, 20));
        tpLabelB3.setAlignmentX(0.5f);
        topPage_B.add(tpLabelB3);

        JPanel btnsB_P = new JPanel();
        btnsB_P.setLayout(new BoxLayout(btnsB_P, BoxLayout.PAGE_AXIS));

        topPage_B.add(Box.createRigidArea(new Dimension(1, 20)));

        JButton btnOnline;
        JButton btnCPUBlack;
        JButton btnCPUWhite;
        JButton btnHardCPUBlack;
        JButton btnHardCPUWhite;

        btnOnline = new JButton("オンライン対戦");
        btnOnline.setAlignmentX(0.5f);
        btnOnline.setFont(new Font("メイリオ", Font.PLAIN, 20));
        btnOnline.setActionCommand("Top_B:btnOnline");
        btnOnline.addActionListener(this);

        btnCPUBlack = new JButton("コンピュータと対戦(先手)");
        btnCPUBlack.setAlignmentX(0.5f);
        btnCPUBlack.setFont(new Font("メイリオ", Font.PLAIN, 20));
        btnCPUBlack.setActionCommand("Top_B:btnCPUBlack");
        btnCPUBlack.addActionListener(this);

        btnCPUWhite = new JButton("コンピュータと対戦(後手)");
        btnCPUWhite.setAlignmentX(0.5f);
        btnCPUWhite.setFont(new Font("メイリオ", Font.PLAIN, 20));
        btnCPUWhite.setActionCommand("Top_B:btnCPUWhite");
        btnCPUWhite.addActionListener(this);

        btnHardCPUBlack = new JButton("強いコンピュータと対戦(先手)");
        btnHardCPUBlack.setAlignmentX(0.5f);
        btnHardCPUBlack.setFont(new Font("メイリオ", Font.PLAIN, 20));
        btnHardCPUBlack.setActionCommand("Top_B:btnHardCPUBlack");
        btnHardCPUBlack.addActionListener(this);

        btnHardCPUWhite = new JButton("強いコンピュータと対戦(後手)");
        btnHardCPUWhite.setAlignmentX(0.5f);
        btnHardCPUWhite.setFont(new Font("メイリオ", Font.PLAIN, 20));
        btnHardCPUWhite.setActionCommand("Top_B:btnHardCPUWhite");
        btnHardCPUWhite.addActionListener(this);


        btnsB_P.add(btnOnline);
        btnsB_P.add(Box.createRigidArea(new Dimension(1, 20)));
        btnsB_P.add(btnCPUBlack);
        btnsB_P.add(Box.createRigidArea(new Dimension(1, 20)));
        btnsB_P.add(btnCPUWhite);
        btnsB_P.add(Box.createRigidArea(new Dimension(1, 20)));
        btnsB_P.add(btnHardCPUBlack);
        btnsB_P.add(Box.createRigidArea(new Dimension(1, 20)));
        btnsB_P.add(btnHardCPUWhite);

        topPage_B.add(btnsB_P);

        cardPanel.add(topPage_B, "Top_B");

    }

    private void draw_entryPage() {

        /* 待ち状態ページの描画処理を記述した関数 */

        JButton entryBtnRetTop;

        JPanel entryPage = new JPanel();
        entryPage.setLayout(new BoxLayout(entryPage, BoxLayout.PAGE_AXIS));

        entryPage.add(Box.createRigidArea(new Dimension(1, 250)));

        etLabel = new JLabel("対戦相手を探しています", SwingConstants.CENTER);
        etLabel.setFont(new Font("メイリオ", Font.PLAIN, 30));
        etLabel.setAlignmentX(0.5f);

        entryPage.add(etLabel);

        entryPage.add(Box.createRigidArea(new Dimension(1, 30)));

        entryBtnRetTop = new JButton("TOPに戻る");
        entryBtnRetTop.setFont(new Font("メイリオ", Font.PLAIN, 20));
        entryBtnRetTop.setAlignmentX(0.5f);
        entryBtnRetTop.setActionCommand("Entry:btnRetTop");
        entryBtnRetTop.addActionListener(this);

        entryPage.add(entryBtnRetTop);

        cardPanel.add(entryPage, "Entry");


    }

    private void draw_gamePage() {
        /* ゲームページの描画処理を記述した関数 */

        // テスト用に局面情報を初期化
        int row = 8; // オセロ盤の縦横マスの数

        // アイコン設定(画像ファイルをアイコンとして使う)
        whiteIcon = new ImageIcon("../img/WhiteStone.jpg");
        blackIcon = new ImageIcon("../img/BlackStone.jpg");
        boardIcon = new ImageIcon("../img/GreenFrame.jpg");
        availableIcon = new ImageIcon("../img/Available.jpg");
        newblackIcon = new ImageIcon("../img/NewBlackStone.jpg");
        newwhiteIcon = new ImageIcon("../img/NewWhiteStone.jpg");

        JPanel gamePage = new JPanel();
        gamePage.setLayout(new BoxLayout(gamePage, BoxLayout.PAGE_AXIS));

        JPanel plzone = new JPanel();
        plzone.setLayout(new BoxLayout(plzone, BoxLayout.LINE_AXIS));
        gamePage.add(plzone);

        JPanel pl1 = new JPanel();
        pl1.setMaximumSize(new Dimension(100,100));
        pl1.setLayout(new BoxLayout(pl1, BoxLayout.PAGE_AXIS));
        pl1.setBackground(Color.BLACK);
        plzone.add(pl1);

        plbname = new JLabel("Player 1");
        plbstones = new JLabel("2個");
        plbname.setFont(new Font("メイリオ", Font.PLAIN, 20));
        plbname.setForeground(Color.WHITE);
        plbstones.setFont(new Font("メイリオ", Font.PLAIN, 20));
        plbstones.setForeground(Color.WHITE);
        pl1.add(plbname);
        pl1.add(Box.createRigidArea(new Dimension(1, 20)));
        pl1.add(plbstones);

        plzone.add(Box.createRigidArea(new Dimension(20, 1)));

        JPanel pl2 = new JPanel();
        pl2.setMaximumSize(new Dimension(100,100));
        pl2.setLayout(new BoxLayout(pl2, BoxLayout.PAGE_AXIS));
        pl2.setMinimumSize(new Dimension(160,160));
        pl2.setBackground(Color.WHITE);
        plzone.add(pl2);

        plwname = new JLabel("Player 2");
        plwstones = new JLabel("2個");
        plwname.setFont(new Font("メイリオ", Font.PLAIN, 20));
        plwstones.setFont(new Font("メイリオ", Font.PLAIN, 20));
        pl2.add(plwname);
        pl2.add(Box.createRigidArea(new Dimension(1, 20)));
        pl2.add(plwstones);

        gamePage.add(Box.createRigidArea(new Dimension(1, 10)));
        gameLog = new JLabel("メッセージログ");
        gameLog.setAlignmentX(0.5f);
        gameLog.setMinimumSize(new Dimension(200, 30));
        gameLog.setFont(new Font("メイリオ", Font.PLAIN, 20));
        gamePage.add(gameLog);

        // オセロ盤の生成
        JPanel brd = new JPanel();
        // brd.setMaxiSize(500, 500);
        brd.setMaximumSize(new Dimension(45 * row, 45 * row));
        brd.setMinimumSize(new Dimension(45 * row, 45 * row));
        brd.setBackground(new Color(0, 128, 0));
        brd.setLayout(new GridLayout(8, 8));

        buttonArray = new JButton[row * row];// ボタンの配列を作成
        for (int i = 0; i < row * row; i++) {
            buttonArray[i] = new JButton();
             // ボタンを配置する
            buttonArray[i].addActionListener(this);// マウス操作を認識できるようにする
            buttonArray[i].setActionCommand(Integer.toString(i));// ボタンを識別するための名前(番号)を付加する
            brd.add(buttonArray[i]);// ボタンの配列をペインに貼り付け
        }

        gamePage.add(brd);

        gamePage.add(Box.createRigidArea(new Dimension(1, 5)));

        warikomi = new JPanel();
        warikomi.setLayout(new FlowLayout(FlowLayout.CENTER));
        warikomi.setAlignmentX(0.5f);
        warikomi.setMaximumSize(new Dimension(450,90));
        // warikomi.setMaximumSize(new Dimension(100,100));
        warikomi.setLayout(new BoxLayout(warikomi, BoxLayout.PAGE_AXIS));
        warikomi.setBackground(Color.YELLOW);
        gamePage.add(warikomi);

        warikomiLabel = new JLabel("〇〇さんから対戦申込が届いています");
        warikomiLabel.setAlignmentX(0.5f);
        warikomiLabel.setMinimumSize(new Dimension(200, 30));
        warikomiLabel.setFont(new Font("メイリオ", Font.PLAIN, 20));
        warikomi.add(warikomiLabel);

        JPanel warikomiBtns = new JPanel();
        warikomiBtns.setLayout(new BoxLayout(warikomiBtns, BoxLayout.LINE_AXIS));
        warikomiBtns.setBackground(Color.YELLOW);
        warikomi.add(warikomiBtns);


        btnAccept = new JButton("対戦を受ける");
        btnAccept.setFont(new Font("メイリオ", Font.PLAIN, 20));
        // btnAccept.setAlignmentX(0.5f);
        btnAccept.setActionCommand("Game:btnAccept");
        btnAccept.addActionListener(this);
        warikomiBtns.add(btnAccept);

        warikomiBtns.add(Box.createRigidArea(new Dimension(20, 1)));

        btnDeny = new JButton("拒否");
        btnDeny.setFont(new Font("メイリオ", Font.PLAIN, 20));
        // btnDeny.setAlignmentX(0.5f);
        btnDeny.setActionCommand("Game:btnDeny");
        btnDeny.addActionListener(this);
        warikomiBtns.add(btnDeny);


        btnRetTop = new JButton("TOPに戻る");
        btnRetTop.setFont(new Font("メイリオ", Font.PLAIN, 20));
        btnRetTop.setAlignmentX(0.5f);
        btnRetTop.setActionCommand("Game:btnRetTop");
        btnRetTop.addActionListener(this);

        gamePage.add(Box.createRigidArea(new Dimension(1, 10)));
        gamePage.add(btnRetTop);

        cardPanel.add(gamePage, "Game");

    }

}
