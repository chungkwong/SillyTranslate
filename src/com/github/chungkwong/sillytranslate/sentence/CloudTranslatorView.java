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
package com.github.chungkwong.sillytranslate.sentence;
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
public class CloudTranslatorView extends JPanel{
	private final JTextField input=new JTextField();
	private final ArrayList<RealTimeTask<String>> output=new ArrayList<>();
	public CloudTranslatorView(CloudTranslator... translators){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(input);
		for(CloudTranslator translator:translators){
			JButton slogan=new JButton(translator.getSLOGAN());
			slogan.addActionListener((e)->{
				try{
					Desktop.getDesktop().browse(new URI(translator.getHOME()));
				}catch(IOException|URISyntaxException ex){
					Logger.getLogger(CloudTranslatorView.class.getName()).log(Level.SEVERE,null,ex);
				}
			});
			slogan.setAlignmentX(0);
			add(slogan);
			JTextArea area=new JTextArea();
			add(area);
			RealTimeTask<String> task=new RealTimeTask<>((text)->{
				area.setText(translator.translate(text,Locale.ENGLISH,Locale.CHINA));
			});
			new Thread(task).start();
			output.add(task);
		}
		input.addActionListener((e)->{
			output.forEach((task)->task.summit(input.getText()));
		});
	}
	public static void main(String[] args){
		JFrame f=new JFrame("Cloud translator");
		f.add(new JScrollPane(new CloudTranslatorView(new BaiduTranslator(),new YandexTranslator())));
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
