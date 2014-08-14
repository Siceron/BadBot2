BadBot
======

Plugin Bukkit de modération pour [Hovercraft] (http://www.hovercraft-servers.com)

1. v0.1 : 
	* Le bot empêche le joueur de spam (ex : si le joueur envoie "hello" 2 fois d'affilée, son deuxième message ne sera pas affiché et il devra attendre X secondes)
	* la fonction /badbot interval [secondes] change l'intervalle entre chaque spam
	* la fonction /badbot kick [boolean] change si le joueur est kick ou non
2. v0.2 :
	* La fonction /badbot mute empêche tous les joueurs du serveur de parler (jusqu'à ce qu'elle soit réactivée)
3. v0.3 :
	* Le bot kick le joueur qui envoie un mot de la blacklist (language)
4. v0.4 :
	* La fonction /badbot addblacklist [mot] ajoute un mot à la blacklist
	* La fonction /badbot removeblacklist [mot] enlève un mot de la blacklist
	* Ajout de permissions différentes pour chaque fonction