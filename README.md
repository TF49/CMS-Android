# CMS-Android

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com/)
[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Room](https://img.shields.io/badge/Room-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/topic/libraries/architecture/room)
[![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white)](https://material.io/)

一个功能完整的社区管理系统Android应用，用于管理社区居民的户籍、居民信息、教育记录和医疗档案。

## 📱 应用截图

*(此处可添加应用截图)*

## ✨ 功能特性

### 🔐 用户认证
- **用户注册与登录** - 安全的身份验证系统
- **权限管理** - 支持管理员和普通用户角色
- **密码重置** - 通过身份证验证的安全密码重置

### 🏠 户籍管理
- **户籍信息管理** - 完整的户籍信息增删改查
- **户主信息维护** - 户主基本信息管理
- **户籍类型分类** - 支持城镇/农村户籍分类

### 👥 居民管理
- **居民信息管理** - 详细的居民个人信息维护
- **户籍关联** - 居民与户籍的关联关系管理
- **健康档案** - 居民健康状况记录

### 📚 教育管理
- **教育记录管理** - 居民教育信息维护
- **学历信息** - 学校、专业、学历等级记录
- **教育状态跟踪** - 在校/毕业状态管理

### 🏥 医疗管理
- **医疗档案管理** - 居民医疗信息记录
- **就诊记录** - 医院、科室、诊断信息
- **健康信息** - 血型、过敏史、慢性病史

## 🛠️ 技术栈

### 核心框架
- **Android SDK 34** - 最新稳定版本
- **Java 1.8** - 兼容性支持
- **Kotlin 1.8.21** - 依赖管理

### 数据库
- **Room Database** - Android官方ORM框架
- **SQLite** - 本地数据库存储
- **数据库版本 7** - 支持多版本迁移

### UI/UX
- **Material Design 3** - 现代化设计语言
- **ConstraintLayout** - 灵活布局管理
- **RecyclerView** - 高效列表显示
- **CardView** - 卡片式布局

### 架构模式
- **MVVM模式** - 数据绑定和生命周期管理
- **Repository模式** - 数据访问层抽象
- **DAO模式** - 数据库操作接口

## 📁 项目结构

```
app/src/main/java/com/example/cms_android/
├── activity/          # 界面控制器
│   ├── LoginActivity.java          # 登录界面
│   ├── MainActivity.java           # 主界面
│   ├── RegisterActivity.java       # 注册界面
│   ├── HouseholdManagementActivity.java    # 户籍管理
│   ├── ResidentManagementActivity.java    # 居民管理
│   ├── EducationManagementActivity.java   # 教育管理
│   └── MedicalManagementActivity.java      # 医疗管理
├── adapter/           # RecyclerView适配器
│   ├── HouseholdAdapter.java       # 户籍适配器
│   ├── ResidentAdapter.java        # 居民适配器
│   ├── EducationAdapter.java       # 教育适配器
│   └── MedicalAdapter.java         # 医疗适配器
├── dao/               # 数据访问对象
│   ├── UserDao.java                # 用户数据操作
│   ├── HouseholdDao.java           # 户籍数据操作
│   ├── ResidentDao.java            # 居民数据操作
│   ├── EducationDao.java           # 教育数据操作
│   └── MedicalDao.java             # 医疗数据操作
├── database/          # 数据库配置
│   └── AppDatabase.java            # 数据库实例
├── model/             # 数据模型
│   ├── User.java                   # 用户模型
│   ├── Household.java              # 户籍模型
│   ├── Resident.java               # 居民模型
│   ├── Education.java              # 教育模型
│   └── Medical.java                # 医疗模型
├── repository/        # 数据仓库
│   ├── EducationRepository.java    # 教育记录仓库
│   ├── EducationRepositoryImpl.java # 教育记录仓库实现
│   ├── HouseholdRepository.java    # 户籍仓库
│   ├── HouseholdRepositoryImpl.java # 户籍仓库实现
│   ├── MedicalRepository.java      # 医疗记录仓库
│   ├── MedicalRepositoryImpl.java   # 医疗记录仓库实现
│   ├── PermissionChecker.java       # 权限检查工具
│   ├── ResidentRepository.java      # 居民信息仓库
│   ├── ResidentRepositoryImpl.java   # 居民信息仓库实现
│   ├── UserRepository.java          # 用户仓库
│   └── UserRepositoryImpl.java       # 用户仓库实现
└── utils/             # 工具类
    ├── PermissionManager.java      # 权限管理工具
    ├── SharedPreferencesManager.java # 本地存储管理
    └── ValidationUtils.java        # 验证工具
```

## 🚀 快速开始

### 环境要求
- **Android Studio** - Arctic Fox 或更高版本
- **Gradle** - 7.0 或更高版本
- **Android SDK** - API 34
- **Java** - 1.8 或更高版本

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/TF49/CMS-Android.git
   cd CMS-Android
   ```

2. **打开项目**
   - 使用 Android Studio 打开项目
   - 等待 Gradle 同步完成

3. **配置环境**
   - 确保已安装 Android SDK 34
   - 配置 Java 1.8 或更高版本

4. **运行应用**
   - 连接 Android 设备或启动模拟器
   - 点击运行按钮或使用快捷键 `Shift + F10`

### 默认账户

系统预置了两个默认账户，用于快速体验不同角色的权限功能：

- **管理员账户**
  - 用户名: `admin`
  - 密码: `admin123`
  - 权限: 可查看和管理所有数据，包括所有用户创建的信息

- **普通用户**
  - 用户名: `user`
  - 密码: `user123`
  - 权限: 仅能查看和管理自己创建的数据

### 注册说明

用户也可以通过注册功能创建新账户：
- 密码支持任意字符组合，至少6位
- 系统中只能存在一个管理员账户，当管理员账户已存在时无法再注册新的管理员账户
- 普通用户账户可以创建多个

## 📊 数据库设计

项目使用 Room Database 管理以下核心数据表：

### 用户表 (users)
- 用户基本信息、权限角色、登录记录

### 户籍表 (households)
- 户籍编号、地址、户主信息、人口数量

### 居民表 (residents)
- 个人信息、健康状况、与户籍关联关系

### 教育表 (education)
- 学校信息、学历等级、入学毕业时间

### 医疗表 (medical)
- 就诊记录、诊断信息、医疗费用

## 🔧 开发指南

### 添加新功能模块

1. **创建数据模型**
   ```java
   @Entity(tableName = "new_table")
   public class NewModel {
       @PrimaryKey(autoGenerate = true)
       private long id;
       // 添加字段...
   }
   ```

2. **创建DAO接口**
   ```java
   @Dao
   public interface NewDao {
       @Insert
       long insert(NewModel model);
       // 添加其他操作...
   }
   ```

3. **更新数据库配置**
   ```java
   @Database(entities = {..., NewModel.class}, version = 7)
   public abstract class AppDatabase extends RoomDatabase {
       public abstract NewDao newDao();
   }
   ```

### 自定义主题

修改 `res/values/colors.xml` 和 `res/values/themes.xml` 来自定义应用主题。

## 🤝 贡献指南

我们欢迎任何形式的贡献！请遵循以下步骤：

1. Fork 本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

- 项目主页: [CMS-Android](https://github.com/TF49/CMS-Android)
- 问题反馈: [Issues](https://github.com/TF49/CMS-Android/issues)
- 邮箱: your-email@example.com

## 🙏 致谢

感谢以下开源项目的支持：
- [Android Jetpack](https://developer.android.com/jetpack)
- [Material Design](https://material.io/)
- [Room Persistence Library](https://developer.android.com/topic/libraries/architecture/room)

---

⭐ 如果这个项目对你有帮助，请给个 Star！