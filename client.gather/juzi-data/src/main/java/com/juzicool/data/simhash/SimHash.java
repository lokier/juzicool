package com.juzicool.data.simhash;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final String text;
    //private final int hashbits;


    private SimHash(String text,String strSimHash) {
       // this.intSimHash = intSimHash;
        this.text = text;
        this.strSimHash = strSimHash;
    }

    private static int getWeight(String nature){
        int weight = 1;
        if (WEIGHT_OF_NATURE.containsKey(nature)) {
            weight = WEIGHT_OF_NATURE.get(nature);
        }
        return weight;
    }

    public String getText(){
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimHash simHash = (SimHash) o;
        return Objects.equals(strSimHash, simHash.strSimHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strSimHash);
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
        ret[3] = strSimHash.substring(3*sSize);
        return ret;
    }

    /**
     *     文本长度16以下： simhahs长度16（分4段），海明距离为3（4以下的就是重合文本）
     *     文本长度55以下： simhahs长度32（分4段），海明距离为3
     *     文本长度55以上： simhahs长度64（分4段），海明距离为3
     */
    public static SimHash simHash(String text) {
        String t = text.trim();
        if(t.length() <=16){
            return simHash(text,16);
        }
        if (t.length() <= 55) {
            return simHash(text,32);
        }

        return simHash(text,64);

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

        return new SimHash(text,strSimHash);
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
            distance = 128;
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


       String string1 = "我人生第一次看芭蕾舞剧是跟着F去的，领舞是他妈妈。那天结束之后他去后台找他妈妈，我也跟着去了。 \n" +
               "F指着我介绍：“妈，这是我同桌。” \n" +
               "那是我第一次见他妈妈，特别紧张（虽然那时我们还只是纯洁的同桌关系），当时我想说阿姨好，恭喜你演出成功，结果脑子一抽，张口就变成了：“妈，恭喜你演出成功。” \n" +
               "说完之后大家都愣了，然后一阵爆笑，我恨不得找到地洞钻进去。 \n" +
               "第二天我偷偷问F他妈怎么说我，F憋着笑，说：“你妈说你挺可爱。” \n" +
               "再一次想找个地洞钻进去。！";

        String string2 = "我人生第一次看芭蕾舞剧是跟着F去的，领舞是他妈妈。那天结束之后他去后台找他妈妈，我也跟着去了。 \n" +
                "F指着我介绍：“妈，这是我同桌。” \n" +
                "那是我第一次见他妈妈，特别紧张（虽然那时我们还只是纯洁的同桌关系），当时我想说阿姨好，恭喜你演出成功，结果脑子一抽，张口就变成了：“妈，恭喜你演出成功。” \n" +
                "说完之后大家都愣了，然后一阵爆笑，我恨不得找到地洞钻进去。 \n" +
                "第二天我偷偷问F他妈怎么说我，F憋着笑，说：“你妈说你挺可爱。” \n" +
                "再一次想找个地洞钻进去。";

        String  string3 = "我人生第一次看芭蕾舞剧是跟着F去的，领舞是他妈妈。那天结束之后他去后台找他妈妈，我也跟着去了。 \n" +
                "F指着我介绍：“妈，这是我同桌。” \n" +
                "那是我第一次见他妈妈，特别紧张（虽然那时我们还只是纯洁的同桌关系），当时我想说阿姨好，恭喜你演出成功，结果脑子一抽，张口就变成了：“妈，恭喜你演出成功。” \n" +
                "说完之后大家都愣了，然后一阵爆笑，我恨不得找到地洞钻进去。 \n" +
                "第二天我偷偷问F他妈怎么说我，F憋着笑，说：“你妈说你挺可爱。” \n" +
                "再一次想找个地洞钻进去。";



        string1  = "没有恒古不变的感情，没有永垂不朽的情爱，只有一如既往的信仰，只有固执到底的执念。";
        string2 = "你折断了我的翅膀，却怪我不会飞翔。 \\n你将我宠成了公主，却怪我娇蛮无礼。";
      //  string3 = "This is a test string for testing als";


        SimHash hash1 = SimHash.simHash(string1);
        SimHash hash2 = SimHash.simHash(string2);
        SimHash hash3 = SimHash.simHash(string3);

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