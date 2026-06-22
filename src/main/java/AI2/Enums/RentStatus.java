package AI2.Enums;

import AI2.Util.LanguageManager;

public enum RentStatus {
    ACTIVE("rent.status.active"),
    OVERDUE("rent.status.overdue"),
    FINISHED("rent.status.finished"),
    SCHEDULED("rent.status.scheduled"),
    PENDING("rent.status.pending"),
    CANCELLED("rent.status.cancelled");

    private final String key;

    RentStatus(String key) {
        this.key = key;
    }

    public String getDisplayName() {

        return LanguageManager.getString(
                key
        );
    }
}
