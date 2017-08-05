# ChatMenuAPI
An API for making menus inside Minecraft's chat.
This API treats Minecraft's chat like a 2D grid, allowing you to position elements freely in chat.

## Preview
![](https://sparse.blue/files/k0ejrc.gif)

---

## Contents
* [ChatMenuAPI](#chatmenuapi)
  - [Preview](#preview)
  - [Contents](#contents)
  - [Usage](#usage)
    + [Setup](#setup)
    + [ChatMenu](#chatmenu)
    + [Element](#element)
    + [States](#states)
    + [Displaying](#displaying)
  - [Links](#links)

---

## Usage

### Setup
Add `ChatMenuAPI.jar` to your build path, then add it as a dependency in your `plugin.yml`:
```YAML
depend: [ChatMenuAPI]
```
### ChatMenu
To create a menu, just create a new instance of `ChatMenu`:
```Java
ChatMenu menu = new ChatMenu();
```
If you are not using this API just for chat formatting, it is recommended that you make the menu a pausing menu:
```Java
ChatMenu menu = new ChatMenu().pauseChat();
```
When this menu is sent to a player, it will automatically pause outgoing chat to that player so that the menu will not be interrupted.

**Warning:** If you make a menu pause chat, you need to add a way to close the menu!

### Element
Elements are the building blocks of menus. They are used to represent everything in a menu.
There are a few elements provided by default, you can view them by [clicking here](../master/src/me/tom/sparse/spigot/chat/menu/element).

Basic `TextElement`:
```Java
menu.addElement(new TextElement("Hello, world!", 10, 10));
```

Basic close button:
```Java
menu.addElement(new ButtonElement(x, y, ChatColor.RED+"[Close]", (p) -> {menu.close(p); return false;}));
```

All of the default elements require and X and Y in their constructor, 
these coordinates should be greater than or equal to 0 and less than 320 on the X axis and 20 on the Y axis.
The default Minecraft chat is 320 pixels wide and 20 lines tall.

### States
Most interactive elements have one or more `State` objects.

`State`s are used to store information about an `Element`, such as the current number in an `IncrementalElement`.

Every state can have a change callback to detect when it changes:
```Java
IncrementalElement incr = ...;
incr.value.onChange((s) -> {
	System.out.println("IncrementalElement changed! "+s.previous()+" -> "+s.current());
});
```

### Displaying
Once you've created your menu and added all the elements you want, now would probably be a good time to display it.
You can display a menu using `ChatMenu#openFor(Player player)`:
```Java
Player p = ...;
menu.openFor(p);
```

## Links
* [Download](https://www.spigotmc.org/resources/chatmenuapi.45144/)
* [JavaDoc](https://sparse.blue/docs/ChatMenuAPI/index.html)