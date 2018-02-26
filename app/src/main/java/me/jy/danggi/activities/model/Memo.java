package me.jy.danggi.activities.model;

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

    private String content;
    private Date writeDate;


}
