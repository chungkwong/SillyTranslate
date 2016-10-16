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
import java.io.*;
import java.util.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DiffViewer extends JPanel{
	private final JTextArea left=new JTextArea();
	private final JTextArea right=new JTextArea();
	public DiffViewer(){
		setLayout(new BorderLayout());
		JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(new JScrollPane(left));
		split.setRightComponent(new JScrollPane(right));
		add(split,BorderLayout.CENTER);
		JButton refresh=new JButton("Refresh");
		refresh.addActionListener((e)->refresh());
		add(refresh,BorderLayout.SOUTH);
	}
	private void refresh(){
		java.util.List<String> leftLines=Arrays.asList(left.getText().split("\n"));
		java.util.List<String> rightLines=Arrays.asList(right.getText().split("\n"));
		java.util.List<String> lcs=Diff.findLCS(leftLines,rightLines);
		StringBuilder lbuf=new StringBuilder();
		StringBuilder rbuf=new StringBuilder();
		int i=0,j=0,k=0;
		while(i<leftLines.size()&&j<rightLines.size()){
			if(i==leftLines.size()||(leftLines.get(i).equals(lcs.get(k))&&!rightLines.get(j).equals(lcs.get(k)))){
				lbuf.append('\n');
				rbuf.append(rightLines.get(j++));
			}else if(j==rightLines.size()||(rightLines.get(j).equals(lcs.get(k))&&!leftLines.get(i).equals(lcs.get(k)))){
				rbuf.append('\n');
				lbuf.append(leftLines.get(i++));
			}else{
				lbuf.append(leftLines.get(i++));
				rbuf.append(rightLines.get(j++));
			}
		}
		left.setText(lbuf.toString());
		right.setText(rbuf.toString());
	}
	public static void main(String[] args) throws IOException {
		JFrame f=new JFrame(java.util.ResourceBundle.getBundle("com/github/sillytranslate/Words").getString("DICTIONARY VIEWER"));
		f.add(new DiffViewer());
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
