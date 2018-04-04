package me.jy.danggi.database;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import me.jy.danggi.model.Memo;
import me.jy.danggi.model.Widget;


/**
 * Realm Data Helper class
 * Created by JY on 2018-04-01.
 */

public class DataHelper {

    /**
     * get Memo find by Id
     *
     * @param realm Realm
     * @param id    id
     * @return Memo
     */
    public static Memo findMemoById ( Realm realm , int id ) {
        return realm.where(Memo.class).equalTo("id" , id).findFirst();
    }

    /**
     * get Memo find by widget Id
     *
     * @param realm    Realm
     * @param widgetId widget id
     * @return Memo
     */
    public static Memo findMemoByWidgetId ( Realm realm , final int widgetId ) {
        return realm.where(Widget.class).equalTo("widgetId" , widgetId).findFirst().getMemo();
    }

    /**
     * add Memo
     *
     * @param realm   Realm
     * @param content content
     */
    public static void addMemoAsync ( Realm realm , final String content ) {
        realm.executeTransactionAsync(r -> {
            Number maxId = r.where(Memo.class).max("id");
            int id = ( maxId != null ) ? maxId.intValue() + 1 : 1;

            Memo memo = r.createObject(Memo.class , id);
            memo.setContent(content);
            memo.setWriteDate(new Date(System.currentTimeMillis()));
        });
    }

    /**
     * update memo
     *
     * @param realm   Realm
     * @param id      id
     * @param content updated content
     */
    public static void updateMemoAsync ( Realm realm , final int id , final String content ) {
        realm.executeTransactionAsync(r -> {
            Memo memo = r.where(Memo.class).equalTo("id" , id).findFirst();
            if ( memo != null ) {
                memo.setContent(content);
                memo.setWriteDate(new Date(System.currentTimeMillis()));
                r.insertOrUpdate(memo);
            }
        });
    }

    /**
     * delete Memo
     *
     * @param realm Realm
     * @param id    id
     */
    public static void deleteMemo ( Realm realm , final int id ) {
        realm.executeTransactionAsync(r -> {
            Memo memo = r.where(Memo.class).equalTo("id" , id).findFirst();
            if ( memo != null )
                memo.deleteFromRealm();
        });
    }

    /**
     * get Widget find by memo id
     *
     * @param realm  Realm
     * @param memoId memo id
     * @return ReamResult
     */
    public static RealmResults<Widget> findWidgetByMemoId ( Realm realm , int memoId ) {
        return realm.where(Widget.class)
                .equalTo("memo.id" , memoId)
                .findAll();
    }

    /**
     * insert or update Widget
     *
     * @param realm    Realm
     * @param widgetId widget id
     * @param memoId   memo id
     */
    public static void saveWidget ( Realm realm , final int widgetId , final int memoId ) {
        realm.executeTransactionAsync(r -> {
            Memo memo = r.where(Memo.class).equalTo("id" , memoId).findFirst();
            Widget widget = r.where(Widget.class).equalTo("widgetId" , widgetId).findFirst();

            if ( widget != null ) { //이미 등록된 위젯인 경우.
                widget.setMemo(memo);
                r.insertOrUpdate(widget);
            } else { //새로운 위젯을 등록하는 경우
                Number maxId = r.where(Widget.class).max("id");
                int id = ( maxId != null ) ? maxId.intValue() + 1 : 1;

                Widget createdWidget = r.createObject(Widget.class , id);
                createdWidget.setWidgetId(widgetId);
                createdWidget.setMemo(memo);
                r.insertOrUpdate(createdWidget);
            }
        });
    }

    /**
     * delete widget
     *
     * @param realm Realm
     * @param widgetId    id
     */
    public static void deleteWidgetAsync ( Realm realm , final int widgetId ) {
        realm.executeTransactionAsync(r -> {
            Widget widget = r.where(Widget.class).equalTo("widgetId" , widgetId).findFirst();
            if ( widget != null ) {
                widget.deleteFromRealm();
            }
        });
    }

}
