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
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.logging.*;
import java.util.zip.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PackageManager extends JPanel{
	private static final String URL="https://github.com/chungkwong/SillyTranslate/blob/master/PACKAGES";
	private static final File PATH=new File(System.getProperty("user.home"),".SillyTranslate");
	private static final Charset UTF8=Charset.forName("UTF-8");
	private final JPanel installed=new JPanel(new FlowLayout(FlowLayout.LEFT));
	private final JPanel downloadable=new JPanel(new FlowLayout(FlowLayout.LEFT));
	public PackageManager(){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		JLabel pkgLabel=new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("PACKAGE"));
		pkgLabel.setAlignmentX(0);
		add(pkgLabel);
		JLabel installedLabel=new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("INSTALLED"));
		installedLabel.setAlignmentX(0);
		add(installedLabel);
		listInstalled();
		installed.setAlignmentX(0);
		add(installed);
		JLabel downloadableLabel=new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("DOWNLOADABLE"));
		downloadableLabel.setAlignmentX(0);
		add(downloadableLabel);
		JButton refreshLabel=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("REFRESH"));
		refreshLabel.setAlignmentX(0);
		refreshLabel.addActionListener((e)->listDownloadable());
		add(refreshLabel);
		downloadable.setAlignmentX(0);
		add(downloadable);
	}
	public void listInstalled(){
		if(PATH.exists()){
			installed.removeAll();
			for(String name:PATH.list((dir,name)->name.endsWith(".xml"))){
				JButton button=new JButton(name);
				button.addActionListener((e)->{});
				installed.add(button);
			}
			validate();
		}
	}
	public void listDownloadable(){
		try(BufferedReader in=new BufferedReader(new InputStreamReader(new URL(URL).openStream(),UTF8))){
			downloadable.removeAll();
			String line;
			while((line=in.readLine())!=null){
				int sp=line.indexOf('\t');
				if(sp!=-1){
					String name=line.substring(0,sp);
					String url=line.substring(sp+1);
					JButton button=new JButton(name);
					button.addActionListener((e)->{
						download(url);
						listInstalled();
					});
					downloadable.add(button);
				}
			}
			validate();
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
	}
	public void download(String url){
		try(ZipInputStream in=new ZipInputStream(new BufferedInputStream(new URL(url).openStream()),UTF8)){
			ZipEntry entry;
			byte[] buf=new byte[4096];
			while((entry=in.getNextEntry())!=null){
				File file=new File(PATH,entry.getName());
				if(entry.isDirectory()){
					file.mkdirs();
				}else{
					FileOutputStream out=new FileOutputStream(file);
					int c;
					while((c=in.read(buf))!=-1)
						out.write(buf,0,c);
					out.close();
				}
			}
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
	}
	public static void main(String[] args){
		JFrame f=new JFrame();
		f.add(new PackageManager());
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}