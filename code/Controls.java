package turing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Scrollbar;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class Controls extends JPanel{
	public int startingState = 0;
	JButton pause;
	JButton play;
	JButton stop;
	JButton right;
	JButton left;
	JTextField startingSign;
	JToolBar belt = new JToolBar();
	JPanel scrolls1 = new JPanel();
	JPanel scrolls2 = new JPanel();
	static Scrollbar scrollState;
	static Scrollbar scrollSpeed;
	private JLabel speed;
	private JLabel state;
	
	public Controls(JButton l, JButton r, JButton str, JButton p, JButton stp){
		setLayout(new FlowLayout(FlowLayout.LEFT));
		scrolls1.setBorder(BorderFactory.createEtchedBorder());
		scrolls2.setBorder(BorderFactory.createEtchedBorder());
		
		pause = p;
		play = str;
		stop = stp;
		right = r;
		left = l;
		pause.setToolTipText("Pauza");
		play.setToolTipText("Start/Wznów");
		stop.setToolTipText("Stop");
		right.setToolTipText("W prawo");
		left.setToolTipText("W lewo");
		
		startingSign = new JTextField(3); 
		
		JPanel panL = new JPanel();
		panL.setLayout(new FlowLayout(FlowLayout.LEFT));
		belt.add(pause);
		belt.add(play);
		belt.add(stop);
		belt.add(left);
		belt.add(right);
		
		JPanel panC = new JPanel();
		panC.setLayout(new GridLayout(3, 1));
		panC.setBorder(BorderFactory.createRaisedBevelBorder());
		panC.setPreferredSize(new Dimension(130, 100));
		Font font = new Font("Monospaced", Font.BOLD, 25);
		JLabel textState = new JLabel(" STAN POCZĄTKOWY:");
		textState.setFont(font);
		scrolls1.add(textState);
		state = new JLabel(" "+startingState);
		state.setForeground(Color.RED);
		state.setFont(font);
		scrolls1.add(state);
		scrolls1.add(scrollState = new Scrollbar(Scrollbar.VERTICAL), BorderLayout.SOUTH);
		
		JPanel panR = new JPanel();
		panR.setLayout(new GridLayout(3, 1));

		panR.setPreferredSize(new Dimension(130, 100));
		JLabel textSpeed = new JLabel(" SZYBKOŚĆ GŁOWICY:");
		textSpeed.setFont(font);
		scrolls2.add(textSpeed);
		speed = new JLabel(" 1");
		speed.setForeground(Color.RED);
		speed.setFont(font);
		scrolls2.add(speed);
		scrolls2.add(scrollSpeed = new Scrollbar(Scrollbar.VERTICAL), BorderLayout.SOUTH);
		scrollSpeed.setEnabled(false);

		add(belt);
		add(scrolls1);
		add(scrolls2);
	}
	
	void addListeners(ActionListener acLis, AdjustmentListener adLis){
		scrollState.addAdjustmentListener(adLis);
		scrollSpeed.addAdjustmentListener(adLis);
	}
	
	public void setSpeedDisplay(int s){
		int txt = (s <= 2 ? 99 : (s == 60 ? 1 : (60-s)/10));
		speed.setText((txt<10 ? " " : "") + (txt == 99 ? "++" : txt));
	}
	
	public void setStateDisplay(){
		state.setText((startingState<10 && startingState>=0 ? " " : "") + startingState);
	}
	
	public void paintComponent(Graphics g){
		setSpeedDisplay(Tape.q);
		super.paintComponent(g);
	}
}
