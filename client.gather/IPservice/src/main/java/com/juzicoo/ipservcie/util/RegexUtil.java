package com.juzicoo.ipservcie.util;


import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RegexUtil {

    /**
     * 通过正则表达式提取text中的所有匹配内容。
     *
     * @param regex  正则表达式
     * @param text   所要提取的文本内容
     * @param groups 组参数，返回值将通过这个参数来返回对应的组值。
     * @return
     */
    public static String[][] getByRegex(String regex, String text,
                                        int[] groups) {
        if (groups == null || groups.length < 1) {
            throw new InvalidParameterException("groups should be assigned.");
        }
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE
                | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        Vector<String[]> vects = new Vector<String[]>();
        while (matcher.find()) {
            String[] vs = new String[groups.length];
            for (int i = 0; i < vs.length; i++) {
                vs[i] = matcher.group(groups[i]);
            }
            vects.add(vs);
        }
        return vects.toArray(new String[vects.size()][]);
    }

    /**
     * 通过正则表达式提取text中的所有匹配内容。
     *
     * @param regex 正则表达式
     * @param text  所要提取的文本内容
     * @param group 返回指定组的内容
     * @return
     */
    public static String[] getByRegex(String regex, String text, int group) {

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE
                | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        Vector<String> vects = new Vector<String>();
        while (matcher.find()) {

            vects.add(matcher.group(group));
        }
        return vects.toArray(new String[vects.size()]);
    }

    /**
     * 是否包含指定的文本。
     *
     * @param regex 正则表达式
     * @param text
     * @return
     */
    public static boolean containText(String regex, String text) {
        return getFristByRegex(regex, text, 0) != null;
    }

    /**
     * 通过正则表达式获取text中的第一个匹配内容。
     *
     * @param regex 正则表达式
     * @param text  所要提取的文本内容
     * @param group 返回指定组的内容
     * @return 若没有匹配结果，返回null
     */
    public static String getFristByRegex(String regex, String text, int group) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE
                | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(group);
        }
        return null;
    }

    /**
     * 通过正则表达式获取text中的第一个匹配内容。
     *
     * @param regex  正则表达式
     * @param text   所要提取的文本内容
     * @param groups 返回指定组的内容
     * @return 若没有匹配结果，返回null
     */
    public static String[] getFristByRegex(String regex, String text,
                                           int[] groups) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE
                | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String[] ret = new String[groups.length];
            for (int i = 0; i < groups.length; i++) {
                ret[i] = matcher.group(groups[i]);
            }

            return ret;
        }
        return null;
    }

}
