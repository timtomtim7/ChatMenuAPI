package me.tom.sparse.spigot.chat.util;

public interface NumberFormat
{
	NumberFormat NONE       = (v, l) -> "";
	NumberFormat FRACTION   = (v, l) -> String.format("%d/%d", v + 1, l);
	NumberFormat PERCENTAGE = (v, l) -> String.format("%.1f%%", ((double) (v + 1) / l) * 100);
	
	String format(int value, int length);
}
