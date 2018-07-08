package core;

import java.util.ArrayList;
import java.util.List;

public class YandexSpellerResponse {
    public Integer code;
    public Integer pos;
    public Integer row;
    public Integer col;
    public Integer len;
    public String word;
    public List<String> s = new ArrayList<>();
}