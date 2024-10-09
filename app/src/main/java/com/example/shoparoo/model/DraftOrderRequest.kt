package com.example.shoparoo.model

data class LineItem(
    val id :Long?=null,
    val product_id: Long? = null,  // Shopify draft order ID
    val title: String,              // The product name
    val price: String,              // The price of the product
    var quantity: Int,              // The quantity of the product
    val variant_id: String,           // Shopify variant ID (for color/size variants)
    val properties:List<Property>, // Additional properties like size, color
    val vendor:String?=null
)
data class Property(
    val name: String,
    val value: String
)

data class DraftOrderResponse(
    val draft_orders: List<DraftOrderDetails>
)

data class DraftOrderRequest(
    val draft_order: DraftOrderDetails
)

data class AppliedDiscount(
    val value: Double,
    val value_type: String,     //percentage
    val amount: Double?
)

data class DraftOrderDetails(
    val id: Long?=null,
    var line_items: MutableList<LineItem>,  // Items in the draft order
    val email: String? = null,          // Customer details
    val note: String? = null,       // Optional: Notes for the order
    val tags: String? = null, // Optional: Tags to categorize the order
    val invoice_url: String? = null,
    val current_total_price: String? = null,
    val total_price: String? = null,
    val subtotal_price: String? = null,
    val total_tax: String? = null,
    val applied_discount: AppliedDiscount? = null  // Discount applied to the order
)

