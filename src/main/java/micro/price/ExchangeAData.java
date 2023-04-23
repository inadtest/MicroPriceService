package micro.price;

public class ExchangeAData {
    int securityId;
    double scaledMicroPrice;

    public ExchangeAData(int securityId, double scaledMicroPrice) {
        this.securityId = securityId;
        this.scaledMicroPrice = scaledMicroPrice;
    }
    public int getSecurityId() {
        return securityId;
    }

    public void setSecurityId(int securityId) {
        this.securityId = securityId;
    }

    public double getScaledMicroPrice() {
        return scaledMicroPrice;
    }

    public void setScaledMicroPrice(double scaledMicroPrice) {
        this.scaledMicroPrice = scaledMicroPrice;
    }


}
