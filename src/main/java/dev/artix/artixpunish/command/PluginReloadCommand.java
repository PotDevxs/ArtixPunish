package dev.artix.artixpunish.command;

import dev.artix.artixpunish.manager.BanManager;
import dev.artix.artixpunish.manager.HistoryManager;
import dev.artix.artixpunish.manager.MuteManager;
import dev.artix.artixpunish.manager.WarnManager;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginReloadCommand implements CommandHandler {

    private final JavaPlugin plugin;
    private final BanManager banManager;
    private final MuteManager muteManager;
    private final WarnManager warnManager;
    private final HistoryManager historyManager;

    public PluginReloadCommand(JavaPlugin plugin, BanManager banManager, MuteManager muteManager,
                               WarnManager warnManager, HistoryManager historyManager) {
        this.plugin = plugin;
        this.banManager = banManager;
        this.muteManager = muteManager;
        this.warnManager = warnManager;
        this.historyManager = historyManager;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.reload")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (a.length() < 1 || !a.getArg(0).equalsIgnoreCase("reload")) {
            Messages.send(a.getSender(), "usage-artixpunish");
            return true;
        }
        Messages.reload(plugin);
        banManager.load();
        muteManager.load();
        warnManager.load();
        historyManager.load();
        Messages.send(a.getSender(), "reload-ok");
        return true;
    }
}
