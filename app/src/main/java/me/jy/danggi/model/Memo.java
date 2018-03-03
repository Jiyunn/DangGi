package me.jy.danggi.model;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by JY on 2018-01-12.
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Memo implements Serializable{

    private int id;
    private String content;
    private Date writeDate;


    public Memo ( String content ) {
        this.content = content;
    }

    public Memo ( String content, Date writeDate ) {
        this.content = content;
        this.writeDate = writeDate;
    }

    public Memo ( int id, String content ) {
        this.id = id;
        this.content = content;
    }
}
