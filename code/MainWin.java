package turing;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MainWin extends JFrame implements ActionListener, AdjustmentListener{
	static JTextField alphabetField;
	static JTextField tapeField;
	static JTextField stateAmoutField;
	static JButton tableReor;
	static JButton tapeReor;
	static TableCopy table;
	static MenuItem newFile;
	static MenuItem open;
	static MenuItem close;
	static MenuItem start;
	static MenuItem pause;
	static MenuItem stop;
	static MenuItem moveRight;
	static MenuItem moveLeft;
	static Controls controls;
	static Tape tape;
	MenuBar mbar;
	
	MainWin(String s){
		super(s);
		setBounds(100, 10, 1030, 660);
		
		setMenuBar(mbar = new MenuBar());
		Menu file = new Menu("Plik");
		
		file.add(newFile = new MenuItem("Nowy..."));
		newFile.setEnabled(false);
		file.add(open = new MenuItem("Otwórz..."));
		open.setEnabled(false);
		file.add(close = new MenuItem("Zakończ program"));
		close.addActionListener(this);
		Menu options = new Menu("Opcje");
		options.add(pause = new MenuItem("Pauza"));
		pause.setEnabled(false);
		pause.addActionListener(this);
		options.add(start = new MenuItem("Start/Kontynuuj"));
		start.addActionListener(this);
		options.add(stop = new MenuItem("Stop"));
		stop.setEnabled(false);
		stop.addActionListener(this);
		options.add(moveRight = new MenuItem("Miejsce startowe w prawo"));
		moveRight.addActionListener(this);
		options.add(moveLeft = new MenuItem("Miejsce startowe w lewo"));
		moveLeft.addActionListener(this);
		
		MenuItem h1;
		MenuItem h2;
		MenuItem h3;
		Menu help = new Menu("Pomoc");
		help.add(h1 = new MenuItem("O maszynie Turinga"));
		help.add(h2 = new MenuItem("O programie"));
		help.add(h3 = new MenuItem("Objaśnienia symboli"));
		h1.addActionListener(this);
		h2.addActionListener(this);
		h3.addActionListener(this);
		
		mbar.add(file);
		mbar.add(options);
		mbar.add(help);
		
	    
		JPanel data = new JPanel();
		JPanel dataN = new JPanel();
		JPanel dataC = new JPanel();
		JPanel dataS = new JPanel();
		table = new TableCopy(1000, 300, "DXM", 3);
		tape = new Tape();
		dataN.add(new JLabel("ALFABET: "), BorderLayout.NORTH); 
		dataN.add(alphabetField = new JTextField("DXM", 51), BorderLayout.NORTH);  //POLE ALFABETU
		dataN.add(new JLabel("        LICZBA STANÓW: "), BorderLayout.NORTH); 
		dataN.add(stateAmoutField = new JTextField("3", 5){
			public void processKeyEvent(KeyEvent ev){
				char c = ev.getKeyChar();
				try{
					if(c > 31 && c < 127){
						Integer.parseInt(c + "");
					}
					super.processKeyEvent(ev);
				}
				catch (NumberFormatException nfe){}
			}
		});
		dataN.add(tableReor = new JButton("DOSTOSUJ TABELĘ"), BorderLayout.NORTH);
		tableReor.addActionListener((e) ->{
			String txt = alphabetField.getText();
			int sq = stateAmoutField.getText().length() == 0 ? 1 : Integer.parseInt(stateAmoutField.getText());
			int width = sq > 7 ? 986 : 1000;
			if(txt.length() != 0){
				dataC.remove(table);
				table = new TableCopy(width, 300, alphabetField.getText(), sq);
				dataC.add(table, BorderLayout.CENTER);
				revalidate();
				repaint();
			}
		});
		
		dataC.add(table, BorderLayout.CENTER);
		dataS.add(new JLabel("WEJŚCIOWY CIĄG ZNAKÓW:  "), BorderLayout.SOUTH); 
		dataS.add(tapeField = new JTextField("XXDXMDMXMXM$",59), BorderLayout.SOUTH); //POLE CIĄGU
		dataS.add(tapeReor = new JButton("DOSTOSUJ TAŚMĘ"), BorderLayout.NORTH);
		tapeReor.addActionListener((e) -> tape.amendTape(tapeField.getText()));
		
		data.add(dataN, BorderLayout.NORTH);
		data.add(dataC, BorderLayout.CENTER);
		data.add(dataS, BorderLayout.SOUTH);
		add(data, BorderLayout.CENTER);
		add(tape, BorderLayout.SOUTH);

		controls = new Controls(tape.getButtonLeft(), 
				tape.getButtonRight(), 
				tape.getButtonStart(),
				tape.getButtonPause(),
				tape.getButtonStop());
		controls.addListeners(this, this);
		add(controls, BorderLayout.NORTH);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void paint(Graphics g){
		super.paint(g);
		tape.setMid(getWidth()/2); 
		tape.repaint();
		table.repaint();
		controls.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent ae){
		String arg = ae.getActionCommand();
		
		switch(arg){
			/*case "Nowy...":
				break;
			case "Otwórz...":
				break;*/
			case "Zakończ program":
				System.exit(0);
				break;
			case "Start/Kontynuuj":
				tape.procedure();
				break;
			case "Pauza":
				tape.pause();
				break;
			case "Stop":
				tape.kill();
				break;
			case "Miejsce startowe w prawo": 
				tape.right();
				break;
			case "Miejsce startowe w lewo":
				tape.left();
				break;
			case "O maszynie Turinga":
				if(Desktop.isDesktopSupported()){
					try{
						Desktop.getDesktop().browse(new URI("http://pl.lmgtfy.com/?q=maszyna+turinga"));
					}catch(IOException | URISyntaxException e){
						e.printStackTrace();
					}
				}
				break;
			case "O programie":
				JOptionPane.showMessageDialog(this, 
						"Turing Machine v. 0.99\nAutor:   Kamil Socha", 
						"O programie", JOptionPane.INFORMATION_MESSAGE);
				break;
			case "Objaśnienia symboli":
				JOptionPane.showMessageDialog(this, 
						"So      odczytany znak"
						+ "\nq0      aktualny stan"
						+ "\nS1      nowy znak"
						+ "\nq1      nowy stan (liczba)"
						+ "\nL/R/-  ruch głowicy w lewo/prawo/brak", 
						"Objaśnienia symboli", JOptionPane.INFORMATION_MESSAGE);
				break;
		}
	}
	
	@Override
	public void adjustmentValueChanged(AdjustmentEvent ae){
		Adjustable object = ae.getAdjustable();
		
		if(object == controls.scrollState){  //zmiana stanu
			if(ae.getAdjustmentType() == AdjustmentEvent.UNIT_INCREMENT){
				if(controls.startingState < table.stateQty-1){
					tape.state = ++controls.startingState;
					controls.setStateDisplay();
					repaint();
				}
			}else{
				if(controls.startingState > 0){
					tape.state = --controls.startingState;
					controls.setStateDisplay();
					repaint();
				}
			}
		}else{  //zmiana szybkosci
			if(ae.getAdjustmentType() == AdjustmentEvent.UNIT_INCREMENT){
				Tape.q += (Tape.q<=50 ? 10 : 0);
				controls.setSpeedDisplay(Tape.q);
				repaint();
			}else{
				Tape.q -= (Tape.q>10 ? 10 : 0);
				controls.setSpeedDisplay(Tape.q);
				repaint();
			}
		}
	}
	
	public static void main(String[] args){
		EventQueue.invokeLater(()->new MainWin("Turing Machine"));
	}
}
