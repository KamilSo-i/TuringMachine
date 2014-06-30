package turing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.lang.Runnable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Tape extends JPanel implements Runnable{
	final int START_MARKER = 4; //znak startowy na tasmie
	static volatile int q = 1; //szybkosc animacji
	static char[] sign = {'X', 'X', 'D', 'X', 'M', 'D', 'M','X', 'M', 'X', 'M', '$'}; //ciag na tasmie (+poczatkowy ciag)
	int mid = 670;
	int[] arrowX = {mid-20, mid, mid+20, mid+20, mid-20};
	int[] arrowY = {100, 70, 100, 130, 130};
	int[][] stateTab;
	char[][] symbol;
	char[][] motion;
	JButton buttonPause;
	JButton buttonStart;
	JButton buttonStop;
	JButton buttonLeft;
	JButton buttonRight;
	Thread t;
	Font f = new Font("Monospaced", Font.BOLD, 25);
	boolean sideR = true;
	boolean shouldRun = true;
	boolean toRight = true;
	boolean runProcedure = false;
	boolean stopProcedure = false;	
	volatile boolean isPaused = false;
	int countTo100 = 0;
	int ref = arrowX[1]; //wg tego wskaznika jest przesuwana tasma
	int marker = START_MARKER; //nr aktualnie zmienianego znaku
	public int state; //aktualny stan układu
	int states; //liczba stanów
	int tempState;
	char tempSign;
	char tempMotion;	
	
	String napis = "procedura zakończona";
	boolean isEnded = false;
	int stanOdczytany;
	
	public Tape(){
		setPreferredSize(new Dimension(400, 240));
		setSize(500, 200);
		buttonPause = new JButton(new ImageIcon("player_pause.png"));
		buttonPause.addActionListener((e) -> pause());
		buttonPause.setEnabled(false);
		buttonStart = new JButton(new ImageIcon("player_play.png"));
		buttonStart.addActionListener((e) -> procedure());
		buttonStop =  new JButton(new ImageIcon("player_stop.png"));
		buttonStop.addActionListener((e) -> kill());
		buttonStop.setEnabled(false);
		buttonLeft = new JButton(new ImageIcon("player_rev.png"));
		buttonLeft.addActionListener((e) -> left());
		buttonRight = new JButton(new ImageIcon("player_fwd.png"));
		buttonRight.addActionListener((e) -> right());
		
		t = new Thread(this, "t1");
		t.start();
		setVisible(true);
	}
	
	public void amendTape(String str){
		sign = str.toCharArray();
		repaint();
	}
	
	public JButton getButtonPause(){
		return buttonPause;
	}
	
	public JButton getButtonStart(){
		return buttonStart;
	}
	
	public JButton getButtonStop(){
		return buttonStop;
	}
	
	public JButton getButtonRight(){
		return buttonRight;
	}
	
	public JButton getButtonLeft(){
		return buttonLeft;
	}
	
	synchronized public void pause(){
		isPaused = true;
		buttonStart.setEnabled(true);
		MainWin.start.setEnabled(true);
		//t.suspend();
	}
	
	synchronized public void kill(){
		isPaused = false;
		runProcedure = false;
		shouldRun = false;
		stopProcedure = true;
		buttonStop.setEnabled(false);
		buttonPause.setEnabled(false);
		MainWin.stop.setEnabled(false);
		notify();
	}
	
	synchronized public void right(){
		toRight = true;
		shouldRun = true;
		notify();	
	}
	
	synchronized public void left(){
		toRight = false;
		shouldRun = true;
		notify();
	}
	
	synchronized public void procedure(){	
		if(marker < 0 || marker > sign.length-1) return;
		buttonPause.setEnabled(true);
		buttonStop.setEnabled(true);
		buttonStart.setEnabled(false);
		MainWin.pause.setEnabled(true);
		MainWin.stop.setEnabled(true);
		MainWin.start.setEnabled(false);		
		
		if(isPaused){
			isPaused = false;
			//t.resume();
			notify();
		}else{
			isEnded = false;
			states = MainWin.table.stateQty;
			if(state == -1) state = MainWin.controls.startingState = 0;
			int size = MainWin.table.alphabet.size();
			symbolTable(size);
			stateTable(size);
			motionTable(size);
			shouldRun = true;
			runProcedure = true;
			notify();
		}
	}
	
	void setMid(int m){
		if(sideR){
			if(countTo100 < 50) m += countTo100;
			else m += 100-countTo100;
		}else{
			if(countTo100 < 50) m -= countTo100;
			else m -= 100-countTo100;
		}
		
		mid = m;
		arrowX[0] = m-20;
		ref += m-arrowX[1]; 
		arrowX[1] = m;
		arrowX[2] = m+20;
		arrowX[3] = m+20;
		arrowX[4] = m-20;
	}
	
	private void moveToR() throws InterruptedException{
		sideR = true;
		while(countTo100<100){
			if(isPaused){
				synchronized(this){
					while(isPaused){
						wait();
					}
				}
			}
			if(countTo100 == 50){
				repaint();
				Thread.sleep(10*q);
			}
			if(countTo100<50){
				for(int i=0; i<arrowX.length; i++) 
					arrowX[i] += 1;
			}else{
				for(int i=0; i<arrowX.length; i++) 
					arrowX[i] -= 1;
				ref--;
			}
			countTo100++;
			repaint();
			Thread.sleep(q); //szybkosc animacji
		}
		++marker;
		if(marker > sign.length) kill();
	}
	
	private void moveToL() throws InterruptedException{
		sideR = false;
		while(countTo100<100){
			if(isPaused){
				synchronized(this){
					while(isPaused){
						wait();
					}
				}
			}
			if(countTo100 == 50){
				repaint();
				Thread.sleep(10*q);
			}
			if(countTo100<50){
				for(int i=0; i<arrowX.length; i++) arrowX[i] -= 1;
			}else{
				for(int i=0; i<arrowX.length; i++) arrowX[i] += 1;
				ref++;
			}
			countTo100++;
			repaint();
			Thread.sleep(q); //szybkosc animancji
		}
		--marker;
		if(marker < 0) kill();
	}
	
	public void run(){
		try{
			while(true){
				synchronized(this){
					while(!shouldRun) wait();
				}
				if(runProcedure){
					tempSign = sign[marker]; //nowy znak
					procedureStarted();
					while(true){
						if(stopProcedure || state == -1){
							napis = "program zakończony" + (state == -1 ? ": stan spoza zakresu" : "");
							stopProcedure = false;
							break;
						}
						try{
							tempSign = sign[marker];
							sign[marker] = symbol[state][indexOfSymbol(sign[marker])]; //zmiana znaku
							repaint();
							Thread.sleep(200);
							tempState = state;
							stanOdczytany = 
							MainWin.controls.startingState = 
							state = stateTab[tempState][indexOfSymbol(tempSign)]; //zmiana stanu 
							
							if(state > states && state < 0){ //jeżeli nowy stan nie miesci sie w zakeresie - przerwij
								//procedureEnded();
								break;
							}
							
							tempMotion = motion[tempState][indexOfSymbol(tempSign)];
							MainWin.controls.setStateDisplay();
							switch(tempMotion){
								case 'L':
									moveToL();
									break;
								case 'R':
									moveToR();
									break;
								case ' ':
									break;	
							}

							if((stopProcedure) && countTo100 >= 100){ //aby program nie czekał przy przerywaniu
								countTo100 = 0;
								//napis = "";
								stopProcedure = false;
								//procedureEnded();
								break;
							}

							if(countTo100 >= 100 || tempMotion == ' '){
								countTo100 = 0;
								repaint();
								Thread.sleep(60*q);
							}
						}catch(ArrayIndexOutOfBoundsException e){
							napis = "program zakończony: alfabet nie zawiera znaku \"" + tempSign +"\"";
							stopProcedure = true;
							break;
						}
					}	
					procedureEnded();
				}else{
					if(toRight){
						this.moveToR();
					}else{
						this.moveToL();
					}
					if(countTo100 >= 100){
						countTo100 = 0;
						repaint();
						Thread.sleep(30);  //////////////// 80*q
					}
				}
					runProcedure = false;
					shouldRun = false;
			}
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		setFont(f);
		g.setColor(new Color(Color.CYAN.getRed(), Color.CYAN.getGreen(), Color.CYAN.getBlue(), 100));
		g.fillRect(0, 30, this.getWidth(), 30);
		g.setColor(Color.BLACK);
		g.drawLine(0, 75, this.getWidth(), 75);
		g.drawLine(0, 15, this.getWidth(), 15);
		for(int i=ref%50; i<this.getWidth()+100; i+=50){
			g.drawLine(i-25, arrowY[1]-55, i-25,  arrowY[1]+5);
		}
		for(int i=0; i<sign.length; i++){ //wyswietla ciag na tasmie
			g.drawString(""+sign[i], ref-50*(START_MARKER-i)-6, arrowY[1]-17);
		}
		g.setColor(Color.RED);
		g.fillRect(arrowX[1]-29, 12, 5, 66);
		g.fillRect(arrowX[1]+25, 12, 5, 66);
		g.fillRect(arrowX[1]-29, 11, 59, 5);
		g.fillRect(arrowX[1]-29, 75, 59, 5);
		if(isEnded) g.drawString(napis, 10, 175);
		g.setColor(Color.BLACK);
		//g.drawString("marker: "+marker, mid+300, 200);
		g.drawString("S0: "+tempSign, 10, 225);
		g.drawString("q0: "+stanOdczytany, 150, 225);
		g.drawString("q1: "+state, 290, 225);
		g.drawString("L/R: "+tempMotion, 430, 225);
		g.fillPolygon(arrowX, arrowY, 5); //strzalka
		/*g.drawString("startingState: "+MainWin.controls.startingState, 550, 205);
		g.drawString("table.stateQty: "+MainWin.table.stateQty, 550, 180);
		g.drawString("tape.state: "+MainWin.tape.state, 550, 230);*/
		g.setColor(Color.WHITE);
		g.drawString(""+state, (state < 10 && state >= 0 ? arrowX[1]-7 : arrowX[1]-14), 115);
	}
	
	void procedureStarted(){
		q = 10;
		isEnded = false;
		buttonStart.setEnabled(false);
		buttonLeft.setEnabled(false);
		buttonRight.setEnabled(false);
		MainWin.start.setEnabled(false);
		MainWin.moveLeft.setEnabled(false);
		MainWin.moveRight.setEnabled(false);
		MainWin.table.tab.setEnabled(false);
		MainWin.alphabetField.setEnabled(false);
		MainWin.tapeField.setEnabled(false);
		MainWin.stateAmoutField.setEnabled(false);
		MainWin.tableReor.setEnabled(false);
		MainWin.tapeReor.setEnabled(false);
		Controls.scrollState.setEnabled(false);
		Controls.scrollSpeed.setEnabled(true);
		state = MainWin.controls.startingState;
		MainWin.controls.repaint();
	}
	
	void procedureEnded(){
		MainWin.tape.state = MainWin.controls.startingState = 0; //ustawia stan startowy na 0
		MainWin.controls.setStateDisplay();                      //
		q = 1;
		isEnded = true;
		buttonPause.setEnabled(false);
		buttonStart.setEnabled(true);
		buttonStop.setEnabled(false);
		buttonLeft.setEnabled(true);
		buttonRight.setEnabled(true);
		MainWin.pause.setEnabled(false);
		MainWin.start.setEnabled(true);
		MainWin.stop.setEnabled(false);
		MainWin.moveLeft.setEnabled(true);
		MainWin.moveRight.setEnabled(true);
		MainWin.table.tab.setEnabled(true);
		MainWin.alphabetField.setEnabled(true);
		MainWin.tapeField.setEnabled(true);
		MainWin.stateAmoutField.setEnabled(true);
		MainWin.tableReor.setEnabled(true);
		MainWin.tapeReor.setEnabled(true);
		Controls.scrollState.setEnabled(true);
		Controls.scrollSpeed.setEnabled(false);
		MainWin.controls.repaint();
		stopProcedure = false;		
		repaint(); 
	}
	
	void symbolTable(int size){
		String str;
		symbol = new char[states][size];
		for(int i=0; i<states; i++){
			for(int j=0; j<size; j++){
				str = MainWin.table.tab.getValueAt(i, j*3).toString();
				symbol[i][j] = str.length() > 0 ? str.charAt(0) : ' ';
			}
		}
	}
	
	void stateTable(int size){
		int value;
		stateTab = new int[states][size];
		for(int i=0; i<states; i++){
			for(int j=0; j<size; j++){
				try{
					value = (int) MainWin.table.tab.getValueAt(i, j*3+1);
					stateTab[i][j] = value > states || value < 0 ? -1 : value;
				}catch(ClassCastException e){
					stateTab[i][j] = -1;
				}
			}
		}
	}
	
	void motionTable(int size){
		char ch;
		motion = new char[states][size];
		for(int i=0; i<states; i++){
			for(int j=0; j<size; j++){
				motion[i][j] = (char) MainWin.table.tab.getValueAt(i, j*3+2);
			}
		}
	}
	
	int indexOfSymbol(char ch) throws ArrayIndexOutOfBoundsException{
		int ret = MainWin.table.alphabet.indexOf(ch);
		if(ret >= 0) return ret;
		else throw new ArrayIndexOutOfBoundsException();
	}
}
