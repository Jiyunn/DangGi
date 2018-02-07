package me.jy.danggi.activities.model;

import android.support.annotation.NonNull;

/**
 * Created by JY on 2018-01-12.
 */

public class Memo implements Comparable{

    private String content;
    private String writeDate;

    public Memo (String content, String writeDate) {
        this.content = content;
        this.writeDate = writeDate;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo ( @NonNull Object o ) {
        return 0;
    }
}
