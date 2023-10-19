package ng.ojibe.storeme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBGuy extends SQLiteOpenHelper {
    final static String db_name = "store_db";
    final static int db_version = 1;
    Context ctx;
    public DBGuy(Context context) {
        super(context, db_name, null, db_version);
        ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String STUDENT_TABLE = "CREATE TABLE student (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "reg_no TEXT," +
                "name TEXT," +
                "dept TEXT," +
                "age INTEGER," +
                "level INTEGER)";
        db.execSQL(STUDENT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS student");
        onCreate(db);
    }

    public void addStudent(Student student){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("reg_no", student.getRegNo());
        values.put("name", student.getName());
        values.put("dept", student.getDept());
        values.put("age", student.getAge());
        values.put("level", student.getLevel());
        db.insert("student", null, values);
        db.close();
    }

    public Student getStudent(String regNo){
        Student student = new Student();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM student WHERE reg_no = ?";
        Cursor cursor = db.rawQuery(query, new String[]{regNo});
        if (cursor.moveToNext()){
            student = new Student(cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5));

        }
        db.close();
        return student;
    }
}
