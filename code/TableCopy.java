package turing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;


public class TableCopy extends JPanel{
	int alphQty;
	public int stateQty;
	static boolean notFirstTable = false;
	ArrayList<Character> alphabet = new ArrayList<>();
	JTable tab;
	JScrollPane scroll;
	GroupableTableHeader header;
	DefaultTableModel dm;
	TableColumnModel tcm;
	JList<String> rowHeader;
	JComboBox<Character> choseCombo;
	TableCellEditor choseEditor;
	Object[] tabHead;
	Object[][] tabData = new Object[stateQty][alphabet.size()*3];
	ListModel<String> lm = new AbstractListModel<String>(){
		String headers[] = states();
	    public int getSize() { return headers.length; }
	    public String getElementAt(int index){
	    	return headers[index];
	    }
	};
	
	TableCopy(int x, int y, String str, int qty){
		setSize(x+50, y);
		amendTable(str, qty);
		notFirstTable = true;
	}
	
	public void amendTable(String str, int qty){
		stateQty = qty;
		alphabet = new ArrayList<>();
		for(char ch : str.toCharArray()){
			if(!alphabet.contains(ch) && ch != ' '){
				alphabet.add(ch);
			}
		}
		
		if(notFirstTable){
			tabData = new Object[stateQty][alphabet.size()*3];
		}else{
			Object[][] temp = {
				{'M', 2, 'R', 'D', 0, 'L', 'X', 1, ' '},
				{'X', 0, 'L', 'D', 2, 'R', 'M', 0, ' '},
				{'X', 2, 'L', 'X', 1, 'R', 'X', 2, 'L'}};
			tabData = temp;
		}
		//
		tabHead = new Object[alphabet.size()*3];
		dm = new DefaultTableModel(lm.getSize(), 10);
		dm.setDataVector(tabData, tabHead);
		
		choseCombo = new JComboBox<>();
		choseCombo.addItem('R');
		choseCombo.addItem('L');
		choseCombo.addItem(' ');
		choseEditor = new DefaultCellEditor(choseCombo);
		
		for(int i=0; i<alphabet.size()*3; i++){
			int r = i%3;
			switch(r){
				case 0:
					tabHead[i] = "S1";
					break;
				case 1:
					tabHead[i] = "q1";
					break;
				case 2:
					tabHead[i] = "L/R";
					break;
			}
		}
		dm.setDataVector(tabData, tabHead);
		lm = new AbstractListModel<String>(){
			String headers[] = states();
		    public int getSize() { return headers.length; }
		    public String getElementAt(int index) {
		    	return headers[index];
		    }
		};
		tab = new JTable(dm){
			protected JTableHeader createDefaultTableHeader(){
				return new GroupableTableHeader(columnModel);
			}
		};
		tab.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		header = (GroupableTableHeader)tab.getTableHeader();
		tabHead = new Object[alphabet.size()*3];
		tcm = tab.getColumnModel();
		int count = 0;
		for(char ch : alphabet){
			ColumnGroup cg = new ColumnGroup(""+ch);
			TableColumn temp;
			TableColumn tempSt;
			cg.add(tcm.getColumn(count*3));
			cg.add(tempSt = tcm.getColumn(count*3+1));
			cg.add(temp = tcm.getColumn(count*3+2));
			temp.setCellEditor(choseEditor);
			header.addColumnGroup(cg);
			count++;
		}
		
		JList<String> rowHeader = new JList<>(lm);    
	    rowHeader.setFixedCellWidth(50);
	    
	    rowHeader.setFixedCellHeight(tab.getRowHeight()
	                               + tab.getRowMargin()-1);
	    rowHeader.setCellRenderer(new RowHeaderRenderer(tab));
	    tab.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		rowHeader = new JList<>(lm);    
	    rowHeader.setFixedCellWidth(50);
	    
	    rowHeader.setFixedCellHeight(tab.getRowHeight()
	                               + tab.getRowMargin()-1);
	    rowHeader.setCellRenderer(new RowHeaderRenderer(tab));
	    tab.setPreferredScrollableViewportSize(new Dimension(this.getWidth()-100, 100));
	    tab.setFillsViewportHeight(false);
	    scroll = new JScrollPane(tab);
	    scroll.setRowHeaderView(rowHeader);
	    Color color = scroll.getBackground();
	    rowHeader.setBackground(color);
	    this.add(scroll, BorderLayout.CENTER);
	    //revalidate();
	}

	String[] states(){
		String[] temp = new String[stateQty];
		for(int i=0; i<stateQty; i++){
			temp[i] = ""+i;
		}
		return temp;
	}
}
