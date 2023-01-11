import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


class MapGen {
    public int map[][];
    public int brickWidth;
    public int brickHeight;
    public MapGen(int row, int col) {
        map = new int[row][col];
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[0].length; j++) {
                map[i][j] = 1;
            }
        }
        brickWidth = 540/col;
        brickHeight = 150/row;
    }
    public void draw(Graphics2D g) {
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[0].length; j++) {
                if(map[i][j] > 0) {
                    g.setColor(Color.black);
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                    BufferedImage img = null;
                    try {
                        img = ImageIO.read(new File("E:\\Java\\BrickBreaker\\src\\brick.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    g.drawImage(img, j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight, null);
                }
            }
        }
    }
    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }
}

class Game extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int totalBricks = 21;
    private Timer timer;
    private int delay = 8;
    private int playerX = 310;
    private int ballposX = 120;
    private int ballposY = 350;
    private int ballXdir = -2;
    private int ballYdir = -3;
    private MapGen map;
    public Game() {
        map = new MapGen(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }
    public void paint(Graphics g) {
        //background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);
        //drawing map
        map.draw((Graphics2D)g);
        //scores
        g.setColor(Color.white);
        g.setFont(new Font("Fira Code", Font.BOLD, 20));
        g.drawString("Score: " + score, 500, 30);
        //the paddle
        BufferedImage img2 = null;
        try {
            img2 = ImageIO.read(new File("E:\\Java\\BrickBreaker\\src\\paddle.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int newWidth2 = 120;
        int newHeight2 = 20;
        Image dimg2 = img2.getScaledInstance(newWidth2, newHeight2, Image.SCALE_SMOOTH);
        g.drawImage(dimg2, playerX, 500, null);
        //the ball
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("E:\\Java\\BrickBreaker\\src\\ball.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int newWidth = 40;
        int newHeight = 40;
        Image dimg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        g.drawImage(dimg, ballposX, ballposY, null);
        if(totalBricks <= 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.red);
            g.setFont(new Font("Fira Code", Font.BOLD, 30));
            g.drawString("You Won!!! :)", 260, 300);
            g.setFont(new Font("Fira Code", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
            try {
                Clip win = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream("Win.wav"));
                win.open(inputStream);
                long winlength = win.getMicrosecondLength();
                win.start();
                Thread.sleep(winlength/1000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if(ballposY > 570) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.red);
            g.setFont(new Font("Fira Code", Font.BOLD, 30));
            g.drawString("Game Over!! :( ", 190, 300);
            g.drawString("Score: "+score, 190, 330);
            g.setFont(new Font("Fira Code", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
            try{
                Clip gameOver = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream("Over.wav"));
                gameOver.open(inputStream);
                long clipLength= gameOver.getMicrosecondLength();
                gameOver.start();
                Thread.sleep(clipLength/1000);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        g.dispose();
    }
    //audio
    AudioClip brickBreak = Applet.newAudioClip(getClass().getResource("break.wav"));
    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if(play) {
            if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 500, 120, 10))) {
                ballYdir = -ballYdir;
            }
            A: for(int i = 0; i < map.map.length; i++) {
                for(int j = 0; j < map.map[0].length; j++) {
                    if(map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;
                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
                        Rectangle brickRect = rect;
                        if(ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;
                            if(ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width) {
                                ballXdir = -ballXdir;
                            } else {
                                ballYdir = -ballYdir;
                            }
                            brickBreak.play();
                            break A;
                            
                        }
                    }
                }
            }
            ballposX += ballXdir;
            ballposY += ballYdir;
            if(ballposX < 0) {
                ballXdir = -ballXdir;
            }
            if(ballposY < 0) {
                ballYdir = -ballYdir;
            }
            if(ballposX > 670) {
                ballXdir = -ballXdir;
            }

        }
        repaint();
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if(playerX >= 600) {
                playerX = 600;
            } else {
                moveRight();
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            if(playerX < 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            if(!play) {
                play = true;
                ballposX = 120;
                ballposY = 350;
                ballXdir = -2;
                ballYdir = -3;
                playerX = 310;
                score = 0;
                totalBricks = 21;
                map = new MapGen(3, 7);
                repaint();
            }
        }
    }
    public void moveRight() {
        play = true;
        playerX += 7;
    }
    public void moveLeft() {
        play = true;
        playerX -= 7;
    }
}

class Main{
    public static void main(String[] args) {
        JFrame obj = new JFrame();
        Game gamePlay = new Game();
        obj.setBounds(10, 10, 700, 600);
        obj.setTitle("Brick Breaker");
        obj.setResizable(false);
        obj.setVisible(true);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        obj.add(gamePlay);
    }
}
        