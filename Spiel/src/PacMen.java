import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import java.util.TimerTask;

import javax.swing.*;
public class PacMen extends JPanel implements ActionListener,KeyListener {
        
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Aufgabe wurde ausgeführt!");
            }
        };
        class Block {
            int x;
            int y;
            int width;
            int height;
            Image image;

            int startX;
            int startY;

            char direction = 'U';
            int velocityX = 0;
            int velocityY = 0;
        

            Block(Image image, int x, int y, int width, int height) {
                this.image = image;
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
                this.startX = x;
                this.startY = y;
            }

            void updateDirection (char direction){
                char prevDirection = this.direction;
                this.direction = direction;
                updateVelocity();
                this.x += this.velocityX;
                this.y += this.velocityY;
                for (Block wall: walls){
                    if (collision(this, wall)){
                        this.x -= this.velocityX;
                        this.y -= this.velocityY;
                        this.direction = prevDirection;
                        updateVelocity();
                        break;
                    }
                }
            }


        
            void updateVelocity() {
            if (this.direction == 'U'){
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            }
            else if (this.direction == 'D'){
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            else if (this.direction == 'L'){
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            }
            else if (this.direction == 'R'){
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
            }

            void reset(){
                this.x = this.startX;
                this.y = this.startY;
                this.direction = 'U';
                updateVelocity();
            }

        }
    

        private int rowCount = 21;
        private int columnCount = 19;
        private int tileSize = 32;
        private int boardWidth = columnCount * tileSize;
        private int boardHeight = rowCount * tileSize;

        private Image wallImage;
        private Image blueGhosImage;
        private Image orangeGhostImage;
        private Image pinkGhostImage;
        private Image redGhostImage;

        private Image pacmenUpImage;
        private Image pacmenDownImage;
        private Image pacmenLeftImage;
        private Image pacmenRightImage;

        private Image cherryImage;
        private Image scarryghostImage;

        HashSet<Block> walls;
        HashSet<Block> foods;
        HashSet<Block> ghosts;
        HashSet<Block> cherrys;
        Block pacmen;

        Timer gameLoop;
        char[] directions = {'U', 'D', 'L', 'R'};
        Random random = new Random();
        int score = 0;
        int highscore = 0;
        int lives = 3;
        boolean gameOver = false;
        String live ="♡";
        boolean scarry = false;
        int wins = 0;



        //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X     c           X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O  c    bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P      X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X           c     X",
        "XXXXXXXXXXXXXXXXXXX" 
    };
private String[] tileMapTwo = {
    "XXXXXXXXXXXXXXXXXXX",
    "X XX XXX X XXX XX X",
    "X    X       X    X",
    "X           c     X",
    "X XX X XXXXX X XX X",
    "X        X        X",
    "XXXX X       X XXXX",
    "OOOX X       X XOOO",
    "XXXX X XXrXX X XXXX",
    "O  c    bpo       O",
    "XXXX X XXXXX X XXXX",
    "OOOX X       X XOOO",
    "X XX XXX X XXX XX X",
    "X    X   X   X    X",
    "X XXXXXX X XXXXXX X",
    "X  X     P     X  X",
    "XX X X XXXXX X X XX",
    "X XX X XXXXX X XX X",
    "X     X           X",
    "X           c     X",
    "XXXXXXXXXXXXXXXXXXX"
};



    PacMen() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhosImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmenUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmenDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmenLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmenRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        cherryImage = new ImageIcon(getClass().getResource("./cherry.png")).getImage();
        scarryghostImage = new ImageIcon(getClass().getResource("./scaredGhost.png")).getImage();

        loadmap();
        for (Block ghost: ghosts){
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(50, this);
        gameLoop.start();
        System.out.println(walls.size());
        System.out.println(foods.size());
        System.out.println(ghosts.size());
        System.out.println(cherrys.size());
    }
    public void loadmap(){
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        cherrys = new HashSet<Block>();
        
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                if (wins >= 1){
                    tileMap = tileMapTwo;
                }
                char tileMapChar = tileMap[r].charAt(c);

                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'X'){
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b'){
                    Block ghost = new Block(blueGhosImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o'){
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p'){
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r'){
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P'){
                    pacmen = new Block(pacmenRightImage, x, y, tileSize, tileSize);
                }
                else if (tileMapChar == 'c'){
                    Block cherry = new Block(cherryImage, x, y, tileSize, tileSize);
                    cherrys.add(cherry);
                }
                else if (tileMapChar == ' '){
                    Block food = new Block(null, x+14, y+14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    javax.swing.Timer scaredTimer = new javax.swing.Timer(10000, new java.awt.event.ActionListener() {
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        scarry = false;
        ((javax.swing.Timer)e.getSource()).stop();
    }
});
    public void scare(){
        if (scaredTimer.isRunning()){
            scaredTimer.restart();
        }
        else {
        scaredTimer.setRepeats(false);
        scaredTimer.start();
        }

        scarry = true;
    }

    public void draw(Graphics g){
        g.drawImage(pacmen.image, pacmen.x, pacmen.y, pacmen.width, pacmen.height, null);

        for ( Block ghost: ghosts){

                if (scarry == true){
                g.drawImage(scarryghostImage, ghost.x, ghost.y, ghost.width, ghost.height, null);
                
            }
            else {
                g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
            }
            
            
        }
        for ( Block wall: walls){
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
        for ( Block cherry: cherrys){
            g.drawImage(cherry.image, cherry.x, cherry.y, cherry.width, cherry.height, null);
        }
        g.setColor(Color.WHITE);
         for ( Block food: foods){
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver){
            g.setColor(Color.RED);
            g.drawString("Game Over. -- Score :" + String.valueOf(score), tileSize*1, tileSize*2);
                       g.drawString("Highscore : "+highscore, tileSize*1, tileSize*3);
        }
        else {
           g.drawString ("Score: " + String.valueOf(score), tileSize*1, tileSize*2);
           g.drawString("Highscore : "+highscore, tileSize*1, tileSize*3);
           g.drawString (live.repeat(lives), tileSize*17, tileSize*2);
        }
    }
    
    public void move() {
        pacmen.x += pacmen.velocityX;
        pacmen.y += pacmen.velocityY;

        for (Block wall: walls){
            if (collision(pacmen, wall)){
                pacmen.x -= pacmen.velocityX;
                pacmen.y -= pacmen.velocityY;
                break;
            }
        }
        if (pacmen.x > boardWidth && pacmen.y == tileSize*9){
            pacmen.x = 0;
        } else if (pacmen.x < 0 && pacmen.y == tileSize*9) {
            pacmen.x = boardWidth;
        }
        else if (pacmen.x > boardWidth){
            pacmen.updateDirection('L');
        }
        Block cherryEaten = null;
        for (Block cherry: cherrys){
            if (collision(cherry, pacmen)){
                scare();
                score += 30;
                cherryEaten = cherry;
                System.out.println("Scarring the Ghosts!");
                break;
            }
        }
        if (cherryEaten != null){
            cherrys.remove(cherryEaten);
        }
        java.util.List<Block> ghostsToRemove = new java.util.ArrayList<>();

for (Block ghost : ghosts) {
    if (collision(pacmen, ghost)) {
        if (scarry != true) {
            lives -= 1;
            if (lives <= 0) {
                gameOver = true;
                return;
            }
            resetPositions();
        } else {
            ghostsToRemove.add(ghost);
            score += 100;


            final Block finalGhost = ghost;
            javax.swing.Timer ghostRespawner = new javax.swing.Timer(10000, new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ghosts.add(finalGhost);
                    ((javax.swing.Timer)e.getSource()).stop();
                }
            });
            ghostRespawner.setRepeats(false);
            ghostRespawner.start();
        }
    }
    
    if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
        ghost.updateDirection('U');
    }
    ghost.x += ghost.velocityX;
    ghost.y += ghost.velocityY;

    for (Block wall : walls) {
        if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width > boardWidth) {
            ghost.x -= ghost.velocityX;
            ghost.y -= ghost.velocityY;
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
            break;
        }
    }
}

ghosts.removeAll(ghostsToRemove);

        Block foodEaten = null;
        for (Block food: foods){
            if (collision(pacmen, food)){
                foodEaten = food;
                score += 10;
                if (score > highscore){
                    highscore = score;
                }
            }
        }
        foods.remove(foodEaten);
        if (foods.isEmpty()){
            wins += 1;
            gameOver = true;
            
        }
    }
    public boolean collision(Block a, Block b){
        return (a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y);
    }
    public void resetPositions(){
        pacmen.reset();
        pacmen.velocityX = 0;
        pacmen.velocityY = 0;
        for (Block ghost: ghosts){
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        
    }

    @Override 
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();        
        if (gameOver){
            gameLoop.stop();
        }
    }
        @Override
    public void keyTyped(KeyEvent e) {}
    @Override 
    public void keyReleased(KeyEvent e) {}
     
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver){
            loadmap();
            resetPositions();
            lives = 3;
            score = 0;
            
            gameOver = false;
            gameLoop.start();
        }
        System.out.println ("Key Released: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W){
            pacmen.updateDirection('U');
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S){
            pacmen.updateDirection('D');
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A){
            pacmen.updateDirection('L');
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D){
            pacmen.updateDirection('R');
        }

        else if (e.getKeyCode() == KeyEvent.VK_SPACE){
                gameLoop.stop();
                javax.swing.JOptionPane.showMessageDialog(null, "Press OK to continue");
                gameLoop.start();

        }

        if (pacmen.direction == 'U'){
            pacmen.image = pacmenUpImage;
        }
        else if (pacmen.direction == 'D'){
            pacmen.image = pacmenDownImage;
        }
        else if (pacmen.direction == 'L'){
            pacmen.image = pacmenLeftImage;
        }
        else if (pacmen.direction == 'R'){
            pacmen.image = pacmenRightImage;
        }
    }
}
