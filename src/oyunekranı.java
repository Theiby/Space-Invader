
import java.awt.HeadlessException;

import javax.swing.JFrame;


public class oyunekranı extends JFrame {
	
	public oyunekranı(String title) throws HeadlessException{
		super(title);
	}
	
	
	
	public static void main(String[] args) {
		oyunekranı eOyunekranı  = new oyunekranı("Space Invader");
		eOyunekranı.setResizable(false);
		eOyunekranı.setFocusable(false);
		
		eOyunekranı.setSize(800,600);
		
		eOyunekranı.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Oyun oyun = new Oyun();
		
		oyun.requestFocus();
		oyun.addKeyListener(oyun);
		
		oyun.setFocusable(true);
		oyun.setFocusTraversalKeysEnabled(false);
		
		eOyunekranı.add(oyun);
		
		eOyunekranı.setVisible(true);
		
		
		
	}
	
}

