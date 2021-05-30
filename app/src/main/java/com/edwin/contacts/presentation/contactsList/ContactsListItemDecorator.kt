package com.edwin.contacts.presentation.contactsList

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.TypedValue
import android.view.View
import androidx.core.graphics.withTranslation
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.edwin.contacts.R


class ContactsListItemDecorator(context: Context) : RecyclerView.ItemDecoration() {

    companion object {
        private const val NO_POSITION = -1
    }

    private val drawingSpace: Int = context.resources.getDimensionPixelSize(R.dimen.margin_start)
    private val stickyLetterPadding: Float =
        context.resources.getDimensionPixelOffset(R.dimen.padding_offset).toFloat()

    private val textPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        alpha = 255
        typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL)
        textSize = context.resources.getDimensionPixelSize(R.dimen.letter_size).toFloat()
        color = TypedValue().apply {
            context.theme.resolveAttribute(R.attr.colorOnSecondary, this, true)
        }.data
        textAlign = Paint.Align.CENTER
    }

    private var positionToLayoutMap: Map<Int, StaticLayout> = emptyMap()

    private fun buildMapWithIndexes(words: List<String>) = words
        .mapIndexed { index, string -> index to string[0].toUpperCase().toString() }
        .distinctBy { it.second }
        .toMap()

    private fun buildPositionToLayoutMap(words: List<String>): Map<Int, StaticLayout> {
        val map = mutableMapOf<Int, StaticLayout>()
        buildMapWithIndexes(words).forEach {
            map[it.key] = buildStaticLayout(it.value)
        }
        return map
    }

    fun submitWords(words: List<String>) {
        positionToLayoutMap = buildPositionToLayoutMap(words)
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        var lastFoundPosition = NO_POSITION
        var previousHeaderTop = Float.MAX_VALUE

        for (viewNo in (parent.size - 1) downTo 0) {
            val view = parent.getChildAt(viewNo)
            if (childOutsideParent(view, parent)) continue

            val childPosition: Int = parent.getChildAdapterPosition(view)

            positionToLayoutMap[childPosition]?.let { initialLayout ->
                val top = (view.top + view.translationY + stickyLetterPadding)
                    .coerceAtMost(previousHeaderTop - initialLayout.height)
                    .coerceAtLeast(stickyLetterPadding)

                canvas.withTranslation(
                    y = top,
                    x = drawingSpace / 8f
                ) { initialLayout.draw(canvas) }

                lastFoundPosition = childPosition
                previousHeaderTop = top - stickyLetterPadding
            }

        }

        if (lastFoundPosition == NO_POSITION) {
            lastFoundPosition = parent.getChildAdapterPosition(parent.getChildAt(0)) + 1
        }

        for (initialsPosition in positionToLayoutMap.keys.reversed()) {
            if (initialsPosition < lastFoundPosition) {
                positionToLayoutMap[initialsPosition]?.let {
                    val top = (previousHeaderTop - it.height)
                        .coerceAtMost(stickyLetterPadding)
                    canvas.withTranslation(y = top, x = drawingSpace / 8f) {
                        it.draw(canvas)
                    }
                }
                break
            }
        }
    }


    private fun childOutsideParent(childView: View, parent: RecyclerView): Boolean {
        return childView.bottom < 0
                || (childView.top + childView.translationY.toInt() > parent.height)

    }

    private fun buildStaticLayout(text: String): StaticLayout {
        return if (Build.VERSION.SDK_INT >= 28) {
            StaticLayout.Builder.obtain(text, 0, 1, textPaint, drawingSpace).apply {
                setAlignment(Layout.Alignment.ALIGN_CENTER)
                setLineSpacing(0f, 1f)
                setIncludePad(false)
            }.build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                text,
                textPaint,
                drawingSpace,
                Layout.Alignment.ALIGN_CENTER,
                1f,
                0f,
                false
            )
        }
    }
}