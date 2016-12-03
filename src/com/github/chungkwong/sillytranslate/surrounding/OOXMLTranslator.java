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
import java.util.function.*;
import java.util.regex.*;
import java.util.zip.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class OOXMLTranslator extends ZipDocumentTranslator{
	private static final Predicate<String> PATTERN=Pattern.compile(
			"word/document\\.xml|.*sharedStrings\\.xml|ppt/slides/slide[0-9]+\\.xml").asPredicate();//ad-hoc
	//xl/worksheets/sheet[0-9]+\\.xml
	@Override
	protected DocumentTranslatorEngine checkForBaseEngine(ZipEntry entry){
		if(PATTERN.test(entry.getName()))
			return new XMLTranslator();
		else
			return null;
	}
	public static void main(String[] args)throws Exception{
		OOXMLTranslator docxTranslator=new OOXMLTranslator();
		docxTranslator.setOnFinished(()->System.exit(0));
		docxTranslator.setTextTranslator(new TextTranslatorStub());
		//docxTranslator.start(new FileInputStream("/home/kwong/sysu_learning/misc/20122.xlsx"),
		//		new FileOutputStream("/home/kwong/sysu_learning/misc/20122_clone.xlsx"));
		//docxTranslator.start(new FileInputStream("/home/kwong/sysu_learning/misc/classlist.xlsx"),
		//		new FileOutputStream("/home/kwong/sysu_learning/misc/classlist_clone.xlsx"));
		docxTranslator.start(new FileInputStream("/home/kwong/sysu_learning/中国近代经济史/中国近代经济史_教学安排.pptx"),
				new FileOutputStream("/home/kwong/sysu_learning/中国近代经济史/中国近代经济史_教学安排_clone.pptx"));
		//docxTranslator.start(new FileInputStream("/home/kwong/下载/20161119044225181.docx"),
		//		new FileOutputStream("/home/kwong/下载/20161119044225181_clone.docx"));
	}
	@Override
	public String toString(){
		return "OOXML(docx, pptx, xlsx)";
	}
}