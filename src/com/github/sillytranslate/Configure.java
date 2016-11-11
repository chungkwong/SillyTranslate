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
import com.github.sillytranslate.surrounding.*;
import com.github.sillytranslate.ui.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Configure extends JDialog{
	private final JRadioButton simple=new JRadioButton("Naive");
	private final JRadioButton staged=new JRadioButton("Staged");
	private final JRadioButton simpleLex=new JRadioButton("Simple");
	private final JRadioButton prefixLex=new JRadioButton("Prefix");
	private final JRadioButton javaLex=new JRadioButton("Java default");
	private final JRadioButton dictWord=new JRadioButton("Dictionary");
	private final JRadioButton naiveSentence=new JRadioButton("Naive");
	private final LocaleChooser localeChooser=new LocaleChooser();
	private final DictionaryChooser dictionaryChooser=new DictionaryChooser();
	public Configure(){
		Box box=Box.createVerticalBox();
		ButtonGroup translatorType=new ButtonGroup();
		translatorType.add(simple);
		box.add(simple);
		translatorType.add(staged);
		box.add(staged);
		Box lexBox=Box.createHorizontalBox();
		lexBox.add(new JLabel("Lex:"));
		ButtonGroup lexType=new ButtonGroup();
		lexType.add(simpleLex);
		lexBox.add(simpleLex);
		lexType.add(prefixLex);
		lexBox.add(prefixLex);
		lexType.add(javaLex);
		lexBox.add(javaLex);
		lexBox.setAlignmentX(0);
		box.add(lexBox);
		Box wordBox=Box.createHorizontalBox();
		wordBox.add(new JLabel("Word:"));
		ButtonGroup wordType=new ButtonGroup();
		wordType.add(dictWord);
		wordBox.add(dictWord);
		wordBox.setAlignmentX(0);
		box.add(wordBox);
		Box sentenceBox=Box.createHorizontalBox();
		sentenceBox.add(new JLabel("Word:"));
		ButtonGroup sentenceType=new ButtonGroup();
		sentenceType.add(naiveSentence);
		sentenceBox.add(naiveSentence);
		sentenceBox.setAlignmentX(0);
		box.add(sentenceBox);
		Box localeBox=Box.createHorizontalBox();
		localeBox.add(new JLabel("Input language"));
		localeBox.add(localeChooser);
		localeBox.setAlignmentX(0);
		box.add(localeBox);
		dictionaryChooser.setAlignmentX(0);
		box.add(dictionaryChooser);
		add(box);
		setUndecorated(false);
		setType(Type.NORMAL);
		pack();
	}

	public TextTranslator getTranslator(){
		if(simple.isSelected())
			return new SimpleTextTranslator();
		else{
			Lex lex=simpleLex.isSelected()?new SimpleLex():
					(prefixLex.isSelected()?new PrefixLex(dictionaryChooser.getDictionary()):new JavaLex(localeChooser.getSelectedItem()));
			WordTranslator wordTranslator=new WordTranslator(dictionaryChooser.getDictionary());
			SentenceTranslatorView sentenceTranslator=new SentenceTranslatorView(new NaiveTranslator(24));
			return new StagedTextTranslator(lex,wordTranslator,sentenceTranslator);
		}
	}
}
