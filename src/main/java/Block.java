import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Block {
    private String hash;
    private String previousHash;
    //private String data;
    private Country countryData;
    private long timeStamp;
    private int nonce;

    public Block(String previousHash, Country country, long timeStamp) {
        this.previousHash = previousHash;
        //this.data = data;
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
    public String calculateBlockHash(){
        String dataToHash = previousHash + String.valueOf(timeStamp)+
//                String.valueOf(nonce) + data;
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
