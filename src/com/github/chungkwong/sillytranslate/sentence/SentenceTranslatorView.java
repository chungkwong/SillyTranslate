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
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
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
	private Iterator<Token> iter;
	private Consumer<String> callback;
	private Token curr;
	public SentenceTranslatorView(SentenceTranslatorEngine engine){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		this.engine=engine;
		input.setFocusable(false);
		input.setAlignmentX(0);
		add(input);
		list.setAlignmentX(0);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e){
				result.setText(list.getSelectedValue());
			}
		});
		list.setAction((item)->next(item));
		add(list);
		result.setAlignmentX(0);
		add(result);
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
			callback.accept(buf.toString());
			buf.setLength(0);
			callback=null;
		}else{
			input.setText(words.stream().map((t)->t.getText()).collect(Collectors.joining(" ")));
			JDialog dialog=new JOptionPane(WAITING,JOptionPane.INFORMATION_MESSAGE,JOptionPane.OK_CANCEL_OPTION).createDialog("");;
			Thread t=new Thread(()->{
				List<String> translation=engine.getTranslation(words);
				SwingUtilities.invokeLater(()->{
					dialog.setVisible(false);
					choices.ensureCapacity(translation.size());
					translation.forEach((s)->choices.addElement(s));
					if(!translation.isEmpty())
						list.setSelectedIndex(0);
					list.requestFocusInWindow();
				});
			});
			SwingUtilities.invokeLater(()->{
				dialog.setVisible(true);
				t.interrupt();
			});
			t.start();
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
		next("");
		return this;
	}
	public static void main(String[] args){
		JOptionPane optionPane=new JOptionPane("正在生成候选",JOptionPane.INFORMATION_MESSAGE,JOptionPane.CANCEL_OPTION);
		JDialog dialog=optionPane.createDialog("");
		Thread t=new Thread(()->{
			try{
				Thread.sleep(5000);
				dialog.setVisible(false);
			}catch(InterruptedException ex){
				System.out.println("oops");
				Logger.getLogger(SentenceTranslatorView.class.getName()).log(Level.SEVERE,null,ex);
			}
		});
		t.start();
		dialog.setVisible(true);
		t.interrupt();
		System.exit(0);
	}
}
