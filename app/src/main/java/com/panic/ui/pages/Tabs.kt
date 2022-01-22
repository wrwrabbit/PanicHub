package com.panic.ui.pages

import androidx.annotation.StringRes
import com.panic.R

enum class Tabs(@StringRes val tabTitle: Int) {
    Responders(R.string.tab_text_1),
    Buttons(R.string.tab_text_2)
}