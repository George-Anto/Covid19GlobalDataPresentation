import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Miner extends Thread {

    private final int prefix;
    private final Semaphore semaphore;
    private final List<Country> counrtiesToWriteInBlocks;
    private final List<Block> blockChain;
    private boolean isProgramClosed;
    private int totalBlocksToMine;
    private final Database database;

    private Miner() {
        prefix = 5;
        semaphore = new Semaphore(0);
        counrtiesToWriteInBlocks = new ArrayList<>();
        blockChain = new ArrayList<>();
        isProgramClosed = false;
        totalBlocksToMine = 0;
        database = new Database();
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
//                mine();
                database.InsertDB(mine());
                totalBlocksToMine--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\nProgram is terminated!");
        //For debugging purposes
//        for (Country country: counrtiesToWriteInBlocks) {
//            System.out.println(country.getCountryName() + " " + country.getConfirmed() + " " + country.getDeaths());
//        }
//        for (Block block: blockChain) {
//            System.out.println(block.getCountryData().getCountryName() + " " + block.getCountryData().getConfirmed() + " " + block.getCountryData().getDeaths());
//        }
    }

    public Block mine() {
        long timestamp = counrtiesToWriteInBlocks.get(0).getQueryTimestamp();
        Block block;
        try {
            block = new Block(database.getLastHashFromDB(), counrtiesToWriteInBlocks.get(0), timestamp);
        } catch (Exception e) {
            block = new Block("0", counrtiesToWriteInBlocks.get(0), timestamp);
        }
        block.mineBlock(prefix);
        blockChain.add(block);
        counrtiesToWriteInBlocks.remove(0);
        System.out.println("Node:" + (blockChain.size()-1) + " created");
        return block;
    }
}
