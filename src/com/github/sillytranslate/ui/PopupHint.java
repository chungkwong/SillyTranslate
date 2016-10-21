/*
 * Copyright (C) 2016 Chan Chung Kwong <1m02math@126.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.sillytranslate.ui;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PopupHint extends JPanel implements MouseInputListener,ListSelectionListener{
	private final DefaultListModel<Hint> vec=new DefaultListModel<>();
	private final JEditorPane note=new JEditorPane();
	private final JList<Hint> loc=new JList<Hint>(vec);
	private Document doc;
	private int pos;
	private Popup popup;
	public PopupHint(){
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(400,300));
		loc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		loc.setSelectedIndex(0);
		loc.addMouseListener(this);
		loc.addListSelectionListener(this);
		loc.setCellRenderer(new DefaultListCellRenderer(){
			@Override
			public Component getListCellRendererComponent(JList arg0,Object arg1,int arg2,boolean arg3,boolean arg4){
				Component c=super.getListCellRendererComponent(arg0,arg1,arg2,arg3,arg4);
				((JLabel)c).setText(((Hint)arg1).getDisplayText());
				((JLabel)c).setIcon(((Hint)arg1).getIcon());
				((JLabel)c).setHorizontalAlignment(SwingConstants.LEFT);
				return c;
			}
		});
		loc.setOpaque(false);
		add(new JScrollPane(loc),BorderLayout.WEST);
		note.setContentType("text/html");
		note.setEditable(false);
		add(new JScrollPane(note),BorderLayout.EAST);
	}
	public void showHints(JTextComponent comp,int pos,Hint[] choices){
		hideHints();
		if(choices.length==0)
			return;
		this.pos=pos;
		vec.ensureCapacity(choices.length);
		for(int i=0;i<choices.length;i++)
			vec.add(i,choices[i]);
			loc.setSelectedIndex(0);
		try{
			Point loc=comp.modelToView(pos).getLocation();
			int lineheight=comp.getFontMetrics(comp.getFont()).getHeight();
			loc.translate((int)comp.getLocationOnScreen().getX(),(int)comp.getLocationOnScreen().getY()+lineheight);
			popup=PopupFactory.getSharedInstance().getPopup(comp,this,(int)loc.getX(),(int)loc.getY());
			popup.show();
			doc=comp.getDocument();
		}catch(BadLocationException|NullPointerException ex){

		}
	}
	public void hideHints(){
		vec.removeAllElements();
		if(popup!=null)
			popup.hide();
		popup=null;
		doc=null;
	}
	public boolean isShowing(){
		return popup!=null;
	}
	void selectPrevious(){
		if(!vec.isEmpty())
			loc.setSelectedIndex((loc.getSelectedIndex()+vec.getSize()-1)%vec.getSize());
	}
	void selectNext(){
		if(!vec.isEmpty())
			loc.setSelectedIndex((loc.getSelectedIndex()+1)%vec.getSize());
	}
	void choose(){
		choose(vec.getElementAt(loc.getSelectedIndex()).getInputText());
	}
	private void choose(String inputText){
		try{
			doc.insertString(pos,inputText,null);
		}catch(Exception ex){
			Logger.getGlobal().log(Level.FINER,inputText,ex);
		}
		hideHints();
	}
	@Override
	public void mouseClicked(MouseEvent e){
		if(e.getClickCount()==2){
			choose(vec.get(loc.locationToIndex(e.getPoint())).getInputText());
		}
	}
	@Override
	public void mousePressed(MouseEvent e){}
	@Override
	public void mouseReleased(MouseEvent e){}
	@Override
	public void mouseEntered(MouseEvent e){}
	@Override
	public void mouseExited(MouseEvent e){}
	@Override
	public void mouseDragged(MouseEvent e){}
	@Override
	public void mouseMoved(MouseEvent e){}
	@Override
	public void valueChanged(ListSelectionEvent e){
		try{
			if(loc.getSelectedValue()!=null){
				note.read(loc.getSelectedValue().getDocument(),null);
			}
		}catch(IOException ex){
			note.setText("NO_DOCUMENT");
		}
	}
}
