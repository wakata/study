# Android + KotlinでC++のソースコードの呼び出しをやってみた

## 目次

* [使用環境](#使用環境)
* [ドキュメント](#ドキュメント)
* [セットアップ](#セットアップ)
* [ビルド](#ビルド)
* [メソッド追加](#メソッド追加)
* [所感](#sy間)

### 使用環境

* Windows 10 home
* AndroidStudio
* Xperia XZ(SO-01J):Android 8.00

### ドキュメント

諸々のセットアップやサンプル実装は下記サイトを参考にしました。

[プロジェクトへの C / C++ コードの追加](https://developer.android.com/studio/projects/add-native-code?hl=ja)

### セットアップ

下記を参考にビルドに必要なコンポーネントをダウンロードします。

[NDK とビルドツールのダウンロード](https://developer.android.com/studio/projects/add-native-code?hl=ja#download-ndk)

* NDK
* CMake
* LLDB

上記コンポーネントは、[SDK Manager](https://developer.android.com/studio/intro/update.html?hl=ja#sdk-manager) を使用してインストールできます。 
開いているプロジェクトで、メニューバーの [Tools] > [Android] > [SDK Manager] を選択します。 
[SDK Tools] タブをクリックします。 
下記図のように [LLDB]、[CMake]、[NDK] の横のボックスをオンにします

![SDK Manager での LLDB、CMake、NDK のインストール](https://developer.android.com/studio/images/projects/ndk-install_2-2_2x.png)

### ビルド

今回は「既存のプロジェクトに対しC++コードを追加する方法」を試してみました。
主なやり方は[既存のプロジェクトへの C / C++ コードの追加](https://developer.android.com/studio/projects/add-native-code?hl=ja#existing-project)
を参考にし下記を行いました。

1. 新しいネイティブ ソース ファイルを作成し、そのファイルを Android Studio プロジェクトに追加 
2. CMake ビルド スクリプトを作成し、ネイティブ ソース コードをライブラリにビルド
3. CMakeをファイルパスを指定して、Gradle をネイティブ ライブラリにリンク
4. Kotolin側でネイティブ ソースファイルの参照を定義し、呼び出しを行う

#### 1. 新しいネイティブ ソース ファイルを作成し、そのファイルを Android Studio プロジェクトに追加

[新しいネイティブ ソース ファイルの作成](https://developer.android.com/studio/projects/add-native-code?hl=ja#create-sources)を参考に下記の図のようにnative-lib.cppを配置します。

![native-lib.cppの配置](https://github.com/wakata/study/blob/master/Andoid/kotlin/jni/cpp_dir.png)

##### 配置の仕方

1. IDE の左側にある [Project] ペインを開き、プルダウン メニューの [Project] ビューを選択します。 
2. [your-module] > [src] に移動し、main ディレクトリを右クリックして [New] > [Directory] を選択します。 
3. ディレクトリの名前（cpp など）を入力し、[OK] をクリックします。 
4. 作成したディレクトリを右クリックして、[New] > [C/C++ Source File] を選択します。 
5. ソースファイルの名前（native-lib など）を入力します。 
6. [Type] プルダウン メニューで、ソースファイルのファイル拡張子（.cpp など）を選択します。  
7. [OK] をクリックします。 

native-lib.cpp内の実装は下記のとおりです。

###### native-lib.cpp

```
#include <string.h>
#include "jni.h"

extern "C" JNIEXPORT jstring
JNICALL Java_com_example_kotlin_myapplication_MainActivity_StringForJNI(JNIEnv* env, jobject thiz ) {
    return env->NewStringUTF("Hello JNI !");
}
```

#### 2. CMake ビルド スクリプトを作成し、ネイティブ ソース コードをライブラリにビルド

[CMake ビルド スクリプトの作成](https://developer.android.com/studio/projects/add-native-code?hl=ja#create-cmake-script)を参考に下記の図のようにCMakeLists.txtを配置します。

![CMakeLists.txtの配置](https://github.com/wakata/study/blob/master/Andoid/kotlin/jni/cpp_dir.png)

##### 配置の仕方

1. IDE の左側にある [Project] ペインを開き、プルダウン メニューの [Project] ビューを選択します。 
2. your-module のルート ディレクトリを右クリックして、[New] > [File] を選択します。 
注: ビルド スクリプトは任意の場所に作成できます。ただし、ビルド スクリプトを設定するときは、ネイティブ ソース ファイルとライブラリのパスにはビルド スクリプトの場所に対する相対パスを指定してください。 
3. ファイル名に "CMakeLists.txt" と入力し、[OK] をクリックします。

CMakeLists.txt内の実装は下記のとおりです。

###### CMakeLists.txt

```
# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

add_library( # Specifies the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp)
```

#### 3. CMakeをファイルパス指定して、Gradle をネイティブ ライブラリにリンク

build.gradle ファイルにexternalNativeBuildを追加します。

##### CMakeLists.txt

```
apply plugin: 'com.android.application'

apply plugin: 'kotlin-android'

apply plugin: 'kotlin-android-extensions'

android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "com.example.kotlin.myapplication"
        minSdkVersion 26
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    // Encapsulates your external native build configurations.
    externalNativeBuild {
        // Encapsulates your CMake build configurations.
        cmake {
            // Provides a relative path to your CMake build script.
            path "CMakeLists.txt"
        }
    }
}
```

#### 4. Kotolin側でネイティブ ソースファイルの参照を定義し、呼び出しを行う

前述の3.までビルドはできるようになっているはずですが、これだけではネイティブソース側の処理は実行されません。
そのためKotolin側でネイティブソースを実行する必要があるので下記を行います。

1. ネイティブソース「native-lib.cpp」のライブラリを読み込む
2. ネイティブソース内のメソッドを呼び出すためのメソッド定義
3. 上記2.で定義したメソッド定義を呼び出し

以下がサンプルコードです。

##### MainActivity.kt

``` kt
class MainActivity : AppCompatActivity() {
    // ネイティブソース「Java_com_example_kotlin_myapplication_MainActivity_StringForJNI()」を呼び出すためのメソッド定義
    private external fun StringForJNI(): String;

    companion object {
        init {
            // ネイティブソース「native-lib.cpp」のライブラリを読み込む
            System.loadLibrary("native-lib")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view = findViewById<TextView>(R.id.text)
        // ネイティブソース「Java_com_example_kotlin_myapplication_MainActivity_StringForJNI」を呼び出し、TextViewに戻り値を設定
        view.setText(StringForJNI())
    }
}
```

##### 実行結果

![実行結果](https://github.com/wakata/study/blob/master/Andoid/kotlin/jni/Screenshot_20190811-165355.png)

### メソッド追加

前章でビルドし実機上でも正しく動作することを確認できたので、下記の処理を行うメソッドを追加してみました。

* native-lib.cpp側で受け取った「int型の特定の数字を2つを足した値」をMainActivity.ktに返却するメソッド

基本的なやり方は前章までと変わらず、下記を行うことでできました。

* C++側にメソッドを追加。
* Kotlin側にC++側を呼び出すための定義及び、呼び出す処理を追加。

##### native-lib.cpp

``` cpp
extern "C" JNIEXPORT jint
JNICALL Java_com_example_kotlin_myapplication_MainActivity_SumForJNI(JNIEnv* env, jobject thiz , jint i, jint j) {
    return i + j;
}
```

##### MainActivity.kt

``` kt
class MainActivity : AppCompatActivity() {
    private external fun SumForJNI(i : Int, j : Int): Int;


    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val str = "1 + 2 = " + SumForJNI(1, 2)
        val view = findViewById<TextView>(R.id.text)
        view.setText(str)
    }
}
```

##### 実行結果
![実行結果](https://github.com/wakata/study/blob/master/Andoid/kotlin/jni/Screenshot_20190811-162427.png)

### 所感

基本的には下記の内容で「Android + KotlinでC++のソースコードの呼び出し」はでたのですが、
参照の定義の仕方や各ファイルについての説明等の情報がバラバラなので理解するのには時間がかかりました・・・。

[プロジェクトへの C / C++ コードの追加](https://developer.android.com/studio/projects/add-native-code?hl=ja)

ただ1度わかってしまえばすんなり出来そうですね。
