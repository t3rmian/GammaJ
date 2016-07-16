package io.github.t3r1jj.gammaj.model.temperature;

public class TemperatureSimpleFactory {

    private String type;
    private boolean isSrgb;
    
    public TemperatureSimpleFactory(String type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIsSrgb(boolean isSrgb) {
        this.isSrgb = isSrgb;
        type = null;
    }

    public boolean isIsSrgb() {
        return isSrgb || "srgb".equals(type);
    }
    
    public RgbTemperature createTemperature(double temperature) {
        if (type != null) {
            switch (type) {
                case "rgb": return new RgbTemperature(temperature);
                case "srgb": return new SrgbTemperature(temperature);
                    default: return new RgbTemperature(temperature);
            }
        } else {
            if (isSrgb) {
                return new SrgbTemperature(temperature);
            } else {
                return new RgbTemperature(temperature);
            }
        }
    }
}
