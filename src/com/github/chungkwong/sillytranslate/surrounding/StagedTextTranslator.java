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
import com.github.chungkwong.sillytranslate.lex.*;
import com.github.chungkwong.sillytranslate.sentence.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class StagedTextTranslator extends JPanel implements TextTranslator{
	private final String LEX="Lex";
	private final String WORD="Word";
	private final String SENTENCE="Sentence";
	private final CardLayout card=new CardLayout();
	private final JPanel content=new JPanel(card);
	private final LexEditor lexEditor=new LexEditor();
	private final Lex lex;
	private final WordTranslator wordTranslator;
	private final SentenceTranslatorView sentenceTranslator;
	private DocumentTranslatorEngine callback;
	public StagedTextTranslator(Lex lex,WordTranslator wordTranslator,SentenceTranslatorView sentenceTranslator){
		super(new BorderLayout());
		this.lex=lex;
		this.wordTranslator=wordTranslator;
		this.sentenceTranslator=sentenceTranslator;
		content.add(lexEditor,LEX);
		content.add(wordTranslator,WORD);
		content.add(sentenceTranslator,SENTENCE);
		add(content,BorderLayout.CENTER);
	}
	@Override
	public void translate(String text,DocumentTranslatorEngine callback){
		this.callback=callback;
		card.show(content,LEX);
		lex.setInput(text);
		lexEditor.accept(lex,(words)->{
			card.show(content,WORD);
			wordTranslator.accept(words,(newWords)->{
				card.show(content,SENTENCE);
				sentenceTranslator.accept(newWords,(sentence)->{
					callback.textTranslated(sentence);
				});
			});
		});
	}
	@Override
	public JComponent getUserInterface(){
		return this;
	}
	@Override
	public String getName(){
		return java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("STAGED");
	}
	public static void main(String[] args) throws FileNotFoundException, IOException{
		JFrame f=new JFrame("Translator");
		Lex lex=new SimpleLex();
		WordTranslator wordTranslator=new WordTranslator(new StardictDictionary(new File("/home/kwong/下载/stardict-lazyworm-ec-2.4.2")),new WordMemory());
		SentenceTranslatorView sentenceTranslator=new SentenceTranslatorView(new NaiveTranslator(24));
		StagedTextTranslator translator=new StagedTextTranslator(lex,wordTranslator,sentenceTranslator);
		PlainTextTranslator t=new PlainTextTranslator();
		t.setTextTranslator(translator);
		t.setOnFinished(()->{});
		f.add(translator);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		t.start(new FileInputStream("/home/kwong/NetBeansProjects/JSchemeMin/README.md"),System.out);
	}
}