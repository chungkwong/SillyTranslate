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
package com.github.chungkwong.sillytranslate.lex;
import com.github.chungkwong.sillytranslate.*;
import com.github.chungkwong.sillytranslate.ui.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
import javax.swing.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class WordTranslator extends JPanel implements TranslatorStage<List<Token>,Iterator<Token>>{
	private final JLabel currIn=new JLabel();
	private final JTextField currOut=new JTextField();
	private final WordMemory memory;
	private final NavigableDictionary dict;
	private final AutoCompleteSupport autoCompleteSupport;
	private List<Token> buf;
	private List<Token> in;
	private int index;
	private int end;
	private Token curr;
	private Consumer<Iterator<Token>> callback;
	public WordTranslator(NavigableDictionary dict,WordMemory memory){
		setLayout(new BorderLayout());
		this.memory=memory;
		this.dict=dict;
		currIn.setFocusable(false);
		MoreAction moreAction=new MoreAction();
		LessAction lessAction=new LessAction();
		Box inBox=Box.createHorizontalBox();
		JButton less=new JButton("<");
		less.setAction(lessAction);
		inBox.add(less);
		inBox.add(currIn);
		JButton more=new JButton(">");
		more.addActionListener(moreAction);
		inBox.add(more);
		add(currIn,BorderLayout.NORTH);
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP,0),"less");
		getActionMap().put("less",lessAction);
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN,0),"more");
		getActionMap().put("more",moreAction);
		autoCompleteSupport=new AutoCompleteSupport(currOut,new DictionaryHintProvider(dict));
		currOut.addActionListener((e)->next());
		add(currOut,BorderLayout.CENTER);
	}
	private void next(){
		if(curr!=null){
			String out=currOut.getText(),meaning,tag;
			if(out.contains(":")){
				int i=out.lastIndexOf(":");
				meaning=out.substring(0,i);
				tag=out.substring(i+1);
			}else{
				meaning=out;
				tag="";
			}
			buf.add(new Token(curr.getType(),meaning,tag));
			memory.useMeaning(currIn.getText(),meaning,tag);
		}
		if(end<in.size()){
			index=end++;
			curr=in.get(index);
			String lastMatch=curr.getText(),trying=lastMatch,next;
			int j=end;
			if(j<in.size())
				do{
					trying+=" "+in.get(j++).getText();
					next=dict.getCurrentWord(trying);
					if(!next.startsWith(trying)){
						trying=trying.toLowerCase();
						next=dict.getCurrentWord(trying);
					}
					System.out.println(trying+":"+next);
					if(trying.equals(next)){
						lastMatch=trying;
						end=j;
					}
				}while(j<in.size()&&next.startsWith(trying));
			currIn.setText(lastMatch);
			currOut.setText("");
			autoCompleteSupport.updateHint();
			currOut.requestFocusInWindow();
		}else{
			in=null;
			curr=null;
			callback.accept(buf.iterator());
			buf=null;
			callback=null;
		}
	}
	@Override
	public JComponent accept(List<Token> source,Consumer<Iterator<Token>> callback){
		this.in=source;
		this.index=0;
		this.end=0;
		this.callback=callback;
		buf=new ArrayList<>();
		next();
		return this;
	}
	/*public static void main(String[] args) throws IOException{
		JFrame f=new JFrame("Translator");
		WordTranslator translator=new WordTranslator(new StardictDictionary(new File("/home/kwong/下载/stardict-lazyworm-ec-2.4.2")));
		f.add(translator);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}*/
	class DictionaryHintProvider implements HintProvider{
		private final NavigableDictionary dict;
		public DictionaryHintProvider(NavigableDictionary dict){
			this.dict=dict;
		}
		@Override
		public Hint[] getHints(Document doc,int pos){
			try{
				return DictionaryHintExtractor.extractHint(currIn.getText(),doc.getText(0,pos),dict,memory);
			}catch(BadLocationException ex){
				Logger.getLogger(WordTranslator.class.getName()).log(Level.SEVERE,null,ex);
				return new Hint[0];
			}
		}
	}
	private class LessAction extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e){
			if(end>index+1){
				--end;
				currIn.setText(in.subList(index,end).stream().map((t)->t.getText()).collect(Collectors.joining(" ")));
				autoCompleteSupport.updateHint();
			}
		}
	}
	private class MoreAction extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e){
			if(end<in.size()){
				++end;
				currIn.setText(in.subList(index,end).stream().map((t)->t.getText()).collect(Collectors.joining(" ")));
				autoCompleteSupport.updateHint();
			}
		}
	}
}