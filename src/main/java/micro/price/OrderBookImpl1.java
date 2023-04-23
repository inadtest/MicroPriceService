package micro.price;

public class OrderBookImpl1 implements OrderBook {
    private int numLevels;
    private double[] bidPrices;
    private int[] bidSizes;
    private double[] askPrices;
    private int[] askSizes;

    public OrderBookImpl1(int numLevels) {
        this.bidPrices = new double[numLevels];
        this.bidSizes = new int[numLevels];
        this.askPrices = new double[numLevels];
        this.askSizes = new int[numLevels];
        this.numLevels = numLevels;
    }

    @Override
    public int numLevels() {
        return numLevels;
    }

    @Override
    public double bidPrice(int level) {
        //if(level < 0 || level >= numLevels)
        return bidPrices[level];
    }

    @Override
    public int bidSize(int level) {
        return bidSizes[level];
    }

    @Override
    public double askPrice(int level) {
        return askPrices[level];
    }

    @Override
    public int askSize(int level) {
        return askSizes[level];
    }

    public void updateOrderBook(int level, int side, long scaledPrice, int qty) {
        double price = scaledPrice / 1e9; // Convert scaled price to actual price
        if (side == 0) { // Update bid side
            bidPrices[level] = price;
            bidSizes[level] = qty;
        } else { // Update ask side
            askPrices[level] = price;
            askSizes[level] = qty;
        }
    }
}
