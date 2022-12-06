
public class Board {
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    public static final int EMPTY = -1;
    public static final int BLACK = 0;
    public static final int WHITE = 1;
    public static final int HINT = 2;

    private int PLAYER;

    private int[][] data;
    private int[][] dataCopy;

    public boolean cantCountHints = false;
    public boolean isSmartComputer = false;
    public boolean canMove = true;

    private double mainR = 0;
    private double mainRHard = -64;

    private int mainX;
    private int mainY;
    private int mainVX;
    private int mainVY;
    private int mainTX;
    private int mainTY;

    private int mainXH;
    private int mainYH;
    private int mainVXH;
    private int mainVYH;
    private int mainTXH;
    private int mainTYH;


    public Board() {
        data = new int[HEIGHT][WIDTH];
        dataCopy = new int[HEIGHT][WIDTH];

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (i == 3 && j == 3 || i == 4 && j == 4) {
                    data[i][j] = WHITE;
                    dataCopy[i][j] = WHITE;
                } else if (i == 3 && j == 4 || i == 4 && j == 3) {
                    data[i][j] = BLACK;
                    dataCopy[i][j] = BLACK;
                } else {
                    data[i][j] = EMPTY;
                    dataCopy[i][j] = EMPTY;
                }
            }
        }
    }

    private void put(int x, int y, int turn) {
        data[y][x] = turn;
    }

    public boolean makingTurnOfPlayer(int x, int y, int turn) {
        if (isEmptyCell(x, y)) {
            return checkNeighboringCells(x, y, turn);
        } else {
            return false;
        }
    }


    public void makingTurnOfComputer(int player) {
        PLAYER = player;
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                if (isEmptyCell(x, y)) {
                    checkNeighboringCellsForComputer(x, y);
                }
                if ((x == 7 && y == 7) && cantCountHints) {
                    if (isSmartComputer) {
                        setCell(mainXH, mainYH, PLAYER);
                        System.out.print("(" + mainXH + ";" + mainYH + ")\n");
                        flip(mainXH, mainYH, mainVXH, mainVYH, mainTXH, mainTYH, PLAYER);
                        mainRHard = -64;
                        mainR = 0;
                    } else {
                        if (canMove) {
                            setCell(mainX, mainY, PLAYER);
                            System.out.print("(" + mainX + ";" + mainY + ")\n");
                            flip(mainX, mainY, mainVX, mainVY, mainTX, mainTY, PLAYER);
                            mainR = 0;
                        }
                    }
                }
            }
        }
    }

    // Проверка Соседних клеток
    private boolean checkNeighboringCells(int x, int y, int turn) {
        boolean canPut = false;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (isOutOfBoard(x + j, y + i)) {
                    canPut = false;
                    continue;
                }
                if (isEnemyPlace(x + j, y + i, turn)) {
                    if (checkLineCells(x, y, j, i, turn)) {
                        canPut = true;
                    }
                }
            }
        }
        return canPut;
    }

    private boolean checkNeighboringCellsForComputer(int x, int y) {
        boolean canPut = false;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (isOutOfBoard(x + j, y + i)) {
                    canPut = false;
                    continue;
                }
                if (isEnemyPlaceForComputer(x + j, y + i)) {
                    if (checkLineCellsForComputer(x, y, j, i)) {
                        canPut = true;
                    }
                }
            }
        }
        return canPut;
    }

    private boolean checkLineCells(int x, int y, int vx, int vy, int turn) {
        boolean canPut = false;
        int tx = x;
        int ty = y;
        while (true) {
            tx += vx;
            ty += vy;
            if (isOutOfBoard(tx, ty)) {
                break;
            }
            if (!isEmptyCell(tx, ty)) {
                if (isMyPlace(tx, ty, turn)) {
                    canPut = true;
                    flip(x, y, vx, vy, tx, ty, turn);
                }
            } else {
                break;
            }
        }
        return canPut;
    }

    private boolean checkLineCellsForComputer(int x, int y, int vx, int vy) {
        boolean canPut = false;
        int tx = x;
        int ty = y;
        while (true) {
            tx += vx;
            ty += vy;
            if (isOutOfBoard(tx, ty)) {
                break;
            }
            if (!isEmptyCell(tx, ty)) {
                if (isPlaceOfComputer(tx, ty)) {
                    canPut = true;
                    if (cantCountHints) {
                        if (isSmartComputer) {
                            highIntelligence(x, y, vx, vy, tx, ty);
                        } else {
                            lowIntelligence(x, y, vx, vy, tx, ty);
                        }
                    } else {
                        put(x, y, HINT);
                    }
                }
            } else {
                break;
            }
        }
        return canPut;
    }

    private void highIntelligence(int x, int y, int vx, int vy, int endX, int endY) {
        int n = 0;
        double si;
        if (isEdgeCell(x, y)) {
            si = 2;
        } else {
            si = 1;
        }
        double ss;
        if (isCornerCell(x, y)) {
            ss = 0.8;
        } else if (isEdgeCell(x, y)) {
            ss = 0.4;
        } else {
            ss = 0;
        }
        double RH = 0;
        int tx = x;
        int ty = y;
        while (true) {
            if (tx == endX && ty == endY) {
                break;
            }
            n++;
            tx += vx;
            ty += vy;
        }
        copyOfArray(dataCopy, data);
        setCell(x, y, PLAYER);
        flip(x, y, vx, vy, tx, ty, PLAYER);
        isSmartComputer = false;
        canMove = false;
        PLAYER = BLACK;
        makingTurnOfComputer(PLAYER);
        PLAYER = WHITE;
        canMove = true;
        isSmartComputer = true;
        copyOfArray(data, dataCopy);
        for (int i = 1; i < n; i++) {
            RH += si;
        }
        RH += ss;
        RH -= mainR;
        if (RH > mainRHard) {
            mainRHard = RH;
            mainXH = x;
            mainYH = y;
            mainVXH = vx;
            mainVYH = vy;
            mainTXH = tx;
            mainTYH = ty;
        }

    }

    private void lowIntelligence(int x, int y, int vx, int vy, int endX, int endY) {
        int n = 0;
        double si;
        if (isEdgeCell(x, y)) {
            si = 2;
        } else {
            si = 1;
        }
        double ss;
        if (isCornerCell(x, y)) {
            ss = 0.8;
        } else if (isEdgeCell(x, y)) {
            ss = 0.4;
        } else {
            ss = 0;
        }
        double R = 0;
        int tx = x;
        int ty = y;
        while (true) {
            if (tx == endX && ty == endY) {
                break;
            }
            n++;
            tx += vx;
            ty += vy;
        }
        for (int i = 1; i < n; i++) {
            R += si;
        }
        R += ss;
        if (R > mainR) {
            mainR = R;
            mainX = x;
            mainY = y;
            mainVX = vx;
            mainVY = vy;
            mainTX = tx;
            mainTY = ty;
        }
    }

    private boolean isEdgeCell(int x, int y) {
        if ((x == 0 && y != 0 && y != 7) || (x == 7 && y != 0 && y != 7) || (y == 0 && x != 0 && x != 7) || (y == 7 && x != 0 && x != 7)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCornerCell(int x, int y) {
        if ((x == 0 && y == 0) || (x == 7 && y == 0) || (x == 0 && y == 7) || (x == 7 && y == 7)) {
            return true;
        } else {
            return false;
        }
    }

    private void flip(int x, int y, int vx, int vy, int endX, int endY, int turn) {
        int tx = x;
        int ty = y;
        while (true) {
            if (tx == endX && ty == endY) {
                break;
            }
            put(tx, ty, turn);
            tx += vx;
            ty += vy;
        }
    }

    public void setCell(int x, int y, int value) {
        data[y][x] = value;
    }

    public int getCell(int x, int y) {
        return data[y][x];
    }

    private boolean isOutOfBoard(int x, int y) {
        return x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT;
    }

    private boolean isMyPlace(int x, int y, int turn) {
        return data[y][x] == turn;
    }

    private boolean isPlaceOfComputer(int x, int y) {
        return data[y][x] == PLAYER;
    }

    //  Это вражеская клетка?
    private boolean isEnemyPlace(int x, int y, int turn) {
        return data[y][x] == ((turn == BLACK) ? WHITE : BLACK);
    }

    private boolean isEnemyPlaceForComputer(int x, int y) {
        return data[y][x] == ((PLAYER == BLACK) ? WHITE : BLACK);
    }

    private boolean isEmptyCell(int x, int y) {
        return data[y][x] == EMPTY || data[y][x] == HINT;
    }

    private void copyOfArray(int[][] a, int[][] b) {
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                a[y][x] = b[y][x];
            }
        }
    }
}
