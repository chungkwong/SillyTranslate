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
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Configure extends JFrame{
	private static final File BASE=new File(System.getProperty("user.home"),".SillyTranslate");
	private final Preferences pref=Preferences.userNodeForPackage(Configure.class);
	private final JTextField name=new JTextField();
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
	private final JRadioButton tuneWord=new JRadioButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("TUNE"));
	private final JCheckBox naiveSentence=new JCheckBox(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("BRUTE_FORCE"));
	private final JCheckBox ruleSentence=new JCheckBox(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("RULE BASED"));
	private final JCheckBox autoSentence=new JCheckBox(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("AUTO_SELECT"));
	private final JCheckBox autoLex=new JCheckBox(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("AUTO_SELECT"));
	private final JTextField wordCache=new JTextField();
	private final JTextField rules=new JTextField();
	private final JTextField yandexKey=new JTextField();
	private final JTextField baiduId=new JTextField();
	private final JTextField baiduSecret=new JTextField();
	private final JTextField youdaoId=new JTextField();
	private final JTextField youdaoSecret=new JTextField();
	private final JComboBox<String> prologEngine=new JComboBox<>(new String[]{"JPrologMin","yap","swipl","gprolog"});
	private final JSpinner naiveLimit=new JSpinner(new SpinnerNumberModel(6,0,Integer.MAX_VALUE,1));
	private final JSpinner ruleLimit=new JSpinner(new SpinnerNumberModel(6,0,Integer.MAX_VALUE,1));
	private final LocaleChooser localeIn=new LocaleChooser();
	private final LocaleChooser localeOut=new LocaleChooser();
	private final DictionaryChooser dictionaryChooser=new DictionaryChooser();
	private final JTextField pkgPlugin=new JTextField();
	private final JTextField initPlugin=new JTextField();
	public Configure(){
		super(ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("CONFIGURE"));
		Box box=Box.createVerticalBox();
		box.add(name);
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
		lexBox.add(autoLex);
		lexBox.setAlignmentX(0);
		box.add(lexBox);
		Box wordBox=Box.createHorizontalBox();
		wordBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("WORD:")));
		ButtonGroup wordType=new ButtonGroup();
		wordType.add(dictWord);
		wordBox.add(dictWord);
		wordType.add(tuneWord);
		wordBox.add(tuneWord);
		wordBox.setAlignmentX(0);
		box.add(wordBox);
		Box sentenceBox=Box.createHorizontalBox();
		sentenceBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("SENTENCE:")));
		sentenceBox.add(naiveSentence);
		sentenceBox.add(naiveLimit);
		sentenceBox.add(ruleSentence);
		sentenceBox.add(ruleLimit);
		sentenceBox.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("RULES_FILE")));
		sentenceBox.add(rules);
		sentenceBox.add(prologEngine);
		sentenceBox.add(autoSentence);
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
		Box plugin=Box.createHorizontalBox();
		plugin.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("PKG_PLUGIN")));
		plugin.add(pkgPlugin);
		plugin.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("INIT_PLUGIN")));
		plugin.add(initPlugin);
		plugin.setAlignmentX(0);
		box.add(plugin);
		Box control=Box.createHorizontalBox();
		JButton importPref=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("IMPORT"));
		importPref.addActionListener((e)->importPref());
		control.add(importPref);
		JButton exportPref=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("EXPORT"));
		exportPref.addActionListener((e)->exportPref());
		control.add(exportPref);
		control.setAlignmentX(0);
		box.add(control);
		PackageManager pkg=new PackageManager(this);
		pkg.setAlignmentX(0);
		box.add(pkg);
		load();
		add(new JScrollPane(box));
		setUndecorated(false);
		setType(Type.NORMAL);
		pack();
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		Runtime.getRuntime().addShutdownHook(new Thread(()->savePref()));
	}
	private void importPref(){
		JFileChooser jfc=new JFileChooser();
		if(jfc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
			importPref(jfc.getSelectedFile());
	}
	void importPref(File file){
		try(InputStream in=new FileInputStream(file)){
			pref.put("Name","");
			Preferences.importPreferences(in);
			pref.sync();
			load();
			if(name.getText().isEmpty())
				name.setText(file.getName().replace(".xml",""));
		}catch(IOException|InvalidPreferencesFormatException|BackingStoreException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
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
		pref.putBoolean("TuneTranslator",tuneWord.isSelected());
		pref.putBoolean("NaiveSentenceTranslator",naiveSentence.isSelected());
		pref.putBoolean("RuleBasedSentenceTranslator",ruleSentence.isSelected());
		pref.putBoolean("AutoSelectSentence",autoSentence.isSelected());
		pref.putBoolean("AutoSelectLex",autoLex.isSelected());
		pref.put("Name",name.getText());
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
		pref.put("RulesFile",rules.getText());
		pref.put("PrologEngine",prologEngine.getSelectedItem().toString());
		pref.put("Dictionary",dictionaryChooser.toPaths());
		pref.put("PackagePlugin",pkgPlugin.getText());
		pref.put("GlobalPlugin",initPlugin.getText());
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
		tuneWord.setSelected(pref.getBoolean("TuneTranslator",false));
		naiveSentence.setSelected(pref.getBoolean("NaiveSentenceTranslator",true));
		ruleSentence.setSelected(pref.getBoolean("RuleBasedSentenceTranslator",false));
		autoSentence.setSelected(pref.getBoolean("AutoSelectSentence",false));
		autoLex.setSelected(pref.getBoolean("AutoSelectLex",false));
		naiveLimit.setValue(pref.getInt("NaiveLimit",6));
		ruleLimit.setValue(pref.getInt("RuleLimit",6));
		rules.setText(pref.get("RulesFile",""));
		prologEngine.setSelectedItem(pref.get("PrologEngine","JPrologMin"));
		name.setText(pref.get("Name",""));
		localeIn.setSelectedItem(Locale.forLanguageTag(pref.get("InputLanguage","en-US")));
		localeOut.setSelectedItem(Locale.forLanguageTag(pref.get("OutputLanguage","zh-CN")));
		wordCache.setText(pref.get("WordCache",System.getProperty("user.home")+"/.sillytranslatecache"));
		baiduId.setText(pref.get("BaiduID",""));
		baiduSecret.setText(pref.get("BaiduSecret",""));
		youdaoId.setText(pref.get("YoudaoID",""));
		youdaoSecret.setText(pref.get("YoudaoSecret",""));
		yandexKey.setText(pref.get("YandexKey",""));
		pkgPlugin.setText(pref.get("PackagePlugin",""));
		initPlugin.setText(pref.get("GlobalPlugin",""));
		try{
			dictionaryChooser.fromPaths(pref.get("Dictionary",""));
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
	}
	public TextTranslator getTranslator(boolean resume){
		SillyTranslate.loadPlugIn(pkgPlugin.getText().split(":"));
		ArrayList<TextTranslator> translators=new ArrayList<>();
		Locale input=localeIn.getSelectedItem();
		Locale output=localeOut.getSelectedItem();
		if(simple.isSelected())
			translators.add(new SimpleTextTranslator());
		if(staged.isSelected()){
			Lex lex=simpleLex.isSelected()?new SimpleLex():
					(prefixLex.isSelected()?new PrefixLex(dictionaryChooser.getDictionary(),input):new JavaLex(input));
			AbstractLexView lexView;
			if(autoLex.isSelected()){
				lexView=new BypassLexView();
			}else{
				lexView=new LexEditor();
			}
			AbstractWordTranslator wordTranslator;
			if(tuneWord.isSelected()){
				wordTranslator=new TuneWordTranslator(dictionaryChooser.getDictionary(),WordMemory.getWordMemory(wordCache.getText()),input);
			}else{
				wordTranslator=new WordTranslator(dictionaryChooser.getDictionary(),WordMemory.getWordMemory(wordCache.getText()),input);
			}
			ArrayList<SentenceTranslatorEngine> sentenceTranslators=new ArrayList<>();
			if(ruleSentence.isSelected())
				if(prologEngine.getSelectedItem().equals("JPrologMin"))
					sentenceTranslators.add(new RuleBasedSentenceTranslator((int)ruleLimit.getValue(),resolveFile(rules.getText()),output));
				else
					sentenceTranslators.add(new ExternalSentenceTranslator((int)ruleLimit.getValue(),resolveFile(rules.getText()),output,prologEngine.getSelectedItem().toString()));
			if(naiveSentence.isSelected())
				sentenceTranslators.add(new NaiveTranslator((int)naiveLimit.getValue(),output));
			SentenceTranslatorEngine sentenceEngine=new IntegratedSentenceTranslator(sentenceTranslators.toArray(new SentenceTranslatorEngine[0]));
			SentenceTranslatorView sentenceTranslator=new SentenceTranslatorView(sentenceEngine,autoSentence.isSelected(),output);
			translators.add(new StagedTextTranslator(lex,lexView,wordTranslator,sentenceTranslator));
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
			cloudTextTranslator.setTranslateDirection(input,output);
			translators.add(cloudTextTranslator);
		}
		return new CombinedTextTranslator(resume,translators.toArray(new TextTranslator[0]));
	}
	Document getNameDocument(){
		return name.getDocument();
	}
	public String getName(){
		return name.getText();
	}
	public Locale getInputLocale(){
		return localeIn.getSelectedItem();
	}
	public Locale getOutputLocale(){
		return localeIn.getSelectedItem();
	}
	public NavigableDictionary getDictionary(){
		return dictionaryChooser.getDictionary();
	}
	public static File resolveFile(String path){
		File file=new File(path);
		return file.isAbsolute()?file:new File(BASE,path);
	}
}