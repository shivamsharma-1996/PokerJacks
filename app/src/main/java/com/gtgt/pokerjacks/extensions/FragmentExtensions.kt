package com.gtgt.pokerjacks.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun AppCompatActivity.replaceFragmentIfNoFragment(
    fragment: Fragment,
    frameId: Int,
    tag: String = fragment.javaClass.simpleName
) {
    if (supportFragmentManager.fragments.isEmpty()) {
        supportFragmentManager.inTransaction {
            val oldFragment = supportFragmentManager.findFragmentByTag(tag)
            supportFragmentManager.fragments.forEach {
                if (it != fragment && it.tag != "force") hide(
                    it
                )
            }
            if (oldFragment == null) {
                add(frameId, fragment, tag)
            } else {
                if (fragment.isAdded)
                    show(fragment)
                else
                    add(frameId, fragment, tag)
            }
        }
    }
}

fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    frameId: Int,
    force: Boolean = false,
    tag: String = fragment.javaClass.simpleName
) {
    if (force) {
        supportFragmentManager.inTransaction { replace(frameId, fragment, "force") }
    } else {
        supportFragmentManager.inTransaction {
            try {
                val oldFragment = supportFragmentManager.findFragmentByTag(tag)
                supportFragmentManager.fragments.forEach {
                    if (it != fragment && it.tag != "force") hide(
                        it
                    )
                }
                if (oldFragment == null) {
                    add(frameId, fragment, tag)
                } else {
                    if (fragment.isAdded)
                        show(fragment)
                    else
                        add(frameId, fragment, tag)
                }
            } catch (ex: java.lang.IllegalStateException) {
                show(fragment)
            }
        }
    }
}

fun Fragment.clearFragments() {
    childFragmentManager.fragments.forEach {
        childFragmentManager.inTransaction { remove(it) }
        childFragmentManager.popBackStackImmediate()
    }

//    log("childFragments", childFragmentManager.fragments.size.toString())

//    childFragmentManager.popBackStackImmediate()

}

fun Fragment.replaceChildFragment(
    fragment: Fragment,
    frameId: Int,
    force: Boolean = false,
    tag: String = fragment.javaClass.simpleName
) {
    if (force) {
        childFragmentManager.inTransaction { replace(frameId, fragment, "force") }
    } else {
        childFragmentManager.inTransaction {
            val oldFragment = childFragmentManager.findFragmentByTag(tag)
            childFragmentManager.fragments.forEach {
                if (it != fragment && it.tag != "force") hide(
                    it
                )
            }
            if (oldFragment == null) {
                add(frameId, fragment, tag)
            } else {
                if (fragment.isAdded)
                    show(fragment)
                else
                    add(frameId, fragment, tag)
            }
        }
    }
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commitAllowingStateLoss()
}

fun Fragment.isActive(): Boolean = !(isRemoving || isDetached)