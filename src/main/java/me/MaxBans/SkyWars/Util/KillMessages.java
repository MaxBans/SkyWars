/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Util;

public class KillMessages {

    public static String getMessage(String player, String damager, KillReason reason) {

        if (damager == null) {
            switch (reason) {
                case VOID:
                    return "§b" + player + " §7fell into the §cVoid";
                case LEFT:
                    return "§b" + player + " §7died";
                case OTHER:
                    return "§b" + player + " §7died ";
                default:
                    break;
            }
        } else {
            switch (reason) {
                case VOID:
                    return "§b" + player + "§7 was killed by " + "§3" + damager;
                case LEFT:
                    return "§b" + player + "§7 was killed by " + "§3" + damager;
                case OTHER:
                    return "§b" + player + " §7was killed by " + "§3" + damager;
                default:
                    break;
            }
        }
        throw new IllegalArgumentException("Unknown reason");
    }

    public enum KillReason {

        VOID, LEFT, OTHER
    }
}
