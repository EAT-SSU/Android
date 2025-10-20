package com.eatssu.android.presentation.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.content.IntentCompat.getParcelableExtra
import kotlin.reflect.KClass

private const val INTENT_ARGS_KEY = "intent_args"

/**
 * ActivityCompanion 패턴: Extra 정보가 필요한 Intent를 type-safe 하게 생성하고 한 가지 공통된 방법으로 Activity를 실행하기 위해 사용합니다.
 *
 * @see ActivityCompanion 인자가 없는 기본 타입
 * @see ActivityCompanionWithArgs 인자가 필요한 타입
 * @see ActivityCompanionWithArgsDefault 기본 인자를 제공하는 타입
 */

/**
 * 인자가 없는 Activity를 위한 Companion 클래스
 *
 * ## 사용 예시
 * ```kotlin
 * class MainActivity : AppCompatActivity() {
 *     companion object : ActivityCompanion(MainActivity::class)
 * }
 *
 * // 다른 곳에서 사용
 * MainActivity.start(context)
 * val intent = MainActivity.intent(context)
 * ```
 *
 * @property activityClass 실행할 Activity의 KClass
 */
abstract class ActivityCompanion(
    protected val activityClass: KClass<out Activity>,
) {
    /**
     * Intent를 생성합니다.
     *
     * @param context Context
     * @param intentBuilder Intent에 추가 설정을 할 수 있는 람다 (예: flags 설정)
     * @return 생성된 Intent
     */
    fun intent(
        context: Context, intentBuilder: Intent.() -> Unit = {}
    ): Intent = Intent(context, activityClass.java).apply {
        intentBuilder()
    }

    /**
     * Activity를 실행합니다.
     *
     * @param context Context
     * @param intentBuilder Intent에 추가 설정을 할 수 있는 람다
     */
    fun start(context: Context, intentBuilder: Intent.() -> Unit = {}) {
        context.startActivity(intent(context, intentBuilder))
    }

}

/**
 * 타입 안전한 인자를 받는 Activity를 위한 Companion 클래스
 * Args 타입은 Activity 내에 선언해 DetailActivity.Args 처럼 사용하는 것을 권장합니다.
 * 필요하다면 intentOptions의 필드를 lazy 프로퍼티로 감싸 사용하세요.
 *
 * ## 사용 예시
 * ```kotlin
 * class DetailActivity : AppCompatActivity() {
 *     @Parcelize
 *     data class Args(val id: Int, val title: String) : Parcelable
 *
 *     companion object : ActivityCompanionWithArgs<Args>(
 *         DetailActivity::class,
 *         Args::class
 *     )
 *
 *     private val args by lazy { intentOptions }
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         val id = args?.id  // type-safe하게 인자 접근
 *     }
 * }
 *
 * // 다른 곳에서 사용
 * DetailActivity.start(context, DetailActivity.Args(id = 1, title = "제목"))
 * ```
 *
 * @property activityClass 실행할 Activity의 KClass
 * @property argsClass 인자로 전달할 Parcelable 데이터 클래스의 KClass
 * @param TArgs Parcelable을 구현한 인자 타입
 */
abstract class ActivityCompanionWithArgs<TArgs>(
    protected val activityClass: KClass<out Activity>,
    protected val argsClass: KClass<TArgs>,
) where TArgs : Parcelable {
    /**
     * 타입 안전한 인자를 포함한 Intent를 생성합니다.
     *
     * @param context Context
     * @param args Activity에 전달할 Parcelable 인자
     * @param intentBuilder Intent에 추가 설정을 할 수 있는 람다
     * @return 인자가 포함된 Intent
     */
    fun intent(
        context: Context, args: TArgs, intentBuilder: Intent.() -> Unit = {}
    ): Intent = Intent(context, activityClass.java).apply {
        putExtra(INTENT_ARGS_KEY, args)
        intentBuilder()
    }

    /**
     * 타입 안전한 인자와 함께 Activity를 실행합니다.
     *
     * @param context Context
     * @param args Activity에 전달할 Parcelable 인자
     * @param intentBuilder Intent에 추가 설정을 할 수 있는 람다
     */
    fun start(context: Context, args: TArgs, intentBuilder: Intent.() -> Unit = {}) {
        context.startActivity(intent(context, args, intentBuilder))
    }

    /**
     * Activity 내부에서 전달받은 인자를 타입 안전하게 꺼내는 확장 프로퍼티
     *
     * 사용 예시:
     * ```kotlin
     * private val postId by lazy { intentOptions?.postId }
     * ```
     */
    val Activity.intentOptions: TArgs?
        get() = getParcelableExtra(
            this.intent, INTENT_ARGS_KEY, argsClass.java
        )
}

/**
 * 커스텀 인자와 기본 인자가 필요한 Activity를 위한 Companion 클래스
 *
 * ActivityCompanionWithArgs를 상속하여 기본 인자로 실행하는 오버로드를 추가로 제공합니다.
 * - `start(context)` - 기본 인자로 실행
 * - `start(context, customArgs)` - 커스텀 인자로 실행
 *
 * ## 사용 예시
 * ```kotlin
 * class SettingsActivity : AppCompatActivity() {
 *     @Parcelize
 *     data class Args(val section: String = "general") : Parcelable
 *
 *     companion object : ActivityCompanionWithArgsDefault<Args>(
 *         SettingsActivity::class,
 *         Args::class,
 *         { Args() }
 *     )
 * }
 *
 * // 기본 인자로 실행: SettingsArgs(section = "general")
 * SettingsActivity.start(context)
 *
 * // 커스텀 인자로 실행: SettingsArgs(section = "privacy")
 * SettingsActivity.start(context, SettingsActivity.Args(section = "privacy"))
 * ```
 *
 * @property defaultArgs Context를 받아 기본 인자를 생성하는 람다
 * @param TArgs Parcelable을 구현한 인자 타입
 */
abstract class ActivityCompanionWithArgsDefault<TArgs>(
    activityClass: KClass<out Activity>,
    argsClass: KClass<TArgs>,
    private val defaultArgs: (Context) -> TArgs,
) : ActivityCompanionWithArgs<TArgs>(activityClass, argsClass) where TArgs : Parcelable {

    /**
     * 기본 인자로 Intent를 생성합니다.
     *
     * @param context Context
     * @param intentBuilder Intent에 추가 설정을 할 수 있는 람다
     * @return 기본 인자가 포함된 Intent
     */
    fun intent(
        context: Context, intentBuilder: Intent.() -> Unit = {}
    ): Intent = Intent(context, activityClass.java).apply {
        putExtra(INTENT_ARGS_KEY, defaultArgs(context))
        intentBuilder()
    }

    /**
     * 기본 인자로 Activity를 실행합니다.
     *
     * @param context Context
     * @param intentBuilder Intent에 추가 설정을 할 수 있는 람다
     */
    fun start(context: Context, intentBuilder: Intent.() -> Unit = {}) {
        context.startActivity(intent(context, intentBuilder))
    }

}
