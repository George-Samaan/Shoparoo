package com.example.shoparoo.model

data class OrderResponse(
    val orders: List<Order>
)


data class Order(
    val billing_address: Address?,
    val browser_ip: String?,
    val buyer_accepts_marketing: Boolean,
    val cancel_reason: String?,
    val cancelled_at: String?,
    val cart_token: String?,
    val checkout_token: String?,
    val client_details: ClientDetails?,
    val closed_at: String?,
    val company: Company?,
    val confirmation_number: String?,
    val confirmed: Boolean,
    val created_at: String?,
    val currency: String?,
    val current_total_additional_fees_set: MoneySet?,
    val current_total_discounts: String?,
    val current_total_discounts_set: MoneySet?,
    val current_total_duties_set: MoneySet?,
    val current_total_price: String?,
    val current_total_price_set: MoneySet?,
    val current_subtotal_price: String?,
    val current_subtotal_price_set: MoneySet?,
    val current_total_tax: String?,
    val current_total_tax_set: MoneySet?,
    val customer: Customer?,
    val customer_locale: String?,
    val discount_applications: List<DiscountApplication>?,
    val discount_codes: List<DiscountCode>?,
    val email: String?,
    val estimated_taxes: Boolean,
    val financial_status: String?,
    val fulfillments: List<Fulfillment>?,
    val fulfillment_status: String?,
    val gateway: String?,
    val id: Long,
    val landing_site: String?,
    val line_items: List<LineItem>?,
    val location_id: Long?,
    val merchant_of_record_app_id: Int?,
    val name: String?,
    val note: String?,
    val note_attributes: List<NoteAttribute>?,
    val number: Int,
    val order_number: Int,
    val original_total_additional_fees_set: MoneySet?,
    val original_total_duties_set: MoneySet?
)

data class Address(
    val address1: String?,
    val address2: String?,
    val city: String?,
    val company: String?,
    val country: String?,
    val first_name: String?,
    val last_name: String?,
    val phone: String?,
    val province: String?,
    val zip: String?,
    val name: String?,
    val province_code: String?,
    val country_code: String?,
    val latitude: String?,
    val longitude: String?
)

data class ClientDetails(
    val accept_language: String?,
    val browser_height: Int?,
    val browser_ip: String?,
    val browser_width: Int?,
    val session_hash: String?,
    val user_agent: String?
)

data class Company(
    val id: Int,
    val location_id: Int
)

data class MoneySet(
    val shop_money: Money?,
    val presentment_money: Money?
)

data class Money(
    val amount: String?,
    val currency_code: String?
)

data class Customer(
    val id: Long?,
    val email: String?,
    val accepts_marketing: Boolean?,
    val created_at: String?,
    val updated_at: String?,
    val first_name: String?,
    val last_name: String?,
    val state: String?,
    val note: String?,
    val verified_email: Boolean?,
    val multipass_identifier: String?,
    val tax_exempt: Boolean?,
    val tax_exemptions: Any?,
    val phone: String?,
    val tags: String?,
    val currency: String?,
    val addresses: Any?,
    val admin_graphql_api_id: String?,
    val default_address: Any?
)

data class DiscountApplication(
    val type: String?,
    val title: String?,
    val description: String?,
    val value: String?,
    val value_type: String?,
    val allocation_method: String?,
    val target_selection: String?,
    val target_type: String?
)

data class DiscountCode(
    val code: String?,
    val amount: String?,
    val type: String?
)

data class Fulfillment(
    val created_at: String?,
    val id: Long?,
    val order_id: Long?,
    val status: String?,
    val tracking_company: String?,
    val tracking_number: String?,
    val updated_at: String?
)

data class LineItem(
    val attributed_staffs: List<AttributedStaff>?,
    val fulfillable_quantity: Int?,
    val fulfillment_service: String?,
    val fulfillment_status: String?,
    val grams: Int?,
    val id: Long?,
    val price: String?,
    val product_id: Long?,
    val quantity: Int?,
    val current_quantity: Int?,
    val requires_shipping: Boolean?,
    val sku: String?,
    val title: String?,
    val variant_id: Long?,
    val variant_title: String?,
    val vendor: String?,
    val name: String?,
    val gift_card: Boolean?,
    val price_set: MoneySet?,
    val properties: List<Property>?,
    val taxable: Boolean?,
    val tax_lines: List<TaxLine>?,
    val total_discount: String?,
    val total_discount_set: MoneySet?,
    val discount_allocations: List<DiscountAllocation>?,
    val origin_location: OriginLocation?,
    val duties: List<Duty>?
)

data class AttributedStaff(
    val id: String?,
    val quantity: Int?
)

data class Property(
    val name: String?,
    val value: String?
)

data class TaxLine(
    val title: String?,
    val price: String?,
    val price_set: MoneySet?,
    val channel_liable: Boolean?,
    val rate: Double?
)

data class DiscountAllocation(
    val amount: String?,
    val discount_application_index: Int?,
    val amount_set: MoneySet?
)

data class OriginLocation(
    val id: Long?,
    val country_code: String?,
    val province_code: String?,
    val name: String?,
    val address1: String?,
    val address2: String?,
    val city: String?,
    val zip: String?
)

data class Duty(
    val id: String?,
    val harmonized_system_code: String?,
    val country_code_of_origin: String?,
    val shop_money: Money?,
    val presentment_money: Money?,
    val tax_lines: List<TaxLine>?,
    val admin_graphql_api_id: String?
)

data class NoteAttribute(
    val name: String?,
    val value: String?
)