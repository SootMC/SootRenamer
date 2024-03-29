package net.sootmc.sootrenamer;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenamerTools {
    private static final String PREFIX = ChatColor.WHITE + "[" + ChatColor.RED + "Soot" + ChatColor.GOLD + "MC" + ChatColor.WHITE + "] ";
    private static final int maximumRepairCost = 40;

    private static final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final char COLOUR_CHAR = ChatColor.COLOR_CHAR;

    public static void Renamer(Player player, String[] args) {
        String name = String.join(" ", args);

        if (name.contains("#") && !player.hasPermission("coloredanvils.color.hex")) {
            player.sendMessage(PREFIX + "You do not have permission to use gradients!");
            return;
        }

        if(name.contains("&k") && !player.hasPermission("sootrenamer.magic")) {
            player.sendMessage(PREFIX + "You are not allowed to use the magic formatting");
            return;
        }

        ItemStack ci = player.getInventory().getItemInMainHand();

        if (ci == null || ci.getType().equals(Material.AIR)) {
            player.sendMessage(PREFIX + "You cannot rename nothing!");
            return;
        }

        ItemMeta meta = ci.getItemMeta();

        if(name.contains("#") && player.hasPermission("coloredanvils.color.hex"))
            name = translateHexCode(name);

        name = ChatColor.translateAlternateColorCodes('&', name);

        meta.setDisplayName(name);

        int cost = getRepairCost(player, meta);

        if (player.getLevel() < cost) {
            player.sendMessage(PREFIX + "You do not have enough experience to rename this item. Required Experience: " + cost);
            return;
        }

        ci.setItemMeta(meta);

        player.getInventory().setItemInMainHand(ci);
        player.setLevel(player.getLevel() - cost);
        player.sendMessage(PREFIX + "Renamed item to '" + name + ChatColor.RESET + "' for " + ChatColor.GREEN + cost + ChatColor.RESET + " levels");
    }

    private static int getRepairCost(Player player, ItemMeta meta) {
        int cost = 1;
        if(player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))
            return 0;
        else if(meta instanceof Repairable repairable) {
            cost = repairable.getRepairCost() + 1;
            return Math.min(cost, maximumRepairCost);
        }

        return cost;
    }

    private static String translateHexCode(String string) {
        Matcher matcher = hexPattern.matcher(string);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOUR_CHAR + "x"
                    + COLOUR_CHAR + group.charAt(0) + COLOUR_CHAR + group.charAt(1)
                    + COLOUR_CHAR + group.charAt(2) + COLOUR_CHAR + group.charAt(3)
                    + COLOUR_CHAR + group.charAt(4) + COLOUR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }
}
