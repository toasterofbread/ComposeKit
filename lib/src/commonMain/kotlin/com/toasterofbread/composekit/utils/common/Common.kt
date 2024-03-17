@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.toasterofbread.composekit.utils.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.isSyncResourceLoadingSupported
import org.jetbrains.compose.resources.orEmpty
import org.jetbrains.compose.resources.readBytesSync
import org.jetbrains.compose.resources.rememberImageBitmap
import org.jetbrains.compose.resources.resource
import org.jetbrains.compose.resources.toImageBitmap
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

fun Boolean.toInt() = if (this) 1 else 0
fun Boolean.toFloat() = if (this) 1f else 0f

fun getInnerSquareSizeOfCircle(radius: Float, corner_percent: Int): Float {
	val C = 1.0 - (corner_percent * 0.02)
	val E = (sqrt(8.0 * radius * radius) / 2.0) - radius
	val I = radius + (E * C)
	return sqrt(I * I * 0.5).toFloat()
}

inline fun lazyAssert(noinline getMessage: (() -> String)? = null, condition: () -> Boolean) {
	if (_Assertions.ENABLED && !condition()) {
		throw AssertionError(getMessage?.invoke() ?: "Assertion failed")
	}
}

@Composable
fun PaddingValues.copy(
	start: Dp? = null,
	top: Dp? = null,
	end: Dp? = null,
	bottom: Dp? = null,
): PaddingValues {
	return PaddingValues(
		start ?: calculateStartPadding(LocalLayoutDirection.current),
		top ?: calculateTopPadding(),
		end ?: calculateEndPadding(LocalLayoutDirection.current),
		bottom ?: calculateBottomPadding()
	)
}

fun PaddingValues.copy(
	layout_direction: LayoutDirection,
	start: Dp? = null,
	top: Dp? = null,
	end: Dp? = null,
	bottom: Dp? = null,
): PaddingValues {
	return PaddingValues(
		start ?: calculateStartPadding(layout_direction),
		top ?: calculateTopPadding(),
		end ?: calculateEndPadding(layout_direction),
		bottom ?: calculateBottomPadding()
	)
}

fun Modifier.thenIf(condition: Boolean, modifier: Modifier): Modifier = if (condition) then(modifier) else this
inline fun Modifier.thenIf(condition: Boolean, action: Modifier.() -> Modifier): Modifier = if (condition) action() else this
inline fun Modifier.thenIf(condition: Boolean, elseAction: Modifier.() -> Modifier, action: Modifier.() -> Modifier): Modifier = if (condition) action() else elseAction()
inline fun <T> T.thenIf(condition: Boolean, action: T.() -> T): T = if (condition) action() else this

inline fun <T: Any> Modifier.thenWith(value: T?, action: Modifier.(T) -> Modifier): Modifier = if (value != null) action(value) else this

fun <T> MutableList<T>.addUnique(item: T): Boolean {
	if (!contains(item)) {
		add(item)
		return true
	}
	return false
}

fun <T> MutableList<T>.toggleItemPresence(item: T) {
	val index: Int = indexOf(item)
	if (index == -1) {
		add(item)
	}
	else {
		removeAt(index)
	}
}

@OptIn(ExperimentalMaterialApi::class)
fun <T> SwipeableState<T>.init(anchors: Map<Float, T>) {
	ensureInit(anchors)
}

operator fun IntSize.times(other: Float): IntSize =
	IntSize(width = (width * other).toInt(), height = (height * other).toInt())

fun formatElapsedTime(seconds: Long): String {
	val hours = TimeUnit.SECONDS.toHours(seconds)
	val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
	val remaining_seconds = seconds % 60
	if (hours > 0) {
		return String.format("%d:%02d:%02d", hours, minutes, remaining_seconds)
	}
	else {
		return String.format("%02d:%02d", minutes, remaining_seconds)
	}
}

inline fun <K, V : Any> MutableMap<K, V>.putIfAbsent(key: K, getValue: () -> V): V {
	var v = this[key]
	if (v == null) {
		v = getValue()
		put(key, v)
	}
	return v
}

fun String.indexOfOrNull(string: String, start_index: Int = 0, ignore_case: Boolean = false): Int? =
	indexOf(string, start_index, ignore_case).takeIf { it != -1 }

fun String.indexOfOrNull(char: Char, start_index: Int = 0, ignore_case: Boolean = false): Int? =
	indexOf(char, start_index, ignore_case).takeIf { it != -1 }

fun String.indexOfFirstOrNull(start: Int = 0, predicate: (Char) -> Boolean): Int? {
	for (i in start until length) {
		if (predicate(elementAt(i))) {
			return i
		}
	}
	return null
}

fun CoroutineScope.launchSingle(
	context: CoroutineContext = EmptyCoroutineContext,
	start: CoroutineStart = CoroutineStart.DEFAULT,
	block: suspend CoroutineScope.() -> Unit
): Job {
	synchronized(this) {
		coroutineContext.cancelChildren()
		return launch(context, start, block)
	}
}

fun Float.roundTo(decimals: Int): Float {
	val multiplier = 10f.pow(decimals)
	return (this * multiplier).roundToInt() / multiplier
}

// Kotlin doesn't like certain syntax when used within a lambda that returns a value
inline fun synchronizedBlock(lock: Any, block: () -> Unit) {
	synchronized(lock, block)
}

fun String.substringBetween(start: String, end: String, ignore_case: Boolean = false): String? {
	val start_index = indexOf(start, ignoreCase = ignore_case) + start.length
	if (start_index < start.length) {
		return null
	}
	val end_index = indexOf(end, start_index, ignoreCase = ignore_case)
	if (end_index == -1) {
		return null
	}

	return substring(start_index, end_index)
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun bitmapResource(res: String): ImageBitmap =
	if (isSyncResourceLoadingSupported()) {
		remember(res) {
			resource(res).readBytesSync().toImageBitmap()
		}
	}
	else {
		resource(res).rememberImageBitmap().orEmpty()
	}

fun Throwable.anyCauseIs(cls: KClass<out Throwable>): Boolean {
	var checking: Throwable? = this
	while(checking != null) {
		if (cls.isInstance(checking)) {
			return true
		}
		checking = checking.cause
	}
	return false
}

operator fun <T> State<T>?.getValue(t: T?, property: KProperty<*>): T? {
	return this?.getValue(t, property)
}

suspend fun <T, V : AnimationVector> Animatable<T, V>.snapOrAnimateTo(
	targetValue: T,
	snap: Boolean,
	animationSpec: AnimationSpec<T> = defaultSpringSpec,
	initialVelocity: T = velocity,
	block: (Animatable<T, V>.() -> Unit)? = null
) {
	if (snap) {
		snapTo(targetValue)
	}
	else {
		animateTo(targetValue, animationSpec, initialVelocity, block)
	}
}

fun Modifier.blockGestures(): Modifier =
	pointerInput(Unit) {
		while (currentCoroutineContext().isActive) {
			awaitPointerEventScope {
				val event: PointerEvent = awaitPointerEvent(PointerEventPass.Initial)
				for (change in event.changes) {
					change.consume()
				}
			}
		}
	}

fun <T, K, V> Iterable<T>.associateNotNull(transform: (T) -> Pair<K, V>?): Map<K, V> =
	mapNotNull(transform).associate { it }
