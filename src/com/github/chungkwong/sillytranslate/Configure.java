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
package com.github.chungkwong.sillytranslate;
import com.github.chungkwong.sillytranslate.lex.*;
import com.github.chungkwong.sillytranslate.sentence.*;
import com.github.chungkwong.sillytranslate.surrounding.*;
import com.github.chungkwong.sillytranslate.ui.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Configure extends JDialog{
	private final Preferences pref=Preferences.userNodeForPackage(Configure.class);
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
		Box control=Box.createHorizontalBox();
		JButton importPref=new JButton("Import");
		importPref.addActionListener((e)->importPref());
		control.add(importPref);
		JButton exportPref=new JButton("export");
		exportPref.addActionListener((e)->exportPref());
		control.add(exportPref);
		JButton savePref=new JButton("Save as default");
		savePref.addActionListener((e)->savePref());
		control.add(savePref);
		control.setAlignmentX(0);
		box.add(control);
		load();
		add(box);
		setUndecorated(false);
		setType(Type.NORMAL);
		pack();
	}
	private void importPref(){
		JFileChooser jfc=new JFileChooser();
		if(jfc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
			try(InputStream in=new FileInputStream(jfc.getSelectedFile())){
				Preferences.importPreferences(in);
				pref.sync();
				load();
			}catch(IOException|InvalidPreferencesFormatException|BackingStoreException ex){
				Logger.getLogger(Configure.class.getName()).log(Level.SEVERE,null,ex);
				JOptionPane.showMessageDialog(null,ex.getLocalizedMessage());
			}
		}
	}
	private void exportPref(){
		updatePref();
		JFileChooser jfc=new JFileChooser();
		if(jfc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
			try(OutputStream out=new FileOutputStream(jfc.getSelectedFile())){
				pref.exportNode(out);
			}catch(IOException|BackingStoreException ex){
				Logger.getLogger(Configure.class.getName()).log(Level.SEVERE,null,ex);
				JOptionPane.showMessageDialog(null,ex.getLocalizedMessage());
			}
		}
	}
	private void savePref(){
		updatePref();
		try{
			pref.flush();
		}catch(BackingStoreException ex){
			Logger.getLogger(Configure.class.getName()).log(Level.SEVERE,null,ex);
			JOptionPane.showMessageDialog(null,ex.getLocalizedMessage());
		}
	}
	private void updatePref(){
		pref.putBoolean("NaiveTranslator",simple.isSelected());
		pref.putBoolean("StagedTranslator",staged.isSelected());
		pref.putBoolean("SimpleLex",simpleLex.isSelected());
		pref.putBoolean("PrefixLex",prefixLex.isSelected());
		pref.putBoolean("JavaLex",javaLex.isSelected());
		pref.putBoolean("DictionaryTranslator",dictWord.isSelected());
		pref.putBoolean("NaiveSentenceTranslator",naiveSentence.isSelected());
		pref.put("InputLanguage",localeChooser.getSelectedItem().toLanguageTag());
		pref.put("Dictionary",dictionaryChooser.toPaths());
	}
	private void load(){
		simple.setSelected(pref.getBoolean("NaiveTranslator",false));
		staged.setSelected(pref.getBoolean("StagedTranslator",true));
		simpleLex.setSelected(pref.getBoolean("SimpleLex",true));
		prefixLex.setSelected(pref.getBoolean("PrefixLex",false));
		javaLex.setSelected(pref.getBoolean("JavaLex",false));
		dictWord.setSelected(pref.getBoolean("DictionaryTranslator",true));
		naiveSentence.setSelected(pref.getBoolean("NaiveSentenceTranslator",true));
		localeChooser.setSelectedItem(Locale.forLanguageTag(pref.get("InputLanguage","en-US")));
		try{
			dictionaryChooser.fromPaths(pref.get("Dictionary",""));
		}catch(IOException ex){
			Logger.getLogger(Configure.class.getName()).log(Level.SEVERE,null,ex);
			JOptionPane.showMessageDialog(null,ex.getLocalizedMessage());
		}
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

