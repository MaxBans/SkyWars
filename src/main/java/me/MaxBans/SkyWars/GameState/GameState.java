package me.MaxBans.SkyWars.GameState;

import maxbans.hcurrencyapi.CurrencyManager;
import maxbans.hcurrencyapi.DepositReason;
import maxbans.hcurrencyapi.GamePlayer;
import me.MaxBans.SkyWars.Managers.*;
import me.MaxBans.SkyWars.Maps.MapLoader;
import me.MaxBans.SkyWars.SkyWarsPlugin;
import me.MaxBans.SkyWars.Stats.SQLGameInfo;
import me.MaxBans.SkyWars.Stats.SQLGetter;
import me.MaxBans.SkyWars.Tasks.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

public enum GameState {

    LOBBY,
    STARTING,
    PREGAME,
    PLAYING,
    WINNER,
    END;

    private static final GameManager gameManager = new GameManager(SkyWarsPlugin.getInstance());
    private static final SQLGetter sqlGetter = new SQLGetter(SkyWarsPlugin.getInstance());
    private static GameState state;
    private static GameStartCountdownTask gameStartCountdownTask;
    private static PreGameCountdownTask preGameCountdownTask;
    private static TotalTimeCountdownTask totalTimeCountdownTask;
    private static ChestRefillCountdownTask chestRefillCountdownTask;
    private static VictoryEndCountdownTask victoryEndCountdownTask;
    private static final ConfigManager configManager = new ConfigManager();
    private static final MapLoader mapLoader = new MapLoader();


    public static boolean isState(GameState state) {
        return GameState.state == state;
    }

    public static GameState getState() {
        return state;
    }


    public static void setState(GameState state) {
        GameState.state = state;

        switch (state) {
            case LOBBY:
                Bukkit.broadcastMessage("LOBBY!");
                SQLGameInfo.updateGameState(GameState.LOBBY);
                break;
            case PREGAME:
                BorderManager.getBorder1();
                VotingManager.sendMapMessage();
                if (PlayerManager.alivePlayers.size() == 1) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.kickPlayer("All players left so you were kicked from the game");
                        setState(GameState.END);
                        return;
                    }
                } else {
                    GameState.gameManager.startPreGame();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        //Player p = Bukkit.getPlayer(uuid);
                        PlayerManager.playerKills.put(p.getUniqueId(), 0);
                        PlayerManager.coinsEarned.put(p.getUniqueId(), 0);
                        PlayerManager.pointsEarned.put(p.getUniqueId(), 0);
                        PlayerManager.keysEarned.put(p.getUniqueId(), 0);
                        p.setGameMode(GameMode.SURVIVAL);
                        p.getInventory().clear();
                        p.setWalkSpeed(0);
                        p.setFallDistance(0);
                        SQLGameInfo.updateGameState(GameState.PREGAME);
                        SkyWarsPlugin.lobbyScoreboard.removePlayer(p);
                        if (GameManager.hasKit(p)) {
                            GamePlayer gamePlayer = new GamePlayer(p);
                            CurrencyManager.removeCoins(gamePlayer, GameManager.getKit(p).getPrice());
                        }
                    }
                    if (GameState.preGameCountdownTask != null) GameState.preGameCountdownTask.cancel();
                    GameState.preGameCountdownTask = new PreGameCountdownTask(GameState.state);
                    GameState.preGameCountdownTask.runTaskTimer(SkyWarsPlugin.getInstance(), 0, 20);

                    break;
                }
            case PLAYING:
                if (GameState.totalTimeCountdownTask != null) GameState.totalTimeCountdownTask.cancel();
                if (GameState.chestRefillCountdownTask != null) GameState.chestRefillCountdownTask.cancel();

                if (GameManager.playersPlaying.size() >= 4) {
                    GameManager.allowCompare = true;
                }
                GameState.totalTimeCountdownTask = new TotalTimeCountdownTask(GameState.state);
                GameState.chestRefillCountdownTask = new ChestRefillCountdownTask(GameState.state);

                GameState.totalTimeCountdownTask.runTaskTimer(SkyWarsPlugin.getInstance(), 0, 20);
                GameState.chestRefillCountdownTask.runTaskTimer(SkyWarsPlugin.getInstance(), 0, 20);

                KitAbilityTask task = new KitAbilityTask();
                task.runTaskTimer(SkyWarsPlugin.getInstance(), 0, 20);
                SQLGameInfo.updateGameState(GameState.PLAYING);

                for (UUID uuid : GameManager.playersPlaying) {
                    Player p = Bukkit.getPlayer(uuid);
                    p.setGameMode(GameMode.SURVIVAL);
                    p.setHealth(20);
                    SkyWarsPlugin.scoreboard.addPlayer(p);
                    p.setWalkSpeed(0.2F);
                }


                for (UUID uuid : GameManager.getKits().keySet()) {
                    GameManager.getKits().get(uuid).onStart(Bukkit.getPlayer(uuid));
                }
                //mapLoader.turnOffAutoSave();
                break;
            case STARTING:
                if (GameState.gameStartCountdownTask != null) GameState.gameStartCountdownTask.cancel();
                GameState.gameStartCountdownTask = new GameStartCountdownTask(GameState.state);
                GameState.gameStartCountdownTask.runTaskTimer(SkyWarsPlugin.getInstance(), 0, 20);
                SQLGameInfo.updateGameState(GameState.STARTING);
                break;
            case WINNER:
                if (GameState.victoryEndCountdownTask != null) GameState.victoryEndCountdownTask.cancel();
                victoryEndCountdownTask = new VictoryEndCountdownTask(GameState.state);
                victoryEndCountdownTask.runTaskTimer(SkyWarsPlugin.getInstance(), 0, 20);
                SQLGameInfo.updateGameState(GameState.WINNER);
                for(Player pl : Bukkit.getOnlinePlayers()) {
                    SkyWarsPlugin.scoreboard.removePlayer(pl);
                    SkyWarsPlugin.lobbyScoreboard.removePlayer(pl);
                }
                for (UUID uuid : PlayerManager.alivePlayers) {
                    Player winner = Bukkit.getPlayer(uuid);
                    sqlGetter.addPoint(winner.getUniqueId(), ConfigManager.getPointsForPlay());
                    sqlGetter.addPoint(winner.getUniqueId(), ConfigManager.getPointsForWin());

                    GamePlayer w = new GamePlayer(winner);
                    CurrencyManager.depositCoins(w, ConfigManager.getCoinsForWin(), DepositReason.MINIGAME);
                    PlayerManager.coinsEarned.put(winner.getUniqueId(), PlayerManager.getCoins(winner.getUniqueId()) + ConfigManager.getCoinsForWin());
                    CurrencyManager.depositKeys(w, 1);
                    PlayerManager.keysEarned.put(winner.getUniqueId(), 1);
                    Bukkit.broadcastMessage("§8[§3SkyWars§8]§b " + winner.getName() + " §7won the game! ");
                    GameManager.spawnFireworks(winner.getLocation(), 5);
                    GameState.sqlGetter.addWin(uuid, 1);
                    gameManager.moveToWinLobby(winner);
                }
                //Bukkit.unloadWorld(VotingManager.getWinner().getC, false);
                break;
            case END:
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.kickPlayer("§cServer is restarting");
                }
                mapLoader.reset();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
                SQLGameInfo.updateGameState(GameState.END);
                break;
        }
    }


}
