package me.MaxBans.SkyWars.Maps;

import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.SkyWarsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.MaxBans.SkyWars.SkyWarsPlugin.preparedStatement;

public class RatingManager {

    public static List<UUID> rated = new ArrayList<>();

    public static void createMap(String map){
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                ResultSet rs = preparedStatement(
                        "SELECT COUNT(MAP) FROM map_rating WHERE MAP = '" + map + "';")
                        .executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    preparedStatement("INSERT INTO map_rating (MAP, RATED, RATING, 5stars, 4stars, 3stars, 2stars, 1stars) VALUES('"
                            + map + "', DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT);")
                            .executeUpdate();
                }
            } catch (SQLException var7) {
                var7.printStackTrace();
            }
        }
    }

    public static double getAverageRating(GameMap map){
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                PreparedStatement ps = SkyWarsPlugin.preparedStatement("SELECT RATING FROM map_rating WHERE MAP='" + map.getCustomName() + "';");
                //ps.setString(1, map.getCustomName());
                ResultSet rs = ps.executeQuery();
                double current = 0;
                if (rs.next()) {
                    current = rs.getDouble("RATING");
                    return current;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static int getPlayersRatedCount(GameMap map){
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                PreparedStatement ps = SkyWarsPlugin.preparedStatement("SELECT RATED FROM map_rating WHERE MAP='" + map.getCustomName() + "';");
                // ps.setString(1, map.getCustomName());
                ResultSet rs = ps.executeQuery();
                int current = 0;
                if (rs.next()) {
                    current = rs.getInt("RATED");
                    if(current == 0) current = 1;
                    return current;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static int getPlayersRatedCount(GameMap map, int value){
        if (SkyWarsPlugin.getInstance().isUsingMySql()) {
            try {
                PreparedStatement ps = SkyWarsPlugin.preparedStatement("SELECT * FROM map_rating WHERE MAP=?");

                String starString =  value + "stars";
                ps.setString(1, map.getCustomName());
                ResultSet rs = ps.executeQuery();
                int current;
                if(rs.next()) {
                    current = rs.getInt(starString);
                    return current;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    public static void rate(GameMap map, int rate, Player p) {
        if (GameState.isState(GameState.PLAYING) || GameState.isState(GameState.WINNER)) {
            if (rate > 5) p.sendMessage(ChatColor.RED + "Too high");

            if (SkyWarsPlugin.getInstance().isUsingMySql()) {
                try {

                    String stars = "`" + String.valueOf(rate) + "stars" + "`";
                    PreparedStatement update = SkyWarsPlugin.preparedStatement("UPDATE map_rating SET RATED = RATED+1 WHERE MAP=?");
                    update.setString(1, map.getCustomName());
                    update.executeUpdate();
                    PreparedStatement star = SkyWarsPlugin.preparedStatement("UPDATE map_rating SET" + stars + "=" + stars + "+ 1 WHERE MAP=?");
                    star.setString(1, map.getCustomName());
                    star.executeUpdate();
                    PreparedStatement rt = SkyWarsPlugin.preparedStatement("UPDATE map_rating set RATING=? WHERE MAP=?");
                    double rating = (5 * getPlayersRatedCount(map, 5) + 4 * getPlayersRatedCount(map, 4) + 3 * getPlayersRatedCount(map, 3) + 2 * getPlayersRatedCount(map, 2) + getPlayersRatedCount(map, 1)) / getPlayersRatedCount(map);
                    rt.setDouble(1, rating);
                    rt.setString(2, map.getCustomName());
                    rt.executeUpdate();
                    rated.add(p.getUniqueId());
                    p.sendMessage("§8[§3Rating§8]§7 You rated this map with §b" + rate + " §e§l✫") ;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }else{
            p.sendMessage(ChatColor.RED + "You can't rate this map");
        }
    }

}


