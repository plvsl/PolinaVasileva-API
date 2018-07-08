package enums;

public enum Languages {
    RU("ru"),
    EN("en"),
    UK("uk");

    public String languageLocale;

    Languages(String language) {
        this.languageLocale = language;
    }
}