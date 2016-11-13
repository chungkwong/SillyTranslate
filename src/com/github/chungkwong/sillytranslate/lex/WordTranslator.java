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
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class WordTranslator extends JPanel implements TranslatorStage<Iterator<Token>,Iterator<Token>>{
	private final JLabel currIn=new JLabel();
	private final JTextField currOut=new JTextField();
	private List<Token> buf;
	private Iterator<Token> iter;
	private Token curr;
	private Consumer<Iterator<Token>> callback;
	public WordTranslator(NavigableDictionary dict){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		currIn.setFocusable(false);
		add(currIn);
		new AutoCompleteSupport(currOut,new DictionaryHintProvider(dict));
		currOut.addActionListener((e)->next());
		add(currOut);
	}
	private void next(){
		if(curr!=null)
			buf.add(new Token(curr.getType(),currOut.getText()));
		if(iter.hasNext()){
			curr=iter.next();
			currIn.setText(curr.getText());
			currOut.setText("");
			currOut.requestFocusInWindow();
		}else{
			iter=null;
			curr=null;
			callback.accept(buf.iterator());
			buf=null;
			callback=null;
		}
	}
	@Override
	public JComponent accept(Iterator<Token> source,Consumer<Iterator<Token>> callback){
		this.iter=source;
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
				return DictionaryHintExtractor.extractHint(currIn.getText(),doc.getText(0,pos),dict);
			}catch(BadLocationException ex){
				Logger.getLogger(WordTranslator.class.getName()).log(Level.SEVERE,null,ex);
				return new Hint[0];
			}
		}
	}
}