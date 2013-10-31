package models;

public enum Role {
    ADMIN, USER;

    public String toString() {
        return play.i18n.Messages.get("role." + this.name().toLowerCase());
    }
}