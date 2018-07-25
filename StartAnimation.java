import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;

/**
 * 动画流程：
 *   - 先延迟下降
 *   - 再从中间分开
 */

public class StartAnimation {

    int step;                     // 动画步骤
    int progress1, progress2;     // 动画进程
    int speed = 78;               // 最大进程

    int LEFT, TOP, WIDTH, HEIGHT; // 棋盘位置
    int RADIUS, SIDE;             // 棋子半径、格子边长、线条一半
    int SPEC = 18;

    HLine[] hline;
    VLine[] vline;

    public StartAnimation(int s, int bl, int bt, int bw, int bh, int ra, int side) {
        SPEC = s;
        LEFT = bl;
        TOP = bt;
        WIDTH = bw;
        HEIGHT = bh;
        RADIUS = ra;
        SIDE = side;

        step = 1;
        progress1 = progress2 = 0;

        hline = new HLine[100];
        vline = new VLine[100];

        initLines();
    }

    void initLines() {
        for (int i = 0; i <= SPEC; i++) {
            hline[i] = new HLine(TOP - SIDE);

            vline[i] = new VLine(LEFT + SIDE * SPEC / 2);
        }
    }

    public boolean paint(Graphics g) {

        if (step <= 1) {
            progress1++;
            System.out.println();
            if (progress1 > speed * 0.5) {
                progress2++;
            }
            if (progress1 >= speed) {
                step++;
            }
        } else {
            progress2++;
        }
        if (step >= 2 && progress2 >= speed)
            return false;

        if (step > 0) { // 横线下降
            for (int i = 0; i <= SPEC; i++) {
                hline[i].paint(g);

                double maxProgress = speed - 60 * i / SPEC;
                double per = progress1 / maxProgress;
                if (per > 1) per = 1;
                hline[i].y = TOP - SIDE + (int)( (SIDE * i + SIDE) * per );
            }
        }
        if (step >= 2 || (step == 1 && progress2 > 0)) { // 竖线出现
            for (int i = 0; i <= SPEC; i++) {
                vline[i].paint(g);

                double maxProgress = speed - 50 * Math.abs((i - SPEC / 2)) / (SPEC / 2);
                double per = progress2 / maxProgress;
                if (per > 1) per = 1;
                vline[i].x = LEFT + SIDE * SPEC / 2 + (int)( (i - SPEC / 2) * SIDE * per );
            }
        }

        return true;
    }

    class HLine {
        int y;

        HLine(int yy) {
            y = yy;
        }

        void paint(Graphics g) {
            g.drawLine(LEFT, y, LEFT + WIDTH, y);
        }
    }

    class VLine {
        int x;

        VLine(int xx) {
            x = xx;
        }

        void paint(Graphics g) {
            g.drawLine(x, TOP, x, TOP + HEIGHT);
        }
    }

}