package micro.price;

import java.util.HashMap;
import java.util.Map;

public class OrderBookImpl implements OrderBook {
    private final int numLevels;
    private final Map<Integer, Level> bidLevels;
    private final Map<Integer, Level> askLevels;

    public OrderBookImpl(int numLevels) {
        this.numLevels = numLevels;
        bidLevels = new HashMap<>();
        askLevels = new HashMap<>();

        for (int i = 0; i < numLevels; i++) {
            bidLevels.put(i, new Level(0, 0));
            askLevels.put(i, new Level(0, 0));
        }
    }
    @Override
    public int numLevels() {
        return numLevels;
    }

    @Override
    public double bidPrice(int level) {
        return bidLevels.get(level).getPrice();
    }

    @Override
    public int bidSize(int level) {
        return bidLevels.get(level).getSize();
    }

    @Override
    public double askPrice(int level) {
        return askLevels.get(level).getPrice();
    }

    @Override
    public int askSize(int level) {
        return askLevels.get(level).getSize();
    }

    public void updateBid(int level, double price, int size) {
        bidLevels.put(level, new Level(price, size));
    }

    public void updateAsk(int level, double price, int size) {
        askLevels.put(level, new Level(price, size));
    }

    private static class Level {
        private final double price;
        private final int size;

        public Level(double price, int size) {
            this.price = price;
            this.size = size;
        }

        public double getPrice() {
            return price;
        }

        public int getSize() {
            return size;
        }
    }
}
