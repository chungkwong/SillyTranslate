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
import java.util.logging.*;
import javax.xml.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class XMLTranslator implements DocumentTranslatorEngine{
	private XMLStreamReader in;
	private XMLStreamWriter out;
	private TextTranslator translator;
	private Runnable callback=()->{};;
	public XMLTranslator(){

	}
	@Override
	public void start(InputStream in,OutputStream out){
		try{
			XMLInputFactory factory=XMLInputFactory.newInstance();
			factory.setProperty("javax.xml.stream.isCoalescing",true);
			this.in=factory.createXMLStreamReader(in);
			this.out=XMLOutputFactory.newInstance().createXMLStreamWriter(out);
			this.out.writeStartDocument(this.in.getEncoding(),this.in.getVersion());
			textTranslated("");
		}catch(XMLStreamException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	@Override
	public void setOnFinished(Runnable callback){
		this.callback=callback;
	}
	@Override
	public void setTextTranslator(TextTranslator translator){
		this.translator=translator;
	}
	@Override
	public void textTranslated(String text){
		try{
			out.writeCharacters(text);
			while(in.hasNext()){
				switch(in.next()){
					case XMLStreamReader.ATTRIBUTE:
						for(int i=0;i<in.getAttributeCount();i++)
							if(in.getAttributeNamespace(i)==null)
								out.writeAttribute(in.getAttributeLocalName(i),in.getAttributeValue(i));
							else if(in.getPrefix()==null)
								out.writeAttribute(in.getAttributeNamespace(i),in.getAttributeLocalName(i),in.getAttributeValue(i));
							else
								out.writeAttribute(in.getAttributePrefix(i),in.getAttributeNamespace(i),
										in.getAttributeLocalName(i),in.getAttributeValue(i));
						break;
					case XMLStreamReader.CDATA:
						out.writeCData(in.getText());
						break;
					case XMLStreamReader.CHARACTERS:
						if(in.getText().matches("\\s*"))
							out.writeCharacters(in.getText());
						else{
							translator.translate(in.getText(),this);
							return;
						}
						break;
					case XMLStreamReader.COMMENT:
						out.writeComment(in.getText());
						break;
					case XMLStreamReader.DTD:
						out.writeDTD(in.getText());
						break;
					case XMLStreamReader.END_DOCUMENT:
						out.writeEndDocument();
						out.flush();
						callback.run();
						in.close();
						return;
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
						for(int i=0;i<in.getNamespaceCount();i++)
							out.writeNamespace(in.getNamespacePrefix(i),in.getNamespaceURI(i));
						for(int i=0;i<in.getAttributeCount();i++)
							if(in.getAttributeNamespace(i)==null)
								out.writeAttribute(in.getAttributeLocalName(i),in.getAttributeValue(i));
							else if(in.getPrefix()==null)
								out.writeAttribute(in.getAttributeNamespace(i),in.getAttributeLocalName(i),in.getAttributeValue(i));
							else
								out.writeAttribute(in.getAttributePrefix(i),in.getAttributeNamespace(i),
										in.getAttributeLocalName(i),in.getAttributeValue(i));
						break;
					case XMLStreamReader.START_ELEMENT:
						if(in.getNamespaceURI()!=null)
							if(in.getPrefix()!=null)
								out.writeStartElement(in.getPrefix(),in.getLocalName(),in.getNamespaceURI());
							else
								out.writeStartElement(in.getNamespaceURI(),in.getLocalName());
						else
							out.writeStartElement(in.getLocalName());
						for(int i=0;i<in.getNamespaceCount();i++)
							out.writeNamespace(in.getNamespacePrefix(i),in.getNamespaceURI(i));
						for(int i=0;i<in.getAttributeCount();i++)
							if(in.getAttributeNamespace(i)==null)
								out.writeAttribute(in.getAttributeLocalName(i),in.getAttributeValue(i));
							else if(in.getPrefix()==null)
								out.writeAttribute(in.getAttributeNamespace(i),in.getAttributeLocalName(i),in.getAttributeValue(i));
							else
								out.writeAttribute(in.getAttributePrefix(i),in.getAttributeNamespace(i),
										in.getAttributeLocalName(i),in.getAttributeValue(i));
						break;
				}
			}
		}catch(XMLStreamException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	@Override
	public String toString(){
		return java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("XML");
	}
	public static void main(String[] args) throws FileNotFoundException, IOException{
		String file="/etc/gtkmathview/gtkmathview.conf.xml";
		XMLTranslator t=new XMLTranslator();
		t.setTextTranslator(new TextTranslatorStub());
		t.start(new FileInputStream(file),System.out);
	}
}
