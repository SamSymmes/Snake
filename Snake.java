//Sam Symmes
//March 5th 2017

//Press Space to start the game, and arrow keys or WASD to control the snake

import java.awt.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class Snake extends Canvas implements KeyEventDispatcher
{
    private JFrame myframe; //Frame to display game
    private Color background = Color.white; 
    private static int windowDim=800; //Window size
    private Canvas myCanvas = null;
    private BufferStrategy strategy = null; //Double buffered for less flashing
    private int direction=1; //1=right 2=up 3=left 4=down direction of your next move
    private int posx=0; //snake's positions
    private int posy=0;
    private int[][] board; //where all the pieces of the snake are on the board
    private int length=1; //snake length
    private int fruitx; //the positions of the fruit
    private int fruity;
    private Random rand=new Random();
    private int score=0;
    private boolean start=false;
    private int aniFrame = 0; //how far through a single animation through a square the snake is (0-74)
    private int[] pastDirs; //past moves the snake made
    private int[][] pastSpots; //past spots the snake was
    private int tempDir = 1; //direction of the current move the snake is on
    private int highScore=0;

    public static void main(String args[]){
        final JFrame myFrame = new JFrame(); //sets up window
        myFrame.setSize(windowDim+20, windowDim+50);

        myFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            }); //lets the window close when you hit the X in the box

        Canvas myCanvas = new Canvas();
        myFrame.getContentPane().add(myCanvas); 

        //BufferedImage emptyImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        //Cursor noCursor = Toolkit.getDefaultToolkit().createCustomCursor(emptyImage, new Point(0, 0), "no cursor");
        //myFrame.getContentPane().setCursor(noCursor);

        Snake game = new Snake(myCanvas);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(game);
        myFrame.setVisible(true);
        game.run();
    }

    public Snake(Canvas init){
        //instantiates variables for the start of the game
        myCanvas = init;
        board=new int[10][10];
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                board[i][j]=0;
            }
        }
        board[0][0]=1;
        newFruit();
    }

    public void run(){
        //more variable instantiation
        myCanvas.createBufferStrategy(2);
        strategy = myCanvas.getBufferStrategy();
        pastDirs=new int[1];
        pastDirs[0]=1;
        pastSpots=new int[1][2];
        pastSpots[0][0]=0;
        pastSpots[0][1]=0;
        repaint();
        tempDir=direction;
        while(true){ //game loop
            while(!start){repaint();} //holds the game up until you hit space to start
            score=0;
            while(true){ //loops throught the snake's movements
                if(pastDirs[pastDirs.length-1]==1 && direction==3){tempDir=pastDirs[pastDirs.length-1];}
                else if(pastDirs[pastDirs.length-1]==2 && direction==4){tempDir=pastDirs[pastDirs.length-1];}
                else if(pastDirs[pastDirs.length-1]==3 && direction==1){tempDir=pastDirs[pastDirs.length-1];}
                else if(pastDirs[pastDirs.length-1]==4 && direction==2){tempDir=pastDirs[pastDirs.length-1];}
                else{tempDir=direction;}
                for(int i=0;i<37;i++){ //does one square movement animation before checking which direction to move in next
                    //try{Thread.sleep(20);}catch(Exception e){}
                    aniFrame=i*2;
                    repaint();
                }
                for(int i=0;i<10;i++){ //decrements the spots on the board, effectively moving the snake
                    for(int j=0;j<10;j++){
                        if(board[i][j]!=0){
                            board[i][j]--;
                        }
                    }
                }
                if(tempDir==1){ //checks the direction it should move
                    posx++;
                }
                else if(tempDir==2){
                    posy--;
                }
                else if(tempDir==3){
                    posx--;
                }
                else{
                    posy++;
                }
                try{ //this try will work if the snake is in the board, an error will be thrown if the snake is trying to leave the board
                    if(board[posx][posy]!=0){ //if the snake hit itself you lose
                        break;
                    }
                    board[posx][posy]=length; //tells the board to draw a snake piece in the new square
                    if(posx==fruitx && posy==fruity){
                        score++;
                        if(score>highScore){
                            highScore++;
                        }
                        length++;
                        newFruit();
                        int[] temp = new int[length]; //holder to lengthen the pastDirs array
                        int[][] temp2 = new int[length][2]; //holder to lengthen the pastSpots array
                        for(int i=0;i<10;i++){ //if you got a fruit this lengthens your snake
                            for(int j=0;j<10;j++){
                                if(board[i][j]!=0){
                                    board[i][j]++;
                                }
                            }
                        }
                        for(int i=0;i<length-1;i++){
                            temp[i]=pastDirs[i];
                            temp2[i][0]=pastSpots[i][0];
                            temp2[i][1]=pastSpots[i][1];
                        }
                        temp2[length-1][0]=posx;
                        temp2[length-1][1]=posy;
                        temp[length-1]=tempDir;
                        pastDirs=temp;
                        pastSpots=temp2;
                    }
                    else{ //adds your new spot and direction to the history and deletes the now unimportant endpoint
                        int[] temp = new int[length];
                        int[][] temp2 = new int[length][2];
                        for(int i=0;i<length-1;i++){
                            temp[i]=pastDirs[i+1];
                            temp2[i][0]=pastSpots[i+1][0];
                            temp2[i][1]=pastSpots[i+1][1];
                        }
                        temp2[length-1][0]=posx;
                        temp2[length-1][1]=posy;
                        temp[length-1]=tempDir;
                        pastSpots=temp2;
                        pastDirs=temp;
                    }
                }
                catch(Exception e){
                    break;
                }
                repaint();
            }
            reset();
        }
    }

    public void paint(Graphics g){
        g.setColor(Color.black);
        //draws scores
        g.setFont(new Font("Times New Roman",Font.PLAIN,28));
        g.drawString("High Score: "+highScore,24,20);
        g.drawString("Score: "+score,400,20);
        //fills in the snake
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                if(board[i][j]!=0){
                    g.fillRect(25+75*i,25+75*j,75,75);
                }
            }
        }
        //draws fruit
        g.setColor(Color.green);
        g.fillOval(40+75*fruitx,40+75*fruity,45,45);
        //draws added head during animation
        g.setColor(Color.black);
        if(tempDir==1){
            g.fillRect(100+75*posx,25+75*posy,aniFrame,75);
        }
        else if(tempDir==2){
            g.fillRect(25+75*posx,25+75*posy-aniFrame,75,aniFrame);
        }
        else if(tempDir==3){
            g.fillRect(25+75*posx-aniFrame,25+75*posy,aniFrame,75);
        }
        else if(tempDir==4){
            g.fillRect(25+75*posx,100+75*posy,75,aniFrame);
        }
        //takes away extra tail during animation
        g.setColor(Color.white);
        try{
            if(pastDirs[1]==1){
                g.fillRect(25+75*pastSpots[0][0],25+75*pastSpots[0][1],aniFrame,75);
            }
            else if(pastDirs[1]==2){
                g.fillRect(25+75*pastSpots[0][0],100+75*pastSpots[0][1]-aniFrame,75,aniFrame);
            }
            else if(pastDirs[1]==3){
                g.fillRect(100+75*pastSpots[0][0]-aniFrame,25+75*pastSpots[0][1],aniFrame,75);
            }
            else if(pastDirs[1]==4){
                g.fillRect(25+75*pastSpots[0][0],25+75*pastSpots[0][1],75,aniFrame);
            }
        }
        catch(Exception e){ 
            if(tempDir==1){
                g.fillRect(25+75*pastSpots[0][0],25+75*pastSpots[0][1],aniFrame,75);
            }
            else if(tempDir==2){
                g.fillRect(25+75*pastSpots[0][0],100+75*pastSpots[0][1]-aniFrame,75,aniFrame);
            }
            else if(tempDir==3){
                g.fillRect(100+75*pastSpots[0][0]-aniFrame,25+75*pastSpots[0][1],aniFrame,75);
            }
            else if(tempDir==4){
                g.fillRect(25+75*pastSpots[0][0],25+75*pastSpots[0][1],75,aniFrame);
            }
        }
        //draws board
        g.setColor(Color.black);
        for(int i=0;i<11;i++){
            g.drawLine(25,25+75*i,775,25+75*i);
            g.drawLine(25+75*i,25,25+75*i,775);
        }
        //draws spots on snake's back
        g.setColor(Color.yellow);
        try{
            for(int i=0;i<length;i++){
                if(pastDirs[i+1]==1){
                    g.fillOval(50+75*pastSpots[i][0]+aniFrame,35+75*pastSpots[i][1],25,25);
                }
                else if(pastDirs[i+1]==2){
                    g.fillOval(35+75*pastSpots[i][0],50+75*pastSpots[i][1]-aniFrame,25,25);
                }
                else if(pastDirs[i+1]==3){
                    g.fillOval(50+75*pastSpots[i][0]-aniFrame,35+75*pastSpots[i][1],25,25);
                }
                else{
                    g.fillOval(65+75*pastSpots[i][0],50+75*pastSpots[i][1]+aniFrame,25,25);
                }
            }
        }
        catch(Exception e){}
    }

    public boolean dispatchKeyEvent(KeyEvent e){
        //records that you pressed buttons and changes the snake's direction
        if(e.paramString().contains("KEY_PRESSED")){
            int button = e.getKeyCode();
            if(button==KeyEvent.VK_LEFT || button==KeyEvent.VK_A){
                if(direction!=1){
                    direction=3;
                }
            }
            if(button==KeyEvent.VK_UP || button==KeyEvent.VK_W){
                if(direction!=4){
                    direction=2;
                }
            }
            if(button==KeyEvent.VK_RIGHT || button==KeyEvent.VK_D){
                if(direction!=3){
                    direction=1;
                }
            }
            if(button==KeyEvent.VK_DOWN || button==KeyEvent.VK_S){
                if(direction!=2){
                    direction=4;
                }
            }
            if(button==KeyEvent.VK_SPACE){
                start=true;
            }
        }
        return true;
    }

    public void repaint(){
        //creates a buffered canvas
        Graphics hiddenCanvas = strategy.getDrawGraphics();

        hiddenCanvas.setColor(background);
        hiddenCanvas.fillRect(0,0,900,900);
        hiddenCanvas.setColor(Color.black);
        paint(hiddenCanvas);

        strategy.show(); //displays the buffered canavas once it's done painting it
        try{
            Thread.sleep(1);
        }
        catch(Exception e){}

    }

    public void newFruit(){//picks a spot for a new fruit
        while(true){
            int newx=rand.nextInt(10);
            int newy=rand.nextInt(10);
            if(board[newx][newy]==0){
                fruitx=newx;
                fruity=newy;
                break;
            }
        }
    }

    public void reset(){ //resets the instance variables for the game
        length=1;
        direction=1;
        tempDir=1;
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                board[i][j]=0;
            }
        }
        board[0][0]=1;
        newFruit();
        posx=0;
        posy=0;
        pastDirs=new int[1];
        pastDirs[0]=1;
        pastSpots=new int[1][2];
        pastSpots[0][0]=0;
        pastSpots[0][1]=0;
        start=false;
        aniFrame=0;
        repaint();
    }
}
