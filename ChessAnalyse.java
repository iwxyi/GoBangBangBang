import java.awt.Point;
import java.util.ArrayList;

public class ChessAnalyse {

    static final int KONG = 0;    // 无子
    static final int HEI = 1;     // 黑子
    static final int BAI = 2;     // 白子

    static final String KO = "0"; // 空
    static final String ME = "1"; // 己方
    static final String YO = "2"; // 对方
    static final String AT = "5"; // 当前按钮
    static final String PP = "9"; // 边界

    static int IQ = 100;          // AI智商程度

    int[][] chess;
    int nowTurn;
    int SPEC = 18;             // 规格：16*16

    int recentX, recentY;

    public ChessAnalyse(int spec) {
        chess = new int[100][100];
        SPEC = spec;
    }

    ArrayList<Point> overPoints1 = new ArrayList<Point>();
    ArrayList<Point> overPoints2 = new ArrayList<Point>();

    /**
     * 判断是否形成五子连珠
     * @param  war[][] 棋盘二位数组
     * @param  player  要判断的棋子颜色
     * @return         是否结束游戏
     */
    public boolean judgeOver(int war[][], int player, int nowX, int nowY) {
        chess = war;
        nowTurn = player;
        recentX = nowX;
        recentY = nowY;

        for (int i = 1; i < SPEC; i++) {
            for (int j = 1; j < SPEC; j++) {
                if ( is5ing(i, j) ) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 是否有连续5个棋子，取 右、下、左下、右下来判断
     * @param  x 当前判断的棋子所在的行数
     * @param  y 当前判断的棋子所在的列数
     * @return   是否有5个棋子
     */
    boolean is5ing(int x, int y) {
        if (x <= 0 || x >= SPEC) return false;
        if (y <= 0 || y >= SPEC) return false;
        if (chess[x][y] != nowTurn) return false;

        if (x < SPEC - 4 && chess[x + 1][y] == nowTurn && chess[x + 2][y] == nowTurn && chess[x + 3][y] == nowTurn && chess[x + 4][y] == nowTurn) {
            overPoints1.clear();
            overPoints2.clear();
            if (recentY == y) {
                x = recentX;
                y = recentY;
            }
            int i = 1;
            while (x + i > 0 && x + i < SPEC && chess[x + i][y] == nowTurn) {
                overPoints1.add(new Point(x + i, y));
                i++;
            }
            i = -1;
            while (x + i > 0 && x + i < SPEC && chess[x + i][y] == nowTurn) {
                overPoints2.add(new Point(x + i, y));
                i--;
            }
            return true;
        }
        if (y < SPEC - 4 && chess[x][y + 1] == nowTurn && chess[x][y + 2] == nowTurn && chess[x][y + 3] == nowTurn && chess[x][y + 4] == nowTurn) {
            overPoints1.clear();
            overPoints2.clear();
            if (recentX == x) {
                x = recentX;
                y = recentY;
            }
            int i = 1;
            while (y + i > 0 && y + i < SPEC && chess[x][y + i] == nowTurn) {
                overPoints1.add(new Point(x, y + i));
                i++;
            }
            i = -1;
            while (y + i > 0 && y + i < SPEC && chess[x][y + i] == nowTurn) {
                overPoints2.add(new Point(x, y + i));
                i--;
            }
            return true;
        }
        if (x > 4 && y < SPEC - 4 && chess[x - 1][y + 1] == nowTurn && chess[x - 2][y + 2] == nowTurn && chess[x - 3][y + 3] == nowTurn && chess[x - 4][y + 4] == nowTurn) {
            overPoints1.clear();
            overPoints2.clear();
            if (recentX - x == y - recentY) {
                x = recentX;
                y = recentY;
            }
            int i = 1;
            while (x + i > 0 && x + i < SPEC && y - i > 0 && y - i < SPEC && chess[x + i][y - i] == nowTurn) {
                overPoints1.add(new Point(x + i, y - i));
                i++;
            }
            i = -1;
            while (x + i > 0 && x + i < SPEC && y - i > 0 && y - i < SPEC && chess[x + i][y - i] == nowTurn) {
                overPoints2.add(new Point(x + i, y - i));
                i--;
            }
            return true;
        }
        if (x < SPEC - 4 && y < SPEC - 4 && chess[x + 1][y + 1] == nowTurn && chess[x + 2][y + 2] == nowTurn && chess[x + 3][y + 3] == nowTurn && chess[x + 4][y + 4] == nowTurn) {
            overPoints1.clear();
            overPoints2.clear();
            if (recentX - x == recentY - y) {
                x = recentX;
                y = recentY;
            }
            int i = 1;
            while (x + i > 0 && x + i < SPEC && y + i > 0 && y + i < SPEC && chess[x + i][y + i] == nowTurn) {
                overPoints1.add(new Point(x + i, y + i));
                i++;
            }
            i = -1;
            while (x + i > 0 && x + i < SPEC && y + i > 0 && y + i < SPEC && chess[x + i][y + i] == nowTurn) {
                overPoints2.add(new Point(x + i, y + i));
                i--;
            }
            return true;
        }

        return false;
    }

    /**
     * 获取最佳落子位置
     * @param war[][] 棋盘二维数组
     * @param player  要落子的棋子颜色
     */
    public Point getBestMoves(int war[][], int player) {
        chess = war;
        nowTurn = player;

        int[][] vals = new int[100][100];
        int maxVal = 0, maxX = 0, maxY = 0;

        for (int i = 1; i < SPEC; i++) {
            for (int j = 1; j < SPEC; j++) {
                if (chess[i][j] == KONG) {
                    vals[i][j] = analysePointVal(i, j);
                    if (vals[i][j] > maxVal) {
                        maxVal = vals[maxX = i][maxY = j];
                    }
                }

            }
        }

        ArrayList<Point> list = new ArrayList<Point>();
        if (maxVal > 0)
            list.add(new Point(maxX, maxY));
        for (int i = 1; i < SPEC; i++) {
            for (int j = 1; j < SPEC; j++) {
                if (vals[i][j] >= (int)(maxVal * IQ / 100))
                    list.add(new Point(i, j));
            }
        }
        if (list.size() == 0) {
            return new Point(maxX, maxY);
        }

        int rand = (int)(Math.random() * list.size());
        Point p = list.get(rand);
        return p;
    }

    /**
     * 判断每个点的价值
     * @param  x 行数
     * @param  y 列数
     * @return   价值
     */
    public int analysePointVal(int x, int y) {
        String s456 = getLine456(x, y);
        String s852 = getLine852(x, y);
        String s951 = getLine951(x, y);
        String s753 = getLine753(x, y);
        int result = judgeVal(s456) + judgeVal(s852) + judgeVal(s951) + judgeVal(s753);
        return result;
    }

    /**
     * 判断某个字符串中间点的价值
     * @param  s 连续的棋谱线
     * @return   价值
     */
    public int judgeVal(String s) {

        /*static*/ String[][] fmts = {
            {"200000", "11115", "11151", "11511", "15111", "51111"},
            {"100000", "22225", "22252", "22522", "25222", "52222"},
            {
                "20000", "01115", "01151", "01511", "05111",
                "     ", "11150", "11510", "15110", "51110"
            },
            {
                "10000", "022250", "022520", "025220",
                "     ", "022520", "025220", "052220"
            },
            {"5000", "11150", "05111"},
            {"5000", "022250", "052220"},
            {
                "500", "520220", "522020",
                "   ", "020225", "022025"
            },
            {"220", "01150", "01510", "05110"},
            {"200", "015010", "010510", "010150", "051010"},
            {
                "100", "25022", "25202",
                "   ", "20522", "22502",
                "   ", "20252", "22052"
            },
            {
                "100", "2-1110", "215110", "211510", "211150",
                "   ", "051112", "015112", "011512", "0111-2",
                "   ", "9-1110", "915110", "911510", "911150",
                "   ", "051119", "015119", "011519", "0111-9"
            },
            {
                "100", "122250", "122520", "125220", "1-2220",
                "   ", "0222-1", "022521", "025221", "052221",
                "   ", "922250", "922520", "925220", "9-2220",
                "   ", "0222-9", "022529", "025229", "052229"
            },
            {"90", "02250", "02520", "05220"},
            {
                "90", "21510", "215010", "210510", "210150", "251010",
                "  ", "91510", "915010", "910510", "910150", "951010",
                "  ", "01512", "015012", "010512", "010152", "051012",
                "  ", "01519", "015019", "010519", "010159", "051019"
            },
            {"40", "251110", "011152", "951110", "011159"},
            {"40", "152220", "022251", "952220", "022259"},
            {
                "40", "520221", "522021",
                "  ", "120225", "122025",
                "  ", "520229", "522029",
                "  ", "920225", "922025"
            },
            {
                "10", "011-2", "01512", "05112",
                "  ", "21150", "21510", "2-110",
                "  ", "011-9", "01519", "05119",
                "  ", "91150", "91510", "9-110"
            },
            {
                "10", "022-1", "02521", "05221",
                "  ", "12250", "12520", "1-220",
                "  ", "022-9", "02529", "05229",
                "  ", "92250", "92520", "9-220"
            },
            {"9", "0511", "0151", "5110", "1510"},
            {"9", "0115", "5110"},
            {"8", "0522", "0252", "5220", "2520"},
            {"8", "0225", "5220"},
            {"7", "01050", "05010"},
            {"5", "015", "051", "150", "510"},
            {"4", "025", "052", "250", "520"},
            {"2", "01152", "25110", "01159", "95110"},
            {"2", "02251", "15220", "02259", "95220"},
            {"2", "0151", "1510"},
            {"1", "15", "51", "25", "52"}
        };

        for (int i = 0; i < fmts.length; i++) {
            int rst = findStrings(s, fmts[i]);
            if (rst > 0) return rst;
        }

        return 0;
    }

    public int findStrings(String s, String[] fmt) {
        for (int i = 1; i < fmt.length; i++) {
            if (s.indexOf(fmt[i]) > -1)
                return Integer.parseInt(fmt[0]);
        }
        return 0;
    }

    public String getLine456(int x, int y) { // -
        String result = AT;
        for (int i = -1; i >= -5; i--)
            result = trans(x + i, y) + result;
        for (int i = 1; i <= 5; i++)
            result += trans(x + i, y);
        return result;
    }

    public String getLine852(int x, int y) { // |
        String result = AT;
        for (int i = -1; i >= -5; i--)
            result = trans(x, y + i) + result;
        for (int i = 1; i <= 5; i++)
            result += trans(x, y + i);
        return result;
    }

    public String getLine951(int x, int y) { // /
        String result = AT;
        for (int i = -1; i >= -5; i--)
            result = trans(x - i, y + i) + result;
        for (int i = 1; i <= 5; i++)
            result += trans(x - i, y + i);
        return result;
    }

    public String getLine753(int x, int y) { // \
        String result = AT;
        for (int i = -1; i >= -5; i--)
            result = trans(x + i, y + i) + result;
        for (int i = 1; i <= 5; i++)
            result += trans(x + i, y + i);
        return result;
    }

    public String trans(int x, int y) {
        if (x <= 0 || x >= SPEC || y <= 0 || y >= SPEC) return PP;
        if (chess[x][y] == KONG) return KO;
        if (chess[x][y] == nowTurn) return ME;
        return YO;
    }


}