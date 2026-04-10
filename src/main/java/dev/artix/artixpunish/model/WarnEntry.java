package dev.artix.artixpunish.model;

/**
 * Um aviso individual aplicado a um jogador.
 */
public final class WarnEntry {

    private final String reason;
    private final String staff;
    private final long created;

    public WarnEntry(String reason, String staff, long created) {
        this.reason = reason != null ? reason : "";
        this.staff = staff != null ? staff : "";
        this.created = created;
    }

    public String getReason() {
        return reason;
    }

    public String getStaff() {
        return staff;
    }

    public long getCreated() {
        return created;
    }
}
