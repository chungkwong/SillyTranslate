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
import com.github.chungkwong.sillytranslate.sentence.*;
import com.github.chungkwong.sillytranslate.ui.*;
import com.github.chungkwong.sillytranslate.util.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CloudTextTranslator extends JPanel implements TextTranslator{
	private final ArrayList<RealTimeTask<String>> output=new ArrayList<>();
	private DocumentTranslatorEngine callback;
	private Locale from;
	private Locale to;
	public CloudTextTranslator(CloudTranslator... translators){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		for(CloudTranslator translator:translators){
			JButton slogan=new JButton(translator.getSLOGAN());
			slogan.addActionListener((e)->{
				try{
					Desktop.getDesktop().browse(new URI(translator.getHOME()));
				}catch(IOException|URISyntaxException ex){
					Logger.getLogger(CloudTextTranslator.class.getName()).log(Level.SEVERE,null,ex);
				}
			});
			slogan.setFocusable(false);
			slogan.setAlignmentX(0);
			add(slogan);
			JTextArea area=new ActionTextArea((text)->callback.textTranslated(text));
			area.setAlignmentX(0);
			add(area);
			RealTimeTask<String> task=new RealTimeTask<>((text)->{
				area.setText(translator.translate(text,from,to,true));
			});
			new Thread(task).start();
			output.add(task);
		}
	}
	public void setTranslateDirection(Locale from,Locale to){
		this.from=from;
		this.to=to;
	}
	@Override
	public void translate(String text,DocumentTranslatorEngine callback){
		this.callback=callback;
		output.forEach((task)->task.summit(text));
	}
	@Override
	public JComponent getUserInterface(){
		return this;
	}
	@Override
	public String getName(){
		return java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("CLOUD");
	}
}
