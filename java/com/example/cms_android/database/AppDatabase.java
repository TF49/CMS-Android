package com.example.cms_android.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

import com.example.cms_android.dao.HouseholdDao;
import com.example.cms_android.dao.ResidentDao;
import com.example.cms_android.dao.EducationDao;
import com.example.cms_android.dao.MedicalDao;
import com.example.cms_android.dao.UserDao;
import com.example.cms_android.model.Household;
import com.example.cms_android.model.Resident;
import com.example.cms_android.model.Education;
import com.example.cms_android.model.Medical;
import com.example.cms_android.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Database(
    entities = {Household.class, Resident.class, Education.class, Medical.class, User.class},
    version = 6,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HouseholdDao householdDao();
    public abstract ResidentDao residentDao();
    public abstract EducationDao educationDao();
    public abstract MedicalDao medicalDao();
    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;

    // 数据库迁移：从版本1到版本2，添加User表
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 创建User表
            database.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "username TEXT, " +
                "password TEXT, " +
                "email TEXT, " +
                "role TEXT, " +
                "phone TEXT, " +
                "createTime TEXT, " +
                "lastLoginTime TEXT, " +
                "isActive INTEGER NOT NULL DEFAULT 1)");
        }
    };
    
    // 数据库迁移：从版本2到版本3，添加身份证字段
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 添加身份证字段
            database.execSQL("ALTER TABLE users ADD COLUMN idCard TEXT");
        }
    };

    // 数据库迁移：从版本3到版本4，为Education表添加status和notes字段
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 为Education表添加status字段
            database.execSQL("ALTER TABLE education ADD COLUMN status TEXT");
            // 为Education表添加notes字段
            database.execSQL("ALTER TABLE education ADD COLUMN notes TEXT");
        }
    };

    // 数据库迁移：从版本4到版本5，为Medical表添加新的字段
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 为Medical表添加新字段
            database.execSQL("ALTER TABLE medical ADD COLUMN hospital TEXT");
            database.execSQL("ALTER TABLE medical ADD COLUMN department TEXT");
            database.execSQL("ALTER TABLE medical ADD COLUMN diagnosis TEXT");
            database.execSQL("ALTER TABLE medical ADD COLUMN treatment TEXT");
            database.execSQL("ALTER TABLE medical ADD COLUMN doctor TEXT");
            database.execSQL("ALTER TABLE medical ADD COLUMN cost REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE medical ADD COLUMN insurance REAL NOT NULL DEFAULT 0");
        }
    };

    // 数据库迁移：从版本5到版本6，为Resident表添加notes字段
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 为Resident表添加notes字段
            database.execSQL("ALTER TABLE residents ADD COLUMN notes TEXT");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "census_database"
                    )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    // 添加这行以启用数据库检查
                    .allowMainThreadQueries() // 仅用于调试，生产环境中应避免在主线程执行数据库操作
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@androidx.annotation.NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // 数据库创建时预填充默认管理员用户
                            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    .format(new Date());
                            
                            String insertAdminSQL = "INSERT INTO users (username, password, email, role, phone, idCard, createTime, isActive) " +
                                "VALUES ('admin', 'admin123', 'admin@cms.com', 'admin', '13800138000', '110101199001011234', '" + currentTime + "', 1)";
                            db.execSQL(insertAdminSQL);
                            
                            String insertUserSQL = "INSERT INTO users (username, password, email, role, phone, idCard, createTime, isActive) " +
                                "VALUES ('user', 'user123', 'user@cms.com', 'user', '13900139000', '110101199001011235', '" + currentTime + "', 1)";
                            db.execSQL(insertUserSQL);
                        }
                    })
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}