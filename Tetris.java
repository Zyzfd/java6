import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;
import java.util.Scanner;
import java.util.Stack;


class Node {
    private String value;

    public Node(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
 
    @Override
    public String toString() {
        return value;
    }
 }

public class Tetris extends JFrame implements KeyListener{
    private Thread thread;
    private boolean isLeft = false;
    private boolean isRight = false;
    private boolean isUp = false;
    private boolean isDown = false;
    private boolean isR = false;
    private boolean queryLeft = false;
    private boolean queryRight = false;
    private boolean queryUp = false;
    private boolean queryDown = false;
    private boolean queryR = false;
    public static boolean exit = false;
    public static Stack<Node> stack = new Stack<>();
    public static int erased_lines = 0;
    public static int score = 0;
    public static String records_score = "0";
    public static String records_line = "0";
    public static int multiplier = 0;
    public static int speed_multiplier = 0;
    public static int[][][] game_field = new int[20][10][2];
    public static int[][][] next_falling = new int[2][10][2];
    public int[][] falling = new int[4][3];
    public static int figure_state = 0;
    public static int figure;
    public int[][][][] tetraminos = {
        {{{0,0}, {0,0}, {0,0}, {0,0}},
         {{0,0}, {0,0}, {0,0}, {0,0}},
         {{0,0}, {0,0}, {0,0}, {0,0}},
         {{0,0}, {0,0}, {0,0}, {0,0}}},

        {{{0,0}, {0,0}, {0,0}, {0,0}},
         {{0,0}, {0,0}, {0,0}, {0,0}},
         {{0,0}, {0,0}, {0,0}, {0,0}},
         {{0,0}, {0,0}, {0,0}, {0,0}}},

        {{{0,-1}, {0,-1}, {-1,1}, {-1,1}},
         {{0,1}, {-1,2}, {0,0}, {1,-1}},
         {{1,-1}, {1,-1}, {0,1}, {0,1}},
         {{-1,1}, {0,0}, {1,-2}, {0,-1}}},

        {{{0,2}, {1,-1}, {0,0}, {-1,1}},
         {{0,-1}, {0,1}, {1,0}, {1,0}},
         {{1,-1}, {0,0}, {-1,1}, {0,-2}},
         {{-1,0}, {-1,0}, {0,-1}, {0,1}}},

        {{{1,-1}, {0,0}, {-1,1}, {-2,2}},
         {{-1,1}, {0,0}, {1,-1}, {2,-2}},
         {{1,-1}, {0,0}, {-1,1}, {-2,2}},
         {{-1,1}, {0,0}, {1,-1}, {2,-2}}},

        {{{0,0}, {0,0}, {0,0}, {-1,1}},
         {{0,0}, {0,1}, {0,1}, {1,-1}},
         {{1,-1}, {0,0}, {0,0}, {0,0}},
         {{-1,1}, {0,-1}, {0,-1}, {0,0}}},

        {{{1,1}, {0,2}, {1,-1}, {0,0}},
         {{-1,-1}, {0,-2}, {-1,1}, {0,0}},
         {{1,1}, {0,2}, {1,-1}, {0,0}},
         {{-1,-1}, {0,-2}, {-1,1}, {0,0}}},

        {{{1,-1}, {0,1}, {1,0}, {0,2}},
         {{-1,1}, {0,-1}, {-1,0}, {0,-2}},
         {{1,-1}, {0,1}, {1,0}, {0,2}},
         {{-1,1}, {0,-1}, {-1,0}, {0,-2}}}
    };

    public Tetris() {
        this.addKeyListener(this);
        thread = new MoveThread(this);
        thread.start();

        timer_move.scheduleAtFixedRate(task_move, 0, 110);
        timer_rotate.scheduleAtFixedRate(task_rotate, 0, 125);
        timer_down.scheduleAtFixedRate(task_down, 0, 50);
        timer_main.scheduleAtFixedRate(task_main, 0, 10);
    }

    public static void main(String[] args) {
        JFrame win;
        try {
            win = new Tetris();
            Draw_graphics draw_gr = new Draw_graphics();
            draw_gr.setSize(300,100);
            win.setSize(1000, 1000);
            win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            win.setLayout(new BorderLayout(1, 1));
            win.add(draw_gr);
            FileReader fr = new FileReader("records.txt");
            Scanner scan = new Scanner(fr);
            
            if (scan.hasNextLine()) {
                records_score = scan.nextLine();
            }
            if (scan.hasNextLine()) {
                records_line = scan.nextLine();
            }
            scan.close();
            fr.close();
            win.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }



    public Timer timer_move = new Timer();
    public TimerTask task_move = new TimerTask(){
      public void run() {
        if (isLeft) {
            queryLeft = true;
        }
        if (isRight) {
            queryRight = true;
        }
      }
    };

    public Timer timer_rotate = new Timer();
    public TimerTask task_rotate = new TimerTask(){
        public void run() {
            if (isUp) {
                queryUp = true;
            }
        }
    };

    public Timer timer_down = new Timer();
    public TimerTask task_down = new TimerTask(){
        public void run() {
            if (isDown) {
                queryDown = true;
            };
            
        }
    };

    public Timer timer_main = new Timer();
    public TimerTask task_main = new TimerTask() {
        public void run() {
            if (queryLeft) {
                game(1);
                queryLeft = false;
            }
            if (queryRight) {
                game(2);
                queryRight = false;
            }
            if (queryUp) {
                game(3);
                queryUp = false;
            }
            if (queryDown) {
                game(4);
                queryDown = false;
            }
        }
        
    };
    

    public void game(int who) {
        for (int i = 3; i < 7; i++) {
            if (game_field[0][i][0] == 1 && game_field[0][i][1] == 0) {
                exit = true;
            }
        }

        for (int i = 4; i < 7; i++) {
            if (game_field[1][i][0] == 1 && game_field[1][i][1] == 0) {
                exit = true;
            }
        }
        

        for (int i = 0; i < 20; i++) {
            int sum = 0;
            for (int j = 0; j < 10; j++) {
                if (game_field[i][j][1] == 0) {
                    sum += game_field[i][j][0];
                }
            }
            if (sum == 10) {
                if (multiplier == 0) {
                    score += 100;
                } else if (multiplier == 1) {
                    score += 200;
                } else if (multiplier == 2) {
                    score += 400;
                } else if (multiplier == 3) {
                    score += 800;
                }
                multiplier++;
                erased_lines++;
                for (int k = i; k >= 0; k--) {
                    if (k == 0) {
                        for (int z = 0; z < 10; z++) {
                            for (int w = 0; w < 2; w++) {
                                game_field[k][z][w] = 0;
                            }
                        }
                    } else {
                        for (int z = 0; z < 10; z++) {
                            for (int w = 0; w < 2; w++) {
                                game_field[k][z][w] = game_field[k-1][z][w];
                            }
                        }
                    }
                }
            } else {
                multiplier = 0;
            }
        }

        boolean no_falling = true;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                falling[i][j] = 0;
            }
        }

        int k = 0;
        for (int i = 0; i < 20 && k < 4; i++) {
            for (int j = 0; j < 10 && k < 4; j++) {
                if (game_field[i][j][1] != 0) {
                    no_falling = false;
                    falling[k][0] = i;
                    falling[k][1] = j;
                    falling[k][2] = game_field[i][j][1];
                    k +=1 ;
                }
            }
        }

        if (no_falling) {
            Random random = new Random();
            int new_fig = random.nextInt(7) + 1;

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 10; j++) {
                    for (int d = 0; d < 2; d++) {
                        game_field[i][j][d] = next_falling[i][j][d];
                    }
                }
            }

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 10; j++) {
                    for (int d = 0; d < 2; d++) {
                        next_falling[i][j][d] = 0;
                    }
                }
            }

            switch (new_fig) {
                case 1:
                    next_falling[0][4][0] = 1;
                    next_falling[0][4][1] = 1;
                    next_falling[0][5][0] = 1;
                    next_falling[0][5][1] = 1;
                    next_falling[1][4][0] = 1;
                    next_falling[1][4][1] = 1;
                    next_falling[1][5][0] = 1;
                    next_falling[1][5][1] = 1;
                    break;
                case 2:
                    next_falling[0][3][0] = 1;
                    next_falling[0][3][1] = 2;
                    next_falling[1][4][0] = 1;
                    next_falling[1][4][1] = 2;
                    next_falling[1][3][0] = 1;
                    next_falling[1][3][1] = 2;
                    next_falling[1][5][0] = 1;
                    next_falling[1][5][1] = 2;
                    break;
                case 3:
                    next_falling[0][5][0] = 1;
                    next_falling[0][5][1] = 3;
                    next_falling[1][4][0] = 1;
                    next_falling[1][4][1] = 3;
                    next_falling[1][3][0] = 1;
                    next_falling[1][3][1] = 3;
                    next_falling[1][5][0] = 1;
                    next_falling[1][5][1] = 3;
                    break;
                case 4:
                    next_falling[0][3][0] = 1;
                    next_falling[0][3][1] = 4;
                    next_falling[0][4][0] = 1;
                    next_falling[0][4][1] = 4;
                    next_falling[0][5][0] = 1;
                    next_falling[0][5][1] = 4;
                    next_falling[0][6][0] = 1;
                    next_falling[0][6][1] = 4;
                    break;
                case 5:
                    next_falling[0][4][0] = 1;
                    next_falling[0][4][1] = 5;
                    next_falling[1][3][0] = 1;
                    next_falling[1][3][1] = 5;
                    next_falling[1][4][0] = 1;
                    next_falling[1][4][1] = 5;
                    next_falling[1][5][0] = 1;
                    next_falling[1][5][1] = 5;
                    break;
                case 6:
                    next_falling[0][4][0] = 1;
                    next_falling[0][4][1] = 6;
                    next_falling[0][5][0] = 1;
                    next_falling[0][5][1] = 6;
                    next_falling[1][3][0] = 1;
                    next_falling[1][3][1] = 6;
                    next_falling[1][4][0] = 1;
                    next_falling[1][4][1] = 6;
                    break;
                case 7:
                    next_falling[0][3][0] = 1;
                    next_falling[0][3][1] = 7;
                    next_falling[0][4][0] = 1;
                    next_falling[0][4][1] = 7;
                    next_falling[1][4][0] = 1;
                    next_falling[1][4][1] = 7;
                    next_falling[1][5][0] = 1;
                    next_falling[1][5][1] = 7;
                    break;
                default:
                    break;
            }

            figure_state = 0;
        } else {
            if (who == 0) {

                for (int i = 0; i < 4; i++) {
                    game_field[falling[i][0]][falling[i][1]][0] = 0;
                    game_field[falling[i][0]][falling[i][1]][1] = 0;
                }

                boolean el2 = falling[0][0]+1 < 20 && falling[1][0]+1 < 20 && falling[2][0]+1 < 20 && falling[3][0]+1 < 20;
                boolean el1 = false;
                if (el2) {
                    el1 = (game_field[falling[0][0]+1][falling[0][1]][0] == 0 || game_field[falling[0][0]+1][falling[0][1]][1] == falling[0][2]) && (game_field[falling[1][0]+1][falling[1][1]][0] == 0 || game_field[falling[1][0]+1][falling[1][1]][1] == falling[1][2]) && (game_field[falling[2][0]+1][falling[2][1]][0] == 0 || game_field[falling[2][0]+1][falling[2][1]][1] == falling[2][2]) && (game_field[falling[3][0]+1][falling[3][1]][0] == 0 || game_field[falling[3][0]+1][falling[3][1]][1] == falling[3][2]);
                }
                
                for (int i = 0; i < 4; i++) {
                    if (el1 && el2) {
                        if (falling[i][1] >= 0 && falling[i][1] < 10) {
                            game_field[falling[i][0]+1][falling[i][1]][0] = 1;
                            game_field[falling[i][0]+1][falling[i][1]][1] = falling[i][2];
                        } else if (falling[i][1] < 10){
                            game_field[falling[i][0]][0][0] = 1;
                            game_field[falling[i][0]][0][1] = falling[i][2];
                        } else {
                            game_field[19][falling[i][1]][0] = 1;
                            game_field[19][falling[i][1]][1] = falling[i][2];
                        }
                    } else {
                        game_field[falling[i][0]][falling[i][1]][0] = 1;
                        game_field[falling[i][0]][falling[i][1]][1] = 0;
                    }               

                }
            } else if (who == 1) {
                if (falling[0][1] - 1 >= 0 && falling[1][1] - 1 >= 0 && falling[2][1] - 1 >= 0 && falling[3][1] - 1 >= 0) {
                    if ((game_field[falling[0][0]][falling[0][1] - 1][0] == 0 || game_field[falling[0][0]][falling[0][1] - 1][1] == falling[0][2]) && (game_field[falling[1][0]][falling[1][1] - 1][0] == 0 || game_field[falling[1][0]][falling[1][1] - 1][1] == falling[1][2]) && (game_field[falling[2][0]][falling[2][1] - 1][0] == 0 || game_field[falling[2][0]][falling[2][1] - 1][1] == falling[2][2]) && (game_field[falling[3][0]][falling[3][1] - 1][0] == 0 || game_field[falling[3][0]][falling[3][1] - 1][1] == falling[3][2])) {
                        for (int i = 0; i < 4; i++) {
                            game_field[falling[i][0]][falling[i][1]][0] = 0;
                            game_field[falling[i][0]][falling[i][1]][1] = 0;
                        }
                        for (int i = 0; i < 4; i++) {
                            game_field[falling[i][0]][falling[i][1]-1][0] = 1;
                            game_field[falling[i][0]][falling[i][1]-1][1] = falling[i][2];
                        }
                    }
                }
            } else if (who == 2) {
                if (falling[0][1] + 1 < 10 && falling[1][1] + 1 < 10 && falling[2][1] + 1 < 10 && falling[3][1] + 1 < 10 && (game_field[falling[0][0]][falling[0][1] + 1][0] == 0 || game_field[falling[0][0]][falling[0][1] + 1][1] == falling[0][2]) && (game_field[falling[1][0]][falling[1][1] + 1][0] == 0 || game_field[falling[1][0]][falling[1][1] + 1][1] == falling[1][2]) && (game_field[falling[2][0]][falling[2][1] + 1][0] == 0 || game_field[falling[2][0]][falling[2][1] + 1][1] == falling[2][2]) && (game_field[falling[3][0]][falling[3][1] + 1][0] == 0 || game_field[falling[3][0]][falling[3][1] + 1][1] == falling[3][2])) {
                    for (int i = 0; i < 4; i++) {
                        game_field[falling[i][0]][falling[i][1]][0] = 0;
                        game_field[falling[i][0]][falling[i][1]][1] = 0;
                    }
                    for (int i = 0; i < 4; i++) {
                        game_field[falling[i][0]][falling[i][1]+1][0] = 1;
                        game_field[falling[i][0]][falling[i][1]+1][1] = falling[i][2];
                    }
                }
            } else if (who == 3) {
                boolean el = true;
                figure = falling[0][2];
                
                if (figure_state == 3) {
                    if (falling[0][0] + tetraminos[figure][0][0][0] >= 0 && falling[1][0] + tetraminos[figure][0][1][0] >= 0 && falling[2][0] + tetraminos[figure][0][2][0] >= 0 && falling[3][0] + tetraminos[figure][0][3][0] >= 0 && falling[0][0] + tetraminos[figure][0][0][0] < 20 && falling[1][0] + tetraminos[figure][0][1][0] < 20 && falling[2][0] + tetraminos[figure][0][2][0] < 20 && falling[3][0] + tetraminos[figure][0][3][0] < 20 && falling[0][1] + tetraminos[figure][0][0][1] >= 0 && falling[1][1] + tetraminos[figure][0][1][1] >= 0 && falling[2][1] + tetraminos[figure][0][2][1] >= 0 && falling[3][1] + tetraminos[figure][0][3][1] >= 0 && falling[0][1] + tetraminos[figure][0][0][1] < 10 && falling[1][1] + tetraminos[figure][0][1][1] < 10 && falling[2][1] + tetraminos[figure][0][2][1] < 10 && falling[3][1] + tetraminos[figure][0][3][1] < 10) {
                        if ((game_field[falling[0][0] + tetraminos[figure][0][0][0]][falling[0][1] + tetraminos[figure][0][0][1]][0] == 1 && game_field[falling[0][0] + tetraminos[figure][0][0][0]][falling[0][1] + tetraminos[figure][0][0][1]][1] == 0) || (game_field[falling[1][0] + tetraminos[figure][0][1][0]][falling[1][1] + tetraminos[figure][0][1][1]][0] == 1 && game_field[falling[1][0] + tetraminos[figure][0][1][0]][falling[1][1] + tetraminos[figure][0][1][1]][1] != falling[1][2]) || (game_field[falling[2][0] + tetraminos[figure][0][2][0]][falling[2][1] + tetraminos[figure][0][2][1]][0] == 1 && game_field[falling[2][0] + tetraminos[figure][0][2][0]][falling[2][1] + tetraminos[figure][0][2][1]][1] != falling[2][2]) || (game_field[falling[3][0] + tetraminos[figure][0][3][0]][falling[3][1] + tetraminos[figure][0][3][1]][0] == 1 && game_field[falling[3][0] + tetraminos[figure][0][3][0]][falling[3][1] + tetraminos[figure][0][3][1]][1] != falling[3][2])){
                            el = false;
                        }
                    } else {
                        el = false;
                    }
                } else {
                    if (falling[0][0] + tetraminos[figure][figure_state+1][0][0] >= 0 && falling[1][0] + tetraminos[figure][figure_state+1][1][0] >= 0 && falling[2][0] + tetraminos[figure][figure_state+1][2][0] >= 0 && falling[3][0] + tetraminos[figure][figure_state+1][3][0] >= 0 && falling[0][0] + tetraminos[figure][figure_state+1][0][0] < 20 && falling[1][0] + tetraminos[figure][figure_state+1][1][0] < 20 && falling[2][0] + tetraminos[figure][figure_state+1][2][0] < 20 && falling[3][0] + tetraminos[figure][figure_state+1][3][0] < 20 && falling[0][1] + tetraminos[figure][figure_state+1][0][1] >= 0 && falling[1][1] + tetraminos[figure][figure_state+1][1][1] >= 0 && falling[2][1] + tetraminos[figure][figure_state+1][2][1] >= 0 && falling[3][1] + tetraminos[figure][figure_state+1][3][1] >= 0 && falling[0][1] + tetraminos[figure][figure_state+1][0][1] < 10 && falling[1][1] + tetraminos[figure][figure_state+1][1][1] < 10 && falling[2][1] + tetraminos[figure][figure_state+1][2][1] < 10 && falling[3][1] + tetraminos[figure][figure_state+1][3][1] < 10) {
                        if ((game_field[falling[0][0] + tetraminos[figure][figure_state+1][0][0]][falling[0][1] + tetraminos[figure][figure_state+1][0][1]][0] == 1 && game_field[falling[0][0] + tetraminos[figure][figure_state+1][0][0]][falling[0][1] + tetraminos[figure][figure_state+1][0][1]][1] != falling[0][2]) || (game_field[falling[1][0] + tetraminos[figure][figure_state+1][1][0]][falling[1][1] + tetraminos[figure][figure_state+1][1][1]][0] == 1 && game_field[falling[1][0] + tetraminos[figure][figure_state+1][1][0]][falling[1][1] + tetraminos[figure][figure_state+1][1][1]][1] != falling[1][2]) || (game_field[falling[2][0] + tetraminos[figure][figure_state+1][2][0]][falling[2][1] + tetraminos[figure][figure_state+1][2][1]][0] == 1 && game_field[falling[2][0] + tetraminos[figure][figure_state+1][2][0]][falling[2][1] + tetraminos[figure][figure_state+1][2][1]][1] != falling[2][2]) || (game_field[falling[3][0] + tetraminos[figure][figure_state+1][3][0]][falling[3][1] + tetraminos[figure][figure_state+1][3][1]][0] == 1 && game_field[falling[3][0] + tetraminos[figure][figure_state+1][3][0]][falling[3][1] + tetraminos[figure][figure_state+1][3][1]][1] != falling[3][2])){
                            el = false;
                        }
                    } else {
                        el = false;
                    }
                }

                if (el) {
                    for (int i = 0; i < 4; i++) {
                        game_field[falling[i][0]][falling[i][1]][0] = 0;
                        game_field[falling[i][0]][falling[i][1]][1] = 0;
                    }
    
                    if (figure_state < 3) {
                        figure_state += 1;
                    } else {
                        figure_state = 0;
                    }
    
                    for (int i = 0; i < 4; i++) {
                        game_field[falling[i][0] + tetraminos[figure][figure_state][i][0]][falling[i][1] + tetraminos[figure][figure_state][i][1]][0] = 1;
                        game_field[falling[i][0] + tetraminos[figure][figure_state][i][0]][falling[i][1] + tetraminos[figure][figure_state][i][1]][1] = falling[0][2];
                    }
                }

            } else if (who == 4) {
                for (int i = 0; i < 4; i++) {
                    game_field[falling[i][0]][falling[i][1]][0] = 0;
                    game_field[falling[i][0]][falling[i][1]][1] = 0;
                }
                int plus = 1;
                while (falling[0][0]+(plus+1) < 20 && falling[1][0]+(plus+1) < 20 && falling[2][0]+(plus+1) < 20 && falling[3][0]+(plus+1) < 20) {
                    if ((game_field[falling[0][0]+plus][falling[0][1]][0] == 0 || game_field[falling[0][0]+plus][falling[0][1]][1] != 0) && (game_field[falling[1][0]+plus][falling[1][1]][0] == 0 || game_field[falling[1][0]+plus][falling[1][1]][1] != 0) && (game_field[falling[2][0]+plus][falling[2][1]][0] == 0 || game_field[falling[2][0]+plus][falling[2][1]][1] != 0) && (game_field[falling[3][0]+plus][falling[3][1]][0] == 0 || game_field[falling[3][0]+plus][falling[3][1]][1] != 0)) {
                        plus++;
                    } else {
                        plus--;
                        break;
                    }
                }
                for (int i = 0; i < 4; i++) {
                    game_field[falling[i][0]+(plus-1)][falling[i][1]][0] = 1;
                    game_field[falling[i][0]+(plus-1)][falling[i][1]][1] = falling[i][2];
                }
            }
        }
        
        repaint();
    }

    public static void new_game() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 2; k++) {
                    game_field[i][j][k] = 0;
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 2; k++) {
                    next_falling[i][j][k] = 0;
                }
            }
        }

        String to_node = "";
        to_node += score + " очков\n";
        to_node += erased_lines + " стертых линий";
        stack.push(new Node(to_node));

        String out = stack.peek().toString();
        System.out.print(out + "-->");

        score = 0;
        erased_lines = 0;
        figure_state = 0;
        figure = 0;
        multiplier = 0;
        speed_multiplier = 0;
        new_game_win();
    }

    public static void new_game_win() {
        JFrame newGame = new JFrame("New game");
        newGame.setSize(600, 200);
		JPanel btnpanel = new JPanel();
        JPanel newPanel = new New_panel();
        JButton button = new JButton("New Game");
        newGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        btnpanel.add(button);
        newGame.add(newPanel);
        newGame.add(btnpanel, BorderLayout.SOUTH);
        button.addActionListener(e ->
		{
				exit = false;
                newGame.setVisible(false);
		});
        newGame.setVisible(true);
    }
     
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_LEFT) isLeft = true;
        if (e.getKeyCode()==KeyEvent.VK_RIGHT) isRight = true;
        if (e.getKeyCode()==KeyEvent.VK_UP) isUp = true;
        if (e.getKeyCode()==KeyEvent.VK_DOWN) isDown = true;
    }
 
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_LEFT) isLeft = false;
        if (e.getKeyCode()==KeyEvent.VK_RIGHT) isRight = false;
        if (e.getKeyCode()==KeyEvent.VK_UP) isUp = false;
        if (e.getKeyCode()==KeyEvent.VK_DOWN) isDown = false;
    }
 
    @Override
    public void keyTyped(KeyEvent e) {

    }
     

    private class MoveThread extends Thread{
        Tetris tetris;
         
        public MoveThread(Tetris tetris) {
            super("MoveThread");
            this.tetris = tetris;
        }
         
        public void run(){
            while(true) {

                speed_multiplier = score / 600;
                

                if (Integer.parseInt(records_score) < score) {
                    records_score = Integer.toString(score);
                }
                if (Integer.parseInt(records_line) < erased_lines) {
                    records_line = Integer.toString(erased_lines);
                }

                if (!exit) {
                    game(0);
                }
                
                try {
                    Thread.sleep(600 - speed_multiplier * 70);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Draw_graphics extends JPanel{

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            String text_score = String.valueOf(score);
            String text_erased = String.valueOf(erased_lines);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Roboto", Font.PLAIN, 30));
            g.drawString("Очки: " + text_score, 5, 400);
            g.drawString("Стертых линий: " + text_erased, 5, 450);
            g.drawString("Рекорд очков: " + records_score, 700, 400);
            g.drawString("Рекорд линий: " + records_line, 700, 450);

            Graphics2D g2d = (Graphics2D)g;
            int y = 100;
            for (int i = 0; i < 20; i++) {
                int x = 275;
                for (int j = 0; j < 10; j++) {
                    if (game_field[i][j][0] == 1) {
                        if (game_field[i][j][1] == 0) {
                            g2d.setColor(Color.BLACK);
                        } else if (game_field[i][j][1] == 1){
                            g2d.setColor(Color.YELLOW);
                        } else if (game_field[i][j][1] == 2){
                            g2d.setColor(Color.BLUE);
                        } else if (game_field[i][j][1] == 3){
                            g2d.setColor(Color.ORANGE);
                        } else if (game_field[i][j][1] == 4){
                            g2d.setColor(Color.CYAN);
                        } else if (game_field[i][j][1] == 5){
                            g2d.setColor(Color.PINK);
                        } else if (game_field[i][j][1] == 6){
                            g2d.setColor(Color.GREEN);
                        } else if (game_field[i][j][1] == 7){
                            g2d.setColor(Color.RED);
                        }
                        g2d.fillRect(x, y, 40, 40);
                    } else {
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(x, y, 40, 40);
                    }
                    x += 42;
                }
                y += 42;
            }

            y = 10;
            for (int i = 0; i < 2; i++) {
                int x = 275;
                for (int j = 0; j < 10; j++) {
                    if (next_falling[i][j][0] == 1) {
                        if (next_falling[i][j][1] == 0) {
                            g2d.setColor(Color.BLACK);
                        } else if (next_falling[i][j][1] == 1){
                            g2d.setColor(Color.YELLOW);
                        } else if (next_falling[i][j][1] == 2){
                            g2d.setColor(Color.BLUE);
                        } else if (next_falling[i][j][1] == 3){
                            g2d.setColor(Color.ORANGE);
                        } else if (next_falling[i][j][1] == 4){
                            g2d.setColor(Color.CYAN);
                        } else if (next_falling[i][j][1] == 5){
                            g2d.setColor(Color.PINK);
                        } else if (next_falling[i][j][1] == 6){
                            g2d.setColor(Color.GREEN);
                        } else if (next_falling[i][j][1] == 7){
                            g2d.setColor(Color.RED);
                        }
                        g2d.fillRect(x, y, 40, 40);
                    } else {
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(x, y, 40, 40);
                    }
                    x += 42;
                }
                y += 42;
            }

            if (exit) {
                g.setColor(Color.RED);
                g.setFont(new Font("Roboto", Font.BOLD, 55));
                g.drawString("Game Over", 350, 500);
                try {
                    FileWriter fw = new FileWriter("records.txt");
                    fw.write(records_score + "\n");
                    fw.write(records_line);
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new_game();
            }
        }
    }

    static class New_panel extends JPanel{
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.RED);
            g.setFont(new Font("Roboto", Font.BOLD, 55));
            String out = stack.peek().toString();
            int x = 60;
            int y = 70;
            for (String retval : out.split("\n")) {
                g.drawString(retval, x, y);
                y += 50;
            }
            
        }
    }
}