package game_design;
import java.awt.BorderLayout;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.io.File;


public class Tetris extends JFrame {


	JLabel statusbar, highScoreLabel;
	int score = 0, highestScore = 0;
	public Tetris() {
		statusbar = new JLabel(" 0");
		highScoreLabel = new JLabel(" <-: move left, ->: move right, up or down: rotate, p: pause game");
		add(statusbar, BorderLayout.SOUTH);
		add(highScoreLabel, BorderLayout.NORTH);
		Board board = new Board(this);
		add(board);

		Clip sound = loadClip("C:\\workspaceEclipseLuna\\Tetris-Revamp\\Tetris-Revamp\\Tetristheme.wav");

		sound.loop(Clip.LOOP_CONTINUOUSLY);
		board.start();
		setSize(400, 800);
		setTitle("Tetris Revamped");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	public void setScore(int score1){
		score = score1;
	}
	private Clip loadClip(String filename) {
		Clip clip = null;
		try{
			File newFile = new File(filename);
			newFile.getAbsolutePath();
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(newFile);
			clip = AudioSystem.getClip();
			clip.open(audioIn);

		}catch(Exception e){
			e.printStackTrace();
		}
		return clip;
	}
	public JLabel getStatusBar() {
		return statusbar;
	}
	public JLabel getHighScoreBar() {
		return highScoreLabel;
	}



	public static void main(String[] args) {
		Tetris game = new Tetris();
		game.setLocationRelativeTo(null);
		game.setVisible(true);
	} 
}