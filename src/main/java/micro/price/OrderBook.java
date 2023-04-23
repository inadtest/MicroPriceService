package micro.price;

public interface OrderBook {
    int numLevels();
    double bidPrice(int level);
    int bidSize(int level);
    double askPrice(int level);
    int askSize(int level);
}
