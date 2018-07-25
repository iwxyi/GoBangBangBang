import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.Dimension;
import javax.swing.ImageIcon;

public class Main extends JFrame {

    int W_WIDTH = 702, W_HEIGHT = 727;                        // 窗口宽高

    public Main() {
        ChessBoard chessBoard = new ChessBoard(this);
        new LocationUtil(this);

        Dimension screensize = new Dimension();
        screensize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((int)(screensize.getWidth() - W_WIDTH) / 2, (int)(screensize.getHeight() - W_HEIGHT) / 2 - 10, W_WIDTH, W_HEIGHT);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(chessBoard);
        this.setIconImage(new ImageIcon("img/huaji1.png").getImage());
        this.setTitle("GoBangBangBang~");
        this.setVisible(true);

        Thread t = new Thread(chessBoard);
        chessBoard.run();
    }

    public static void main(String[] args) {
        Main main = new Main();
    }
}