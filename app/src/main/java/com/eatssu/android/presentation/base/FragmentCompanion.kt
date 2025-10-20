package com.eatssu.android.presentation.base

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

private const val FRAGMENT_ARGS_KEY = "fragment_args"

/**
 * FragmentCompanion 패턴: Arguments 정보가 필요한 Fragment를 type-safe 하게 생성하고 한 가지 공통된 방법으로 인스턴스를 만들기 위해 사용합니다.
 *
 * @see FragmentCompanion 인자가 없는 기본 타입
 * @see FragmentCompanionWithArgs 인자가 필요한 타입
 */

/**
 * 인자가 없는 Fragment를 위한 Companion 클래스
 *
 * ## 사용 예시
 * ```kotlin
 * class HomeFragment : Fragment() {
 *     companion object : FragmentCompanion(::HomeFragment)
 * }
 *
 * // 다른 곳에서 사용
 * val fragment = HomeFragment.newInstance()
 * ```
 *
 * @property fragmentBuilder Fragment 인스턴스를 생성하는 람다
 */
abstract class FragmentCompanion(
    private val fragmentBuilder: () -> Fragment,
) {
    fun newInstance(): Fragment {
        return fragmentBuilder()
    }
}

/**
 * 타입 안전한 인자를 받는 Fragment를 위한 Companion 클래스
 * Args 타입은 Fragment 내에 선언해 DetailFragment.Args 처럼 사용하는 것을 권장합니다.
 * 필요하다면 fragmentOptions의 필드를 lazy 프로퍼티로 감싸 사용하세요.
 *
 * ## 사용 예시
 * ```kotlin
 * class DetailFragment : Fragment() {
 *     @Parcelize
 *     data class Args(val id: Int, val title: String) : Parcelable
 *
 *     companion object : FragmentCompanionWithArgs<Args>(
 *         ::DetailFragment,
 *         Args::class
 *     )
 *
 *     private val args by lazy { fragmentOptions }
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *         val id = args?.id  // type-safe하게 인자 접근
 *     }
 * }
 *
 * // 다른 곳에서 사용
 * val fragment = DetailFragment.newInstance(DetailFragment.Args(id = 1, title = "제목"))
 * ```
 *
 * @property fragmentBuilder Fragment 인스턴스를 생성하는 람다
 * @property argsClass 인자로 전달할 Parcelable 데이터 클래스의 KClass
 * @param TArgs Parcelable을 구현한 인자 타입
 */
abstract class FragmentCompanionWithArgs<TArgs>(
    private val fragmentBuilder: () -> Fragment,
    private val argsClass: KClass<TArgs>
) where TArgs : Parcelable {

    fun newInstance(args: TArgs): Fragment {
        return fragmentBuilder().apply {
            arguments = Bundle().apply {
                putParcelable(FRAGMENT_ARGS_KEY, args)
            }
        }
    }

    /**
     * Fragment 내부에서 전달받은 인자를 타입 안전하게 꺼내는 확장 프로퍼티
     *
     * 사용 예시:
     * ```kotlin
     * private val postId by lazy { fragmentOptions?.postId }
     * ```
     */
    val Fragment.fragmentOptions: TArgs?
        get() = arguments?.let {
            BundleCompat.getParcelable(it, FRAGMENT_ARGS_KEY, argsClass.java)
        }
}
