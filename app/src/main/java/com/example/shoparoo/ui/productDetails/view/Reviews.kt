package com.example.shoparoo.ui.productDetails.view

import com.example.shoparoo.R
import kotlin.random.Random

data class Reviews(
    val id: Int,
    val name: String,
    val review: String,
    val rating: Float,
    val userImage: Int,
)

val reviews = listOf(

    Reviews(id = 7, name = "gembo", review = "I recently purchased this and I must say, it exceeded all my expectations! The quality is top-notch, and it performs incredibly well. I've been using it daily for a few weeks now, and I can already see a noticeable difference in my routine. The design is sleek and user-friendly, making it easy to integrate into my daily life. I also appreciate the attention to detail in the packaging. It's not just a product; it's an experience. I highly recommend it to anyone looking for quality and reliability.", rating = 5.0f, userImage = R.drawable.user3),

    Reviews(id = 8, name = "George", review = "This product is good but not without its flaws. While it performs well most of the time, I did encounter a few minor issues that could be addressed. For example, the instructions were a bit unclear, which led to some confusion during setup. However, once I figured it out, I was pleased with the performance. It offers great value for the price, but I think it could be even better with some tweaks. Overall, I would still recommend it to others, with the caveat that they might need some patience.", rating = 4.0f, userImage = R.drawable.user5),

    Reviews(id = 9, name = "Sara", review = "I bought this product after hearing a lot of good things about it, but unfortunately, it didn't live up to the hype for me. The quality felt a bit lacking, and I had some trouble getting it to work as described. While I appreciate the effort put into the design, it just didn't meet my expectations. That said, it wasn't all bad; I did find some aspects enjoyable. I would suggest potential buyers do some thorough research before making a purchase, as it might not be the best fit for everyone.", rating = 3.0f, userImage = R.drawable.user6),

    Reviews(id = 10, name = "Galal", review = "I had high hopes for this product, but I was ultimately disappointed. The quality was not what I expected based on the description and reviews. After a few uses, it started showing signs of wear and tear, which is frustrating. I tried to reach out to customer service for support, but the response was slower than I would have liked. I believe that improvements can be made to enhance the product's durability and the overall customer experience. I can't recommend it based on my experience.", rating = 2.0f, userImage = R.drawable.user7),

    Reviews(id = 11, name = "Amy", review = "This is hands down one of the worst purchases I’ve ever made. I received the product in poor condition, and after a few uses, it completely broke. The customer service was unhelpful, and I felt completely let down by the whole experience. I wish I had done more research before buying. I can’t stress enough how much I regret this purchase. Please, save your money and look for better options. This product is simply not worth it.", rating = 1.0f, userImage = R.drawable.user9),



    Reviews(id = 6, name = "Alice", review = "Amazing product! Highly recommend.", rating = 5.0f, userImage = R.drawable.user6),
    Reviews(id = 6, name = "Jennifer", review = "Pretty good, but could improve in some areas.", rating = 4.0f, userImage = R.drawable.user6),
    Reviews(id = 6, name = "Linda", review = "Not what I expected, but okay.", rating = 3.0f, userImage = R.drawable.user6),
    Reviews(id = 6, name = "Emma", review = "Disappointed with the quality.", rating = 2.0f, userImage = R.drawable.user6),
    Reviews(id = 6, name = "Olivia", review = "Terrible experience, wouldn't buy again.", rating = 1.0f, userImage = R.drawable.user6),

    Reviews(id = 8, name = "Charlie", review = "Loved it! Will buy again.", rating = 5.0f, userImage = R.drawable.user8),
    Reviews(id = 8, name = "Sophia", review = "Good, but packaging was damaged.", rating = 4.0f, userImage = R.drawable.user8),
    Reviews(id = 8, name = "Ella", review = "Satisfactory but has room for improvement.", rating = 3.0f, userImage = R.drawable.user8),
    Reviews(id = 8, name = "Chloe", review = "Didn't meet my expectations.", rating = 2.0f, userImage = R.drawable.user8),
    Reviews(id = 8, name = "Grace", review = "Worst product I’ve ever bought.", rating = 1.0f, userImage = R.drawable.user8),

    Reviews(id = 1, name = "Dana", review = "Fantastic quality, worth every penny!", rating = 5.0f, userImage = R.drawable.user1),
    Reviews(id = 1, name = "Rachel", review = "Good product overall, but slightly overpriced.", rating = 4.0f, userImage = R.drawable.user1),
    Reviews(id = 1, name = "Megan", review = "It's alright, but I probably won’t buy it again.", rating = 3.0f, userImage = R.drawable.user1),
    Reviews(id = 1, name = "Nora", review = "Quality is below average.", rating = 2.0f, userImage = R.drawable.user1),
    Reviews(id = 1, name = "Zoe", review = "Total waste of money.", rating = 1.0f, userImage = R.drawable.user1),

    Reviews(id = 2, name = "Bob", review = "Excellent value for the price!", rating = 5.0f, userImage = R.drawable.user2),
    Reviews(id = 2, name = "Mark", review = "Good, but had some minor issues.", rating = 4.0f, userImage = R.drawable.user2),
    Reviews(id = 2, name = "David", review = "Average product, nothing special.", rating = 3.0f, userImage = R.drawable.user2),
    Reviews(id = 2, name = "James", review = "Expected better for the price.", rating = 2.0f, userImage = R.drawable.user2),
    Reviews(id = 2, name = "Kevin", review = "Wouldn’t recommend it to others.", rating = 1.0f, userImage = R.drawable.user2),

    Reviews(id = 3, name = "Frank", review = "Exceeded my expectations, highly recommend.", rating = 5.0f, userImage = R.drawable.user3),
    Reviews(id = 3, name = "Tom", review = "Solid product, but could be cheaper.", rating = 4.0f, userImage = R.drawable.user3),
    Reviews(id = 3, name = "Chris", review = "Mediocre product, nothing to write home about.", rating = 3.0f, userImage = R.drawable.user3),
    Reviews(id = 3, name = "Harry", review = "Very underwhelming.", rating = 2.0f, userImage = R.drawable.user3),
    Reviews(id = 3, name = "Sam", review = "Regret buying it, would not recommend.", rating = 1.0f, userImage = R.drawable.user3),

    Reviews(id = 9, name = "Dana", review = "Fantastic quality, worth every penny!", rating = 5.0f, userImage = R.drawable.user9),
    Reviews(id = 9, name = "Rachel", review = "Good product overall, but slightly overpriced.", rating = 4.0f, userImage = R.drawable.user9),
    Reviews(id = 9, name = "Megan", review = "It's alright, but I probably won’t buy it again.", rating = 3.0f, userImage = R.drawable.user9),
    Reviews(id = 9, name = "Nora", review = "Quality is below average.", rating = 2.0f, userImage = R.drawable.user9),
    Reviews(id = 9, name = "Zoe", review = "Total waste of money.", rating = 1.0f, userImage = R.drawable.user9),

    Reviews(id = 10, name = "Eve", review = "Absolutely love it! A must-have.", rating = 5.0f, userImage = R.drawable.user10),
    Reviews(id = 10, name = "Alice", review = "Good quality but had minor flaws.", rating = 4.0f, userImage = R.drawable.user10),
    Reviews(id = 10, name = "Lily", review = "Decent, but not worth the price.", rating = 3.0f, userImage = R.drawable.user10),
    Reviews(id = 10, name = "Sophie", review = "Subpar quality, wouldn’t buy again.", rating = 2.0f, userImage = R.drawable.user10),
    Reviews(id = 10, name = "Ruby", review = "Horrible experience, very disappointed.", rating = 1.0f, userImage = R.drawable.user10),

    Reviews(id = 5, name = "Ahmed Mazen", review = "I was incredibly disappointed with this product. From the moment I opened it, I could tell it was poorly made and didn't meet my expectations. I wouldn't recommend it to anyone. Save your money and look for something else!", rating = 1.0f, userImage = R.drawable.user11),

    Reviews(id = 5, name = "Heba Ismail", review = "This product has truly changed my life! The quality is outstanding, and it exceeded all my expectations. I've been using it daily, and it has become an essential part of my routine. I couldn't be happier, and I highly recommend it to anyone looking for something reliable and effective!", rating = 5.0f, userImage = R.drawable.user13),

    Reviews(id = 5, name = "Mohamed Galal", review = "This product is pretty decent. While it didn't blow me away, it gets the job done without any major flaws. I think it's a solid choice if you're looking for something that's neither extraordinary nor terrible—just a straightforward option for everyday use.", rating = 3.0f, userImage = R.drawable.user12),

    Reviews(id = 5, name = "GG", review = "Haha, kinda fun, but don’t expect much!", rating = 2.5f, userImage = R.drawable.user3),

    Reviews(id = 5, name = "Yasmin", review = "This product is neither here nor there. It does its job, but nothing more. I expected a bit more flair or functionality, but it’s just an average product that doesn’t particularly stand out. It’s okay if you need something basic, but don’t expect to be wowed.", rating = 3.0f, userImage = R.drawable.user13)



)



fun getRandomReviews(count: Int): Pair<List<Reviews>,Double> {
    val reviews = reviews.shuffled(Random).take(count)
    var avg = reviews.sumOf  { it.rating.toDouble() } /count
    avg = String.format("%.1f", avg).toDouble()
    return reviews.shuffled(Random).take(count) to avg
}