package demo;

public class DamerauLevenshteinDistance2 {
    private int getDistance(String s1,String s2){
        int m= (s1==null)?0:s1.length();
        int n= (s2==null)?0:s2.length();
        if(m==0){
            return n;
        }
        if(n==0){
            return m;
        }
        int[] p=new int[n+1];
        int[] p1=new int[n+1];
        int[] t=new int[n+1];
        for(int i=0;i<p.length;i++){
            p[i]=i;
        }
        int d=0;
        int cost=0;
        char s1_c,s2_c;
        for(int i=0;i<m;i++){
            t[0]=i+1;
            s1_c=s1.charAt(i);
            for(int j=1;j<p.length;j++){
                s2_c=s2.charAt(j-1);
                cost=(s1_c==s2_c)?0:1;
                d=Math.min(Math.min(t[j-1],p[j])+1,p[j-1]+cost);
                if(i>0&&j>1&&s1_c==s2.charAt(j-2)&&s1.charAt(i-1)==s2_c){
                    d=Math.min(d,p1[j-2]+cost);
                }
                t[j]=d;
            }
            p1=p;
            p=t;
            t=new int[n+1];
        }
        return d;
    }

    public float getSimilarity(String s1,String s2){
        if(s1==null||s2==null){
            if(s1==s2){
                return 1.0f;
            }
            return 0.0f;
        }
        float d=getDistance(s1,s2);
        System.out.println("getDistance:" + d);

        return 1-(d/Math.max(s1.length(), s2.length()));
    }

    public static void main(String[] args) {

        String s1="aborad";
        String s2="aboard";
        DamerauLevenshteinDistance2 dld=new DamerauLevenshteinDistance2();
        System.out.println(dld.getSimilarity(s1, s2));
        DamerauLevenshteinDistance2 dld2=new DamerauLevenshteinDistance2();
        System.out.println(dld2.getSimilarity(s1, s2));
    }




}
