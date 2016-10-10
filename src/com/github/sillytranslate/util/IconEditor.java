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
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class IconEditor extends JPanel{
	private final int width,height;
	private JColorChooser colorChooser=new JColorChooser(Color.WHITE);
	private final JButton[][] pixels;
	public IconEditor(){
		width=Integer.parseInt(JOptionPane.showInputDialog("Width:"));
		height=Integer.parseInt(JOptionPane.showInputDialog("Height:",Integer.toString(width)));
		pixels=new JButton[HEIGHT][WIDTH];
		setLayout(new BorderLayout());
		JPanel dataArea=new JPanel(new GridLayout(HEIGHT,WIDTH));
		for(int i=0;i<HEIGHT;i++)
			for(int j=0;j<WIDTH;j++){
				pixels[i][j]=new JButton();
				pixels[i][j].setBackground(Color.WHITE);
				dataArea.add(pixels[i][j]);
			}
		add(dataArea,BorderLayout.CENTER);
		add(colorChooser,BorderLayout.EAST);
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