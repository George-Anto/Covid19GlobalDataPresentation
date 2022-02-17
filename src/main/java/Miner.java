import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

//The instance of this class that is created is in charge of getting the data that the user requested to see
//and put them in a block, then try to mine the block using the code provided from the lectures
//The mining of the block takes several seconds so in order for the user to be able to perform other
//searches at that time or view some statistics, this process is happening on another thread
public class Miner extends Thread {

    //Except the fields that are already implemented on the provided code...
    private final int prefix;
    //We use a semaphore in order for the thread to sleep when no data is given to it, so it can
    //start the mining of a new block when some country data arrive
    private final Semaphore semaphore;
    //This list is holding all the country objects (data) that have to be written in a block in the future
    private final List<Country> countriesToWriteInBlocks;
    private final List<Block> blockChain;
    //boolean variable to indicate that the user has terminated the program and as soon as the
    //miner finishes its tasks should terminate too
    private boolean isProgramClosed;
    //A counter to keep track of the total blocks the miner has to mine before it can terminate
    private int totalBlocksToMine;
    private final Database database;

    //The fields are initialized
    private Miner() {
        prefix = 5;
        semaphore = new Semaphore(0);
        countriesToWriteInBlocks = new ArrayList<>();
        blockChain = new ArrayList<>();
        isProgramClosed = false;
        totalBlocksToMine = 0;
        database = new Database();
    }

    //We use the singleton design pattern here too, and is very crucial to only have one instance of the
    //Miner class so the other classes give work to that instance to calculate the blocks correctly
    //We can not do this work in parallel since a block must have finished mined before the next one is started
    //because it will need to have the hash of the previous block at its disposal
    private static final class MinerHolder {
        private static final Miner miner = new Miner();
    }

    //The fact that we retrieve the same instance of the Miner class every time we call this method
    //is very handy because we do not need to pass the miner object around ourselves .e.g as methods parameter
    public static Miner getInstance() {
        return MinerHolder.miner;
    }

    //When this method is called, a new country object is written in the list and the semaphore is released so
    //that the miner is being awakened (if already asleep)
    //and ready to mine the next block with the new country data added to its list
    //also the counter is incremented
    public void writeCountryToBlock(Country aCountry) {
        countriesToWriteInBlocks.add(aCountry);
        this.semaphore.release();
        this.totalBlocksToMine++;
    }

    //when this method is called, the boolean variable that indicates that the user has terminated the
    //program is set to true and the semaphore is released so, if the miner was asleep when the program is
    //terminated, it can detect the isProgramClosed = true and terminate too
    public void programIsTerminated() {
        this.semaphore.release();
        isProgramClosed = true;
    }

    //The method that is executed when the start() method is called in the Menu class
    @Override
    public void run() {
        //this loop will run until the break command on line 75 is ran
        while (true) {
            try {
                //the miner instance will try and acquire the semaphore, if there is country data added to
                //the list, the semaphore will be acquired and a block will be mined, or else the miner
                //will sleep here until data are added to the list
                //If the semaphore is released many times due to the user making many searches before the first
                //block is mined, this is no problem because the miner will then acquire the semaphore the same
                //amount of times and then insert the country data from its list to the respective blocks
                semaphore.acquire();
                //If the semaphore was acquired, the counter is 0, which means that all blocks that the miner
                //has to create are done (or never incremented above 0) and also that the boolean variable
                //indicates that the user terminated the program, then the miner also terminates
                //If any of the above conditions is not true the miner will keep creating blocks
                //until there are all done
                if (totalBlocksToMine == 0 && isProgramClosed) break;
                //If the semaphore was acquired and there is a block to be mined, then the mine() method is
                //called, and it returns a created block that is passed as a parameter to the insert method
                //of the database instance so the data of the block can be inserted into the mySQL database
                database.InsertDB(mine());
                //Also, the counter that indicates how many blocks must be created in the future based
                //on the current country data in the list, is decremented by one
                totalBlocksToMine--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //When the miner reaches this line of code, it means that the user has requested that the program
        //is terminated and also that the miner instance has mined all the blocks that it had to (and passed
        //them to the database, so they can be saved there) and the program is indeed terminated
        System.out.println("\nProgram is terminated!");
    }

    //This method creates the new block with the respectively country data and returns the new block to the caller
    public Block mine() {
        //Gets the timestamp of the first country object of the list
        long timestamp = countriesToWriteInBlocks.get(0).getQueryTimestamp();
        Block block;
        try {
            //Creates the new block, with previous hash retrieved from the hash of the last entry
            //of data from the database, so it sends a query to the database to find it
            //The country data that are injected in the block are taken from the first place of the
            //list that stores them before the mining
            block = new Block(database.getLastHashFromDB(), countriesToWriteInBlocks.get(0), timestamp);
        } catch (Exception e) {
            //This catch block is executed if there is a problem connecting to the database
            //in order to not crash the program
            block = new Block("0", countriesToWriteInBlocks.get(0), timestamp);
        }
        //We mine the block and add it to the blockchain list
        block.mineBlock(prefix);
        blockChain.add(block);
        //We then remove the country object from the list because we have stored it in the newly created block
        countriesToWriteInBlocks.remove(0);
        //Visual confirmation that the block with its country data was mined
        System.out.println("Block :" + (blockChain.size()-1) + " created");
        //We return the block to the caller
        return block;
    }
}
