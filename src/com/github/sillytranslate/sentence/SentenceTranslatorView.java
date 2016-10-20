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
package com.github.sillytranslate.sentence;
import java.util.*;
import java.util.function.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SentenceTranslatorView extends JPanel{
	private final JLabel input=new JLabel();
	private final DefaultListModel<String> choices=new DefaultListModel<>();
	private final JList<String> list=new JList<>(choices);
	private final JTextField result=new JTextField();
	private final SentenceTranslatorEngine engine;
	public SentenceTranslatorView(SentenceTranslatorEngine engine,Consumer<String> consumer){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		this.engine=engine;
		add(input);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e){
				result.setText(list.getSelectedValue());
			}
		});
		add(list);
		result.addActionListener((e)->consumer.accept(result.getText()));
		add(result);
	}
	public void setInput(String org,List<String> words){
		input.setText(org);
		List<String> translation=engine.getTranslation(words);
		choices.capacity();
		choices.ensureCapacity(translation.size());
		translation.forEach((s)->choices.addElement(s));
		if(!translation.isEmpty())
			list.setSelectedIndex(0);
	}
	public static void main(String[] args){
		JFrame f=new JFrame("Sentence translator");
		SentenceTranslatorView sentenceTranslator=new SentenceTranslatorView(
				new NaiveTranslator()
				,(s)->JOptionPane.showMessageDialog(null,s));
		sentenceTranslator.setInput("Org",Arrays.asList(JOptionPane.showInputDialog("").split(" ")));
		f.add(sentenceTranslator);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
