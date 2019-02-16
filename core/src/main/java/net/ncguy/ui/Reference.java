package net.ncguy.ui;

import com.badlogic.gdx.graphics.Color;

public class Reference {

    public static class Colours {

        public static final float hoverGlowHue = 60;
        public static final float defaultSat = .3f;
        public static final float defaultVal = .5f;

        public static final Color DEFAULT_GREY = new Color(.32f, .32f, .32f, 1.f);
        public static final Color HOVER_GREY = DEFAULT_GREY.cpy().sub(.1f, .1f, .1f, 1f);
        public static final Color HOVER_COLOUR = FromHue(hoverGlowHue);

        public static Color FromHue(float hue) {
            return FromHue(hue, 1.f);
        }
        public static Color FromHue(float hue, float alpha) {
            final Color c = new Color().fromHsv(hue, defaultSat, defaultVal);
            c.a = alpha;
            return c;
        }

    }

}
