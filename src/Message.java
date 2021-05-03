import java.io.Serializable;

public class Message implements Serializable {
    static final long serialVersionUID = 1;

    Message(int type, String username) {
        this.type = type;
        this.username = username;
    }

    Message(int type) {
        this.type = type;
        this.username = "nanashi";
    }

    Message() {
        this.type = 0;
        this.username = "nanashi";
    }

    public int type;
    /*
     * 0:空のメッセージ
     * 101:クライアントからサーバへの接続要求 
     * 102:サーバからクライアントへの接続成功の報告
     * 
     * 201:クライアントからサーバへのオンライン対戦の申込 
     * 202:「対戦相手が見つかりました！」状態のサーバからの返答←いらないとおもう。
     * 212:「対戦相手を探しています」状態のサーバからの返答 
     * 222:「コンピュータ対戦中のユーザに対戦を申し込んでいます」状態のサーバからの返答
     * 232:「CPU対戦中のユーザにオンライン対戦を申し込む」サーバからクライアントへのメッセージ
     * 231:「対戦承認（コンピュータ対戦中のユーザ）」のクライアントからの応答 
     * 241:「対戦拒否（コンピュータ対戦中のユーザ）」のクライアントからの応答
     * 281:クライアントが「Topに戻る」を押したときのサーバへの報告
     * 282:サーバが「Topに戻る」を受理したことのクライアントへの報告
     * 292:「対戦相手が見つかりませんでした」状態のサーバからの返答
     * 
     * 302:マッチング完了、色を教えられる 
     * 311:手を打つ 
     * 321:ゲームの結果と終了のお知らせ
     * 332:マッチングしたはずの相手が離脱したことを伝えるメッセージ
     * 
     * 401:コンピュータ対戦モードに移行したことをサーバに伝える
     * 402:コンピュータ対戦モードに移行したことを承ったのをクライアントに伝える
     * 411:コンピュータ対戦モードが終了したことをサーバに伝える
     * 412:コンピュータ対戦モードが終了したことを承ったのをクライアントに伝える
     */
    public int x;
    public int y;
    public int color;
    public String username;
}
