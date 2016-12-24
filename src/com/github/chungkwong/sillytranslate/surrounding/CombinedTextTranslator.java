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
import java.util.concurrent.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CombinedTextTranslator extends JPanel implements TextTranslator{
	private static final String MAGIC="7617ccd567d77b740d321bcb0128d6ef";
	private final TextTranslator[] translators;
	private final JTextArea in=new JTextArea();
	private ExecutorService executor=null;
	private boolean resume;
	private DocumentTranslatorEngine callback;
	public CombinedTextTranslator(boolean resume,TextTranslator... translators){
		this.translators=translators;
		this.resume=resume;
		if(resume)
			executor=Executors.newSingleThreadExecutor();
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		JLabel org=new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("INPUT"));
		org.setAlignmentX(0);
		add(org);
		in.setEditable(false);
		in.setAlignmentX(0);
		add(in);
		for(TextTranslator translator:translators){
			JLabel tips=new JLabel(translator.getName()+"    "+translator.getUsage());
			tips.setAlignmentX(0);
			add(tips);
			JComponent translatorView=translator.getUserInterface();
			translatorView.setAlignmentX(0);
			add(translatorView);
		}
		JButton pause=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("PAUSE"));
		pause.addActionListener((e)->{
			executor=Executors.newSingleThreadExecutor();
			callback.textTranslated(MAGIC+in.getText());
		});
		pause.setAlignmentX(0);
		add(pause);
	}
	@Override
	public void translate(String text,DocumentTranslatorEngine callback){
		if(resume&&text.startsWith(MAGIC)){
			executor.shutdown();
			executor=null;
			resume=false;
			translate(text.substring(MAGIC.length()),callback);
			return;
		}
		if(executor!=null){
			executor.execute(()->callback.textTranslated(text));
		}else{
			this.callback=callback;
			in.setText(text);
			for(TextTranslator translator:translators){
				translator.translate(text,callback);
			}
		}
	}
	@Override
	public JComponent getUserInterface(){
		return this;
	}
	@Override
	public String getName(){
		return java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("INTEGRATED");
	}
	@Override
	public String getUsage(){
		return "";
	}
}
