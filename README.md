# very-simple-android-music-player
一个非常非常简单的音乐播放器，通过 `Intent` 调用系统音乐播放器实现而非通过 `Service` 播放音乐。

也是一个关于 Android `Intent` 和 `ListView` 控件的小练习，也是移动平台开发课程的第二次作业~

使用的开发环境及最小 SDK 如下：

- Android Studio 3.6.1
- Genymotion 模拟器（Version 3.0.4，Google Nexus 6，Android 8.0）
- minSdkVersion 14（支持最低 Android 版本 4.0）

## 已实现的功能

- [x] 扫描并列出指定路径下的所有 mp3 或 m4a 音乐文件，并显示到 `ListView` 中
- [x] 自定义的可自由选择要显示音乐文件的路径的界面
- [x] 仿系统设置菜单的设置界面
- [x] 通过 `Intent` 调用系统音乐播放器播放相应的音乐文件，并兼容 Android 系统高低版本
- [x] 申请SD卡访问权限，方式兼容 Android 系统高低版本
- [x] 通过文件名搜索该路径下的音乐文件，不区分大小写
- [x] 查看某一个显示在 `ListView` 中的音乐文件的详细信息（修改时间、大小、标题、音乐人、专辑名、专辑图片、时长等）
- [x] 删除某一个显示在 `ListView` 中的音乐文件
- [x] 程序稳定性和兼容性相比上次作业（repo：`android-ui-design-homework`）有所提升

## 其他事项

由于程序播放音乐的实现依赖系统内置的音乐播放器，因此若该定制 Android 系统未安装播放器（或者已卸载）则不能播放音乐。