package com.miguan.expression.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    // 性能测试注释 (JDK 1.4, Jul03, scolebourne)
    // Whitespace:
    // Character.isWhitespace() 比 WHITESPACE.indexOf() 更快
    // WHITESPACE 是string中所有的字符都是whitespace
    //
    // Character access:
    // String.charAt(n) 与 toCharArray()相比, then array[n]
    // String.charAt(n) 对于10K长度的string，他的效率要低15%
    // 对于50个字符的string，这两个函数的处理速度是大概相等的
    // String.charAt(n) 对于3字节的string大约是4倍的处理速度
    // String.charAt(n) 在大多数的情况下更好些
    //
    // 附:
    // String.concat 速度大约是 StringBuffer.append 的两倍
    // (无确切测试信息)

    /**
     * 空String使用常量EMPTY来代表: <code>""</code>.
     * @since 2.0
     */
    public static final String EMPTY = "";

    /**
     * <p>可变长的string所能扩展的最大长度常量.</p>
     */
    private static final int PAD_LIMIT = 8192;

    /**
     * <p>一个用来填充的<code>String</code>数组.</p>
     *
     * <p>用于高效的空间填充. 根据需要扩展每个string的长度.</p>
     */
    private static final String[] PADDING = new String[Character.MAX_VALUE];

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    static {
        // space padding is most common, start with 64 chars
        PADDING[32] = "                                                                ";
    }

    /**
     * <p><code>StringUtils</code> 类中除了构造函数其他的都是静态方法
     *    所以在程序中不应创建这个类的实例
     *    应象这样使用:<code>StringUtils.trim(" foo ");</code>.</p>
     *
     * <p>默认的构造方法是为了 JavaBean 的操作。</p>
     */
    public StringUtils() {
        return;
    }

    // string 中为null 或者长度为'0' 或者string为空白字符串的一些检查
    //-----------------------------------------------------------------------
    /**
     * <p>检查string的长度是否为0或者string是否为null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>注意: 这个方法在 Lang version 2.0 有所改变.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str  需要检查的string, 可能为null
     * @return <code>true</code> 如果string为null或者string的长度为0
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    /**
     * <p>是否string的长度不为0或者string不为null.</p>
     *
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str  需要检查的string, 可能为null
     * @return <code>true</code> 如果string不为null或者string的长度不为0
     */
    public static boolean isNotEmpty(String str) {
        return (str != null && str.length() > 0);
    }

    /**
     * <p>检查string是否是空白或者string为null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  需要检查的string, 可能为null
     * @return <code>true</code> 如果string为null或者string中数据为空白
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>是否string不为空白，string的长度不为0，string不为null.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str  需要检查的string, 可能为null
     * @return <code>true</code> 如果string不为null并且
     *         string不为空白，string的长度不为0.
     * @since 2.0
     */
    public static boolean isNotBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    // Trim
    //-----------------------------------------------------------------------
    /**
     * <p>移去string前后两边的控制字符(char &lt;= 32)
     *    当遇到 <code>null</code> 时返回一个空string("").</p>
     *
     * <pre>
     * StringUtils.clean(null)          = ""
     * StringUtils.clean("")            = ""
     * StringUtils.clean("abc")         = "abc"
     * StringUtils.clean("    abc    ") = "abc"
     * StringUtils.clean("     ")       = ""
     * </pre>
     *
     * @see String#trim()
     * @param str  需要清空空白的string, 可能为null
     * @return     被清空空白的string,结果中不包含任何的<code>null</code>
     * @deprecated 使用这种更清晰的方法: {@link #trimToEmpty(String)}.
     *             在 Commons Lang 3.0. 此方法将会被取消
     */
    public static String clean(String str) {
        return (str == null ? EMPTY : str.trim());
    }

    /**
     * <p>移去string前后两边的控制字符 (char &lt;= 32)
     *  当遇到 <code>null</code> 时返回null.</p>
     *
     * <p>这个清除string前后空白的方法使用了 {@link String#trim()}.
     *   清除了string前后的字符(char &lt;= 32)
     *   要清除空白字符可以选用 {@link #strip(String)}.</p>
     *
     * <p>清除string的中你所制定的字符,如清除"abc"中的"a"结果为"bc", 使用
     * {@link #strip(String, String)} 方法.</p>
     *
     * <pre>
     * StringUtils.trim(null)          = null
     * StringUtils.trim("")            = ""
     * StringUtils.trim("     ")       = ""
     * StringUtils.trim("abc")         = "abc"
     * StringUtils.trim("    abc    ") = "abc"
     * </pre>
     *
     * @param str  需要进行处理的string,可能为空
     * @return     清除两端空白的string, 当输入为null时返回<code>null</code>
     */
    public static String trim(String str) {
        return (str == null ? null : str.trim());
    }

    /**
     * <p>移除string中两端的控制字符(char &lt;= 32)
     *    string为null,string中字符都为空白(&lt;= 32)或者string的长度为0时
     *    返回的结果为<code>null</code>
     *
     *
     * <p>这个清除string前后空白的方法使用了 {@link String#trim()}.
     *    清除了string前后的(char &lt;= 32)字符.
     *    要清除空白可以使用 {@link #stripToNull(String)}.</p>
     *
     * <pre>
     * StringUtils.trimToNull(null)          = null
     * StringUtils.trimToNull("")            = null
     * StringUtils.trimToNull("     ")       = null
     * StringUtils.trimToNull("abc")         = "abc"
     * StringUtils.trimToNull("    abc    ") = "abc"
     * </pre>
     *
     * @param str  需要进行处理的string,可能为空
     * @return     返回处理后的数据
     *        当输入的string中字符都为空白(&lt;= 32),string的长度为0
     *        或者string为null时返回<code>null</code>
     * @since 2.0
     */
    public static String trimToNull(String str) {
        String ts = trim(str);
        return (ts == null || ts.length() == 0 ? null : ts);
    }

    /**
     * <p>移除string前后两端的控制字符 (char &lt;= 32)
     *    当string为<code>null</code>,string的长度为0 或者string中的所有字符都为空白时.
     *    结果返回("").
     *
     * <p>这个清除string前后空白的方法使用了 {@link String#trim()}.
     *    清除了string前后的(char &lt;= 32)字符.
     *    要清除空白可以使用 {@link #stripToEmpty(String)}.</p>
     *
     * <pre>
     * StringUtils.trimToEmpty(null)          = ""
     * StringUtils.trimToEmpty("")            = ""
     * StringUtils.trimToEmpty("     ")       = ""
     * StringUtils.trimToEmpty("abc")         = "abc"
     * StringUtils.trimToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param str  需要进行处理的string,可能为空
     * @return     返回处理后的数据, 输入为<code>null</code>时返回""
     * @since 2.0
     */
    public static String trimToEmpty(String str) {
        return (str == null ? EMPTY : str.trim());
    }

    // Stripping
    //-----------------------------------------------------------------------
    /**
     * <p>清除string两端的空白的字符.</p>
     *
     * <p>这个方法和{@link #trim(String)}类似,但是它移除whitespace.
     * Whitespace 的定义在 {@link Character#isWhitespace(char)}.</p>
     *
     * <p><code>null</code> 的String输入将返回 <code>null</code>.</p>
     *
     * <pre>
     * StringUtils.strip(null)     = null
     * StringUtils.strip("")       = ""
     * StringUtils.strip("   ")    = ""
     * StringUtils.strip("abc")    = "abc"
     * StringUtils.strip("  abc")  = "abc"
     * StringUtils.strip("abc  ")  = "abc"
     * StringUtils.strip(" abc ")  = "abc"
     * StringUtils.strip(" ab c ") = "ab c"
     * </pre>
     *
     * @param str  需要移除空白的string,可能为null
     * @return     处理后的结果, 输入为null时返回<code>null</code>
     */
    public static String strip(String str) {
        return strip(str, null);
    }

    /**
     * <p>清除string两端的空白的字符
     * 返回<code>null</code> 但string为null,string长度为0或者string中字符都为空白.</p>
     *
     * <p>这个方法和{@link #trimToNull(String)}类似,但是它移除whitespace.
     * Whitespace 的定义在 {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.strip(null)     = null
     * StringUtils.strip("")       = null
     * StringUtils.strip("   ")    = null
     * StringUtils.strip("abc")    = "abc"
     * StringUtils.strip("  abc")  = "abc"
     * StringUtils.strip("abc  ")  = "abc"
     * StringUtils.strip(" abc ")  = "abc"
     * StringUtils.strip(" ab c ") = "ab c"
     * </pre>
     *
     * @param str  需要移除空白的string,可能为null
     * @return     处理后的结果,
     *    返回<code>null</code>但输入为null,string长度为0或者string中字符都为空白
     * @since 2.0
     */
    public static String stripToNull(String str) {
        if (str == null) {
            return null;
        }
        str = strip(str, null);
        return (str.length() == 0 ? null : str);
    }

    /**
     * <p>清除string两端的空白的字符
     *    如果输入为<code>null</code>返回("").</p>
     *
     * <p>这个方法和 {@link #trimToEmpty(String)}类似,但是它移除whitespace.
     * Whitespace 的定义在 {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.strip(null)     = ""
     * StringUtils.strip("")       = ""
     * StringUtils.strip("   ")    = ""
     * StringUtils.strip("abc")    = "abc"
     * StringUtils.strip("  abc")  = "abc"
     * StringUtils.strip("abc  ")  = "abc"
     * StringUtils.strip(" abc ")  = "abc"
     * StringUtils.strip(" ab c ") = "ab c"
     * </pre>
     *
     * @param str  需要移除空白的string,可能为null
     * @return     处理后的结果, 输入<code>null</code>时返回"".
     * @since 2.0
     */
    public static String stripToEmpty(String str) {
        return (str == null ? EMPTY : strip(str, null));
    }

    /**
     * <p>清除string中指定的字符.
     *    这个方法和 {@link String#trim()}类似,但你可以选择你需要移除的字符.</p>
     *
     * <p>第一个参数输入<code>null</code>时返回<code>null</code>.
     *    输入 ("")时返回("").</p>
     *
     * <p>如果第二个参数为<code>null</code>,
     *    在{@link Character#isWhitespace(char)}中定义的whitespace将被移除.
     *    和使用{@link #strip(String)}一样.</p>
     *
     * <pre>
     * StringUtils.strip(null, *)          = null
     * StringUtils.strip("", *)            = ""
     * StringUtils.strip("abc", null)      = "abc"
     * StringUtils.strip("  abc", null)    = "abc"
     * StringUtils.strip("abc  ", null)    = "abc"
     * StringUtils.strip(" abc ", null)    = "abc"
     * StringUtils.strip("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str         需要移除字符的string,可能为null
     * @param stripChars  选择要移除的字符, null被当作whitespace处理
     * @return     返回移除后的结果, 如果第一个参数为null返回<code>null</code>
     */
    public static String strip(String str, String stripChars) {
        if (str == null || str.length() == 0) {
            return str;
        }
        str = stripStart(str, stripChars);
        return stripEnd(str, stripChars);
    }

    /**
     * <p>移除string的开端中指定的字符.</p>
     *
     * <p>第一个参数为<code>null</code>时返回<code>null</code>.
     *    输入为 ("") 时返回 ("").</p>
     *
     * <p>第二个参数为 <code>null</code>,
     *    在{@link Character#isWhitespace(char)}中定义的whitespace将被移除.</p>
     *
     * <pre>
     * StringUtils.stripStart(null, *)          = null
     * StringUtils.stripStart("", *)            = ""
     * StringUtils.stripStart("abc", "")        = "abc"
     * StringUtils.stripStart("abc", null)      = "abc"
     * StringUtils.stripStart("  abc", null)    = "abc"
     * StringUtils.stripStart("abc  ", null)    = "abc  "
     * StringUtils.stripStart(" abc ", null)    = "abc "
     * StringUtils.stripStart("yxabc  ", "xyz") = "abc  "
     * </pre>
     *
     * @param str         需要移除字符的string,可能为null
     * @param stripChars  选择要移除的字符, null被当作whitespace处理
     * @return 返回移除后的结果, 如果第一个参数为null返回<code>null</code>
     */
    public static String stripStart(String str, String stripChars) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            while ((start != strLen) && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((start != strLen) && (stripChars.indexOf(str.charAt(start)) != -1)) {
                start++;
            }
        }
        return str.substring(start);
    }

    /**
     * <p>移除string的末尾中指定的字符.</p>
     *
     * <p>第一个参数为<code>null</code>时返回<code>null</code>.
     *    输入为 ("") 时返回 ("").</p>
     *
     * <p>第二个参数为 <code>null</code>,
     *    在{@link Character#isWhitespace(char)}中定义的whitespace将被移除.</p>
     *
     * <pre>
     * StringUtils.stripEnd(null, *)          = null
     * StringUtils.stripEnd("", *)            = ""
     * StringUtils.stripEnd("abc", "")        = "abc"
     * StringUtils.stripEnd("abc", null)      = "abc"
     * StringUtils.stripEnd("  abc", null)    = "  abc"
     * StringUtils.stripEnd("abc  ", null)    = "abc"
     * StringUtils.stripEnd(" abc ", null)    = " abc"
     * StringUtils.stripEnd("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str         需要移除字符的string,可能为null
     * @param stripChars  选择要移除的字符, null被当作whitespace处理
     * @return 返回移除后的结果, 如果第一个参数为null返回<code>null</code>
     */
    public static String stripEnd(String str, String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }

        if (stripChars == null) {
            while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    // StripAll
    //-----------------------------------------------------------------------
    /**
     * <p>移除string数组中每个string的两端的空白字符.
     * Whitespace 的定义在 {@link Character#isWhitespace(char)}.</p>
     *
     * <p>每次都返回一个新的数组, 除了数组的长度为0时.
     *    一个<code>null</code>数组将返回<code>null</code>.
     *    空数组将返回它本身.
     *    数组中为<code>null</code>的数组元素将被忽略.</p>
     *
     * <pre>
     * StringUtils.stripAll(null)             = null
     * StringUtils.stripAll([])               = []
     * StringUtils.stripAll(["abc", "  abc"]) = ["abc", "abc"]
     * StringUtils.stripAll(["abc  ", null])  = ["abc", null]
     * </pre>
     *
     * @param strs     需要处理的string数组,可以为null
     * @return         返回处理结果,如果输入为null将返回<code>null</code>
     */
    public static String[] stripAll(String[] strs) {
        return stripAll(strs, null);
    }

    /**
     * <p>移除string数组中每个string的两端的指定字符
     *    .</p>
     *    Whitespace 的定义在 {@link Character#isWhitespace(char)}.</p>
     *
     * <p>每次都返回一个新的数组, 除了数组的长度为0时.
     *    一个<code>null</code>数组将返回<code>null</code>.
     *    空数组将返回它本身.
     *    数组中为<code>null</code>的数组元素将被忽略.
     *    第二个参数为<code>null</code>时将移除在
     *    {@link Character#isWhitespace(char)}定义的whitespace .</p>
     *
     * <pre>
     * StringUtils.stripAll(null, *)                = null
     * StringUtils.stripAll([], *)                  = []
     * StringUtils.stripAll(["abc", "  abc"], null) = ["abc", "abc"]
     * StringUtils.stripAll(["abc  ", null], null)  = ["abc", null]
     * StringUtils.stripAll(["abc  ", null], "yz")  = ["abc  ", null]
     * StringUtils.stripAll(["yabcz", null], "yz")  = ["abc", null]
     * </pre>
     *
     * @param strs        需要处理的string数组,可以为null
     * @param stripChars  要被移除的字符, null被认为是whitespace
     * @return            处理后的结果, 输入null的数组时返回<code>null</code>
     */
    public static String[] stripAll(String[] strs, String stripChars) {
        int strsLen;
        if (strs == null || (strsLen = strs.length) == 0) {
            return strs;
        }
        String[] newArr = new String[strsLen];
        for (int i = 0; i < strsLen; i++) {
            newArr[i] = strip(strs[i], stripChars);
        }
        return newArr;
    }

    // Equals
    //-----------------------------------------------------------------------
    /**
     * <p>比较两个string, 相等时返回<code>true</code>.</p>
     *
     * <p>能处理比较中有为<code>null</code>的情况. 两个<code>null</code>
     *    比较时认为是相等的. 比较时是区分大小写的.</p>
     *
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * @see String#equals(Object)
     * @param str1  第一个string参数,可能为null
     * @param str2  第二个string参数,可能为null
     * @return <code>true</code> 当两个string相等时, 区分大小写, 或者
     *         同为<code>null</code>
     */
    public static boolean equals(String str1, String str2) {
        return (str1 == null ? str2 == null : str1.equals(str2));
    }

    /**
     * <p>比较两个string, 忽略大小写时相等返回 <code>true</code>.</p>
     *
     * <p>能处理比较中有为<code>null</code>的情况. 两个<code>null</code>
     *    比较时认为是相等的. 比较时是不区分大小写的.</p>
     *
     * <pre>
     * StringUtils.equalsIgnoreCase(null, null)   = true
     * StringUtils.equalsIgnoreCase(null, "abc")  = false
     * StringUtils.equalsIgnoreCase("abc", null)  = false
     * StringUtils.equalsIgnoreCase("abc", "abc") = true
     * StringUtils.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     *
     * @see String#equalsIgnoreCase(String)
     * @param str1  第一个string参数,可能为null
     * @param str2  第二个string参数,可能为null
     * @return <code>true</code> 两个string相等, 不区分大小写, 或者
     *         同为<code>null</code>
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return (str1 == null ? str2 == null : str1.equalsIgnoreCase(str2));
    }

    // IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>检索string中第一个指定字符的位置, 能处理<code>null</code>的情况.
     *    这个方法使用了 {@link String#indexOf(int)}.</p>
     *
     * <p>一个<code>null</code>字符串或者("")字符串将会返回<code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.indexOf(null, *)         = -1
     * StringUtils.indexOf("", *)           = -1
     * StringUtils.indexOf("aabaabaa", 'a') = 0
     * StringUtils.indexOf("aabaabaa", 'b') = 2
     * </pre>
     *
     * @param str          需要处理的string,可能为null
     * @param searchChar   要进行检索的字符
     * @return             第一个出现检索的字符的位置,
     *                     -1 string 中没有指定的字符或者输入<code>null</code>的字符串
     * @since 2.0
     */
    public static int indexOf(String str, char searchChar) {
        if (str == null || str.length() == 0) {
            return -1;
        }
        return str.indexOf(searchChar);
    }

    /**
     * <p>检索出string中从起始位开始第一个指定字符的位置,能处理<code>null</code>的情况.
     * 这个方法使用了 {@link String#indexOf(int, int)}.</p>
     *
     * <p>为<code>null</code>或者字符串为("")将返回<code>-1</code>.
     *   如果起始位为负数将当作从第一位开始检索.
     *   如果起始的位置比string的长度大将返回 <code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.indexOf(null, *, *)          = -1
     * StringUtils.indexOf("", *, *)            = -1
     * StringUtils.indexOf("aabaabaa", 'b', 0)  = 2
     * StringUtils.indexOf("aabaabaa", 'b', 3)  = 5
     * StringUtils.indexOf("aabaabaa", 'b', 9)  = -1
     * StringUtils.indexOf("aabaabaa", 'b', -1) = 2
     * </pre>
     *
     * @param str         需要处理的string,可能为null
     * @param searchChar  要进行检索的字符
     * @param startPos    起始位置，负数将当作零
     * @return            第一个出现检索的字符的位置,
     *                    -1 string 中没有指定的字符或者输入<code>null</code>的字符串
     * @since 2.0
     */
    public static int indexOf(String str, char searchChar, int startPos) {
        if (str == null || str.length() == 0) {
            return -1;
        }
        return str.indexOf(searchChar, startPos);
    }

    /**
     * <p>检索string中第一个指定字符的位置, 能处理 <code>null</code>的情况.
     * T这个方法使用了 {@link String#indexOf(String)}.</p>
     *
     * <p>为<code>null</code> 的String 返回 <code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.indexOf(null, *)          = -1
     * StringUtils.indexOf(*, null)          = -1
     * StringUtils.indexOf("", "")           = 0
     * StringUtils.indexOf("aabaabaa", "a")  = 0
     * StringUtils.indexOf("aabaabaa", "b")  = 2
     * StringUtils.indexOf("aabaabaa", "ab") = 1
     * StringUtils.indexOf("aabaabaa", "")   = 0
     * </pre>
     *
     * @param str        需要处理的string,可能为null
     * @param searchStr  要进行检索的字符,可能为null
     * @return           第一个出现检索的字符的位置,
     *                   -1 string 中没有指定的字符或者输入<code>null</code>的字符串
     * @since 2.0
     */
    public static int indexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.indexOf(searchStr);
    }

    /**
     * <p>检索出string中从指定位开始第一个指定字符的位置, 能处理 <code>null</code>的情况.
     * 这个方法使用了 {@link String#indexOf(String, int)}.</p>
     *
     * <p>为<code>null</code> 的String 返回 <code>-1</code>.
     *   负数的检索位置当作零.
     *   空的字符("") 总是满足条件.
     *   如果检索的位置大于string的长度,只有当检索的数据为("")时才能返回结果
     *   .</p>
     *
     * <pre>
     * StringUtils.indexOf(null, *, *)          = -1
     * StringUtils.indexOf(*, null, *)          = -1
     * StringUtils.indexOf("", "", 0)           = 0
     * StringUtils.indexOf("aabaabaa", "a", 0)  = 0
     * StringUtils.indexOf("aabaabaa", "b", 0)  = 2
     * StringUtils.indexOf("aabaabaa", "ab", 0) = 1
     * StringUtils.indexOf("aabaabaa", "b", 3)  = 5
     * StringUtils.indexOf("aabaabaa", "b", 9)  = -1
     * StringUtils.indexOf("aabaabaa", "b", -1) = 2
     * StringUtils.indexOf("aabaabaa", "", 2)   = 2
     * StringUtils.indexOf("abc", "", 9)        = 3
     * </pre>
     *
     * @param str        需要处理的string,可能为null
     * @param searchStr  要进行检索的字符,可能为null
     * @param startPos   起始位置，负数将当作零
     * @return           第一个出现检索的字符的位置,
     *                   -1 string 中没有指定的字符或者输入<code>null</code>的字符串
     * @since 2.0
     */
    public static int indexOf(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        // JDK1.2/JDK1.3 have a bug, when startPos > str.length for "", hence
        if (searchStr.length() == 0 && startPos >= str.length()) {
            return str.length();
        }
        return str.indexOf(searchStr, startPos);
    }

    // LastIndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>检索string中最后一个指定字符的位置, 能处理 <code>null</code>的情况.
     * 这个方法使用了 {@link String#lastIndexOf(int)}.</p>
     *
     * <p><code>null</code>的string或者("") String 返回 <code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *)         = -1
     * StringUtils.lastIndexOf("", *)           = -1
     * StringUtils.lastIndexOf("aabaabaa", 'a') = 7
     * StringUtils.lastIndexOf("aabaabaa", 'b') = 5
     * </pre>
     *
     * @param str         需要处理的string,可能为null
     * @param searchChar  要进行检索的字符
     * @return            最后出现检索的字符的位置,
     *                    -1 string 中没有指定的字符或者输入<code>null</code>的字符串
     * @since 2.0
     */
    public static int lastIndexOf(String str, char searchChar) {
        if (str == null || str.length() == 0) {
            return -1;
        }
        return str.lastIndexOf(searchChar);
    }

    /**
     * <p>检索出string中从指定位开始最后一个指定字符的位置,
     * 能处理 <code>null</code>的情况.
     * 这个方法使用了 {@link String#lastIndexOf(int, int)}.</p>
     *
     * <p><code>null</code>的string或者("") String 返回 <code>-1</code>.
     *    如果起始位置为负数将返回<code>-1</code>.
     *    起始位置大于string的长度将检索整个string.</p>
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *, *)          = -1
     * StringUtils.lastIndexOf("", *,  *)           = -1
     * StringUtils.lastIndexOf("aabaabaa", 'b', 8)  = 5
     * StringUtils.lastIndexOf("aabaabaa", 'b', 4)  = 2
     * StringUtils.lastIndexOf("aabaabaa", 'b', 0)  = -1
     * StringUtils.lastIndexOf("aabaabaa", 'b', 9)  = 5
     * StringUtils.lastIndexOf("aabaabaa", 'b', -1) = -1
     * StringUtils.lastIndexOf("aabaabaa", 'a', 0)  = 0
     * </pre>
     *
     * @param str         需要处理的string,可能为null
     * @param searchChar  需要查询的字符
     * @param startPos    查询的起始位置
     * @return            最后一个指定字符的位置,
     *                    -1 string 中没有指定的字符或者输入<code>null</code>的字符串
     * @since 2.0
     */
    public static int lastIndexOf(String str, char searchChar, int startPos) {
        if (str == null || str.length() == 0) {
            return -1;
        }
        return str.lastIndexOf(searchChar, startPos);
    }

    /**
     * <p>检索出string中最后一个指定字符的位置, 能处理 <code>null</code>的情况.
     * 这个方法使用了 {@link String#lastIndexOf(String)}.</p>
     *
     * <p><code>null</code>的String 将返回 <code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *)          = -1
     * StringUtils.lastIndexOf(*, null)          = -1
     * StringUtils.lastIndexOf("", "")           = 0
     * StringUtils.lastIndexOf("aabaabaa", "a")  = 0  ---
     * StringUtils.lastIndexOf("aabaabaa", "b")  = 2  ---
     * StringUtils.lastIndexOf("aabaabaa", "ab") = 1  ---
     * StringUtils.lastIndexOf("aabaabaa", "")   = 8
     * </pre>
     *
     * @param str        需要处理的string,可能为null
     * @param searchStr  需要查找的string, 可能为null
     * @return the last index of the search String,
     *                   -1 string 中没有指定的字符或者输入<code>null</code>的字符串
     * @since 2.0
     */
    public static int lastIndexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.lastIndexOf(searchStr);
    }

    /**
     * <p>检索出字符串str中指定位置startPos以前的字符串中
     *    特定字符searchStr最后出现的位置,
     *    能处理 <code>null</code>的情况.
     *    这个方法使用了 {@link String#lastIndexOf(String, int)}.</p>
     *
     * <p><code>null</code> 的String 将返回 <code>-1</code>.
     *    如果起始位置为负数将返回 <code>-1</code>.
     *    除非起始位置为负数,那么空的string("")将总是能匹配.
     *    如果起始位超出了string的长度将对整个string进行检索.</p>
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *, *)          = -1
     * StringUtils.lastIndexOf(*, null, *)          = -1
     * StringUtils.lastIndexOf("aabaabaa", "a", 8)  = 7
     * StringUtils.lastIndexOf("aabaabaa", "b", 8)  = 5
     * StringUtils.lastIndexOf("aabaabaa", "ab", 8) = 4
     * StringUtils.lastIndexOf("aabaabaa", "b", 9)  = 5
     * StringUtils.lastIndexOf("aabaabaa", "b", -1) = -1
     * StringUtils.lastIndexOf("aabaabaa", "a", 0)  = 0
     * StringUtils.lastIndexOf("aabaabaa", "b", 0)  = -1
     * </pre>
     *
     * @param str        需要处理的string,可能为null
     * @param searchStr  需要查询的string, 可能为null
     * @param startPos   开始查询的位置, 负数当作零
     * @return           检索出的位置,
     *                   -1 string 中没有指定的字符或者输入<code>null</code>的字符串
     * @since 2.0
     */
    public static int lastIndexOf(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.lastIndexOf(searchStr, startPos);
    }

    // Contains
    //-----------------------------------------------------------------------
    /**
     * <p>检查字符串str是否包含指定的字符searchChar, 能处理 <code>null</code>的情况.
     * 这个方法使用了 {@link String#indexOf(int)}.</p>
     *
     * <p><code>null</code>字符或者空字符串("")将返回 <code>false</code>.</p>
     *
     * <pre>
     * StringUtils.contains(null, *)    = false
     * StringUtils.contains("", *)      = false
     * StringUtils.contains("abc", 'a') = true
     * StringUtils.contains("abc", 'z') = false
     * </pre>
     *
     * @param str         需要处理的string,可能为null
     * @param searchChar  要查找的字符
     * @return            true string中包含要查找的字符,
     *                    false 没有要查找的字符或者输入位 <code>null</code>
     * @since 2.0
     */
    public static boolean contains(String str, char searchChar) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return (str.indexOf(searchChar) >= 0);
    }

    /**
     * <p>检查string是否出现指定的字符串, 能处理 <code>null</code>的情况.
     * 这个方法使用了 {@link String#indexOf(int)}.</p>
     *
     * <p><code>null</code> 的String 返回 <code>false</code>.</p>
     *
     * <pre>
     * StringUtils.contains(null, *)     = false
     * StringUtils.contains(*, null)     = false
     * StringUtils.contains("", "")      = true
     * StringUtils.contains("abc", "")   = true
     * StringUtils.contains("abc", "a")  = true
     * StringUtils.contains("abc", "z")  = false
     * </pre>
     *
     * @param str        需要处理的string,可能为null
     * @param searchStr  需要查找的字符串, 可能为null
     * @return           true string中包含要查找的字符串,
     *                   false 没有要查找的字符或者输入位 <code>null</code>
     * @since 2.0
     */
    public static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return (str.indexOf(searchStr) >= 0);
    }

    // IndexOfAny chars
    //-----------------------------------------------------------------------
    /**
     * <p>检索出字符串str中第一个包含字符数组searchChars中任意字符的位置
     *    .</p>
     *
     * <p><code>null</code> 的String 返回<code>-1</code>.
     *    <code>null</code> 或者字符数组的长度为零返回<code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.indexOfAny(null, *)                = -1
     * StringUtils.indexOfAny("", *)                  = -1
     * StringUtils.indexOfAny(*, null)                = -1
     * StringUtils.indexOfAny(*, [])                  = -1
     * StringUtils.indexOfAny("zzabyycdxx",['z','a']) = 0
     * StringUtils.indexOfAny("zzabyycdxx",['b','y']) = 3
     * StringUtils.indexOfAny("aba", ['z'])           = -1
     * </pre>
     *
     * @param str          需要处理的string,可能为null
     * @param searchChars  需要查找的字符, 可能为null
     * @return             返回第一个检索出的位置, 没有适合的或者输入null都将返回-1
     * @since 2.0
     */
    public static int indexOfAny(String str, char[] searchChars) {
        if (str == null || str.length() == 0 || searchChars == null || searchChars.length == 0) {
            return -1;
        }
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * <p>检索出字符串str中第一个出现字符串searchChars的任意字符的位置
     *    .</p>
     *
     * <p><code>null</code> 的String 返回<code>-1</code>.
     *    需要检索的string为<code>null</code> 返回 <code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.indexOfAny(null, *)            = -1
     * StringUtils.indexOfAny("", *)              = -1
     * StringUtils.indexOfAny(*, null)            = -1
     * StringUtils.indexOfAny(*, "")              = -1
     * StringUtils.indexOfAny("zzabyycdxx", "za") = 0
     * StringUtils.indexOfAny("zzabyycdxx", "by") = 3
     * StringUtils.indexOfAny("aba","z")          = -1
     * </pre>
     *
     * @param str          需要处理的string,可能为null
     * @param searchChars  需要查找的字符, 可能为null
     * @return             返回第一个检索出的位置, 没有适合的或者输入null都将返回-1
     * @since 2.0
     */
    public static int indexOfAny(String str, String searchChars) {
        if (str == null || str.length() == 0 || searchChars == null || searchChars.length() == 0) {
            return -1;
        }
        return indexOfAny(str, searchChars.toCharArray());
    }

    // IndexOfAnyBut chars
    //-----------------------------------------------------------------------
    /**
     * <p>检索出字符串str中第一个出现不是字符数组searchChars的字符的位置
     *    .</p>
     *
     * <p><code>null</code> 的String 返回 <code>-1</code>.
     *    <code>null</code> 或者字符的长度为零返回<code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.indexOfAnyBut(null, *)           = -1
     * StringUtils.indexOfAnyBut("", *)             = -1
     * StringUtils.indexOfAnyBut(*, null)           = -1
     * StringUtils.indexOfAnyBut(*, [])             = -1
     * StringUtils.indexOfAnyBut("zzabyycdxx",'za') = 3
     * StringUtils.indexOfAnyBut("zzabyycdxx", '')  = 0
     * StringUtils.indexOfAnyBut("aba", 'ab')       = -1
     * </pre>
     *
     * @param str          需要处理的string,可能为null
     * @param searchChars  要检索的字符, 可能为null
     * @return             返回第一个检索出的位置, 没有适合的或者输入null都将返回-1
     * @since 2.0
     */
    public static int indexOfAnyBut(String str, char[] searchChars) {
        if (str == null || str.length() == 0 || searchChars == null || searchChars.length == 0) {
            return -1;
        }
        outer: for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    /**
     * <p>检索出字符串str中第一个出现不是字符串searchChars的字符的位置
     *    .</p>
     *
     * <p>第一个参数为<code>null</code>将返回<code>-1</code>.
     *    第二个参数为<code>null</code>将返回<code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.indexOfAnyBut(null, *)            = -1
     * StringUtils.indexOfAnyBut("", *)              = -1
     * StringUtils.indexOfAnyBut(*, null)            = -1
     * StringUtils.indexOfAnyBut(*, "")              = -1
     * StringUtils.indexOfAnyBut("zzabyycdxx", "za") = 3
     * StringUtils.indexOfAnyBut("zzabyycdxx", "")   = 0
     * StringUtils.indexOfAnyBut("aba","ab")         = -1
     * </pre>
     *
     * @param str          需要处理的string,可能为null
     * @param searchChars  字符串A中可能包含的字符串B, 可能为null
     * @return             返回第一个检索出的位置, 没有适合的或者输入null都将返回-1
     * @since 2.0
     */
    public static int indexOfAnyBut(String str, String searchChars) {
        if (str == null || str.length() == 0 || searchChars == null || searchChars.length() == 0) {
            return -1;
        }
        for (int i = 0; i < str.length(); i++) {
            if (searchChars.indexOf(str.charAt(i)) < 0) {
                return i;
            }
        }
        return -1;
    }

    // ContainsOnly
    //-----------------------------------------------------------------------
    /**
     * <p>字符串str中是否只有字符数组valid中的字符.</p>
     *
     * <p>第一个参数为<code>null</code> 返回 <code>false</code>.
     *    第二个参数为<code>null</code> 返回 <code>false</code>.
     *    第一个参数为 ("") 返回 <code>true</code>.</p>
     *
     * <pre>
     * StringUtils.containsOnly(null, *)       = false
     * StringUtils.containsOnly(*, null)       = false
     * StringUtils.containsOnly("", *)         = true
     * StringUtils.containsOnly("ab", '')      = false
     * StringUtils.containsOnly("abab", 'abc') = true
     * StringUtils.containsOnly("ab1", 'abc')  = false
     * StringUtils.containsOnly("abz", 'abc')  = false
     * </pre>
     *
     * @param str      需要处理的string,可能为null
     * @param valid    字符数组参数, 可能为null
     * @return         true 字符串不为null,并且字符串中的字符都是字符数组中的字符
     */
    public static boolean containsOnly(String str, char[] valid) {
        // All these pre-checks are to maintain API with an older version
        if ((valid == null) || (str == null)) {
            return false;
        }
        if (str.length() == 0) {
            return true;
        }
        if (valid.length == 0) {
            return false;
        }
        return indexOfAnyBut(str, valid) == -1;
    }

    /**
     * <p>字符串str中是否只有字符串validChars中的字符.</p>
     *
     * <p>第一个参数为<code>null</code> 返回 <code>false</code>.
     *    第二个参数为<code>null</code> 返回 <code>false</code>.
     *    第一个参数为 ("") 返回 <code>true</code>.</p>
     *
     * <pre>
     * StringUtils.containsOnly(null, *)       = false
     * StringUtils.containsOnly(*, null)       = false
     * StringUtils.containsOnly("", *)         = true
     * StringUtils.containsOnly("ab", "")      = false
     * StringUtils.containsOnly("abab", "abc") = true
     * StringUtils.containsOnly("ab1", "abc")  = false
     * StringUtils.containsOnly("abz", "abc")  = false
     * </pre>
     *
     * @param str         需要处理的string,可能为null
     * @param validChars  字符串参数, 可能为null
     * @return            true 字符串不为null,并且字符串str中的字符都是字符串validChars中的字符
     * @since 2.0
     */
    public static boolean containsOnly(String str, String validChars) {
        if (str == null || validChars == null) {
            return false;
        }
        return containsOnly(str, validChars.toCharArray());
    }

    // ContainsNone
    //-----------------------------------------------------------------------
    /**
     * <p>检查字符串str中是否不含有字符数组invalidChar中的字符.</p>
     *
     * <p>第一个参数为<code>null</code> 返回 <code>true</code>.
     *    第二个参数为<code>null</code> 返回 <code>true</code>.
     *    参数中有为空("") 时返回true.</p>
     *
     * <pre>
     * StringUtils.containsNone(null, *)       = true
     * StringUtils.containsNone(*, null)       = true
     * StringUtils.containsNone("", *)         = true
     * StringUtils.containsNone("ab", '')      = true
     * StringUtils.containsNone("abab", 'xyz') = true
     * StringUtils.containsNone("ab1", 'xyz')  = true
     * StringUtils.containsNone("abz", 'xyz')  = false
     * </pre>
     *
     * @param str           需要处理的string,可能为nulll
     * @param invalidChars  字符数组参数, 可能为null
     * @return              true 字符串str中不包含字符数组invalidChar中的字符, 或者参数为null
     * @since 2.0
     */
    public static boolean containsNone(String str, char[] invalidChars) {
        if (str == null || invalidChars == null) {
            return true;
        }
        int strSize = str.length();
        int validSize = invalidChars.length;
        for (int i = 0; i < strSize; i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < validSize; j++) {
                if (invalidChars[j] == ch) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * <p>检查字符串str中是否不含有字符串invalidChar中的字符.</p>
     *
     * <p>第一个参数str为<code>null</code> 返回 <code>true</code>.
     *    第二个参数invalidChars为<code>null</code> 返回 <code>true</code>.
     *    参数中有为空("") 时返回true.</p>
     *
     * <pre>
     * StringUtils.containsNone(null, *)       = true
     * StringUtils.containsNone(*, null)       = true
     * StringUtils.containsNone("", *)         = true
     * StringUtils.containsNone("ab", "")      = true
     * StringUtils.containsNone("abab", "xyz") = true
     * StringUtils.containsNone("ab1", "xyz")  = true
     * StringUtils.containsNone("abz", "xyz")  = false
     * </pre>
     *
     * @param str           需要处理的string,可能为null
     * @param invalidChars  字符串参数, 可能为null
     * @return              true 字符串str中不包含字符串invalidChar中的字符, 或者参数为null
     * @since 2.0
     */
    public static boolean containsNone(String str, String invalidChars) {
        if (str == null || invalidChars == null) {
            return true;
        }
        return containsNone(str, invalidChars.toCharArray());
    }

    // IndexOfAny strings
    //-----------------------------------------------------------------------
    /**
     * <p>找出字符串str中第一次出现字符串数组searchStrs中元素的位置.</p>
     *
     * <p>第一个参数str为<code>null</code>返回<code>-1</code>.
     *    第二个参数searchStrs为<code>null</code> 或者长度为0返回<code>-1</code>.
     *    第二个参数searchStrs为<code>null</code> 的情况将被忽略, 但如果为[""]
     *        返回 <code>0</code> 当 <code>第一个参数</code>不为null时.
     *    这个方法使用了 {@link String#indexOf(String)}.</p>
     *
     * <pre>
     * StringUtils.indexOfAny(null, *)                     = -1
     * StringUtils.indexOfAny(*, null)                     = -1
     * StringUtils.indexOfAny(*, [])                       = -1
     * StringUtils.indexOfAny("zzabyycdxx", ["ab","cd"])   = 2
     * StringUtils.indexOfAny("zzabyycdxx", ["cd","ab"])   = 2
     * StringUtils.indexOfAny("zzabyycdxx", ["mn","op"])   = -1
     * StringUtils.indexOfAny("zzabyycdxx", ["zab","aby"]) = 1
     * StringUtils.indexOfAny("zzabyycdxx", [""])          = 0
     * StringUtils.indexOfAny("", [""])                    = 0
     * StringUtils.indexOfAny("", ["a"])                   = -1
     * </pre>
     *
     * @param str         需要处理的string,可能为null
     * @param searchStrs  字符串数组参数, 可能为null
     * @return            字符串str中第一次出现字符串数组searchStrs中元素的位置,
     *                    -1 没有适合的情况
     */
    public static int indexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }
        int sz = searchStrs.length;

        // String's can't have a MAX_VALUEth index.
        int ret = Integer.MAX_VALUE;

        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            String search = searchStrs[i];
            if (search == null) {
                continue;
            }
            tmp = str.indexOf(search);
            if (tmp == -1) {
                continue;
            }

            if (tmp < ret) {
                ret = tmp;
            }
        }

        return (ret == Integer.MAX_VALUE) ? -1 : ret;
    }

    /**
     * <p>找出字符串str中最后出现字符串数组searchStrs中元素的位置.</p>
     *
     * <p>第一个参数str为<code>null</code>返回<code>-1</code>.
     * A <code>null</code> search array will return <code>-1</code>.
     * A <code>null</code> or zero length search array entry will be ignored,
     *    当第一个参数str不为null时,第二个参数searchStrs中有"" 的情况
     *        将返回<code>str</code>的长度.
     *    这个方法使用了 {@link String#indexOf(String)}</p>
     *
     * <pre>
     * StringUtils.lastIndexOfAny(null, *)                   = -1
     * StringUtils.lastIndexOfAny(*, null)                   = -1
     * StringUtils.lastIndexOfAny(*, [])                     = -1
     * StringUtils.lastIndexOfAny(*, [null])                 = -1
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["ab","cd"]) = 6
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["cd","ab"]) = 6
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn",""])   = 10
     * </pre>
     *
     * @param str         需要处理的string,可能为null
     * @param searchStrs  字符串数组参数, 可能为null
     * @return            字符串str中最后出现字符串数组searchStrs中元素的位置,
     *                    -1 没有适合的情况
     */
    public static int lastIndexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }
        int sz = searchStrs.length;
        int ret = -1;
        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            String search = searchStrs[i];
            if (search == null) {
                continue;
            }
            tmp = str.lastIndexOf(search);
            if (tmp > ret) {
                ret = tmp;
            }
        }
        return ret;
    }

    // Substring
    //-----------------------------------------------------------------------
    /**
     * <p>从字符串str中指定位置start后面的字符串.</p>
     *
     * <p>第二个参数start为负数时，取start的绝对值并从str的末端开倒数<code>n</code>
     *    个字符.</p>
     *
     * <p>A <code>null</code> String will return <code>null</code>.
     * An empty ("") String will return "".</p>
     *
     * <pre>
     * StringUtils.substring(null, *)   = null
     * StringUtils.substring("", *)     = ""
     * StringUtils.substring("abc", 0)  = "abc"
     * StringUtils.substring("abc", 2)  = "c"
     * StringUtils.substring("abc", 4)  = ""
     * StringUtils.substring("abc", -2) = "bc"
     * StringUtils.substring("abc", -4) = "abc"
     * </pre>
     *
     * @param str     要从str中获得子字符串,可能为null
     * @param start   位置的开始位置, 负数时取start的绝对值并从str的末端开倒数字符
     * @return        从起始位置start开始的子串,
     *                输入的第一个字符串为null时返回<code>null</code>
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }

        // handle negatives, which means last n characters
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return EMPTY;
        }

        return str.substring(start);
    }

    /**
     * <p>从字符串str中获得从开始位置start到结束位end的子字符串，并且避免了异常.</p>
     *
     * <p>start/end 能使用负数来表示,将会从字符串str的末端开始计数
     *    .</p>
     *
     * <p>返回的字符串从<code>start</code>位置开始
     *    到 <code>end</code> 位置结束. 字符串的开始位是以0开始的
     *    -- i.e.,要以字符串的第一位开始<code>start = 0</code>.
     *    负数的开始位置偏移量是相对于str的末端开始的
     *    .</p>
     *
     * <p>如果开始位置<code>start</code>不是在<code>end</code>位置的左边,将返回 ""
     *    .</p>
     *
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", * ,  *)    = "";
     * StringUtils.substring("abc", 0, 2)   = "ab"
     * StringUtils.substring("abc", 2, 0)   = ""
     * StringUtils.substring("abc", 2, 4)   = "c"
     * StringUtils.substring("abc", 4, 6)   = ""
     * StringUtils.substring("abc", 2, 2)   = ""
     * StringUtils.substring("abc", -2, -1) = "b"
     * StringUtils.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str    要从中获得子字符串的string,可能为null
     * @param start  子字符串的开始位置, 负数代表从字符串的末端开始计数
     * @param end    子字符串的结束位置, 负数代表从字符串的末端开始计数
     * @return       从开始start到结束end的子字符串,如果输入的str为null返回<code>null</code>
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) {
            return EMPTY;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    // Left/Right/Mid
    //-----------------------------------------------------------------------
    /**
     * <p>从字符串的最左边获得指定长度<code>len</code> 的字符串.</p>
     *
     * <p>如果指定长度<code>len</code> 不是有效的,
     *    或者str为<code>null</code>, str将作为返回值并不抛出异常.
     *    如果指定的长度为负数将抛出异常.</p>
     *
     * <pre>
     * StringUtils.left(null, *)    = null
     * StringUtils.left(*, -ve)     = ""
     * StringUtils.left("", *)      = ""
     * StringUtils.left("abc", 0)   = ""
     * StringUtils.left("abc", 2)   = "ab"
     * StringUtils.left("abc", 4)   = "abc"
     * </pre>
     *
     * @param str  需要转换的字符串str, 可能为null
     * @param len  要请求的字符串的长度, 必须为0或者正数
     * @return     指定长度的字符串最左边的字符串, 如果输入的字符串str为null返回<code>null</code>
     */
    public static String left(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        } else {
            return str.substring(0, len);
        }
    }

    /**
     * <p>从字符串的最右边获得指定长度<code>len</code> 的字符串.</p>
     *
     * <p>如果指定长度<code>len</code> 不是有效的,
     *    或者str为 <code>null</code>, str将作为返回值并不抛出异常
     *    .如果指定的长度len为负数将抛出异常.</p>
     *
     * <pre>
     * StringUtils.right(null, *)    = null
     * StringUtils.right(*, -ve)     = ""
     * StringUtils.right("", *)      = ""
     * StringUtils.right("abc", 0)   = ""
     * StringUtils.right("abc", 2)   = "bc"
     * StringUtils.right("abc", 4)   = "abc"
     * </pre>
     *
     * @param str  需要转换的字符串str, 可能为null
     * @param len  要请求的字符串的长度, 必须为0或者正数
     * @return     定长度的字符串最右边的字符串, 如果输入的字符串str为null返回</code>
     */
    public static String right(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        } else {
            return str.substring(str.length() - len);
        }
    }

    /**
     * <p>从字符串的中间获得指定长度<code>len</code> 的字符串.</p>
     *
     * <p>如果<code>len</code>参数为无效的,从pos开始的剩余的字符串将返回
     *    而不抛出异常. 如果字符串str为<code>null</code>, 将返回<code>null</code> .
     *    如果指定的长度len为负数将抛出异常.</p>
     *
     * <pre>
     * StringUtils.mid(null, *, *)    = null
     * StringUtils.mid(*, *, -ve)     = ""
     * StringUtils.mid("", 0, *)      = ""
     * StringUtils.mid("abc", 0, 2)   = "ab"
     * StringUtils.mid("abc", 0, 4)   = "abc"
     * StringUtils.mid("abc", 2, 4)   = "c"
     * StringUtils.mid("abc", 4, 2)   = ""
     * StringUtils.mid("abc", -2, 2)  = "ab"
     * </pre>
     *
     * @param str  需要转换的字符串str, 可能为null
     * @param pos  返回的子串的开始位置, 如为负数当成0
     * @param len  要返回的子串的长度, 必须为0或者为正数
     * @return     中间的子字符串,如果输入的字符串str为null返回<code>null</code>
     */
    public static String mid(String str, int pos, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0 || pos > str.length()) {
            return EMPTY;
        }
        if (pos < 0) {
            pos = 0;
        }
        if (str.length() <= (pos + len)) {
            return str.substring(pos);
        } else {
            return str.substring(pos, pos + len);
        }
    }

    // SubStringAfter/SubStringBefore
    //-----------------------------------------------------------------------
    /**
     * <p> 从字符串str中获得第一个出现隔离字符串separator之前的子字符串.
     *     隔离字符串separator不作为结果返回.</p>
     *
     * <p>输入字符串str为<code>null</code>返回<code>null</code>.
     *    空的字符串("")将返回空字符串("").
     *    隔离字符串separator为<code>null</code> 将返回字符串str本身.</p>
     *
     * <pre>
     * StringUtils.substringBefore(null, *)      = null
     * StringUtils.substringBefore("", *)        = ""
     * StringUtils.substringBefore("abc", "a")   = ""
     * StringUtils.substringBefore("abcba", "b") = "a"
     * StringUtils.substringBefore("abc", "c")   = "ab"
     * StringUtils.substringBefore("abc", "d")   = "abc"
     * StringUtils.substringBefore("abc", "")    = ""
     * StringUtils.substringBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param str          要处理的字符串, 可能为null
     * @param separator    隔离字符串, 可能为null
     * @return             字符串str中获得第一个出现隔离字符串separator之前的子字符串,
     *                     输入的字符串str为null时返回<code>null</code>
     * @since 2.0
     */
    public static String substringBefore(String str, String separator) {
        if (str == null || separator == null || str.length() == 0) {
            return str;
        }
        if (separator.length() == 0) {
            return EMPTY;
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * <p>从字符串str中获得第一个出现隔离字符串separator之后的子字符串.
     *    隔离字符串separator不作为结果返回.</p>
     *
     * <p>输入字符串str为<code>null</code>返回<code>null</code>.
     *    空的字符串("")将返回空字符串("").
     *    如果第一个参数字符串不为<code>null</code>但隔离字符串separator为<code>null</code>
     *    将返回空字符串("").
     *    .</p>
     *
     * <pre>
     * StringUtils.substringAfter(null, *)      = null
     * StringUtils.substringAfter("", *)        = ""
     * StringUtils.substringAfter(*, null)      = ""
     * StringUtils.substringAfter("abc", "a")   = "bc"
     * StringUtils.substringAfter("abcba", "b") = "cba"
     * StringUtils.substringAfter("abc", "c")   = ""
     * StringUtils.substringAfter("abc", "d")   = ""
     * StringUtils.substringAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param str        要处理的字符串, 可能为null
     * @param separator  隔离字符串, 可能为null
     * @return           字符串str中获得第一个出现隔离字符串separator之后的子字符串,
     *                   输入的字符串str为null时返回<code>null</code>
     * @since 2.0
     */
    public static String substringAfter(String str, String separator) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (separator == null) {
            return EMPTY;
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    /**
     * <p>从字符串str中获得最后出现分割符separator之前的子字符串.
     *    分割符separator不作为结果返回.</p>
     *
     * <p>输入字符串str为<code>null</code>返回<code>null</code>.
     *    空的字符串("")将返回空字符串("").
     *    分割符separator为 <code>null</code> 将返回输入字符串str本身.</p>
     *
     * <pre>
     * StringUtils.substringBeforeLast(null, *)      = null
     * StringUtils.substringBeforeLast("", *)        = ""
     * StringUtils.substringBeforeLast("abcba", "b") = "abc"
     * StringUtils.substringBeforeLast("abc", "c")   = "ab"
     * StringUtils.substringBeforeLast("a", "a")     = ""
     * StringUtils.substringBeforeLast("a", "z")     = "a"
     * StringUtils.substringBeforeLast("a", null)    = "a"
     * StringUtils.substringBeforeLast("a", "")      = "a"
     * </pre>
     *
     * @param str        要处理的字符串, 可能为null
     * @param separator  分割符, 可能为null
     * @return           字符串str中获得最后出现分割符separator之前的子字符串,
     *                   <code>null</code> 如果输入的字符串str为null
     * @since 2.0
     */
    public static String substringBeforeLast(String str, String separator) {
        if (str == null || separator == null || str.length() == 0 || separator.length() == 0) {
            return str;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * <p>从字符串str中获得最后出现隔离字符串separator之后的子字符串.
     *    隔离字符串separator不作为结果返回.</p>
     *
     * <p>输入字符串str为<code>null</code>返回<code>null</code>.
     *    空的字符串("")将返回空字符串("").
     *    隔离字符串separator为 <code>null</code> 将返回空的字符串("").</p>
     *
     * <pre>
     * StringUtils.substringAfterLast(null, *)      = null
     * StringUtils.substringAfterLast("", *)        = ""
     * StringUtils.substringAfterLast(*, "")        = ""
     * StringUtils.substringAfterLast(*, null)      = ""
     * StringUtils.substringAfterLast("abc", "a")   = "bc"
     * StringUtils.substringAfterLast("abcba", "b") = "a"
     * StringUtils.substringAfterLast("abc", "c")   = ""
     * StringUtils.substringAfterLast("a", "a")     = ""
     * StringUtils.substringAfterLast("a", "z")     = ""
     * </pre>
     *
     * @param str        要处理的字符串, 可能为null
     * @param separator  隔离字符串, 可能为null
     * @return           字符串str中获得最后出现隔离字符串separator之后的子字符串,
     *                   <code>null</code> 如果输入的字符串str为null
     * @since 2.0
     */
    public static String substringAfterLast(String str, String separator) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (separator == null || separator.length() == 0) {
            return EMPTY;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1 || pos == (str.length() - separator.length())) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    // Substring between
    //-----------------------------------------------------------------------
    /**
     * <p>从字符串str中分离出嵌套在两个同样字符串tag中间的子字符串
     *    .</p>
     *
     * <p>输入字符串str为<code>null</code>返回<code>null</code>.
     *    第二个参数tag为<code>null</code>返回<code>null</code>.</p>
     *
     * <pre>
     * StringUtils.substringBetween(null, *)            = null
     * StringUtils.substringBetween("", "")             = ""
     * StringUtils.substringBetween("", "tag")          = null
     * StringUtils.substringBetween("tagabctag", null)  = null
     * StringUtils.substringBetween("tagabctag", "")    = ""
     * StringUtils.substringBetween("tagabctag", "tag") = "abc"
     * </pre>
     *
     * @param str  包含子字符串的字符串, 可能为null
     * @param tag  子字符串之前和之后的字符串, 可能为null
     * @return     子字符串, <code>null</code> 如果没有匹配的情况
     * @since 2.0
     */
    public static String substringBetween(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    /**
     * <p>从字符串str中分离出嵌套在两个字符串open,close中间的子字符串.
     *    只返回第一个匹配的子串.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> open/close returns <code>null</code> (no match).
     * An empty ("") open/close returns an empty string.</p>
     *
     * <pre>
     * StringUtils.substringBetween(null, *, *)          = null
     * StringUtils.substringBetween("", "", "")          = ""
     * StringUtils.substringBetween("", "", "tag")       = null
     * StringUtils.substringBetween("", "tag", "tag")    = null
     * StringUtils.substringBetween("yabcz", null, null) = null
     * StringUtils.substringBetween("yabcz", "", "")     = ""
     * StringUtils.substringBetween("yabcz", "y", "z")   = "abc"
     * StringUtils.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str     包含子串的字符串, 可能为null
     * @param open    子串前面的字符串, 可能为null
     * @param close   子串后面的字符串, 可能为null
     * @return        子串, <code>null</code> 如果没有匹配的情况
     * @since 2.0
     */
    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    // Nested extraction
    //-----------------------------------------------------------------------
    /**
     * <p>从字符串str中分离出嵌套在两个同样字符串tag中间的子字符串
     *    .</p>
     *
     * <p>输入字符串str为<code>null</code>返回<code>null</code>.
     *    第二个参数tag为<code>null</code>返回<code>null</code>.</p>
     *
     * <pre>
     * StringUtils.getNestedString(null, *)            = null
     * StringUtils.getNestedString("", "")             = ""
     * StringUtils.getNestedString("", "tag")          = null
     * StringUtils.getNestedString("tagabctag", null)  = null
     * StringUtils.getNestedString("tagabctag", "")    = ""
     * StringUtils.getNestedString("tagabctag", "tag") = "abc"
     * </pre>
     *
     * @param str     包含嵌套字符串的字符串, 可能为null
     * @param tag     在嵌套字符串前后的字符串, 可能为null
     * @return        嵌套字符串, <code>null</code> 如果没有匹配的情况
     * @deprecated    应使用更好的方法 {@link #substringBetween(String, String)}.
     *                方法将在 Commons Lang 3.0.被取消
     */
    public static String getNestedString(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    /**
     * <p>从字符串str中分离出嵌套在两个字符串open，close中间的子字符串.
     *    只返回第一个匹配的子串.</p>
     *
     * <p>输入字符串str为<code>null</code>返回<code>null</code>.
     *    <code>null</code>的 open/close 参数返回 <code>null</code> (没有匹配的).
     *    空白字符的("") open/close 返回一个空字符.</p>
     *
     * <pre>
     * StringUtils.getNestedString(null, *, *)          = null
     * StringUtils.getNestedString("", "", "")          = ""
     * StringUtils.getNestedString("", "", "tag")       = null
     * StringUtils.getNestedString("", "tag", "tag")    = null
     * StringUtils.getNestedString("yabcz", null, null) = null
     * StringUtils.getNestedString("yabcz", "", "")     = ""
     * StringUtils.getNestedString("yabcz", "y", "z")   = "abc"
     * StringUtils.getNestedString("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str     包含嵌套字符串的字符串, 可能为null
     * @param open    嵌套字符串之前的字符串, 可能为null
     * @param close   嵌套字符串之后的字符串, 可能为null
     * @return        嵌套字符串, <code>null</code> 如果没有匹配的情况
     * @deprecated    应使用更好的方法 {@link #substringBetween(String, String, String)}.
     *                方法将在 Commons Lang 3.0.被取消
     */
    public static String getNestedString(String str, String open, String close) {
        return substringBetween(str, open, close);
    }

    // Splitting
    //-----------------------------------------------------------------------
    /**
     * <p>Splits the provided text into an array, using whitespace as the
     * separator.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * StringUtils.split(null)       = null
     * StringUtils.split("")         = []
     * StringUtils.split("abc def")  = ["abc", "def"]
     * StringUtils.split("abc  def") = ["abc", "def"]
     * StringUtils.split(" abc ")    = ["abc"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] split(String str) {
        return split(str, null, -1);
    }

    /**
     * <p>根据指定的分割符separator把字符串分割为字符串数组.
     *    也可以选择使用 StringTokenizer.</p>
     *
     * <p>分割符separator不作为数组元素中的数据返回.
     *    邻近的多个分割符separators 被认为是一个分割符separator.</p>
     *
     * <p>输入的字符串为<code>null</code>返回<code>null</code>.</p>
     *
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("a.b.c", '.')    = ["a", "b", "c"]
     * StringUtils.split("a..b.c", '.')   = ["a", "b", "c"]
     * StringUtils.split("a:b:c", '.')    = ["a:b:c"]
     * StringUtils.split("a\tb\nc", null) = ["a", "b", "c"]
     * StringUtils.split("a b c", ' ')    = ["a", "b", "c"]
     * </pre>
     *
     * @param str            需要处理的字符串, 可能为null
     * @param separatorChar  作为分隔符的字符,
     *                       如果为<code>null</code>就以whitespace作为分隔符
     * @return               处理后的数组, <code>null</code>如果输入的字符串为null
     * @since 2.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList split(String str, char separatorChar) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return new ArrayList();
        }
        ArrayList list = new ArrayList();
        int i = 0;
        int start = 0;
        boolean match = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                }
                start = ++i;
                continue;
            }
            match = true;
            i++;
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return list;
    }

    /**
     * <p>Splits the provided text into an array, separator specified.
     * This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("a.b.c", '.')    = ["a", "b", "c"]
     * StringUtils.split("a..b.c", '.')   = ["a", "b", "c"]
     * StringUtils.split("a:b:c", '.')    = ["a:b:c"]
     * StringUtils.split("a\tb\nc", null) = ["a", "b", "c"]
     * StringUtils.split("a b c", ' ')    = ["a", "b", "c"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChar  the character used as the delimiter,
     *  <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.0
     */
    public static String[] splitByChar(String str, char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    /**
     * <p>Splits the provided text into an array, separators specified.
     * This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separatorChars splits on whitespace.</p>
     *
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("abc def", null) = ["abc", "def"]
     * StringUtils.split("abc def", " ")  = ["abc", "def"]
     * StringUtils.split("abc  def", " ") = ["abc", "def"]
     * StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChars  the characters used as the delimiters,
     *  <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    /**
     * <p>Splits the provided text into an array with a maximum length,
     * separators specified.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separatorChars splits on whitespace.</p>
     *
     * <p>If more than <code>max</code> delimited substrings are found, the last
     * returned string includes all characters after the first <code>max - 1</code>
     * returned strings (including separator characters).</p>
     *
     * <pre>
     * StringUtils.split(null, *, *)            = null
     * StringUtils.split("", *, *)              = []
     * StringUtils.split("ab de fg", null, 0)   = ["ab", "cd", "ef"]
     * StringUtils.split("ab   de fg", null, 0) = ["ab", "cd", "ef"]
     * StringUtils.split("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
     * StringUtils.split("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChars  the characters used as the delimiters,
     *  <code>null</code> splits on whitespace
     * @param max  the maximum number of elements to include in the
     *  array. A zero or negative value implies no limit
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] split(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    /**
     * <p>Splits the provided text into an array, separator string specified.</p>
     *
     * <p>The separator(s) will not be included in the returned String array.
     * Adjacent separators are treated as one separator.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separator splits on whitespace.</p>
     *
     * <pre>
     * StringUtils.splitByWholeSeparator(null, *)               = null
     * StringUtils.splitByWholeSeparator("", *)                 = []
     * StringUtils.splitByWholeSeparator("ab de fg", null)      = ["ab", "de", "fg"]
     * StringUtils.splitByWholeSeparator("ab   de fg", null)    = ["ab", "de", "fg"]
     * StringUtils.splitByWholeSeparator("ab:cd:ef", ":")       = ["ab", "cd", "ef"]
     * StringUtils.splitByWholeSeparator("ab-!-cd-!-ef", "-!-") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separator  String containing the String to be used as a delimiter,
     *  <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String was input
     */
    public static String[] splitByWholeSeparator(String str, String separator) {
        return splitByWholeSeparator(str, separator, -1);
    }

    /**
     * <p>Splits the provided text into an array, separator string specified.
     * Returns a maximum of <code>max</code> substrings.</p>
     *
     * <p>The separator(s) will not be included in the returned String array.
     * Adjacent separators are treated as one separator.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separator splits on whitespace.</p>
     *
     * <pre>
     * StringUtils.splitByWholeSeparator(null, *, *)               = null
     * StringUtils.splitByWholeSeparator("", *, *)                 = []
     * StringUtils.splitByWholeSeparator("ab de fg", null, 0)      = ["ab", "de", "fg"]
     * StringUtils.splitByWholeSeparator("ab   de fg", null, 0)    = ["ab", "de", "fg"]
     * StringUtils.splitByWholeSeparator("ab:cd:ef", ":", 2)       = ["ab", "cd:ef"]
     * StringUtils.splitByWholeSeparator("ab-!-cd-!-ef", "-!-", 5) = ["ab", "cd", "ef"]
     * StringUtils.splitByWholeSeparator("ab-!-cd-!-ef", "-!-", 2) = ["ab", "cd-!-ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separator  String containing the String to be used as a delimiter,
     *  <code>null</code> splits on whitespace
     * @param max  the maximum number of elements to include in the returned
     *  array. A zero or negative value implies no limit.
     * @return an array of parsed Strings, <code>null</code> if null String was input
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static String[] splitByWholeSeparator(String str, String separator, int max) {
        if (str == null) {
            return null;
        }

        int len = str.length();

        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }

        if ((separator == null) || ("".equals(separator))) {
            // Split on whitespace.
            return split(str, null, max);
        }

        int separatorLength = separator.length();

        ArrayList substrings = new ArrayList();
        int numberOfSubstrings = 0;
        int beg = 0;
        int end = 0;
        while (end < len) {
            end = str.indexOf(separator, beg);

            if (end > -1) {
                if (end > beg) {
                    numberOfSubstrings += 1;

                    if (numberOfSubstrings == max) {
                        end = len;
                        substrings.add(str.substring(beg));
                    } else {
                        // The following is OK, because String.substring( beg, end ) excludes
                        // the character at the position 'end'.
                        substrings.add(str.substring(beg, end));

                        // Set the starting point for the next search.
                        // The following is equivalent to beg = end + (separatorLength - 1) + 1,
                        // which is the right calculation:
                        beg = end + separatorLength;
                    }
                } else {
                    // We found a consecutive occurrence of the separator, so skip it.
                    beg = end + separatorLength;
                }
            } else {
                // String.substring( beg ) goes from 'beg' to the end of the String.
                substrings.add(str.substring(beg));
                end = len;
            }
        }

        return (String[]) substrings.toArray(new String[substrings.size()]);
    }

    //-----------------------------------------------------------------------
    /**
     * <p>Splits the provided text into an array, using whitespace as the
     * separator, preserving all tokens, including empty tokens created by
     * adjacent separators. This is an alternative to using StringTokenizer.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * StringUtils.splitPreserveAllTokens(null)       = null
     * StringUtils.splitPreserveAllTokens("")         = []
     * StringUtils.splitPreserveAllTokens("abc def")  = ["abc", "def"]
     * StringUtils.splitPreserveAllTokens("abc  def") = ["abc", "", "def"]
     * StringUtils.splitPreserveAllTokens(" abc ")    = ["", "abc", ""]
     * </pre>
     *
     * @param str  the String to parse, may be <code>null</code>
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(String str) {
        return splitWorker(str, null, -1, true);
    }

    /**
     * <p>Splits the provided text into an array, separator specified,
     * preserving all tokens, including empty tokens created by adjacent
     * separators. This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * StringUtils.splitPreserveAllTokens(null, *)         = null
     * StringUtils.splitPreserveAllTokens("", *)           = []
     * StringUtils.splitPreserveAllTokens("a.b.c", '.')    = ["a", "b", "c"]
     * StringUtils.splitPreserveAllTokens("a..b.c", '.')   = ["a", "", "b", "c"]
     * StringUtils.splitPreserveAllTokens("a:b:c", '.')    = ["a:b:c"]
     * StringUtils.splitPreserveAllTokens("a\tb\nc", null) = ["a", "b", "c"]
     * StringUtils.splitPreserveAllTokens("a b c", ' ')    = ["a", "b", "c"]
     * StringUtils.splitPreserveAllTokens("a b c ", ' ')   = ["a", "b", "c", ""]
     * StringUtils.splitPreserveAllTokens("a b c  ", ' ')   = ["a", "b", "c", "", ""]
     * StringUtils.splitPreserveAllTokens(" a b c", ' ')   = ["", a", "b", "c"]
     * StringUtils.splitPreserveAllTokens("  a b c", ' ')  = ["", "", a", "b", "c"]
     * StringUtils.splitPreserveAllTokens(" a b c ", ' ')  = ["", a", "b", "c", ""]
     * </pre>
     *
     * @param str  the String to parse, may be <code>null</code>
     * @param separatorChar  the character used as the delimiter,
     *  <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(String str, char separatorChar) {
        return splitWorker(str, separatorChar, true);
    }

    /**
     * Performs the logic for the <code>split</code> and
     * <code>splitPreserveAllTokens</code> methods that do not return a
     * maximum array length.
     *
     * @param str  the String to parse, may be <code>null</code>
     * @param separatorChar the separate character
     * @param preserveAllTokens if <code>true</code>, adjacent separators are
     * treated as empty token separators; if <code>false</code>, adjacent
     * separators are treated as one separator.
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        List list = new ArrayList();
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            } else {
                lastMatch = false;
            }
            match = true;
            i++;
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * <p>Splits the provided text into an array, separators specified,
     * preserving all tokens, including empty tokens created by adjacent
     * separators. This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separatorChars splits on whitespace.</p>
     *
     * <pre>
     * StringUtils.splitPreserveAllTokens(null, *)           = null
     * StringUtils.splitPreserveAllTokens("", *)             = []
     * StringUtils.splitPreserveAllTokens("abc def", null)   = ["abc", "def"]
     * StringUtils.splitPreserveAllTokens("abc def", " ")    = ["abc", "def"]
     * StringUtils.splitPreserveAllTokens("abc  def", " ")   = ["abc", "", def"]
     * StringUtils.splitPreserveAllTokens("ab:cd:ef", ":")   = ["ab", "cd", "ef"]
     * StringUtils.splitPreserveAllTokens("ab:cd:ef:", ":")  = ["ab", "cd", "ef", ""]
     * StringUtils.splitPreserveAllTokens("ab:cd:ef::", ":") = ["ab", "cd", "ef", "", ""]
     * StringUtils.splitPreserveAllTokens("ab::cd:ef", ":")  = ["ab", "", cd", "ef"]
     * StringUtils.splitPreserveAllTokens(":cd:ef", ":")     = ["", cd", "ef"]
     * StringUtils.splitPreserveAllTokens("::cd:ef", ":")    = ["", "", cd", "ef"]
     * StringUtils.splitPreserveAllTokens(":cd:ef:", ":")    = ["", cd", "ef", ""]
     * </pre>
     *
     * @param str  the String to parse, may be <code>null</code>
     * @param separatorChars  the characters used as the delimiters,
     *  <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, true);
    }

    /**
     * <p>Splits the provided text into an array with a maximum length,
     * separators specified, preserving all tokens, including empty tokens
     * created by adjacent separators.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * Adjacent separators are treated as one separator.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separatorChars splits on whitespace.</p>
     *
     * <p>If more than <code>max</code> delimited substrings are found, the last
     * returned string includes all characters after the first <code>max - 1</code>
     * returned strings (including separator characters).</p>
     *
     * <pre>
     * StringUtils.splitPreserveAllTokens(null, *, *)            = null
     * StringUtils.splitPreserveAllTokens("", *, *)              = []
     * StringUtils.splitPreserveAllTokens("ab de fg", null, 0)   = ["ab", "cd", "ef"]
     * StringUtils.splitPreserveAllTokens("ab   de fg", null, 0) = ["ab", "cd", "ef"]
     * StringUtils.splitPreserveAllTokens("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
     * StringUtils.splitPreserveAllTokens("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
     * StringUtils.splitPreserveAllTokens("ab   de fg", null, 2) = ["ab", "  de fg"]
     * StringUtils.splitPreserveAllTokens("ab   de fg", null, 3) = ["ab", "", " de fg"]
     * StringUtils.splitPreserveAllTokens("ab   de fg", null, 4) = ["ab", "", "", "de fg"]
     * </pre>
     *
     * @param str  the String to parse, may be <code>null</code>
     * @param separatorChars  the characters used as the delimiters,
     *  <code>null</code> splits on whitespace
     * @param max  the maximum number of elements to include in the
     *  array. A zero or negative value implies no limit
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, true);
    }

    /**
     * Performs the logic for the <code>split</code> and
     * <code>splitPreserveAllTokens</code> methods that return a maximum array
     * length.
     *
     * @param str  the String to parse, may be <code>null</code>
     * @param separatorChars the separate character
     * @param max  the maximum number of elements to include in the
     *  array. A zero or negative value implies no limit.
     * @param preserveAllTokens if <code>true</code>, adjacent separators are
     * treated as empty token separators; if <code>false</code>, adjacent
     * separators are treated as one separator.
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static String[] splitWorker(String str, String separatorChars, int max,
            boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        List list = new ArrayList();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                } else {
                    lastMatch = false;
                }
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                } else {
                    lastMatch = false;
                }
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                } else {
                    lastMatch = false;
                }
                match = true;
                i++;
            }
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    // Joining
    //-----------------------------------------------------------------------
    /**
     * <p>连接数组array为一个字符串.
     *    在数组中的null对象或者空字符串将被认为是一个空的字符串处理
     *    .</p>
     *
     * <pre>
     * StringUtils.concatenate(null)            = null
     * StringUtils.concatenate([])              = ""
     * StringUtils.concatenate([null])          = ""
     * StringUtils.concatenate(["a", "b", "c"]) = "abc"
     * StringUtils.concatenate([null, "", "a"]) = "a"
     * </pre>
     *
     * @param array    需要连接的数组, 可能为null
     * @return         连接后的字符串, <code>null</code> 如果输入的为null
     * @deprecated     应使用更好的方法{@link #join(Object[])} 来代替.
     *                 方法将在 Commons Lang 3.0.被取消
     */
    public static String concatenate(Object[] array) {
        return join(array, null);
    }

    /**
     * <p>连接数组array为一个字符串
     *    .</p>
     *
     * <p>在连接后的字符串中不包括分割符separator.
     *    在数组中的null对象或者空字符串将被认为是一个空的字符串处理
     *    .</p>
     *
     * <pre>
     * StringUtils.join(null)            = null
     * StringUtils.join([])              = ""
     * StringUtils.join([null])          = ""
     * StringUtils.join(["a", "b", "c"]) = "abc"
     * StringUtils.join([null, "", "a"]) = "a"
     * </pre>
     *
     * @param array    需要连接的数组, 可能为null
     * @return         连接后的字符串, <code>null</code> 如果输入的数组为null
     * @since 2.0
     */
    public static String join(Object[] array) {
        return join(array, null);
    }

    /**
     * <p>把一个数组array和给定的字符分割符separator连接为一个字符串
     *    .</p>
     *
     * <p>在连接的数组array前后不添加分隔符.
     *    在数组中的null对象或者空字符串将被认为是一个空的字符串处理
     *    .</p>
     *
     * <pre>
     * StringUtils.join(null, *)               = null
     * StringUtils.join([], *)                 = ""
     * StringUtils.join([null], *)             = ""
     * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
     * StringUtils.join(["a", "b", "c"], null) = "abc"
     * StringUtils.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array      需连接的数组, 可能为null
     * @param separator  字符分割符separator
     * @return           连接后的字符串, <code>null</code> 如果输入的数组为null
     * @since 2.0
     */
    public static String join(Object[] array, char separator) {
        if (array == null) {
            return null;
        }
        int arraySize = array.length;
        int bufSize = (arraySize == 0 ? 0
                : ((array[0] == null ? 16 : array[0].toString().length()) + 1) * arraySize);
        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * <p>把一个数组array和给定的字符串分割符separator连接为一个字符串
     *    .</p>
     *
     * <p>在连接的数组array前后不添加分隔符.
     *    <code>null</code> 分隔符被当成空字符串 ("").
     *    在数组中的null对象或者空字符串将被认为是一个空的字符串处理
     *    .</p>
     *
     * <pre>
     * StringUtils.join(null, *)                = null
     * StringUtils.join([], *)                  = ""
     * StringUtils.join([null], *)              = ""
     * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtils.join(["a", "b", "c"], null)  = "abc"
     * StringUtils.join(["a", "b", "c"], "")    = "abc"
     * StringUtils.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array      需连接的数组, 可能为null
     * @param separator  字符串分割符separator, null 当作 ""
     * @return           连接后的字符串, <code>null</code> 如果输入的数组为null
     */
    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }
        int arraySize = array.length;

        // ArraySize ==  0: Len = 0
        // ArraySize > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        int bufSize = ((arraySize == 0) ? 0
                : arraySize
                        * ((array[0] == null ? 16 : array[0].toString().length()) + ((separator != null) ? separator
                                .length()
                                : 0)));

        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = 0; i < arraySize; i++) {
            if ((separator != null) && (i > 0)) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * <p>把<code>Iterator</code>中的所有元素和给定的字符分割符separator连接为一个字符串
     *    .</p>
     *
     * <p>在连接的链表前后不添加分隔符.
     *    在iteration中的null对象或者空字符串将被认为是一个空的字符串处理
     *    .</p>
     *
     * <p>例如: {@link #join(Object[],char)}. </p>
     *
     * @param iterator    需要进行连接的<code>Iterator</code>, 可能为null
     * @param separator   用于间隔的字符
     * @return            连接后的字符串, <code>null</code> 如果输入的iterator为null
     * @since 2.0
     */
    @SuppressWarnings("rawtypes")
	public static String join(Iterator iterator, char separator) {
        if (iterator == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(256); // Java default is 16, probably too small
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
            if (iterator.hasNext()) {
                buf.append(separator);
            }
        }
        return buf.toString();
    }

    /**
     * <p>把<code>Iterator</code>中的所有元素和给定的字符串分割符separator连接为一个字符串
     *    .</p>
     *
     * <p>在连接的链表前后不添加分隔符.
     *    <code>null</code>的分隔符separator 被当作空字符串("").</p>
     *
     * <p>例如: {@link #join(Object[],String)}. </p>
     *
     * @param iterator     需要进行连接的 <code>Iterator</code>, 可能为null
     * @param separator    用于间隔的字符, null当作""
     * @return             连接后的字符串, <code>null</code> 如果输入的iterator为null
     */
    @SuppressWarnings("rawtypes")
	public static String join(Iterator iterator, String separator) {
        if (iterator == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(256); // Java default is 16, probably too small
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
            if ((separator != null) && iterator.hasNext()) {
                buf.append(separator);
            }
        }
        return buf.toString();
    }

    /**
     * <p>删除在{@link Character#isWhitespace(char)}定义的空白字符
     *    .</p>
     *
     * <pre>
     * StringUtils.deleteWhitespace(null)         = null
     * StringUtils.deleteWhitespace("")           = ""
     * StringUtils.deleteWhitespace("abc")        = "abc"
     * StringUtils.deleteWhitespace("   ab  c  ") = "abc"
     * </pre>
     *
     * @param str     需要移除空白的字符串, 可能为null
     * @return        移除空白的字符串, <code>null</code> 输入的字符串为null
     */
    public static String deleteWhitespace(String str) {
        if (str == null) {
            return null;
        }
        int sz = str.length();
        StringBuffer buffer = new StringBuffer(sz);
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                buffer.append(str.charAt(i));
            }
        }
        return buffer.toString();
    }

    // Replacing
    //-----------------------------------------------------------------------
    /**
     * <p>替换一个字符串text中的部分字符串repl为一新的字符串with,只替换开始的第一个.</p>
     *
     * <p>传入的参数中为<code>null</code>时这个方法不进行操作.</p>
     *
     * <pre>
     * StringUtils.replaceOnce(null, *, *)        = null
     * StringUtils.replaceOnce("", *, *)          = ""
     * StringUtils.replaceOnce("aba", null, null) = "aba"
     * StringUtils.replaceOnce("aba", null, null) = "aba"
     * StringUtils.replaceOnce("aba", "a", null)  = "aba"
     * StringUtils.replaceOnce("aba", "a", "")    = "aba"
     * StringUtils.replaceOnce("aba", "a", "z")   = "zba"
     * </pre>
     *
     * @see #replace(String text, String repl, String with, int max)
     * @param text   要进行替换的字符串, 可能为null
     * @param repl   要被替换的字符串, 可能为null
     * @param with   替换后的字符串, 可能为null
     * @return       进行替换操作后的字符串,
     *               <code>null</code> 如果输入的字符串为null
     */
    public static String replaceOnce(String text, String repl, String with) {
        return replace(text, repl, with, 1);
    }

    /**
     * <p>替换字符串text中所有出现的字符串repl替换为新的字符串with.</p>
     *
     * <p><code>null</code> 传递给这个方法将不会被处理操作.</p>
     *
     * <pre>
     * StringUtils.replace(null, *, *)        = null
     * StringUtils.replace("", *, *)          = ""
     * StringUtils.replace("aba", null, null) = "aba"
     * StringUtils.replace("aba", null, null) = "aba"
     * StringUtils.replace("aba", "a", null)  = "aba"
     * StringUtils.replace("aba", "a", "")    = "aba"
     * StringUtils.replace("aba", "a", "z")   = "zbz"
     * </pre>
     *
     * @see #replace(String text, String repl, String with, int max)
     * @param text   要进行替换的字符串, 可能为null
     * @param repl   要被替换的字符串, 可能为null
     * @param with   替换后的字符串, 可能为null
     * @return       进行替换操作后的字符串,
     *               <code>null</code> 如果输入的字符串为null
     */
    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    /**
     * <p>将字符串text中出现指定个数<code>max</code>的字符串repl为替换新的字符串with,
     *    .</p>
     *
     * <p><code>null</code> 传递给这个方法将不会被处理操作.</p>
     *
     * <pre>
     * StringUtils.replace(null, *, *, *)         = null
     * StringUtils.replace("", *, *, *)           = ""
     * StringUtils.replace("abaa", null, null, 1) = "abaa"
     * StringUtils.replace("abaa", null, null, 1) = "abaa"
     * StringUtils.replace("abaa", "a", null, 1)  = "abaa"
     * StringUtils.replace("abaa", "a", "", 1)    = "abaa"
     * StringUtils.replace("abaa", "a", "z", 0)   = "abaa"
     * StringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
     * StringUtils.replace("abaa", "a", "z", 2)   = "zbza"
     * StringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     *
     * @param text   要进行替换的字符串, 可能为null
     * @param repl   要被替换的字符串, 可能为null
     * @param with   替换后的字符串, 可能为null
     * @param max    要替换的字符串的个数, 或者 <code>-1</code> 标识没有个数限制
     * @return       进行替换操作后的字符串,
     *               <code>null</code> 如果输入的字符串为null
     */
    public static String replace(String text, String repl, String with, int max) {
        if (text == null || repl == null || with == null || repl.length() == 0 || max == 0) {
            return text;
        }

        StringBuffer buf = new StringBuffer(text.length());
        int start = 0;
        int end = 0;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();

            if (--max == 0) {
                break;
            }
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    // Replace, character based
    //-----------------------------------------------------------------------
    /**
     * <p>替换字符串str中所有出现的字符searchChar为一个新的字符replaceChar.
     *    一个 null-safe 的版本 {@link String#replace(char, char)}.</p>
     *
     * <p><code>null</code> 字符串的输入返回 <code>null</code>.
     *    空的字符串 ("") string 返回一个空字符串.</p>
     *
     * <pre>
     * StringUtils.replaceChars(null, *, *)        = null
     * StringUtils.replaceChars("", *, *)          = ""
     * StringUtils.replaceChars("abcba", 'b', 'y') = "aycya"
     * StringUtils.replaceChars("abcba", 'z', 'y') = "abcba"
     * </pre>
     *
     * @param str         要进行替换的字符串, 可能为null
     * @param searchChar  需要替换的字符, 可能为null
     * @param replaceChar 替换后的字符, 可能为null
     * @return            替换后的字符串, <code>null</code> 如果输入的字符串为null
     * @since 2.0
     */
    public static String replaceChars(String str, char searchChar, char replaceChar) {
        if (str == null) {
            return null;
        }
        return str.replace(searchChar, replaceChar);
    }

    /**
     * <p>替换字符串str中的多个字符searchChars替换为新的字符replaceChars.
     *    这个方法可以用来删除字符.</p>
     *
     * <p>例如:<br />
     * <code>replaceChars(&quot;hello&quot;, &quot;ho&quot;, &quot;jy&quot;) = jelly</code>.</p>
     *
     * <p><code>null</code> 字符串输入返回 <code>null</code>.
     *    空字符串 ("") 将返回空字符串.
     *    输入null或者空的需要替换的字符searchChars将仿佛哈字符串str本身.</p>
     *
     * <p>要被替换的字符searchChars的长度通常应该等于替换的字符replaceChars的长度.
     *    如果被替换的字符searchChars的长度更长,被替换的字符searchChars中额外的字符将被删除
     *    如果替换的字符replaceChars的长度更长,替换的字符replaceChars中额外的字符将被忽略
     *    .</p>
     *
     * <pre>
     * StringUtils.replaceChars(null, *, *)           = null
     * StringUtils.replaceChars("", *, *)             = ""
     * StringUtils.replaceChars("abc", null, *)       = "abc"
     * StringUtils.replaceChars("abc", "", *)         = "abc"
     * StringUtils.replaceChars("abc", "b", null)     = "ac"
     * StringUtils.replaceChars("abc", "b", "")       = "ac"
     * StringUtils.replaceChars("abcba", "bc", "yz")  = "ayzya"
     * StringUtils.replaceChars("abcba", "bc", "y")   = "ayya"
     * StringUtils.replaceChars("abcba", "bc", "yzx") = "ayzya"
     * </pre>
     *
     * @param str           需要被替换的字符串, 可能为null
     * @param searchChars   字符串中要被替换的字符, 可能为null
     * @param replaceChars  替换的字符, 可能为null
     * @return              替换后的字符串, <code>null</code> 如果输入的字符串为null
     * @since 2.0
     */
    public static String replaceChars(String str, String searchChars, String replaceChars) {
        if (str == null || str.length() == 0 || searchChars == null || searchChars.length() == 0) {
            return str;
        }
        char[] chars = str.toCharArray();
        int len = chars.length;
        boolean modified = false;
        for (int i = 0, isize = searchChars.length(); i < isize; i++) {
            char searchChar = searchChars.charAt(i);
            if (replaceChars == null || i >= replaceChars.length()) {
                // delete
                int pos = 0;
                for (int j = 0; j < len; j++) {
                    if (chars[j] != searchChar) {
                        chars[pos++] = chars[j];
                    } else {
                        modified = true;
                    }
                }
                len = pos;
            } else {
                // replace
                for (int j = 0; j < len; j++) {
                    if (chars[j] == searchChar) {
                        chars[j] = replaceChars.charAt(i);
                        modified = true;
                    }
                }
            }
        }
        if (!modified) {
            return str;
        }
        return new String(chars, 0, len);
    }

    // Overlay覆盖
    //-----------------------------------------------------------------------
    /**
     * <p>用字符串overlay覆盖字符串text中的部分字符.
     *    当start或者end参数有不合法的数字时会抛出IndexOutOfBoundsException异常
     *    </p>
     *
     * <pre>
     * StringUtils.overlayString(null, *, *, *)           = NullPointerException
     * StringUtils.overlayString(*, null, *, *)           = NullPointerException
     * StringUtils.overlayString("", "abc", 0, 0)         = "abc"
     * StringUtils.overlayString("abcdef", null, 2, 4)    = "abef"
     * StringUtils.overlayString("abcdef", "", 2, 4)      = "abef"
     * StringUtils.overlayString("abcdef", "zzzz", 2, 4)  = "abzzzzef"
     * StringUtils.overlayString("abcdef", "zzzz", 4, 2)  = "abcdzzzzcdef"
     * StringUtils.overlayString("abcdef", "zzzz", -1, 4) = IndexOutOfBoundsException
     * StringUtils.overlayString("abcdef", "zzzz", 2, 8)  = IndexOutOfBoundsException
     * </pre>
     *
     * @param text      将要被覆盖字符的字符串, 可能为null
     * @param overlay   覆盖的字符, 可能为null
     * @param start     覆盖字符串的起始位置,必须是有效的数字
     * @param end       覆盖字符串的结束位置,必须是有效的数字
     * @return          覆盖后的字符串, <code>null</code> 如果输入的字符串为null
     * @throws NullPointerException 如果覆盖的字符为null
     * @throws IndexOutOfBoundsException 如果起始位置start结束位置end中有无效的数字
     * @deprecated 使用 {@link #overlay(String, String, int, int)} 来代替.
     *             方法将在 Commons Lang 3.0. 被取消
     */
    public static String overlayString(String text, String overlay, int start, int end) {
        return new StringBuffer(start + overlay.length() + text.length() - end + 1).append(
                text.substring(0, start)).append(overlay).append(text.substring(end)).toString();
    }

    /**
     * <p>用字符串overlay覆盖字符串text中的部分字符.</p>
     *
     * <p><code>null</code>的字符串输入返回<code>null</code>.
     *    start和end参数中有为负数时当作0处理.
     *    start和end参数大于字符串str的长度时以str的长度来处理.
     *    start参数中是选择两个位置参数中小的那一个.</p>
     *
     * <pre>
     * StringUtils.overlay(null, *, *, *)            = null
     * StringUtils.overlay("", "abc", 0, 0)          = "abc"
     * StringUtils.overlay("abcdef", null, 2, 4)     = "abef"
     * StringUtils.overlay("abcdef", "", 2, 4)       = "abef"
     * StringUtils.overlay("abcdef", "", 4, 2)       = "abef"
     * StringUtils.overlay("abcdef", "zzzz", 2, 4)   = "abzzzzef"
     * StringUtils.overlay("abcdef", "zzzz", 4, 2)   = "abzzzzef"
     * StringUtils.overlay("abcdef", "zzzz", -1, 4)  = "zzzzef"
     * StringUtils.overlay("abcdef", "zzzz", 2, 8)   = "abzzzz"
     * StringUtils.overlay("abcdef", "zzzz", -2, -3) = "zzzzabcdef"
     * StringUtils.overlay("abcdef", "zzzz", 8, 10)  = "abcdefzzzz"
     * </pre>
     *
     * @param str      将要被覆盖字符的字符串, 可能为null
     * @param overlay  覆盖的字符, 可能为null
     * @param start    覆盖字符串的起始位置
     * @param end      覆盖字符串的结束位置
     * @return         覆盖后的字符串, <code>null</code> 如果输入的字符串为null
     * @since 2.0
     */
    public static String overlay(String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }
        if (overlay == null) {
            overlay = EMPTY;
        }
        int len = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > len) {
            start = len;
        }
        if (end < 0) {
            end = 0;
        }
        if (end > len) {
            end = len;
        }
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        return new StringBuffer(len + start - end + overlay.length() + 1).append(
                str.substring(0, start)).append(overlay).append(str.substring(end)).toString();
    }

    // Chomping
    //-----------------------------------------------------------------------
    /**
     * <p>如果字符串str的末尾有换行就去掉一个换行,多个换行的保留其他的.
     *    换行的定义为 &quot;<code>\n</code>&quot;,
     *    &quot;<code>\r</code>&quot;, 或者 &quot;<code>\r\n</code>&quot;.</p>
     *
     * <p>注意: 这个方法在 2.0. 有所改变
     *    现在和 Perl chomp 相匹配了.</p>
     *
     * <pre>
     * StringUtils.chomp(null)          = null
     * StringUtils.chomp("")            = ""
     * StringUtils.chomp("abc \r")      = "abc "
     * StringUtils.chomp("abc\n")       = "abc"
     * StringUtils.chomp("abc\r\n")     = "abc"
     * StringUtils.chomp("abc\r\n\r\n") = "abc\r\n"
     * StringUtils.chomp("abc\n\r")     = "abc\n"
     * StringUtils.chomp("abc\n\rabc")  = "abc\n\rabc"
     * StringUtils.chomp("\r")          = ""
     * StringUtils.chomp("\n")          = ""
     * StringUtils.chomp("\r\n")        = ""
     * </pre>
     *
     * @param str      需要去掉换行的字符串, 可能为null
     * @return         去掉换行的字符串, <code>null</code> 如果输入的字符串为null
     */
    public static String chomp(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }

        if (str.length() == 1) {
            char ch = str.charAt(0);
            if (ch == '\r' || ch == '\n') {
                return EMPTY;
            } else {
                return str;
            }
        }

        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);

        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
        } else if (last == '\r') {

        } else {
            lastIdx++;
        }
        return str.substring(0, lastIdx);
    }

    /**
     * <p>如果字符串<code>str</code>的末端有指定的分隔符<code>separator</code> 就去掉一个
     *    , 其他剩余的保留.</p>
     *
     * <p>注意: 这个方法在 2.0. 有所改变
     *    现在和 Perl chomp 相匹配了.
     *    先前的版本使用了 {@link #substringBeforeLast(String, String)}.
     *    这个方法使用了 {@link String#endsWith(String)}.</p>
     *
     * <pre>
     * StringUtils.chomp(null, *)         = null
     * StringUtils.chomp("", *)           = ""
     * StringUtils.chomp("foobar", "bar") = "foo"
     * StringUtils.chomp("foobar", "baz") = "foobar"
     * StringUtils.chomp("foo", "foo")    = ""
     * StringUtils.chomp("foo ", "foo")   = "foo"
     * StringUtils.chomp(" foo", "foo")   = " "
     * StringUtils.chomp("foo", "foooo")  = "foo"
     * StringUtils.chomp("foo", "")       = "foo"
     * StringUtils.chomp("foo", null)     = "foo"
     * </pre>
     *
     * @param str        需要去掉分隔符的字符串, 可能为null
     * @param separator  指定的分隔符, 可能为null
     * @return           去掉分隔符的字符串, <code>null</code>如果输入的字符串为null
     */
    public static String chomp(String str, String separator) {
        if (str == null || str.length() == 0 || separator == null) {
            return str;
        }
        if (str.endsWith(separator)) {
            return str.substring(0, str.length() - separator.length());
        }
        return str;
    }

    /**
     * <p>如果字符串str的末端是以指定的字符串sep结束的
     *    那么就删去str的末端的sep字符串.</p>
     *
     * @param str  需要处理的字符串,不能为null
     * @param sep  要去掉的字符串,不能为null
     * @return     去掉字符串后的字符串
     * @throws NullPointerException   如果参数 str 或者 sep 为 <code>null</code>
     * @deprecated 使用 {@link #chomp(String,String)} 来代替.
     *             方法将在Commons Lang 3.0.被取消
     */
    public static String chompLast(String str, String sep) {
        if (str.length() == 0) {
            return str;
        }
        String sub = str.substring(str.length() - sep.length());
        if (sep.equals(sub)) {
            return str.substring(0, str.length() - sep.length());
        } else {
            return str;
        }
    }

    /**
     * <p>去掉字符串str中最后出现指定字符sep前的所有的字符,
     *    返回指定字符sep和它后面的字符串.</p>
     *
     * @param str      需要处理的字符串, 不能为null
     * @param sep      要去掉的字符串, 不能为null
     * @return         去掉字符串后的字符串
     * @throws NullPointerException 如果参数 str 或者 sep 为 <code>null</code>
     * @deprecated 使用 {@link #substringAfterLast(String, String)} 来代替.
     *             (虽然不包括分隔符 separator)
     *             方法将在Commons Lang 3.0.被取消
     */
    public static String getChomp(String str, String sep) {
        int idx = str.lastIndexOf(sep);
        if (idx == str.length() - sep.length()) {
            return sep;
        } else if (idx != -1) {
            return str.substring(idx);
        } else {
            return EMPTY;
        }
    }

    /**
     * <p>去掉字符串str中第一个出现指定字符sep前的所有的字符,
     *    返回字符串str中第一个字符串sep后面的字符串.</p>
     *
     * @param str  需要处理的字符串, 不能为null
     * @param sep  要去掉的字符串, 不能为null
     * @return     去掉字符串后的字符串
     * @throws NullPointerException 如果参数 str 或者 sep 为 <code>null</code>
     * @deprecated 使用 {@link #substringAfter(String,String)} 来代替.
     *             方法将在Commons Lang 3.0.被取消
     */
    public static String prechomp(String str, String sep) {
        int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(idx + sep.length());
        } else {
            return str;
        }
    }

    /**
     * <p>去掉字符串str中第一个出现指定字符sep后面的所有的字符
     *    返回字符串str中第一个字符串sep前面的字符串,包括seq字符串.</p>
     *
     * <pre>
     *    StringUtils.getPrechomp("accbcabc", "bc") = "accbc"
     * </pre>
     *
     * @param str       需要处理的字符串, 不能为null
     * @param sep       进行比较的字符串, 不能为null
     * @return          去掉字符串后的字符串
     * @throws NullPointerException 如果参数 str 或者 sep 为 <code>null</code>
     * @deprecated 使用 {@link #substringBefore(String,String)} 来代替
     *             (不包括分隔符separator).
     *             方法将在Commons Lang 3.0.被取消
     */
    public static String getPrechomp(String str, String sep) {
        int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(0, idx + sep.length());
        } else {
            return EMPTY;
        }
    }

    // Chopping
    //-----------------------------------------------------------------------
    /**
     * <p>去掉字符串str末端的字符.</p>
     *
     * <p>如果字符串str是以这<code>\r\n</code>其中任意形式的换行,
     *    去掉换行.</p>
     *
     * <pre>
     * StringUtils.chop(null)          = null
     * StringUtils.chop("")            = ""
     * StringUtils.chop("abc \r")      = "abc "
     * StringUtils.chop("abc\n")       = "abc"
     * StringUtils.chop("abc\r\n")     = "abc"
     * StringUtils.chop("abc")         = "ab"
     * StringUtils.chop("abc\nabc")    = "abc\nab"
     * StringUtils.chop("a")           = ""
     * StringUtils.chop("\r")          = ""
     * StringUtils.chop("\n")          = ""
     * StringUtils.chop("\r\n")        = ""
     * </pre>
     *
     * @param str     需要去掉字符的字符串, 可能为null
     * @return        去掉末尾字符的字符串, <code>null</code> 如果输入的字符串为null
     */
    public static String chop(String str) {
        if (str == null) {
            return null;
        }
        int strLen = str.length();
        if (strLen < 2) {
            return EMPTY;
        }
        int lastIdx = strLen - 1;
        String ret = str.substring(0, lastIdx);
        char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (ret.charAt(lastIdx - 1) == '\r') {
                return ret.substring(0, lastIdx - 1);
            }
        }
        return ret;
    }

    /**
     * <p>如果字符串str的结尾是<code>\n</code>就去掉<code>\n</code>.
     *    如果<code>\n</code>前面是<code>\r</code>,同样去掉<code>\r</code>.</p>
     *
     * @param str  the String to chop a newline from, 不能为null
     * @return        去掉换行的字符串
     * @throws NullPointerException 如果参数str为<code>null</code>
     * @deprecated    使用 {@link #chomp(String)} 来代替.
     *                方法将在Commons Lang 3.0.被取消
     */
    public static String chopNewline(String str) {
        int lastIdx = str.length() - 1;
        if (lastIdx <= 0) {
            return EMPTY;
        }
        char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
        } else {
            lastIdx++;
        }
        return str.substring(0, lastIdx);
    }

    // Padding
    //-----------------------------------------------------------------------
    /**
     * <p>复制一个字符串指定的次数<code>repeat</code>
     *    .</p>
     *
     * <pre>
     * StringUtils.repeat(null, 2) = null
     * StringUtils.repeat("", 0)   = ""
     * StringUtils.repeat("", 2)   = ""
     * StringUtils.repeat("a", 3)  = "aaa"
     * StringUtils.repeat("ab", 2) = "abab"
     * StringUtils.repeat("a", -2) = ""
     * </pre>
     *
     * @param str       要复制的字符串, 可能为null
     * @param repeat    字符串str要重复的参数,负数为0
     * @return          由原来字符串进行repeat次重写后的字符串,
     *                  <code>null</code> 如果输入的字符串为null
     */
    public static String repeat(String str, int repeat) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return EMPTY;
        }
        int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return padding(repeat, str.charAt(0));
        }

        int outputLength = inputLength * repeat;
        switch (inputLength) {
        case 1:
            char ch = str.charAt(0);
            char[] output1 = new char[outputLength];
            for (int i = repeat - 1; i >= 0; i--) {
                output1[i] = ch;
            }
            return new String(output1);
        case 2:
            char ch0 = str.charAt(0);
            char ch1 = str.charAt(1);
            char[] output2 = new char[outputLength];
            for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                output2[i] = ch0;
                output2[i + 1] = ch1;
            }
            return new String(output2);
        default:
            StringBuffer buf = new StringBuffer(outputLength);
            for (int i = 0; i < repeat; i++) {
                buf.append(str);
            }
            return buf.toString();
        }
    }

    /**
     * <p>重复一个字符padChar指定的次数repeat
     *    .</p>
     *
     * <pre>
     * StringUtils.padding(0, 'e')  = ""
     * StringUtils.padding(3, 'e')  = "eee"
     * StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
     * </pre>
     *
     * @param repeat   要重复的次数
     * @param padChar  要重复的字符
     * @return         重复后的字符串
     * @throws IndexOutOfBoundsException 如果<code>参数repeat 为 &lt; 0</code>
     */
    private static String padding(int repeat, char padChar) {
        // be careful of synchronization in this method
        // we are assuming that get and set from an array index is atomic
        String pad = PADDING[padChar];
        if (pad == null) {
            pad = String.valueOf(padChar);
        }
        while (pad.length() < repeat) {
            pad = pad.concat(pad);
        }
        PADDING[padChar] = pad;
        return pad.substring(0, repeat);
    }

    /**
     * <p>为字符串str的右边添加(' ').</p>
     *
     * <p>添加(' ')后的字符串的长度为参数<code>size</code>指定的长度.</p>
     *
     * <pre>
     * StringUtils.rightPad(null, *)   = null
     * StringUtils.rightPad("", 3)     = "   "
     * StringUtils.rightPad("bat", 3)  = "bat"
     * StringUtils.rightPad("bat", 5)  = "bat  "
     * StringUtils.rightPad("bat", 1)  = "bat"
     * StringUtils.rightPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str     要添加(' ')的字符串, 可能为null
     * @param size    要添加的长度
     * @return        要添加(' ')的后字符串,如果没添加就返回原字符串.
     *                <code>null</code>如果输入的字符串为null
     */
    public static String rightPad(String str, int size) {
        return rightPad(str, size, ' ');
    }

    /**
     * <p>为字符串str的右边添加指定的字符padChar.</p>
     *
     * <p>添加指定的字符后的字符串的长度为参数<code>size</code>指定的长度.</p>
     *
     * <pre>
     * StringUtils.rightPad(null, *, *)     = null
     * StringUtils.rightPad("", 3, 'z')     = "zzz"
     * StringUtils.rightPad("bat", 3, 'z')  = "bat"
     * StringUtils.rightPad("bat", 5, 'z')  = "batzz"
     * StringUtils.rightPad("bat", 1, 'z')  = "bat"
     * StringUtils.rightPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str      要添加字符的字符串, 可能为null
     * @param size     添加字符后字符串的长度
     * @param padChar  添加的字符
     * @return         添加字符后的字符串如果没添加就返回原字符串,
     *                 <code>null</code>如果输入的字符串为null
     * @since 2.0
     */
    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(padding(pads, padChar));
    }

    /**
     * <p>为字符串str的右边添加指定的字符串padStr.</p>
     *
     * <p>添加指定的字符串padStr后的字符串的长度为参数<code>size</code>指定的长度.</p>
     *
     * <pre>
     * StringUtils.rightPad(null, *, *)      = null
     * StringUtils.rightPad("", 3, "z")      = "zzz"
     * StringUtils.rightPad("bat", 3, "yz")  = "bat"
     * StringUtils.rightPad("bat", 5, "yz")  = "batyz"
     * StringUtils.rightPad("bat", 8, "yz")  = "batyzyzy"
     * StringUtils.rightPad("bat", 1, "yz")  = "bat"
     * StringUtils.rightPad("bat", -1, "yz") = "bat"
     * StringUtils.rightPad("bat", 5, null)  = "bat  "
     * StringUtils.rightPad("bat", 5, "")    = "bat  "
     * </pre>
     *
     * @param str     要添加字符串的字符串, 可能为null
     * @param size    添加字符串后字符串的长度
     * @param padStr  要添加的字符串, null或空字符串当作(" ")
     * @return        添加字符串后的字符串,如果没添加就返回原字符串,
     *  <code>null</code> 如果输入的字符串为null
     */
    public static String rightPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (padStr == null || padStr.length() == 0) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return rightPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    /**
     * <p>为字符串str的左边添加(' ').</p>
     *
     * <p>添加(' ')后的字符串的长度为参数<code>size</code>指定的长度.</p>
     *
     * <pre>
     * StringUtils.leftPad(null, *)   = null
     * StringUtils.leftPad("", 3)     = "   "
     * StringUtils.leftPad("bat", 3)  = "bat"
     * StringUtils.leftPad("bat", 5)  = "  bat"
     * StringUtils.leftPad("bat", 1)  = "bat"
     * StringUtils.leftPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str    要添加(' ')的字符串, 可能为null
     * @param size   要添加的长度
     * @return       要添加(' ')的后字符串,如果没添加就返回原字符串,
     *               <code>null</code> i如果输入的字符串为null
     */
    public static String leftPad(String str, int size) {
        return leftPad(str, size, ' ');
    }

    /**
     * <p>为字符串str的左边添加指定的字符padChar.</p>
     *
     * <p>添加指定的字符后的字符串的长度为参数<code>size</code>指定的长度.</p>
     *
     * <pre>
     * StringUtils.leftPad(null, *, *)     = null
     * StringUtils.leftPad("", 3, 'z')     = "zzz"
     * StringUtils.leftPad("bat", 3, 'z')  = "bat"
     * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
     * StringUtils.leftPad("bat", 1, 'z')  = "bat"
     * StringUtils.leftPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str      要添加字符的字符串, 可能为null
     * @param size     添加字符后字符串的长度
     * @param padChar  添加的字符
     * @return         添加字符后的字符串,如果没添加就返回原字符串,
     *                 <code>null</code>如果输入的字符串为null
     * @since 2.0
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return padding(pads, padChar).concat(str);
    }

    /**
     * <p>为字符串str的左边添加指定的字符串padStr.</p>
     *
     * <p>添加指定的字符串padStr后的字符串的长度为参数<code>size</code>指定的长度.</p>
     *
     * <pre>
     * StringUtils.leftPad(null, *, *)      = null
     * StringUtils.leftPad("", 3, "z")      = "zzz"
     * StringUtils.leftPad("bat", 3, "yz")  = "bat"
     * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
     * StringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
     * StringUtils.leftPad("bat", 1, "yz")  = "bat"
     * StringUtils.leftPad("bat", -1, "yz") = "bat"
     * StringUtils.leftPad("bat", 5, null)  = "  bat"
     * StringUtils.leftPad("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param str     要添加字符串的字符串, 可能为null
     * @param size    添加字符串后字符串的长度
     * @param padStr  要添加的字符串, null或空字符串当作(" ")
     * @return        添加字符串后的字符串,如果没添加就返回原字符串,
     *                <code>null</code> 如果输入的字符串为null
     */
    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (padStr == null || padStr.length() == 0) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    // Centering
    //-----------------------------------------------------------------------
    /**
     * <p> 把字符串str放到指定长度<code>size</code>的中间,
     *     原字符串的前后用(' ')进行填充
     *    .<p>
     *
     * <p>如果size参数的小于字符串str的长度就直接返回字符串str.
     *    字符串参数str为<code>null</code>返回<code>null</code>.
     *    size参数为负数当作0.</p>
     *
     * <p>等同于函数<code>center(str, size, " ")</code>.</p>
     *
     * <pre>
     * StringUtils.center(null, *)   = null
     * StringUtils.center("", 4)     = "    "
     * StringUtils.center("ab", -1)  = "ab"
     * StringUtils.center("ab", 4)   = " ab "
     * StringUtils.center("abcd", 2) = "abcd"
     * StringUtils.center("a", 4)    = " a  "
     * </pre>
     *
     * @param str    需要放在中间的字符串, 可能为null
     * @param size   新的字符串的长度, 负数当作0
     * @return       放在中间后的字符串, <code>null</code> 如果输入的字符串为null
     */
    public static String center(String str, int size) {
        return center(str, size, ' ');
    }

    /**
     * <p>把字符串str放到指定长度<code>size</code>的中间.
     *    原字符串的前后用指定的字符padChar进行填充.</p>
     *
     * <p>如果size参数的小于字符串str的长度就直接返回字符串str.
     *    字符串参数str为<code>null</code>返回<code>null</code>.
     *    size参数为负数当作0.</p>
     *
     * <pre>
     * StringUtils.center(null, *, *)     = null
     * StringUtils.center("", 4, ' ')     = "    "
     * StringUtils.center("ab", -1, ' ')  = "ab"
     * StringUtils.center("ab", 4, ' ')   = " ab"
     * StringUtils.center("abcd", 2, ' ') = "abcd"
     * StringUtils.center("a", 4, ' ')    = " a  "
     * StringUtils.center("a", 4, 'y')    = "yayy"
     * </pre>
     *
     * @param str      需要放在中间的字符串, 可能为null
     * @param size     新的字符串的长度, 负数当作0
     * @param padChar  字符串str两边填充的字符
     * @return         放在中间后的字符串, <code>null</code> 如果输入的字符串为null
     * @since 2.0
     */
    public static String center(String str, int size, char padChar) {
        if (str == null || size <= 0) {
            return str;
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padChar);
        str = rightPad(str, size, padChar);
        return str;
    }

    /**
     * <p>把字符串str放到指定长度<code>size</code>的中间.
     *    原字符串的前后用指定的字符串padStr进行填充.</p>
     *
     * <p>如果size参数的小于字符串str的长度就直接返回字符串str.
     *    字符串参数str为<code>null</code>返回<code>null</code>.
     *    size参数为负数当作0.</p>
     *
     * <pre>
     * StringUtils.center(null, *, *)     = null
     * StringUtils.center("", 4, " ")     = "    "
     * StringUtils.center("ab", -1, " ")  = "ab"
     * StringUtils.center("ab", 4, " ")   = " ab"
     * StringUtils.center("abcd", 2, " ") = "abcd"
     * StringUtils.center("a", 4, " ")    = " a  "
     * StringUtils.center("a", 4, "yz")   = "yayz"
     * StringUtils.center("abc", 7, null) = "  abc  "
     * StringUtils.center("abc", 7, "")   = "  abc  "
     * </pre>
     *
     * @param str      需要放在中间的字符串, 可能为null
     * @param size     新的字符串的长度, 负数当作0
     * @param padStr   字符串str两边填充的字符串,必须不为null或者是空白
     * @return         放在中间后的字符串, <code>null</code>如果输入的字符串为null
     * @throws IllegalArgumentException 如果padStr参数为<code>null</code>或者empty
     */
    public static String center(String str, int size, String padStr) {
        if (str == null || size <= 0) {
            return str;
        }
        if (padStr == null || padStr.length() == 0) {
            padStr = " ";
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padStr);
        str = rightPad(str, size, padStr);
        return str;
    }

    // Case conversion
    //-----------------------------------------------------------------------
    /**
     * <p>把字符串中的每个字符转换为大写{@link String#toUpperCase()}.</p>
     *
     * <p><code>null</code>字符串输入返回<code>null</code>.</p>
     *
     * <pre>
     * StringUtils.upperCase(null)  = null
     * StringUtils.upperCase("")    = ""
     * StringUtils.upperCase("aBc") = "ABC"
     * </pre>
     *
     * @param str    需要转换的字符串, 可能为null
     * @return       大写后的字符串,<code>null</code> 如果输入的字符串为null
     */
    public static String upperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    /**
     * <p>把字符串中的每个字符转换为小写{@link String#toLowerCase()}.</p>
     *
     * <p><code>null</code>字符串输入返回<code>null</code>.</p>
     *
     * <pre>
     * StringUtils.lowerCase(null)  = null
     * StringUtils.lowerCase("")    = ""
     * StringUtils.lowerCase("aBc") = "abc"
     * </pre>
     *
     * @param str    需要转换的字符串, 可能为null
     * @return       小写后的字符串, <code>null</code> 如果输入的字符串为null
     */
    public static String lowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    /**
     * <p>把字符串str的第一个字符转换为大写的
     *    {@link Character#toTitleCase(char)}. 其他的字符不会改变.</p>
     *
     * <p>For a word based alorithm, 参考{@link WordUtils#capitalize(String)}.
     *    <code>null</code>字符串输入返回<code>null</code>.</p>
     *
     * <pre>
     * StringUtils.capitalize(null)  = null
     * StringUtils.capitalize("")    = ""
     * StringUtils.capitalize("cat") = "Cat"
     * StringUtils.capitalize("cAt") = "CAt"
     * </pre>
     *
     * @param str      需要大写首字符的字符串, 可能为null
     * @return         大写首字符的字符串, <code>null</code> 如果输入的字符串为null
     * @see WordUtils#capitalize(String)
     * @see #uncapitalize(String)
     * @since 2.0
     */
    public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen).append(Character.toTitleCase(str.charAt(0))).append(
                str.substring(1)).toString();
    }

    /**
     * <p>把字符串str的第一个字符转换为大写的
     *    {@link Character#toTitleCase(char)}. 其他的字符不会改变.</p>
     *
     * @param str  需要改变的string, 可能为null
     * @return the 大写后的string, 输入null返回<code>null</code>
     * @deprecated 使用标准的命名 {@link #capitalize(String)}.
     *             方法将在 Commons Lang 3.0. 被移除
     */
    public static String capitalise(String str) {
        return capitalize(str);
    }

    /**
     * <p>把字符串str的第一个字符转换为小写的
     *    {@link Character#toLowerCase(char)}. 其他的字符不会改变.</p>
     *
     * <p>For a word based alorithm, 参考{@link WordUtils#uncapitalize(String)}.
     *    <code>null</code>字符串输入返回<code>null</code>.</p>
     *
     * <pre>
     * StringUtils.uncapitalize(null)  = null
     * StringUtils.uncapitalize("")    = ""
     * StringUtils.uncapitalize("Cat") = "cat"
     * StringUtils.uncapitalize("CAT") = "cAT"
     * </pre>
     *
     * @param str      要小写首字符的字符串, 可能为null
     * @return         小写首字符的字符串, <code>null</code> 如果输入的字符串为null
     * @see WordUtils#uncapitalize(String)
     * @see #capitalize(String)
     * @since 2.0
     */
    public static String uncapitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen).append(Character.toLowerCase(str.charAt(0))).append(
                str.substring(1)).toString();
    }

    /**
     * <p>把字符串str的第一个字符转换为小写的
     *    {@link Character#toLowerCase(char)}. 其他的字符不会改变.</p>
     *
     * @param str    要小写首字符的字符串, 可能为null
     * @return       小写首字符的字符串, <code>null</code> 如果输入的字符串为null
     * @deprecated   使用标准的命名 {@link #uncapitalize(String)}.
     *               方法将在 Commons Lang 3.0. 被取消
     */
    public static String uncapitalise(String str) {
        return uncapitalize(str);
    }

    /**
     * <p>把字符串str中的字符进行大小写的交换,
     *    字符串中大写的转换为小写,小写的转换为大写.</p>
     *
     * <ul>
     *  <li>大写的字符转换为小写</li>
     *  <li>标题字符转换为小写</li>
     *  <li>小写的字符转换为大写</li>
     * </ul>
     *
     * <p>For a word based alorithm, 参考{@link WordUtils#swapCase(String)}.
     *    <code>null</code>字符串输入返回<code>null</code>.</p>
     *
     * <pre>
     * StringUtils.swapCase(null)                 = null
     * StringUtils.swapCase("")                   = ""
     * StringUtils.swapCase("The dog has a BONE") = "tHE DOG HAS A bone"
     * </pre>
     *
     * <p>注意: 这个方法在 Lang version 2.0. 有所改变
     *    It no longer performs a word based alorithm.
     *    如果使用的是ASCII, 就没有改变.
     *    在WordUtils中是有效的.</p>
     *
     * @param str    要进行大小写转换的字符串, 可能为null
     * @return       转换的字符串, <code>null</code> 如果输入的字符串为null
     */
    public static String swapCase(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        StringBuffer buffer = new StringBuffer(strLen);

        char ch = 0;
        for (int i = 0; i < strLen; i++) {
            ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                ch = Character.toUpperCase(ch);
            }
            buffer.append(ch);
        }
        return buffer.toString();
    }

    // Count matches
    //-----------------------------------------------------------------------
    /**
     * <p>找出字符串str中匹配字符串sub的总数.</p>
     *
     * <p><code>null</code>字符串或空白("")的字符串返回<code>0</code>.</p>
     *
     * <pre>
     * StringUtils.countMatches(null, *)       = 0
     * StringUtils.countMatches("", *)         = 0
     * StringUtils.countMatches("abba", null)  = 0
     * StringUtils.countMatches("abba", "")    = 0
     * StringUtils.countMatches("abba", "a")   = 2
     * StringUtils.countMatches("abba", "ab")  = 1
     * StringUtils.countMatches("abba", "xxx") = 0
     * </pre>
     *
     * @param str    需要检查的字符串, 可能为null
     * @param sub    匹配的子字符串, 可能为null
     * @return       字符串str中出现字符串sub的次数, 0 如果输入了<code>null</code>
     */
    public static int countMatches(String str, String sub) {
        if (str == null || str.length() == 0 || sub == null || sub.length() == 0) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    // Character Tests
    //-----------------------------------------------------------------------
    /**
     * <p>检查字符串str中是否只包含了unicode的字母.</p>
     *
     * <p><code>null</code> 返回<code>false</code>.
     *    空白字符串("") 返回<code>true</code>.</p>
     *
     * <pre>
     * StringUtils.isAlpha(null)   = false
     * StringUtils.isAlpha("")     = true
     * StringUtils.isAlpha("  ")   = false
     * StringUtils.isAlpha("abc")  = true
     * StringUtils.isAlpha("ab2c") = false
     * StringUtils.isAlpha("ab-c") = false
     * </pre>
     *
     * @param str     要检查的字符串, 可能为null
     * @return        <code>true</code> 如果只由字符,并且输入不为null
     */
    public static boolean isAlpha(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>查看字符串中是否只有unicode的字母或者是空白(" ").
     *    </p>
     *
     * <p><code>null</code>返回<code>false</code>
     *    空白字符("")返回<code>true</code>.</p>
     *
     * <pre>
     * StringUtils.isAlphaSpace(null)   = false
     * StringUtils.isAlphaSpace("")     = true
     * StringUtils.isAlphaSpace("  ")   = true
     * StringUtils.isAlphaSpace("abc")  = true
     * StringUtils.isAlphaSpace("ab c") = true
     * StringUtils.isAlphaSpace("ab2c") = false
     * StringUtils.isAlphaSpace("ab-c") = false
     * </pre>
     *
     * @param str     要检查的字符串, 可能为null
     * @return        <code>true</code> 如果只由字符或者(" "),而且输入不为null
     */
    public static boolean isAlphaSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((!Character.isLetter(str.charAt(i))) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>查看字符串中是否只有unicode的字母或者数字.</p>
     *
     * <p><code>null</code>返回<code>false</code>.
     *    空的字符串("")返回<code>true</code>.</p>
     *
     * <pre>
     * StringUtils.isAlphanumeric(null)   = false
     * StringUtils.isAlphanumeric("")     = true
     * StringUtils.isAlphanumeric("  ")   = false
     * StringUtils.isAlphanumeric("abc")  = true
     * StringUtils.isAlphanumeric("ab c") = false
     * StringUtils.isAlphanumeric("ab2c") = true
     * StringUtils.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param str     要检查的字符串, 可能为null
     * @return        <code>true</code>只有unicode的字母或者数字,而且输入不为null
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetterOrDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>查看字符串中是否只有unicode的字母,数字
     *    或者空白(<code>' '</code>).</p>
     *
     * <p><code>null</code> 返回<code>false</code>.
     *    空的字符串 ("") 返回<code>true</code>.</p>
     *
     * <pre>
     * StringUtils.isAlphanumeric(null)   = false
     * StringUtils.isAlphanumeric("")     = true
     * StringUtils.isAlphanumeric("  ")   = true
     * StringUtils.isAlphanumeric("abc")  = true
     * StringUtils.isAlphanumeric("ab c") = true
     * StringUtils.isAlphanumeric("ab2c") = true
     * StringUtils.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param str  要检查的字符串, 可能为null
     * @return     <code>true</code> 只有unicode的字母或者数字或者空白
     *             而且输入不为null
     */
    public static boolean isAlphanumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isLetterOrDigit(str.charAt(i)) == false) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>查看字符串中是否只有unicode的数字.
     *    小数点不认为是unicode的数字.</p>
     *
     * <p><code>null</code> 返回 <code>false</code>.
     *    空的字符串("") 返回 <code>true</code>.</p>
     *
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = true
     * StringUtils.isNumeric("  ")   = false
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("12 3") = false
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * </pre>
     *
     * @param str  要检查的字符串, 可能为null
     * @return     <code>true</code> 只有unicode的数字,而且输入不为null
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>查看字符串中是否只有unicode的数字
     *    和空白(<code>' '</code>).
     *    小数点不认为是unicode的数字.</p>
     *
     * <p><code>null</code> 返回 <code>false</code>.
     *    空的字符串("") 返回 <code>true</code>.</p>
     *
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = true
     * StringUtils.isNumeric("  ")   = true
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("12 3") = true
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * </pre>
     *
     * @param str  要检查的字符串, 可能为null
     * @return <code>true</code> 只有unicode的数字或者空白,而且输入不为null
     */
    public static boolean isNumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isDigit(str.charAt(i)) == false) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>查看字符串中是否只有空白字符.</p>
     *
     * <p><code>null</code> 返回 <code>false</code>.
     *    空的字符串("") 返回 <code>true</code>.</p>
     *
     * <pre>
     * StringUtils.isWhitespace(null)   = false
     * StringUtils.isWhitespace("")     = true
     * StringUtils.isWhitespace("  ")   = true
     * StringUtils.isWhitespace("abc")  = false
     * StringUtils.isWhitespace("ab2c") = false
     * StringUtils.isWhitespace("ab-c") = false
     * </pre>
     *
     * @param str  要检查的字符串, 可能为null
     * @return     <code>true</code> 只有空白字符,并且输入不为null
     * @since 2.0
     */
    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    // Defaults
    //-----------------------------------------------------------------------
    /**
     * <p>但输入的字符串为null时返回(""),其他情况返回字符串本身
     *    .</p>
     *
     * <pre>
     * StringUtils.defaultString(null)  = ""
     * StringUtils.defaultString("")    = ""
     * StringUtils.defaultString("bat") = "bat"
     * </pre>
     *
     * @see ObjectUtils#toString(Object)
     * @see String#valueOf(Object)
     * @param str  要检查的字符串, 可能为null
     * @return     返回输入的字符串, 如果输入为<code>null</code>返回""
     */
    public static String defaultString(String str) {
        return (str == null ? EMPTY : str);
    }

    /**
     * <p>检查输入的字符串是否为<code>null</code>是就返回指定的默认值defaultStr,
     *    不是就返回字符串自己本身</p>
     *
     * <pre>
     * StringUtils.defaultString(null, "null")  = "null"
     * StringUtils.defaultString("", "null")    = ""
     * StringUtils.defaultString("bat", "null") = "bat"
     * </pre>
     *
     * @see ObjectUtils#toString(Object,String)
     * @see String#valueOf(Object)
     * @param str          要检查的字符串, 可能为null
     * @param defaultStr   默认返回的字符串
     *                     如果输入为<code>null</code>, 可能为null
     * @return             不为<code>null</code>是返回字符串str本身
     */
    public static String defaultString(String str, String defaultStr) {
        return (str == null ? defaultStr : str);
    }

    // Reversing
    //-----------------------------------------------------------------------
    /**
     * <p>字符串中的每个字符进行反序{@link StringBuffer#reverse()}.</p>
     *
     * <p><A code>null</code>返回<code>null</code>.</p>
     *
     * <pre>
     * StringUtils.reverse(null)  = null
     * StringUtils.reverse("")    = ""
     * StringUtils.reverse("bat") = "tab"
     * </pre>
     *
     * @param str    要进行反序的字符串, 可能为null
     * @return       反序的字符串, <code>null</code> 如果输入的字符串为null
     */
    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuffer(str).reverse().toString();
    }

    //-----------------------------------------------------------------------
    /**
     * <p>对一个字符串进行省略缩写.
     *    例如转换 "Now is the time for all good men" 为 "Now is the time for..."</p>
     *
     * <p>特殊的情况:
     * <ul>
     *   <li>如果参数字符串<code>str</code>的长度小于参数<code>maxWidth</code>
     *       返回字符串自己本身.</li>
     *   <li>缩写的形式为<code>(substring(str, 0, max-3) + "...")</code>.</li>
     *   <li>如果参数<code>maxWidth</code>小于<code>4</code>, 抛出异常
     *       <code>IllegalArgumentException</code>.</li>
     *   <li>其他情况会返回一个
     *       <code>maxWidth</code>指定的长度的缩写字符串.</li>
     * </ul>
     * </p>
     *
     * <pre>
     * StringUtils.abbreviate(null, *)      = null
     * StringUtils.abbreviate("", 4)        = ""
     * StringUtils.abbreviate("abcdefg", 6) = "abc..."
     * StringUtils.abbreviate("abcdefg", 7) = "abcdefg"
     * StringUtils.abbreviate("abcdefg", 8) = "abcdefg"
     * StringUtils.abbreviate("abcdefg", 4) = "a..."
     * StringUtils.abbreviate("abcdefg", 3) = IllegalArgumentException
     * </pre>
     *
     * @param str       需要转换的字符串, 可能为null
     * @param maxWidth  返回的字符串的长度, 最少必须为4
     * @return          简略的字符串, <code>null</code> 如果输入的字符串为null
     * @throws IllegalArgumentException 如果maxWidth小于4
     * @since 2.0
     */
    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }

    /**
     * <p>对一个字符串进行省略缩写.
     *    例如转换"Now is the time for all good men"为"...is the time for..."</p>
     *
     * <p>类似<code>abbreviate(String, int)</code>, 但是你可以指定左边字符串的起始位置.
     *    注意 以左边开始的offset的字符在结果中不一定是最左边,
     *    第一个字符可能是(...)但是offset的位置的字符一定会在结果中.
     *
     * <p>其他情况会返回一个<code>maxWidth</code>指定的长度的缩写字符串.</p>
     *
     * <pre>
     * StringUtils.abbreviate(null, *, *)                = null
     * StringUtils.abbreviate("", 0, 4)                  = ""
     * StringUtils.abbreviate("abcdefghijklmno", -1, 10) = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 0, 10)  = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 1, 10)  = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 4, 10)  = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 5, 10)  = "...fghi..."
     * StringUtils.abbreviate("abcdefghijklmno", 6, 10)  = "...ghij..."
     * StringUtils.abbreviate("abcdefghijklmno", 8, 10)  = "...ijklmno"
     * StringUtils.abbreviate("abcdefghijklmno", 10, 10) = "...ijklmno"
     * StringUtils.abbreviate("abcdefghijklmno", 12, 10) = "...ijklmno"
     * StringUtils.abbreviate("abcdefghij", 0, 3)        = IllegalArgumentException
     * StringUtils.abbreviate("abcdefghij", 5, 6)        = IllegalArgumentException
     * </pre>
     *
     * @param str         需要转换的字符串, 可能为null
     * @param offset      左边开始的字符的偏移位置
     * @param maxWidth    返回的结果的最大长度, 最少必须为 4
     * @return            缩写的字符串, <code>null</code> 如果输入的字符串为null
     * @throws IllegalArgumentException 如果maxWidth参数太小
     * @since 2.0
     */
    public static String abbreviate(String str, int offset, int maxWidth) {
        if (str == null) {
            return null;
        }
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        if (offset > str.length()) {
            offset = str.length();
        }
        if ((str.length() - offset) < (maxWidth - 3)) {
            offset = str.length() - (maxWidth - 3);
        }
        if (offset <= 4) {
            return str.substring(0, maxWidth - 3) + "...";
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if ((offset + (maxWidth - 3)) < str.length()) {
            return "..." + abbreviate(str.substring(offset), maxWidth - 3);
        }
        return "..." + str.substring(str.length() - (maxWidth - 3));
    }

    // Difference
    //-----------------------------------------------------------------------
    /**
     * <p>比较两个字符串str1,str2 返回str2不同于str1的位置.
     *   (返回第二个字符串str2的从开始和
     *   第一个字符串str1不同的位置到末尾的剩余部分.)</p>
     *
     * <p>例如,
     * <code>difference("i am a machine", "i am a robot") -> "robot"</code>.</p>
     *
     * <pre>
     * StringUtils.difference(null, null) = null
     * StringUtils.difference("", "") = ""
     * StringUtils.difference("", "abc") = "abc"
     * StringUtils.difference("abc", "") = ""
     * StringUtils.difference("abc", "abc") = ""
     * StringUtils.difference("ab", "abxyz") = "xyz"
     * StringUtils.difference("abcde", "abxyz") = "xyz"
     * StringUtils.difference("abcde", "xyz") = "xyz"
     * </pre>
     *
     * @param str1     第一个字符串, 可能为null
     * @param str2     第二个字符串, 可能为null
     * @return         返回第二个字符串str2不同于第一个字符串str1的字符串;
     *                 两个字符串相等就返回("")
     * @since 2.0
     */
    public static String difference(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        int at = indexOfDifference(str1, str2);
        if (at == -1) {
            return EMPTY;
        }
        return str2.substring(at);
    }

    /**
     * <p>比较两个字符串str1,str2 返回他们开始不相同时的位置
     *    .</p>
     *
     * <p>例如,
     * <code>indexOfDifference("i am a machine", "i am a robot") -> 7</code></p>
     *
     * <pre>
     * StringUtils.indexOfDifference(null, null) = -1
     * StringUtils.indexOfDifference("", "") = -1
     * StringUtils.indexOfDifference("", "abc") = 0
     * StringUtils.indexOfDifference("abc", "") = 0
     * StringUtils.indexOfDifference("abc", "abc") = -1
     * StringUtils.indexOfDifference("ab", "abxyz") = 2
     * StringUtils.indexOfDifference("abcde", "abxyz") = 2
     * StringUtils.indexOfDifference("abcde", "xyz") = 0
     * </pre>
     *
     * @param str1  第一个字符串,可能为null
     * @param str2  第二个字符串,可能为null
     * @return      字符串 str2 和字符串 str1 开始不同的位置; -1 表示他们相等
     * @since 2.0
     */
    public static int indexOfDifference(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return 0;
        }
        if (str1.equals(str2)) {
            return -1;
        }
        int i;
        for (i = 0; i < str1.length() && i < str2.length(); ++i) {
            if (str1.charAt(i) != str2.charAt(i)) {
                break;
            }
        }
        if (i < str2.length() || i < str1.length()) {
            return i;
        }
        return -1;
    }

    // Misc
    //-----------------------------------------------------------------------
    /**
     * <p>计算两字符串之间的Levenshtein-Distance.</p>
     *
     * <p>是字符串s要变成字符串t需要改动的字符的个,一次改动一个字符
     *    (删除,插入或者置换).</p>
     *
     * <p> Levenshtein distance 的算法规则可以参考
     *     <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a></p>
     *
     * <pre>
     * StringUtils.getLevenshteinDistance(null, *)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance(*, null)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance("","")               = 0
     * StringUtils.getLevenshteinDistance("","a")              = 1
     * StringUtils.getLevenshteinDistance("aaapppp", "")       = 7
     * StringUtils.getLevenshteinDistance("frog", "fog")       = 1
     * StringUtils.getLevenshteinDistance("fly", "ant")        = 3
     * StringUtils.getLevenshteinDistance("elephant", "hippo") = 7
     * StringUtils.getLevenshteinDistance("hippo", "elephant") = 7
     * StringUtils.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
     * StringUtils.getLevenshteinDistance("hello", "hallo")    = 1
     * </pre>
     *
     * @param s  第一个字符串, 不能为null
     * @param t  第二个字符串, 不能为null
     * @return   返回要改动的字符的个数
     * @throws IllegalArgumentException 如果输入的任何一个字符串为<code>null</code>
     */
    public static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        int d[][]; // matrix
        int n; // length of s
        int m; // length of t
        int i; // iterates through s
        int j; // iterates through t
        char s_i; // ith character of s
        char t_j; // jth character of t
        int cost; // cost

        // Step 1
        n = s.length();
        m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];

        // Step 2
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        // Step 3
        for (i = 1; i <= n; i++) {
            s_i = s.charAt(i - 1);

            // Step 4
            for (j = 1; j <= m; j++) {
                t_j = t.charAt(j - 1);

                // Step 5
                if (s_i == t_j) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                // Step 6
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
            }
        }

        // Step 7
        return d[n][m];
    }

    /**
     * <p>得到三个<code>int</code>数字中的最小值.</p>
     *
     * @param a  值1
     * @param b  值2
     * @param c  值3
     * @return   最小值
     */
    private static int min(int a, int b, int c) {
        // Method copied from NumberUtils to avoid dependency on subpackage
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }

    /**
     * 长整型类型数组转化为数组的方法
     * @param 长整型类型数组
     * @return 字符串
     */
    public static String toString(long[] lArray) {
        if (lArray == null) {
            return "null";
        }
        if (lArray.length == 0) {
            return "[]";
        }

        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(lArray[0]);

        for (int i = 1; i < lArray.length; i++) {
            sb.append(", ");
            sb.append(lArray[i]);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * 整型类型数组转化为数组的方法
     * @param 整型类型数组
     * @return 字符串
     */
    public static String toString(int[] iArray) {
        if (iArray == null) {
            return "null";
        }
        if (iArray.length == 0) {
            return "[]";
        }

        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(iArray[0]);

        for (int i = 1; i < iArray.length; i++) {
            sb.append(", ");
            sb.append(iArray[i]);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * 短整型类型数组转化为数组的方法
     * @param 短整型类型数组
     * @return 字符串
     */
    public static String toString(short[] sArray) {
        if (sArray == null) {
            return "null";
        }
        if (sArray.length == 0) {
            return "[]";
        }

        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(sArray[0]);

        for (int i = 1; i < sArray.length; i++) {
            sb.append(", ");
            sb.append(sArray[i]);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * 字符类型数组转化为数组的方法
     * @param 字符类型数组
     * @return 字符串
     */
    public static String toString(char[] cArray) {
        if (cArray == null) {
            return "null";
        }
        if (cArray.length == 0) {
            return "[]";
        }

        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(cArray[0]);

        for (int i = 1; i < cArray.length; i++) {
            sb.append(", ");
            sb.append(cArray[i]);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * byte类型数组转化为数组的方法
     * @param byte类型数组
     * @return 字符串
     */
    public static String toString(byte[] bArray) {
        if (bArray == null) {
            return "null";
        }
        if (bArray.length == 0) {
            return "[]";
        }

        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(bArray[0]);

        for (int i = 1; i < bArray.length; i++) {
            sb.append(", ");
            sb.append(bArray[i]);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * boolean类型数组转化为数组的方法
     * @param boolean类型数组
     * @return 字符串
     */
    public static String toString(boolean[] bArray) {
        if (bArray == null) {
            return "null";
        }
        if (bArray.length == 0) {
            return "[]";
        }

        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(bArray[0]);

        for (int i = 1; i < bArray.length; i++) {
            sb.append(", ");
            sb.append(bArray[i]);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * 浮点类型数组转化为数组的方法
     * @param 浮点类型数组
     * @return 字符串
     */
    public static String toString(float[] fArray) {
        if (fArray == null) {
            return "null";
        }
        if (fArray.length == 0) {
            return "[]";
        }

        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(fArray[0]);

        for (int i = 1; i < fArray.length; i++) {
            sb.append(", ");
            sb.append(fArray[i]);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * 双精度类型数组转化为数组的方法
     * @param 双精度类型数组
     * @return 字符串
     */
    public static String toString(double[] dArray) {
        if (dArray == null) {
            return "null";
        }
        if (dArray.length == 0) {
            return "[]";
        }

        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(dArray[0]);

        for (int i = 1; i < dArray.length; i++) {
            sb.append(", ");
            sb.append(dArray[i]);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Object类型数组转化为数组的方法
     * @param Object类型数组
     * @return 字符串
     */
    public static String toString(Object[] objArray) {
        if (objArray == null) {
            return "null";
        }
        if (objArray.length == 0) {
            return "[]";
        }

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < objArray.length; i++) {
            if (i == 0) {
                sb.append('[');
            } else {
                sb.append(", ");
            }

            sb.append(String.valueOf(objArray[i]));
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * 是否包含汉字字符
     * @param cnStr 指定字符串
     * @return true:是 false:否
     */
    public static boolean isContainChineseCharacter(String cnStr) {
        if (StringUtils.isEmpty(cnStr)) {
            return false;
        }
        char[] chars = cnStr.toCharArray();
        boolean bIsContainChineseCharacter = false;
        for (int i = 0; i < chars.length; i++) {
            byte[] bytes = ("" + chars[i]).getBytes();
            if (bytes.length == 2) {
                int[] ints = new int[2];
                ints[0] = bytes[0] & 0xff;
                ints[1] = bytes[1] & 0xff;
                if (ints[0] >= 0x81 && ints[0] <= 0xFE && ints[1] >= 0x40 && ints[1] <= 0xFE) {
                    bIsContainChineseCharacter = true;
                    break;
                }
            }
        }
        return bIsContainChineseCharacter;
    }

    /**
     * 得到字符串的长度，一个汉字长度为2
     * @param str 指定字符串
     * @return int 字符串的长度
     */
    public static int getStringLength(String str) {
        if (StringUtils.isEmpty(str)) {
            return 0;
        }
        int iLen = 0;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char charTemp = chars[i];
            String strTemp = String.valueOf(charTemp);
            if (StringUtils.isContainChineseCharacter(strTemp)) {
                iLen += 2;
            } else {
                iLen++;
            }
        }
        return iLen;
    }

    public static boolean isNumber(String str) {
        if(isBlank(str)) {
            return false;
        } else {
            Pattern regex = Pattern.compile("(-)?\\d*(.\\d*)?");
            Matcher matcher = regex.matcher(str);
            return matcher.matches();
        }
    }
}
