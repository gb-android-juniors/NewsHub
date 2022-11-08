package com.robivan.newsgb._core.ui.model

data class AdBanner(val id: Int, val bannerId: String = "R-M-2006130-${id + 2}") : NewsListItem
