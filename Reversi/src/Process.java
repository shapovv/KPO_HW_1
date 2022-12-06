import java.util.Scanner;
import java.awt.Point;
import java.util.InputMismatchException;

public class Process {
    private final Scanner sc = new Scanner(System.in);

    // Поля-константы, для определеня игрока.
    public static final int PLAYER = 0;
    public static final int COMPUTER = 1;

    // Поля, отвечающие за цвет отрисовки.
    public static final String CYAN_BACKGROUND = "\u001B[46m";
    public static final String RED_BACKGROUND = "\u001B[41m";
    static final String BLACK = "\u001b[30m";
    static final String RESET = "\u001b[0m";

    // Поле - экземпляр игровой доски.
    private Board board;

    //Вспомогательные поля.
    private int bestScore = 0;
    private Point cell;
    private Point score;
    private int turn;
    private int player;
    private boolean stop;

    public void start() {
        System.out.println("\nДобро пожаловать в РЕВЕРСИ! \n");
        System.out.println("Выберите режим игры:\n1 - Игрок против игрока \n2 - Компьютер против игрока \n3 - Умный компьютер против игрока");
        System.out.print("Ввод: ");
        int inputMode;
        inputMode = sc.nextInt();
        if (inputMode == 1) {
            runPvP();
        } else if (inputMode == 2) {
            initialize();
            runPvC();
        } else if (inputMode == 3) {
            initialize();
            board.isSmartComputer = true;
            runPvC();
            board.isSmartComputer = false;
        }

        while (true) {
            System.out.println("Хотите сыграть еще раз? ");
            System.out.println("1 - Да \n2 - Нет");
            int inputYesOrNot;
            inputYesOrNot = sc.nextInt();
            if (inputYesOrNot == 1) {
                System.out.println("Выберите режим игры:\n1 - Игрок против игрока \n2 - Компьютер против игрока \n3 - Умный компьютер против игрока");
                System.out.print("Ввод: ");
                inputMode = sc.nextInt();
                if (inputMode == 1) {
                    runPvP();
                } else if (inputMode == 2) {
                    initialize();
                    runPvC();
                } else if (inputMode == 3) {
                    initialize();
                    board.isSmartComputer = true;
                    runPvC();
                    board.isSmartComputer = false;
                }
            } else if (inputYesOrNot == 2) {
                System.out.println("_____________________________");
                System.out.println("Лучший счёт за сессию: " + bestScore);
                System.out.println("_____________________________");
                break;
            }
        }
    }

    public void runPvP() {
        initialize();
        board.cantCountHints = false;
        while (!stop) {
            if (!checkVictory()) {
                if (turn == 0) {
                    showHints();
                    rendering();
                    hideHints();
                    printScore();
                    turn++;
                } else {
                    readTurn();
//                    Дописать парсинг
//                    if (cell.x == -1 && cell.y == -1) {
//                    }
                    System.out.println("_____________________________");
                    processPlayersTurn();
                    showHints();
                    rendering();
                    hideHints();
                    printScore();
                    turn++;
                }
            } else {
                System.out.println("Игра окончена!");
                calculateScore();
                if (score.x > score.y) {
                    System.out.println("Победили чёрные!");
                } else if (score.y > score.x) {
                    System.out.println("Победили белые!");
                } else {
                    System.out.println("Ничья!");
                }
                if (score.x > bestScore) {
                    bestScore = score.x;
                } else if (score.y > bestScore) {
                    bestScore = score.y;
                }
                break;
            }
        }
    }

    private boolean checkVictory() {
        int counter = 0;
        showHints();
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                if (board.getCell(x, y) == Board.HINT) {
                    counter++;
                }
            }
        }
        hideHints();
        if (counter != 0) {
            return false;
        } else {
            return true;
        }
    }

    private void showHints() {
        board.makingTurnOfComputer(player);
    }

    private void hideHints() {
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                if (board.getCell(x, y) == Board.HINT) {
                    board.setCell(x, y, Board.EMPTY);
                }
            }
        }
    }

    public void runPvC() {
        showHints();
        rendering();
        hideHints();
        System.out.println("_____________________________");
        while (!stop) {
            if (!checkVictory()) {
                if (turn == PLAYER) {
                    board.cantCountHints = false;
                    readTurn();
                    processPlayersTurn();
                    rendering();
                    printScore();
                    turn++;
                } else if (turn == COMPUTER) {
                    board.cantCountHints = true;
                    System.out.println("_____________________________");
                    System.out.print("Компьютер сделал ход ");
                    processComputerTurn();
                    board.cantCountHints = false;
                    showHints();
                    rendering();
                    hideHints();
                    printScore();
                    System.out.println("_____________________________");
                    turn--;
                }
            } else {
                System.out.println("Игра окончена!");
                if (player == Board.WHITE) {
                    System.out.println("Победили чёрные!");
                } else {
                    System.out.println("Победили белые!");
                }
                if (score.x > bestScore) {
                    bestScore = score.x;
                }
                break;
            }
        }
    }

    private void initialize() {
        board = new Board();
        cell = new Point();
        player = Board.BLACK;
        stop = false;
        score = new Point(2, 2);
        turn = 0;
    }

    private void readTurn() {
        if (player == Board.BLACK) {
            System.out.println("Ход чёрных");
        } else {
            System.out.println("Ход белых");
        }
        System.out.print("X: ");
        cell.x = sc.nextInt();
        System.out.print("Y: ");
        cell.y = sc.nextInt();
        while (cell.x >= 9 || cell.y >= 9 || cell.x < -1 || cell.y < -1) {
            System.out.println("Некорректный ввод!");
            System.out.print("X: ");
            cell.x = sc.nextInt();
            System.out.print("Y: ");
            cell.y = sc.nextInt();
        }

    }


    private void processPlayersTurn() {
        board.makingTurnOfPlayer(cell.x, cell.y, player);
        player = ((player == Board.BLACK) ? Board.WHITE : Board.BLACK);

    }

    private void processComputerTurn() {
        board.makingTurnOfComputer(player);
        player = Board.BLACK;

    }

    private void rendering() {
        for (int x = 0; x < Board.WIDTH; x++) {
            if (x == 0) {
                System.out.printf("   %d ", x);
            } else if (x == Board.WIDTH - 1) {
                System.out.println(" " + x + " ");
            } else {
                System.out.print(" " + x + " ");
            }
        }
        for (int y = 0; y < Board.HEIGHT; y++) {
            System.out.print(y + " ");
            for (int x = 0; x < Board.WIDTH; x++) {
                switch (board.getCell(x, y)) {
                    case Board.BLACK -> System.out.print(CYAN_BACKGROUND + BLACK + " \u25CF " + RESET);
                    case Board.WHITE -> System.out.print(CYAN_BACKGROUND + " \u25CF " + RESET);
                    case Board.HINT -> System.out.print(CYAN_BACKGROUND + " \u25CC " + RESET);
                    case Board.EMPTY -> System.out.print(CYAN_BACKGROUND + "   " + RESET);
                    default -> System.out.print(RED_BACKGROUND + " ! " + RESET);
                }
            }
            System.out.println();
        }
    }

    private void calculateScore() {
        score.x = 0;
        score.y = 0;
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                if (board.getCell(x, y) == Board.BLACK) {
                    score.x += 1;
                } else if (board.getCell(x, y) == Board.WHITE) {
                    score.y += 1;
                }
            }
        }
    }

    private void printScore() {
        calculateScore();
        System.out.println("Чёрных: " + score.x + " Белых: " + score.y);
    }
}
