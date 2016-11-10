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
package com.github.sillytranslate;
import com.github.sillytranslate.lex.*;
import com.github.sillytranslate.sentence.*;
import java.awt.*;
import java.io.*;
import java.util.function.*;
import javax.swing.FocusManager;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Translator extends JPanel{
	private InputPanel in;
	private final TranslatorStage[] translators;
	public Translator(NavigableDictionary dict){
		FocusManager.getCurrentManager().addPropertyChangeListener((e)->{System.out.println(e.toString());});
		setLayout(new BorderLayout());
		translators=new TranslatorStage[]{
			new LexEditor(),new WordTranslator(dict),new SentenceTranslatorView(new NaiveTranslator(24)),new OutputPanel()
		};
		in=new InputPanel((text)->{
			removeAll();
			add(translators[0].accept(new SimpleLex(new StringReader(text)),bind(1)));
			validate();
			getComponent(0).requestFocusInWindow();
		});
		add(in,BorderLayout.CENTER);
	}
	private <T> Consumer<T> bind(int i){
		return (t)->{
			removeAll();
			if(i+1<translators.length)
				add(translators[i].accept(t,bind(i+1)),BorderLayout.CENTER);
			else
				add(translators[i].accept(t,null),BorderLayout.CENTER);
			validate();
			getComponent(0).requestFocusInWindow();
		};
	}
	public static void main(String[] args) throws IOException {
		JFrame f=new JFrame("Translator");
		f.add(new Translator(new StardictDictionary(new File("/home/kwong/下载/stardict-lazyworm-ec-2.4.2"))));
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
class InputPanel extends JPanel{
	public InputPanel(Consumer<String> consumer){
		setLayout(new BorderLayout());
		JTextArea from=new JTextArea();
		add(new JScrollPane(from),BorderLayout.CENTER);
		JButton ok=new JButton("Start");
		ok.addActionListener((e)->consumer.accept(from.getText()));
		add(ok,BorderLayout.SOUTH);
	}
}
class OutputPanel extends JPanel implements TranslatorStage<String,String>{
	private JTextArea to=new JTextArea();
	public OutputPanel(){
		setLayout(new BorderLayout());
		add(new JScrollPane(to),BorderLayout.CENTER);
	}
	@Override
	public JComponent accept(String source,Consumer<String> callback){
		to.setText(source);
		return this;
	}
}