package me.jy.danggi.binding;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class BindingAdapters{

    @BindingAdapter( {"thumbRes"})
    public static void thumbnailLoad( ImageView imageView, byte[] thumbnailBytes) {
        Glide.with(imageView.getContext())
                .load(thumbnailBytes)
                .into(imageView);
    }
}
