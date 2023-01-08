//package app.sato.kchan.tasks.fanction;
//
//private class DBHelper(context: Context, databaseName:String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, databaseName, factory, version) {
//
//        override fun onCreate(database: SQLiteDatabase?) {
//        database?.execSQL("create table if not exists service (
//        service_id INTEGER NOT NULL PRIMARY KEY,
//        create_at TEXT NOT NULL,
//        service_name TEXT NOT NULL,
//        type INTEGER NOT NULL,
//        version INTEGER NOT NULL DEFAULT 1,
//        notification_token TEXT,
//        connect_token TEXT NOT NULL,
//        synchronize_at TEXT NOT NULL,
//        status_flag INTEGER NOT NULL DEFAULT 0,
//        name_update_at TEXT NOT NULL,
//        others_update_at TEXT NOT NULL,
//        status_update_at TEXT NOT NULL,
//        )"
//        );
//        database?.execSQL("create table if not exists note (
//        note_id INTEGER NOT NULL,
//        service_id INTEGER NOT NULL,
//        create_at TEXT NOT NULL,
//        title TEXT NOT NULL,
//        content TEXT NOT NULL DEFAULT '',
//        complete_at TEXT
//        status_flag INTEGER NOT NULL DEFAULT 0,
//        title_update_at TEXT NOT NULL,
//        content_update_at TEXT NOT NULL,
//        show_update_at TEXT NOT NULL,
//        hide_update_at TEXT NOT NULL,
//        completion_update_at TEXT NOT NULL,
//        status_update_at TEXT NOT NULL,
//        PRIMARY KEY(note_id, service_id),
//        FOREIGN KEY(service_id) REFERENCES service(service_id)
//        )"
//        );
//        database?.execSQL("create table if not exists place (
//        place_id INTEGER NOT NULL,
//        service_id INTEGER NOT NULL,
//        create_at TEXT NOT NULL,
//        name TEXT NOT NULL,
//        address TEXT NOT NULL,
//        priority INTEGER NOT NULL DEFAULT 0,
//        status_flag INTEGER NOT NULL DEFAULT 0,
//        name_update_at TEXT NOT NULL,
//        address_update_at TEXT NOT NULL,
//        priority_update_at TEXT NOT NULL,
//        status_update_at TEXT NOT NULL,
//        PRIMARY KEY(place_id, service_id),
//        FOREIGN KEY (service_id) REFERENCES service
//        )"
//        );
//        database?.execSQL("create table if not exists notice (
//        notice_id INTEGER NOT NULL,
//        service_id INTEGER NOT NULL,
//        note_id INTEGER NOT NULL,
//        note_service_id INTEGER NOT NULL,
//        notice_service_id INTEGER NOT NULL,
//        show_at TEXT,
//        hide_at TEXT,
//        place_id INTEGER,
//        place_service_id INTEGER,
//        status_flag INTEGER NOT NULL DEFAULT 0,
//        others_update_at TEXT NOT NULL,
//        show_update_at TEXT NOT NULL,
//        hide_update_at TEXT NOT NULL,
//        place_update_at TEXT NOT NULL,
//        status_update_at TEXT NOT NULL,
//        PRIMARY KEY(note_id, service_id),
//        FOREIGN KEY (service_id, note_service_id, notice_service_id, place_service_id) REFERENCES service,
//        FOREIGN KEY (place_id) REFERENCES place,
//        FOREIGN KEY (note_id) REFERENCES note
//        )"
//        );
//        }
//        }
//
//
//
//
//        fun insertQuery(table, value) {
//        val dbHelper = DBHelper(applicationContext, "DB", null, 1);
//        val database = dbHelper.writableDatabase
//
//        val values = ContentValues()
//        for ((k, v) in value) {
//        values.put($k "," $v)
//        }
//
//        database.insert(table, null, values)
//        }
//
//        fun updateQuery(table, value, pick, filter) {
//        val dbHelper = DBHelper(applicationContext, "DB", null, 1);
//        val database = dbHelper.writableDatabase
//
//        val vl = ContentValues()
//        var text = ""
//        var c = 0
//
//        for (fil in filter) {
//        if(c > 0)
//        text = text + " AND "
//        text = text + fil["column"]
//        if(fil["compare"] == "Big")
//        text = text + " > "
//        if(fil["compare"] == "Small")
//        text = text + " < "
//        if(fil["compare"] == "Equal")
//        text = text + " = "
//        text = text + "?"
//        vl.put(fil["value"])
//        c++
//        }
//
//        for ((k, v) in pick) {
//        if(c > 0)
//        text = text + " AND "
//        text = text + k + " = ?"
//        vl.put(v)
//        c++
//        }
//
//        database.update(table, values, text, vl)
//        }
//
//        fun selectQuery(table, value, pick, filter) {
//        val dbHelper = DBHelper(applicationContext, "DB", null, 1);
//        val database = dbHelper.writableDatabase
//
//        val vl = ContentValues()
//        var text = ""
//        var c = 0
//
//        for (fil in filter) {
//        if(c > 0)
//        text = text + " AND "
//        text = text + fil["column"]
//        if(fil["compare"] == "Big")
//        text = text + " > "
//        if(fil["compare"] == "Small")
//        text = text + " < "
//        if(fil["compare"] == "Equal")
//        text = text + " = "
//        text = text + "?"
//        vl.put(fil["value"])
//        c++
//        }
//
//        for ((k, v) in pick) {
//        if(c > 0)
//        text = text + " AND "
//        text = text + k + " = ?"
//        vl.put(v)
//        c++
//        }
//
//        database.update(table, values, text, vl)
//        }
