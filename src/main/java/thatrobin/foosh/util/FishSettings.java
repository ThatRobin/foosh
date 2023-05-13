package thatrobin.foosh.util;

public class FishSettings {

    Float minLength;
    Float maxLength;

    public FishSettings() {
    }

    public FishSettings minLength(Float minLength) {
        this.minLength = minLength;
        return this;
    }

    public FishSettings maxLength(Float maxLength) {
        this.maxLength = maxLength;
        return this;
    }


    public Float getMinLength() {
        return minLength;
    }

    public Float getMaxLength() {
        return maxLength;
    }
}
