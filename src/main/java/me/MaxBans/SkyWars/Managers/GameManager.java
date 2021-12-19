package me.MaxBans.SkyWars.Managers;

import com.connorlinfoot.titleapi.TitleAPI;
import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.Maps.GameMap;
import me.MaxBans.SkyWars.Objects.Kits.Kit;
import me.MaxBans.SkyWars.Objects.Kits.KitType;
import me.MaxBans.SkyWars.Objects.Kits.Types.*;
import me.MaxBans.SkyWars.SkyWarsPlugin;
import me.MaxBans.SkyWars.Stats.SQLGameInfo;
import me.MaxBans.SkyWars.Stats.SQLGetter;
import me.MaxBans.SkyWars.Tasks.GameStartCountdownTask;
import me.MaxBans.SkyWars.Util.MapSorterValue;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.SQLException;
import java.util.*;

import static org.bukkit.ChatColor.*;

public class GameManager{

    private SkyWarsPlugin plugin;
    public static List<UUID> playersPlaying = new ArrayList<UUID>();
    private ConfigManager configManager = new ConfigManager();
    private GameStartCountdownTask gameStartCountdownTask;
    private GameState gameState;
    private static HashMap<UUID, Kit> kits;

    public static boolean allowCompare = false;


    public static HashMap<String, Integer> servers = new HashMap<>();
    private static HashMap<String, Integer> sorted;
    private static SQLGetter sqlManager = new SQLGetter(SkyWarsPlugin.getInstance());
    public static String highestPlayersServer;


    public GameManager(SkyWarsPlugin plugin){
        this.plugin = plugin;
        kits = new HashMap<>();
    }

    public static List<UUID> getPlayingPlayers(){
        return GameManager.playersPlaying;
    }

    public void startPreGame(){
            if(playersPlaying.isEmpty()){
                System.out.println("ERROR!!");
                GameState.setState(GameState.END);
            }

        for(int i = 0; i < playersPlaying.size(); i++){
            Player p = Bukkit.getPlayer(playersPlaying.get(i));
            PlayerManager.alivePlayers.add(p.getUniqueId());

        }

            for(int i = 0; i < playersPlaying.size(); i++){
            Player p = Bukkit.getPlayer(playersPlaying.get(i));
            p.teleport(configManager.getSpawn(i, VotingManager.getWinner().getCustomName()));

        }
    }

    public static void removeKit(UUID uuid){
        if(getKits().containsKey(uuid)){
            getKits().get(uuid).remove();
            getKits().remove(uuid);
        }
    }
    public static void setKit(UUID uuid, KitType type){
        GameManager.removeKit(uuid);


        switch(type){
            case Warrior:
                getKits().put(uuid, new Warrior(uuid));
                break;
            case EndServant:
                getKits().put(uuid, new EndServant(uuid));
                break;
            case Fisherman:
                getKits().put(uuid, new Fisherman(uuid));
                break;
            case Hawkeye:
                getKits().put(uuid, new Hawkeye(uuid));
                break;
            case Warlock:
                getKits().put(uuid, new Warlock(uuid));
                break;
        }
    }

    public static boolean hasKit(Player player){
        if(playersPlaying.contains(player.getUniqueId())){
            if(getKits().containsKey(player.getUniqueId())){
                return true;
            }
        }
        return false;
    }

    public static KitType getKit(Player player){
        if(playersPlaying.contains(player.getUniqueId())){
            if(getKits().containsKey(player.getUniqueId())){
                return getKits().get(player.getUniqueId()).getType();
            }
        }
        return null;
    }



    public  static HashMap<UUID, Kit> getKits(){return kits;}

    public boolean isLastPlayer(Player p){
        if(PlayerManager.alivePlayers.size() == 1 && PlayerManager.alivePlayers.contains(p.getUniqueId())){
            return true;
        }else{
            return false;
        }
    }

    public static void spawnFireworks(Location location, int amount) {
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for(int i = 0;i<amount; i++){
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

    public static void sendRewardSummary(Player player) {
        player.sendMessage(WHITE + "" + BOLD + "■■■■■■■■■■■■■■■■■■■■■■");
        player.sendMessage("§6§lReward Summary");
        player.sendMessage("§7You earned §e§l" + PlayerManager.coinsEarned.get(player.getUniqueId()) + " Coins§r§7 and§5§l " + PlayerManager.pointsEarned.get(player.getUniqueId()) + " HelPoints§r§7 and §2§l" + PlayerManager.keysEarned.get(player.getUniqueId()) + " Lottery Keys");
        player.sendMessage(WHITE + "" + BOLD + "■■■■■■■■■■■■■■■■■■■■■■");


    }

    public void moveToWinLobby(Player winner){
        for(Player pl : Bukkit.getOnlinePlayers()){
            for (int i = 0; i < configManager.getWinLocList().size(); i++) {
                String[] coords = configManager.getWinCoords(i).split(",");
                double x = Double.parseDouble(coords[0]);
                double y = Double.parseDouble(coords[1]);
                double z = Double.parseDouble(coords[2]);
                Location wantedLocation = new Location(Bukkit.getWorld(SkyWarsPlugin.getInstance().getConfig().getString("settings.spawns.WinLocation.world")), x, y, z);
                pl.teleport(wantedLocation);
                pl.getInventory().setBoots(null);
                pl.getInventory().setLeggings(null);
                pl.getInventory().setChestplate(null);
                pl.getInventory().setHelmet(null);
                pl.getInventory().clear();
                pl.setAllowFlight(false);
                pl.setFlying(false);
                pl.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                for (PotionEffect ev : pl.getActivePotionEffects()) {
                    pl.addPotionEffect(new PotionEffect(ev.getType(), 0, 0), true);
                }
            }
            winner.teleport(configManager.getWinLocForWinner());
            GameManager.sendWinMessage(pl, winner);
            sendRewardSummary(pl);
            pl.setGameMode(GameMode.ADVENTURE);
            pl.setHealth(20);
            pl.getInventory().clear();
            ItemStack next = new ItemStack(Material.PAPER);
            ItemMeta nMeta = next.getItemMeta();
            nMeta.setDisplayName("§e§lFind new Arena");
            next.setItemMeta(nMeta);
            pl.getInventory().setItem(4, next);
            SkyWarsPlugin.getInstance().scoreboard.removePlayer(pl);
        }

        sendRateMessage(winner, VotingManager.getWinner());
    }
    public static void sendWinMessage(Player p, Player winner){
        TitleAPI.sendTitle(winner,10,40,10,"§eVictory","§fCongratulations!");
        p.sendMessage(WHITE + "" + BOLD + "■■■■■■■■■■■■■■■■■■■■■■");
        p.sendMessage(" ");
        p.sendMessage(   "  §3§lWinner:§b§l "  + winner.getName());
        p.sendMessage(" ");
        HashMap<UUID,Integer> sortedMap = (HashMap<UUID, Integer>) sortByValue(PlayerManager.getKillsMap());
        for(UUID uuid : sortedMap.keySet()){
            OfflinePlayer key = Bukkit.getOfflinePlayer(uuid);
            p.sendMessage( "  §e§l" + key.getName() + ":§r§7 " + sortedMap.get(uuid) + " Kills");
        }

        p.sendMessage(WHITE + "" + BOLD + "■■■■■■■■■■■■■■■■■■■■■■");
        p.sendMessage("  ");
        p.sendMessage(" ");
    }

    public static void setSpectator(Player p){
        p.getInventory().clear();
        p.getInventory().setBoots(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setHelmet(null);
        for (PotionEffect ev : p.getActivePotionEffects()) {
            p.addPotionEffect(new PotionEffect(ev.getType(), 0, 0), true);
        }

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(p.getName());
        meta.setDisplayName("§bTeleport to player");
        skull.setItemMeta(meta);

        p.getInventory().setItem(0, skull);

        ItemStack leave = new ItemStack(Material.BED);
        ItemMeta tmeta = leave.getItemMeta();
        tmeta.setDisplayName("§cReturn to lobby!");
        leave.setItemMeta(tmeta);
        p.getInventory().setItem(8, leave);

        ItemStack next = new ItemStack(Material.PAPER);
        ItemMeta nMeta = next.getItemMeta();
        nMeta.setDisplayName("§e§lFind new Arena");
        next.setItemMeta(nMeta);
        p.getInventory().setItem(4, next);

        for (Player players : Bukkit.getOnlinePlayers()) {
            players.hidePlayer(p);
        }
        p.setAllowFlight(true);
        p.setFlying(true);
        p.setFlySpeed(0.2F);
        p.setCanPickupItems(false);
        p.setHealth(20);
        PlayerManager.alivePlayers.remove(p.getUniqueId());

    }

    public static void setWatcher(Player p) {
        if (!PlayerManager.spectators.contains(p.getUniqueId())) {
            p.getInventory().clear();
            p.getInventory().setBoots(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setHelmet(null);
            for (PotionEffect ev : p.getActivePotionEffects()) {
                p.addPotionEffect(new PotionEffect(ev.getType(), 0, 0), true);
            }

            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwner(p.getName());
            meta.setDisplayName("§bTeleport to player");
            skull.setItemMeta(meta);

            p.getInventory().setItem(0, skull);

            ItemStack leave = new ItemStack(Material.BED);
            ItemMeta tmeta = leave.getItemMeta();
            tmeta.setDisplayName("§cReturn to lobby!");
            leave.setItemMeta(tmeta);
            p.getInventory().setItem(8, leave);

            for (Player players : Bukkit.getOnlinePlayers()) {
                players.hidePlayer(p);
            }
            p.setAllowFlight(true);
            p.setFlying(true);
            p.setFlySpeed(0.2F);
            p.setCanPickupItems(false);
            p.setHealth(20);

            PlayerManager.spectators.add(p.getUniqueId());
            playersPlaying.remove(p.getUniqueId());
            PlayerManager.alivePlayers.remove(p.getUniqueId());
            Bukkit.broadcastMessage("§8[§3SkyWars§8]§b" + p.getName() + "§7 is now spectating this game!");
        }else{
            p.sendMessage(RED + "You are already spectating this game!");
        }
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static TextComponent createStar(Player player, int value, GameMap map){
        String mapName = map.getCustomName();
        TextComponent component = new TextComponent();
        component.setText(AQUA + "✫");
        component.setBold(true);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Rate this map with §b" + value + "✫").create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rate " + mapName + " " + value));
        return component;
    }

    public static void sendRateMessage(Player p, GameMap map){
        TextComponent star1 = createStar(p, 1, map);
        TextComponent star2 = createStar(p, 2, map);
        TextComponent star3 = createStar(p, 3, map);
        TextComponent star4 = createStar(p, 4, map);
        TextComponent star5 = createStar(p, 5, map);

        p.sendMessage("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
        p.sendMessage("§3§l" + VotingManager.getWinner());
        p.sendMessage(" §7Rate this map!");
        p.spigot().sendMessage(star1, star2, star3, star4, star5);
        p.sendMessage("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
    }

    public static void autoJoin(Player player) {
        servers.clear();
        try {
            for (String server : SQLGameInfo.getActiveServers("sw")) {
                servers.put(server, sqlManager.getOnlinePlayers(server));
                servers.remove(ConfigManager.getServerName());
                sorted = (HashMap<String, Integer>) MapSorterValue.sortByValue(servers);
                highestPlayersServer = Collections.max(sorted.entrySet(), Map.Entry.comparingByValue()).getKey();
            }
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            if(highestPlayersServer != null) {
                try {
                    out.writeUTF("Connect");
                    out.writeUTF(highestPlayersServer);
                    System.out.println(highestPlayersServer);
                } catch (Exception es) {
                    es.printStackTrace();
                }

                player.sendMessage("§8[§3Server§8]§7 Connecting you to§b " + highestPlayersServer + "...");
                player.sendPluginMessage(SkyWarsPlugin.getInstance(), "BungeeCord", b.toByteArray());
            }else{
                player.sendMessage(ChatColor.RED + " There is currently no free server. Return to lobby!");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}


