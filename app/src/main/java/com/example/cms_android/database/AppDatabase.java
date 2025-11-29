package com.example.cms_android.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.cms_android.dao.EducationDao;
import com.example.cms_android.dao.HouseholdDao;
import com.example.cms_android.dao.MedicalDao;
import com.example.cms_android.dao.ResidentDao;
import com.example.cms_android.dao.UserDao;
import com.example.cms_android.model.Education;
import com.example.cms_android.model.Household;
import com.example.cms_android.model.Medical;
import com.example.cms_android.model.Resident;
import com.example.cms_android.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Database(
        entities = {Household.class, Resident.class, Education.class, Medical.class, User.class},
        version = 6
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
    
    // 数据库迁移：从版本3到版本4，添加居民表的ownerId字段
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 为居民表添加ownerId字段
            database.execSQL("ALTER TABLE residents ADD COLUMN ownerId INTEGER NOT NULL DEFAULT 1");
        }
    };

    // 数据库迁移：从版本4到版本5，修改居民表ownerId默认值为适应现有数据
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 由于之前添加的ownerId默认值可能有问题，这里不做特殊处理
            // Room会处理列的兼容性
        }
    };

    // 数据库迁移：从版本5到版本6，确保用户表结构完整
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 确保用户表包含所有必要的字段
            try {
                database.execSQL("ALTER TABLE users ADD COLUMN idCard TEXT");
            } catch (Exception e) {
                // 字段可能已经存在，忽略错误
            }
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
                        
                        @Override
                        public void onOpen(@androidx.annotation.NonNull SupportSQLiteDatabase db) {
                            super.onOpen(db);
                            // 数据库打开时检查是否存在默认用户，如果没有则创建
                            createDefaultUsersIfNotExists(db);
                        }
                    })
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * 检查并创建默认用户（如果不存在）
     */
    private static void createDefaultUsersIfNotExists(SupportSQLiteDatabase db) {
        // 检查是否已存在管理员用户
        boolean adminExists = false;
        boolean userExists = false;
        
        // 注意：在onOpen回调中执行查询可能会有问题，所以我们使用简单的插入方式
        // 使用 INSERT OR IGNORE 来避免重复插入
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        
        String insertAdminSQL = "INSERT OR IGNORE INTO users (id, username, password, email, role, phone, idCard, createTime, isActive) " +
            "VALUES (1, 'admin', 'admin123', 'admin@cms.com', 'admin', '13800138000', '110101199001011234', '" + currentTime + "', 1)";
        db.execSQL(insertAdminSQL);
        
        String insertUserSQL = "INSERT OR IGNORE INTO users (id, username, password, email, role, phone, idCard, createTime, isActive) " +
            "VALUES (2, 'user', 'user123', 'user@cms.com', 'user', '13900139000', '110101199001011235', '" + currentTime + "', 1)";
        db.execSQL(insertUserSQL);
    }
}