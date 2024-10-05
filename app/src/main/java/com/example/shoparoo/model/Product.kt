package com.example.shoparoo.model

import com.google.gson.annotations.SerializedName

data class Product(

    @field:SerializedName("products")
    val products: List<ProductsItem?>? = null
)

data class ProductsItem(

    @field:SerializedName("image")
    val image: Image? = null,

    @field:SerializedName("body_html")
    val bodyHtml: String? = null,

    @field:SerializedName("images")
    val images: List<ImagesItem?>? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("handle")
    val handle: String? = null,

    @field:SerializedName("variants")
    val variants: List<VariantsItem?>? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("tags")
    val tags: String? = null,

    @field:SerializedName("published_scope")
    val publishedScope: String? = null,

    @field:SerializedName("product_type")
    val productType: String? = null,

    @field:SerializedName("template_suffix")
    val templateSuffix: Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("vendor")
    val vendor: String? = null,

    @field:SerializedName("admin_graphql_api_id")
    val adminGraphqlApiId: String? = null,

    @field:SerializedName("options")
    val options: List<OptionsItem?>? = null,

    @field:SerializedName("id")
    val id: Long? = null,

    @field:SerializedName("published_at")
    val publishedAt: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class VariantsItem(

    @field:SerializedName("inventory_management")
    val inventoryManagement: String? = null,

    @field:SerializedName("requires_shipping")
    val requiresShipping: Boolean? = null,

    @field:SerializedName("old_inventory_quantity")
    val oldInventoryQuantity: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("inventory_item_id")
    val inventoryItemId: Long? = null,

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("product_id")
    val productId: Long? = null,

    @field:SerializedName("option3")
    val option3: Any? = null,

    @field:SerializedName("option1")
    val option1: String? = null,

    @field:SerializedName("id")
    val id: Long? = null,

    @field:SerializedName("option2")
    val option2: String? = null,

    @field:SerializedName("grams")
    val grams: Int? = null,

    @field:SerializedName("sku")
    val sku: String? = null,

    @field:SerializedName("barcode")
    val barcode: Any? = null,

    @field:SerializedName("inventory_quantity")
    val inventoryQuantity: Int? = null,

    @field:SerializedName("compare_at_price")
    val compareAtPrice: Any? = null,

    @field:SerializedName("taxable")
    val taxable: Boolean? = null,

    @field:SerializedName("fulfillment_service")
    val fulfillmentService: String? = null,

    @field:SerializedName("weight")
    val weight: Any? = null,

    @field:SerializedName("inventory_policy")
    val inventoryPolicy: String? = null,

    @field:SerializedName("weight_unit")
    val weightUnit: String? = null,

    @field:SerializedName("admin_graphql_api_id")
    val adminGraphqlApiId: String? = null,

    @field:SerializedName("position")
    val position: Int? = null,

    @field:SerializedName("image_id")
    val imageId: Any? = null
)

data class ImagesItem(

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("src")
    val src: String? = null,

    @field:SerializedName("product_id")
    val productId: Long? = null,

    @field:SerializedName("admin_graphql_api_id")
    val adminGraphqlApiId: String? = null,

    @field:SerializedName("alt")
    val alt: Any? = null,

    @field:SerializedName("width")
    val width: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("variant_ids")
    val variantIds: List<Any?>? = null,

    @field:SerializedName("id")
    val id: Long? = null,

    @field:SerializedName("position")
    val position: Int? = null,

    @field:SerializedName("height")
    val height: Int? = null
)

data class OptionsItem(

    @field:SerializedName("product_id")
    val productId: Long? = null,

    @field:SerializedName("values")
    val values: List<String?>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Long? = null,

    @field:SerializedName("position")
    val position: Int? = null
)
