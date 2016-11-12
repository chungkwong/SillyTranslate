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
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Diff{
	public static <T> List<T> findLCS(List<T> l,List<T> r){
		int m=l.size();
		int n=r.size();
		int[][] t=new int[m+1][n+1];
		for(int i=0;i<m;i++){
			for(int j=0;j<n;j++){
				if(l.get(i).equals(r.get(j))){
					t[i+1][j+1]=t[i][j]+1;
				}else{
					t[i+1][j+1]=Math.max(t[i][j+1],t[i+1][j]);
				}
			}
		}
		int len=t[m][n];
		ArrayList<T> lcs=new ArrayList<>(len);
		int i=m-1,j=n-1;
		while(len>0){
			if(t[i][j+1]==len){
				--i;
			}else if(t[i+1][j]==len){
				--j;
			}else{
				--len;
				lcs.add(l.get(i));
				--i;
				--j;
			}
		}
		Collections.reverse(lcs);
		return lcs;
	}
	public static void main(String[] args){
		Scanner in=new Scanner(System.in);
		while(in.hasNextLine()){
			String s1=in.nextLine();
			String s2=in.nextLine();
			System.out.println(toString(findLCS(toCodepointList(s1),toCodepointList(s2))));
		}
	}
	private static List<Integer> toCodepointList(String str){
		return str.codePoints().boxed().collect(Collectors.toList());
	}
	private static String toString(List<Integer> list){
		return list.stream().collect(StringBuilder::new,StringBuilder::appendCodePoint,StringBuilder::append).toString();
	}
}