package com.juzicool.data.utils;

import com.juzicool.data.Juzi;
import org.apache.commons.lang3.StringUtils;

public class JuziUtils {

    /**
     * 句子评分值（范围：0-100），评分值越高，包含的鉴赏描述（tags,applyDesc,remark）越能鉴赏这个句子,同时在搜索时的权重越高。评分值越低，说明要完善相关信息，比如：tags，applyDesc，remark。（注意：applyDesc,remark等字段在表juzi_ext）
     *
     * value	说明
     * 0	tags，applyDesc，reamark 等字段都为空，比较难搜索，非常需要完善句子评论
     * 20	缺少tags，有applyDesc，remark,急需完善
     * 40	有tags，缺少applyDesc，remark，需要完善
     * 60	有tags，applyDesc,remark,但applyDesc、remark等字段描述的比较粗
     * 80	有tags，applyDesc，remark，applyDesc等字段描述的比较细
     * 100	评论得非常完美
     * @param juzi
     * @return
     */
    public static int remark(Juzi juzi){

        if(StringUtils.isEmpty(juzi.tags) && StringUtils.isEmpty(juzi.applyDesc) && StringUtils.isEmpty(juzi.remark)){
            return 0;
        }

        if(StringUtils.isEmpty(juzi.tags)){
            //缺少tags，有applyDesc，remark,急需完善
            return 20;
        }


        boolean isGoodTags = juzi.tags.length() > 5;
        boolean isGoodDesc = !StringUtils.isEmpty(juzi.remark) && !StringUtils.isEmpty(juzi.applyDesc);

        if(isGoodTags){
            if(isGoodDesc){
                return 80;
            }
            return 60;
        }else{
            if(isGoodDesc){
                return 60;
            }
            return 40;
        }
    }


    /**
     * 修正句子的长度，以便插入数据库不会出错。
     */
    public static void adjustJuziLength(Juzi juzi){
        juzi.content = subString(juzi.content,2048);
        juzi.author = subString(juzi.author,50);
        juzi.from = subString(juzi.from,50);
        juzi.category = subString(juzi.category,50);
        juzi.tags = subString(juzi.tags,300);
        juzi.applyDesc = subString(juzi.applyDesc,1024);
        juzi.remark = subString(juzi.remark,1024);

    }

    public static String subString(String text,int length){
        if(text == null){
            return null;
        }

        if(text.length() > length){
            return text.substring(0,length);
        }

        return  text;
    }


    public static String toIndexJson(Juzi juzi) {
        long updateAt = juzi.getUpdateAt() != null ? juzi.getUpdateAt().getTime(): 0L;
        return  "{" +
                "\"length\":" + juzi.getLength() +
                ",\"content\":\"" + juzi.content + "\"" +
                ",\"from\":\"" + juzi.from + "\"" +
                ",\"author\":\"" + juzi.author + "\"" +
                ",\"category\":\"" + juzi.category + "\"" +
                ",\"remark\":\"" + juzi.remark + "\"" +
                ",\"tags\":\"" + juzi.tags + "\"" +
                ",\"applyDesc\":\"" + juzi.applyDesc + "\"" +
                ",\"updateAt\":" + updateAt +
                '}';
    }
}
