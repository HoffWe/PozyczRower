package AI2.Enums;

import AI2.Util.LanguageManager;

public enum BikeStatus {
    AVAILABLE("bike.status.available"),
    RENTED("bike.status.rent"),
    SERVICING("bike.status.service"),
    UNAVAILABLE("bike.status.unavible")

    ;
    private final String key;
    private BikeStatus(String key) {
        this.key = key;
    }
    public String getDisplayName() {
        return LanguageManager.getString(key);
    }


}
