package fr.corenting.epitime_ng.data;

import java.util.Random;

/**
 * Created by KingGreed on 08/06/2014.
 */
public class FlatUIColor {

    private Random rn;
    private int currentColor = 0;

    private final String[] flatUiColors       = {"#f39c12", "#d35400", "#7f8c8d", "#c0392b", "#8e44ad", "#16a085", "#2980b9", "#27ae60"};
    private final String[] flatUiColorsShadow = {"#cf850f", "#ba4a00", "#677273", "#992d22", "#6d3485", "#12806a", "#1c5880", "#1d8046"};

    public FlatUIColor() {
        this.newRandomSeed();
        this.nextRandomColor();
    }

    public FlatUIColor(int seed) {
        this.newSeed(seed);
        this.nextRandomColor();
    }

    public void newRandomSeed() {
        this.rn = new Random();
    }

    public void newSeed(int seed) {
        this.rn = new Random(seed);
    }

    public void nextRandomColor() {
        this.currentColor = this.rn.nextInt(this.flatUiColors.length);
    }

    public void setRandomColor(int color) {
        this.currentColor = color % this.flatUiColors.length;
    }

    public String getColor() {
        return this.flatUiColors[this.currentColor];
    }

    public String getShadowColor() {
        return this.flatUiColorsShadow[this.currentColor];
    }


}
