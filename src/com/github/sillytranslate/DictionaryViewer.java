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
import com.github.sillytranslate.ui.*;
import java.awt.*;
import java.io.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DictionaryViewer extends JPanel{
	private static final int WORD_LIST_MAX_LENGTH=10;
	private final StardictDictionary dict;
	private final JTextField input=new JTextField();
	private final DefaultListModel<String> wordsModel=new DefaultListModel<>();
	private final JList words=new JList(wordsModel);
	private final JTextArea meaning=new JTextArea();
	private final JFileChooser fileChooser=new JFileChooser();
	public DictionaryViewer(StardictDictionary dict){
		this.dict=dict;
		setLayout(new BorderLayout());
		input.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e){
				inputUpdated();
			}
			@Override
			public void removeUpdate(DocumentEvent e){
				inputUpdated();
			}
			@Override
			public void changedUpdate(DocumentEvent e){
				inputUpdated();
			}
		});
		add(input,BorderLayout.NORTH);
		words.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e){
				wordUpdated();
			}
		});
		add(words,BorderLayout.WEST);
		meaning.setEditable(false);
		add(new JScrollPane(meaning),BorderLayout.CENTER);
		DefaultListModel<NavigableDictionary> dicts=new DefaultListModel<>();
		dicts.addElement(dict);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		add(new JScrollPane(new EditableList(dicts,this::loadDictionary),
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),BorderLayout.SOUTH);
	}
	private NavigableDictionary loadDictionary(){
		try{
			fileChooser.showOpenDialog(this);
			return new StardictDictionary(fileChooser.getSelectedFile());
		}catch(IOException ex){
			Logger.getLogger(DictionaryViewer.class.getName()).log(Level.SEVERE,null,ex);
			return null;
		}
	}
	private void inputUpdated(){
		String word=dict.getCurrentWord(input.getText());
		wordsModel.clear();
		for(int i=0;i<WORD_LIST_MAX_LENGTH&&word!=null;i++){
			wordsModel.addElement(word);
			word=dict.getNextWord(word);
		}
		wordUpdated();
		//words.setSelectedIndex(0);
		//input.requestFocusInWindow();
	}
	private void wordUpdated(){
		if(words.isSelectionEmpty()){
			if(!wordsModel.isEmpty())
				meaning.setText(dict.getMeaning(wordsModel.get(0)));
		}else
			meaning.setText(dict.getMeaning((String)words.getSelectedValue()));
	}
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws IOException {
		JFrame f=new JFrame("Dictionary Viewer");
		f.add(new DictionaryViewer(new StardictDictionary(new File("/home/kwong/projects/stardict-kdic-computer-gb-2.4.2"))));
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}