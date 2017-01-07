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
import java.util.*;
import java.util.logging.*;
import java.util.zip.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PackageManager extends JPanel{
	private static final String URL="https://raw.githubusercontent.com/chungkwong/SillyTranslate/master/PACKAGES";
	private static final File PATH=new File(System.getProperty("user.home"),".SillyTranslate");
	private static final Charset UTF8=Charset.forName("UTF-8");
	private final Box installed=Box.createVerticalBox();
	private final Box downloadable=Box.createVerticalBox();
	private final Configure conf;
	public PackageManager(Configure conf){
		super(new BorderLayout());
		this.conf=conf;
		Box oldBox=Box.createVerticalBox();
		Box newBox=Box.createVerticalBox();
		JLabel pkgLabel=new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("PACKAGE"));
		add(pkgLabel,BorderLayout.NORTH);
		JLabel installedLabel=new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("INSTALLED"));
		oldBox.add(installedLabel);
		listInstalled();
		oldBox.add(installed);
		JLabel downloadableLabel=new JLabel(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("DOWNLOADABLE"));
		newBox.add(downloadableLabel);
		JButton refreshLabel=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("REFRESH"));
		refreshLabel.addActionListener((e)->listDownloadable());
		newBox.add(refreshLabel);
		newBox.add(downloadable);
		add(oldBox,BorderLayout.WEST);
		add(newBox,BorderLayout.EAST);
	}
	public void listInstalled(){
		if(PATH.exists()){
			installed.removeAll();
			String[] confs=PATH.list((dir,name)->name.endsWith(".xml"));
			Arrays.sort(confs);
			for(String name:confs){
				JButton button=new JButton(name);
				button.addActionListener((e)->conf.importPref(new File(PATH,name)));
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
			revalidate();
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
}