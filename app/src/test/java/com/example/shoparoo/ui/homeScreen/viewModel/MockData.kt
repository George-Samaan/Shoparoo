package com.example.shoparoo.ui.homeScreen.viewModel

import com.example.shoparoo.model.AppliedDiscount
import com.example.shoparoo.model.DraftOrderDetails
import com.example.shoparoo.model.DraftOrderRequest
import com.example.shoparoo.model.DraftOrderResponse
import com.example.shoparoo.model.Image
import com.example.shoparoo.model.LineItem
import com.example.shoparoo.model.ProductsItem
import com.example.shoparoo.model.Property
import com.example.shoparoo.model.RulesItem
import com.example.shoparoo.model.ShippingAddress
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.model.SmartCollections
import com.example.shoparoo.model.SmartCollectionsItem
import com.example.shoparoo.model.VariantsItem

object MockData {
    val testProduct1 = ProductsItem(
        createdAt = "2024-01-01T00:00:00Z",
        handle = "test-product-1",
        variants = listOf(
            VariantsItem(
                price = "29.99",
                inventoryQuantity = 10,
                option1 = "Red",
                option2 = "Large"
            )
        ),
        title = "Test Product 1",
        tags = "men, casual",
        productType = "T-Shirt",
        id = 1L
    )

    val testProduct2 = ProductsItem(
        createdAt = "2024-02-01T00:00:00Z",
        handle = "test-product-2",
        variants = listOf(
            VariantsItem(
                price = "49.99",
                inventoryQuantity = 5,
                option1 = "Blue",
                option2 = "Medium"
            )
        ),
        title = "Test Product 2",
        tags = "women, sale",
        productType = "Dress",
        id = 2L
    )

    val testProduct3 = ProductsItem(
        createdAt = "2024-03-01T00:00:00Z",
        handle = "test-product-3",
        variants = listOf(
            VariantsItem(
                price = "19.99",
                inventoryQuantity = 20,
                option1 = "Green",
                option2 = "Small"
            )
        ),
        title = "Test Product 3",
        tags = "kids, new",
        productType = "Shoes",
        id = 3L
    )


    val testSmartCollectionProduct = SmartCollections(
        smartCollections = listOf(
            SmartCollectionsItem(
                image = Image(
                    src = "https://example.com/image1.jpg",
                    alt = "Sample Image 1",
                    width = 300,
                    createdAt = "2023-01-01T12:00:00",
                    height = 400
                ),
                bodyHtml = "<p>Sample Collection 1</p>",
                handle = "sample-collection-1",
                rules = listOf(
                    RulesItem(
                        condition = "title contains 'shirt'",
                        column = "title",
                        relation = "contains"
                    ),
                    RulesItem(
                        condition = "vendor equals 'brand-1'",
                        column = "vendor",
                        relation = "equals"
                    )
                ),
                title = "Sample Collection 1",
                publishedScope = "global",
                templateSuffix = null,
                updatedAt = "2023-01-02T12:00:00",
                disjunctive = true,
                adminGraphqlApiId = "gid://shopify/Collection/1234567890",
                id = 1234567890L,
                publishedAt = "2023-01-01T12:00:00",
                sortOrder = "best-selling"
            ),
            SmartCollectionsItem(
                image = Image(
                    src = "https://example.com/image2.jpg",
                    alt = "Sample Image 2",
                    width = 200,
                    createdAt = "2023-02-01T12:00:00",
                    height = 300
                ),
                bodyHtml = "<p>Sample Collection 2</p>",
                handle = "sample-collection-2",
                rules = listOf(
                    RulesItem(
                        condition = "tag equals 'sale'",
                        column = "tag",
                        relation = "equals"
                    )
                ),
                title = "Sample Collection 2",
                publishedScope = "web",
                templateSuffix = null,
                updatedAt = "2023-02-02T12:00:00",
                disjunctive = false,
                adminGraphqlApiId = "gid://shopify/Collection/9876543210",
                id = 9876543210L,
                publishedAt = "2023-02-01T12:00:00",
                sortOrder = "manual"
            )
        )

    )
}

object mockData3 {
    fun createMockDraftOrderResponse(): DraftOrderResponse {
        val lineItem1 = LineItem(
            id = 1L,
            product_id = 101L,
            title = "T-Shirt",
            price = "19.99",
            quantity = 2,
            variant_id = "v1",
            properties = listOf(
                Property(name = "Size", value = "M"),
                Property(name = "Color", value = "Blue")
            ),
            vendor = "Brand A"
        )

        val lineItem2 = LineItem(
            id = 2L,
            product_id = 102L,
            title = "Jeans",
            price = "49.99",
            quantity = 1,
            variant_id = "v2",
            properties = listOf(
                Property(name = "Size", value = "32"),
                Property(name = "Color", value = "Black")
            ),
            vendor = "Brand B"
        )

        val appliedDiscount = AppliedDiscount(
            value = 10.0,
            value_type = "percentage",
            amount = 5.0
        )

        val draftOrderDetails = DraftOrderDetails(
            id = 1L,
            line_items = mutableListOf(lineItem1, lineItem2),
            email = "customer@example.com",
            note = "Please deliver by next week.",
            tags = "Summer Sale",
            invoice_url = "http://example.com/invoice/1",
            current_total_price = "64.98",
            total_price = "59.98",
            subtotal_price = "64.98",
            total_tax = "0.00",
            applied_discount = appliedDiscount
        )

        return DraftOrderResponse(draft_orders = listOf(draftOrderDetails))
    }

    val mockProductItem = SingleProduct(
        product = ProductsItem(
            id = 123,
            title = "Sample Product",
            vendor = "Example Vendor",
            tags = "tag1, tag2",
            createdAt = "2024-10-12T08:00:00Z",
            status = "active"
        )
    )

    val mockDraftOrderRequest = DraftOrderRequest(
        draft_order = DraftOrderDetails(
            line_items = mutableListOf(
                LineItem(
                    id = 1L,
                    product_id = 101L,
                    title = "T-Shirt",
                    price = "19.99",
                    quantity = 2,
                    variant_id = "v1",
                    properties = listOf(
                        Property(name = "Size", value = "M"),
                        Property(name = "Color", value = "Blue")
                    ),
                    vendor = "Brand A"
                ),
                LineItem(
                    id = 2L,
                    product_id = 102L,
                    title = "Jeans",
                    price = "49.99",
                    quantity = 1,
                    variant_id = "v2",
                    properties = listOf(
                        Property(name = "Size", value = "32"),
                        Property(name = "Color", value = "Black")
                    ),
                    vendor = "Brand B"
                )
            ),
            email = ""
        )
    )


    var mockDraftOrderRequesttobeUpdated = DraftOrderRequest(
        draft_order = DraftOrderDetails(
            line_items = mutableListOf(
                LineItem(
                    id = 1L,
                    product_id = 101L,
                    title = "T-Shirt",
                    price = "19.99",
                    quantity = 2,
                    variant_id = "v1",
                    properties = listOf(
                        Property(name = "Size", value = "M"),
                        Property(name = "Color", value = "Blue")
                    ),
                    vendor = "Brand A"
                ),
                LineItem(
                    id = 2L,
                    product_id = 102L,
                    title = "Jeans",
                    price = "49.99",
                    quantity = 1,
                    variant_id = "v2",
                    properties = listOf(
                        Property(name = "Size", value = "32"),
                        Property(name = "Color", value = "Black")
                    ),
                    vendor = "Brand B"
                )
            ),
            email = ""
        )
    )

}
val mockDraftOrderResponse = DraftOrderResponse(
    draft_orders = listOf(
        DraftOrderDetails(
            id = 101,
            line_items = mutableListOf(
                LineItem(
                    id = 1,
                    product_id = 1001,
                    title = "Sneakers",
                    price = "50.00",
                    quantity = 2,
                    variant_id = "v001",
                    properties = listOf(
                        Property(name = "Size", value = "42"),
                        Property(name = "Color", value = "Red")
                    ),
                    vendor = "Adidas"
                ),
                LineItem(
                    id = 2,
                    product_id = 1002,
                    title = "T-Shirt",
                    price = "20.00",
                    quantity = 1,
                    variant_id = "v002",
                    properties = listOf(
                        Property(name = "Size", value = "M"),
                        Property(name = "Color", value = "Blue")
                    ),
                    vendor = "Nike"
                )
            ),
            email = "customer1@example.com",
            note = "Please gift wrap",
            tags = "holiday-sale",
            invoice_url = "https://shopify.com/invoice/101",
            current_total_price = "120.00",
            total_price = "120.00",
            subtotal_price = "110.00",
            total_tax = "10.00",
            shipping_address = ShippingAddress(
                address1 = "123 Main St",
                phone = "555-1234"
            )
        )
    )
)