
import java.awt.Color;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Oyun extends JPanel implements KeyListener, ActionListener {
    private Timer timer;

    private int time;
    private int ammo;
    private int health; // Added health variable

    private BufferedImage image;
    private BufferedImage backgroundImage;
    private BufferedImage alienImage;
    private BufferedImage fireImage;
    private BufferedImage heartImage; // Added heart image

    private ArrayList<Fire> fireList;
    private ArrayList<Alien> alienList;

    private int fireDirY;
    private int spaceShipX;
    private int dirSpaceX;
    private int dirSpaceY;
    private int spaceShipY;
    private int alienDirY;

    public Oyun() {
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        timer = new Timer(10, this);

        timer.start();

        time = 0;
        ammo = 700; 
        health = 4;

        fireList = new ArrayList<>();
        alienList = new ArrayList<>();

        try {
            image = ImageIO.read(new FileImageInputStream(new File(("src/spaceship.png"))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            backgroundImage = ImageIO.read(new File("src/image.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            alienImage = ImageIO.read(new File("src/alien.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fireImage = ImageIO.read(new File("src/fire.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            heartImage = ImageIO.read(new File("src/kalp.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //setBackground(Color.YELLOW);

        fireDirY = 1;
        
        
        spaceShipX = 0;
        dirSpaceX = 20;
        dirSpaceY = 20;
        spaceShipY = 470;
      
        alienDirY = 1;

        createAliens();
        
    }

    private void createAliens() {
        int alienCount = 50;
        int alienWidth = alienImage.getWidth() / 10; //Genişlik
        int alienHeight = alienImage.getHeight() / 10; //Yükseklik

        int initialX = 100;
        int initialY = 100;

        int aliensPerRow = 7; // Number of aliens in each row
        int rows = alienCount / aliensPerRow; // Calculate the number of rows

        int spacingX = 100; // Spacing between aliens in X direction
        int spacingY = 100; // Spacing between aliens in Y direction

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < aliensPerRow; col++) {
                int x = initialX + col * (alienWidth + spacingX);
                int y = initialY + row * (alienHeight + spacingY);

                Alien alien = new Alien(x, y, alienWidth, alienHeight);
                alienList.add(alien);
            }
        }
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        for (Alien alien : alienList) {
            g.drawImage(alienImage, alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight(), this);
        }

        //time += 5;

        g.drawImage(image, spaceShipX, spaceShipY, image.getWidth() / 10, image.getHeight() / 10, this);

        for (Fire fire : fireList) {
            if (fire.getY() < 0) {
                fireList.remove(fire);
            }
        }

        for (Fire fire : fireList) {
            g.drawImage(fireImage, fire.getX(), fire.getY(), fireImage.getWidth() / 30, fireImage.getHeight() / 30, this);
        }

        // Display health as heart images
        int heartSize = 25;
        int heartSpacing = 10;
        int initialHeartX = 10;
        int heartY = 10;

        for (int i = 0; i < health; i++) {
            int heartX = initialHeartX + (heartSize + heartSpacing) * i;
            g.drawImage(heartImage, heartX, heartY, heartSize, heartSize, this);
        }

        // Display ammo count
        String ammoText = "Ammo: " + ammo;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(ammoText, getWidth() - 100, 30);
        time+=5;

        if (isColliding()) {
            timer.stop();
            String message = "Game Over...\n"  + " Time Used: " + time / 1000.0;
            JOptionPane.showMessageDialog(this, message);
            System.exit(0);
        }
        
    }

    private boolean isColliding() {
        for (Fire fire : fireList) {
            for (Alien alien : alienList) {
                Rectangle fireRect = new Rectangle(fire.getX(), fire.getY(), fireImage.getWidth() / 10, fireImage.getHeight() / 10);
                Rectangle alienRect = new Rectangle(alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight());

                if (fireRect.intersects(alienRect)) {
                    fireList.remove(fire);
                    alien.decreaseHealth();

                    if (alien.getHealth() <= 0) {
                        alienList.remove(alien);
                        createNewAlien();
                    }

                    return false;
                }
            }
        }

        // Check collision between spaceship and aliens
        for (Alien alien : alienList) {
        	
            Rectangle spaceshipRect = new Rectangle(spaceShipX, spaceShipY, image.getWidth() / 10, image.getHeight() / 10);
            Rectangle alienRect = new Rectangle(alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight());

            if (spaceshipRect.intersects(alienRect)) {
            	
                health--;
                if (health <= 0) {
                    return true; // Game over if health reaches 0
                }
                alienList.remove(alien);
                createNewAlien();
                break;
            }
        }

        return false;
    }


    private void createNewAlien() {
        int alienWidth = alienImage.getWidth() / 10;
        int alienHeight = alienImage.getHeight() / 10;

        int initialX = 50;
        int initialY = 50;

        int lastAlienX = alienList.get(alienList.size() - 1).getX();
        int lastAlienY = alienList.get(alienList.size() - 1).getY();

        int x = lastAlienX + alienWidth + 50;
        int y = lastAlienY;

        if (x + alienWidth > getWidth()) { //Oyun sınırını aşıyorsa yeni satıra geç.
            x = initialX;
            y += alienHeight + 50;
        }

        if (y < spaceShipY + (image.getHeight() / 10)) { //Yeni uzaylının spawn noktası uzay gemisinden yukarıda olmalı
            y = spaceShipY + (image.getHeight() / 10) + 50;
        }

        Alien alien = new Alien(x, y, alienWidth, alienHeight);
        alienList.add(alien);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int c = e.getKeyCode();
        if (c == KeyEvent.VK_LEFT) {
            if (spaceShipX <= 0) {
                spaceShipX = 0;
            } else {
                spaceShipX -= dirSpaceX;
            }
        } else if (c == KeyEvent.VK_RIGHT) {
            if (spaceShipX >= 720) {
                spaceShipX = 720;
            } else {
                spaceShipX += dirSpaceX;
            }
        } else if (c == KeyEvent.VK_UP) {
            if (spaceShipY <= 0) {
                spaceShipY = 0;
            } else {
                spaceShipY -= dirSpaceY;
            }
        } else if (c == KeyEvent.VK_DOWN) {
            if (spaceShipY >= 490) {
                spaceShipY = 490;
            } else {
                spaceShipY += dirSpaceY;
            }
        } else if (c == KeyEvent.VK_SPACE) {
            if (ammo > 0) { // Check if there is ammo remaining
                int fireX = spaceShipX + (image.getWidth() / 10) / 2;
                int fireY = spaceShipY;
                fireList.add(new Fire(fireX, fireY));
                ammo--;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Fire fire : fireList) {
            fire.setY(fire.getY() - fireDirY);
        }
        /*
        topX += topDirX;

        if (topX >= getWidth()) {
            topDirX = -topDirX;
        }

        if (topX <= 0) {
            topDirX = -topDirX;
        }
		*/
        for (Alien alien : alienList) {
            alien.setY(alien.getY() + alienDirY);

            if (alien.getY() >= getHeight()) {
                int newX = alien.getX();
                alien.setX(newX);
                alien.setY(0);
            }
        }

        repaint();
    }
}



