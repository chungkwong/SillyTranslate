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
import com.github.sillytranslate.util.*;
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
public class AutoCompleteSupport implements KeyListener{
	private final JTextComponent comp;
	private final Document doc;
	private final HintProvider hints;
	private final RealTimeTask<Integer> task;
	private final PopupHint popupHint;
	private Popup popup;
	public AutoCompleteSupport(JTextComponent comp,HintProvider hints){
		this.hints=hints;
		this.comp=comp;
		this.doc=comp.getDocument();
		this.popupHint=new PopupHint();
		this.task=new RealTimeTask<>((pos)->{
			Hint[] hint=hints.getHints(doc,pos);
			SwingUtilities.invokeLater(()->popupHint.showHints(pos,hint));
		});
		comp.addKeyListener(this);
		comp.addCaretListener((e)->task.summit(e.getDot()));
	}
	public static void main(String[] args){
		JFrame f=new JFrame("Test");
		JTextField field=new JTextField();
		new AutoCompleteSupport(field,new HintProvider() {
			@Override
			public Hint[] getHints(Document doc,int pos){
				return new Hint[]{
					new SimpleHint("ln",null,"<h1>link file</h1>"),
					new SimpleHint("pwd",null,"show working directory"),
					new SimpleHint(Integer.toHexString(pos),null,"position")
				};
			}
		});
		f.add(field,BorderLayout.CENTER);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	@Override
	public void keyTyped(KeyEvent e){

	}
	@Override
	public void keyPressed(KeyEvent e){
		if(popup!=null)
			switch(e.getKeyCode()){
				case KeyEvent.VK_UP:
					popupHint.selectPrevious();
					break;
				case KeyEvent.VK_DOWN:
					popupHint.selectNext();
					break;
				case KeyEvent.VK_ENTER:
					popupHint.choose();
					break;
			}
	}
	@Override
	public void keyReleased(KeyEvent e){

	}
	class PopupHint extends JPanel implements MouseInputListener,ListSelectionListener{
		private DefaultListModel<Hint> vec=new DefaultListModel<>();
		private JEditorPane note=new JEditorPane();
		private JList<Hint> loc=new JList<Hint>(vec);
		private int pos;
		private final int lineheight=comp.getFontMetrics(comp.getFont()).getHeight();
		public PopupHint(){
			setLayout(new BorderLayout());
		//setUndecorated(true);
			setSize(400,300);
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
		public void showHints(int pos,Hint[] choices){
			if(popup!=null)
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
				loc.translate((int)comp.getLocationOnScreen().getX(),(int)comp.getLocationOnScreen().getY()+lineheight);
				popup=PopupFactory.getSharedInstance().getPopup(comp,this,(int)loc.getX(),(int)loc.getY());
				popup.show();
			//popup.show(this,(int)rect.getX(),(int)rect.getY());
			//popup.requestFocusInWindow();
			}catch(BadLocationException|NullPointerException ex){

			}
		}
		public void hideHints(){
			vec.removeAllElements();
			popup.hide();
			popup=null;
		}
		void selectPrevious(){
			loc.setSelectedIndex((loc.getSelectedIndex()+vec.getSize()-1)%vec.getSize());
		}
		void selectNext(){
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
			popup.hide();
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
}
