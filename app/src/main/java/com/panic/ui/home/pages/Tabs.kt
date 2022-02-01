package com.panic.ui.home.pages

import androidx.annotation.StringRes
import com.panic.R

enum class Tabs(@StringRes val tabTitle: Int) {
    Responders(R.string.tab_text_1),
    Triggers(R.string.tab_text_2)
}