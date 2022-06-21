
```
buildFeatures {
        //databinding开启
        dataBinding = true
}
```

[![](https://jitpack.io/v/Larissa-x/LoggerTools.svg)](https://jitpack.io/#Larissa-x/LoggerTools)

### gradle集成方式

```
    maven { url 'https://maven.aliyun.com/repository/public/' }
    maven { url "https://maven.aliyun.com/nexus/content/repositories/releases" }
    maven { url 'https://www.jitpack.io' }
    maven { url 'https://repo1.maven.org/maven2/' }
    maven { url 'https://jitpack.io' }
```

```
    dependencies {
        implementation 'com.github.Larissa-x:LoggerTools:v1.0.1'
    }
```
    ### 二选一
```
    一、Application继承BaseApplication
    二、DatabaseManager.saveApplication(this)
```

    ### OkHttp网络请求请求日志拦截器
```
LoggerInterceptor()
```


[![](https://jitpack.io/v/Larissa-x/LoggerTools.svg)](https://jitpack.io/#Larissa-x/LoggerTools)

