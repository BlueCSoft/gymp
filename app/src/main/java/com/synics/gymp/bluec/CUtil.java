package com.synics.gymp.bluec;

/**
 * Created by Administrator on 2016/10/25.
 */

import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CUtil {
    /**
     * formatStr 使用字符串param替换字符串dstr中为rStr的所有子字符串
     *
     * @param dstr  原字符串
     * @param param 参数
     * @param rStr  被替换的子字符串
     * @return 替换后的字符串
     */
    public static String formatStr(String dstr, String param, String rStr) {
        StringBuffer temp = new StringBuffer();
        int i = 0; // 寻找子字符串的开始位置
        int l = dstr.length();
        int k = rStr.length();

        while ((i = dstr.indexOf(rStr)) != -1) {
            temp.append(dstr.substring(0, i));
            temp.append(param);
            l -= i + k;
            dstr = dstr.substring(i + k);
        }
        if (!dstr.equals("")) temp.append(dstr);
        return temp.toString();
    }

    /**
     * formatStr 使用数组sstr中的项顺序替换字符串dstr中为rStr的子字符串
     *
     * @param dstr   原字符串
     * @param params 参数数组
     * @param rStr   被替换的子字符串
     * @return 替换后的字符串
     */
    public static String formatStr(String dstr, String[] params, String rStr) {
        StringBuffer temp = new StringBuffer();
        int i = 0; // 寻找子字符串的开始位置
        int j = 0; // 数组下标
        int l = dstr.length();
        int k = rStr.length();
        // System.out.println("l="+l);
        while (j < params.length && (i = dstr.indexOf(rStr)) != -1) {
            temp.append(dstr.substring(0, i));
            temp.append(params[j]);
            l -= i + k;
            dstr = dstr.substring(i + k);
            j++;
        }
        if (!dstr.equals("")) temp.append(dstr);
        return temp.toString();
    }

    /**
     * 获取当前日期
     *
     * @return String，返回当前日期字符串，格式为yyyy.mm.dd
     */
    public static String getOrigDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        int Y = calendar.get(Calendar.YEAR);
        int M = 1 + calendar.get(Calendar.MONTH);
        int D = calendar.get(Calendar.DAY_OF_MONTH);
        return Y + "-" + Integer.toString(100 + M).substring(1) + "-" + Integer.toString(100 + D)
                .substring(1);
    }

    /**
     * 获取当前日期
     *
     * @param dChar String，年月日的分割符
     * @return String 返回当前日期字符串，格式为yyyy dChar mm dChar dd
     */
    public static String getOrigDate(String dChar) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        int Y = calendar.get(Calendar.YEAR);
        int M = 1 + calendar.get(Calendar.MONTH);
        int D = calendar.get(Calendar.DAY_OF_MONTH);
        return Y + dChar + Integer.toString(100 + M).substring(1) + dChar + Integer.toString(
                100 + D).substring(1);
    }

    /**
     * 2013-01-17
     */
    public static String getChinaDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        int Y = calendar.get(Calendar.YEAR);
        int M = 1 + calendar.get(Calendar.MONTH);
        int D = calendar.get(Calendar.DAY_OF_MONTH);
        return Y + "年" + Integer.toString(M) + "月" + Integer.toString(D) + "日";
    }

    /**
     * 格式化指定日期为中文格式
     *
     * @param sDate String 要格式化的日期
     * @param dChar String 年月日间的分割符
     * @return String 中文格式的日期字符串 yyyy年mm月dd日
     */
    public static String dateToChinaFormat(String sDate, String dChar) {
        int[] ymd = new int[3];
        ymd[0] = Integer.parseInt(sDate.substring(0, 4));
        ymd[1] = Integer.parseInt(sDate.substring(5, 7));
        ymd[2] = Integer.parseInt(sDate.substring(8, 10));
        return ymd[0] + "年" + ymd[1] + "月" + ymd[2] + "日";
    }

    /**
     * 获取当前时间
     *
     * @return String 返回当前时间的字符串，格式为:yyyy.mm.dd H:mm:s
     */
    public static String getOrigTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());

        int Y = calendar.get(Calendar.YEAR);
        int M = 1 + calendar.get(Calendar.MONTH);
        int D = calendar.get(Calendar.DAY_OF_MONTH);
        int H = calendar.get(Calendar.HOUR_OF_DAY);
        int MM = calendar.get(Calendar.MINUTE);
        int S = calendar.get(Calendar.SECOND);
        return Y + "-" + Integer.toString(100 + M).substring(1) + "-" + Integer.toString(100 + D)
                .substring(1) + " " + H + ":" + MM + ":" + S;
    }

    public static String getSTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());

        int Y = calendar.get(Calendar.YEAR);
        int M = 1 + calendar.get(Calendar.MONTH);
        int D = calendar.get(Calendar.DAY_OF_MONTH);
        int H = calendar.get(Calendar.HOUR_OF_DAY);
        int MM = calendar.get(Calendar.MINUTE);
        int S = calendar.get(Calendar.SECOND);
        return Y + Integer.toString(100 + M).substring(1) + Integer.toString(100 + D)
                .substring(1) + Integer.toString(100 + H).substring(1) +
                Integer.toString(100 + MM).substring(1) + Integer.toString(100 + S).substring(1);
    }

    /**
     * 获取当前日期
     *
     * @param dChar String，年月日的分割符
     * @return String
     */
    public static String getOrigTime(String dChar) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        int Y = calendar.get(Calendar.YEAR);
        int M = 1 + calendar.get(Calendar.MONTH);
        int D = calendar.get(Calendar.DAY_OF_MONTH);
        int H = calendar.get(Calendar.HOUR_OF_DAY);
        int MM = calendar.get(Calendar.MINUTE);
        int S = calendar.get(Calendar.SECOND);
        return Y + "-" + Integer.toString(100 + M).substring(1) + "-" + Integer.toString(100 + D)
                .substring(1) + " " + H + ":" + MM + ":" + S;
    }

    public static String getTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        int H = calendar.get(Calendar.HOUR_OF_DAY);
        int MM = calendar.get(Calendar.MINUTE);
        int S = calendar.get(Calendar.SECOND);
        int MS = calendar.get(Calendar.MILLISECOND);
        return H + ":" + MM + ":" + S + "." + MS;
    }

    public static String getTimeSSS() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return formatter.format(new java.util.Date());
    }

    /**
     * 替换字符串
     */
    public static String replaceStr(String dstr, String param, String rStr) {
        return formatStr(dstr, param, rStr);
    }

    /**
     * 重复子字符串的次数组成一个字符串
     *
     * @param subStr String
     * @param rCount int
     * @return String
     */
    public static String StringOfChar(String subStr, int rCount) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < rCount; i++)
            buffer.append(subStr);
        return buffer.toString();
    }

    public static String replaceXmlSpChar(String s) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < s.length(); i++)
            switch (s.charAt(i)) {
                case '&':
                    buffer.append("&amp;");
                    break;
                case '<':
                    buffer.append("&lt;");
                    break;
                case '"':
                    buffer.append("&quot;");
                    break;
                default:
                    buffer.append(s.charAt(i));
            }
        return buffer.toString();
    }

    public static String NVL(String value) {
        if (value == null) {
            return "";
        } else {
            return replaceXmlSpChar(value); // value.trim();
        }
    }

    public static String rightTrim(String value) {
        int i = value.length();
        for (; i > 0; i--)
            if (value.charAt(i - 1) != ' ') break;
        return value.substring(0, i);
    }

    public static String rightStr(String value, int nlen) {
        int i = value.length();
        if (i > nlen) return value.substring(i - nlen);
        return value;
    }

    public static String removeCR(String sql) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < sql.length(); i++)
            if (sql.charAt(i) != '\r') buffer.append(sql.charAt(i));
        return buffer.toString();
    }

    /**
     * 处理特殊字符
     */
    public static String replaceSpChar(String s) {
        StringBuffer buffer = new StringBuffer();
        s = formatStr(s, "\n", "ん");
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (c == '\'') buffer.append('\'');
            buffer.append(c);
        }
        return buffer.toString();
    }

    public static String formatStr(String str) {
        String stred = str;
        try {
            stred = new String(str.getBytes("ISO8859-1"), "GBK");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return stred;
    }

    public static String formatChinaMonth(String pubyf) {
        return pubyf.substring(0, 4) + "年" + pubyf.substring(5, 7) + "月";
    }

    public static String formatToChinaDate(String sDate) {
        return sDate.substring(0, 4) + "年" + Integer.parseInt(sDate.substring(5, 7)) + "月" + Integer
                .parseInt(sDate.substring(8, 10)) + "日";
    }

    public static String combinate(String subs, String cbs, int slen) {
        StringBuffer sbuffer = new StringBuffer();
        for (int i = 0; i < slen; i++) {
            if (i > 0) sbuffer.append(cbs);
            sbuffer.append(subs);
        }
        return sbuffer.toString();
    }

    public static String combinate(String[] subs, String cbs) {
        StringBuffer sbuffer = new StringBuffer();
        for (int i = 0; i < subs.length; i++) {
            if (i > 0) sbuffer.append(cbs);
            sbuffer.append(subs[i]);
        }
        return sbuffer.toString();
    }

    public static String extractFileName(String sFile) {
        sFile = sFile.replace('\\', '/');
        int n = sFile.lastIndexOf("/");
        if (n > -1) sFile = sFile.substring(n + 1);
        return sFile;
    }

    public static String escape(String src) {
        try {
            return URLEncoder.encode(src, "utf-8");
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0, nLen = src.length();
        char ch;
        while (lastPos < nLen) {
            pos = src.indexOf("%u", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = nLen;
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    public static String string2Unicode(String src) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < src.length(); i++) {

            // 取出每一个字符
            char c = src.charAt(i);

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

    public static String getChinaMonth(String pubyf) {
        return pubyf.substring(0, 4) + "年" + pubyf.substring(5, 7) + "月";
    }

    public static int findElementInArray(String[] Arrays, String element) {
        int result = -1;
        element = element.toUpperCase();
        for (int i = 0; i < Arrays.length; i++) {
            if (element.equals(Arrays[i])) {
                result = i;
                break;
            }
        }
        return result;
    }

    public static String formatNumber(double n, String sFormat, boolean __zeroToSpace) {

        if (__zeroToSpace && n == 0) return "";

        java.text.DecimalFormat df =
                (java.text.DecimalFormat) java.text.DecimalFormat.getInstance();
        df.applyPattern(sFormat);

        return df.format(n);
    }

    public final static String MD5(String s) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }

            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String arrayToUrlParam(String[] vStrings) {
        StringBuffer param = new StringBuffer();
        int i = 0;
        for (; i < vStrings.length; i += 2) {
            if (i == 0) {
                param.append("?");
            } else {
                param.append("&");
            }
            param.append(vStrings[i] + "=" + vStrings[i + 1]);
        }
        return param.toString();
    }

    public static String joinArray(String[] strAry, String c) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strAry.length; i++) {
            if (i == (strAry.length - 1)) {
                sb.append(strAry[i]);
            } else {
                sb.append(strAry[i]).append(c);
            }
        }

        return new String(sb);
    }

    public static boolean strIsEmpty(String str) {
        return TextUtils.isEmpty(str) || (str.trim().length() == 0);
    }

    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    /**
     * 添加space
     *
     * @author lizhgb
     * @Date 2015-10-14 上午10:38:04
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
    }

    /**
     * 判断字符串是否能转为整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否能转为浮点数
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是数字
     */
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) return false;
        }
        return true;
    }

    public static double round(double v, int ndigi) {
        BigDecimal b = new BigDecimal(v);
        return b.setScale(ndigi, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                      int dstart, int dend, int nDigi) {
        if (source.equals(".") && dest.toString().length() == 0) {
            return "0.";
        }
        if (dest.toString().contains(".")) {
            int index = dest.toString().indexOf(".");
            int mlength = dest.toString().substring(index).length();
            if (mlength == nDigi + 1) {
                return "";
            }
        }
        return null;
    }

    public static void textChange(int nDigi, CharSequence s, EditText edit) {
        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > nDigi) {
                s = s.toString().subSequence(0, s.toString().indexOf(".") + nDigi + 1);
                edit.setText(s);
                edit.setSelection(s.length());
            }
        }
        if (s.toString().trim().substring(0).equals(".")) {
            s = "0" + s;
            edit.setText(s);
            edit.setSelection(nDigi);
        }

        if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
            if (!s.toString().substring(nDigi - 1, nDigi).equals(".")) {
                edit.setText(s.subSequence(0, 1));
                edit.setSelection(1);
                return;
            }
        }
    }

    //
    public static String formatAlign(String v, int nlen, int pos) {
        String result = v;
        try {
            int n = v.getBytes("gbk").length;
            if (n < nlen) {
                switch (pos) {
                    case 0:
                        result += StringOfChar(" ", nlen - n);
                        break;
                    case 1:
                        int m = (nlen - n) / 2;
                        if (m > 0) result += StringOfChar(" ", m);
                        result = StringOfChar(" ", nlen - n - m) + result;
                        break;
                    case 2:
                        result = StringOfChar(" ", nlen - n) + result;
                        break;
                }
            }
        } catch (Exception ex) {

        }
        return result;
    }

    public static String formatAlign(double v, int nlen, int pos) {
        return formatAlign("" + v, nlen, pos);
    }

    public static String formatAlign(String v1, String v2, int nlen) {
        String result = "";
        try {
            int n = v1.getBytes("gbk").length + v2.getBytes("gbk").length;
            if (n < nlen) {
                result = v1 + StringOfChar(" ", nlen - n) + v2;
            } else {
                result = v1 + v2;
            }
        } catch (Exception ex) {

        }
        return result;
    }

    public static String formatAlign(String v1, double v2, int nlen) {
        return formatAlign(v1, v2 + "", nlen);
    }

    public static String formatHideSubStr(String v, int hlen, int pos) {
        String result = "";
        int clen = v.length();
        if (clen <= hlen) {
            result = StringOfChar("*", hlen);
        } else {
            if (pos == 0) {
                result = StringOfChar("*", hlen) + v.substring(hlen);
            } else {
                result = v.substring(0, clen - hlen) + StringOfChar("*", hlen);
            }
        }
        return result;
    }

    public static String showSubStr(String v, int slen) {
        String result = "";
        int clen = v.length();
        if (slen >= clen) {
            result = v;
        } else {
            result = v.substring(0, slen) + StringOfChar("*", clen - slen);
        }
        return result;
    }

    private static final Integer DEF_DIV_SCALE = 2;

    /**
     * 提供精确的加法运算。
     *
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static Double add(Double value1, Double value2, int scale) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return roundEx(b1.add(b2).doubleValue(), scale);
    }

    public static Double add(Double value1, Double value2) {
        return add(value1, value2, 2);
    }

    /**
     * 提供精确的减法运算。
     *
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static double sub(Double value1, Double value2, int scale) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return roundEx(b1.subtract(b2).doubleValue(), scale);
    }

    public static double sub(Double value1, Double value2) {
        return sub(value1, value2, 2);
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static Double mul(Double value1, Double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.multiply(b2).doubleValue();
    }

    public static Double mul(Double value1, Double value2, int scale) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return roundEx(b1.multiply(b2).doubleValue(), scale);
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时， 精确到小数点以后10位，以后的数字四舍五入。
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @return 两个参数的商
     */
    public static Double divide(Double dividend, Double divisor) {
        return divide(dividend, divisor, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param scale    表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static Double divide(Double dividend, Double divisor, Integer scale) {
        BigDecimal b1 = new BigDecimal(Double.toString(dividend));
        BigDecimal b2 = new BigDecimal(Double.toString(divisor));
        return b1.divide(b2, scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 提供指定数值的（精确）小数位四舍五入处理。
     *
     * @param value 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double roundEx(double value, int scale) {
        BigDecimal b = new BigDecimal(Double.toString(value));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, RoundingMode.HALF_UP).doubleValue();
    }

    public static int FieldType(String typeName) {
        int nDataType = -1;
        if (typeName.equals("class java.lang.String")) {
            nDataType = DataType.STRING;
        } else if (typeName.equals("int")) {
            nDataType = DataType.INT;
        } else if (typeName.equals("long")) {
            nDataType = DataType.LONG;
        } else if (typeName.equals("short")) {
            nDataType = DataType.SHORT;
        } else if (typeName.equals("double")) nDataType = DataType.DOUBLE;
        return nDataType;
    }

    public static int getSelectedIndex(RadioGroup group) {
        int n = 0;
        for (int i = 0; i < group.getChildCount(); i++) {
            if (((RadioButton) group.getChildAt(i)).isChecked()) {
                n = i;
                break;
            }
        }
        return n;
    }
}
