package me.tom.sparse.spigot.chat.util;

import java.util.Arrays;

public final class TextUtil
{
	public static String generateSpaces(int count)
	{
		char[] chars = new char[count];
		Arrays.fill(chars, ' ');
		return new String(chars);
	}
	
	
	private TextUtil() {}
}
