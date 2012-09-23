package ru.android.hellslade.autochmo;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;

public class AGosnomerCheck implements InputFilter{
    private int gn_error_level = 0;
    private String gn_type = "";
    private String mNomer;
    private int mResult = 0;
    private boolean bNoNomer = false;

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
        Spanned dest, int dstart, int dend) {
        String s = source.subSequence(start, end).toString();
        String d = dest.subSequence(dstart, dend).toString();
        
        mNomer = (dest.toString() + source.toString()).toUpperCase();
        if (transliterate_gn()) {
            return "";
        }
        mResult = checkGosNomer();
        //String checkedText = dest.toString() + source.toString();
        //String pattern = getPattern();
        
        /*if (!Pattern.matches(pattern, checkedText)) {
            return "";
        }*/
        Log.v("mResult=" + mResult);
        if (mResult != 0 && mResult != 2) {
            return "";
        }
        return null;
    }
    /*public AGosnomerCheck(String nomer) {
        mNomer = nomer.toUpperCase();
//        capitalize();
        transliterate_gn();
        mResult = checkGosNomer();
    }*/
    public String getNomer() {
        Log.v(String.format("%s %s %s", gn_error_level, gn_type, mResult));
        return mNomer;
    }
    /**
     * Проверка валидности госномера.
     * @param String gn номер
     * @return int возвращаемые значения:
     *    0 всё ок
     *    1 кириллические кривые буквы в номере (которых там не может быть) (неактуально)
     *    2 латинские буквы в номере (неактуально)
     *    3 вообще странные символы в номере
     *    4 неправильный формат номера (или введён не до конца)
     */
    public int checkGosNomer()
    {
        Log.v("checkGosNomer() " + bNoNomer + " " + mNomer);
        if(bNoNomer)
        {
            gn_error_level = 0;
            return 0;
        }
        String spec = "\\\"/`~!@#$%^&*()=_+[]\";:,.<>?| ";
        for(String c : mNomer.split("")) {
            if (!c.isEmpty() && spec.indexOf(c) != -1) {
                Log.v("matches" + c + ":" + mNomer + ":");
                gn_error_level = 2;
                return 1;
            }
        }
        String regexp = "^[A-Z][0-9]{3}[A-Z]{2}[0-9]{2,3}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches1");
            gn_type = "rus"; // российский
            gn_error_level = 0;
            return 0;
        }
        regexp = "^[A-Z][0-9]{3}[A-Z]{2}[0-9]{0,1}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches2");
            gn_type = "rus";
            gn_error_level = 1;
            return 2;
        }
        regexp = "^[A-Z][0-9]{6,7}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches3");
            gn_type = "mvd"; //МВД
            gn_error_level = 0;
            return 0;
        }
        regexp = "^[A-Z][0-9]{4,5}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches4");
            gn_type = "mvd";
            gn_error_level = 1;
            return 2;
        }
        regexp = "^[A-Z]{2}[0-9]{3}[A-Z][0-9]{2,3}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches5");
            gn_type = "trans"; // Транзит
            gn_error_level = 0;
            return 0;
        }
        regexp = "^[A-Z]{2}[0-9]{3}[A-Z][0-9]{0,1}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches6");
            gn_type = "trans";
            gn_error_level = 1;
            return 2;
        }
        regexp = "^[0-9]{3}(CC|CD|D|T)[0-9]{3,6}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches7");
            gn_type = "dip"; // Дипломатический
            gn_error_level = 0;
            return 0;
        }
        regexp = "^[0-9]{3}(CC|CD|D|T)[0-9]{2}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches8");
            gn_type = "dip";
            gn_error_level = 1;
            return 2;
        }
        regexp = "^[0-9]{4}[A-Z]{2}[0-9]{2}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches9");
            gn_type = "mo"; //Военный
            gn_error_level = 0;
            return 0;
        }
        regexp = "^[0-9]{4}[A-Z]{2}[0-9]{0,1}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches10");
            gn_type = "mo";
            gn_error_level = 1;
            return 2;
        }
        regexp = "^T[A-Z]{2}[0-9]{5,6}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches11");
            gn_type = "exp"; //Номер вывозимой за границу машины
            gn_error_level = 0;
            return 0;
        }
        regexp = "^T[A-Z]{2}[0-9]{3,4}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches12");
            gn_type = "exp";
            gn_error_level = 1;
            return 2;
        }
        regexp = "^[A-Z]{2}[0-9]{5,6}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches13");
            gn_type = "pub"; //Общественный транспорт
            gn_error_level = 0;
            return 0;
        }
        regexp = "^[A-Z]{2}[0-9]{3,4}$";
        if(mNomer.matches(regexp)) {
            Log.v("matches14");
            gn_type = "pub";
            gn_error_level = 1;
            return 2;
        }
        gn_type = "";
        gn_error_level = 0;
        return 0;
    }
    
    /**
     * Транслитирировать строку.
     * @param   String cyr_str
     * @returns String
     */
    public boolean transliterate_gn()
    {
        boolean isCyrilic = false;
        Log.v("transliterate_gn()");
        String[] alphabet_c = new String[]
        {           
            "А", "Б", "В", "Г", "Д", "Е", "Ё", "Ж", "З", "И",
            "Й", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т",
            "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ъ", "Ы", "Ь",
            "Э", "Ю", "Я"
        };
        String[] alphabet_a = new String[]
        {
            "A", "", "B", "", "", "E", "", "", "", "",
            "", "K", "", "M", "H", "O", "", "P", "C", "T",
            "Y", "", "X", "", "", "", "", "", "", "",
            "", "", ""
        };
        Map<String, String> alphabet = new HashMap<String, String>();
        
        for (int i=0;i<alphabet_c.length;i++) {
            alphabet.put(alphabet_c[i], alphabet_a[i]);
        }
        String result = "";
        String replacing;
        String[] cyr_s = mNomer.split("");
        for(String c : cyr_s) {
            if (alphabet.containsKey(c)) {
                replacing = alphabet.get(c);
                result += replacing;
                if (replacing == "") {
                    isCyrilic = true;
                }
            } else {
                result += c;
            }
        }
        mNomer = result;
        return isCyrilic;
    }
    /**
     * Сделать все буквы заглавными, даже русские.
     * @param   String str исходная строка
     * @returns String РЕЗУЛЬТАТ
     */
    /*public void capitalize()
    {
        String low = new String("qwertyuiopasdfghjklzxcvbnmйцукенгшщзхъфывапролджэячсмитьбю");
        String up = new String("QWERTYUIOPASDFGHJKLZXCVBNMЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ");
        String result = "";
        int i;
        String[] str = mNomer.split("");
        for(String c : str) {
                i = low.indexOf(c);
                
                if(i > -1) {
                    result += up.charAt(i);
                } else {
                    result += c;
                }
        }
        mNomer = result;
    }*/

}