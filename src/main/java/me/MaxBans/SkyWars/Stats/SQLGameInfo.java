/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Stats;

import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.Managers.ConfigManager;
import me.MaxBans.SkyWars.Managers.GameManager;
import me.MaxBans.SkyWars.SkyWarsPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static me.MaxBans.SkyWars.SkyWarsPlugin.preparedStatement;

public class SQLGameInfo {

    private SkyWarsPlugin plugin;

    private static List<String> activeS = new ArrayList<>();

    public SQLGameInfo(SkyWarsPlugin pl){
        this.plugin = pl;
    }

    public static void createGame() {
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                ResultSet rs = preparedStatement(
                        "SELECT COUNT(ID) FROM sw_game_info WHERE ID = '" + ConfigManager.getServerName() + "';")
                        .executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    preparedStatement("INSERT INTO sw_game_info(ID, GAME_STATE, MAP, ONLINE) VALUES('"
                            + ConfigManager.getServerName() + "','" + GameState.getState().name() + "',' VOTING" + "', '" + GameManager.playersPlaying.size() + "');")
                            .executeUpdate();
                }
            } catch (SQLException var7) {
                var7.printStackTrace();
            }
        }

    }
//"DELETE FROM `sw_game_info` WHERE `sw_game_info`.`ID` = '" + ConfigManager.getServerName() + "';"
    public static void deleteGame(){
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                String query = "DELETE FROM `sw_game_info` WHERE `sw_game_info`.`ID` = '" + ConfigManager.getServerName() + "';";
                Statement st = SkyWarsPlugin.getConnection().createStatement();
                st.executeUpdate(query);

            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public static void updateGameState(GameState state){
        try {
            PreparedStatement ps = SkyWarsPlugin.preparedStatement("UPDATE sw_game_info SET GAME_STATE=? WHERE ID= '" +ConfigManager.getServerName() + "';");
            ps.setString(1, state.name());
           // ps.setString(2, ConfigManager.getServerName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateMap(String map){
        try {
            PreparedStatement ps = SkyWarsPlugin.preparedStatement("UPDATE sw_game_info SET MAP=? WHERE ID= '" +ConfigManager.getServerName() + "';");
            ps.setString(1, map);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayers(int amount){
        try {
            PreparedStatement ps = SkyWarsPlugin.preparedStatement("UPDATE sw_game_info SET ONLINE=? WHERE ID= '" +ConfigManager.getServerName() + "';");
            ps.setInt(1, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getActiveServers(String mode) throws SQLException {
        Statement sta = SkyWarsPlugin.getConnection().createStatement();
        ResultSet res = sta.executeQuery("SELECT * FROM " + mode + "_game_info");
        activeS.clear();
        while (res.next()) {
            if(res.getString("GAME_STATE").equals("LOBBY") || res.getString("GAME_STATE").equals("STARTING") && res.getInt("ONLINE") < 12) {
                String server = res.getString("ID");
                activeS.add(server);
                activeS.remove(ConfigManager.getServerName());
            }
        }

        return activeS;
    }

}
