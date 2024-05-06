package dam_48286.pokedex.model.network

import android.util.Log
import dam_48286.pokedex.model.network.responses.PokemonAPI
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal class HttpRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val request = originalRequest.newBuilder().url(originalRequest.url).build()
        Log.d("Pokemon-API-Request",request.toString())
        return chain.proceed(request)
    }
}

internal object NetworkModule
{
    private val _client = initPokemonRemoteService()
    val client: PokemonAPI
        get() = _client

    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpRequestInterceptor())
            .build()
    }

    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }


    fun providePokedexService(retrofit: Retrofit): PokemonAPI {
        return retrofit.create(PokemonAPI::class.java)
    }

    fun initPokemonRemoteService() : PokemonAPI
    {
        val okHttpClient = provideOkHttpClient()
        val retrofit = provideRetrofit(okHttpClient)
        val pokemonApi = providePokedexService(retrofit)
        return pokemonApi
    }
}