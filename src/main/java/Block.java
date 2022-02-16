import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//The Block class is the same as the code provided from the lectures with
//some minor modifications that are shown below
public class Block {
    private String hash;
    private final String previousHash;
    //Instead of some String data, the block has a field that is a Country object that is passed to it
    //when the block is constructed and that will be the data that will be stored in it
    private final Country countryData;
    private final long timeStamp;
    private int nonce;

    public Block(String previousHash, Country country, long timeStamp) {
        this.previousHash = previousHash;
        countryData = country;
        this.timeStamp = timeStamp;
        hash = calculateBlockHash();
    }

    public Country getCountryData() {
        return this.countryData;
    }

    public String mineBlock(int prefix){
        String prefixString = new String(new char[prefix]).replace('\0','0');
        while (!hash.substring(0,prefix).equals(prefixString)){
            nonce++;
            hash = calculateBlockHash();
        }
        return hash;
    }
    public String getHash() {
        return hash;
    }
    public String getPreviousHash() {
        return previousHash;
    }
    public int getNonce() {
        return nonce;
    }
    public String calculateBlockHash(){
        String dataToHash = previousHash + String.valueOf(timeStamp)+
                //The country data that are stored in the block, are passed as a String parameter
                //in the calculation of the hash so if they were to be changed the validation of
                //the blockchain would fail
                String.valueOf(nonce) + countryData.toString();
        MessageDigest digest = null;
        byte[] bytes = null;
        try
        {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        StringBuffer buffer = new StringBuffer();
        for (byte b: bytes)
            buffer.append(String.format("%02x",b));

        return buffer.toString();
    }
}
