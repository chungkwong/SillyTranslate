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
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Permutator<T>{
	private final List<T> org;
	private int[] curr,spare;
	public Permutator(List<T> org){
		this.org=org;
		curr=new int[org.size()];
		spare=new int[org.size()];
		for(int i=0;i<curr.length;i++)
			curr[i]=spare[i]=i;
	}
	public ArrayList<T> nextPermutation(){
		int n=curr.length,k=n-2;
		while(k>=0&&curr[k+1]<curr[k])
			--k;
		if(k==-1)
			return null;
		int l=k+1;
		int pivot=curr[k];
		while(l<n&&curr[l]>pivot)
			++l;
		--l;
		int j=0;
		for(;j<k;j++)
			spare[j]=curr[j];
		spare[k]=curr[l];
		for(int i=n-1;i>l;--i)
			spare[++j]=curr[i];
		spare[++j]=pivot;
		for(int i=l-1;i>k;--i)
			spare[++j]=curr[i];
		int[] tmp=curr;
		curr=spare;
		spare=tmp;
		ArrayList<T> nextperm=new ArrayList<>(n);
		for(int i=0;i<n;i++)
			nextperm.add(org.get(curr[i]));
		return nextperm;
	}
	public static int factorial(int n){
		int result=1;
		while(n>1)
			result*=n--;
		return result;
	}
	private static int compareId(Object o1,Object o2){
		return Integer.compare(System.identityHashCode(o1),System.identityHashCode(o2));
	}
	public static void main(String[] args){
		ArrayList<String> list=new ArrayList<>();
		//int n=Integer.valueOf(args[0]);
		Scanner in=new Scanner(System.in);
		while(in.hasNextLine()){
			String line=in.nextLine();
			if(line.isEmpty())
				break;
			list.add(line);
		}
		Permutator<String> p=new Permutator<>(list);
		int count=0;
		while(list!=null){
			System.out.println(list);
			list=p.nextPermutation();
			++count;
		}
		System.out.println(count);
	}
}
