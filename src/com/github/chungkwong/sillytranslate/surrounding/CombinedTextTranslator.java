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
package com.github.chungkwong.sillytranslate.surrounding;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CombinedTextTranslator extends JPanel implements TextTranslator{
	private final TextTranslator[] translators;
	private final JTextArea in=new JTextArea();
	public CombinedTextTranslator(TextTranslator... translators){
		this.translators=translators;
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		JLabel org=new JLabel("Input");
		org.setAlignmentX(0);
		add(org);
		in.setEditable(false);
		in.setAlignmentX(0);
		add(in);
		for(TextTranslator translator:translators){
			JLabel label=new JLabel(translator.getName());
			label.setAlignmentX(0);
			add(label);
			JComponent translatorView=translator.getUserInterface();
			translatorView.setAlignmentX(0);
			add(translatorView);
		}
	}
	@Override
	public void translate(String text,DocumentTranslatorEngine callback){
		in.setText(text);
		for(TextTranslator translator:translators){
			translator.translate(text,callback);
		}
	}
	@Override
	public JComponent getUserInterface(){
		return this;
	}
	@Override
	public String getName(){
		return "Integrated";
	}
}
