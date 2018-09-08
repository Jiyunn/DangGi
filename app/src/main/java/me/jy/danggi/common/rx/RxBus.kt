package me.jy.danggi.common.rx

import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class RxBus {

    private var subject = BehaviorSubject.create<Any>()

    companion object {
        @Volatile
        private var INSTANCE: RxBus? = null

        fun getInstance(): RxBus =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: initInstance().also { INSTANCE = it }
                }

        private fun initInstance(): RxBus = RxBus()
    }

    fun takeBus(task: Any) {
        if (subject.hasComplete()) {
            subject = BehaviorSubject.create()
        }
        subject.onNext(task)
    }

    fun toObservable(): Observable<Any> = subject

    fun shutdownBus() {
        subject.onComplete()
    }

}