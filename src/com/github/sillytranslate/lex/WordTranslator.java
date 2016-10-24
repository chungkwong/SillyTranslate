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
package com.github.sillytranslate.lex;
import com.github.sillytranslate.ui.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class WordTranslator extends JPanel{
	private final JLabel currIn=new JLabel();
	private final JTextField currOut=new JTextField();
	private Iterator<Token> iter;
	private StringBuilder buf=new StringBuilder();
	public WordTranslator(NavigableDictionary dict,Iterator<Token> iter){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(currIn);
		new AutoCompleteSupport(currOut,new DictionaryHintProvider(dict));
		currOut.addActionListener((e)->next());
		add(currOut);
		this.iter=iter;
		next();
	}
	private void next(){
		buf.append(currOut.getText());
		if(iter.hasNext()){
			currIn.setText(iter.next().getText());
			currOut.setText("");
			currOut.requestFocusInWindow();
		}else{
			iter=null;
			currOut.setText(buf.toString());
			buf=null;
			currOut.setEnabled(false);
		}
	}
	/*public static void main(String[] args) throws IOException{
		JFrame f=new JFrame("Translator");
		f.add(new WordTranslator(new StardictDictionary(new File("/home/kwong/下载/stardict-lazyworm-ec-2.4.2"))));
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
			String text=dict.getMeaning(currIn.getText());
			if(text==null)
				text=dict.getMeaning(currIn.getText().toLowerCase());
			if(text!=null)
				return split(text,currOut.getText());
			else
				return new Hint[0];
		}
		private Hint[] split(String text,String prefix){
			ArrayList<Hint> hints=new ArrayList<>();
			int prefixLen=prefix.length();
			for(String mean:text.split("[\\p{ASCII}]+")){
				if(mean.startsWith(prefix)&&!mean.isEmpty())
					hints.add(new SimpleHint(mean,mean.substring(prefixLen),null,""));
			}
			String org=currIn.getText();
			if(org.startsWith(prefix))
				hints.add(new SimpleHint(org,org.substring(prefixLen),null,""));
			return hints.toArray(new Hint[0]);
		}
	}
}
