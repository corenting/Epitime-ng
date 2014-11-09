package fr.corenting.epitime_ng.data;

import java.util.Random;

public class FlatUIColor {

    private Random rn;
    private int currentColor = 0;

    private final String[] flatUiColors = {"#ffc107", "#ff5722", "#9e9e9e", "#f44336", "#9c27b0", "#009688", "#2196f3", "#4caf50", "#607d8b", "#00bcd4", "#3f51b5"};
    private final String[] flatUiColorsShadow = {"#ff8f00", "#d84315", "#424242", "#c62828", "#6a1b9a", "#00695c", "#1565c0", "#2e7d32", "#37474f", "#00838f", "#283593"};

    public FlatUIColor(int seed) {
        this.newSeed(seed);
        this.nextRandomColor();
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
