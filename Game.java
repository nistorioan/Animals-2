/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wat;

/**
 *
 * @author PR19
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import sun.audio.*;



public class Game extends JFrame{

    private JPanel panel;
    private JLabel[] holes = new JLabel[12];
    private int[] board = new int[12];

    private int score = 0;
    private int timeLeft = 60;
    private int highscore = 0;
    private int ran=0;

    private JLabel lblScore;
    private JLabel lblTimeLeft;
    private JLabel lblTime;
    private JLabel lblTimeBonus;
    private JLabel lblBonus;
    private JLabel lblHighscore;


    private JButton btnBack,btnPause,btnRestart;
    private Timer timer;
    
    private void saveHighscore(){
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/highscore.txt", false)); //append - set to false
            bw.write("" + highscore);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error while saving highscore", JOptionPane.ERROR_MESSAGE);
        }
    }   
    private void loadHighscore(){
    BufferedReader br = null;
        String line = "";
        try {
            br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/highscore.txt"));
            line = br.readLine();
            br.close();
        } catch (IOException e) {
            line = "";
        }

        if(line != ""){
            highscore = Integer.parseInt(line);
            lblHighscore.setText("Highscore: " + highscore);
        }
    }
    private void gameOver(){
        btnBack.setEnabled(true);
        if(score > highscore){
            highscore = score;
            lblHighscore.setText("Highscore: " + highscore);
            JOptionPane.showMessageDialog(this, "Your final score is: " + score, "You beat the high score!", JOptionPane.INFORMATION_MESSAGE);
            
        }else{
            JOptionPane.showMessageDialog(this, "Your final score is: " + score, "Game Over!", JOptionPane.INFORMATION_MESSAGE);
        }
        
        score = 0;
        timeLeft =60;
        lblScore.setText("Score: 0");
        lblTimeLeft.setText("60");
        clearBoard();
        saveHighscore();
    }

    private void pressedButton(int id){
        int val = board[id];
        
        //if val is 1 = terror
        //if val is 0 = empty hole
       
        if(val==1){ 
            GunShell(); 
            GunReload(); 
            score+=10;
            timeLeft++;
            lblBonus.setText("+1");
        }
        else{ 
            GunShell(); 
            GunReload();
            score-=10;
            lblBonus.setText("0");        
        }      
        lblScore.setText("Score: " + score); //update the score
        clearBoard();  
    }

    private void initEvents(){
        for(int i = 0; i < holes.length; i++){
            holes[i].addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e){
                    GunShot();
                    JLabel lbl = (JLabel)e.getSource();
                    int id = Integer.parseInt(lbl.getName());
                    pressedButton(id);
                }
            });
        }

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnBack.setEnabled(false);
                int YesOrNo =JOptionPane.showConfirmDialog(null,"Do you really want to quit the Game?","EXIT",JOptionPane.YES_NO_OPTION);
                if(YesOrNo == 0){
                    java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                    new Menu().setVisible(true);
                    setVisible(false);
                    }   
                     });
                }
                    else{
                        btnBack.setEnabled(true);
                    }
            }
        });
        
        btnPause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnPause.setEnabled(false);
                timer.stop();
                JOptionPane.showMessageDialog(null, "Do you want to resume Game?");
                timer.start();
                btnPause.setEnabled(true);
            }
        });
        
        btnRestart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnRestart.setEnabled(false);
                timer.stop();
                JOptionPane.showMessageDialog(null, "Do you want to Restart Game?");
                score=0;
                lblScore.setText("Score: " + score);
                timeLeft =60;
                lblTimeLeft.setText("60");
                timer.start();
                btnRestart.setEnabled(true);
            }
        });

        
    }

    private void initGUI(){
        
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(timeLeft == 0){
                    lblTimeLeft.setText("" + timeLeft);
                    
                    timer.stop();
                    gameOver();
                }
                lblTimeLeft.setText("" + timeLeft);
                timeLeft--;
                lblBonus.setText("0");
                ran++;
                if(ran==2){
                    clearBoard();
                    genRandterror();
                ran=0;
                
                }
            }
        });
        timer.start();
        
        setTitle("Whack A Terror");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(00, 00, 1370,1020);

        JPanel contentPane = new JPanel();
        contentPane = new JPanel();
        contentPane.setBackground(new Color(0, 0, 0));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);

        JLabel lblTitle = new JLabel("Whack A Terror");
        lblTitle.setForeground(new Color(153, 204, 0));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 30));
        lblTitle.setBounds(0, 30, 400, 30);
        contentPane.add(lblTitle);

        panel = new JPanel();
        panel.setBackground(new Color(0, 102, 0)); //Areana Bgcolor
        panel.setBounds(382, 0, 950, 760);
        panel.setLayout(null);
        panel.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
        loadImage("sight.png").getImage(),
        new Point(0,0),"custom cursor1"));
        contentPane.add(panel);

        holes[0] = new JLabel("");
        holes[0].setName("0");
        holes[0].setBounds(0, 0, 300, 250);
        panel.add(holes[0]);

        holes[1] = new JLabel("");
        holes[1].setName("1");
        holes[1].setBounds(316, 0, 300, 250);
        panel.add(holes[1]);
        
        holes[2] = new JLabel("");
        holes[2].setName("2");
        holes[2].setBounds(632, 0, 300, 250);
        panel.add(holes[2]);
        
        holes[3] = new JLabel("");
        holes[3].setName("3");
        holes[3].setBounds(0, 190, 300, 250);
        panel.add(holes[3]);
        
        holes[4] = new JLabel("");
        holes[4].setName("4");
        holes[4].setBounds(316, 190, 300, 250);
        panel.add(holes[4]);
        
        holes[5] = new JLabel("");
        holes[5].setName("5");
        holes[5].setBounds(632, 190, 300, 250);
        panel.add(holes[5]);
        
        holes[6] = new JLabel("");
        holes[6].setName("6");
        holes[6].setBounds(0, 380, 300, 250);
        panel.add(holes[6]);
        
        holes[7] = new JLabel("");
        holes[7].setName("7");
        holes[7].setBounds(316, 380, 300, 250);
        panel.add(holes[7]);
        
        holes[8] = new JLabel("");
        holes[8].setName("8");
        holes[8].setBounds(632, 380, 300, 250);
        panel.add(holes[8]);
        
        holes[9] = new JLabel("");
        holes[9].setName("9");
        holes[9].setBounds(0, 570, 300, 250);
        panel.add(holes[9]);
        
        holes[10] = new JLabel("");
        holes[10].setName("10");
        holes[10].setBounds(316, 570, 300, 250);
        panel.add(holes[10]);
        
        holes[11] = new JLabel("");
        holes[11].setName("11");
        holes[11].setBounds(632, 570, 300, 250);
        panel.add(holes[11]);
       
        
        lblScore = new JLabel("Score: 0");
        lblScore.setHorizontalAlignment(SwingConstants.TRAILING);
        lblScore.setFont(new Font("Cambria", Font.BOLD, 25));
        lblScore.setForeground(new Color(135, 206, 250));
        lblScore.setBounds(90, 100, 144, 33);
        contentPane.add(lblScore);
        
        lblTime = new JLabel("Time Left:");
        lblTime.setHorizontalAlignment(SwingConstants.CENTER);
        lblTime.setForeground(new Color(255, 0, 0));
        lblTime.setFont(new Font("Cambria Math", Font.BOLD, 25));
        lblTime.setBounds(90, 200, 144, 33);
        contentPane.add(lblTime);
        
        lblTimeBonus = new JLabel("Time Bonus:");
        lblTimeBonus.setHorizontalAlignment(SwingConstants.CENTER);
        lblTimeBonus.setForeground(new Color(0, 102, 0));
        lblTimeBonus.setFont(new Font("Cambria Math", Font.BOLD, 25));
        lblTimeBonus.setBounds(90, 400, 150, 33);
        contentPane.add(lblTimeBonus);

        lblTimeLeft = new JLabel("60");
        lblTimeLeft.setHorizontalAlignment(SwingConstants.CENTER);
        lblTimeLeft.setForeground(new Color(255, 0, 0));
        lblTimeLeft.setFont(new Font("Cambria Math", Font.BOLD, 25));
        lblTimeLeft.setBounds(190, 200, 144, 33);
        contentPane.add(lblTimeLeft);

        lblHighscore = new JLabel("Highscore: 0");
        lblHighscore.setHorizontalAlignment(SwingConstants.TRAILING);
        lblHighscore.setForeground(new Color(255, 255, 0));
        lblHighscore.setFont(new Font("Cambria", Font.BOLD,25));
        lblHighscore.setBounds(80, 300, 200, 33);
        contentPane.add(lblHighscore);

        btnBack = new JButton("Exit");
        btnBack.setBackground(Color.white);
        btnBack.setBounds(120, 600, 110, 33);
        btnBack.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        contentPane.add(btnBack);
        
        btnRestart = new JButton("Restart");
        btnRestart.setBackground(Color.white);
        btnRestart.setBounds(120, 700, 110, 33);
        btnRestart.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        contentPane.add(btnRestart);
        
    
        setContentPane(contentPane);
    }
    public void testb(){
        btnPause = new JButton("Pause");
        btnPause.setBackground(Color.white);
        btnPause.setBounds(120, 500, 110, 33);
        btnPause.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        add(btnPause);
    }
    private void clearBoard(){
        for(int i = 0; i < 12; i++){
            holes[i].setIcon(loadImage("holes-0.png"));
            board[i] = 0;
        }
    }

   private void genRandterror(){
        Random rnd = new Random(System.currentTimeMillis()); //random appear terrorist
        int moleID = rnd.nextInt(12);
        board[moleID] = 1;
        holes[moleID].setIcon(terrorImage("terror.png"));
    }
    
    private ImageIcon loadImage(String path){
        Image image = new ImageIcon(this.getClass().getResource(path)).getImage();
        Image scaledImage = image.getScaledInstance(250, 250,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
        
    }
    private ImageIcon terrorImage(String path){
        Image image = new ImageIcon(this.getClass().getResource(path)).getImage();
        Image scaledImage = image.getScaledInstance(250, 250,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
        
    }
    public static void GunShell() {
        InputStream in;
        try {
            in = new FileInputStream(new File("src/wat/gunShell.wav"));
            AudioStream audios = new AudioStream(in);
            AudioPlayer.player.start(audios);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }
    public static void GunReload() {
        InputStream in;
        try {
            in = new FileInputStream(new File("src/wat/gunReload.wav"));
            AudioStream audios = new AudioStream(in);
            AudioPlayer.player.start(audios);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }
    
    public static void GunShot() {
        InputStream in;
        try {
            in = new FileInputStream(new File("src/wat/gunshot.wav"));
            AudioStream audios = new AudioStream(in);
            AudioPlayer.player.start(audios);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }
    public static void BgMusic() {
        
    InputStream in;
    try {
        in = new FileInputStream(new File("src/wat/bgmusic.wav"));
        AudioStream audios = new AudioStream(in);
        AudioPlayer.player.start(audios);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e);

    }

}
    public void bonus(){
        lblBonus = new JLabel("0");
        lblBonus.setHorizontalAlignment(SwingConstants.CENTER);
        lblBonus.setForeground(new Color(0, 102, 0));
        lblBonus.setFont(new Font("Cambria Math", Font.BOLD, 25));
        lblBonus.setBounds(190, 400, 144, 33);
        add(lblBonus); 
    }
    
    public void close(){

WindowEvent winClosingEvent = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(winClosingEvent);

        }
    public Game() {
        BgMusic();
        initGUI();
        testb();
        clearBoard();
        bonus();
        genRandterror();
        initEvents();
        loadHighscore();
        //---RGG-----
    }
}


