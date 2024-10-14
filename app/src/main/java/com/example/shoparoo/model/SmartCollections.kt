package com.example.shoparoo.model

import com.google.gson.annotations.SerializedName

data class SmartCollections(

    @field:SerializedName("smart_collections")
    val smartCollections: List<SmartCollectionsItem?>? = null
)

data class Image(

    @field:SerializedName("src")
    val src: String? = null,

    @field:SerializedName("alt")
    val alt: Any? = null,

    @field:SerializedName("width")
    val width: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("height")
    val height: Int? = null
)

data class RulesItem(

    @field:SerializedName("condition")
    val condition: String? = null,

    @field:SerializedName("column")
    val column: String? = null,

    @field:SerializedName("relation")
    val relation: String? = null
)

data class SmartCollectionsItem(

    @field:SerializedName("image")
    val image: Image? = null,

    @field:SerializedName("body_html")
    val bodyHtml: String? = null,

    @field:SerializedName("handle")
    val handle: String? = null,

    @field:SerializedName("rules")
    val rules: List<RulesItem?>? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("published_scope")
    val publishedScope: String? = null,

    @field:SerializedName("template_suffix")
    val templateSuffix: Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("disjunctive")
    val disjunctive: Boolean? = null,

    @field:SerializedName("admin_graphql_api_id")
    val adminGraphqlApiId: String? = null,

    @field:SerializedName("id")
    val id: Long? = null,

    @field:SerializedName("published_at")
    val publishedAt: String? = null,

    @field:SerializedName("sort_order")
    val sortOrder: String? = null
)
