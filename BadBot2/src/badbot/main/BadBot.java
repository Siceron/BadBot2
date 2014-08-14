package badbot.main;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BadBot extends JavaPlugin implements Listener {

	public JavaPlugin plugin;
	public String lastMessage;
	public int interval;
	public BukkitTask task;
	public boolean kick;
	public boolean mute;
	public List<String> blackList;
	
	public BadBot(){
		plugin = this;
	}
	
	@Override
    public void onEnable(){
		saveDefaultConfig();
		getLogger().info("[BadBot] Plugin : ON !");
		
		plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
		this.interval = getConfig().getInt("interval");
		this.kick = getConfig().getBoolean("kick");
		if(interval == 0){
			interval = 15;
		}
		mute = false;
		this.blackList = getConfig().getStringList("blacklist");
    }
	
	@Override
    public void onDisable(){
		getLogger().info("[BadBot] Plugin : OFF !");
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("badbot")) {
			if(sender instanceof Player) {
				Player p = (Player)sender;
				if(args.length == 0){
					p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.GREEN +" Auteur du plugin : Siceron");
				}
				else if(args.length == 1){
					if(p.hasPermission("badbot.perm")){
						if(args[0].equals("mute")){
							mute = !mute;
							if(mute){
								Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "[BadBot] " + ChatColor.RED + sender.getName() + " a mute le chat");
							}
							else{
								Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "[BadBot] " + ChatColor.RED + sender.getName() + " a unmute le chat");
							}
						}
					}
				}
				else if(args.length == 2){
					if(p.hasPermission("badbot.perm")){
						if(args[0].equals("interval")){
							try{
								interval = Integer.valueOf(args[1]);
								p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED + " Nouveau intervalle : "+interval);
							}
							catch (NumberFormatException e) {
								p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Veuillez entrer un nombre correct");
							}
							return true;
						}
						else if(args[0].equals("kick")){
							if(args[1].equals("true") || args[1].equals("false")){
								kick = Boolean.valueOf(args[1]);
								p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Kick : "+kick);
							}
							else{
								p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Veuillez entrer true ou false");
							}
							return true;
						}
					}
					else{
						p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Vous n'avez pas les permissions necessaires");
						return true;
					}
				}
				else{
					return false;
				}
			}
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e){
		Player player = e.getPlayer();
		if(!player.hasPermission("badbot.perm")){
			String newMessage = e.getMessage().toString();
			
			// Gestion du language
			if(newMessage != null){
				String messageTab[] = newMessage.split(" ");
				for(int i = 0 ; i<messageTab.length ; i++){
					for(String grosMot : blackList){
						if((messageTab[i].toLowerCase()).equals(grosMot.toLowerCase())){
							player.kickPlayer("[BadBot] Language");
							Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "[BadBot] "+ ChatColor.RED + player.getName() + " kick pour language");
						}
					}
				}
			}
			
			// Gestion du spam
			if(!newMessage.equalsIgnoreCase(this.lastMessage) || this.lastMessage == null){
				if(task != null){
					task.cancel();
				}
				this.lastMessage = newMessage;
				new Task().runTaskLater(plugin, interval * 20); // 20 ticks = 1 seconde si pas de lags
			}
			else{
				if(kick){
					player.kickPlayer("[BadBot] Spam");
				}
				else{
					e.setCancelled(true);
					player.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Attendez "+interval+" secondes avant d'envoyer le meme message !");
				}
			}
		}
	}
	
	@EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
        if (mute) {
            if (player.hasPermission("badbot.perm")) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Le chat a ete mute");
            }
        }
    }
	
	class Task extends BukkitRunnable{

		@Override
		public void run() {
			BadBot.this.lastMessage = null;
		}
		
	}
}
