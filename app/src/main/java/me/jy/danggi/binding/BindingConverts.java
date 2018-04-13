package me.jy.danggi.binding;

import android.databinding.BindingConversion;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Data Binding conversion methods
 * Created by JY on 2018-02-07.
 */

public class BindingConverts{

    @BindingConversion
    public static String convertDate( Date date ) {
        return new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm" , Locale.KOREAN).format(date);
    }

    @BindingConversion
    public static Drawable convertByteArrayToDrawable( byte[] bytes ) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0 , bytes.length);
        return new BitmapDrawable(bitmap);
    }


}
