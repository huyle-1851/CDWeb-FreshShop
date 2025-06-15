package vn.fshop.model;

/**
 * Enum for user roles in the system
 */
public enum UserRoleEnum {
    USER("User"),
    ADMIN("Administrator");

    private final String displayName;

    UserRoleEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
