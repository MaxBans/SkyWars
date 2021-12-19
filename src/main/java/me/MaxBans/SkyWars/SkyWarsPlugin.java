
/**
 * Copyright (c) 2021 Filip Crhonek
 */


package me.MaxBans.SkyWars;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.Listeners.*;
import me.MaxBans.SkyWars.Managers.*;
import me.MaxBans.SkyWars.Maps.RateCommand;
import me.MaxBans.SkyWars.Maps.RatingManager;
import me.MaxBans.SkyWars.Objects.Kits.KitCommand;
import me.MaxBans.SkyWars.Objects.VoteCommand;
import me.MaxBans.SkyWars.Stats.SQLGameInfo;
import me.MaxBans.SkyWars.Stats.SQLGetter;
import me.MaxBans.SkyWars.Stats.StatsCommand;
import me.MaxBans.SkyWars.Tasks.ChestRefillCountdownTask;
import me.MaxBans.SkyWars.Tasks.GameStartCountdownTask;
import me.MaxBans.SkyWars.Tasks.LobbyUpdateScoreboardTask;
import me.MaxBans.SkyWars.Tasks.TotalTimeCountdownTask;
import me.MaxBans.SkyWars.commands.SpectatorCommand;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

public class SkyWarsPlugin extends JavaPlugin implements Listener {

    private static SkyWarsPlugin instance;
    private static Connection connection;



    public static JPerPlayerScoreboard lobbyScoreboard;
    public static JPerPlayerScoreboard scoreboard;
    public SQLGetter data;
    private ChestManager chestManager;
    private GameState gameState;
    private GameManager gameManager;
    private LobbyUpdateScoreboardTask lobbyUpdateScoreboardTask;
    private final VotingManager votingManager = new VotingManager();
    private PlayerDeathListener deathListener;
    private final SQLGetter sqlGetter = new SQLGetter(this);

    public File dir = new File("plugins/SkyWars");
    FileManager fileManager = new FileManager(this);
    private File chestFile;
    private FileConfiguration chests;
    private File mapFile;
    private FileConfiguration maps;

    private String host;
    private String database;
    private String username;
    private String password;
    private int port;

    public SkyWarsPlugin() {
        instance = this;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static SkyWarsPlugin getInstance() {
        return instance;
    }


    public static PreparedStatement preparedStatement(String query) {
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query);
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

        return ps;
    }

    public static void createPlayerProfile(Player p) {
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                ResultSet rs = preparedStatement(
                        "SELECT COUNT(UUID) FROM skywars_stats WHERE UUID = '" + p.getUniqueId() + "';")
                        .executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    preparedStatement("INSERT INTO skywars_stats(UUID, NAME, HELPOINTS, KILLS, DEATHS, WINS) VALUES('"
                            + p.getUniqueId() + "','" + p.getName() + "',DEFAULT, DEFAULT, DEFAULT, DEFAULT);")
                            .executeUpdate();
                } else {
                    ResultSet rs2 = preparedStatement("SELECT * FROM skywars_stats WHERE UUID = '" + p.getUniqueId() + "';")
                            .executeQuery();
                    rs2.next();
                    String name = rs2.getString("NAME");
                    int kills = rs2.getInt("KILLS");
                    int deaths = rs2.getInt("DEATHS");
                    int wins = rs2.getInt("WINS");
                    int points = rs2.getInt("HELPOINTS");
                    System.out.println(name);
                    System.out.println(kills);
                    System.out.println(deaths);
                    System.out.println(wins);
                    System.out.println(points);
                }
            } catch (SQLException var7) {
                var7.printStackTrace();
            }
        }

    }

    public void onDisable() {
        scoreboard.destroy();
        lobbyScoreboard.destroy();
        GameState.setState(GameState.END);
        SQLGameInfo.deleteGame();
    }

    public void onEnable() {
        System.out.println("SkyWars | Developed by Maxbans9");
        VotingManager.start();
        Bukkit.setDefaultGameMode(GameMode.ADVENTURE);
        for(World w : Bukkit.getWorlds()){
            w.setDifficulty(Difficulty.PEACEFUL);
            
        }

        this.data = new SQLGetter(this);
        this.host = this.getConfig().getString("settings.MySQL.host");
        this.port = this.getConfig().getInt("settings.MySQL.port");
        this.database = this.getConfig().getString("settings.MySQL.database");
        this.username = this.getConfig().getString("settings.MySQL.username");
        this.password = this.getConfig().getString("settings.MySQL.password");
        if(isUsingMySql()) {
            try {
                this.openConnection();
                System.out.println("==================");
                System.out.println("DATABASE CONNECTED!");
                System.out.println("==================");

            } catch (SQLException var3) {
                var3.printStackTrace();
            }
        }else{
            System.out.println("==================");
            System.out.println("This version of plugin is not using MySQL!");
            System.out.println("==================");
        }

        this.gameManager = new GameManager(this);
        Iterator var1 = GameManager.playersPlaying.iterator();

        while (var1.hasNext()) {
            UUID uuid = (UUID) var1.next();
            GameManager var4 = this.gameManager;
            GameManager.removeKit(uuid);
        }
        setupFiles();
        setupScoreboard();
        GameState.setState(GameState.LOBBY);
        this.saveDefaultConfig();
        Bukkit.getOnlinePlayers().forEach(this::addToScoreboard);
        registerEvents();
        getCommand("rate").setExecutor(new RateCommand());
        setupBungee();
        resetArmorStands(Bukkit.getWorld("lobby"));
        setupHolograms();
        SQLGameInfo.createGame();
        GameState.setState(GameState.LOBBY);
        registerMaps();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        lobbyScoreboard.addPlayer(p);
        lobbyScoreboard.updateScoreboard();

    }

    public void registerEvents(){
        this.chestManager = new ChestManager(this.getConfig());
        this.getServer().getPluginManager().registerEvents(this.chestManager, this);
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);
        this.getServer().getPluginManager().registerEvents(new ServerListPingListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        this.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        this.getServer().getPluginManager().registerEvents(new LobbyListener(), this);
        this.getServer().getPluginManager().registerEvents(new WorldLoadListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        this.getServer().getPluginManager().registerEvents(new SpectatorManager(), this);
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getCommand("game").setExecutor(new KitCommand());
        this.getCommand("vote").setExecutor(new VoteCommand());
        this.getCommand("top").setExecutor(new StatsCommand());
        this.getCommand("spectator").setExecutor(new SpectatorCommand());
    }

    public void setupBungee(){
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

    }

    public void addToScoreboard(Player p) {
        lobbyScoreboard.addPlayer(p);
        lobbyScoreboard.updateScoreboard();
    }

    public void setPlayerScoreboard(Player p) {
        scoreboard.addPlayer(p);
        scoreboard.updateScoreboard();
    }

    public void setupScoreboard(){
        scoreboard = new JPerPlayerScoreboard((player) -> {
            return "&3&lSW Tournament&r&f | 0" + TotalTimeCountdownTask.getTitle();
        }, (player) -> {
            return Arrays.asList
                    ("&7Server: &f" + this.getConfig().getString("settings.serverName"),
                            "&7&lMap:&b " + VotingManager.getWinner().getCustomName(),
                            "",
                            "&3&lNext Event:",
                            "&f0" + ChestRefillCountdownTask.getTitle(),
                            "",
                            "&3&lAlive:",
                            "&f" + PlayerManager.alivePlayers.size() + "",
                            " ",
                            "&7Kills:&f " + PlayerManager.getKills(player.getUniqueId()),
                            "",
                            "&bmc.helheim.cz");
        });
        this.lobbyUpdateScoreboardTask = new LobbyUpdateScoreboardTask(this);
        this.lobbyUpdateScoreboardTask.runTaskTimer(this, 0L, 10L);
        lobbyScoreboard = new JPerPlayerScoreboard((player) -> {
            return GameStartCountdownTask.getTitle();
        }, (player) -> {
            return Arrays.asList
                    ("&7Server: &f" + this.getConfig().getString("settings.serverName"),
                            " ",
                            "&3Arena",
                            "&b►&f Mode:&b Tournament",
                            "&b►&f Players:&b " + GameManager.playersPlaying.size() + "/12",
                            "   ",
                            "&3Kit",
                            "&b►&f " + getKitName(player),
                            "    ",
                            "&bmc.helheim.cz");
        });

    }

    public void registerMaps(){
        for(String map : this.getConfig().getStringList("settings.maps")){
            RatingManager.createMap(map);
        }
    }

    public String getKitName(Player p) {
        String kit;
        if (!GameManager.hasKit(p)) {
            kit = "None";
            return kit;
        } else {
            kit = GameManager.getKit(p).getDisplay();
            return kit;
        }
    }

    private void openConnection() throws SQLException {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
            }
    }

    public void setupHolograms() {
        Location killsLoc = new Location(Bukkit.getWorld(this.getConfig().getString("settings.spawns.lobby.world")),
                this.getConfig().getInt("holograms.locations.kills.x"),
                this.getConfig().getInt("holograms.locations.kills.y"),
                this.getConfig().getInt("holograms.locations.kills.z"));
        Location winLoc = new Location(Bukkit.getWorld(this.getConfig().getString("settings.spawns.lobby.world")),
                this.getConfig().getInt("holograms.locations.wins.x"),
                this.getConfig().getInt("holograms.locations.wins.y"),
                this.getConfig().getInt("holograms.locations.wins.z"));
        Location deathLoc = new Location(Bukkit.getWorld(this.getConfig().getString("settings.spawns.lobby.world")),
                this.getConfig().getInt("holograms.locations.deaths.x"),
                this.getConfig().getInt("holograms.locations.deaths.y"),
                this.getConfig().getInt("holograms.locations.deaths.z"));
        Location pointsLoc = new Location(Bukkit.getWorld(this.getConfig().getString("settings.spawns.lobby.world")),
                this.getConfig().getInt("holograms.locations.points.x"),
                this.getConfig().getInt("holograms.locations.points.y"),
                this.getConfig().getInt("holograms.locations.points.z"));

        Hologram killsBoard = HologramsAPI.createHologram(this, killsLoc);

        String[] kills = this.sqlGetter.getTop("KILLS", 5);
        for (int i = 0; i < 5; i++) {
            killsBoard.insertTextLine(i, kills[i]);
        }
        killsBoard.insertTextLine(0, "§c§lTOP 5 Lifetime Killers");

        Hologram winsBoard = HologramsAPI.createHologram(this, winLoc);
        String[] wins = this.sqlGetter.getTop("WINS", 10);

        for (int i = 0; i < 5; ++i) {
            winsBoard.insertTextLine(i, wins[i]);
        }
        winsBoard.insertTextLine(0, "§c§lTOP 5 Lifetime Winners");
        Hologram deathsBoard = HologramsAPI.createHologram(this, deathLoc);
        String[] deaths = this.sqlGetter.getTop("DEATHS", 10);

        for (int i = 0; i < 5; ++i) {
            deathsBoard.insertTextLine(i, deaths[i]);
        }
        deathsBoard.insertTextLine(0, "§c§lTOP 5 Lifetime Deaths");

        String[] points = this.sqlGetter.getTop("HELPOINTS", 10);
        Hologram pointsBoard = HologramsAPI.createHologram(this, pointsLoc);

        for (int i = 0; i < 10; ++i) {
            pointsBoard.insertTextLine(i, points[i]);
        }
        pointsBoard.insertTextLine(0, "§c§lTOP 10 HelPoints");


    }

    public boolean isUsingMySql() {
        return this.getConfig().getBoolean("settings.MySQL.usemysql");
    }

    public void resetArmorStands(World world) {
        for(Entity entity : world.getEntities()) {
            entity.remove();
        }
    }

    public File getMapsFolder(){
        this.getDirectory().mkdirs();

        File gameMapsFolder = new File(getDirectory(), "gameMaps");
        if(!gameMapsFolder.exists()){
            gameMapsFolder.mkdirs();
        }

        return gameMapsFolder;
    }

    public void setupFiles(){
        chestFile = fileManager.getFile("chest");
        if(!chestFile.exists()){
            fileManager.createFile("chest");
            System.out.println("Chest File created successfully");
        }
        mapFile = fileManager.getFile("maps");
        if(!mapFile.exists()){
            fileManager.createFile("maps");
            System.out.println("Maps File created successfully");
        }
        maps = YamlConfiguration.loadConfiguration(mapFile);
        chests =  YamlConfiguration.loadConfiguration(chestFile);
    }

    public File getDirectory(){
        return dir;
    }
    public FileManager getFileManager(){return fileManager; }

    public FileConfiguration getChestsConfig(){return chests;}
    public FileConfiguration getMapsConfig(){return maps;}

}