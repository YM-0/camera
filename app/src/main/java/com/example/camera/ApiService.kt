import retrofit2.http.GET
import retrofit2.http.Path

data class ImagePath(val path: String)

interface ApiService {
    @GET("/download/{id}")
    suspend fun getImagePaths(@Path("id") id: String): List<ImagePath>
}
