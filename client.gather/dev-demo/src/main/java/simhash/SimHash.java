package simhash;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 必须使用 com.hankcs:hanlp:portable-1.7.0版本的分词库，否则不同版本的分词特性生成的simHash会不一样；
 */
public class SimHash {

    private static Map<String, Integer> WEIGHT_OF_NATURE = new HashMap<String, Integer>();
    static {
        // 个性化设置词性权重，这里将n：名词设置为2。（默认权重为1）
        WEIGHT_OF_NATURE.put("n",2);
    }

    private final String strSimHash;
    //private final int hashbits;


    private SimHash(String strSimHash) {
       // this.intSimHash = intSimHash;
        this.strSimHash = strSimHash;
    }

    private static int getWeight(String nature){
        int weight = 1;
        if (WEIGHT_OF_NATURE.containsKey(nature)) {
            weight = WEIGHT_OF_NATURE.get(nature);
        }
        return weight;
    }


    public String getSimHash(){
        return strSimHash;
    }

    public String[] get4SimHash(){
        int sSize = strSimHash.length() / 4;
        String[] ret = new String[4];
        ret[0] = strSimHash.substring(0,sSize);
        ret[1] = strSimHash.substring(sSize,2*sSize);
        ret[2] = strSimHash.substring(2*sSize,3*sSize);
        ret[3] = strSimHash.substring(3*sSize,sSize);
        return ret;
    }


    public static SimHash simHash(String text,int hashbits) {
        int[] v = new int[hashbits];
        //StringTokenizer stringTokens = new StringTokenizer(text);

        List<Term> termList = StandardTokenizer.segment(text);

        for (Term term:termList){

            String temp = term.word;
            String nature = term.nature.toString();

            if("w".equals(nature)){
                continue;
            }

            BigInteger t = hash(temp,hashbits);
            int weight = getWeight(nature);

            for (int i = 0; i < hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                if (t.and(bitmask).signum() != 0) {
                    v[i] += weight;
                } else {
                    v[i] -= weight;
                }
            }
        }
        BigInteger fingerprint = new BigInteger("0");
        StringBuffer simHashBuffer = new StringBuffer();
        for (int i = 0; i < hashbits; i++) {
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                simHashBuffer.append("1");
            }else{
                simHashBuffer.append("0");
            }
        }
        String strSimHash = simHashBuffer.toString();

        return new SimHash(strSimHash);
    }
    private static BigInteger hash(String source,int hashbits) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(hashbits).subtract(
                    new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }
/*    public int hammingDistance(SimHash other) {
        BigInteger x = this.intSimHash.xor(other.intSimHash);
        int tot = 0;
        //统计x中二进制位数为1的个数   
        //我们想想，一个二进制数减去1，那么，从最后那个1（包括那个1）后面的数字全都反了，对吧，然后，n&(n-1)就相当于把后面的数字清0，   
        //我们看n能做多少次这样的操作就OK了。         
        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }*/
    public static int getDistance(String str1, String str2) {
        int distance;
        if (str1.length() != str2.length()) {
            distance = -1;
        } else {
            distance = 0;
            for (int i = 0; i < str1.length(); i++) {
                if (str1.charAt(i) != str2.charAt(i)) {
                    distance++;
                }
            }
        }
        return distance;
    }

    public static void main(String[] args) {


       String string1 = "我没有李白手执两笔青锋抒尽心中万缕情丝的豪迈 \n" +
                "只有一支忙碌于黑夜从不停歇的孤单画笔 \n" +
                "摘下那妙手偶得之的灵感和一丝悸动 \n" +
                "留下生活中的那点点滴滴和唯美！";

        String string2 = "我没有李太白手执一笔青锋抒尽心中万缕情丝的豪迈 \n" +
                "只有一支忙碌于黑夜从不停歇的孤单画笔 \n" +
                "摘下那妙手偶得之的灵感和一丝悸动 \n" +
                "留下生活中的那点点滴滴和唯美！";

        String  string3 = "我没有李太白手执一笔青锋抒尽心中万缕情丝的豪迈 \n" +
                "只有一支忙碌于黑夜从不停歇的孤单画笔 \n" +
                "摘下那妙手偶得之的灵感和一丝悸动 \n" +
                "留下生活中的那点点滴滴和唯美！";



       // string1  = "This is a test string for testing";
      //  string2 = "This is a test string for testing, This is a test string for testing abcdef";
      //  string3 = "This is a test string for testing als";

         string1  = "我没有李太白手执一笔青锋";
         //string2 = "我没有李太白手执一笔青锋";
         string3 = "我没有李太白手执两笔青锋";

         string2 = "我并不是一个很活泼的人，就像此刻，站在队伍里面，我也没什么兴趣主动跟前后左右的新同学打招呼做自我介绍，当然如果有人愿意起这个头儿，我一定是那种乐于捧场、不吝微笑的群众角色。";

        SimHash hash1 = SimHash.simHash(string1,16);
        SimHash hash2 = SimHash.simHash(string2,16);
        SimHash hash3 = SimHash.simHash(string3,16);

        System.out.println(seg(hash1.getSimHash()) + "  " + hash1.getSimHash().length() + ",text length= " + string1.length());
        System.out.println(seg(hash2.getSimHash()) + "  " + hash2.getSimHash().length() + ",text length= " + string2.length());
        System.out.println(seg(hash3.getSimHash()) + "  " + hash3.getSimHash().length() + ",text length= " + string3.length());

        System.out.println(String.format(
                "dis(1,2)=%d, dis(1,3)=%d, dis(2,3)=%d"
                ,SimHash.getDistance(hash1.getSimHash(),hash2.getSimHash())
                 ,SimHash.getDistance(hash1.getSimHash(),hash3.getSimHash())
                ,SimHash.getDistance(hash3.getSimHash(),hash2.getSimHash())

                                ));

    }

    private static String seg(String str){

        int sSize = str.length() / 4;

        return str.substring(0,sSize) +"-" + str.substring(sSize,2*sSize) +"-" + str.substring(2*sSize,3*sSize) +"-"+str.substring(3*sSize);

    }
}  