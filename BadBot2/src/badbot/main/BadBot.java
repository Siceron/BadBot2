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
		blackList = getConfig().getStringList("blacklist");
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
					return true;
				}
				else if(args.length == 1){
					if(args[0].equals("mute")){
						if(p.hasPermission("badbot.mute")){
							mute = !mute;
							if(mute){
								Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "[BadBot] " + ChatColor.RED + sender.getName() + " a mute le chat");
								return true;
							}
							else{
								Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "[BadBot] " + ChatColor.RED + sender.getName() + " a unmute le chat");
								return true;
							}
						}
						else{
							p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Vous n'avez pas les permissions necessaires");
							return true;
						}
					}
					else{
						p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" La commande /badbot"+args[0]+" n'existe pas");
						return true;
					}
				}
				else if(args.length == 2){
					if(args[0].equals("interval")){
						if(p.hasPermission("badbot.spam")){
							try{
								interval = Integer.valueOf(args[1]);
								p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED + " Nouveau intervalle : "+interval);
							}
							catch (NumberFormatException e) {
								p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Veuillez entrer un nombre correct");
							}
							return true;
						}
						else{
							p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Vous n'avez pas les permissions necessaires");
							return true;
						}
					}
					else if(args[0].equals("kick")){
						if(p.hasPermission("badbot.spam")){
							if(args[1].equals("true") || args[1].equals("false")){
								kick = Boolean.valueOf(args[1]);
								p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Kick : "+kick);
							}
							else{
								p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Veuillez entrer true ou false");
							}
							return true;
						}
						else{
							p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Vous n'avez pas les permissions necessaires");
							return true;
						}
					}
					else if(args[0].equals("addblacklist")){
						if(p.hasPermission("badbot.blacklist")){
							if(blackList.contains(args[1])){
								p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Le mot est deja dans la liste");
							}
							else{
								addString(args[1], "blacklist");
								blackList = getConfig().getStringList("blacklist");
								p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Le mot "+args[1]+" a ete ajoute a la liste");
							}
						}
						else{
							p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Vous n'avez pas les permissions necessaires");
							return true;
						}
					}
					else if(args[0].equals("removeblacklist")){
						if(p.hasPermission("badbot.blacklist")){
							if(blackList.contains(args[1])){
								removeString(args[1], "blacklist");
								blackList = getConfig().getStringList("blacklist");
								p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Le mot "+args[1]+" a ete supprime de la liste");
							}
							else{
								p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Le mot n'est pas dans la liste");
							}
						}
						else{
							p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Vous n'avez pas les permissions necessaires");
							return true;
						}
					}
					else{
						p.sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" La commande /badbot"+args[0]+" n'existe pas");
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
		if(!player.hasPermission("badbot.spam") && !mute){
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

		// Gestion du mute général
		if (mute) {
			if (player.hasPermission("badbot.muteperm")) {
				event.setCancelled(false);
			} else {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.YELLOW + "[BadBot]"+ ChatColor.RED +" Le chat a ete mute");
			}
		}
	}

	/**
	 * Enlève un élément (string) de la liste configList dans config.yml
	 * @param string
	 * @param configList
	 */
	private void removeString(String string, String configList) {
		List<String> list = getConfig().getStringList(configList);
		list.remove(string);
		getConfig().set(configList, list);
		saveConfig();
	}

	/**
	 * Ajoute un élément (string) dans la liste configList dans config.yml
	 * @param string
	 * @param configList
	 */
	private void addString(String string, String configList) {
		List<String> list = getConfig().getStringList(configList);
		list.add(string);
		getConfig().set(configList, list);
		saveConfig();
	}

	class Task extends BukkitRunnable{

		@Override
		public void run() {
			BadBot.this.lastMessage = null;
		}

	}
}
