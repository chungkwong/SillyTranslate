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
import com.github.chungkwong.sillytranslate.ui.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SentenceTranslatorView extends JPanel implements TranslatorStage<Iterator<Token>,String>{
	private static final String WAITING=java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("GENERATING");
	private final JLabel input=new JLabel();
	private final DefaultListModel<String> choices=new DefaultListModel<>();
	private final ActionList<String> list=new ActionList<>(choices);
	private final JTextArea result=new ActionTextArea((text)->next(text));
	private final SentenceTranslatorEngine engine;
	private final StringBuilder buf=new StringBuilder();
	private final JButton cancel=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("CANCEL"));
	private final JProgressBar progressBar=new JProgressBar();
	private Iterator<Token> iter;
	private Consumer<String> callback;
	private Token curr;
	private final boolean auto;
	private final Locale locale;
	private SwingWorker<List<String>,Object> worker;
	public SentenceTranslatorView(SentenceTranslatorEngine engine,boolean auto,Locale locale){
		setLayout(new BorderLayout());
		this.engine=engine;
		this.auto=auto;
		this.locale=locale;
		input.setFocusable(false);
		input.setAlignmentX(0);
		add(input,BorderLayout.NORTH);
		result.setAlignmentX(0);
		add(result,BorderLayout.CENTER);
		Box bottom=Box.createVerticalBox();
		list.setAlignmentX(0);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e){
				result.setText(list.getSelectedValue());
			}
		});
		list.setAction((item)->next(item));
		bottom.add(list);
		Box progress=Box.createHorizontalBox();
		cancel.setEnabled(false);
		progress.add(progressBar);
		cancel.addActionListener((e)->{
			worker.cancel(true);
			cancel.setEnabled(false);
			progressBar.setString("");
			progressBar.setIndeterminate(false);
		});
		cancel.setMnemonic(KeyEvent.VK_DELETE);
		progress.add(cancel);
		progress.setAlignmentX(0);
		bottom.add(progress);
		add(bottom,BorderLayout.SOUTH);
	}
	private void next(String text){
		buf.append(text);
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
			String result=buf.toString();
			buf.setLength(0);
			callback.accept(result);
		}else{
			input.setText(Sentences.build(words.stream().map(Token::getText),locale));
			result.setText(input.getText());
			worker=new SwingWorker<List<String>,Object>(){
				@Override
				protected List<String> doInBackground() throws Exception{
					return engine.getTranslation(words);
				}
				@Override
				protected void done(){
					try{
						List<String> translation=get();
						cancel.setEnabled(false);
						progressBar.setString("");
						progressBar.setIndeterminate(false);
						if(auto&&translation.size()==1){
							next(translation.get(0));
						}else{
							choices.ensureCapacity(translation.size());
							translation.forEach((s)->choices.addElement(s));
							if(!translation.isEmpty())
								list.setSelectedIndex(0);
							list.requestFocusInWindow();
						}
					}catch(Exception ex){
						Logger.getGlobal().log(Level.INFO,ex.getLocalizedMessage(),ex);
						ex.printStackTrace();
					}
				}
			};
			worker.execute();
			cancel.setEnabled(true);
			progressBar.setString(WAITING);
			progressBar.setIndeterminate(true);
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
	public void accept(Iterator<Token> source,Consumer<String> callback){
		this.callback=callback;
		this.iter=source;
		next("");
	}
}
