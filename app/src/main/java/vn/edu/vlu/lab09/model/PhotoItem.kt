package vn.edu.vlu.lab09.model

/**
 * One photo shown in the gallery. Photos are served by Picsum.
 */
data class PhotoItem(
    val id: Int,
    val title: String,
    val author: String,
    val imageUrl: String
) {
    companion object {
        val samplePhotos: List<PhotoItem> = listOf(
            PhotoItem(10, "Forest Path", "Linh Tran", "https://picsum.photos/id/10/800/600"),
            PhotoItem(11, "Misty Lake", "Minh Pham", "https://picsum.photos/id/11/800/600"),
            PhotoItem(12, "Harbor Lights", "Duy Anh", "https://picsum.photos/id/12/800/600"),
            PhotoItem(13, "Coastal Drive", "Thu Ha", "https://picsum.photos/id/13/800/600"),
            PhotoItem(14, "Wooden Pier", "Quang Huy", "https://picsum.photos/id/14/800/600"),
            PhotoItem(15, "Waterfall", "Ngoc Mai", "https://picsum.photos/id/15/800/600"),
            PhotoItem(16, "Rocky Shore", "Bao Chau", "https://picsum.photos/id/16/800/600"),
            PhotoItem(17, "Mountain Road", "Hoang Nam", "https://picsum.photos/id/17/800/600"),
            PhotoItem(18, "Clear Sky", "Kim Ngan", "https://picsum.photos/id/18/800/600"),
            PhotoItem(19, "Golden Field", "Van Long", "https://picsum.photos/id/19/800/600"),
            PhotoItem(20, "Desk Corner", "Thanh Truc", "https://picsum.photos/id/20/800/600"),
            PhotoItem(22, "Riverside", "Gia Bao", "https://picsum.photos/id/22/800/600")
        )
    }
}
