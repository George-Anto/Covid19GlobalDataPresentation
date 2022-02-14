import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Miner extends Thread {

    private static int prefix;
    private final Semaphore semaphore;
    private List<Country> counrtiesToWriteInBlocks;
    private static List<Block> blockChain;
    private boolean isProgramClosed;
    private int totalBlocksToMine;

    private Miner() {
        prefix = 5;
        semaphore = new Semaphore(0);
        counrtiesToWriteInBlocks = new ArrayList<>();
        blockChain = new ArrayList<>();
        isProgramClosed = false;
        totalBlocksToMine = 0;
    }

    private static final class MinerHolder {
        private static final Miner miner = new Miner();
    }

    public static Miner getInstance() {
        return MinerHolder.miner;
    }

    public void writeCountryToBlock(Country aCountry) {
        counrtiesToWriteInBlocks.add(aCountry);
        this.semaphore.release();
        this.totalBlocksToMine++;
    }

    public void programIsTerminated() {
        this.semaphore.release();
        isProgramClosed = true;
    }

    @Override
    public void run() {
        while (true) {
            try {
                semaphore.acquire();
                if (totalBlocksToMine == 0 && isProgramClosed) break;
                mine();
                totalBlocksToMine--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done creating all blocks!");
    }

    public void mine() {
        Block genesisBlock = new Block("0","Very important data",new Date().getTime());
        genesisBlock.mineBlock(prefix);
        blockChain.add(genesisBlock);
        System.out.println("Node:" + (blockChain.size()-1) + " created");
    }
}
