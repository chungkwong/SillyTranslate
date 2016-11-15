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
package com.github.chungkwong.sillytranslate.util;
import java.util.function.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class RealTimeTask<K> implements Runnable{
	private K currentKey;
	private final Consumer<K> consumer;
	private Thread thread;
	public RealTimeTask(Consumer<K> consumer){
		this.consumer=consumer;
	}
	public synchronized void summit(K key){
		this.currentKey=key;
		if(thread==null){
			thread=new Thread(this);
			thread.start();
		}else
			thread.interrupt();
	}
	public synchronized void cancelAll(){
		this.currentKey=null;
		if(thread!=null){
			thread.interrupt();
			thread=null;
		}
	}
	@Override
	public void run(){
		while(true){
			K key=null;
			synchronized(this){
				key=currentKey;
			}
			try{
				if(key!=null)
					consumer.accept(key);
			}catch(Exception ex){
				Logger.getGlobal().throwing(null,null,ex);
			}
			synchronized(this){
				if(key==currentKey&&!Thread.interrupted())
					try{
						wait();
					}catch(InterruptedException ex){

					}
			}
		}
	}
}
