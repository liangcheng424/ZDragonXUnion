#your dex.loader patterns here
-keep class tinker.sample.android.app.SampleApplication {
    <init>();
}

-keep class com.tencent.tinker.loader.** {
    <init>();
}