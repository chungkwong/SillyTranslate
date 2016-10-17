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
import javax.swing.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class AutoCompleteSupport implements KeyListener,FocusListener{
	private final JTextComponent comp;
	private final HintProvider hints;
	private static final PopupHint popupHint=new PopupHint();
	private static final RealTimeTask<HintContext> task=new RealTimeTask<>((o)->{
		Hint[] hint=o.provider.getHints(o.component.getDocument(),o.position);
		SwingUtilities.invokeLater(()->popupHint.showHints(o.component,o.position,hint));
	});
	public AutoCompleteSupport(JTextComponent comp,HintProvider hints){
		this.hints=hints;
		this.comp=comp;
		comp.addKeyListener(this);
		comp.addFocusListener(this);
		comp.addCaretListener((e)->updateHint());
	}
	private void updateHint(){
		task.summit(new HintContext(hints,comp,comp.getSelectionStart()));
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
		//if(popup!=null)
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
	@Override
	public void focusGained(FocusEvent e){
		updateHint();
	}
	@Override
	public void focusLost(FocusEvent e){
		popupHint.hideHints();
	}
	static class HintContext{
		final HintProvider provider;
		final JTextComponent component;
		final int position;
		public HintContext(HintProvider provider,JTextComponent component,int position){
			this.provider=provider;
			this.component=component;
			this.position=position;
		}
	}
}