###gradle集成方式

    ext.hilt_version = '2.38.1'

    classpath "com.google.dagger:hilt-android-gradle-plugin:$hilt_version"

    kapt 'com.google.dagger:hilt-android-compiler:${version["hilt_version"]}'

    implementation 'com.google.dagger:hilt-android:${version["hilt_version"]}'


    Application继承BaseApplication

    并使用    @HiltAndroidApp注解


