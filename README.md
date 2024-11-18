# LibQiNiu
 七牛上传的引用

#### QiNiuFactory
七牛云上传工厂类，用于创建七牛云上传对象
* getInstance 获取七牛云上传对象
* uploadFile 上传文件
* uploadImage 上传图片

1. AndroidManifest.xml
```
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```
```
    android:networkSecurityConfig="@xml/network_security_config"
```
2. res/xml/network_security_config.xml
```
    <?xml version="1.0" encoding="utf-8"?>
    <network-security-config>
        <base-config cleartextTrafficPermitted="true" />
    </network-security-config>
```
3. build.gradle
```
    implementation 'com.google.code.gson:gson:2.8.5'
    implementation 'com.squareup.okhttp3:okhttp:4.2.2'
```
