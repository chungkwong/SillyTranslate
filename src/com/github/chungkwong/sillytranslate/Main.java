/*
 * Copyright (C) 2016-2017 Chan Chung Kwong <1m02math@126.com>
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
import com.github.chungkwong.sillytranslate.surrounding.*;
import com.github.chungkwong.sillytranslate.ui.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.logging.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Main extends JFrame{
	private static final String INPUT_CARD_NAME="input";
	private static final String OUTPUT_CARD_NAME="output";
	private static final String PROCESS_CARD_NAME="process";
	private final CardLayout card=new CardLayout();
	private final JFileChooser fileChooser=new JFileChooser();
	private final JTextArea out=new JTextArea();
	private final Configure conf=new Configure();
	public Main() throws HeadlessException{
		super(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("TRANSLATOR"));
		setLayout(card);
		add(createInputCard(),INPUT_CARD_NAME);
		add(createOutputCard(),OUTPUT_CARD_NAME);
		card.show(getContentPane(),INPUT_CARD_NAME);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	private void startTranslation(String text,DocumentTranslatorEngine engine){
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		engine.setOnFinished(()->endTranslation(new String(out.toByteArray(),Charset.forName("UTF-8"))));
		startTranslation(new ByteArrayInputStream(text.getBytes(Charset.forName("UTF-8"))),out,engine,false);
	}
	private void startTranslation(InputStream in,DocumentTranslatorEngine engine) throws FileNotFoundException{
		if(fileChooser.getSelectedFile()!=null)
			fileChooser.setSelectedFile(new File(fileChooser.getSelectedFile().toString()+".new"));
		fileChooser.showSaveDialog(this);
		FileOutputStream out=new FileOutputStream(fileChooser.getSelectedFile());
		engine.setOnFinished(()->{
			try{
				out.close();
				card.show(getContentPane(),INPUT_CARD_NAME);
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			}
		});
		startTranslation(in,out,engine,false);
	}
	private void resumeTranslation(InputStream in,OutputStream out,DocumentTranslatorEngine engine){
		engine.setOnFinished(()->{
			try{
				out.close();
				card.show(getContentPane(),INPUT_CARD_NAME);
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			}
		});
		startTranslation(in,out,engine,true);
	}
	private void startTranslation(InputStream in,OutputStream out,DocumentTranslatorEngine engine,boolean resume){
		TextTranslator translator=conf.getTranslator(resume);
		add(new JScrollPane(translator.getUserInterface()),PROCESS_CARD_NAME);
		card.show(getContentPane(),PROCESS_CARD_NAME);
		engine.setTextTranslator(translator);
		engine.start(in,out);
	}
	private void endTranslation(String output){
		out.setText(output);
		card.show(getContentPane(),OUTPUT_CARD_NAME);
	}
	private JPanel createInputCard(){
		JPanel pane=new JPanel(new BorderLayout());
		Box steps=Box.createVerticalBox();
		Box step0=Box.createHorizontalBox();
		step0.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("STEP0")));
		JTextField name=new JTextField();
		name.setEditable(false);
		name.setDocument(conf.getNameDocument());
		step0.add(name);
		JButton setting=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("SETTINGS"));
		setting.addActionListener((e)->conf.setVisible(true));
		step0.add(setting);
		step0.setAlignmentX(0);
		steps.add(step0);
		Box step1=Box.createHorizontalBox();
		step1.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("STEP1")));
		JComboBox<DocumentTranslatorEngine> formats=new JComboBox<>(SillyTranslate.getDocumentTranslatorEngines().toArray(new DocumentTranslatorEngine[0]));
		step1.add(formats);
		step1.setAlignmentX(0);
		steps.add(step1);
		JPanel step2=new JPanel(new FlowLayout(FlowLayout.LEFT));
		step2.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("STEP2")));
		JButton fromFile=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("FROM FILE"));
		fromFile.addActionListener((e)->{
			if(fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
				try{
					startTranslation(new FileInputStream(fileChooser.getSelectedFile()),(DocumentTranslatorEngine)formats.getSelectedItem());
				}catch(IOException ex){
					Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
				}
			}
		});
		step2.add(fromFile);
		step2.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("OR")));
		JButton fromURL=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("FROM NETWORK"));
		fromURL.addActionListener((e)->{
			String url=JOptionPane.showInputDialog(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("ENTER THE URL:"));
			try{
				URLConnection conn=new URL(url).openConnection();
				startTranslation(conn.getInputStream(),(DocumentTranslatorEngine)formats.getSelectedItem());
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			}
		});
		step2.add(fromURL);
		step2.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("OR")));
		JButton resume=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("CONTINUE"));
		resume.addActionListener((e)->{
			if(fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
				try{
					File to=fileChooser.getSelectedFile();
					File old=File.createTempFile("partial","");
					Files.copy(to.toPath(),old.toPath(),StandardCopyOption.REPLACE_EXISTING);
					resumeTranslation(new FileInputStream(old),new FileOutputStream(to),(DocumentTranslatorEngine)formats.getSelectedItem());
				}catch(IOException ex){
					Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
				}
			}
		});
		step2.add(resume);
		step2.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("FROM_TEXT")));
		step2.setAlignmentX(0);
		steps.add(step2);
		pane.add(steps,BorderLayout.NORTH);
		JTextArea area=new ActionTextArea((text)->{
			startTranslation(text,(DocumentTranslatorEngine)formats.getSelectedItem());
		});
		pane.add(new JScrollPane(area),BorderLayout.CENTER);
		return pane;
	}
	private JPanel createOutputCard(){
		JPanel pane=new JPanel(new BorderLayout());
		pane.add(new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("RESULT")),BorderLayout.NORTH);
		pane.add(new JScrollPane(out),BorderLayout.CENTER);
		Box bar=Box.createHorizontalBox();
		JButton ok=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("SAVE"));
		ok.addActionListener((e)->{
			if(fileChooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
				try{
					out.write(new FileWriter(fileChooser.getSelectedFile()));
				}catch(IOException ex){
					Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
				}
			}
		});
		JButton restart=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("RESTART"));
		restart.addActionListener((e)->card.show(getContentPane(),INPUT_CARD_NAME));
		bar.add(ok);
		bar.add(restart);
		pane.add(bar,BorderLayout.SOUTH);
		return pane;
	}
	public static void main(String[] args) throws IOException{
		//Locale.setDefault(Locale.FRANCE);
		Logger.getGlobal().setLevel(Level.SEVERE);
		Logger.getGlobal().addHandler(OptionPaneHandler.INSTANCE);
		new Main();
	}
}