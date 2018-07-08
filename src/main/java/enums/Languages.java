package enums;

public enum Languages {
    RU("ru"),
    EN("en"),
    UK("uk"),
    NON_VALID("ch");

    public String languageLocale;

    Languages(String language) {
        this.languageLocale = language;
    }
}