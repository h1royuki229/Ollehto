class PlayerDriver{
    public static void main(String[] args) {
        Player player1 = new Player_Self("Jacob",Othello.BLACK);
        System.out.println("Player_SelfのコンストラクタでJacobとBLACK(=1)を入力しました。");
        System.out.println("name="+player1.name);
        System.out.println("player_color="+player1.player_color);

        Player player2 = new Player_Online("Emily",Othello.WHITE);
        System.out.println("Player_OnlineのコンストラクタでEmilyとWHITE(=-1)を入力しました。");
        System.out.println("name="+player2.name);
        System.out.println("player_color="+player2.player_color);
        
        Player player3 = new Player_CPU("Michael",Othello.BLACK);
        System.out.println("Player_CPUのコンストラクタでMichaelとBLACK(=1)を入力しました。");
        System.out.println("name="+player3.name);
        System.out.println("player_color="+player3.player_color);
        
        Player player4 = new Player_HardCPU("Emma",Othello.WHITE);
        System.out.println("Player_HardCPUのコンストラクタでEmmaとWHITE(=-1)を入力しました。");
        System.out.println("name="+player4.name);
        System.out.println("player_color="+player4.player_color);
        
    }
}