package me.jy.danggi.activities.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by JY on 2018-01-12.
 */
@Data
@AllArgsConstructor(staticName = "of")
public class Memo {

    private String content;
    private Date writeDate;

}
