package dk.bison.rpg.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bison on 18-08-2016.
 */
public class StringUtil {
    private static Pattern firstIntPattern = Pattern.compile("\\d+");
    private static Pattern firstWordPattern = Pattern.compile("[a-zA-Z]+");

    public static int parseFirstInteger(String str)
    {
        Matcher matcher = firstIntPattern.matcher(str);
        matcher.find();
        int i = Integer.valueOf(matcher.group());
        return i;
    }

    public static String parseFirstWord(String str)
    {
        Matcher matcher = firstWordPattern.matcher(str);
        matcher.find();
        return matcher.group();
    }
}
