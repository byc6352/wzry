/**
 * 
 */
package util;

import java.util.regex.Pattern;

import static util.ConstUtils.*;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/2
 *     desc  : ������ع�����
 * </pre>
 */
public class RegularUtils {

    private RegularUtils() {
        throw new UnsupportedOperationException("u can't fuck me...");
    }

    /**
     * If u want more please visit http://toutiao.com/i6231678548520731137/
     */

    /**
     * ��֤�ֻ��ţ��򵥣�
     *
     * @param string ����֤�ı�
     * @return {@code true}: ƥ��<br>{@code false}: ��ƥ��
     */
    public static boolean isMobileSimple(String string) {
        return isMatch(REGEX_MOBILE_SIMPLE, string);
    }

    /**
     * ��֤�ֻ��ţ���ȷ��
     *
     * @param string ����֤�ı�
     * @return {@code true}: ƥ��<br>{@code false}: ��ƥ��
     */
    public static boolean isMobileExact(String string) {
        return isMatch(REGEX_MOBILE_EXACT, string);
    }

    /**
     * ��֤�绰����
     *
     * @param string ����֤�ı�
     * @return {@code true}: ƥ��<br>{@code false}: ��ƥ��
     */
    public static boolean isTel(String string) {
        return isMatch(REGEX_TEL, string);
    }

    /**
     * ��֤���֤����15λ
     *
     * @param string ����֤�ı�
     * @return {@code true}: ƥ��<br>{@code false}: ��ƥ��
     */
    public static boolean isIDCard15(String string) {
        return isMatch(REGEX_IDCARD15, string);
    }

    /**
     * ��֤���֤����18λ
     *
     * @param string ����֤�ı�
     * @return {@code true}: ƥ��<br>{@code false}: ��ƥ��
     */
    public static boolean isIDCard18(String string) {
        return isMatch(REGEX_IDCARD18, string);
    }

    /**
     * ��֤����
     *
     * @param string ����֤�ı�
     * @return {@code true}: ƥ��<br>{@code false}: ��ƥ��
     */
    public static boolean isEmail(String string) {
        return isMatch(REGEX_EMAIL, string);
    }

    /**
     * ��֤URL
     *
     * @param string ����֤�ı�
     * @return {@code true}: ƥ��<br>{@code false}: ��ƥ��
     */
    public static boolean isURL(String string) {
        return isMatch(REGEX_URL, string);
    }

    /**
     * ��֤����
     *
     * @param string ����֤�ı�
     * @return {@code true}: ƥ��<br>{@code false}: ��ƥ��
     */
    public static boolean isChz(String string) {
        return isMatch(REGEX_CHZ, string);
    }

    /**
     * ��֤�û���
     * <p>ȡֵ��ΧΪa-z,A-Z,0-9,"_",���֣�������"_"��β,�û���������6-20λ</p>
     *
     * @param string ����֤�ı�
     * @return {@code true}: ƥ��<br>{@code false}: ��ƥ��
     */
    public static boolean isUsername(String string) {
        return isMatch(REGEX_USERNAME, string);
    }

    /**
     * ��֤yyyy-MM-dd��ʽ������У�飬�ѿ���ƽ����
     *
     * @param string ����֤�ı�
     * @return {@code true}: ƥ��<br>{@code false}: ��ƥ��
     */
    public static boolean isDate(String string) {
        return isMatch(REGEX_DATE, string);
    }

    /**
     * ��֤IP��ַ
     *
     * @param string ����֤�ı�
     * @return {@code true}: ƥ��<br>{@code false}: ��ƥ��
     */
    public static boolean isIP(String string) {
        return isMatch(REGEX_IP, string);
    }

    /**
     * string�Ƿ�ƥ��regex
     *
     * @param regex  ������ʽ�ַ���
     * @param string Ҫƥ����ַ���
     * @return {@code true}: ƥ��<br>{@code false}: ��ƥ��
     */
    public static boolean isMatch(String regex, String string) {
        return !StringUtils.isEmpty(string) && Pattern.matches(regex, string);
    }
}