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
package com.github.chungkwong.sillytranslate.ui;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.function.*;
import java.util.logging.*;
import javax.activation.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 * @param <E> Element type
 */
public class EditableList<E> extends JPanel{
	private final JButton addItem=new JButton(new ImageIcon(EditableList.class.getResource("add.png")));
	private final JButton removeItem=new JButton(new ImageIcon(EditableList.class.getResource("cross.png")));
	private final JButton upItem=new JButton(new ImageIcon(EditableList.class.getResource("arrow-up.png")));
	private final JButton downItem=new JButton(new ImageIcon(EditableList.class.getResource("arrow-down.png")));
	private final DefaultListModel<E> model;
	private final JList<E> list;
	private final Supplier<E> creator;
	public EditableList(DefaultListModel<E> model,Supplier<E> creator){
		setLayout(new BorderLayout());
		this.model=model;
		this.list=new JList(model);
		this.creator=creator;
		list.setDragEnabled(true);
		list.setDropMode(DropMode.INSERT);
		list.setTransferHandler(new ListTransferHandle());
		//list.setDropTarget(new DropTarget(this,new ListDropTargetListener()));
		Box bar=Box.createVerticalBox();
		upItem.setPreferredSize(new Dimension(upItem.getIcon().getIconWidth(),upItem.getIcon().getIconHeight()));
		addItem.addActionListener((e)->addItem());
		removeItem.addActionListener((e)->removeItem());
		upItem.addActionListener((e)->upItem());
		downItem.addActionListener((e)->downItem());
		bar.add(addItem);
		bar.add(removeItem);
		bar.add(upItem);
		bar.add(downItem);
		add(bar,BorderLayout.WEST);
		add(new JScrollPane(list),BorderLayout.CENTER);
	}
	public JList getJList(){
		return list;
	}
	private void addItem(){
		model.addElement(creator.get());
	}
	private void removeItem(){
		if(!list.isSelectionEmpty()){
			model.removeElementAt(list.getSelectedIndex());
		}
	}
	private void upItem(){
		int index=list.getSelectedIndex();
		if(index>0){
			E obj=model.getElementAt(index);
			model.removeElementAt(index);
			model.add(index-1,obj);
		}
	}
	private void downItem(){
		int index=list.getSelectedIndex();
		if(index>=0&&index<model.size()-1){
			E obj=model.getElementAt(index);
			model.removeElementAt(index);
			model.add(index+1,obj);
		}
	}
	private class ListTransferHandle extends TransferHandler{
		private DataFlavor localDataFlavor;
		private int index=-1;
		public ListTransferHandle(){
			super(null);
		}

		@Override
		public boolean canImport(TransferHandler.TransferSupport info){
			return info.getComponent()==list;
		}
		@Override
		protected Transferable createTransferable(JComponent c){
			index=list.getSelectedIndex();
			localDataFlavor=new ActivationDataFlavor(model.get(index).getClass(),"Object");
			return new DataHandler(model.get(index),localDataFlavor.getMimeType());
		}
		@Override
		public int getSourceActions(JComponent c){
			return TransferHandler.MOVE;
		}
		@Override
		public boolean importData(TransferHandler.TransferSupport info){
			if(!canImport(info))
				return false;
			try{
				E data=(E)info.getTransferable().getTransferData(localDataFlavor);
				int to=((JList.DropLocation)info.getDropLocation()).getIndex();
				model.add(to,data);
				if(to<index)
					++index;
				return true;
			}catch(UnsupportedFlavorException|IOException ex){
				Logger.getLogger(EditableList.class.getName()).log(Level.SEVERE,null,ex);
			}
			return false;
		}
		@Override
		protected void exportDone(JComponent c,Transferable data,int action){
			model.removeElementAt(index);
		}
	}
}
