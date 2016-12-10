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
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Configure extends JFrame{
	private final Preferences pref=Preferences.userNodeForPackage(Configure.class);
	private final JCheckBox simple=new JCheckBox(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("MANUAL"));
	private final JCheckBox staged=new JCheckBox(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("STAGED"));
	private final JCheckBox cloud=new JCheckBox(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("CLOUD"));
	private final JCheckBox baidu=new JCheckBox(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("BAIDU"));
	private final JCheckBox youdao=new JCheckBox(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("YOUDAO"));
	private final JCheckBox yandex=new JCheckBox(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("YANDEX"));
	private final JRadioButton simpleLex=new JRadioButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("SIMPLE"));
	private final JRadioButton prefixLex=new JRadioButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("PREFIX"));
	private final JRadioButton javaLex=new JRadioButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("JAVA DEFAULT"));
	private final JRadioButton dictWord=new JRadioButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("DICTIONARY"));
	private final JCheckBox naiveSentence=new JCheckBox(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("BRUTE_FORCE"));
	private final JCheckBox ruleSentence=new JCheckBox(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("RULE BASED"));
	private final JTextField wordCache=new JTextField();
	private final JTextField yandexKey=new JTextField();
	private final JTextField baiduId=new JTextField();
	private final JTextField baiduSecret=new JTextField();
	private final JTextField youdaoId=new JTextField();
	private final JTextField youdaoSecret=new JTextField();
	private final JSpinner naiveLimit=new JSpinner(new SpinnerNumberModel(6,0,Integer.MAX_VALUE,1));
	private final JSpinner ruleLimit=new JSpinner(new SpinnerNumberModel(6,0,Integer.MAX_VALUE,1));
	private final LocaleChooser localeIn=new LocaleChooser();
	private final LocaleChooser localeOut=new LocaleChooser();
	private final DictionaryChooser dictionaryChooser=new DictionaryChooser();
	public Configure(){
		Box box=Box.createVerticalBox();
		box.add(simple);
		box.add(staged);
		box.add(cloud);
		Box baiduBox=Box.createHorizontalBox();
		baiduBox.add(baidu);
		baiduBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("ID:")));
		baiduBox.add(baiduId);
		baiduBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("SECRET:")));
		baiduBox.add(baiduSecret);
		JButton getBaiduKey=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("GET_ONE"));
		getBaiduKey.addActionListener((e)->{
			try{
				Desktop.getDesktop().browse(new URI("http://api.fanyi.baidu.com/api/trans/product/apiapply"));
			}catch(URISyntaxException|IOException ex){
				Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			}
		});
		baiduBox.add(getBaiduKey);
		baiduBox.setAlignmentX(0);
		box.add(baiduBox);
		Box youdaoBox=Box.createHorizontalBox();
		youdaoBox.add(youdao);
		youdaoBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("ID:")));
		youdaoBox.add(youdaoId);
		youdaoBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("SECRET:")));
		youdaoBox.add(youdaoSecret);
		JButton getYoudexKey=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("GET_ONE"));
		getYoudexKey.addActionListener((e)->{
			try{
				Desktop.getDesktop().browse(new URI("http://fanyi.youdao.com/openapi?path=data-mode"));
			}catch(URISyntaxException|IOException ex){
				Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			}
		});
		youdaoBox.add(getYoudexKey);
		youdaoBox.setAlignmentX(0);
		box.add(youdaoBox);
		Box yandexBox=Box.createHorizontalBox();
		yandexBox.add(yandex);
		yandexBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("KEY:")));
		yandexBox.add(yandexKey);
		JButton getYandexKey=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("GET_ONE"));
		getYandexKey.addActionListener((e)->{
			try{
				Desktop.getDesktop().browse(new URI("https://tech.yandex.com/key/form.xml?service=trnsl"));
			}catch(URISyntaxException|IOException ex){
				Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			}
		});
		yandexBox.add(getYandexKey);
		yandexBox.setAlignmentX(0);
		box.add(yandexBox);
		Box lexBox=Box.createHorizontalBox();
		lexBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("LEX:")));
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
		wordBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("WORD:")));
		ButtonGroup wordType=new ButtonGroup();
		wordType.add(dictWord);
		wordBox.add(dictWord);
		wordBox.setAlignmentX(0);
		box.add(wordBox);
		Box sentenceBox=Box.createHorizontalBox();
		sentenceBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("SENTENCE:")));
		sentenceBox.add(naiveSentence);
		sentenceBox.add(naiveLimit);
		sentenceBox.add(ruleSentence);
		sentenceBox.add(ruleLimit);
		sentenceBox.setAlignmentX(0);
		box.add(sentenceBox);
		Box localeBox=Box.createHorizontalBox();
		localeBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("INPUT LANGUAGE")));
		localeBox.add(localeIn);
		localeBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("OUTPUT LANGUAGE")));
		localeBox.add(localeOut);
		localeBox.setAlignmentX(0);
		box.add(localeBox);
		Box cacheBox=Box.createHorizontalBox();
		cacheBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("WORD CACHE PATH")));
		cacheBox.add(wordCache);
		JButton view=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("VIEW"));
		view.addActionListener((e)->{
			JFrame dia=new JFrame();
			WordMemory memory=WordMemory.getWordMemory(wordCache.getText());
			dia.add(new WordMemoryEditor(memory));
			dia.setExtendedState(JFrame.MAXIMIZED_BOTH);
			dia.setVisible(true);
		});
		cacheBox.add(view);
		cacheBox.setAlignmentX(0);
		box.add(cacheBox);
		dictionaryChooser.setAlignmentX(0);
		box.add(dictionaryChooser);
		JButton dict=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("CHECK_DICT"));
		dict.setAlignmentX(0);
		dict.addActionListener((e)->{
			JFrame f=new JFrame(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("DICTIONARY VIEWER"));
			f.add(new DictionaryViewer(dictionaryChooser.getDictionary()));
			f.setExtendedState(JFrame.MAXIMIZED_BOTH);
			f.setVisible(true);
		});
		box.add(dict);
		Box control=Box.createHorizontalBox();
		JButton importPref=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("IMPORT"));
		importPref.addActionListener((e)->importPref());
		control.add(importPref);
		JButton exportPref=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("EXPORT"));
		exportPref.addActionListener((e)->exportPref());
		control.add(exportPref);
		JButton savePref=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("SAVE AS DEFAULT"));
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
		if(jfc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
			try(InputStream in=new FileInputStream(jfc.getSelectedFile())){
				Preferences.importPreferences(in);
				pref.sync();
				load();
			}catch(IOException|InvalidPreferencesFormatException|BackingStoreException ex){
				Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
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
				Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			}
		}
	}
	private void savePref(){
		updatePref();
		try{
			pref.flush();
		}catch(BackingStoreException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
	}
	private void updatePref(){
		pref.putBoolean("NaiveTranslator",simple.isSelected());
		pref.putBoolean("StagedTranslator",staged.isSelected());
		pref.putBoolean("CloudTranslator",cloud.isSelected());
		pref.putBoolean("useBaidu",baidu.isSelected());
		pref.putBoolean("useYandex",yandex.isSelected());
		pref.putBoolean("useYoudao",youdao.isSelected());
		pref.putBoolean("SimpleLex",simpleLex.isSelected());
		pref.putBoolean("PrefixLex",prefixLex.isSelected());
		pref.putBoolean("JavaLex",javaLex.isSelected());
		pref.putBoolean("DictionaryTranslator",dictWord.isSelected());
		pref.putBoolean("NaiveSentenceTranslator",naiveSentence.isSelected());
		pref.putBoolean("RuleBasedSentenceTranslator",ruleSentence.isSelected());
		pref.put("InputLanguage",localeIn.getSelectedItem().toLanguageTag());
		pref.put("OutputLanguage",localeOut.getSelectedItem().toLanguageTag());
		pref.put("WordCache",wordCache.getText());
		pref.put("BaiduID",baiduId.getText());
		pref.put("BaiduSecret",baiduSecret.getText());
		pref.put("YoudaoID",youdaoId.getText());
		pref.put("YoudaoSecret",youdaoSecret.getText());
		pref.put("YandexKey",yandexKey.getText());
		pref.putInt("NaiveLimit",(Integer)naiveLimit.getValue());
		pref.putInt("RuleBasedLimit",(Integer)ruleLimit.getValue());
		pref.put("Dictionary",dictionaryChooser.toPaths());
	}
	private void load(){
		simple.setSelected(pref.getBoolean("NaiveTranslator",true));
		staged.setSelected(pref.getBoolean("StagedTranslator",true));
		cloud.setSelected(pref.getBoolean("CloudTranslator",true));
		baidu.setSelected(pref.getBoolean("useBaidu",false));
		yandex.setSelected(pref.getBoolean("useYandex",false));
		youdao.setSelected(pref.getBoolean("useYoudao",false));
		simpleLex.setSelected(pref.getBoolean("SimpleLex",true));
		prefixLex.setSelected(pref.getBoolean("PrefixLex",false));
		javaLex.setSelected(pref.getBoolean("JavaLex",false));
		dictWord.setSelected(pref.getBoolean("DictionaryTranslator",true));
		naiveSentence.setSelected(pref.getBoolean("NaiveSentenceTranslator",true));
		ruleSentence.setSelected(pref.getBoolean("RuleBasedSentenceTranslator",false));
		naiveLimit.setValue(pref.getInt("NaiveLimit",6));
		ruleLimit.setValue(pref.getInt("RuleLimit",6));
		localeIn.setSelectedItem(Locale.forLanguageTag(pref.get("InputLanguage","en-US")));
		localeOut.setSelectedItem(Locale.forLanguageTag(pref.get("OutputLanguage","zh-CN")));
		wordCache.setText(pref.get("WordCache",System.getProperty("user.home")+"/.sillytranslatecache"));
		baiduId.setText(pref.get("BaiduID",""));
		baiduSecret.setText(pref.get("BaiduSecret",""));
		youdaoId.setText(pref.get("YoudaoID",""));
		youdaoSecret.setText(pref.get("YoudaoSecret",""));
		yandexKey.setText(pref.get("YandexKey",""));
		try{
			dictionaryChooser.fromPaths(pref.get("Dictionary",""));
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
	}
	public TextTranslator getTranslator(){
		ArrayList<TextTranslator> translators=new ArrayList<>();
		if(simple.isSelected())
			translators.add(new SimpleTextTranslator());
		if(staged.isSelected()){
			Lex lex=simpleLex.isSelected()?new SimpleLex():
					(prefixLex.isSelected()?new PrefixLex(dictionaryChooser.getDictionary(),localeIn.getSelectedItem()):new JavaLex(localeIn.getSelectedItem()));
			WordTranslator wordTranslator=new WordTranslator(dictionaryChooser.getDictionary(),WordMemory.getWordMemory(wordCache.getText()));
			ArrayList<SentenceTranslatorEngine> sentenceTranslators=new ArrayList<>();
			if(naiveSentence.isSelected())
				sentenceTranslators.add(new NaiveTranslator((int)naiveLimit.getValue()));
			if(ruleSentence.isSelected())
				sentenceTranslators.add(new RuleBasedSentenceTranslator((int)ruleLimit.getValue()));
			SentenceTranslatorEngine sentenceEngine=new CombinedTranslator(sentenceTranslators.toArray(new SentenceTranslatorEngine[0]));
			SentenceTranslatorView sentenceTranslator=new SentenceTranslatorView(sentenceEngine);
			translators.add(new StagedTextTranslator(lex,wordTranslator,sentenceTranslator));
		}
		if(cloud.isSelected()){
			ArrayList<CloudTranslator> clouds=new ArrayList<>();
			if(baidu.isSelected())
				clouds.add(new BaiduTranslator(baiduId.getText(),baiduSecret.getText()));
			if(youdao.isSelected())
				clouds.add(new YoudaoTranslator(youdaoId.getText(),youdaoSecret.getText()));
			if(yandex.isSelected())
				clouds.add(new YandexTranslator(yandexKey.getText()));
			CloudTextTranslator cloudTextTranslator=new CloudTextTranslator(clouds.toArray(new CloudTranslator[0]));
			cloudTextTranslator.setTranslateDirection(localeIn.getSelectedItem(),localeOut.getSelectedItem());
			translators.add(cloudTextTranslator);
		}
		return new CombinedTextTranslator(translators.toArray(new TextTranslator[0]));
	}
}