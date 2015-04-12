package game_design;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import game_design.Shape.Tetrominoes;

public class Board extends JPanel implements ActionListener {

	final int BoardWidth = 11;
	final int BoardHeight = 22;

	Timer timer;
	boolean isFallingFinished = false;
	boolean isStarted = false;
	boolean isPaused = false;
	int numLinesRemoved = 0;
	int curX = 0;
	int curY = 0;
	int pieceCount = 0, score = 0, count = 0, highestScore = 0;
	JLabel statusbar, highScoreLabel;
	Shape curPiece;
	Tetrominoes[] board;


	public Board(Tetris parent) {
		setFocusable(true);
		curPiece = new Shape();
		timer = new Timer(400, this);
		timer.start(); 
		highScoreLabel = parent.getHighScoreBar();
		highestScore = 0;
		statusbar =  parent.getStatusBar();
		statusbar.setText("Score: 0");
		parent.setScore(score);
		board = new Tetrominoes[BoardWidth * BoardHeight];
		addKeyListener(new TAdapter());
		clearBoard();  
	}
//	public int getScore(){
//		return score;
//	}
	public void actionPerformed(ActionEvent e) {
		if (isFallingFinished) {
			isFallingFinished = false;
			newPiece();
		} else {
			oneLineDown();
		}
	}

	int squareWidth() { return (int) getSize().getWidth() / BoardWidth; }
	int squareHeight() { return (int) getSize().getHeight() / BoardHeight; }
	Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }

	public void start()
	{
		if (isPaused)
			return;

		isStarted = true;
		isFallingFinished = false;
		numLinesRemoved = 0;
		clearBoard();

		newPiece();
		timer.start();
	}

	private void pause() {
		if (!isStarted)
			return;
	
		isPaused = !isPaused;
		if (isPaused) {

			timer.stop();
			statusbar.setText("Paused. Score: " + score);
			
		} else {
			timer.start();
			score = (numLinesRemoved * 10 + pieceCount * 5);
			statusbar.setText(("Score: " + score));
		}
		repaint();
	}

	public void paint(Graphics g)
	{ 
		super.paint(g);

		Dimension size = getSize();
		int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

		for (int i = 0; i < BoardHeight; ++i) {
			for (int j = 0; j < BoardWidth; ++j) {
				Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
				if (shape != Tetrominoes.NoShape)
					drawSquare(g, 0 + j * squareWidth(),
							boardTop + i * squareHeight(), shape);
			}
		}

		if (curPiece.getShape() != Tetrominoes.NoShape) {
			for (int i = 0; i < 3; ++i) {
				int x = curX + curPiece.x(i);
				int y = curY - curPiece.y(i);
				drawSquare(g, 0 + x * squareWidth(),
						boardTop + (BoardHeight - y - 1) * squareHeight(),
						curPiece.getShape());
			}
		}
	}

	private void rotateContentsLeft() {
		for (int row = 0; row < BoardWidth / 2 + BoardWidth % 2; row ++) {
			for (int col = 0; col < BoardWidth / 2 + BoardWidth % 2; col++) {
				Tetrominoes temp = board[(row) * BoardWidth + col];
				board[( row) * BoardWidth + col] = 
						board[( row) * BoardWidth + col + BoardWidth * (BoardWidth - col - 1) - col - row * (BoardWidth - 1)];
				board[(row) * BoardWidth + col + BoardWidth * (BoardWidth - col - 1) - col - row * (BoardWidth - 1)] = temp;
			}
		}

	}

	private void rotateContentsRight() {
		for (int row = 0; row < BoardWidth / 2 + BoardWidth % 2; row ++) {
			for (int col = 0; col < BoardWidth / 2 + BoardWidth % 2; col++) {
				Tetrominoes temp = board[( col) * BoardWidth + row];
				board[( col) * BoardWidth + row] = 
						board[( row) * BoardWidth + col + BoardWidth * (BoardWidth - row - 1) - row - col * (BoardWidth - 1)];
				board[row * BoardWidth + col + BoardWidth * (col - row)+ BoardWidth - row - col - 1 ] = temp;
			}
		}

	}

	private void groundPieces() {
		for (int i = 0; i < board.length; i++) {
			curPiece.setShape(board[i]);
			if (shapeAt((int)(i / BoardWidth), i % BoardWidth) != 
					Tetrominoes.NoShape) {
				dropDown();
			}
		}
	}

	private void detDirecContentRotate() {

		int randNum = (int)(Math.random() * 9);
		System.out.println(randNum);

		if (randNum < 3) {
			rotateContentsLeft();
		} else if (randNum >= 6) {
			rotateContentsRight();
		}
	}

	private void dropDown() {
		int newY = curY;
		while (newY > 0) {
			if (!tryMove(curPiece, curX, newY - 1))
				break;
			--newY;
		}
		pieceDropped();
	}

	private void oneLineDown() {
		if (!tryMove(curPiece, curX, curY - 1))
			pieceDropped();
	}


	private void clearBoard() {
		for (int i = 0; i < BoardHeight * BoardWidth; ++i)
			board[i] = Tetrominoes.NoShape;
	}

	private void pieceDropped() {
		for (int i = 0; i < 3; ++i) {
			int x = curX + curPiece.x(i);
			int y = curY - curPiece.y(i);
			board[(y * BoardWidth) + x] = curPiece.getShape();
		}
		removeFullLines();

		if (!isFallingFinished)
			newPiece();
	}

	private void newPiece() {
		curPiece.setRandomShape();
		curX = BoardWidth / 2 + 1;
		curY = BoardHeight - 1 + curPiece.minY();
		pieceCount++;
		detDirecContentRotate();
		score += 5;
		if (!tryMove(curPiece, curX, curY)) {
			curPiece.setShape(Tetrominoes.NoShape);
			timer.stop();
			isStarted = false;
			score -= 5;
			count = -1;
			highScoreLabel.setText("Highest Score: " + calcHighScore());
			ImageIcon image = new ImageIcon("http:///stagetetris.globalhost.com//wp-content//uploads//2014/04//Master_TetrisLogo_R_700x4562.png");
			highScoreLabel.setIcon(image);
			add(highScoreLabel);
			
			
			statusbar.setText("Game Over. Score: " + score);
		}
	}
	public int calcHighScore(){
     
		if (score >= highestScore) {
			highestScore = score;
			} 
		return highestScore;
	}
	public int getScore(){
		return score;
	}

	
	private boolean tryMove(Shape newPiece, int newX, int newY) {
		for (int i = 0; i < 3; ++i) {
			int x = newX + newPiece.x(i);
			int y = newY - newPiece.y(i);
			if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
				return false;
			if (shapeAt(x, y) != Tetrominoes.NoShape)
				return false;
		}

		curPiece = newPiece;
		curX = newX;
		curY = newY;
		repaint();
		return true;
	}

	private void removeFullLines() {
		int numFullLines = 0;

		for (int i = BoardHeight - 1; i >= 0; --i) {
			boolean lineIsFull = true;

			for (int j = 0; j < BoardWidth; ++j) {
				if (shapeAt(j, i) == Tetrominoes.NoShape) {
					lineIsFull = false;
					break;
				}
			}

			if (lineIsFull) {
				++numFullLines;
				for (int k = i; k < BoardHeight - 1; ++k) {
					for (int j = 0; j < BoardWidth; ++j)
						board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
				}
			}
		}

		numLinesRemoved += numFullLines;
		score += numFullLines * 10;
		statusbar.setText(("Score: " + score));
		if (numFullLines > 0) {
			isFallingFinished = true;
			curPiece.setShape(Tetrominoes.NoShape);
			repaint();
		}
	}

	private void removeEmptyLines() {
		int numEmptyLines = 0;

		for (int i = BoardHeight - 1; i >= 0; --i) {
			boolean lineIsEmpty = true;

			for (int j = 0; j < BoardWidth; ++j) {
				if (shapeAt(j, i) != Tetrominoes.NoShape) {
					lineIsEmpty = false;
					break;
				}
			}

			if (lineIsEmpty) {
				++numEmptyLines;
				for (int k = i; k < BoardHeight - 1; ++k) {
					for (int j = 0; j < BoardWidth; ++j)
						board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
				}
			}
		}

		if (numEmptyLines > 0) {
			numLinesRemoved += numEmptyLines;
			isFallingFinished = true;
			curPiece.setShape(Tetrominoes.NoShape);
			repaint();
		}
	}

	private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
		Color colors[] = { new Color(0, 0, 0), new Color(102, 204, 254), 
				new Color(102, 204, 102), new Color(255, 165, 0), 
				new Color(123, 104, 238) };

		Color color = colors[shape.ordinal()];

		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

		g.setColor(color.brighter());
		g.drawLine(x, y + squareHeight() - 1, x, y);
		g.drawLine(x, y, x + squareWidth() - 1, y);

		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1,
				x + squareWidth() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
				x + squareWidth() - 1, y + 1);
	}

	class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {

			if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {  
				return;
			}

			int keycode = e.getKeyCode();

			if (keycode == 'p' || keycode == 'P') {
				pause();
				return;
			}

			if (isPaused)
				return;

			switch (keycode) {
			case KeyEvent.VK_LEFT:
				tryMove(curPiece, curX - 1, curY);
				break;
			case KeyEvent.VK_RIGHT:
				tryMove(curPiece, curX + 1, curY);
				break;
			case KeyEvent.VK_DOWN:
				tryMove(curPiece.rotateRight(), curX, curY);
				break;
			case KeyEvent.VK_UP:
				tryMove(curPiece.rotateLeft(), curX, curY);
				break;
			case KeyEvent.VK_SPACE:
				dropDown();
				break;
			case 'd':
				oneLineDown();
				break;
			case 'D':
				oneLineDown();
				break;
			}
		}
	}
}