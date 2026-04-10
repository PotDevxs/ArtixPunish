package dev.artix.artixpunish.command;

import dev.artix.artixpunish.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/**
 * Argumentos e contexto de um comando; métodos auxiliares para handlers.
 */
public final class CommandArgs {

    private final JavaPlugin plugin;
    private final CommandSender sender;
    private final Command command;
    private final String label;
    private final String[] args;

    public CommandArgs(JavaPlugin plugin, CommandSender sender, Command command, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args != null ? Arrays.copyOf(args, args.length) : new String[0];
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public CommandSender getSender() {
        return sender;
    }

    public Command getCommand() {
        return command;
    }

    public String getLabel() {
        return label;
    }

    public String[] getArgs() {
        return Arrays.copyOf(args, args.length);
    }

    public int length() {
        return args.length;
    }

    public String getArg(int index) {
        if (index < 0 || index >= args.length) {
            return null;
        }
        return args[index];
    }

    public boolean hasArg(int index) {
        return index >= 0 && index < args.length && args[index] != null && !args[index].isEmpty();
    }

    /**
     * Junta argumentos a partir do índice (ex.: motivo com várias palavras).
     */
    public String join(int start) {
        if (start >= args.length) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i > start) {
                b.append(' ');
            }
            b.append(args[i]);
        }
        return b.toString();
    }

    public String defaultReason(int startIndex) {
        String s = join(startIndex);
        if (s != null && !s.trim().isEmpty()) {
            return s.trim();
        }
        return plugin.getConfig().getString("defaults.reason", "Sem motivo");
    }

    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @SuppressWarnings("deprecation")
    public OfflinePlayer getOfflinePlayer(int index) {
        String name = getArg(index);
        if (name == null) {
            return null;
        }
        return Bukkit.getOfflinePlayer(name);
    }

    public Player getPlayerExact(int index) {
        String name = getArg(index);
        if (name == null) {
            return null;
        }
        return Bukkit.getPlayerExact(name);
    }

    public boolean isPlayerSender() {
        return sender instanceof Player;
    }

    public Player getPlayerSender() {
        return sender instanceof Player ? (Player) sender : null;
    }

    /**
     * Exige que o executor seja jogador; envia mensagem e retorna false se for console.
     */
    public boolean requirePlayerSender() {
        if (sender instanceof Player) {
            return true;
        }
        Messages.send(sender, "error-console");
        return false;
    }
}
