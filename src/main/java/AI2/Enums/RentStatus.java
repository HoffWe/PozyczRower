package AI2.Enums;

import AI2.Util.LanguageManager;

public enum RentStatus {
    ACTIVE("rent.status.active"),
    CLOSED("rent.status.closed"),
    OVERDUE("rent.status.overdue"),
    FINISHED("rent.status.finished"),
    SCHEDULED("rent.status.scheduled");

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
