package dev.artix.artixpunish.command;

/**
 * Lógica de um comando; implementações recebem {@link CommandArgs}.
 */
@FunctionalInterface
public interface CommandHandler {

    boolean handle(CommandArgs args);
}
