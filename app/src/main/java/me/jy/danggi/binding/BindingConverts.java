package me.jy.danggi.binding;

import android.databinding.BindingConversion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**Data Binding conversion methods
 * Created by JY on 2018-02-07.
 */

public class BindingConverts {

    @BindingConversion
    public static String convertDate(Date date) {
        return new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.KOREAN).format(date);
    }
}
