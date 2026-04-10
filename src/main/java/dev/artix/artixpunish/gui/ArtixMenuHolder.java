package dev.artix.artixpunish.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Identifica inventários do ArtixPunish e guarda contexto (tipo + alvo).
 */
public final class ArtixMenuHolder implements InventoryHolder {

    public enum Type {
        MAIN,
        ONLINE_PLAYERS,
        PUNISH_TARGET,
        CONFIRM_RESET_PLAYERS,
        CONFIRM_RESET_IP,
        PICK_TEMP_BAN,
        PICK_TEMP_MUTE
    }

    private Inventory inventory;
    private final Type menuType;
    private final String targetName;

    public ArtixMenuHolder(Type menuType, String targetName) {
        this.menuType = menuType;
        this.targetName = targetName != null ? targetName : "";
    }

    void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Type getMenuType() {
        return menuType;
    }

    /** Nome do jogador alvo (menus de punição / duração). */
    public String getTargetName() {
        return targetName;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
