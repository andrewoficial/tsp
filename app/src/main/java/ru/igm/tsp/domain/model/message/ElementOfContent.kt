package ru.igm.tsp.domain.model.message

import com.google.gson.annotations.SerializedName

data class ElementOfContent (
        val type: String? = null, // "sent", "delivered" и т.д.
        val url: String? = null,
        val caption: String? = null,
        val label: String? = null,
        val action: String? = null,
        val data: Map<String, kotlin.Any>? = null,
        val text: String? = null,
        val attachments: String? = null

)