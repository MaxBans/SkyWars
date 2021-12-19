/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Stats;


import maxbans.hcurrencyapi.HCurrencyAPI;
import me.MaxBans.SkyWars.Managers.PlayerManager;
import me.MaxBans.SkyWars.SkyWarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLGetter {

    private SkyWarsPlugin plugin;


    public SQLGetter(SkyWarsPlugin pl){
        this.plugin = pl;
    }

    public void addKill(UUID uuid, int kills){
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                PreparedStatement ps = SkyWarsPlugin.preparedStatement("UPDATE skywars_stats SET KILLS=? WHERE UUID=?");
                ps.setInt(1, (getKills(uuid) + kills));
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addDeath(UUID uuid, int death){
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                PreparedStatement ps = SkyWarsPlugin.preparedStatement("UPDATE skywars_stats SET DEATHS=? WHERE UUID=?");
                ps.setInt(1, (getDeaths(uuid) + death));
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addWin(UUID uuid, int win){
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                PreparedStatement ps = SkyWarsPlugin.preparedStatement("UPDATE skywars_stats SET WINS=? WHERE UUID=?");
                ps.setInt(1, (getWins(uuid) + win));
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addPoint(UUID uuid, int amount){
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                PreparedStatement ps = SkyWarsPlugin.preparedStatement("UPDATE skywars_stats SET HELPOINTS=? WHERE UUID=?");
                PlayerManager.pointsEarned.put(uuid, PlayerManager.getPoints(uuid) + amount);
                ps.setInt(1, (getPoints(uuid) + amount));
                ps.setString(2, uuid.toString());
                ps.executeUpdate();

                System.out.println("Added " + amount + " point to player " + Bukkit.getPlayer(uuid).getName());
                System.out.println("Current points of player " + Bukkit.getPlayer(uuid).getName() + " in this game: " + PlayerManager.getPoints(uuid));
                System.out.println("Total points of player " + Bukkit.getPlayer(uuid).getName() + " :" + getPoints(uuid));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getKills(UUID uuid){
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                PreparedStatement ps = SkyWarsPlugin.preparedStatement("SELECT KILLS FROM skywars_stats WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                int kills = 0;
                if (rs.next()) {
                    kills = rs.getInt("KILLS");
                    return kills;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public Integer getOnlinePlayers(String id) {
        try {
            PreparedStatement ps = HCurrencyAPI.preparedStatement("SELECT ONLINE FROM sw_game_info WHERE ID='" + id + "';");
            //ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            int online;
            if (rs.next()) {
                online = rs.getInt("ONLINE");
                return online;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int getDeaths(UUID uuid){
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                PreparedStatement ps = SkyWarsPlugin.preparedStatement("SELECT DEATHS FROM skywars_stats WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                int deaths = 0;
                if (rs.next()) {
                    deaths = rs.getInt("DEATHS");
                    return deaths;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public int getWins(UUID uuid){
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                PreparedStatement ps = SkyWarsPlugin.preparedStatement("SELECT WINS FROM skywars_stats WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                int wins = 0;
                if (rs.next()) {
                    wins = rs.getInt("WINS");
                    return wins;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public int getPoints(UUID uuid){
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                PreparedStatement ps = SkyWarsPlugin.preparedStatement("SELECT HELPOINTS FROM skywars_stats WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                int points = 0;
                if (rs.next()) {
                    points = rs.getInt("HELPOINTS");
                    return points;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private Connection c = SkyWarsPlugin.getConnection();

    public String[] getTop(String type, int amount) {
        String[] array = new String[amount];
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                //Statement s = c.createStatement();
                PreparedStatement ps = SkyWarsPlugin.preparedStatement("SELECT * FROM skywars_stats ORDER BY " + type + " DESC LIMIT " + amount);
                ResultSet r = ps.executeQuery();
                int counter = 0;
                while (r.next()) {
                    array[counter] = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + r.getString("NAME") + ": §b" + r.getInt(type.toLowerCase()) + " " + type.toLowerCase();
                    counter++;
                }
            } catch (SQLException e) {
            }

        }
        return array;
    }
}
