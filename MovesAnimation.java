public class MovesAnimation {
    public int warx, wary;                         // 配合棋盘，动画结束后显示棋子
    public int color;                              // 棋子颜色：BAI / HEI
    public int aniStaX, aniStaY, aniStaR, aniStaA; // 动画起始参数
    public int aniNowX, aniNowY, aniNowR, aniNowA; // 动画过程参数
    public int aniEndX, aniEndY, aniEndR, aniEndA; // 动画结束参数
    public boolean phaseTwo = false;               // 第二阶段：先缩小，再放大
    public int progress = 0;                       // 进度
    public int maxProgress = 120;

    public MovesAnimation(int wx, int wy, int c, int sx, int sy, int sr, int sa, int ex, int ey, int er, int ea) {
        warx = wx;
        wary = wy;
        color = c;
        progress = 0;
        aniStaX = aniNowX = sx;
        aniStaY = aniNowY = sy;
        aniStaR = aniNowR = sr;
        aniNowA = aniStaA = sa;
        aniEndX = ex;
        aniEndY = ey;
        aniEndR = er;
        aniEndA = ea;

        maxProgress = 120;
    }

    public MovesAnimation(int wx, int wy, int c, int sx, int sy, int sr, int sa, int ex, int ey, int er, int ea, int pr) {
        warx = wx;
        wary = wy;
        color = c;
        progress = 0;
        aniStaX = aniNowX = sx;
        aniStaY = aniNowY = sy;
        aniStaR = aniNowR = sr;
        aniNowA = aniStaA = sa;
        aniEndX = ex;
        aniEndY = ey;
        aniEndR = er;
        aniEndA = ea;

        maxProgress = pr;
    }

    public boolean next() {
        if (!phaseTwo) { // 第一阶段：缩小
            progress += 13;
            if (progress >= maxProgress) {
                phaseTwo = true;
                // progress = maxProgress;
            }
        } else {         // 第二阶段：放大
            progress -= 8;
            if (progress <= 100) {
                return false;
            }
        }
        aniNowX = aniStaX + (int)((aniEndX - aniStaX) * progress / 100);
        aniNowY = aniStaY + (int)((aniEndY - aniStaY) * progress / 100);
        aniNowR = aniStaR + (int)((aniEndR - aniStaR) * progress / 100);
        aniNowA = aniStaA + (int)((aniEndA - aniStaA) * progress / 100);
        if (aniNowA < 0) aniNowA = 0;
        if (aniNowA > 255) aniNowA = 255;
        if (aniNowR < 0) aniNowR = 0;

        return true;
    }
}