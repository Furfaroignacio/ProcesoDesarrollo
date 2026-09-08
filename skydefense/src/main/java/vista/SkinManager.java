package vista;

public class SkinManager {
    private static Skin actual = Skin.COSMOS;

    public static Skin getActual()       { return actual; }
    public static void setActual(Skin s) { actual = s; }
}