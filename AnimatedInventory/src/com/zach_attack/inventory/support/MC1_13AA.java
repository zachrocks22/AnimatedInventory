package com.zach_attack.inventory.support;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.zach_attack.inventory.Main;

import net.minecraft.server.v1_13_R2.ChatMessageType;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;

public class MC1_13AA {
	static Main plugin = Main.getPlugin(Main.class);
	// ----- The following code is the property of extended_clip and has been used with permission.
	  public static void sendActionbar(Player p, String message)
	  {
		IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
		PacketPlayOutChat bar = new PacketPlayOutChat(icbc, ChatMessageType.GAME_INFO);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(bar);
	  }
		  // -- End of ActionAnnouncer Code
}
