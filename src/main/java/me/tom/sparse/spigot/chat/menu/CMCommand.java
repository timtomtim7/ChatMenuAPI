package me.tom.sparse.spigot.chat.menu;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMCommand implements CommandExecutor
{
	CMCommand() {}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(!(sender instanceof Player))
			return true;
		
		if(args.length > 0)
		{
			String id = args[0];
			if(id.equalsIgnoreCase("close"))
			{
				ChatMenuAPI.setCurrentMenu((Player) sender, null);
				return true;
			}else if(args.length > 1)
			{
				int element;
				try
				{
					element = Integer.parseInt(args[1]);
				}catch(NumberFormatException e)
				{
					return true;
				}
				String[] elementArgs = new String[args.length - 2];
				System.arraycopy(args, 2, elementArgs, 0, elementArgs.length);
				ChatMenu menu = ChatMenuAPI.getMenu(id);
				if(menu == null || element < 0 || element >= menu.elements.size())
					return true;
				menu.edit((Player) sender, element, elementArgs);
			}
		}
		return true;
	}

//	private static Filter originalFilter;
//	private static PrintStream originalOut;
	
	static void setLoggerFilter()
	{
//		System.setOut(new PrintStream(new FilterOutputStream(originalOut = System.out) {
//			public void write(byte[] b) throws IOException
//			{
//				if(new String(b).contains("/cmapi"))
//					return;
//				super.write(b);
//			}
//		}));

//		Logger logger = Bukkit.getLogger();
//		originalFilter = logger.getFilter();
//		logger.setFilter(record -> !record.getMessage().contains("/cmapi"));
//		logger.setFilter(record -> false);
	}
	
	static void restoreLoggerFilter()
	{
//		System.setOut(originalOut);
//		Bukkit.getLogger().setFilter(originalFilter);
	}
	
}
