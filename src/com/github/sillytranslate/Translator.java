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
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Translator extends JPanel{
	public Translator(){
		setLayout(new BorderLayout());
		JSplitPane contentPanel=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JTextArea from=new JTextArea();
		JTextArea to=new JTextArea();
		contentPanel.setLeftComponent(new JScrollPane(from));
		contentPanel.setRightComponent(new JScrollPane(to));
		add(contentPanel,BorderLayout.CENTER);

	}
	public static void main(String[] args) throws IOException {
		JFrame f=new JFrame("Translator");
		f.add(new Translator());
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
