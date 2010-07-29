package pl.eurekin.util;

/**
 *
 * @author Rekin
 */
public class EnumHelper {

    public static String getHumanReadableName(Enum algorithmType) {
        String[] splitNames = algorithmType.name().toLowerCase().split("_");
        StringBuffer fixedName = new StringBuffer();
        for (int i = 0; i < splitNames.length; i++) {
            String firstLetter = splitNames[i].substring(0, 1).toUpperCase();
            String restOfWord = splitNames[i].substring(1);
            String spacer = i == splitNames.length ? "" : " ";
            fixedName.append(firstLetter).append(restOfWord).append(spacer);
        }
        return fixedName.toString();
    }
}
