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
package com.github.sillytranslate.surrounding;
import java.io.*;
import java.util.logging.*;
import javax.xml.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class HTMLTranslator implements DocumentTranslatorEngine{
	private XMLStreamReader in;
	private XMLStreamWriter out;
	private TextTranslator translator;
	public HTMLTranslator(){

	}
	@Override
	public void start(InputStream in,OutputStream out){
		try{
			XMLInputFactory factory=XMLInputFactory.newInstance();
			factory.setProperty("javax.xml.stream.isCoalescing",true);
			this.in=factory.createXMLStreamReader(in);
			this.out=XMLOutputFactory.newInstance().createXMLStreamWriter(out);
			textTranslated("");
		}catch(XMLStreamException ex){
			Logger.getLogger(HTMLTranslator.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	@Override
	public void setTextTranslator(TextTranslator translator){
		this.translator=translator;
	}
	@Override
	public void textTranslated(String text){
		try{
			while(in.hasNext()){
				switch(in.next()){
					case XMLStreamReader.ATTRIBUTE:
						for(int i=0;i<in.getAttributeCount();i++)
							out.writeAttribute(in.getAttributePrefix(i),in.getAttributeNamespace(i),
									in.getAttributeLocalName(i),in.getAttributeValue(i));
						break;
					case XMLStreamReader.CDATA:
						out.writeCData(in.getText());
						break;
					case XMLStreamReader.CHARACTERS:
						translator.translate(in.getText(),this);
						break;
					case XMLStreamReader.COMMENT:
						out.writeComment(in.getText());
						break;
					case XMLStreamReader.DTD:
						out.writeDTD(in.getText());
						break;
					case XMLStreamReader.END_DOCUMENT:
						out.writeEndDocument();
						break;
					case XMLStreamReader.END_ELEMENT:
						out.writeEndElement();
						break;
					case XMLStreamReader.ENTITY_DECLARATION:

						break;
					case XMLStreamReader.ENTITY_REFERENCE:
						out.writeEntityRef(in.getText());
						break;
					case XMLStreamReader.NAMESPACE:
						for(int i=0;i<in.getNamespaceCount();i++)
							out.writeNamespace(in.getNamespacePrefix(i),in.getNamespaceURI(i));
						break;
					case XMLStreamReader.NOTATION_DECLARATION:

						break;
					case XMLStreamReader.PROCESSING_INSTRUCTION:
						if(in.getPIData()!=null)
							out.writeProcessingInstruction(in.getPITarget(),in.getPIData());
						else
							out.writeProcessingInstruction(in.getPITarget());
						break;
					case XMLStreamReader.SPACE:
						out.writeCharacters(in.getText());
						break;
					case XMLStreamReader.START_DOCUMENT:
						out.writeStartDocument(in.getEncoding(),in.getVersion());
						break;
					case XMLStreamReader.START_ELEMENT:
						if(in.getNamespaceURI()!=null)
							if(in.getPrefix()!=null)
								out.writeStartElement(in.getPrefix(),in.getLocalName(),in.getNamespaceURI());
							else
								out.writeStartElement(in.getNamespaceURI(),in.getLocalName());
						else
							out.writeStartElement(in.getLocalName());
						break;
				}
			}
			out.flush();
		}catch(XMLStreamException ex){
			Logger.getLogger(HTMLTranslator.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	public static void main(String[] args) throws FileNotFoundException, IOException{
		String file="/etc/gtkmathview/gtkmathview.conf.xml";
		HTMLTranslator t=new HTMLTranslator();
		t.setTextTranslator((text,callback)->{
			callback.textTranslated("#"+text+"#");
		});
		t.start(new FileInputStream(file),System.out);
	}
}
