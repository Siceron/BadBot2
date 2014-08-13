package badbot.main;

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
	
	public BadBot(){
		plugin = this;
	}
	
	@Override
    public void onEnable(){
		saveDefaultConfig();
		getLogger().info("[BadBot] Plugin démarré !");
		
		plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
		this.interval = getConfig().getInt("interval");
		if(interval == 0){
			interval = 15;
		}
    }
	
	@Override
    public void onDisable(){
		
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("badbot")) {
			if(sender instanceof Player) {
				Player p = (Player)sender;
				if(args.length == 0){
					p.sendMessage(ChatColor.GREEN + "[BadBot] Auteur du plugin : Siceron");
				}
				else if(args.length == 2){
					if(p.hasPermission("badbot.perm")){
						if(args[0].equals("interval")){
							interval = Integer.valueOf(args[1]);
							p.sendMessage(ChatColor.RED + "[BadBot] Nouveau intervalle : "+interval);
							return true;
						}
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
			if(!newMessage.equalsIgnoreCase(this.lastMessage) || this.lastMessage == null){
				if(task != null){
					task.cancel();
				}
				this.lastMessage = newMessage;
				new Task().runTaskLater(plugin, interval * 20); // 20 ticks = 1 seconde si pas de lag
			}
			else{
				e.setCancelled(true);
				player.sendMessage(ChatColor.RED + "[BadBot] Attendez "+interval+" secondes avant d'envoyer le même message !");
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
