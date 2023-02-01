package app.sato.kchan.tasks.fanction

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, databaseName:String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, databaseName, factory, version) {
    override fun onCreate(database: SQLiteDatabase?) {
        database?.execSQL("""
            create table if not exists account (
                account_id TEXT,
                service_id INTEGER,
                password_flag INTEGER NOT NULL DEFAULT 0,
                secret_question1_item TEXT,
                secret_question2_item TEXT,
                secret_question3_item TEXT,
                connect_token TEXT NOT NULL,
                sync_at TEXT NOT NULL
            )
        """)
        database?.execSQL("""
            create table if not exists setting (
                color1 INTEGER NOT NULL,
                color2 INTEGER NOT NULL,
                color3 INTEGER NOT NULL,
                auto_delete_period TEXT,
                init_show_at TEXT,
                init_hide_at TEXT,
                status_flag INTEGER NOT NULL DEFAULT 0,
                color_update_at TEXT NOT NULL,
                auto_delete_update_at TEXT NOT NULL,
                init_show_update_at TEXT NOT NULL,
                init_hide_update_at TEXT NOT NULL,
                status_update_at TEXT NOT NULL
            )
        """)
        database?.execSQL("""
            create table if not exists service (
                service_id INTEGER NOT NULL PRIMARY KEY,
                create_at TEXT NOT NULL,
                service_name TEXT NOT NULL,
                type INTEGER NOT NULL,
                version INTEGER NOT NULL DEFAULT 1,
                status_flag INTEGER NOT NULL DEFAULT 0,
                name_update_at TEXT NOT NULL,
                others_update_at TEXT NOT NULL,
                status_update_at TEXT NOT NULL
            )
        """)
        database?.execSQL("""
            create table if not exists note (
                note_id INTEGER NOT NULL,
                service_id INTEGER NOT NULL,
                create_at TEXT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL DEFAULT '',
                complete_at TEXT,
                lock_at TEXT,
                notice_bar_id INTEGER,
                status_flag INTEGER NOT NULL DEFAULT 0,
                title_update_at TEXT NOT NULL,
                content_update_at TEXT NOT NULL,
                completion_update_at TEXT NOT NULL,
                lock_update_at TEXT NOT NULL,
                status_update_at TEXT NOT NULL,
                PRIMARY KEY(note_id, service_id),
                FOREIGN KEY(service_id) REFERENCES service (service_id)
            )
        """)
        database?.execSQL("""
            create table if not exists place (
                place_id INTEGER NOT NULL,
                service_id INTEGER NOT NULL,
                create_at TEXT NOT NULL,
                name TEXT NOT NULL,
                address TEXT NOT NULL,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                priority INTEGER NOT NULL,
                status_flag INTEGER NOT NULL DEFAULT 0,
                name_update_at TEXT NOT NULL,
                address_update_at TEXT NOT NULL,
                priority_update_at TEXT NOT NULL,
                status_update_at TEXT NOT NULL,
                PRIMARY KEY(place_id, service_id),
                FOREIGN KEY (service_id) REFERENCES service (service_id)
            )
        """)
        database?.execSQL("""
            create table if not exists notice (
                notice_id INTEGER NOT NULL,
                service_id INTEGER NOT NULL,
                create_at TEXT NOT NULL,
                target_note_id INTEGER NOT NULL,
                target_note_service_id INTEGER NOT NULL,
                target_service_id INTEGER NOT NULL,
                show_at TEXT,
                hide_at TEXT,
                place_id INTEGER,
                place_service_id INTEGER,
                status_flag INTEGER NOT NULL DEFAULT 0,
                show_update_at TEXT NOT NULL,
                hide_update_at TEXT NOT NULL,
                place_update_at TEXT NOT NULL,
                status_update_at TEXT NOT NULL,
                PRIMARY KEY(notice_id, service_id),
                FOREIGN KEY (service_id) REFERENCES service (service_id),
                FOREIGN KEY (target_note_id, target_note_service_id) REFERENCES note (note_id, service_id),
                FOREIGN KEY (target_service_id) REFERENCES service (service_id),
                FOREIGN KEY (place_id, place_service_id) REFERENCES place (place_id, service_id)
            )
        """);
    }
    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            database?.execSQL("alter table SampleTable add column deleteFlag integer default 0")
        }
    }
}