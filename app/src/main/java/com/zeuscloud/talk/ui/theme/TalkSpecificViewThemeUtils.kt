/*
 * Nextcloud Talk application
 *
 * @author Álvaro Brey
 * Copyright (C) 2022 Álvaro Brey
 * Copyright (C) 2022 Nextcloud GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.zeuscloud.talk.ui.theme

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.text.Spannable
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase
import com.nextcloud.android.common.ui.theme.utils.AndroidXViewThemeUtils
import com.zeuscloud.talk.R
import com.zeuscloud.talk.databinding.ReactionsInsideMessageBinding
import com.zeuscloud.talk.ui.MicInputCloud
import com.zeuscloud.talk.ui.StatusDrawable
import com.zeuscloud.talk.ui.WaveformSeekBar
import com.zeuscloud.talk.utils.DisplayUtils
import com.zeuscloud.talk.utils.DrawableUtils
import com.zeuscloud.talk.utils.message.MessageUtils
import com.vanniktech.emoji.EmojiTextView
import com.wooplr.spotlight.SpotlightView
import eu.davidea.flexibleadapter.utils.FlexibleUtils
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * View theme utils specific for the Talk app.
 *
 */
@Suppress("TooManyFunctions")
class TalkSpecificViewThemeUtils @Inject constructor(
    schemes: MaterialSchemes,
    private val appcompat: AndroidXViewThemeUtils
) :
    ViewThemeUtilsBase(schemes) {
    fun themeIncomingMessageBubble(bubble: View, grouped: Boolean, deleted: Boolean) {
        val resources = bubble.resources

        var bubbleResource = R.drawable.shape_incoming_message

        if (grouped) {
            bubbleResource = R.drawable.shape_grouped_incoming_message
        }

        val bgBubbleColor = if (deleted) {
            resources.getColor(R.color.bg_message_list_incoming_bubble_deleted, null)
        } else {
            resources.getColor(R.color.bg_message_list_incoming_bubble, null)
        }
        val bubbleDrawable = DisplayUtils.getMessageSelector(
            bgBubbleColor,
            resources.getColor(R.color.transparent, null),
            bgBubbleColor,
            bubbleResource
        )
        ViewCompat.setBackground(bubble, bubbleDrawable)
    }

    fun themeOutgoingMessageBubble(bubble: View, grouped: Boolean, deleted: Boolean) {
        withScheme(bubble) { scheme ->
            val bgBubbleColor = if (deleted) {
                ColorUtils.setAlphaComponent(scheme.surfaceVariant, HALF_ALPHA_INT)
            } else {
                scheme.surfaceVariant
            }

            val layout = if (grouped) {
                R.drawable.shape_grouped_outcoming_message
            } else {
                R.drawable.shape_outcoming_message
            }
            val bubbleDrawable = DisplayUtils.getMessageSelector(
                bgBubbleColor,
                ResourcesCompat.getColor(bubble.resources, R.color.transparent, null),
                bgBubbleColor,
                layout
            )
            ViewCompat.setBackground(bubble, bubbleDrawable)
        }
    }

    fun colorOutgoingQuoteText(textView: TextView) {
        withScheme(textView) { scheme ->
            textView.setTextColor(scheme.onSurfaceVariant)
        }
    }

    fun colorOutgoingQuoteAuthorText(textView: TextView) {
        withScheme(textView) { scheme ->
            ColorUtils.setAlphaComponent(scheme.onSurfaceVariant, ALPHA_80_INT)
        }
    }

    fun colorOutgoingQuoteBackground(view: View) {
        withScheme(view) { scheme ->
            view.setBackgroundColor(scheme.onSurfaceVariant)
        }
    }

    fun colorContactChatItemName(contactName: androidx.emoji2.widget.EmojiTextView) {
        withScheme(contactName) { scheme ->
            contactName.setTextColor(scheme.onPrimaryContainer)
        }
    }

    fun colorContactChatItemBackground(card: MaterialCardView) {
        withScheme(card) { scheme ->
            card.setCardBackgroundColor(scheme.primaryContainer)
        }
    }

    fun colorSwitch(preference: MaterialSwitch) {
        val switch = preference as SwitchCompat
        appcompat.colorSwitchCompat(switch)
    }

    fun setCheckedBackground(emoji: EmojiTextView) {
        withScheme(emoji) { scheme ->
            val drawable = AppCompatResources
                .getDrawable(emoji.context, R.drawable.reaction_self_bottom_sheet_background)!!
                .mutate()
            DrawableCompat.setTintList(
                drawable,
                ColorStateList.valueOf(scheme.primary)
            )
            emoji.background = drawable
        }
    }

    fun setCheckedBackground(linearLayout: LinearLayout, incoming: Boolean) {
        withScheme(linearLayout) { scheme ->
            val drawable = AppCompatResources
                .getDrawable(linearLayout.context, R.drawable.reaction_self_background)!!
                .mutate()
            val backgroundColor = if (incoming) {
                scheme.primaryContainer
            } else {
                ContextCompat.getColor(
                    linearLayout.context,
                    R.color.bg_message_list_incoming_bubble
                )
            }
            DrawableCompat.setTintList(
                drawable,
                ColorStateList.valueOf(backgroundColor)
            )
            linearLayout.background = drawable
        }
    }

    fun getPlaceholderImage(context: Context, mimetype: String?): Drawable? {
        val drawableResourceId = DrawableUtils.getDrawableResourceIdForMimeType(mimetype)
        val drawable = AppCompatResources.getDrawable(
            context,
            drawableResourceId
        )
        if (drawable != null && THEMEABLE_PLACEHOLDER_IDS.contains(drawableResourceId)) {
            colorDrawable(context, drawable)
        }
        return drawable
    }

    private fun colorDrawable(context: Context, drawable: Drawable) {
        withScheme(context) { scheme ->
            drawable.setTint(scheme.primary)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun themePlaceholderAvatar(avatar: View, @DrawableRes foreground: Int): Drawable? {
        var drawable: LayerDrawable? = null
        withScheme(avatar) { scheme ->
            val layers = arrayOfNulls<Drawable>(2)
            layers[0] = ContextCompat.getDrawable(avatar.context, R.drawable.ic_avatar_background)
            layers[0]?.setTint(scheme.surfaceVariant)
            layers[1] = ContextCompat.getDrawable(avatar.context, foreground)
            layers[1]?.setTint(scheme.onSurfaceVariant)
            drawable = LayerDrawable(layers)
        }

        return drawable
    }

    fun themeSearchView(searchView: SearchView) {
        withScheme(searchView) { scheme ->
            // hacky as no default way is provided
            val editText = searchView.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
            val searchPlate = searchView.findViewById<LinearLayout>(R.id.search_plate)
            editText.setHintTextColor(scheme.onSurfaceVariant)
            editText.setTextColor(scheme.onSurface)
            editText.setBackgroundColor(scheme.surface)
            searchPlate.setBackgroundColor(scheme.surface)
        }
    }

    fun themeStatusCardView(cardView: MaterialCardView) {
        withScheme(cardView) { scheme ->
            val background = cardView.context.getColor(R.color.grey_200)
            cardView.backgroundTintList =
                ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_checked)
                    ),
                    intArrayOf(
                        scheme.secondaryContainer,
                        background
                    )
                )
            cardView.setStrokeColor(
                ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_checked)
                    ),
                    intArrayOf(
                        scheme.onSecondaryContainer,
                        scheme.surface
                    )
                )
            )
        }
    }

    fun themeMicInputCloud(micInputCloud: MicInputCloud) {
        withScheme(micInputCloud) { scheme ->
            micInputCloud.setColor(scheme.primary)
        }
    }

    fun themeWaveFormSeekBar(waveformSeekBar: WaveformSeekBar) {
        withScheme(waveformSeekBar) { scheme ->
            waveformSeekBar.thumb.colorFilter =
                PorterDuffColorFilter(scheme.inversePrimary, PorterDuff.Mode.SRC_IN)
            waveformSeekBar.setColors(scheme.inversePrimary, scheme.onPrimaryContainer)
            waveformSeekBar.progressDrawable?.colorFilter =
                PorterDuffColorFilter(scheme.primary, PorterDuff.Mode.SRC_IN)
        }
    }

    fun themeForegroundColorSpan(context: Context): ForegroundColorSpan {
        return withScheme(context) { scheme ->
            return@withScheme ForegroundColorSpan(scheme.primary)
        }
    }

    fun themeSpotlightView(context: Context, builder: SpotlightView.Builder): SpotlightView.Builder {
        return withScheme(context) { scheme ->
            return@withScheme builder.headingTvColor(scheme.primary).lineAndArcColor(scheme.primary)
        }
    }

    fun themeAndHighlightText(
        textView: TextView,
        originalText: String?,
        c: String?
    ) {
        withScheme(textView) { scheme ->
            var constraint = c
            constraint = FlexibleUtils.toLowerCase(constraint)
            var start = FlexibleUtils.toLowerCase(originalText).indexOf(constraint)
            if (start != -1) {
                val spanText = Spannable.Factory.getInstance().newSpannable(originalText)
                do {
                    val end = start + constraint.length
                    spanText.setSpan(
                        ForegroundColorSpan(scheme.primary),
                        start,
                        end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spanText.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    start = FlexibleUtils.toLowerCase(originalText)
                        .indexOf(constraint, end + 1) // +1 skips the consecutive span
                } while (start != -1)
                textView.setText(spanText, TextView.BufferType.SPANNABLE)
            } else {
                textView.setText(originalText, TextView.BufferType.NORMAL)
            }
        }
    }

    fun themeSortButton(sortButton: MaterialButton) {
        withScheme(sortButton) { scheme ->
            sortButton.iconTint = ColorStateList.valueOf(scheme.onSurface)
            sortButton.setTextColor(scheme.onSurface)
        }
    }

    fun themePathNavigationButton(navigationBtn: MaterialButton) {
        withScheme(navigationBtn) { scheme ->
            navigationBtn.iconTint = ColorStateList.valueOf(scheme.onSurface)
            navigationBtn.setTextColor(scheme.onSurface)
        }
    }

    fun themeSortListButtonGroup(relativeLayout: RelativeLayout) {
        withScheme(relativeLayout) { scheme ->
            relativeLayout.setBackgroundColor(scheme.surface)
        }
    }

    fun themeStatusDrawable(context: Context, statusDrawable: StatusDrawable) {
        withScheme(context) { scheme ->
            statusDrawable.colorStatusDrawable(scheme.surface)
        }
    }

    fun themeMessageCheckMark(imageView: ImageView) {
        withScheme(imageView) { scheme ->
            imageView.setColorFilter(
                scheme.onSurfaceVariant,
                PorterDuff.Mode.SRC_ATOP
            )
        }
    }

    fun themeMarkdown(context: Context, message: String, incoming: Boolean): Spanned {
        return withScheme(context) { scheme ->
            return@withScheme if (incoming) {
                MessageUtils(context).getRenderedMarkdownText(
                    context,
                    message,
                    context.getColor(R.color.nc_incoming_text_default)
                )
            } else {
                MessageUtils(context).getRenderedMarkdownText(context, message, scheme.onSurfaceVariant)
            }
        }
    }

    fun getTextColor(
        isOutgoingMessage: Boolean,
        isSelfReaction: Boolean,
        binding: ReactionsInsideMessageBinding
    ): Int {
        return withScheme(binding.root) { scheme ->
            return@withScheme if (!isOutgoingMessage || isSelfReaction) {
                ContextCompat.getColor(binding.root.context, R.color.high_emphasis_text)
            } else {
                scheme.onSurfaceVariant
            }
        }
    }

    companion object {
        private val THEMEABLE_PLACEHOLDER_IDS = listOf(
            R.drawable.ic_mimetype_package_x_generic,
            R.drawable.ic_mimetype_folder
        )

        private val ALPHA_80_INT: Int = (255 * 0.8).roundToInt()

        private const val HALF_ALPHA_INT: Int = 255 / 2
    }
}
