package panetina.util;

import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import java.util.Locale;

public class TeamColorUtil {
    public static TextColor parseColor(String colorStr) {
        if (colorStr == null) return TextColor.fromFormatting(Formatting.WHITE);
        if (colorStr.startsWith("#")) {
            try {
                return TextColor.fromRgb(Integer.parseInt(colorStr.substring(1), 16));
            } catch (NumberFormatException ignored) {}
        }
        Formatting formatting = Formatting.byName(colorStr.toLowerCase(Locale.ROOT));
        if (formatting != null && formatting.isColor()) {
            return TextColor.fromFormatting(formatting);
        }
        return TextColor.fromFormatting(Formatting.WHITE);
    }

    public static Formatting parseFormatting(String colorStr) {
        if (colorStr == null) return null;
        if (colorStr.startsWith("#")) {
            return null; // Scoreboard teams only support named colors
        }
        return Formatting.byName(colorStr.toLowerCase(Locale.ROOT));
    }
}