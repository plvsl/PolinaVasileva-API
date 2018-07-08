package enums;

public enum Parameters {
    TEXT("text"),
    OPTIONS("options"),
    LANGUAGE("lang"),
    FORMAT("format");

    public String parameter;

    Parameters(String parameter) {
        this.parameter = parameter;
    }
}