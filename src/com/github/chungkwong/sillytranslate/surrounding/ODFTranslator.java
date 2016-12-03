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
package com.github.chungkwong.sillytranslate.surrounding;
import java.io.*;
import java.util.zip.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ODFTranslator extends ZipDocumentTranslator{
	@Override
	protected DocumentTranslatorEngine checkForBaseEngine(ZipEntry entry){
		if(entry.getName().equals("content.xml"))
			return new XMLTranslator();
		else
			return null;
	}
	public static void main(String[] args)throws Exception{
		ODFTranslator docxTranslator=new ODFTranslator();
		docxTranslator.setOnFinished(()->System.exit(0));
		docxTranslator.setTextTranslator(new TextTranslatorStub());
		docxTranslator.start(new FileInputStream("/home/kwong/sysu_learning/政治课/11336019-陈颂光-读书报告.odt"),
				new FileOutputStream("/home/kwong/sysu_learning/政治课/11336019-陈颂光-读书报告_clone.odt"));
		//docxTranslator.start(new FileInputStream("/home/kwong/sysu_learning/misc/classlist.ods"),
		//		new FileOutputStream("/home/kwong/sysu_learning/misc/classlist_clone.ods"));
		//docxTranslator.start(new FileInputStream("/home/kwong/sysu_learning/政治课/ethic.odp"),
		//		new FileOutputStream("/home/kwong/sysu_learning/政治课/ethic_clone.odp"));
	}
	@Override
	public String toString(){
		return "Open Document(odt, odp, ods)";
	}
}
