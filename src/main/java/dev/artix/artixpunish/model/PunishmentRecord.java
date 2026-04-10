package dev.artix.artixpunish.model;

import java.util.UUID;

/**
 * Registro de ban ou mute: motivo, staff, data e expiração (-1 = permanente).
 */
public final class PunishmentRecord {

    private final UUID uuid;
    private String lastKnownName;
    private final String reason;
    private final String staff;
    private final long created;
    /** Epoch millis; -1 = permanente */
    private final long expiresAt;

    public PunishmentRecord(UUID uuid, String lastKnownName, String reason, String staff, long created, long expiresAt) {
        this.uuid = uuid;
        this.lastKnownName = lastKnownName != null ? lastKnownName : "";
        this.reason = reason != null ? reason : "";
        this.staff = staff != null ? staff : "";
        this.created = created;
        this.expiresAt = expiresAt;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getLastKnownName() {
        return lastKnownName;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName != null ? lastKnownName : "";
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

    public long getExpiresAt() {
        return expiresAt;
    }

    public boolean isPermanent() {
        return expiresAt < 0;
    }

    public boolean isExpired() {
        if (isPermanent()) {
            return false;
        }
        return System.currentTimeMillis() >= expiresAt;
    }
}
