import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.awt.Point;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.io.File;
import java.nio.Buffer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import javax.swing.plaf.basic.BasicMenuItemUI;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.image.ImageObserver;

public class ChessBoard extends JPanel implements Runnable {

    static final int KONG = 0;                                          // 无子
    static final int HEI = 1;                                           // 黑子
    static final int BAI = 2;                                           // 白子
    static final int ANIING = 1;                                        // 棋子状态：动画中

    static final boolean DEBUG = false;                                  // 调试模式
    final int SB = 0;                                                   // 调整AI的智障程序

    int SPEC = 18;                                                      // 规格：16*16
    int W_WIDTH = 702, W_HEIGHT = 727;                                  // 窗口宽高
    int B_LEFT = 0, B_TOP = 25, B_WIDTH = 702, B_HEIGHT = 702;          // 棋盘位置
    int RADIUS = 14, SIDE = 32;                                         // 棋子半径、格子边长、线条一半

    boolean PVP = false;                                                // 玩家 VS 玩家
    boolean GAME_OVER = false;                                          // 游戏是否结束
    int nowTurn;                                                        // 当前回合
    int war[][] = new int[100][100], warStatus[][] = new int[100][100]; // 战局（落子状态）、显示状态
    ArrayList<Point> record ;                                           // 记录对
    ArrayList<MovesAnimation>movesAnis;                                 // 落子动画
    int recentX = 0, recentY = 0;                                       // 上一个落子位置
    boolean HUAJI = false;

    boolean dragging = false;                                           // 拖拽中
    boolean aniing;                                                     // 是否在动画中
    int mouseX, mouseY;                                                 // 当前鼠标的位置
    int pressX, pressY;                                                 // 鼠标按下的位置
    StartAnimation startAni;                                            // 启动动画
    ChessAnalyse chessAI;                                               // 五子棋AI
    int overAniCount = 0, overAniDelay = 5;                            // 结束动画计数器
    int overAniRadius = 10;

    JButton OverButton = new JButton("游戏结束");
    JMenuItem backChess, tipChess;
    JFrame frame;

    public ChessBoard(JFrame jf) {
        frame = jf;

        RADIUS = (int)((SIDE = W_HEIGHT / SPEC) * 0.45);

        JMenuBar bar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem restart, startPVE, startPVP, exitGame;
        JMenuItem saveGame, loadGame;
        gameMenu.add(restart = new JMenuItem("restart"));
        gameMenu.add(startPVE = new JMenuItem("begin PVE"));
        gameMenu.add(startPVP = new JMenuItem("begin PVP"));
        startPVE.setEnabled(false);
        gameMenu.addSeparator();
        gameMenu.add(saveGame = new JMenuItem("Save File"));
        gameMenu.add(loadGame = new JMenuItem("Load File"));
        gameMenu.addSeparator();
        gameMenu.add(exitGame = new JMenuItem("Exit"));
        JMenu operaMenu = new JMenu("Opera");
        // JMenuItem backChess, tipChess;
        JMenuItem IQSimple, IQNormal, IQHard;
        operaMenu.add(backChess = new JMenuItem("Back"));
        operaMenu.add(tipChess = new JMenuItem("Tip"));
        operaMenu.addSeparator();
        backChess.setEnabled(false);
        tipChess.setEnabled(false);
        operaMenu.add(IQSimple = new JMenuItem("Simple"));
        operaMenu.add(IQNormal = new JMenuItem("Normal"));
        operaMenu.add(IQHard = new JMenuItem("Hard"));
        operaMenu.addSeparator();
        JMenuItem huajiMenu;
        operaMenu.add(huajiMenu = new JMenuItem("滑稽"));
        IQHard.setEnabled(false);
        IQSimple.setUI(new MyMenuItemUI(null, Color.GREEN));
        IQNormal.setUI(new MyMenuItemUI(null, Color.YELLOW));
        IQHard.setUI(new MyMenuItemUI(null, Color.RED));
        chessAI.IQ = 100;
        bar.setOpaque(false);
        bar.setBorderPainted(false);
        bar.add(gameMenu);
        bar.add(operaMenu);
        this.add(bar);

        InitGame();

        OverButton.setBounds(300, 300, 100, 500);
        this.add(OverButton);
        OverButton.addMouseListener(new MouseListener() {

            public void mouseReleased(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseClicked(MouseEvent e) {
                GAME_OVER = false;
                InitGame();
            }
        });

        this.addMouseListener(new MouseListener() {

            public void mouseReleased(MouseEvent e) {
                if (!dragging) return ;
                dragging = false;
                int x = e.getX(), y = e.getY();
                int war_x = (x - B_LEFT + SIDE / 2) / SIDE;
                int war_y = (y - B_TOP + SIDE / 2) / SIDE;
                if (war_x <= 0 || war_x >= SPEC) return;
                if (war_y <= 0 || war_y >= SPEC) return;
                if (moves(war_x, war_y, nowTurn)) {
                    recentX = war_x;
                    recentY = war_y;
                    log("nowTurn=" + (chessAI.nowTurn = nowTurn) + "  val:" + chessAI.analysePointVal(war_x, war_y));
                    warStatus[war_x][war_y] = ANIING;
                    movesAnis.add(new MovesAnimation(war_x, war_y, nowTurn, x, y, (int)(RADIUS * 1.5), 127, B_LEFT + war_x * SIDE, B_TOP + war_y * SIDE, RADIUS, 255));
                    nextTurn();
                } else {
                    movesAnis.add(new MovesAnimation(0, 0, nowTurn, x, y, (int)(RADIUS * 1.5), 127, x, y, RADIUS, 0, 100));
                }
                repaint();
            }

            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                if (aniing || GAME_OVER || mouseY < B_TOP) {
                    return ;
                }
                dragging = true;
                repaint();
            }

            public void mouseExited(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseClicked(MouseEvent e) {

            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {

            public void mouseMoved(MouseEvent e) {
                ;
            }

            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    mouseX = e.getX();
                    mouseY = e.getY();
                    repaint();
                } else {
                    int left = frame.getLocation().x;
                    int top  = frame.getLocation().y;
                    frame.setLocation(left + e.getX() - mouseX, top + e.getY() - mouseY);
                }

            }
        });

        restart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                InitGame();
            }
        });

        startPVP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PVP = true;
                startPVP.setEnabled(false);
                startPVE.setEnabled(true);
                InitGame();
            }
        });

        startPVE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PVP = false;
                startPVP.setEnabled(true);
                startPVE.setEnabled(false);
                InitGame();
            }
        });

        exitGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        saveGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.showSaveDialog(null);
                File f = jfc.getSelectedFile();
                if (f == null) return ;
                saveFile(f);
            }
        });

        loadGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.showOpenDialog(null);
                File f = jfc.getSelectedFile();
                if (f == null) return ;
                if (GAME_OVER) {
                    OverButton.setVisible(false);
                    GAME_OVER = false;
                }
                for (int i = 1; i < SPEC; i++) {
                    for (int j = 1; j < SPEC; j++) {
                        war[i][j] = KONG;
                    }
                }
                loadFile(f);
                repaint();
            }
        });

        backChess.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isOver = GAME_OVER;
                backOperator();
                if (!PVP && !(isOver && nowTurn == HEI)/*人机自己赢了，就只撤回一次*/) {
                    backOperator();

                    if (!PVP && isOver && nowTurn == BAI) {
                        nextTurn();
                    }
                }
                if (record.size() == 0) {
                    backChess.setEnabled(false);
                    tipChess.setEnabled(false);
                }
            }
        });

        tipChess.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tipOperator();
            }
        });

        IQSimple.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chessAI.IQ = 40;
                IQSimple.setEnabled(false);
                IQNormal.setEnabled(true);
                IQHard.setEnabled(true);
            }
        });

        IQNormal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chessAI.IQ = 70;
                IQSimple.setEnabled(true);
                IQNormal.setEnabled(false);
                IQHard.setEnabled(true);
            }
        });

        IQHard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chessAI.IQ = 100;
                IQSimple.setEnabled(true);
                IQNormal.setEnabled(true);
                IQHard.setEnabled(false);
            }
        });

        huajiMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (HUAJI == false) {
                    HUAJI = true;
                } else {
                    HUAJI = false;
                }
                repaint();
            }
        });
    }

    void InitGame() {
        for (int i = 0; i < 100; i++)
            for (int j = 0; j < 100; j++) {
                war[i][j] = KONG;
                warStatus[i][j] = 0;
            }

        nowTurn = HEI;
        GAME_OVER = false;
        aniing = true;
        startAni = new StartAnimation(SPEC, B_LEFT, B_TOP, B_WIDTH, B_HEIGHT, RADIUS, SIDE);
        chessAI = new ChessAnalyse(SPEC);
        recentX = recentY = 0;

        record = new ArrayList<Point>();
        movesAnis = new ArrayList<MovesAnimation>();

        OverButton.setVisible(false);
        tipChess.setEnabled(false);
        backChess.setEnabled(false);
    }

    public void paint(Graphics g) {
        super.paint(g);
        this.setBackground(new Color(214, 160, 91));

        if (aniing) {
            if (!startAni.paint(g)) {
                aniing = false;
            } else {
                return ;
            }
        }

        // 画线条
        for (int i = 0; i <= SPEC; i++) {
            g.drawLine(B_LEFT, B_TOP + i * SIDE, B_LEFT + B_WIDTH, B_TOP + i * SIDE);
            g.drawLine(B_LEFT + i * SIDE, B_TOP, B_LEFT + i * SIDE, B_TOP + B_WIDTH);
        }

        // 落子动画
        for (int i = 0; i < movesAnis.size(); i++) {
            MovesAnimation ma = movesAnis.get(i);
            if (ma.next()) {
                if (!HUAJI) {
                    if (ma.color == BAI) {
                        g.setColor(new Color(255, 255, 255, ma.aniNowA));
                    } else  {
                        g.setColor(new Color(0, 0, 0, ma.aniNowA));
                    }
                    g.fillArc(ma.aniNowX - ma.aniNowR, ma.aniNowY - ma.aniNowR, ma.aniNowR * 2, ma.aniNowR * 2, 0, 360);
                } else {
                    Image image;
                    if (ma.color == BAI) {
                        image = new ImageIcon("img/huaji1.png").getImage();
                    } else {
                        image = new ImageIcon("img/huaji2.png").getImage();
                    }
                    g.drawImage(image, ma.aniNowX - ma.aniNowR, ma.aniNowY - ma.aniNowR, ma.aniNowR * 2, ma.aniNowR * 2, null);
                }
            } else {
                warStatus[ma.warx][ma.wary] = 0;
                movesAnis.remove(ma);
                i--;
            }
        }

        // 画已经落下的子
        for (int i = 0; i < SPEC; i++) {
            for (int j = 0; j < SPEC; j++) {
                if (war[i][j] == KONG || warStatus[i][j] == ANIING) {
                    continue;
                }
                if (!HUAJI) {
                    if (war[i][j] == BAI) {
                        g.setColor(Color.white);
                    } else if (war[i][j] == HEI) {
                        g.setColor(Color.black);
                    }
                    g.fillArc(B_LEFT + SIDE * i - RADIUS, B_TOP + SIDE * j - RADIUS, RADIUS * 2, RADIUS * 2, 0, 360);
                } else {
                    Image image;
                    if (war[i][j] == BAI) {
                        image = new ImageIcon("img/huaji1.png").getImage();
                    } else {
                        image = new ImageIcon("img/huaji2.png").getImage();
                    }
                    g.drawImage(image, B_LEFT + SIDE * i - RADIUS, B_TOP + SIDE * j - RADIUS, RADIUS * 2, RADIUS * 2, null);
                }
            }
        }

        // 画最后一个落子的十字交叉线
        if (recentX > 0 && recentY > 0 && warStatus[recentX][recentY] == 0) {
            if (war[recentX][recentY] == BAI)
                g.setColor(Color.black);
            else
                g.setColor(Color.white);
            g.drawLine(B_LEFT + recentX * SIDE - RADIUS / 3, B_TOP + recentY * SIDE, B_LEFT + recentX * SIDE + RADIUS / 3, B_TOP + recentY * SIDE);
            g.drawLine(B_LEFT + recentX * SIDE, B_TOP + recentY * SIDE - RADIUS / 3, B_LEFT + recentX * SIDE, B_TOP + recentY * SIDE + RADIUS / 3);
        }

        // 拖拽中的话画拖拽的子
        if (dragging) {
            if (!HUAJI) {
                if (nowTurn == BAI) {
                    g.setColor(new Color(255, 255, 255, 128));
                } else  {
                    g.setColor(new Color(0, 0, 0, 128));
                }
                g.fillArc((int)(mouseX - RADIUS * 1.5), (int)(mouseY - RADIUS * 1.5), RADIUS * 3, RADIUS * 3, 0, 360);
            } else {
                Image image;
                if (nowTurn == BAI) {
                    image = new ImageIcon("img/huaji1.png").getImage();
                } else {
                    image = new ImageIcon("img/huaji2.png").getImage();
                }
                g.drawImage(image, (int)(mouseX - RADIUS * 1.5), (int)(mouseY - RADIUS * 1.5), RADIUS * 3, RADIUS * 3, null);
            }

        }

        // 游戏结束
        if (GAME_OVER) {
            String text = "";
            if (nowTurn == BAI)
                text = "白子胜利！";
            else
                text = "黑子胜利！";
            OverButton.setText(text);
            OverButton.setVisible(true);

            if (chessAI.overPoints1.size() > 0 || chessAI.overPoints2.size() > 0) {
                overAniCount++;
                if (overAniCount > overAniDelay) {
                    overAniCount = 0;
                    Point p;
                    if (chessAI.overPoints1.size() > 0) {
                        p = chessAI.overPoints1.get(0);
                        warStatus[p.x][p.y] = 1;
                        movesAnis.add(new MovesAnimation(p.x, p.y, nowTurn, B_LEFT + p.x * SIDE, B_TOP + p.y * SIDE, (int)(RADIUS * 0.5), 255, B_LEFT + p.x * SIDE, B_TOP + p.y * SIDE, RADIUS, 255));
                        chessAI.overPoints1.remove(0);
                    }
                    if (chessAI.overPoints2.size() > 0) {
                        p = chessAI.overPoints2.get(0);
                        warStatus[p.x][p.y] = 1;
                        movesAnis.add(new MovesAnimation(p.x, p.y, nowTurn, B_LEFT + p.x * SIDE, B_TOP + p.y * SIDE, (int)(RADIUS * 0.5), 255, B_LEFT + p.x * SIDE, B_TOP + p.y * SIDE, RADIUS, 255));
                        chessAI.overPoints2.remove(0);
                    }
                }

            }

            if (overAniRadius < B_HEIGHT) {
                overAniO(g);
            } else if (overAniCount < 0) {
                overAniCount = 0;
            }
        }
    }

    /**
     * 落子函数
     * @param  x    格子的X
     * @param  y    格子的Y
     * @param  kind 当前颜色：黑/白
     * @return      是否下子成功，若失败则返回false
     */
    public boolean moves(int x, int y, int kind) {
        if (war[x][y] != KONG)
            return false;
        war[x][y] = kind;
        record.add(new Point(x, y));
        repaint();
        return true;
    }

    /**
     * 当前回合落子结束，尝试切换到下一个回合
     * 如果是人机，则电脑落子，并重新切换到人
     * 人机的话，玩家是黑子
     */
    public void nextTurn() {

        // 判断游戏是否结束
        if (judgeOver()) {
            gameOver();
            return ;
        }

        if (nowTurn == HEI) {
            nowTurn = BAI;

            if (!PVP) {
                AI_moves();
                nextTurn();
            }
        } else  {
            nowTurn = HEI;
        }

        backChess.setEnabled(true);
        tipChess.setEnabled(true);
    }

    public void gameOver() {
        GAME_OVER = true;
        tipChess.setEnabled(false);
        overAniCount = -100000;
        overAniRadius = 10;
    }

    public void overAniO(Graphics g) {

        int x = B_LEFT + recentX * SIDE;
        int y = B_TOP + recentY * SIDE;
        int val2;

        for (int i = -5; i <= 5; i++) {
            g.setColor(new Color(255, 215, 0, 255 - 10 * Math.abs(i)));
            g.drawOval(x - overAniRadius + i, y - overAniRadius + i, (overAniRadius + i) * 2, (overAniRadius + i) * 2);
        }
        overAniRadius += 10;

        for (int i = 1; i < SPEC; i++) {
            for (int j = 1; j < SPEC; j++) {
                if (war[i][j] != KONG && war[i][j] != nowTurn && warStatus[i][j] == 0) {
                    val2 = ((i - recentX) * (i - recentX) + (j - recentY) * (j - recentY)) * SIDE * SIDE;
                    if (val2 >= (overAniRadius - 5) * (overAniRadius - 5) && val2 <= (overAniRadius + 5) * (overAniRadius + 5)) {
                        warStatus[i][j] = 1;
                        movesAnis.add(new MovesAnimation(i, j, war[i][j], B_LEFT + i * SIDE, B_TOP + j * SIDE, (int)(RADIUS * 0.5), 155, B_LEFT + i * SIDE, B_TOP + j * SIDE, RADIUS, 255));
                    }
                }
            }
        }
    }

    /**
     * 判断当前的棋子是否形成五子
     * @return 本局是否结束(即五子连珠)
     */
    public boolean judgeOver() {

        return chessAI.judgeOver(war, nowTurn, recentX, recentY);
    }

    /**
     * AI 落子
     */
    public void AI_moves() {
        Point point = chessAI.getBestMoves(war, nowTurn);

        int x = point.x, y = point.y;
        if (x <= 0 || x >= SPEC || y <= 0 || y >= SPEC || war[x][y] != KONG)
            return ;
        recentX = x;
        recentY = y;
        war[x][y] = nowTurn;
        record.add(new Point(x, y));
        warStatus[x][y] = ANIING;
        movesAnis.add(new MovesAnimation(x, y, nowTurn, B_LEFT + x * SIDE, B_TOP + y * SIDE, (int)(RADIUS * 0.1), 127, B_LEFT + x * SIDE, B_TOP + y * SIDE, RADIUS, 255));
    }

    public void backOperator() {
        if (record.size() == 0)
            return ;

        Point p = record.get(record.size() - 1);
        movesAnis.add(new MovesAnimation(p.x, p.y, war[p.x][p.y], B_LEFT + p.x * SIDE, B_TOP + p.y * SIDE, RADIUS, 255, B_LEFT + p.x * SIDE, B_TOP + p.y * SIDE, 0, 0, 100));
        war[p.x][p.y] = 0;
        record.remove(record.size() - 1);

        if (record.size() > 0) {
            p = record.get(record.size() - 1);
            recentX = p.x;
            recentY = p.y;
        } else {
            recentX = recentY = 0;
        }

        repaint();

        if (GAME_OVER) {
            /*if (nowTurn != HEI)
                nextTurn();*/
            GAME_OVER = false;
            OverButton.setVisible(false);
            tipChess.setEnabled(true);
        }
    }

    public void tipOperator()  {
        AI_moves();
        nextTurn();
    }

    /**
     * 线程定时操作
     */
    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(24);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存到文件
     * @param f 文件
     */
    public void saveFile(File f) {
        PrintWriter out;
        try {
            out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f)));
            Point p;
            for (int i = 0; i < record.size(); i++) {
                p = record.get(i);
                out.println(p.x + ":" + p.y + ":" + war[p.x][p.y]);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载文件
     * @param f 文件
     */
    public void loadFile(File f) {
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line = "";
            while ( (line = in.readLine()) != null ) {
                parseDate(line);
            }
            if (record.size() > 0) {
                backChess.setEnabled(true);
                if (!PVP && nowTurn == BAI) {
                    if (judgeOver()) {
                        gameOver();
                        return ;
                    }
                }
            }
            nextTurn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中读取一行记录
     * @param line 一行字符串，格式为： x:y:c
     */
    public void parseDate(String line) {
        String[] str = line.split(":");
        int x = Integer.parseInt(str[0]);
        int y = Integer.parseInt(str[1]);
        int c = Integer.parseInt(str[2]);
        record.add(new Point(x, y));
        nowTurn = war[x][y] = c;
        recentX = x;
        recentY = y;
    }

    class MyMenuItemUI extends BasicMenuItemUI {
        public MyMenuItemUI(Color bg, Color fg) {
            if (bg != null)
                super.selectionBackground = bg;
            if (fg != null)
                super.selectionForeground = fg;
        }
    }

    public static void log(Object o) {
        if (DEBUG) {
            System.out.println(o.toString());
        }
    }
}