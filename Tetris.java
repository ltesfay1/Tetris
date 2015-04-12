package game_design;
import java.awt.BorderLayout;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.applet.*;
import java.io.File;
import java.io.InputStream;
import java.net.*;

public class Tetris extends JFrame {
	
	private Clip loadClip(String filename) {
		Clip clip = null;
		try{
			File newFile = new File("C:\\workspaceEclipseLuna\\Tetris-Revamp\\Tetris-Revamp\\Tetristheme.wav");
			newFile.getAbsolutePath();
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(newFile);
			clip = AudioSystem.getClip();
			clip.open(audioIn);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return clip;
	}
	JLabel statusbar;
	public Tetris() {
		statusbar = new JLabel(" 0");
		add(statusbar, BorderLayout.SOUTH);
		Board board = new Board(this);
		add(board);
		File newFile = new File("C:\\workspaceEclipseLuna\\Tetris-Revamp\\Tetris-Revamp\\Tetristheme.wav");

		Clip sound = loadClip("C:\\workspaceEclipseLuna\\Tetris-Revamp\\Tetris-Revamp\\Tetristheme.wav");
		
				sound.start();
		
		board.start();
		setSize(400, 800);
		setTitle("Tetris Revamped");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public JLabel getStatusBar() {
		return statusbar;
	}

	public static void main(String[] args) {
		Tetris game = new Tetris();
		game.setLocationRelativeTo(null);
		game.setVisible(true);
	} 
}