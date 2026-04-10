package dev.artix.artixpunish;

import dev.artix.artixpunish.command.BanCommand;
import dev.artix.artixpunish.command.ClearWarnsCommand;
import dev.artix.artixpunish.command.HistoricoCommand;
import dev.artix.artixpunish.command.IpBanCommand;
import dev.artix.artixpunish.command.KickCommand;
import dev.artix.artixpunish.command.MuteCommand;
import dev.artix.artixpunish.command.PluginReloadCommand;
import dev.artix.artixpunish.command.PunishMenuCommand;
import dev.artix.artixpunish.command.TempBanCommand;
import dev.artix.artixpunish.command.TempMuteCommand;
import dev.artix.artixpunish.command.UnbanCommand;
import dev.artix.artixpunish.command.UnbanIpCommand;
import dev.artix.artixpunish.command.UnmuteCommand;
import dev.artix.artixpunish.command.WarnCommand;
import dev.artix.artixpunish.gui.MenuListener;
import dev.artix.artixpunish.listener.ChatListener;
import dev.artix.artixpunish.listener.LoginListener;
import dev.artix.artixpunish.manager.BanManager;
import dev.artix.artixpunish.manager.HistoryManager;
import dev.artix.artixpunish.manager.MuteManager;
import dev.artix.artixpunish.manager.RegisterManager;
import dev.artix.artixpunish.manager.WarnManager;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class ArtixPunish extends JavaPlugin {

    private BanManager banManager;
    private MuteManager muteManager;
    private WarnManager warnManager;
    private HistoryManager historyManager;
    private RegisterManager registerManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Messages.init(this);

        banManager = new BanManager(this);
        muteManager = new MuteManager(this);
        warnManager = new WarnManager(this);
        historyManager = new HistoryManager(this);

        banManager.load();
        muteManager.load();
        warnManager.load();
        historyManager.load();

        getServer().getPluginManager().registerEvents(new LoginListener(banManager), this);
        getServer().getPluginManager().registerEvents(new ChatListener(muteManager), this);
        getServer().getPluginManager().registerEvents(
                new MenuListener(this, banManager, muteManager, warnManager, historyManager), this);

        registerManager = new RegisterManager(this);
        registerCommands();
    }

    private void registerCommands() {
        registerManager.register("ban", "artixpunish.ban", "/<command> <jogador> [motivo]", Arrays.asList("b"),
                new BanCommand(banManager, historyManager));
        registerManager.register("tempban", "artixpunish.tempban", "/<command> <jogador> <duração> [motivo]", Arrays.asList("tb"),
                new TempBanCommand(banManager, historyManager));
        registerManager.register("unban", "artixpunish.unban", "/<command> <jogador>",
                new UnbanCommand(banManager));
        registerManager.register("kick", "artixpunish.kick", "/<command> <jogador> [motivo]", Arrays.asList("k"),
                new KickCommand(historyManager));
        registerManager.register("mute", "artixpunish.mute", "/<command> <jogador> [motivo]",
                new MuteCommand(muteManager, historyManager));
        registerManager.register("tempmute", "artixpunish.tempmute", "/<command> <jogador> <duração> [motivo]", Arrays.asList("tm"),
                new TempMuteCommand(muteManager, historyManager));
        registerManager.register("unmute", "artixpunish.unmute", "/<command> <jogador>",
                new UnmuteCommand(muteManager));
        registerManager.register("warn", "artixpunish.warn", "/<command> <jogador> [motivo]",
                new WarnCommand(warnManager, historyManager));
        registerManager.register("clearwarns", "artixpunish.clearwarns", "/<command> <jogador>",
                new ClearWarnsCommand(warnManager));
        registerManager.register("historico", "artixpunish.history", "/<command> <jogador>", Arrays.asList("phist", "punishhist"),
                new HistoricoCommand(historyManager));
        registerManager.register("ipban", "artixpunish.ipban", "/<command> <ip> [motivo]",
                new IpBanCommand(banManager, historyManager));
        registerManager.register("unbanip", "artixpunish.unbanip", "/<command> <ip>",
                new UnbanIpCommand(banManager));
        registerManager.register("artixpunish", "artixpunish.reload", "/<command> reload", Arrays.asList("apunish", "artixp"),
                new PluginReloadCommand(this, banManager, muteManager, warnManager, historyManager));
        registerManager.register("punishmenu", "artixpunish.menu", "/<command>", Arrays.asList("punicao", "pmenu", "punishgui"),
                new PunishMenuCommand(this));
    }

    @Override
    public void onDisable() {
        if (banManager != null) {
            banManager.save();
        }
        if (muteManager != null) {
            muteManager.save();
        }
        if (warnManager != null) {
            warnManager.save();
        }
        if (historyManager != null) {
            historyManager.save();
        }
    }
}
