package com.zach_attack.inventory;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.zach_attack.inventory.other.Updater;
import com.zach_attack.inventory.support.MC1_14;
import com.zach_attack.inventory.other.Metrics;
import com.zach_attack.inventory.Cooldowns;
import com.zach_attack.inventory.AnimatedInventoryAPI;


public class Main extends JavaPlugin implements Listener {
	
	public AnimatedInventoryAPI api;
/**        > ActionAnnouncer code was borrowed with permission to make this possible. 
	       > Please check out extended_clip's ActionAnnouncer plugin, it's amazing!              */
	
	public ArrayList<String> disabledclearworld = (ArrayList<String>) getConfig().getStringList("features.clearing.disabled-worlds");
	public ArrayList<String> disabledfortuneworld = (ArrayList<String>) getConfig().getStringList("features.fortunes.disabled-worlds");
	public static boolean outdatedplugin = false;
	public static String outdatedpluginversion = "0";
	
	  public void onEnable() { 
		  
		  if(!Bukkit.getBukkitVersion().contains("1.14") && !Bukkit.getVersion().contains("1.13")) {
			  getLogger().warning("ERROR: This version of AnimatedInventory ONLY supports 1.14.X, or 1.13.2. Please use AnimatedInventory v6.4 or below!"); 
			  Bukkit.getPluginManager().disablePlugin(this);
			  return;
		  }
		  
      api = new AnimatedInventoryAPI();
	  getConfig().options().copyDefaults(true);
	  getConfig().options().header("Thanks for downloading AnimatedInventory! When installing new updates \n to our plugin, check the console to see if you need to reset your config.yml\n\nFor slot numbers see: https://gamepedia.cursecdn.com/minecraft_gamepedia/b/b2/Items_slot_number.png");
	  saveConfig();
	  
	  configChecks();
	  
		 if (getServer().getPluginManager().isPluginEnabled("PerWorldInventory") && (getServer().getPluginManager().getPlugin("PerWorldInventory") != null))  {
			 getLogger().info("PerWorldInventory has been detected.");  
		  }
		 
	  // Big Thanks <3
		getLogger().info("Thanks to extended_clip for the additional help.");
		// ----------------
		
		  try {
						if(getConfig().getInt("config-version") != 17) {
							if(getConfig().getInt("config-version") <= 13) {
								getLogger().warning("WARNING: Your config is EXTREMELY old. A reset is recommended.");
								saveDefaultConfig();
						}
								  getLogger().info("We have added new features into your configuration.");
						   getConfig().set("features.clearing.enable-slot-skipping", false); 
						   getConfig().set("features.clearing.clear-armor", true);
							getConfig().set("config-version", 17);
							saveConfig();
						}
		  }catch(Exception e) {
			  getConfig().options().copyDefaults(true);
			  saveConfig();
			  getLogger().info("Hm. Something strange happend when trying to find your config version.");
			  reloadConfig();
		  }
		  
	        Clear.purgeCache();
		  
	        if(!getDescription().getVersion().toString().contains("pre")) {
		    if(getConfig().getBoolean("options.metrics")) {
			    Metrics metrics = new Metrics(this);
			    try {
		        metrics.addCustomChart(new Metrics.SimplePie("update_notifications", () -> {
		            if(getConfig().getBoolean("options.updates.notify")) {
		                return "Enabled";
		            } else {
		            	return "Disabled";
		            }
		        }));
			    }catch(Exception e) { 
			       getLogger().info("Error when setting Metrics, setting to false."); 
			       getConfig().set("options.metrics", false); 
			       saveConfig(); 
			       reloadConfig(); 
			       }
		 	    }}
		    
		    if(!getDescription().getVersion().contains("pre")) {
		  if (getConfig().getBoolean("options.updates.notify")) {
				    try
				    {
				    	new Updater(this).checkForUpdate();
				    }catch(Exception e) {
				    	getLogger().warning("There was an issue while trying to check for updates.");
				    }
		  } else {
			  outdatedplugin = false;
			  outdatedpluginversion = "0";
		  }}
		
		  Bukkit.getServer().getPluginManager().registerEvents(this, this);
		  
		  if(getConfig().getBoolean("options.debug")) {
		  Bukkit.getConsoleSender().sendMessage("[AnimatedInventory] [Debug] Using Minecraft Version: §a"  + Bukkit.getBukkitVersion().toString());
		  }
		  
		   disabledclearworld.clear();
		   disabledfortuneworld.clear();
			disabledclearworld.addAll(getConfig().getStringList("features.clearing.disabled-worlds"));
			disabledfortuneworld.addAll(getConfig().getStringList("features.fortunes.disabled-worlds"));
			
			  try {
	            	 MC1_14.emergencyRemove();
			  } catch(Exception e) {
				  getLogger().info("Error when trying to check players inventorys on disable event.");
				  if(getConfig().getBoolean("options.debug")) {
					  e.printStackTrace();
				  }}
				  
			getLogger().info("Done! Ready to initialize awesome.");
	 }
	    
	  public void onDisable() {
		  disabledclearworld.clear();
		   disabledfortuneworld.clear();
		   
		  if(!Bukkit.getBukkitVersion().contains("1.14") && !Bukkit.getBukkitVersion().contains("1.13")) {
			  getLogger().warning("Disabled because the server is not running 1.14.X, or 1.13.X.."); 
			  return;
		  }
		  
		  try {
            	 MC1_14.emergencyRemove();
		  } catch(Exception e) {
			  getLogger().info("Error when trying to check players inventorys on disable event.");
			  if(getConfig().getBoolean("options.debug")) {
				  e.printStackTrace();
			  }
		  }
		  
	        if(Bukkit.getOnlinePlayers().size() >= 1) {
	        for(final Player online:Bukkit.getServer().getOnlinePlayers())
            {
		     Cooldowns.removeAll(online.getPlayer());
            }}
	        
	  }
	  
	public void configChecks() {		  
		  if(getConfig().getBoolean("options.debug")) {
			  getLogger().info("[Debug] Running configChecks()");
		  }
		  
	   	  if(!getConfig().getBoolean("features.clearing.animations.Pane_Animation.enabled") &&
				  !getConfig().getBoolean("features.clearing.animations.Rainbow_Animation.enabled") &&
				  !getConfig().getBoolean("features.clearing.animations.Water_Animation.enabled") &&
				  !getConfig().getBoolean("features.clearing.animations.Explode_Animation.enabled") &&
				  !getConfig().getBoolean("features.clearing.animations.Fireball_Animation.enabled") &&
				  getConfig().getBoolean("features.clearing.enabled")) {
			  
			  getConfig().set("features.clearing.enabled", false);
			  saveConfig();
			  reloadConfig();
			  getLogger().info("All clear animations were turned off! Disabling Clearing...");
		  }
		  
	   	  if(getConfig().getBoolean("options.cooldowns.enabled") && 
	   (getConfig().getInt("options.cooldowns.time") == 0 || 
	   getConfig().getString("options.cooldowns.time").equalsIgnoreCase("none") ||
	   getConfig().getString("options.cooldowns.time").equalsIgnoreCase("disabled") ||
	   getConfig().getString("options.cooldowns.time").equalsIgnoreCase("off") ||
	   getConfig().getString("options.cooldowns.time") == null)) {
	   		getConfig().set("options.cooldowns.enabled", false);
	   		getConfig().set("options.cooldowns.time", 0);
			  saveConfig();
			  reloadConfig();
			  getLogger().info("Cooldown time was set to 0... Disabling Cooldowns.");
	   	  }
	   	  
		  if(getServer().getPluginManager().isPluginEnabled("Essentials") && (getServer().getPluginManager().getPlugin("Essentials") != null)) {
			  if(getConfig().getBoolean("override-clear-cmd")) {
			  getLogger().info("Found Essentials. /clear & /ci override enabled in config.");
			  } else {
			getLogger().info("Found Essentials. /clear & /ci override disabled in config.");	  
			  }
		  }
			if(getConfig().getBoolean("features.clearing.animations.Explode_Animation"))  {
		  if((getServer().getPluginManager().isPluginEnabled("ViaVersion") && (getServer().getPluginManager().getPlugin("ViaVersion") != null))) {
			  if(getConfig().getBoolean("features.clearing.animations.Explode_Animation")) {
				getLogger().info("HEADS UP: The TNT Animation has a known bug with ViaVersion. We've disabled this animation for you.");
			  getConfig().set("features.clearing.animations.Explode_Animation", false);
			  saveConfig();
			  reloadConfig();
			  }
		  } else
		 if (getServer().getPluginManager().isPluginEnabled("ProtocolSupport") && (getServer().getPluginManager().getPlugin("ProtocolSupport") != null))  {
			  if(getConfig().getBoolean("features.clearing.animations.Explode_Animation")) {
			 getLogger().info("HEADS UP: The TNT Animation has a known bug with ProtocolSupport. This bug can cause players to crash!");	  
			 getConfig().set("features.clearing.animations.Explode_Animation", false);
			 saveConfig();
			 reloadConfig();
			  }
			  }
			}
			
        	  if(getConfig().getBoolean("features.clearing.enable-slot-skipping")) {
                	ArrayList<Integer> skipslot = new ArrayList<Integer>();
                  	skipslot.addAll(getConfig().getIntegerList("features.clearing.skip-slots"));
                 if(skipslot.contains(0) || skipslot.contains(1) || skipslot.contains(2) || skipslot.contains(3) || skipslot.contains(4) || skipslot.contains(5) || skipslot.contains(6) || skipslot.contains(7) || skipslot.contains(8)) {
        		getLogger().warning("WARNING: You're choosing to skip a slot between 1-8 (Hotbar). These WONT be skipped because they are part of the animaions.");
        	  }
        	
        	if(skipslot.contains(22)) {
        		getLogger().warning("WARNING: Slot 22. This is where the AnimatedInventory token is, and WONT be skipped.");
        	  }
        	skipslot.clear();
        	}
	  }
	  
	  public void noPermission(CommandSender sender)
	  {
		  if(sender instanceof Player) {
			    Player p = (Player)sender;
			    bass(p);
		  }
	    Msgs.send(sender, getConfig().getString("messages.no-permission"));
	  }
	  
	  public void clearMessage(CommandSender sender) {
		  Player p = (Player)sender;
          Msgs.sendBar(p, getConfig().getString("features.clearing.progress-msg"));
	  }
	  
	    public void saveInv(Player p){
	        ItemStack[] inv = p.getInventory().getContents();
	        Cooldowns.inventories.put(p.getPlayer(), inv);
	        p.updateInventory();
	        if(getConfig().getBoolean("options.debug")) {
	        	getLogger().info("[Debug] Saving " + p.getName() + "'s inventory in system.");
	        }
	    }
	    public void loadInv(Player p){
	    p.getInventory().clear();
	        p.getInventory().setContents(Cooldowns.inventories.get(p.getPlayer()));
	        p.updateInventory();
	        if(getConfig().getBoolean("options.debug")) {
	        	getLogger().info("[Debug] Loading back " + p.getName() + "'s inventory from system.");
	        }
	    }
	    public void deleteInv(Player p){
	    	Cooldowns.inventories.remove(p);
	        if(getConfig().getBoolean("options.debug")) {
	        	getLogger().info("[Debug] Removing system data on " + p.getName() + "'s inventory.");
	        }
	    }
	    
	    public void errorMsg(Player p, int v, Exception e){
			  if(getConfig().getBoolean("options.debug")) {
	       getLogger().info("----------------------[ERROR]----------------------");
	       getLogger().info("Below is the error that occured:");
	       e.printStackTrace();
	       getLogger().info("Event was returned as: " + e.getMessage());
	       getLogger().info("--------------------[ERROR END]--------------------");
			  }
	       getLogger().info("Error! Couldn't play Animation #" + v + " to player: " + p.getName());
	       p.sendMessage("§c§lError. §fSomething went wrong here. §7Sorry!");
	       bass(p);
	    }

	  
	  public void cleardone(CommandSender sender) {
		    Player p = (Player)sender;
          Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
          {
            public void run()
            {
          	  doneding(p);
          	  burp(p);
          	  
          	  // Erasing of Inv
          	  if(getConfig().getBoolean("features.clearing.enable-slot-skipping")) {

          	ArrayList<Integer> skipslot = new ArrayList<Integer>();
          	skipslot.addAll(getConfig().getIntegerList("features.clearing.skip-slots"));
          	for (int i = 0; i < 36; i++) {
          	  if (!skipslot.contains(i)) {
          	  p.getInventory().setItem(i, new ItemStack(Material.AIR));
          	}}
          	
          	p.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
          	p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
          	
          	if(getConfig().getBoolean("features.clearing.clear-armor")) {
          		p.getInventory().setHelmet(new ItemStack(Material.AIR));
          		p.getInventory().setChestplate(new ItemStack(Material.AIR));
          		p.getInventory().setLeggings(new ItemStack(Material.AIR));
          		p.getInventory().setBoots(new ItemStack(Material.AIR));
          	}
    		   skipslot.clear();

              } else { // Slot Skipping OFF
            	  
              	if(!getConfig().getBoolean("features.clearing.clear-armor")) {
                  	for (int i = 0; i < 36; i++) {
                    	  p.getInventory().setItem(i, new ItemStack(Material.AIR));
                    }
              	} else {
                  p.getInventory().clear();
              }}
 
          	  
          	  Cooldowns.removeActive(p);
           		   Msgs.sendBar(p, getConfig().getString("features.clearing.done-msg"));
            }
          }, 4L);
	  }
	  
	    public void bass(Player sender) {
	        Player p = sender;
	        if(Bukkit.getBukkitVersion().contains("1.13") || Bukkit.getBukkitVersion().contains("1.14") || Bukkit.getBukkitVersion().contains("1.15")) {
	            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 5.0f, 1.3f);
	        } else {
	        	p.playSound(p.getLocation(), Sound.valueOf("BLOCK_NOTE_BASS"), 5.0F, 1.3F);
	        }
	    }
	    
	    public void despsound(Player sender) {
	        Player p = sender;
	        p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 5.0f, 0.4f);
	    }
	    
	    public void fireballshootsound(Player sender) {
	        Player p = sender;
	        p.playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 5.0f, 0.1f);
	    }
	    
	    public void tick(Player sender) {
	        Player p = sender;
	            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 5.0f, 2.0f);
	    }

	    public void doneding(Player sender) {
	        Player p = sender;
	        if(Bukkit.getBukkitVersion().contains("1.13") || Bukkit.getBukkitVersion().contains("1.14") || Bukkit.getBukkitVersion().contains("1.15")) {
	            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 2.0f);
	        } else {
	        	p.playSound(p.getLocation(), Sound.valueOf("BLOCK_NOTE_CHIME"), 1.0F, 2.0F);
	        }
	    }

	    public void clearingsound(Player sender) {
	        Player p = sender;
	            p.playSound(p.getLocation(), Sound.ENTITY_DOLPHIN_EAT, 1.0f, 0.1f);
	    }

	    public void burp(Player sender) {
	        Player p = sender;
	            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0f, 0.9f);
	    }

	    public void pop(Player sender) {
	        Player p = sender;
	            p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 2.0f, 2.0f);
	    }
	    
	    public void levelup(Player sender) {
	        Player p = sender;
	            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 2.0f);
	    }

	    public void tntmovesound(Player sender) {
	        Player p = sender;
	          p.playSound(p.getLocation(), Sound.ENTITY_MINECART_INSIDE, 1.0f, 1.4f);
	    }

	    public void tntmovesoundstop(Player sender) {
	        Player p = sender;
	            p.stopSound(Sound.ENTITY_MINECART_INSIDE);
	    }

	    public void tntplacesound(Player sender) {
	        Player p = sender;
	            p.playSound(p.getLocation(), Sound.BLOCK_GRASS_PLACE, 2.0f, 2.0f);
	    }

	    public void boomsound(Player sender) {
	        Player p = sender;
	            p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 2.0f);
	    }
	    
	    @EventHandler(priority = EventPriority.HIGHEST) //ovveride
	    public void onCommandPreProcess(PlayerCommandPreprocessEvent event){
	    if(getConfig().getBoolean("options.commands.clear-override")) {
	    		if(event.getMessage().toLowerCase().equalsIgnoreCase("/clear")){
	    		event.setCancelled(true);
	    		Bukkit.dispatchCommand(event.getPlayer(), "ai clear");
	    		return;
	    	}
	    	if(event.getMessage().toLowerCase().equalsIgnoreCase("/ci")){
	    		event.setCancelled(true);
	    		Bukkit.dispatchCommand(event.getPlayer(), "ai clear");
	    		return;
	    	}
	    	if(event.getMessage().toLowerCase().contains("/ci ")){
	    		event.setCancelled(true);
	    		Bukkit.dispatchCommand(event.getPlayer(), "ai clear " + event.getMessage().replace("/ci ", ""));
	    		return;
	    	}
	    	if(event.getMessage().toLowerCase().contains("/clear ")){
	    		event.setCancelled(true);
	    		Bukkit.dispatchCommand(event.getPlayer(), "ai clear " + event.getMessage().replace("/clear ", ""));
	    		return;
	    	}
	       }
	    }
	    
	    @EventHandler(priority = EventPriority.HIGHEST)
	    	public void onPickUp(EntityPickupItemEvent event) {
	    	if(!getConfig().getBoolean("features.prevent-pickup")) {
	    		return;
	    	}
	    	
	    	if(event.getEntity() instanceof Player) {
	    		Player p = (Player)event.getEntity();
	    	if(Cooldowns.activefortune.containsKey(p.getPlayer())) {
	    event.setCancelled(true);
	    return;
	    	} 
	    	if(Cooldowns.active.containsKey(p.getPlayer())) {
	    event.setCancelled(true);
	    return;
	    	}
	    	}
	    }
	    
	    @EventHandler(priority = EventPriority.HIGHEST)
	    public void onWater(PlayerBucketEmptyEvent event){
	    	if(!getConfig().getBoolean("features.prevent-place")) {
	    		return;
	    	}
	    	
	    	if(Cooldowns.activefortune.containsKey(event.getPlayer())) {
	    event.setCancelled(true);
	    return;
	    	} 
	    	if(Cooldowns.active.containsKey(event.getPlayer())) {
	    event.setCancelled(true);
	    return;
	    	}
	    }
	    
	    @EventHandler(priority = EventPriority.HIGH)
	    public void onInv(InventoryClickEvent e) {
	    Player p = (Player) e.getWhoClicked();
 	 if(getConfig().getBoolean("features.prevent-move")) {
	    if(Cooldowns.active.containsKey(p) || Cooldowns.activefortune.containsKey(p)) {
           e.setCancelled(true);
	    }// end of prevent move config
	      }// end of active key
	    }

	    @EventHandler(priority = EventPriority.HIGH)
	    public void onPlayerDropItem(PlayerDropItemEvent e){
	    	 Player p = (Player) e.getPlayer();
	    	 if(getConfig().getBoolean("features.prevent-drop")) {
	     if(Cooldowns.active.containsKey(p) || Cooldowns.activefortune.containsKey(p)) {
	    	e.setCancelled(true);
		    }
	    	 }
	    }
	    
		  @EventHandler
		    public void onPlayerDeath(PlayerDeathEvent event) {
			  if(getConfig().getBoolean("features.prevent-drop")) {
		    	if(Cooldowns.activefortune.containsKey(event.getEntity().getPlayer()) || Cooldowns.active.containsKey(event.getEntity().getPlayer())) {
		                for(ItemStack i : event.getDrops()) {
		                    i.setType(Material.AIR);
		                }
		                getLogger().info(event.getEntity().getPlayer().getName() + " died. Their drops were canceled!");
		                Msgs.send(event.getEntity().getPlayer(), getConfig().getString("messages.death"));
		       }
			  }
		    }
		  
		@EventHandler(priority = EventPriority.HIGH)
			public void onSlotChange(PlayerItemHeldEvent e) {
			 if(Cooldowns.active.containsKey(e.getPlayer()) || Cooldowns.activefortune.containsKey(e.getPlayer())) {
				 if(getConfig().getBoolean("features.prevemt-player-slot-changes")) {
				 e.setCancelled(true);
				 }
			 }
		}
	    
	    @EventHandler(priority = EventPriority.HIGH)
	    public void onLeave(PlayerQuitEvent e)
	    {    final Player player = e.getPlayer();

	     if(Cooldowns.active.containsKey(player)) {
	     	  player.getInventory().clear();
	     }
	     
	     if(Cooldowns.activefortune.containsKey(player)) {
	     	  loadInv(player.getPlayer());
	     	  deleteInv(player.getPlayer());
	     }
	     
	     Cooldowns.removeAll(player.getPlayer());
	    }// end of onPlayerGameLeave
	    
	    @EventHandler(priority = EventPriority.HIGH)
	    public void onPlayerPlaceBlock(BlockPlaceEvent e) {
	    	final Player player = e.getPlayer();
	    	if(getConfig().getBoolean("features.prevent-place")) {
	    	if(Cooldowns.active.containsKey(player) || Cooldowns.activefortune.containsKey(player)) {
	    		e.setCancelled(true);
	    	}
	    	}
	    }
	    
	    @EventHandler(priority = EventPriority.HIGH)
	    public void onPlayerBucketFill(PlayerBucketFillEvent e) {
	    	final Player player = e.getPlayer();
	    	if(Cooldowns.active.containsKey(player) || Cooldowns.activefortune.containsKey(player)) {
	    		e.setCancelled(true);
	    	}
	    }
	    
	    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args)
	    {   
	      if (cmd.getName().equalsIgnoreCase("animatedinventory") || (cmd.getName().equalsIgnoreCase("ai")) && args.length == 0) {
	    	  sender.sendMessage("");
	    	  sender.sendMessage("§b§lA§r§bnimated §f§lI§r§fnventory");
	    	 sender.sendMessage("§7/ai help §7§ofor commands & links.");
	    	 sender.sendMessage("");
	    	 if(sender instanceof Player) {
	    		 final Player p = (Player)sender;
	    	 pop(p);
	    	 }
	      }else
	      
	      if (cmd.getName().equalsIgnoreCase("animatedinventory") || (cmd.getName().equalsIgnoreCase("ai")) && args.length == 1) {
	    	if(args[0].length() > 1) {
	    	  if(args[0].equalsIgnoreCase("help")) {
		    	 sender.sendMessage("§8§m------------§r §b§lA§r§bnimated §f§lI§r§fnventory §8§m------------");
		    	 sender.sendMessage("§7/ai help §f- Shows this amazing help menu.");
		    	 if(getConfig().getBoolean("features.clearing.enabled")) {
		    		 if(sender.hasPermission("animatedinv.clear.others") || sender.isOp()) {
		    			 sender.sendMessage("§7/ai clear (player) §f- Shows an animation to clear inventories.");	  
		    		 } else {
		    			 sender.sendMessage("§7/ai clear §f- Shows an animation to clear your inventory.");
		    		 }
		    	 } else {
		         sender.sendMessage("§c/ai clear §f- Command has been disabled.");
		    	 }
		    	 if(getConfig().getBoolean("features.fortunes.enabled")) {
		    		 if(sender.hasPermission("animatedinv.fortune.others") || sender.isOp()) {
		    			 sender.sendMessage("§7/ai fortune (player) §f- Get yes/no answer in an inventory.");
		    		 } else {
		    			 sender.sendMessage("§7/ai fortune §f- Get yes/no answer in your inventory.");
		    		 }
		    	 } else {
		    	 sender.sendMessage("§c/ai fortune §f- Command has been disabled.");
		    	 }
		    	 if(getConfig().getBoolean("features.clearing.inv-backup.enabled") && (sender.hasPermission("animatedinv.clear.undo") || sender.isOp())) {
		    	 sender.sendMessage("§7/ai undoclear §f- Restores your inventory after a clear.");
		    	 }
		    	 sender.sendMessage("§7/ai version §f- Shows the version of this plugin.");
			    if (sender.hasPermission("animatedinv.admin") || sender.isOp()) {
		    	 sender.sendMessage("§7/ai reload §f- Reloads the config.yml.");
		    	 sender.sendMessage("§7/ai toggle §f- Enable/Disable fortune & clearing.");
		    	 if(getConfig().getBoolean("features.clearing.inv-backup.enabled")) {
		    	 sender.sendMessage("§7/ai purge §f- Purges any old cache.");
		    	 }
			    }
		    	 sender.sendMessage("§8§m------------------------------------------");
		    	 if(sender instanceof Player) {
		    		 final Player p = (Player)sender;
		    	 pop(p);
		    	 }
	    	}
	    	else if(args[0].equalsIgnoreCase("version")) {
	    		Msgs.send(sender, "&7You're currently running &f&lv" + getDescription().getVersion());
		    	 if(sender instanceof Player) {
		    		 final Player p = (Player)sender; 
	    		pop(p);
		    	 }
		    	 
	    	}
	    	else if(args[0].equalsIgnoreCase("purge")) {
	    		
	    		if(sender instanceof Player) {
	    			Player p = (Player)sender;
	    			
		    		if(!sender.hasPermission("animatedinv.admin") && !sender.isOp()) {
			    		noPermission(p);
		    			return true;
		    		}
		    		
	    		if(Cooldowns.filecooldown.containsKey(p.getPlayer())) {
	    			Msgs.send(sender, getConfig().getString("messages.backup-must-wait").replace("%number%", Integer.toString(getConfig().getInt("features.clearing.inv-backup.backup-cooldown"))));
	    			bass(p);
	    			return true;
	    		}}
	    		
	    		Msgs.send(sender, "&c&lCache Purged. &fAny old cache has been deleted.");
		    	 if(sender instanceof Player) {
		    		 final Player p = (Player)sender; 
	    		pop(p);
		    	 }
		    	 Clear.purgeCache();
	    	}
	    	else if(args[0].equalsIgnoreCase("undoclear")) {
	    		
	    		if(!(sender instanceof Player)) {
	    			Msgs.send(sender, getConfig().getString("messages.no-player"));
	    			return true;
	    		}
	    		
	    		Player p = (Player)sender; 
	    		if(!getConfig().getBoolean("features.clearing.inv-backup.enabled")) {
		    		bass(p);
			    	 Msgs.send(sender, getConfig().getString("messages.backups-disabled"));
	    			return true;
	    		}
	    		
	    		if(!sender.hasPermission("animatedinv.clear.undo") && !sender.isOp()) {
		    		noPermission(p);
	    			return true;
	    		}
	    		
	    		if(Cooldowns.active.containsKey(p.getPlayer())) {
	    			Msgs.send(sender, getConfig().getString("messages.backup-must-wait-clear"));
	    			bass(p);
	    			return true;
	    		}
	    		
	    		if(Cooldowns.activefortune.containsKey(p.getPlayer())) {
	    			Msgs.send(sender, getConfig().getString("messages.backup-must-wait-fortune"));
	    			bass(p);
	    			return true;
	    		}
	    		
	    		if(Cooldowns.filecooldown.containsKey(p.getPlayer())) {
	    			Msgs.send(sender, getConfig().getString("messages.backup-must-wait").replace("%number%", Integer.toString(getConfig().getInt("features.clearing.inv-backup.backup-cooldown"))));
	    			bass(p);
	    			return true;
	    		}
	    		
	    		try {
	    			File cache = new File(this.getDataFolder(), File.separator + "Cache");
	    			File f = new File(cache, File.separator + "" + p.getUniqueId().toString() + ".yml");
	    			if(f.exists()) {
	    				try {
	    		Clear.undoClear(p);
	    				}catch(Exception e) {
	    	    			Msgs.send(sender, getConfig().getString("messages.backup-error"));
	    	    			bass(p);
	    	    			getLogger().info("Hm. We were unable to restore " + p.getName() + "'s backup.");
	    	    			if(getConfig().getBoolean("options.debug")) {
	    	    				getLogger().info("[Debug] Error below: ------------------------------");
	    	    				e.printStackTrace();
	    	    				getLogger().info("[Debug] End of Error ------------------------------");
	    	    			}
	    				}
	    				FileConfiguration setcache = YamlConfiguration.loadConfiguration(f);
	    				long secondsAgo = Math.abs(((setcache.getLong("Last-Backup"))/1000) - (System.currentTimeMillis()/1000));
	    				if(secondsAgo < 60) {
	    	    Msgs.send(sender, getConfig().getString("messages.backup-restored").replace("%time%", Long.toString(secondsAgo) + "s"));
	    				} else if(secondsAgo < 3600) {
	    		Msgs.send(sender, getConfig().getString("messages.backup-restored").replace("%time%", Long.toString(secondsAgo/60) + "m"));			
	    				} else if(secondsAgo < 86400) {
	    		Msgs.send(sender, getConfig().getString("messages.backup-restored").replace("%time%", Long.toString(secondsAgo/3600) + "h"));			
	    				} else {
	    		Msgs.send(sender, getConfig().getString("messages.backup-restored").replace("%time%", Long.toString(secondsAgo/86400) + "d"));					
	    				}
	    		levelup(p);
	    			} else {
	    				Msgs.send(sender, getConfig().getString("messages.backup-no-file"));			
	    			bass(p);
	    			}
	    		} catch(Exception e) {
	    			Msgs.send(sender, getConfig().getString("messages.backup-error"));
	    			bass(p);
	    			getLogger().info("Hm. Something went wrong when trying to get " + p.getName() + "'s cache files.");
	    			if(getConfig().getBoolean("options.debug")) {
	    				getLogger().info("[Debug] Error below: ------------------------------");
	    				e.printStackTrace();
	    				getLogger().info("[Debug] End of Error ------------------------------");
	    			}
	    		}
		    	 
	    	} else if(args[0].equalsIgnoreCase("glitched")) {
			    if(!(sender instanceof Player)) {
			    	Msgs.send(sender, "&c&lSorry. &fOnly players can do this.");
			    	return true;
				}
			    
			    if(!sender.isOp()) {
			    	noPermission(sender);
			    	return true;
			    }
			    
				   final Player p = (Player)sender; 
			       pop(p);
			       Cooldowns.activefortune.remove(p);
			       Cooldowns.active.remove(p);
			       Cooldowns.cooldown.remove(p);
			    Msgs.send(sender, "&6&lGlitch Fixed. &fWe have tried to fix your sticky situation.");
	    	} else if(args[0].equalsIgnoreCase("debug")) {
			    if(!sender.isOp() && !sender.hasPermission("animatedinv.admin") && sender instanceof Player) {
			    	noPermission(sender);
			    	return true;
				}
			    if(sender instanceof Player) {
				   Player p = (Player)sender; 
			       pop(p);
			    }
			       if(getConfig().getBoolean("options.debug")) {
			       getConfig().set("options.debug", false);
			       saveConfig();
			       reloadConfig();
				    Msgs.send(sender, "&c&lDebug Off. &fWe have disabled debug mode.");
			       } else {
			       getConfig().set("options.debug", true);
			       saveConfig();
			       reloadConfig();
				    Msgs.send(sender, "&a&lDebug On. &fWe have enabled debug mode.");
				    if(getConfig().getBoolean("options.debug")) {
				    getLogger().info("[Debug] Now enabled via the /ai debug command.");
				    }
			       }
	    	} else if(args[0].equalsIgnoreCase("toggle")) {
	    		if(!sender.hasPermission("animatedinv.admin") && !sender.isOp() && sender instanceof Player) {
			        Player p = (Player)sender;
	    			noPermission(p);
	    			return true;
	    		}
	    		
	    		if(getConfig().getBoolean("features.clearing.enabled")) {
	    			getConfig().set("features.clearing.enabled", false);
	    			getConfig().set("features.fortunes.enabled", false);
	    			saveConfig();
	    			reloadConfig();
	    			configChecks();
	    			Msgs.send(sender, "&fYou have &c&lDISABLED &fclearing & fortunes.");
			    	 if(sender instanceof Player) {
					      Player p = (Player)sender;
				    		pop(p);
					    	 }
	    		} else {
	    			getConfig().set("features.clearing.enabled", true);
	    			getConfig().set("features.fortunes.enabled", true);
	    			saveConfig();
	    			reloadConfig();
	    			configChecks();
	    			if(!getConfig().getBoolean("features.clearing.enabled")) {
	    				Msgs.send(sender, "&c&lError! &fCouldn't re-enable clearing, are all clearing animations set to false?");
				    	 if(sender instanceof Player) {
						      Player p = (Player)sender;
					    		bass(p);
						    	 }
	    			} else {
	    			Msgs.send(sender, "&fYou have &a&lENABLED &fclearing & fortunes.");
			    	 if(sender instanceof Player) {
					      Player p = (Player)sender;
				    		pop(p);
					    	 }
	    			}
	    		}
	    	}
	    	else if(args[0].equalsIgnoreCase("reload")) {
		    	if (!sender.hasPermission("animatedinv.admin") || !sender.isOp()) {
			    	 if(sender instanceof Player) {
			        Player p = (Player)sender;
	    			noPermission(p);
			    	 return true;
			      }}
		    	
		    	 reloadConfig();
		    	 configChecks();
				   disabledclearworld.clear();
				   disabledfortuneworld.clear();
					disabledclearworld.addAll(getConfig().getStringList("features.clearing.disabled-worlds"));
					disabledfortuneworld.addAll(getConfig().getStringList("features.fortunes.disabled-worlds"));
		    	 Msgs.send(sender, getConfig().getString("messages.reload"));
		    	 if(getConfig().getBoolean("options.debug")) {
				   getLogger().info("[Debug] Disabled Clear Worlds: " + disabledclearworld.toString());
				   getLogger().info("[Debug] Disabled Fortune Worlds: " + disabledfortuneworld.toString());
		    	 }
		    	 if(sender instanceof Player) {
		    		Player p = (Player)sender;
		    	       levelup(p);
	    	  }
		    	 
	    	} else if(args[0].equalsIgnoreCase("fortune")) {
	    		if(!(sender instanceof Player)) {
	    			Msgs.send(sender, getConfig().getString("messages.no-player"));
	    		return true;
	    		}
	    			 Player p = (Player)sender;
	    			 
 		    		if (Cooldowns.active.containsKey(p) || Cooldowns.activefortune.containsKey(p)) {
 		    			return true;
 		    		}
	    			 
	    		if(!sender.hasPermission("animatedinv.fortune")) {
	    			noPermission(p);
	    			return true;
		    	}
	    		
	    		if (!getConfig().getBoolean("features.fortunes.enabled")) {
	    			bass(p);
	           		   Msgs.sendBar(p, getConfig().getString("messages.fortune-disabled"));
	          	  return true;
		    	    		}
		    	    			
		    	    	if(disabledfortuneworld.contains(p.getLocation().getWorld().getName())) {
		    	    		bass(p);
		    	    		Msgs.sendBar(sender, getConfig().getString("messages.fortune-world-disabled"));
		    	    		return true;
		    	    	}
		    	    		
		    	    	if(getConfig().getBoolean("features.fortunes.health-restriction.enabled")) {
		    	    	if(p.getHealth() < getConfig().getDouble("features.fortunes.health-restriction.min")) {
		    	    		bass(p);
		    	    		Msgs.sendBar(sender, getConfig().getString("messages.fortune-need-more-health").replace("%num%", Double.toString(getConfig().getDouble("features.fortunes.health-restriction.min")/2)));
		    	    	}}
		    	    		
		    	  	if (Cooldowns.cooldown.containsKey(p) && !Cooldowns.active.containsKey(p)) {
		    		          	Msgs.sendBar(p, getConfig().getString("options.cooldowns.msg").replace("%number%", Integer.toString(getConfig().getInt("options.cooldowns.time"))));
		    	    			bass(p);
		    	    			return true;
		    	    		}

		    	  	if(!Cooldowns.notHurt(p.getPlayer())) {
		    	  		Msgs.sendBar(p, getConfig().getString("messages.fortune-while-hurt"));
		    	  		bass(p);
		    	  		return true;
		    	  	}
		    	  	
		    		    			Cooldowns.activefortune.put(p, p.getName());
		    		    			if(getConfig().getBoolean("options.debug")) {
		    		    				getLogger().info("[Debug] Self induced fortune: " + p.getName());
		    		    			}
		    	     		    	 try {
		    	          		    	MC1_14.fortune(sender);
		    	          		    	 }catch(Exception e) { 
		    	          		    		 errorMsg(p, 10, e);
		    	          		    	 }	
	    	}
	    	else if(args[0].equalsIgnoreCase("clear")) {
	    		if(!(sender instanceof Player)) {
	    			Msgs.send(sender, getConfig().getString("messages.no-player"));
	    		return true;
	    		}
	    		
	    			Player p = (Player)sender;
	    			
	    	if (!p.hasPermission("animatedinv.clear")) {
	    		noPermission(p);
	    		return true;
	    	} 
	    	
	    	if (!getConfig().getBoolean("features.clearing.enabled")) {
	    			bass(p);
	           		  Msgs.sendBar(p, getConfig().getString("messages.clear-disabled"));
	          	  return true;
	    	}
	    	
	    		if (Cooldowns.cooldown.containsKey(p) && !Cooldowns.active.containsKey(p)) {
		            	  Msgs.sendBar(sender, getConfig().getString("options.cooldowns.msg").replace("%number%", Integer.toString(getConfig().getInt("options.cooldowns.time"))));
	    			bass(p);
	    			return true;
	    		}
	    			
	    			if(disabledclearworld.contains(p.getLocation().getWorld().getName())) {
	    				bass(p);
	    				Msgs.sendBar(sender, getConfig().getString("messages.clear-world-disabled"));
	    				return true;
	    			}
	    			
		    		if (!Cooldowns.active.containsKey(p) && !Cooldowns.activefortune.containsKey(p)) {
                          Clear.go(p);
		    		}
		    		
	    		} else {
	    		if(sender instanceof Player) {
	    			Player p = (Player)sender;
		    	bass(p);
	    		}
	    		Msgs.send(sender, getConfig().getString("messages.not-a-command").replace("%cmd%", args[0].toString()));
	    		  if(args[0].contains("undo")) {
	    		   Msgs.send(sender, getConfig().getString("messages.undo-suggestion"));
	    		  }
	    		}
	    	}
	      }else if(args.length == 2 && args[0].equalsIgnoreCase("undoclear")){
		    	
    		  if(sender instanceof Player) {
	    	  if(!sender.hasPermission("animatedinv.clear.undo.others") && !sender.isOp()) {
	    			  Player p = (Player)sender;
	    		  noPermission(p);
	    		  return true;
	    	  }}
    		  
	    	 if(!getConfig().getBoolean("features.clearing.inv-backup.enabled")) {
	    		  if(sender instanceof Player) {
    			  Player p = (Player)sender;
    		      bass(p);
	    		  }
	    		  Msgs.send(sender, getConfig().getString("messages.backups-disabled"));
    		  return true;
    	     }
	    	  
	          Player target = Bukkit.getServer().getPlayer(args[1]);
	    	  if (target == null)
	          {
	    		  if(sender instanceof Player) {
	    			  Player p = (Player)sender;
	           bass(p);
	    		  }
	           Msgs.send(sender, getConfig().getString("messages.not-online").replace("%player%", args[1].toString()));
	           return true;
	          } 
	    	  
	    	  if (Cooldowns.active.containsKey(target)) {
	    		  if(sender instanceof Player) {
	    			  Player p = (Player)sender;
		           bass(p);
	    		  }
		         Msgs.send(sender, getConfig().getString("messages.already-clearing").replace("%player%", args[1].toString()));	  
	            return true;
	    	  }
	    	  
	    	  if(Cooldowns.activefortune.containsKey(target)) {
	    		  if(sender instanceof Player) {
	    			  Player p = (Player)sender;
		           bass(p);
	    		  }
		          Msgs.send(sender, getConfig().getString("messages.already-getting-fortune").replace("%player%", args[1].toString())); 
	            return true;
	    	  } 
	    	  
		    		try {
		    			File cache = new File(this.getDataFolder(), File.separator + "Cache");
		    			File f = new File(cache, File.separator + "" + target.getUniqueId().toString() + ".yml");
		    			
		    			if(!f.exists()) {
		    				Msgs.send(sender, getConfig().getString("messages.backup-no-file-other").replace("%player%", target.getName()));
		    				if(sender instanceof Player) {
		    					Player p = (Player)sender;
		    			           bass(p);
		    				}
		    			    return true;
		    			}
		    				try {
		    		Clear.undoClear(target);
		    				}catch(Exception e) {
		    	    			Msgs.send(sender, getConfig().getString("messages.backup-error"));
		    	    			if(sender instanceof Player) {
		    	    				Player p = (Player)sender;
		    	    		     	bass(p);
		    	    			}
		    	    			getLogger().info("Hm. We were unable to restore " + target.getName() + "'s backup.");
		    	    			if(getConfig().getBoolean("options.debug")) {
		    	    				getLogger().info("[Debug] Error below: ------------------------------");
		    	    				e.printStackTrace();
		    	    				getLogger().info("[Debug] End of Error ------------------------------");
		    	    			}
		    				}
		    				FileConfiguration setcache = YamlConfiguration.loadConfiguration(f);
		    				long secondsAgo = Math.abs(((setcache.getLong("Last-Backup"))/1000) - (System.currentTimeMillis()/1000));
		    				if(secondsAgo < 60) {
		    	    Msgs.send(sender, getConfig().getString("messages.backup-restored-other").replace("%time%", Long.toString(secondsAgo) + "s").replace("%player%", target.getName()));
		    	    Msgs.send(target, getConfig().getString("messages.backup-restored-target").replace("%time%", Long.toString(secondsAgo) + "s").replace("%sender%", sender.getName()));
		    				} else if(secondsAgo < 3600) {
		    		Msgs.send(sender, getConfig().getString("messages.backup-restored-other").replace("%time%", Long.toString(secondsAgo/60) + "m").replace("%player%", target.getName()));
		    		Msgs.send(target, getConfig().getString("messages.backup-restored-target").replace("%time%", Long.toString(secondsAgo/60) + "m").replace("%sender%", sender.getName()));
		    				} else if(secondsAgo < 86400) {
		    		Msgs.send(sender, getConfig().getString("messages.backup-restored-other").replace("%time%", Long.toString(secondsAgo/3600) + "h").replace("%player%", target.getName()));			
		    		Msgs.send(target, getConfig().getString("messages.backup-restored-target").replace("%time%", Long.toString(secondsAgo/3600) + "h").replace("%sender%", sender.getName()));
		    				} else {
		    		Msgs.send(sender, getConfig().getString("messages.backup-restored-other").replace("%time%", Long.toString(secondsAgo/86400) + "d").replace("%player%", target.getName()));					
		    		Msgs.send(target, getConfig().getString("messages.backup-restored-target").replace("%time%", Long.toString(secondsAgo/86400) + "d").replace("%sender%", sender.getName()));
		    				}
		    				
		    				levelup(target);
		    				
		    				if(sender instanceof Player) {
		    					Player p = (Player)sender;
		    		            levelup(p);
		    				}
		    				
		    		} catch(Exception finalerr) {
    	    			if(sender instanceof Player) {
    	    				Player p = (Player)sender;
    	    		     	bass(p);
    	    			}
    	    			Msgs.send(sender, "&c&lHm. &fWe were not able to do that to &7" + target.getName());
    	    			if(getConfig().getBoolean("options.debug")) {
    	    				getLogger().info("[Debug] An error occured. See below: ------------------");
    	    				finalerr.printStackTrace();
    	    				getLogger().info("[Debug] End of Error. ---------------------------------");
    	    			}
	          }
	          return true;
	      }else if(args.length == 2 && args[0].equalsIgnoreCase("clear")){
	    	
	    	  if(!sender.hasPermission("animatedinv.clear.others") && !sender.isOp()) {
	    		  if(sender instanceof Player) {
	    			  Player p = (Player)sender;
	    		  noPermission(p);
	    		  } else {
	    			  Msgs.send(sender, "&cSorry, but CONSOLE is not allowed to clear others.");
	    		  }
	    		  return true;
	    	  }
	    	  
	          Player target = Bukkit.getServer().getPlayer(args[1]);
	    	  if (target == null)
	          {
	    		  if(sender instanceof Player) {
	    			  Player p = (Player)sender;
	           bass(p);
	    		  }
	           Msgs.send(sender, getConfig().getString("messages.not-online").replace("%player%", args[1].toString()));
	          } else if(Cooldowns.active.containsKey(target)) {
	    		  if(sender instanceof Player) {
	    			  Player p = (Player)sender;
		           bass(p);
	    		  }
		         Msgs.send(sender, getConfig().getString("messages.already-clearing").replace("%player%", args[1].toString()));	  
	          } else if(Cooldowns.activefortune.containsKey(target)) {
	    		  if(sender instanceof Player) {
	    			  Player p = (Player)sender;
		           bass(p);
	    		  }
		          Msgs.send(sender, getConfig().getString("messages.already-getting-fortune").replace("%player%", args[1].toString())); 
	          } else {
	    		  if(sender instanceof Player) {
	    			  Player p = (Player)sender;
	        	levelup(p);
	    		  }
	    		  Msgs.send(sender, getConfig().getString("messages.clear-other-success").replace("%player%", args[1].toString()));
	        	Clear.go(target);
	          }
	          return true;
	          
	      } else if(args.length == 2 && args[0].equalsIgnoreCase("fortune")) {
    		  if(sender instanceof Player) {
	    	  if(!sender.hasPermission("animatedinv.fortune.others") && !sender.isOp()) {
	    			  Player p = (Player)sender;
	    		  noPermission(p);
	    		  return true;
	    	  }}
	    	  
	    	  Player target = Bukkit.getServer().getPlayer(args[1]);
	          if (target == null)
	          {
	    		  if(sender instanceof Player) {
	    			  Player p = (Player)sender;
	           bass(p);
	    		  }
	           Msgs.send(sender, getConfig().getString("messages.not-online").replace("%player%", args[1].toString()));
	           return true;
	          } else if(Cooldowns.active.containsKey(target)) {
	    		  if(sender instanceof Player) {
	    			  Player p = (Player)sender;
		           bass(p);
	    		  }
		           Msgs.send(sender, getConfig().getString("messages.already-clearing").replace("%player%", args[1].toString()));
		           return true;
	          } else if(Cooldowns.activefortune.containsKey(target)) {
	    		  if(sender instanceof Player) {
	    			  Player p = (Player)sender;
		           bass(p);
	    		  }
		           Msgs.send(sender, getConfig().getString("messages.already-getting-fortune").replace("%player%", args[1].toString())); 
		           return true;
	          } else {
	    		  if(sender instanceof Player) {
	    			  Player p = (Player)sender;
	        	levelup(p);
	    		  }
	        	Msgs.send(sender, getConfig().getString("messages.fortune-other-success").replace("%player%", args[1].toString()));
		    	 try {
       		    		MC1_14.fortune(target);
       		    	Cooldowns.activefortune.put(target, target.getName());
       		    	 }catch(Exception e) { errorMsg(target, 10, e);}
	          }
	          return true;  
	      }else { 	    	  // too many args & not clear or fortune for players
    		  if(sender instanceof Player) {
    			  Player p = (Player)sender;
		      bass(p);
    		  }
		      Msgs.send(sender, "&cToo many args for: &f/" + cmd.getName() + " " + args[0]);    
	    }
		return false;
	  }
	    
	    @EventHandler
	    public void onDamage(EntityDamageEvent e) {
	    	if(e.getEntity() instanceof Player) {
	    		if(e.getCause() == DamageCause.FALL){
	    			return;
	    		}
	    		
	    		if(getConfig().getBoolean("features.fortunes.prevent-if-being-hurt")) {
	    		Player player = (Player)e.getEntity();
	    		Cooldowns.isBeinghurt.put(player.getPlayer(), System.currentTimeMillis());
	    		}
	    	}
	    }
	    
	    @EventHandler(priority = EventPriority.HIGHEST)
	    public void onWorld(PlayerChangedWorldEvent e) {
	    	
		      Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		          public void run()
		          { 
		  	    	final ItemStack token = new ItemStack(Material.getMaterial(getConfig().getString("features.clearing.token-item")));
				    ItemMeta tokenm = token.getItemMeta();
				    tokenm.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("features.clearing.token")));
				    token.setItemMeta(tokenm);
				    
		  	    	if(e.getPlayer().getInventory().contains(token)) {
		  	    		e.getPlayer().getInventory().clear();
		  	    		getLogger().info(e.getPlayer().getName() + " switched worlds during a clear and glitched their inventory. Clearing everything!");
		  	    	}
		          }
		        }, 25L);
	   
		      Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		          public void run()
		          {
					  final ItemStack one = new ItemStack(Material.LIME_CONCRETE);
					    ItemMeta onem = one.getItemMeta();
					    onem.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("features.fortunes.yes-block.name")));
					    one.setItemMeta(onem);
					    
						  final ItemStack two = new ItemStack(Material.RED_CONCRETE);
						    ItemMeta twom = two.getItemMeta();
						    twom.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("features.fortunes.no-block.name")));
						    two.setItemMeta(twom);
				   
		   		   if(e.getPlayer().getInventory().contains(one) && e.getPlayer().getInventory().contains(two)) {
					   e.getPlayer().getInventory().clear();
					   getLogger().info(e.getPlayer().getName() + "'s fortune is glitched because they switched inventories. Will clear fortune items if they are stuck!");
				   }
		          }
		        }, 25L);
	    }
	    
	    @EventHandler(priority = EventPriority.HIGH)
	    public void onJoin(PlayerJoinEvent e)
	    {    final Player player = e.getPlayer();
	    
	    // This is a dev-join message sent to me only. It's to help me understand which servers support my work <3
	      if (player.getUniqueId().toString().equals("6191ff85-e092-4e9a-94bd-63df409c2079")) {
	        player.sendMessage(ChatColor.GRAY + "This server is running " + ChatColor.WHITE + "AnimatedInventory " + ChatColor.GOLD + "v" + getDescription().getVersion() + ChatColor.GRAY + " for " + Bukkit.getBukkitVersion().replace("-SNAPSHOT", ""));
	      }
	   // I kindly ask you leave the above portion in ANY modification of this plugin. Thank You!

	      if(!getDescription().getVersion().toString().contains("pre")) {
	    if (getConfig().getBoolean("options.updates.notify") &&
	    	  (player.isOp()) || (player.hasPermission("animatedinv.admin"))) {
				 if(outdatedplugin) {
    	            	Msgs.sendPrefix(player, "&c&lOutdated Plugin. &7Using v" + getDescription().getVersion() + ", while the latest is &f&l" + outdatedpluginversion);
			 }
		   }}
	    	
	    }// end of onPlayerGameJoin
}