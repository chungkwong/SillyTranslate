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
package com.github.chungkwong.sillytranslate.lex;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class E2CNormalizers{
	private static final WordNormalizer[] NORMALIZERS=new WordNormalizer[]{
		createOwnNormalizer(),createPluralNormalizer(),createContinuingTenseNormalizer(),
		createPastTenseNormalizer(),createPastPerfectTenseNormalizer()
	};
	public static WordNormalizer[] getNormalizers(){
		return NORMALIZERS;
	}
	private static WordNormalizer createPastTenseNormalizer(){
		return new PropertyNormalizer("PAST","","了");
	}
	private static WordNormalizer createPastPerfectTenseNormalizer(){
		return new PropertyNormalizer("PERFECT","","过");
	}
	private static WordNormalizer createContinuingTenseNormalizer(){
		return new PropertyNormalizer("CONTINUING","正在","");
	}
	private static WordNormalizer createPluralNormalizer(){
		return new PropertyNormalizer("PLURAL","","");
	}
	private static WordNormalizer createOwnNormalizer(){
		return new PropertyNormalizer("OWN","","的");
	}
}