package com.example.shoparoo.ui.homeScreen.viewModel

import com.example.shoparoo.model.Image
import com.example.shoparoo.model.ProductsItem
import com.example.shoparoo.model.RulesItem
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