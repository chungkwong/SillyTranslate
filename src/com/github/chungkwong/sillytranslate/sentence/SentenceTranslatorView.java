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
package com.github.chungkwong.sillytranslate.sentence;
import com.github.chungkwong.sillytranslate.*;
import com.github.chungkwong.sillytranslate.lex.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SentenceTranslatorView extends JPanel implements TranslatorStage<Iterator<Token>,String>{
	private final JLabel input=new JLabel();
	private final DefaultListModel<String> choices=new DefaultListModel<>();
	private final JList<String> list=new JList<>(choices);
	private final JTextField result=new JTextField();
	private final SentenceTranslatorEngine engine;
	private final StringBuilder buf=new StringBuilder();
	private Iterator<Token> iter;
	private Consumer<String> callback;
	private Token curr;
	public SentenceTranslatorView(SentenceTranslatorEngine engine){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		this.engine=engine;
		input.setFocusable(false);
		add(input);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e){
				result.setText(list.getSelectedValue());
			}
		});
		list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount()==2)
					next();
			}
		});
		add(list);
		result.addActionListener((e)->next());
		add(result);
	}
	private void next(){
		buf.append(result.getText());
		result.setText("");
		choices.removeAllElements();
		if(curr!=null){
			buf.append(curr.getText());
			curr=null;
		}
		List<Token> words=new ArrayList<>();
		while(iter.hasNext()){
			Token token=iter.next();
			if(token.getType()==Token.Type.FULL_STOP){
				if(words.isEmpty()){
					buf.append(token.getText());
				}else{
					curr=token;
					break;
				}
			}else{
				words.add(token);
			}
		}
		if(words.isEmpty()){
			iter=null;
			callback.accept(buf.toString());
			buf.setLength(0);
			callback=null;
		}else{
			input.setText(words.stream().map((t)->t.getText()).collect(Collectors.joining(" ")));
			List<String> translation=engine.getTranslation(words);
			choices.ensureCapacity(translation.size());
			translation.forEach((s)->choices.addElement(s));
			if(!translation.isEmpty())
				list.setSelectedIndex(0);
			list.requestFocusInWindow();
		}
	}
	/*public static void main(String[] args){
		JFrame f=new JFrame("Sentence translator");
		SentenceTranslatorView sentenceTranslator=new SentenceTranslatorView(
				new NaiveTranslator()
				,(s)->JOptionPane.showMessageDialog(null,s));
		sentenceTranslator.setInput("Org",Arrays.asList(JOptionPane.showInputDialog("").split(" ")));
		f.add(sentenceTranslator);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}*/
	@Override
	public JComponent accept(Iterator<Token> source,Consumer<String> callback){
		this.callback=callback;
		this.iter=source;
		next();
		return this;
	}
}
