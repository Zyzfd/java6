import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Random;


public class Tetris extends JFrame implements KeyListener, ActionListener{
    private Thread thread;
    private boolean isLeft = false;
    private boolean isRight = false;
    private boolean isUp = false;
    private boolean isDown = false;
    private boolean queryLeft = false;
    private boolean queryRight = false;
    private boolean queryUp = false;
    private boolean queryDown = false;


    public Tetris(int width, int height) {
        this.setSize(width, height);
        this.addKeyListener(this);
        thread = new MoveThread(this);
        thread.start();
        
        Timer timer = new Timer(50, this);
        timer.start();
    }

    public static void main(String[] args) {
        JFrame win = new Tetris(1000, 1000);
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setLayout(new BorderLayout(1, 1));
        win.setVisible(true);
    }

    public int[][][] game_field = new int[20][10][2];
    public int[][] falling = new int[4][3];


    @Override
    public void actionPerformed(ActionEvent e) {
        move();
    } 

    public void game(int who) {
        for (int i = 0; i < 20; i++) {
            int sum = 0;
            for (int j = 0; j < 10; j++) {
                if (game_field[i][j][1] == 0) {
                    sum += game_field[i][j][0];
                }
            }
            if (sum == 10) {
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
                        //game_field[k] = game_field[k-1];
                    }
                }
            }
        }
        repaint();

        boolean no_falling = true;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                falling[i][j] = 0;
            }
        }

        repaint();
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
        repaint();
        if (no_falling) {
            Random random = new Random();
            int i = random.nextInt(7) + 1;
            if (i == 1) {
                game_field[0][4][0] = 1;
                game_field[0][4][1] = 1;
                game_field[0][5][0] = 1;
                game_field[0][5][1] = 1;
                game_field[1][4][0] = 1;
                game_field[1][4][1] = 1;
                game_field[1][5][0] = 1;
                game_field[1][5][1] = 1;
            }
            if (i == 2) {
                game_field[0][3][0] = 1;
                game_field[0][3][1] = 2;
                game_field[1][4][0] = 1;
                game_field[1][4][1] = 2;
                game_field[1][3][0] = 1;
                game_field[1][3][1] = 2;
                game_field[1][5][0] = 1;
                game_field[1][5][1] = 2;
            }
            if (i == 3) {
                game_field[0][5][0] = 1;
                game_field[0][5][1] = 3;
                game_field[1][4][0] = 1;
                game_field[1][4][1] = 3;
                game_field[1][3][0] = 1;
                game_field[1][3][1] = 3;
                game_field[1][5][0] = 1;
                game_field[1][5][1] = 3;
            }
            if (i == 4) {
                game_field[0][3][0] = 1;
                game_field[0][3][1] = 4;
                game_field[0][4][0] = 1;
                game_field[0][4][1] = 4;
                game_field[0][5][0] = 1;
                game_field[0][5][1] = 4;
                game_field[0][6][0] = 1;
                game_field[0][6][1] = 4;
            }
            if (i == 5) {
                game_field[0][4][0] = 1;
                game_field[0][4][1] = 5;
                game_field[1][3][0] = 1;
                game_field[1][3][1] = 5;
                game_field[1][4][0] = 1;
                game_field[1][4][1] = 5;
                game_field[1][5][0] = 1;
                game_field[1][5][1] = 5;
            }
            if (i == 6) {
                game_field[0][4][0] = 1;
                game_field[0][4][1] = 6;
                game_field[0][5][0] = 1;
                game_field[0][5][1] = 6;
                game_field[1][3][0] = 1;
                game_field[1][3][1] = 6;
                game_field[1][4][0] = 1;
                game_field[1][4][1] = 6;
            }
            if (i == 7) {
                game_field[0][3][0] = 1;
                game_field[0][3][1] = 7;
                game_field[0][4][0] = 1;
                game_field[0][4][1] = 7;
                game_field[1][4][0] = 1;
                game_field[1][4][1] = 7;
                game_field[1][5][0] = 1;
                game_field[1][5][1] = 7;
            }
        } else {
            if (who == 0) {
                for (int i = 0; i < 4; i++) {
                    game_field[falling[i][0]][falling[i][1]][0] = 0;
                    game_field[falling[i][0]][falling[i][1]][1] = 0;
                }

                boolean el2 = falling[0][0]+1 < 20 && falling[1][0]+1 < 20 && falling[2][0]+1 < 20 && falling[3][0]+1 < 20;
                boolean el1 = false;
                if (el2) {
                    if (falling[0][2] == 1) {
                        el1 = game_field[falling[2][0]+1][falling[2][1]][0] == 0 && game_field[falling[3][0]+1][falling[3][1]][0] == 0;
                    } else if (falling[0][2] == 2 || falling[0][2] == 3 || falling[0][2] == 5 || falling[0][2] == 6) {
                        el1 = game_field[falling[1][0]+1][falling[1][1]][0] == 0 && game_field[falling[2][0]+1][falling[2][1]][0] == 0 && game_field[falling[3][0]+1][falling[3][1]][0] == 0;
                    } else if (falling[0][2] == 4) {
                        el1 = game_field[falling[0][0]+1][falling[0][1]][0] == 0 && game_field[falling[1][0]+1][falling[1][1]][0] == 0 && game_field[falling[2][0]+1][falling[2][1]][0] == 0 && game_field[falling[3][0]+1][falling[3][1]][0] == 0;
                    } else if (falling[0][2] == 7) {
                        el1 = game_field[falling[0][0]+1][falling[0][1]][0] == 0 && game_field[falling[2][0]+1][falling[2][1]][0] == 0 && game_field[falling[3][0]+1][falling[3][1]][0] == 0;
                    }
                }
                
                for (int i = 0; i < 4; i++) {
                    if (el1 && el2) {
                        game_field[falling[i][0]+1][falling[i][1]][0] = 1;
                        game_field[falling[i][0]+1][falling[i][1]][1] = falling[i][2];
                    } else if (el2) {
                        game_field[falling[i][0]][falling[i][1]][0] = 1;
                        game_field[falling[i][0]][falling[i][1]][1] = 0;
                    } else {
                        game_field[falling[i][0]][falling[i][1]][0] = 1;
                        game_field[falling[i][0]][falling[i][1]][1] = 0;
                    }                    

                }
            } else if (who == 1) {
                if (falling[0][1] - 1 >= 0 && falling[1][1] - 1 >= 0 && falling[2][1] - 0 >= 1 && falling[3][1] - 1 >= 0) {
                    for (int i = 0; i < 4; i++) {
                        game_field[falling[i][0]][falling[i][1]][0] = 0;
                        game_field[falling[i][0]][falling[i][1]][1] = 0;
                    }
                    for (int i = 0; i < 4; i++) {
                        game_field[falling[i][0]][falling[i][1]-1][0] = 1;
                        game_field[falling[i][0]][falling[i][1]-1][1] = falling[i][2];
                    }
                }
            } else if (who == 2) {
                if (falling[0][1] + 1 < 10 && falling[1][1] + 1 < 10 && falling[2][1] + 1 < 10 && falling[3][1] + 1 < 10) {
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

            } else if (who == 4) {
                
            }
        }
        
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        int y = 80;
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
    }
     
    //Listener
     
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
    public void keyTyped(KeyEvent arg0) {}
     
    public void move() {
        if (isLeft) {
            queryLeft = true;
        }
        if (isRight) {
            queryRight = true;
        }
        if (isUp) {
            queryUp = true;
        }
        if (isDown) {
            queryDown = true;
        };
    }

    private class MoveThread extends Thread{
        Tetris tetris;
         
        public MoveThread(Tetris tetris) {
            super("MoveThread");
            this.tetris = tetris;
        }
         
        public void run(){
            while(true) {
                game(0);
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (queryLeft) {
                    game(1);
                    queryLeft = false;
                }
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (queryRight) {
                    game(2);
                    queryRight = false;
                }
                
                
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}