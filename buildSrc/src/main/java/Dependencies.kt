
object ProjectLevelDependencies{
    val navigation_safe_arg_dep =   "androidx.navigation:navigation-safe-args-gradle-plugin:${Dependencies.Versions.nav_version}"

}
object Dependencies {


    val coreKtx_dep =  "androidx.core:core-ktx:${Versions.coreKtx_version}"
    val appCompate_dep = "androidx.appcompat:appcompat:${Versions.appCompat_version}"
    val material_dep  =  "com.google.android.material:material:${Versions.android_material_version}"
    val constraint_layout_dep =   "androidx.constraintlayout:constraintlayout:${Versions.constraint_layout_version}"


    //Retrofit
    val retrofit_dep = "com.squareup.retrofit2:retrofit:${Versions.retrofit_version}"
    val retrofit_gson_converter_dep =  "com.squareup.retrofit2:converter-gson:${Versions.retrofit_version}"
    val okhttp_dep = "com.squareup.okhttp3:okhttp:${Versions.okhttp_version}"
    val logging_interceptor_dep =  "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp_version}"
    val retrofit_converter_scaler_dep =  "com.squareup.retrofit2:converter-scalars:${Versions.retrofit_converter_scaler_version}"
    val kotlin_coroutine_dep =  "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.ktolin_coroutine_android_version}"

    // navigaiton
    val navigation_fragment_ktx_dep  = "androidx.navigation:navigation-fragment-ktx:${Versions.nav_version}"
    val navigation_ui_ktx_dep =  "androidx.navigation:navigation-ui-ktx:${Versions.nav_version}"
    val glide_dep =   "com.github.bumptech.glide:glide:${Versions.glide_version}"

    val dagger_hilt_dep =  "com.google.dagger:hilt-android:${Versions.dagger_hilt_version}"
    val dagger_hilt_kapt =  "com.google.dagger:hilt-compiler:${Versions.dagger_hilt_version}"
    val gson_dep =  "com.google.code.gson:gson:${Versions.gson_version}"
    val datastore_preference_dep = "androidx.datastore:datastore-preferences:${Versions.datastore_preference_version}"
    val viewpager2_dep =   "androidx.viewpager2:viewpager2:${Versions.viewpager2_version}"



    val room_dep = "androidx.room:room-runtime:${Versions.room_version}"
    val room_annotation_dep = "androidx.room:room-compiler:${Versions.room_version}"
    val room_kapt_dep = "androidx.room:room-compiler:${Versions.room_version}"
    val room_ktx_dep = "androidx.room:room-ktx:${Versions.room_version}"



    object Versions{
        val coreKtx_version = "1.10.1"
        val appCompat_version = "1.6.1"
        val android_material_version = "1.9.0"
        val retrofit_version = "2.9.0"
        val okhttp_version = "4.9.0"
        val retrofit_converter_scaler_version = "2.4.0"
        val ktolin_coroutine_android_version = "1.6.2"
        val constraint_layout_version = "2.1.4"
        val nav_version = "2.5.3"
        val glide_version = "4.15.1"
        val dagger_hilt_version = "2.44"
        val gson_version = "2.9.0"
        val datastore_preference_version = "1.0.0"
        val viewpager2_version  = "1.0.0"
        val room_version = "2.5.1"

    }
}