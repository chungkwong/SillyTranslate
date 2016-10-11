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

package com.github.sillytranslate.util;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.logging.*;
import javax.imageio.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class IconEditor extends JPanel{
	private final int width,height;
	private static final Color DEFAULT_COLOR=new Color(255,255,255,0);
	private JColorChooser colorChooser=new JColorChooser(DEFAULT_COLOR);
	private final JButton[][] pixels;
	public IconEditor(){
		width=Integer.parseInt(JOptionPane.showInputDialog("Width:"));
		height=Integer.parseInt(JOptionPane.showInputDialog("Height:",Integer.toString(width)));
		pixels=new JButton[height][width];
		setLayout(new BorderLayout());
		JPanel dataArea=new JPanel(new GridLayout(height,width));
		for(int i=0;i<height;i++)
			for(int j=0;j<width;j++){
				pixels[i][j]=new JButton();
				pixels[i][j].setBackground(DEFAULT_COLOR);
				pixels[i][j].setActionCommand(i+":"+j);
				pixels[i][j].addActionListener((e)->{
					String[] location=e.getActionCommand().split(":");
					pixels[Integer.parseInt(location[0])][Integer.parseInt(location[1])].setBackground(colorChooser.getColor());
				});
				dataArea.add(pixels[i][j]);
			}
		add(new JScrollPane(dataArea),BorderLayout.CENTER);
		add(colorChooser,BorderLayout.EAST);
		JButton save=new JButton("Save");
		save.addActionListener((e)->save());
		add(save,BorderLayout.SOUTH);
	}
	private void save(){
		JFileChooser fileChooser=new JFileChooser();
		if(fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
			File file=fileChooser.getSelectedFile();
			BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
			for(int i=0;i<height;i++)
				for(int j=0;j<width;j++)
					image.setRGB(j,i,pixels[i][j].getBackground().getRGB());
			try{
				String name=file.getName();
				ImageIO.write(image,name.substring(name.lastIndexOf('.')+1),file);
			}catch(IOException ex){
				Logger.getLogger(IconEditor.class.getName()).log(Level.SEVERE,null,ex);
			}
		}
	}
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		JFrame f=new JFrame("Icon Editor");
		f.add(new IconEditor());
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}