package dev.artix.artixpunish.manager;

import dev.artix.artixpunish.command.CommandArgs;
import dev.artix.artixpunish.command.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Regista comandos em runtime no {@link CommandMap}, sem precisar listar cada um no {@code plugin.yml}.
 */
public final class RegisterManager {

    private final JavaPlugin plugin;
    private final CommandMap commandMap;
    private final List<String> registeredLabels = new ArrayList<String>();

    public RegisterManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.commandMap = resolveCommandMap();
    }

    private static CommandMap resolveCommandMap() {
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            return (CommandMap) f.get(Bukkit.getServer());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Não foi possível obter CommandMap do servidor.", e);
        }
    }

    private static PluginCommand newPluginCommand(String name, JavaPlugin owner) throws ReflectiveOperationException {
        Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, org.bukkit.plugin.Plugin.class);
        c.setAccessible(true);
        return c.newInstance(name, owner);
    }

    /**
     * Regista um comando com permissão, uso, aliases e handler.
     */
    public void register(String name, String permission, String usage, List<String> aliases, CommandHandler handler) {
        try {
            PluginCommand cmd = newPluginCommand(name.toLowerCase(), plugin);
            cmd.setPermission(permission);
            cmd.setUsage(usage);
            cmd.setDescription("ArtixPunish");
            if (aliases != null && !aliases.isEmpty()) {
                cmd.setAliases(new ArrayList<String>(aliases));
            }
            cmd.setExecutor((CommandSender sender, org.bukkit.command.Command command, String label, String[] args) ->
                    handler.handle(new CommandArgs(plugin, sender, command, label, args)));

            boolean ok = registerOnMap(cmd);
            if (ok) {
                registeredLabels.add(name.toLowerCase());
                plugin.getLogger().fine("Comando registado: /" + name);
            } else {
                plugin.getLogger().warning("Falha ao registar comando: /" + name);
            }
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().severe("Erro ao criar comando /" + name + ": " + e.getMessage());
        }
    }

    public void register(String name, String permission, String usage, CommandHandler handler) {
        register(name, permission, usage, Collections.<String>emptyList(), handler);
    }

    private boolean registerOnMap(PluginCommand command) {
        try {
            Method m = commandMap.getClass().getMethod("register", String.class, String.class, org.bukkit.command.Command.class);
            Object r = m.invoke(commandMap, command.getName(), plugin.getName(), command);
            return r instanceof Boolean ? (Boolean) r : true;
        } catch (NoSuchMethodException e) {
            try {
                Method m2 = commandMap.getClass().getMethod("register", String.class, org.bukkit.command.Command.class);
                Object r = m2.invoke(commandMap, plugin.getName(), command);
                return r instanceof Boolean ? (Boolean) r : true;
            } catch (ReflectiveOperationException e2) {
                plugin.getLogger().severe("CommandMap.register incompatível: " + e2.getMessage());
                return false;
            }
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().severe("Erro ao registar no CommandMap: " + e.getMessage());
            return false;
        }
    }

    public List<String> getRegisteredLabels() {
        return Collections.unmodifiableList(registeredLabels);
    }
}
