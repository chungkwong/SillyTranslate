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
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Main extends JFrame{
	private static final String INPUT_CARD_NAME="input";
	private static final String OUTPUT_CARD_NAME="output";
	private final CardLayout card=new CardLayout();
	private final JFileChooser fileChooser=new JFileChooser();
	private final JTextArea out=new JTextArea();
	public Main() throws HeadlessException{
		super(java.util.ResourceBundle.getBundle("com/github/sillytranslate/Words").getString("TRANSLATOR"));
		setLayout(card);
		add(createInputCard(),INPUT_CARD_NAME);
		add(createOutputCard(),OUTPUT_CARD_NAME);
		card.show(getContentPane(),INPUT_CARD_NAME);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	private void startTranslation(String input){
		endTranslation(input);
	}
	private void endTranslation(String output){
		out.setText(output);
		card.show(getContentPane(),OUTPUT_CARD_NAME);
	}
	private JPanel createInputCard(){
		JPanel pane=new JPanel(new BorderLayout());
		JTextArea area=new JTextArea();
		pane.add(new JScrollPane(area),BorderLayout.CENTER);
		JButton ok=new JButton(java.util.ResourceBundle.getBundle("com/github/sillytranslate/Words").getString("START"));
		ok.addActionListener((e)->{
			startTranslation(area.getText());
			area.setText("");
		});
		pane.add(ok,BorderLayout.SOUTH);
		Box from=Box.createHorizontalBox();
		JButton fromClip=new JButton(java.util.ResourceBundle.getBundle("com/github/sillytranslate/Words").getString("FROM CLIPBOARD"));
		JButton fromFile=new JButton(java.util.ResourceBundle.getBundle("com/github/sillytranslate/Words").getString("FROM FILE"));
		JButton fromURL=new JButton(java.util.ResourceBundle.getBundle("com/github/sillytranslate/Words").getString("FROM URL"));
		fromClip.addActionListener((e)->{
			area.setText("");
			area.paste();
		});
		fromFile.addActionListener((e)->{
			if(fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
				try{
					area.read(new FileReader(fileChooser.getSelectedFile()),null);
				}catch(IOException ex){
					Logger.getLogger(Main.class.getName()).log(Level.SEVERE,null,ex);
				}
			}
		});
		fromURL.addActionListener((e)->{
			String url=JOptionPane.showInputDialog(java.util.ResourceBundle.getBundle("com/github/sillytranslate/Words").getString("ENTER THE URL:"));
			try{
				URLConnection conn=new URL(url).openConnection();
				String encode=conn.getContentEncoding();
				if(encode==null)
					encode="UTF-8";
				area.read(new InputStreamReader(conn.getInputStream()),encode);
			}catch(IOException ex){
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE,null,ex);
			}
		});
		from.add(fromClip);
		from.add(fromFile);
		from.add(fromURL);
		pane.add(from,BorderLayout.NORTH);
		return pane;
	}
	private JPanel createOutputCard(){
		JPanel pane=new JPanel(new BorderLayout());
		pane.add(new JScrollPane(out),BorderLayout.CENTER);
		JButton ok=new JButton(java.util.ResourceBundle.getBundle("com/github/sillytranslate/Words").getString("SAVE"));
		ok.addActionListener((e)->{
			if(fileChooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
				try{
					out.write(new FileWriter(fileChooser.getSelectedFile()));
				}catch(IOException ex){
					Logger.getLogger(Main.class.getName()).log(Level.SEVERE,null,ex);
				}
			}
		});
		pane.add(ok,BorderLayout.SOUTH);
		return pane;
	}
	public static void main(String[] args) {
		new Main();
	}
}